<?php
/* * * * *
 * This script transforms the input of the form into
 * a uniform format, as used by the model.
 * It converts a dataset name into an id and filters
 * the datasets with the wrong target attribute.
 * * * * */

if($this->subpage == 'task') {
  $ttid = $this->input->post( 'ttid' );
  $datatype = array( 'nominal' );
  $required_inputs = $this->Task_type_inout->getWhere( '`io` = "input" AND `ttid` = "'.$ttid.'"' );
  $inputs = array();
  // retrieve all inputs
  foreach( $required_inputs as $i ) {
    if($this->input->post($i->name) == false ) { continue; }
    $inputs[$i->name] = $this->input->post($i->name);
  }

  // special retrieve datasets and target feature
  $constraints = $this->Dataset->nameVersionConstraints( $this->input->post( 'source_data' ), 'd' );
  $target_feature = '`d`.`default_target_attribute`';
  if( trim( $this->input->post( 'target_feature' ) ) ) {
    $target_feature = trim( $this->input->post( 'target_feature' ) );
  } else {
    unset( $inputs['target_feature'] );
  }

  // TODO: remove mapping, add logic in DB instead of here.
  if( $ttid == 2 ) { // exception.
    $datatype = array( 'numeric' );
  }

  $sql =
    'SELECT `d`.`did` AS `source_data`, `f`.`name` AS `target_feature` ' .
    'FROM `dataset` `d`,`data_feature` `f` ' .
    'WHERE `d`.`did` = `f`.`did` ' .
    'AND `f`.`name` = "' . $target_feature . '" ' .
    'AND `f`.`NumberOfMissingValues` = 0 ' .
    'AND `f`.`data_type` IN ("' . implode( '","', $datatype ) . '") ' .
    'AND ' . $constraints . ' ';

  // TODO: remove these mappings in a good way.
  if( $ttid == 5 ) {
    // clustering. no target feature
    $sql = 'SELECT `d`.`did` AS `source_data` FROM `dataset` `d` WHERE ' . $constraints . ' ';
  }
  if( $ttid == 6 ) {
    // data mining challange. labeled data set and dataset with missing labels
    $constraints1 = $this->Dataset->nameVersionConstraints( $this->input->post( 'source_data' ), 'd1' );
    $constraints2 = $this->Dataset->nameVersionConstraints( $this->input->post( 'source_data_labeled' ), 'd2' );

    $sql =
      'SELECT `d1`.`did` AS source_data, `d2`.`did` AS `source_data_labeled`, `f1`.`name` AS `target_feature` '.
      'FROM `dataset` `d1`, `dataset` `d2`, `data_feature` `f1`, `data_feature` `f2` '.
      'WHERE `d1`.`did` = `f1`.`did` '.
      'AND `d2`.`did` = `f2`.`did` '.
      'AND `f1`.`name` = "' . $target_feature . '" ' .
      'AND `f2`.`name` = "' . $target_feature . '" ' .
      'AND `f2`.`NumberOfMissingValues` = 0 ' .
      'AND `f1`.`data_type` IN ("' . implode( '","', $datatype ) . '") ' .
      'AND `f2`.`data_type` IN ("' . implode( '","', $datatype ) . '") ' .
      'AND ' . $constraints1 . ' AND ' . $constraints2;
  }
  if( $ttid == 7 ) {
    $tfl = $this->input->post( 'target_feature_left' );
    $tfr = $this->input->post( 'target_feature_right' );
    $tfe = $this->input->post( 'target_feature_event' );
    $source_data = $this->input->post( 'source_data' );

    if( ($tfl == false && $tfr == false) || $tfe == false || $source_data == false ) {
      // illegal request, return nothing
      $sql = 'SELECT * FROM dataset WHERE 0;';
    } else {
      $sql =
        'SELECT `d`.`did` AS `source_data`, `fl`.`name` AS `target_feature_left`, ' .
          '`fr`.`name` AS `target_feature_right`, '.
          '`fe`.`name` AS `target_feature_event` ' .
        'FROM `data_feature` `fe`, `dataset` `d` ' .
        'LEFT JOIN `data_feature` `fl` ON `d`.`did` = `fl`.`did` AND `fl`.`name` = "' . $tfl . '" ' .
        'LEFT JOIN `data_feature` `fr` ON `d`.`did` = `fr`.`did` AND `fr`.`name` = "' . $tfr . '" ' .
        'WHERE `d`.`did` = `fe`.`did` AND `fe`.`name` = "' . $tfe . '" ' .
        'AND ' . $constraints . ' ';
    }
  }

  $datasets = $this->Dataset->query( $sql . 'ORDER BY `source_data`;' );

  // sanity check input
  $input_safe = true;
  // first sanitize custom testset
  if( $this->input->post('custom_testset') ) {
    if( is_cs_natural_numbers( $inputs['custom_testset'] ) ) {
      $inputs['custom_testset'] = implode( ',', range_string_to_array( $inputs['custom_testset'] ) );
    } else {
      unset( $inputs['custom_testset'] );
      $input_safe = false;
    }
  }

  // TODO: Check input $this->input->post('cost_matrix')

  $results = array(); // resulting task configurations
  $dids = array();    // used dataset ids
  $new_tasks = array();
  if( is_array( $datasets ) ) {
    foreach( $datasets as $dataset ) {
      $current = $inputs;
      foreach( get_object_vars($dataset) as $key => $value ) {
        $current[$key] = $value;
      }
      $results[] = $current;
      $dids[] = $dataset->source_data;
    }
    if( count( $datasets ) > 1 && ($this->input->post('custom_testset') || $this->input->post('cost_matrix') ) ) {
      // against the rules
    } elseif( $input_safe ) {
      $new_tasks = $this->Task->create_batch( $ttid, $results );
    }
  }
  $inputs['source_data'] = $dids;

  $tasks = $this->Task->tasks_crosstabulated( $ttid, true, $inputs );

  if( $tasks ) {
    foreach( $tasks as $task ) {
      $new = in_array( $task->task_id, $new_tasks ) ? '*' : '';
      $this->task_ids[] = '<a href="t/' . $task->task_id . '">' . $task->task_id . '</a>' . $new;
    }
  }

  if( $new_tasks ) { $this->new_text = '* new'; }

} elseif($this->subpage == 'data') {

  $session_hash = $this->ion_auth->user()->row()->session_hash;

  $description = $this->dataoverview->generate_xml(
    'data_set_description',
    $this->config->item('xml_fields_dataset')
  );

  $post_data = array(
      'description' => $description,
      'api_key' => $session_hash
  );
  if( $_FILES['dataset']['error'] == 0 ) {
      $post_data['dataset'] = new CurlFile($_FILES['dataset']['tmp_name'], 'text/xml', $_FILES['dataset']['name']);
  }

  $url = BASE_URL.'api/v1/data';
  // Send the request & save response to $resp
  $api_response = $this->curlhandler->post_multipart_helper( $url, $post_data );
  if($api_response !== false) {
    $xml = simplexml_load_string( $api_response );
    if(!$xml){
        print "Something went wrong. Server says:<br>";
        print $api_response;
    }
    $this->responsetype = 'alert alert-success';
    $this->responsecode = -1;
    $this->response = 'Data was uploaded with id: ';
    if( property_exists( $xml->children('oml', true), 'code' ) ) {
      $this->responsetype = 'alert alert-danger';
      $this->responsecode = $xml->children('oml', true)->code;
      $this->response = 'Error '.$this->responsecode.': '.$xml->children('oml', true)->message.' - '.$xml->children('oml', true)->additional_information;
      if($this->responsecode=='131') $this->response .= ' Please fill in all required (red) fields, upload a file or give a URL (not both), and avoid spaces in the dataset name.';
    } else if($xml->children('oml', true)->id){
      $this->response = '<h2><i class="fa fa-thumbs-o-up"></i> Thanks!</h2>Data was uploaded successfully (ID = '.$xml->children('oml', true)->id .')<br> You can now <b><a href="d/'. $xml->children('oml', true)->id . '"> follow your dataset on OpenML</a></b>, complete its description, track its impact, create OpenML tasks, and see all ensuing results.<br><br>You can also continue to add datasets below.';
      sm($this->response);
      su('new/data');
    } else {
      print "Something went wrong. Server says:<br>";
      print $api_response;
    }
  } else{
    $this->responsetype = 'alert alert-danger';
    $this->response = 'Could not upload data. Please fill in all required (red) fields.';
  }
 } elseif($this->subpage == 'flow') {

  $session_hash = $this->ion_auth->user()->row()->session_hash;

  $description = $this->dataoverview->generate_xml(
    'flow',
    $this->config->item('xml_fields_implementation')
  );
  $post_data = array(
      'description' => $description,
      'api_key' => $session_hash
  );
  if( array_key_exists('flow',$_FILES) and $_FILES['flow']['error'] == 0 ) {
      $post_data['flow'] = new CurlFile($_FILES['flow']['tmp_name'], 'text/xml');
  }
  $url = BASE_URL.'api/v1/flow';
  // Send the request & save response to $resp
  $api_response = $this->curlhandler->post_multipart_helper( $url, $post_data );
  if($api_response !== false) {
    $xml = simplexml_load_string( $api_response );
    $this->responsetype = 'alert alert-success';
    $this->responsecode = -1;
    $this->response = 'Flow was uploaded with id: ';
    if( property_exists( $xml->children('oml', true), 'code' ) ) {
      $this->responsetype = 'alert alert-danger';
      $this->responsecode = $xml->children('oml', true)->code;
      $this->response = 'Error '.$this->responsecode.': '.$xml->children('oml', true)->message;
      if($this->responsecode=='131') $this->response .= ' Please fill in all required (red) fields, upload a file or give a URL (not both), and avoid spaces in the flow name.';
    } else if($xml->children('oml', true)->id){
      $this->response = '<h2><i class="fa fa-thumbs-o-up"></i> Thanks!</h2>Flow was uploaded successfully (ID = '.$xml->children('oml', true)->id .')<br> You can now <b><a href="f/'. $xml->children('oml', true)->id . '"> follow your flow on OpenML</a></b>, complete its description, track its impact, and see all ensuing results.<br><br>You can also continue to add flows below.';
      sm($this->response);
      su('new/flow');
    } else {
      print "Something went wrong. Server says:<br>";
      print $api_response;
    }
  } else{
    $this->responsetype = 'alert alert-danger';
    $this->response = 'Could not upload flow. Please fill in all required (red) fields.';
  }
} elseif($this->subpage == 'study') {
  $user_id = $this->ion_auth->user()->row()->id;
  $name = $this->input->post( 'study_title' );
  $alias = $this->input->post( 'study_alias' );
  $description = $this->input->post( 'description' );
  $sid = $this->Study->create( $name, $alias, $description, $user_id );
  if($sid) {
    redirect('s/'.$sid);
  }
}

?>
