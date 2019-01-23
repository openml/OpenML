<?php
class Api_run extends MY_Api_Model {

  protected $version = 'v1';

  function __construct() {
    parent::__construct();

    // load models
    $this->load->model('Run');
    $this->load->model('Run_evaluated');
    $this->load->model('Dataset');
    $this->load->model('Run_tag');
    $this->load->model('Runfile');
    $this->load->model('Algorithm_setup');
    $this->load->model('Input_setting');
    $this->load->model('Output_data');
    $this->load->model('Input_data');
    $this->load->model('Task');
    $this->load->model('Task_inputs');
    $this->load->model('Author');
    $this->load->model('Implementation');
    $this->load->model('Trace');

    $this->load->model('Estimation_procedure');
    $this->load->model('Evaluation');
    $this->load->model('Evaluation_fold');
    $this->load->model('Evaluation_sample');

    $this->load->helper('arff');

    $this->load->model('File');

    $this->db = $this->Database_singleton->getWriteConnection();

    // Currently default
    $this->weka_engine_id = 1;
  }

  function bootstrap($format, $segments, $request_type, $user_id) {
    $this->outputFormat = $format;

    $getpost = array('get','post');

    if (count($segments) >= 1 && $segments[0] == 'list') {
      array_shift($segments);
      $this->run_list($segments, $user_id);
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'evaluate' && $request_type == 'post') {
      $this->run_evaluate();
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'trace' && $request_type == 'post') {
      $this->run_trace_upload();
      return;
    }

    if (count($segments) == 2 && $segments[0] == 'trace' && is_numeric($segments[1])) {
      $this->run_trace($segments[1]);
      return;
    }

    if (count($segments) == 1 && is_numeric($segments[0]) && in_array($request_type, $getpost)) {
      $this->run($segments[0]);
      return;
    }

    if (count($segments) == 2 && is_numeric($segments[1]) && $segments[0] == 'reset' && in_array($request_type, $getpost)) {
      $this->run_reset($segments[1]);
      return;
    }

    if (count($segments) == 1 && is_numeric($segments[0]) && $request_type == 'delete') {
      $this->run_delete($segments[0]);
      return;
    }

    if (count($segments) == 0 && $request_type == 'post') {
      $this->run_upload();
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'tag' && $request_type == 'post') {
      $this->entity_tag_untag('run', $this->input->post('run_id'), $this->input->post('tag'), false, 'run');
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'untag' && $request_type == 'post') {
      $this->entity_tag_untag('run', $this->input->post('run_id'), $this->input->post('tag'), true, 'run');
      return;
    }
    
    if (count($segments) == 2 && $segments[0] == 'tag' && $segments[1] == 'list') {
      $this->list_tags('run', 'run');
      return;
    }

    $this->returnError( 100, $this->version );
  }


  private function run_list($segs, $user_id) {
    $result_limit = 10000;
    $legal_filters = array('task', 'setup', 'flow', 'uploader', 'run', 'tag', 'limit', 'offset', 'task_type', 'show_errors');
    
    list($query_string, $illegal_filters) = $this->parse_filters($segs, $legal_filters);
    if (count($illegal_filters) > 0) {
      $this->returnError(514, $this->version, $this->openmlGeneralErrorCode, 'Legal filter operators: ' . implode(',', $legal_filters) .'. Found illegal filter(s): ' . implode(', ', $illegal_filters));
      return;
    }
    
    $illegal_filter_inputs = $this->check_filter_inputs($query_string, $legal_filters, array('tag', 'show_errors'));
    if (count($illegal_filter_inputs) > 0) {
      $this->returnError(511, $this->version, $this->openmlGeneralErrorCode, 'Filters with illegal values: ' . implode(',', $illegal_filter_inputs));
      return;
    }

    $task_id = element('task', $query_string, null);
    $task_type_id = element('task_type', $query_string, null);
    $setup_id = element('setup',$query_string, null);
    $implementation_id = element('flow',$query_string, null);
    $uploader_id = element('uploader',$query_string, null);
    $run_id = element('run',$query_string, null);
    $tag = element('tag',$query_string, null);
    $limit = element('limit',$query_string, null);
    $offset = element('offset',$query_string, null);
    $show_errors = element('show_errors',$query_string, null);
    
    if ($offset && !$limit) {
      $this->returnError(515, $this->version);
      return;
    }
    if ($limit && $limit > $result_limit) {
      $this->returnError(516, $this->version);
      return;
    }
    
    if ($task_id === null && $task_type_id === null && $setup_id === null && $implementation_id === null && $uploader_id === null && $run_id === null && $tag === null && $limit === null) {
      $this->returnError(510, $this->version);
      return;
    }

    $where_task = $task_id === null ? '' : ' AND `r`.`task_id` IN (' . $task_id . ') ';
    $where_task_type = $task_type_id === null ? '' : ' AND `t`.`ttid` IN (' . $task_type_id . ') ';
    $where_setup = $setup_id === null ? '' : ' AND `r`.`setup` IN (' . $setup_id . ') ';
    $where_uploader = $uploader_id === null ? '' : ' AND `r`.`uploader` IN (' . $uploader_id . ') ';
    $where_impl = $implementation_id === null ? '' : ' AND `i`.`id` IN (' . $implementation_id . ') ';
    $where_run = $run_id === null ? '' : ' AND `r`.`rid` IN (' . $run_id . ') ';
    $where_tag = $tag === null ? '' : ' AND `r`.`rid` IN (select id from run_tag where tag="' . $tag . '") ';
    // TODO: runs with errors are always removed?
    $where_server_error = ' AND `e`.`error` IS NULL ';
    if (strtolower($show_errors) == 'true') {
      $where_server_error = '';
    }
    // Don't return runs of closed runs, unless the user uploaded them
    $where_task_closed = ' AND (`t`.`embargo_end_date` is NULL OR `t`.`embargo_end_date` < NOW() OR `r`.`uploader` = '.$user_id.')';

    $where_limit = $limit === null ? '' : ' LIMIT ' . $limit;
    if ($limit && $offset) {
      $where_limit =  ' LIMIT ' . $offset . ', ' . $limit;
    }

    $where_total = $where_task . $where_task_type . $where_setup . $where_uploader . $where_impl . $where_run . $where_tag . $where_server_error . $where_task_closed;

    $sql =
      'SELECT r.rid, r.uploader, r.task_id, r.start_time, t.ttid, d.did AS dataset_id, d.name AS dataset_name,' .
             'r.setup, i.id AS flow_id, i.name AS flow_name, r.error_message, r.run_details ' .
             //', GROUP_CONCAT(tag) AS tags ' .
      'FROM algorithm_setup s, implementation i, task t ' .
      'LEFT JOIN task_inputs ti ON t.task_id = ti.task_id AND ti.input = "source_data" ' .
      'LEFT JOIN dataset d ON ti.value = d.did, ' .
      'run r ' .
      'LEFT JOIN run_evaluated e ON r.rid = e.run_id ' .
      'WHERE r.setup = s.sid AND i.id = s.implementation_id AND t.task_id = r.task_id ' .
      $where_total .
      // 'GROUP BY r.rid ' .
      $where_limit;
    $res = $this->Run->query($sql);

    if ($res == false) {
      $this->returnError(512, $this->version);
      return;
    }

    if (count($res) > $result_limit) {
      $this->returnError(513, $this->version, $this->openmlGeneralErrorCode, 'Size of result set: ' . count($res) . '; max size: ' . $result_limit);
      return;
    }

    $this->xmlContents('runs', $this->version, array('runs' => $res));
  }


  private function run($run_id) {
    if( $run_id == false ) {
      $this->returnError( 235, $this->version );
      return;
    }
    $run = $this->Run->getById($run_id);
    if( $run === false ) {
      $this->returnError( 236, $this->version );
      return;
    }

    $run->eval_data = $this->Run_evaluated->getById(array($run_id,$this->weka_engine_id)); # TODO: currently, by default we get weka evaluation results
    $run->inputData = $this->Run->getInputData( $run->rid );
    $run->outputData = $this->Run->getOutputData( $run->rid );
    $run->setup = $this->Algorithm_setup->getById( $run->setup );
    $run->tags = $this->Run_tag->getColumnWhere( 'tag', 'id = ' . $run->rid );
    $run->inputSetting = $this->Input_setting->query('SELECT i.name, i.implementation_id, s.value from input i, input_setting s where i.id=s.input_id and setup = ' . $run->setup->sid );
    $run->task_type = $this->Task->query('SELECT tt.name from task t, task_type tt where t.ttid=tt.ttid and t.task_id = ' . $run->task_id )[0]->name;
    $user = $this->Author->getById($run->uploader);
    $run->user_name = $user->first_name . ' ' . $user->last_name;
    $run->flow_name = $this->Implementation->getById($run->setup->implementation_id)->fullName;
    $run->task_evaluation = $this->Task_inputs->getWhere("task_id = " . $run->task_id . " and input = 'evaluation_measures'")[0];

    $this->xmlContents( 'run-get', $this->version, array( 'source' => $run ) );
  }

  private function run_delete($run_id) {

    $run = $this->Run->getById( $run_id );
    if( $run == false ) {
      $this->returnError( 392, $this->version );
      return;
    }

    if($run->uploader != $this->user_id && $this->user_has_admin_rights == false) {
      $this->returnError( 393, $this->version );
      return;
    }

    $result = true;
    $result = $result && $this->Input_data->deleteWhere( 'run =' . $run->rid );
    $result = $result && $this->Output_data->deleteWhere( 'run =' . $run->rid );

    if( $result ) {
      $additional_sql = ''; //' AND `did` NOT IN (SELECT `data` FROM `input_data` UNION SELECT `data` FROM `output_data`)';
      $result = $result && $this->Runfile->deleteWhere('`source` = "' . $run->rid . '" ' . $additional_sql);
      $result = $result && $this->Evaluation->deleteWhere('`source` = "' .  $run->rid. '" ' . $additional_sql);
      $result = $result && $this->Evaluation_fold->deleteWhere('`source` = "' . $run->rid . '" ' . $additional_sql);
      $result = $result && $this->Evaluation_sample->deleteWhere('`source` = "' . $run->rid . '" ' . $additional_sql);
      $result = $result && $this->Run_evaluated->deleteWhere('`run_id` = "' . $run->rid . '" ');
      // Not needed
      //$this->Dataset->deleteWhere('`source` = "' . $run->rid . '" ' . $additional_sql);
    }

    if( $result ) {
      $result = $result && $this->Run->delete( $run->rid );
    }

    if( $result == false ) {
      $this->returnError( 394, $this->version );
      return;
    }

    try {
      $this->elasticsearch->delete('run', $run_id);
      $this->elasticsearch->index('user', $this->user_id);
    } catch (Exception $e) {
      $additionalMsg = get_class() . '.' . __FUNCTION__ . ':' . $e->getMessage();
      $this->returnError(105, $this->version, $this->openmlGeneralErrorCode, $additionalMsg);
      return;
    }

    $this->xmlContents( 'run-delete', $this->version, array( 'run' => $run ) );
  }


  private function run_reset($run_id) {

    $run = $this->Run->getById( $run_id );
    if( $run == false ) {
      $this->returnError( 412, $this->version );
      return;
    }

    if($run->uploader != $this->user_id && $this->user_has_admin_rights == false) {
      $this->returnError(413, $this->version);
      return;
    }
    
    $result = $this->Run_evaluated->deleteWhere('`run_id` = "' . $run->rid . '" ');

    if( $result == false ) {
      $this->returnError(394, $this->version);
      return;
    }
    $this->xmlContents( 'run-reset', $this->version, array( 'run' => $run ) );
  }

  private function run_upload() {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Everything that needs to be done for EVERY task,        *
     * Including the unsupported tasks                         *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    // IMPORTANT! This function is sort of similar to "setup exists".
    // If changing something big, also test that function.

    $timestamps = array(microtime(true)); // profiling 0

    // check uploaded file
    $description = isset($_FILES['description']) ? $_FILES['description'] : false;
    $uploadError = '';
    if(!check_uploaded_file($description,false,$uploadError)) {
      $this->returnError(201, $this->version,$this->openmlGeneralErrorCode,$uploadError);
      return;
    }

    // validate xml
    $xmlErrors = '';
    if(validateXml($description['tmp_name'], xsd('openml.run.upload', $this->controller, $this->version), $xmlErrors) == false) {
      if (DEBUG) {
        $to = $this->user_email;
        $subject = 'OpenML Flow Upload DEBUG message. ';
        $content = 'Filename: ' . $_FILES['description']['name'] . "\nXSD Validation Message: " . $xmlErrors . "\n=====BEGIN XML=====\n" . file_get_contents($description['tmp_name']);
        sendEmail($to, $subject, $content,'text');
      }

      $this->returnError(202, $this->version, $this->openmlGeneralErrorCode, $xmlErrors);
      return;
    }

    // fetch xml
    $xml = simplexml_load_file($description['tmp_name']);
    if($xml === false) {
      $this->returnError(203, $this->version);
      return;
    }

    $run_xml = all_tags_from_xml(
      $xml->children('oml', true),
      $this->xml_fields_run);

    $task_id = $run_xml['task_id'];
    $implementation_id = $run_xml['flow_id'];
    $setup_string = array_key_exists('setup_string', $run_xml) ? $run_xml['setup_string'] : null;
    $error_message = array_key_exists('error_message', $run_xml) ? $run_xml['error_message'] : false;
    $run_details = array_key_exists('run_details', $run_xml) ? $run_xml['run_details'] : null;
    $parameter_objects = array_key_exists('parameter_setting', $run_xml) ? $run_xml['parameter_setting'] : array();
    $output_data = array_key_exists('output_data', $run_xml) ? $run_xml['output_data'] : array();
    $tags = array_key_exists('tag', $run_xml) ? str_getcsv ($run_xml['tag']) : array();

    $supported_evaluation_measures = $this->Math_function->getColumnWhere('name', '`functionType` = "EvaluationFunction"');

    // the user can specify his own metrics. here we check whether these exists in the database.
    if($output_data != false && array_key_exists('evaluation', $output_data)) {
      // php does not have a set data structure, use hashmap instead
      $used_evaluation_measures = array();
      foreach($output_data->children('oml',true)->{'evaluation'} as $eval) {
        $used_evaluation_measures[''.$eval->name] = true;
      }
      $used_evaluation_measures = array_keys($used_evaluation_measures);
      $unknown_measures = array_diff($used_evaluation_measures, $supported_evaluation_measures);
      if (count($unknown_measures) > 0) {
        $this->returnError(217, $this->version,$this->openmlGeneralErrorCode,'Measure(s): ' . implode(',', $unknown_measures));
        return;
      }
    }
    $predictionsUrl   = false;

    // fetch implementation
    $implementation = $this->Implementation->getById($implementation_id);
    if($implementation === false) {
      $this->returnError(205, $this->version);
      return;
    }
    if(in_array($implementation->{'implements'}, $this->supportedMetrics)) {
      $this->returnError(218, $this->version);
      return;
    }

    // check whether uploaded files are present.

    foreach ($_FILES as $key => $value) {
      $message = '';
      $extension = getExtension($_FILES[$key]['name']);

      if (/*in_array($extension,$this->config->item('allowed_extensions')) == false ||*/ $extension == false) {
        $this->returnError(206, $this->version, $this->openmlGeneralErrorCode, 'Invalid extension for file "'.$key.'". Original filename: ' . $_FILES[$key]['name']);
        return;
      }

      if (!check_uploaded_file($_FILES[$key], false, $message)) {
        $this->returnError(207, $this->version, $this->openmlGeneralErrorCode, 'Upload problem with file "'.$key.'": ' . $message);
        return;
      }

      if ($extension == 'arff') {
        $arffCheck = ARFFcheck($_FILES[$key]['tmp_name'], 1000);
        if ($arffCheck !== true) {
          $this->returnError(209, $this->version, $this->openmlGeneralErrorCode, 'Arff error in ' . $key . ' file: ' . $arffCheck);
          return;
        }
      }

      if ($extension == 'xml') {
        $xmlCheck = simplexml_load_file($_FILES[$key]['tmp_name']);
        if($xmlCheck === false) {
          $this->returnError(209, $this->version, $this->openmlGeneralErrorCode, 'XML error in ' . $key . ' file: ' . $xmlCheck);
          return;
        }
      }
    }

    $timestamps[] = microtime(true); // profiling 1

    $parameters = array();
    foreach($parameter_objects as $p) {
      // since 'component' is an optional XML field, we add a default option
      $component = property_exists($p, 'component') ? $p->component : $implementation->id;

      // now find the input id
      $input_id = $this->Input->getWhereSingle('`implementation_id` = ' . $component . ' AND `name` = "' . $p->name . '"');
      if($input_id === false) {
        $this->returnError(213, $this->version, $this->openmlGeneralErrorCode, 'Name: ' . $p->name . ', flow id (component): ' . $component);
        return;
      }

      $parameters[$input_id->id] = $p->value . '';
    }
    
    // search setup ... // TODO: do something about the new parameters. Are still retrieved by ID, does not work with Weka plugin.
    try {
      $setupId = $this->Algorithm_setup->getSetupId($implementation, $parameters, true, $setup_string);
    } catch(Exception $e) {
      $this->returnError(215, $this->version);
      return;
    }
    
    if( $setupId === false ) {
      $this->returnError(214, $this->version);
      return;
    }

    $timestamps[] = microtime(true); // profiling 2

    // fetch task
    $taskRecord = $this->Task->getById($task_id);
    if($taskRecord === false) {
      $this->returnError(204, $this->version);
      return;
    }

    $task = $this->Task_inputs->getTaskValuesAssoc($task_id);
    if (!array_key_exists('source_data', $task)) {
      $this->returnError(219, $this->version);
      return;
    }
    if (!array_key_exists('estimation_procedure', $task)) {
      $this->returnError(220, $this->version);
      return;
    }
    // check if dataset record associated to task exists
    $dataset_record = $this->Dataset->getById($task['source_data']);
    if ($dataset_record === false) {
      $this->returnError(221, $this->version);
      return;
    }
    // check if estimation procedure record associated to task exists
    $ep_record = $this->Estimation_procedure->getById($task['estimation_procedure']);
    if ($ep_record === false) {
      $this->returnError(222, $this->version);
      return;
    }
    
    // check whether user calculated measures are actually legal
    if (array_key_exists('evaluation', $output_data)) {
      // check
    }

    // now create a run
    $runData = array(
      'uploader' => $this->user_id,
      'setup' => $setupId,
      'task_id' => $task_id,
      'start_time' => now(),
      'error_message' => ($error_message == false) ? null : $error_message,
      'run_details' => ($run_details == false) ? null : $run_details
    );

    $this->db->trans_start();
    $runId = $this->Run->insert($runData);
    if($runId === false) {
      $this->returnError(210, $this->version);
      return;
    }
    // and fetch the run record
    $run = $this->Run->getById($runId);
    $result = new stdClass();
    $result->run_id = $runId; // for output

    // attach uploaded files as output to run
    foreach($_FILES as $key => $value) {
      $file_type = 'run_uploaded_file';
      if ($key == 'predictions') {
        $file_type = 'predictions';
      } elseif ($key == 'trace') {
        $file_type = 'run_trace';
      }

      // it is important to put the runs in various directories
      $subdirectory = floor($runId / $this->content_folder_modulo) * $this->content_folder_modulo;
      $to_folder = $this->data_folders['run'] . '/' . $subdirectory . '/' . $runId . '/';
      $file_id = $this->File->register_uploaded_file($value, $to_folder, $this->user_id, $file_type);
      if(!$file_id) {
        $this->returnError(223, $this->version);
        return;
      }
      $file_record = $this->File->getById($file_id);

      $record = array(
        'source' => $runId,
        'field' => $key,
        'name' => $value['name'],
        'format' => $file_record->extension,
        'file_id' => $file_id,
        'upload_time' => now()
      );

      $did = $this->Runfile->insert($record);
      if( $did == false ) {
        $this->returnError(212, $this->version);
        return;
      }
      $this->Run->outputData($runId, $did, 'runfile', $key);
    }

    // attach input data TODO: JvR: this is legacy and implicit with the task. Remove?
    $inputData = $this->Run->inputData($runId, $task['source_data'], 'dataset'); // Based on the query, it has been garantueed that the dataset id exists.
    if($inputData === false) {
      $errorCode = 211;
      return false;
    }
    $this->db->trans_complete();
	if ($this->db->trans_status() === FALSE) {
	  $this->returnError(224, $this->version);
      return;
    }

    $timestamps[] = microtime(true); // profiling 3
    // add to elastic search index.

    try {
      $this->elasticsearch->index('run', $runId);
    } catch (Exception $e) {
      // TODO: should log
    }

    $timestamps[] = microtime(true); // profiling 4
    if (DEBUG) {
      $this->Log->profiling(__FUNCTION__, $timestamps,
        array(
          'uploaded file handling',
          'setup searching / creation',
          'database insertions',
          'elastic search')
      );
    }

    // tag it, if neccessary
    foreach($tags as $tag) {
      $success = $this->entity_tag_untag('run', $runId, $tag, false, 'run', true);
      // if tagging went wrong, an error is displayed. (TODO: something else?)
      if (!$success) return;
    }

    // remove scheduled task
    $this->Schedule->deleteWhere( 'task_id = "' . $task_id . '" AND sid = "' . $setupId . '"' );

    // and present result, in effect only a run_id.
    $this->xmlContents( 'run-upload', $this->version, $result );
  }

  private function run_trace($run_id) {
    $trace = $this->Trace->getWhere('run_id = ' . $run_id, 'repeat ASC, fold ASC, iteration ASC');

    if ($trace === false) {
      $this->returnError(570,$this->version);
      return;
    }

    $this->xmlContents('run-trace-get', $this->version, array('run_id' => $run_id, 'trace' => $trace));
  }

  private function run_trace_upload() {
    if (!$this->user_has_admin_rights) {
      $this->returnError(106, $this->version);
      return;
    }

    // check uploaded file
    $trace = isset($_FILES['trace']) ? $_FILES['trace'] : false;
    if(!check_uploaded_file($trace)) {
      $this->returnError(561,$this->version);
      return;
    }

    $xsd = xsd('openml.run.trace', $this->controller, $this->version);

    // validate xml
    if(validateXml($trace['tmp_name'], $xsd, $xmlErrors) == false) {
      $this->returnError(562, $this->version, $this->openmlGeneralErrorCode, $xmlErrors);
      return;
    }

    // fetch xml
    $xml = simplexml_load_file($trace['tmp_name']);
    if($xml === false) {
      $this->returnError(563, $this->version);
      return;
    }

    $run_id = (string) $xml->children('oml', true)->{'run_id'};

    $this->db->trans_start();
    foreach($xml->children('oml', true)->{'trace_iteration'} as $t) {
      $iteration = xml2assoc($t, true);

      $iteration['run_id'] = $run_id;

      $this->Trace->insert($iteration);
    }
    $this->db->trans_complete();
    if ($this->db->trans_status() === FALSE) {
	  $this->returnError(564, $this->version);
      return;
    }

    $this->xmlContents('run-trace', $this->version, array('run_id' => $run_id));
  }

  private function run_evaluate() {
    $timestamps = array(microtime(true)); // profiling 0
    if (!$this->user_has_admin_rights) {
      $this->returnError(106, $this->version);
      return;
    }

    // check uploaded file
    $description = isset( $_FILES['description']) ? $_FILES['description'] : false;
    $error_message = null;
    if(!check_uploaded_file($description, false, $error_message)) {
      $this->returnError(422, $this->version, $this->openmlGeneralErrorCode, $error_message);
      return;
    }

    $xsd = xsd('openml.run.evaluate', $this->controller, $this->version);

    // validate xml
    if(validateXml( $description['tmp_name'], $xsd, $xmlErrors) == false) {
      $this->returnError(423, $this->version, $this->openmlGeneralErrorCode, $xmlErrors);
      return;
    }

    // fetch xml
    $xml = simplexml_load_file($description['tmp_name']);
    if($xml === false) {
      $this->returnError(424, $this->version);
      return;
    }

    // TODO: check if user id and evaluation_engine_id are compatible
    // (i.e., is the user allowed to run the engine)

    $run_id = (string) $xml->children('oml', true)->{'run_id'};
    $eval_engine_id = '' . $xml->children('oml', true)->{'evaluation_engine_id'};

    $runRecord = $this->Run->getById($run_id);
    if($runRecord == false) {
      $this->returnError(425, $this->version);
      return;
    }


    $math_functions = $this->Math_function->getAssociativeArray('name', 'id', 'functionType = "EvaluationFunction"');

    $evaluation_record = $this->Run_evaluated->getById(array($run_id, $eval_engine_id));
    $evaluations_stored = $this->Evaluation->getWhere('source = "' . $run_id . '" AND evaluation_engine_id = "' . $eval_engine_id . '"');

    if($evaluation_record && $evaluation_record->error == null) {
      $this->returnError(426, $this->version);
      return;
    }

    if ($evaluations_stored && !$evaluation_record) {
      $this->returnError(427, $this->version);
      return;
    }

    $num_tries = 0;
    if ($evaluation_record) {
      $num_tries = $evaluation_record->num_tries;
    }

    $timestamps[] = microtime(true); // profiling 1

    $data = array('evaluation_date' => now());
    if (isset($xml->children('oml', true)->{'error'})) {
      $data['error'] = '' . $xml->children('oml', true)->{'error'};
    }
    if (isset( $xml->children('oml', true)->{'warning'})) {
      $data['warning'] = '' . $xml->children('oml', true)->{'warning'};
    }

    // TODO: the new way to go.
    $data['run_id'] = $run_id;
    $data['evaluation_engine_id'] = $eval_engine_id;
    $data['user_id'] = $this->user_id;
    $data['num_tries'] = $num_tries + 1;

    $this->db->trans_start();
    $this->Run_evaluated->replace($data);

    foreach($xml->children('oml', true)->{'evaluation'} as $e) {
      $evaluation = xml2assoc($e, true);

      // adding rid
      $evaluation['source'] = $run_id;
      // adding evaluation engine id
      $evaluation['evaluation_engine_id'] = $eval_engine_id;

      // TODO: this responsibility should be shifted to the evaluation engine
      if (array_key_exists($evaluation['name'], $math_functions)) {
        $evaluation['function_id'] = $math_functions[$evaluation['name']];
      } else {
        // there will be a DB error due to the absence of 'function_id'
      }
      // unset function field
      unset($evaluation['name']);

      if(array_key_exists('fold', $evaluation) && array_key_exists('repeat', $evaluation) &&  array_key_exists('sample', $evaluation)) {
        // evaluation_sample
        $this->Evaluation_sample->insert($evaluation);
      } elseif(array_key_exists('fold', $evaluation) && array_key_exists('repeat', $evaluation)) {
        // evaluation_fold
        $this->Evaluation_fold->insert($evaluation);
      } else {
        // global
        $this->Evaluation->insert($evaluation);
      }
    }
    $this->db->trans_complete();
    if ($this->db->trans_status() === FALSE) {
	  $this->returnError(428, $this->version);
      return;
    }


    $timestamps[] = microtime(true); // profiling 2

    // update elastic search index.
    try {
      $this->elasticsearch->index('run', $run_id);
      $this->elasticsearch->index('user', $this->user_id);
    } catch (Exception $e) {
      $additionalMsg = get_class() . '.' . __FUNCTION__ . ':' . $e->getMessage();
      $this->returnError(105, $this->version, $this->openmlGeneralErrorCode, $additionalMsg);
      return;
    }

    $timestamps[] = microtime(true); // profiling 3
    if (DEBUG) {
      $this->Log->profiling(__FUNCTION__, $timestamps,
        array(
          'basic checks of inputs and xml',
          'database insertions',
          'elastic search indexing')
      );
    }

    $this->xmlContents('run-evaluate', $this->version, array('run_id' => $run_id));
  }
}
?>
