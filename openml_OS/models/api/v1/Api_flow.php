<?php
class Api_flow extends MY_Api_Model {

  protected $version = 'v1';

  function __construct() {
    parent::__construct();

    // load models
    $this->load->model('Algorithm_setup');

    $this->load->model('Implementation');
    $this->load->model('Implementation_tag');
    $this->load->model('Implementation_component');
    $this->load->model('Input_setting');


    $this->load->model('File');
    $this->load->model('Input');

    $this->load->model('Database_singleton');
    $this->db = $this->Database_singleton->getReadConnection();
  }

  function bootstrap($format, $segments, $request_type, $user_id) {
    $this->outputFormat = $format;

    $getpost = array('get','post');

    if (count($segments) >= 1 && $segments[0] == 'list') {
      array_shift($segments);
      $this->flow_list($segments);
      return;
    }

    // TODO: deprecate!
    if (count($segments) == 3 && $segments[0] == 'exists') {
      $this->flow_exists($segments[1],$segments[2]);
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'exists') {
      $this->flow_exists($this->input->post('name'),$this->input->post('external_version'));
      return;
    }

    if (count($segments) == 1 && is_numeric($segments[0]) && in_array($request_type, $getpost)) {
      $this->flow($segments[0]);
      return;
    }

    if (count($segments) == 1 && is_numeric($segments[0]) && $request_type == 'delete') {
      $this->flow_delete($segments[0]);
      return;
    }

    if (count($segments) == 2 && is_numeric($segments[0]) && $segments[1] == 'force' && $request_type == 'delete') {
      $this->flow_forcedelete($segments[0]);
      return;
    }

    if (count($segments) == 0 && $request_type == 'post') {
      $this->flow_upload();
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'tag' && $request_type == 'post') {
      $this->entity_tag_untag('implementation', $this->input->post('flow_id'), $this->input->post('tag'), false, 'flow');
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'untag' && $request_type == 'post') {
      $this->entity_tag_untag('implementation', $this->input->post('flow_id'), $this->input->post('tag'), true, 'flow');
      return;
    }

    $this->returnError( 100, $this->version );
  }


  private function flow_list($segs) {
    $legal_filters = array('uploader', 'tag', 'limit', 'offset');
    $query_string = array();
    for ($i = 0; $i < count($segs); $i += 2) {
      $query_string[$segs[$i]] = urldecode($segs[$i+1]);
      if (in_array($segs[$i], $legal_filters) == false) {
        $this->returnError(501, $this->version, $this->openmlGeneralErrorCode, 'Legal filter operators: ' . implode(',', $legal_filters) .'. Found illegal filter: ' . $segs[$i]);
        return;
      }
    }

    $uploader_id = element('uploader', $query_string);
    $tag = element('tag', $query_string);
    $limit = element('limit', $query_string);
    $offset = element('offset', $query_string);

    if (!(uploader_id($uploader_id) && is_safe($tag) && is_natural_number($limit) && is_natural_number($offset))) {
      $this->returnError(502, $this->version);
      return;
    }

    $query = $this->db->select('`i`.*');
    $query->from('implementation i');
    if ($tag) {
      $query->join('implementation_tag t', 'i.id = t.id');
      $query->where('t.tag', $tag);
    }
    $query->group_start()->where('`visibility`', 'public')->or_where('i.uploader', $this->user_id)->group_end();
    if ($uploader_id) {
      $query->where('i.uploader', $uploader_id);
    }
    if ($limit) {
      $query->limit($limit);
    }
    if ($offset) {
      $query->offset($offset);
    }
    $sql = $query->get_compiled_select();

    # TODO: can remove next statement and replace by original active record
    $implementations_res = $this->Implementation->query($sql);
    if( $implementations_res == false ) {
      $this->returnError( 500, $this->version );
      return;
    }

    $this->xmlContents('implementations', $this->version, array('implementations' => $implementations_res));
  }

  // deprecated, will be removed soon
  /*private function flow_owned() {

    $implementations = $this->Implementation->getColumnWhere( 'id', '`uploader` = "'.$this->user_id.'"' );
    if( $implementations == false ) {
      $this->returnError( 312, $this->version );
      return;
    }
    $this->xmlContents( 'implementation-owned', $this->version, array( 'implementations' => $implementations ) );
  }*/


  private function flow_exists($name, $external_version) {

    $similar = false;
    if( $name !== false && $external_version !== false ) {
      $similar = $this->Implementation->getWhere( '`name` = "' . $name . '" AND `external_version` = "' . $external_version . '"' );
    } else {
      $this->returnError( 330, $this->version );
      return;
    }

    $result = array( 'exists' => 'false', 'id' => -1 );
    if( $similar ) {
      $result = array( 'exists' => 'true', 'id' => $similar[0]->id );
    }
    $this->xmlContents( 'implementation-exists', $this->version, $result );
  }

  // TODO: check what is going wrong with implementation id 1
  private function flow($id) {
    if( $id == false ) {
      $this->returnError( 180, $this->version );
      return;
    }

    $implementation = $this->Implementation->fullImplementation( $id );

    if( $implementation === false ) {
      $this->returnError( 181, $this->version );
      return;
    }

    $this->xmlContents( 'implementation-get', $this->version, array( 'source' => $implementation ) );
  }

  private function flow_upload() {

    if(isset($_FILES['source']) && $_FILES['source']['error'] == 0) {
      $source = true;
    } else {
      $source = false;
      unset($_FILES['source']);
    }

    if(isset($_FILES['binary']) && $_FILES['binary']['error'] == 0) {
      $binary = true;
    } else {
      $binary = false;
      unset($_FILES['binary']);
    }

    //if( $source == false && $binary == false ) {
    //  $this->returnError( 162, $this->version );
    //  return;
    //}

    foreach( $_FILES as $key => $file ) {
      if( check_uploaded_file( $file ) == false ) {
        $this->returnError( 160, $this->version );
        return;
      }
    }

    $xsd = xsd('openml.implementation.upload', $this->controller, $this->version);
    if (!$xsd) {
      $this->returnError( 172, $this->version, $this->openmlGeneralErrorCode );
      return;
    }

    // get correct description
    if( $this->input->post('description') ) {
      // get description from string upload
      $description = $this->input->post('description');
      $xmlErrors = "";
      if( validateXml( $description, $xsd, $xmlErrors, false ) == false ) {
        if (DEBUG) {
          $to = $this->user_email;
          $subject = 'OpenML Flow Upload DEBUG message. ';
          $content = "Uploaded by POST field.\nXSD Validation Message: " . $xmlErrors . "\n=====BEGIN XML=====\n" . $this->input->post('description');
          sendEmail($to, $subject, $content,'text');
        }

        $this->returnError( 163, $this->version, $this->openmlGeneralErrorCode, $xmlErrors );
        return;
      }
      $xml = simplexml_load_string( $description );
    } elseif(isset($_FILES['description'])) {
      // get description from file upload
      $description = $_FILES['description'];

      if (validateXml($description['tmp_name'], $xsd, $xmlErrors) == false) {
        if (DEBUG) {
          $to = $this->user_email;
          $subject = 'OpenML Flow Upload DEBUG message. ';
          $content = 'Filename: ' . $_FILES['description']['name'] . "\nXSD Validation Message: " . $xmlErrors . "\n=====BEGIN XML=====\n" . file_get_contents($description['tmp_name']);
          sendEmail($to, $subject, $content,'text');
        }

        $this->returnError( 163, $this->version, $this->openmlGeneralErrorCode, $xmlErrors );
        return;
      }
      $xml = simplexml_load_file( $description['tmp_name'] );
      $similar = $this->Implementation->compareToXML( $xml );
      if( $similar ) {
        $this->returnError( 171, $this->version, $this->openmlGeneralErrorCode, 'implementation_id:' . $similar );
        return;
      }
    } else {
      $this->returnError( 161, $this->version );
      return;
    }

    $name = ''.$xml->children('oml', true)->{'name'};

    $implementation = array(
      'uploadDate' => now(),
      'uploader' => $this->user_id
    );

    foreach( $_FILES as $key => $file ) {
      if( $key == 'description' ) { continue; }
      if( ! in_array( $key, array( 'description', 'source', 'binary' ) ) ) {
        $this->returnError( 167, $this->version );
        return;
      }

      $file_id = $this->File->register_uploaded_file($_FILES[$key], $this->data_folders['implementation'] . $key . '/', $this->user_id, 'implementation');
      if($file_id === false) {
        $this->returnError( 173, $this->version );
        return;
      }
      $file_record = $this->File->getById($file_id);

      //$implementation[$key.'Url'] = $this->data_controller . 'download/' . $file_id . '/' . $file_record->filename_original;
      $implementation[$key.'_md5'] = $file_record->md5_hash;
      $implementation[$key.'_file_id'] = $file_id;
      //$implementation[$key.'Format'] = $file_record->md5_hash;

      if( property_exists( $xml->children('oml', true), $key.'_md5' ) ) {
        if( $xml->children('oml', true)->{$key.'_md5'} != $file_record->md5_hash ) {
          $this->returnError( 168, $this->version );
          return;
        }
      }
    }

    $impl = $this->insertImplementationFromXML( $xml->children('oml', true), $this->xml_fields_implementation, $implementation );
    if( $impl == false ) {
      $this->returnError( 165, $this->version );
      return;
    }
    $implementation = $this->Implementation->getById( $impl );

    try {
      // update elastic search index.
      $this->elasticsearch->index('flow', $impl);

      // update counters
      $this->elasticsearch->index('user', $this->user_id);
    } catch (Exception $e) {
      // TODO: should be logged
    }

    $this->xmlContents( 'implementation-upload', $this->version, $implementation );
  }

  private function flow_delete($flow_id) {

    $implementation = $this->Implementation->getById($flow_id);
    if($implementation == false) {
      $this->returnError(322, $this->version);
      return;
    }

    if($implementation->uploader != $this->user_id && $this->user_has_admin_rights == false) {
      $this->returnError(323, $this->version);
      return;
    }

    $runs = $this->Run->getRunsByFlowId($implementation->id, null, null, 100);

    if ($runs) {
      $ids = array();
      foreach ($runs as $r) {
        $ids[] = $r->id;
      }
      $this->returnError(324, $this->version, $this->openmlGeneralErrorCode, '{'. implode(', ', $ids) .'} ()');
      return;
    }

    if ($this->Implementation->isComponent($implementation->id)) {
      $parent_ids = $this->Implementation_component->getColumnWhere('parent', 'child = "'.$implementation->id.'"');
      $this->returnError(328, $this->version, $this->openmlGeneralErrorCode, '{' . implode(', ', $parent_ids) . '}');
      return;
    }

    $remove_input_setting = $this->Input_setting->deleteWhere('setup IN (SELECT sid FROM algorithm_setup WHERE implementation_id = '.$implementation->id.')');
    if (!$remove_input_setting) {
      $this->returnError(326, $this->version);
      return;
    }
    $remove_setups = $this->Algorithm_setup->deleteWhere('implementation_id = ' . $implementation->id);
    if (!$remove_setups) {
      $this->returnError(327, $this->version);
      return;
    }

    $this->Input->deleteWhere('implementation_id =' . $implementation->id); // should be handled by constraints ..
    $this->Implementation_component->deleteWhere('parent = ' . $implementation->id);
    $result = $this->Implementation->delete($implementation->id);
    if( $implementation->binary_file_id != false ) { $this->File->delete_file($implementation->binary_file_id); }
    if( $implementation->source_file_id != false ) { $this->File->delete_file($implementation->source_file_id); }

    // TODO: also check component parts.

    if($result == false) {
      $this->returnError(325, $this->version);
      return;
    }

    try {
      $this->elasticsearch->delete('flow', $flow_id);
      $this->elasticsearch->index('user', $this->user_id);
    } catch (Exception $e) {
      $additionalMsg = get_class() . '.' . __FUNCTION__ . ':' . $e->getMessage();
      $this->returnError(105, $this->version, $this->openmlGeneralErrorCode, $additionalMsg);
      return;
    }

    $this->xmlContents('implementation-delete', $this->version, array('implementation' => $implementation));
  }

  private function flow_forcedelete($flow_id) {
    if( $this->ion_auth->is_admin($this->user_id) == false ) {
      $this->returnError( 550, $this->version );
      return;
    }

    $condition = 'SELECT rid FROM run r, algorithm_setup s WHERE s.sid = r.setup AND s.implementation_id = ' . $flow_id;

    $queries = array(
      'evaluation' => 'DELETE FROM evaluation WHERE source IN ('.$condition.');',
      'evaluation_fold' => 'DELETE FROM evaluation_fold WHERE source IN ('.$condition.');',
      'evaluation_sample' => 'DELETE FROM evaluation_sample WHERE source IN ('.$condition.');',
      'runfile' => 'DELETE FROM runfile WHERE source IN ('.$condition.');',
      'run' => 'DELETE FROM run WHERE setup IN (SELECT sid FROM algorithm_setup WHERE implementation_id = '.$flow_id.');',
      'algorithm_setup' => 'DELETE FROM algorithm_setup WHERE implementation_id = ' . $flow_id . ';'
    );

    foreach ($queries as $table => $query) {
      $res = $this->Implementation->query($query);
      if ($res == false) {
        $this->returnError(551, $this->version, $this->openmlGeneralErrorCode, 'In query table: ' . $table);
        return;
      }
    }

    $this->flow_delete($flow_id);
  }

  private function insertImplementationFromXML( $xml, $configuration, $implementation_base = array() ) {
    $implementation_objects = all_tags_from_xml( $xml, array_custom_filter($configuration, array('plain','array')) );
    $implementation = all_tags_from_xml( $xml, array_custom_filter($configuration, array('string','csv')), $implementation_base );

    // insert the implementation itself
    $version = $this->Implementation->incrementVersionNumber( $implementation['name'] );
    $implementation['fullName'] = $implementation['name'] . '(' . $version . ')';
    $implementation['version'] = $version;

    if( array_key_exists( 'source_md5', $implementation ) ) {
      if( array_key_exists( 'external_version', $implementation ) === false ) {
        $implementation['external_version'] = $implementation['source_md5'];
      }
    } elseif( array_key_exists( 'binary_md5', $implementation ) ) {
      if( array_key_exists( 'external_version', $implementation ) === false ) {
        $implementation['external_version'] = $implementation['binary_md5'];
      }
    }

    if( array_key_exists( 'implements', $implementation ) ) {
      if( in_array( $implementation['implements'], $this->supportedMetrics ) == false &&
          in_array( $implementation['implements'], $this->supportedAlgorithms == false ) ) {
        return false;
      }
    }

    // information illegal to insert
    unset($implementation['source_md5']);
    unset($implementation['binary_md5']);

    // tags also not insertable. but handled differently.
    $tags = array();
    if( array_key_exists( 'tag', $implementation ) ) {
      $tags = str_getcsv( $implementation['tag'] );
      unset( $implementation['tag'] );
    }
    $flow_id = $this->Implementation->insert( $implementation );
    if( $flow_id === false ) {
      return false;
    }

    // add to elastic search index.
    try {
      $this->elasticsearch->index('flow', $flow_id);
    } catch (Exception $e) {
      // TODO should be logged
    }


    foreach( $tags as $tag ) {
      $error = -1;
      $res = $this->entity_tag_untag('implementation', $flow_id, $tag, false, 'flow', true);
      if ($res != true) { // TODO: do something better
        exit();
      }
    }


    // insert all important "components"
    foreach( $implementation_objects as $key => $value ) {

      if( $key == 'component' ) {
        foreach($value as $entry) {
          $component = $entry->flow->children('oml', true);
          $similarComponent = $this->Implementation->compareToXml( $entry->flow );
          if( $similarComponent === false ) {
            $component->version = $this->Implementation->incrementVersionNumber( $component->name );
            $componentFullName = $component->name . '(' . $component->version . ')';
            $succes = $this->insertImplementationFromXML(
              $component,
              $configuration,
              array('uploadDate' => $implementation['uploadDate'], 'uploader' => $implementation['uploader']));

            if($succes == false) { return false; }
            $this->Implementation->addComponent( $flow_id, $succes, trim($entry->identifier) );
          } else {
            $this->Implementation->addComponent( $flow_id, $similarComponent, trim($entry->identifier) );
          }
        }
      } elseif( $key == 'parameter' ) {
        foreach( $value as $entry ) {
          $children = $entry->children('oml', true);
          $succes = $this->Input->insert(
            array(
              'fullName' => $implementation['fullName'] . '_' . $children->name,
              'implementation_id' => $flow_id,
              'name' => trim($children->name),
              'defaultValue' => property_exists( $children, 'default_value') ? trim($children->default_value) : null,
              'description' => property_exists( $children, 'description') ? trim($children->description) : null,
              'dataType' => property_exists( $children, 'data_type') ? trim($children->data_type) : null,
              'recommendedRange' => property_exists( $children, 'recommended_range') ? trim($children->recommendedRange) : null
            )
          );
        }
      }
    }
    return $flow_id;
  }
}
?>
