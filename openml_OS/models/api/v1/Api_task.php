<?php
class Api_task extends MY_Api_Model {

  protected $version = 'v1';

  function __construct() {
    parent::__construct();
    
    $this->load->helper('text');
    
    // load models
    $this->load->model('Dataset_status');
    $this->load->model('Task');
    $this->load->model('Task_tag');
    $this->load->model('Task_inputs');
    $this->load->model('Task_type');
    $this->load->model('Task_type_inout');
    $this->load->model('Data_quality');
    $this->load->model('Run');
    
    $this->load->model('Database_singleton');
    $this->db = $this->Database_singleton->getReadConnection();
  }

  function bootstrap($format, $segments, $request_type, $user_id) {
    $this->outputFormat = $format;

    $getpost = array('get','post');

    if (count($segments) >= 1 && $segments[0] == 'list') {
      array_shift($segments);
      $this->task_list($segments, $user_id);
      return;
    }

    if (count($segments) == 1 && is_numeric($segments[0]) && in_array($request_type, $getpost)) {
      $this->task($segments[0]);
      return;
    }

    if (count($segments) == 2 && is_numeric($segments[1]) && $segments[0] == 'inputs') {
      $this->task_inputs($segments[1]);
      return;
    }

    if (count($segments) == 0 && $request_type == 'post') {
      $this->task_upload();
      return;
    }

    if (count($segments) == 1 && is_numeric($segments[0]) && $request_type == 'delete') {
      $this->task_delete($segments[0]);
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'tag' && $request_type == 'post') {
      $this->entity_tag_untag('task', $this->input->post('task_id'), $this->input->post('tag'), false, 'task');
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'untag' && $request_type == 'post') {
      $this->entity_tag_untag('task', $this->input->post('task_id'), $this->input->post('tag'), true, 'task');
      return;
    }
    
    if (count($segments) == 2 && $segments[0] == 'tag' && $segments[1] == 'list') {
      $this->list_tags('task', 'task');
      return;
    }

    $this->returnError(100, $this->version);
  }


  private function task_list($segs, $user_id) {
    $legal_filters = array('type', 'tag', 'data_tag', 'status', 'limit', 'offset', 'task_id', 'data_id', 'data_name', 'number_instances', 'number_features', 'number_classes', 'number_missing_values');
    
    list($query_string, $illegal_filters) = $this->parse_filters($segs, $legal_filters);
    if (count($illegal_filters) > 0) {
      $this->returnError(480, $this->version, $this->openmlGeneralErrorCode, 'Legal filter operators: ' . implode(',', $legal_filters) .'. Found illegal filter(s): ' . implode(', ', $illegal_filters));
      return;
    }
    
    $illegal_filter_inputs = $this->check_filter_inputs($query_string, $legal_filters, array('type', 'tag', 'data_tag', 'status', 'data_name', 'number_instances', 'number_features', 'number_classes', 'number_missing_values'));
    if (count($illegal_filter_inputs) > 0) {
      $this->returnError(481, $this->version, $this->openmlGeneralErrorCode, 'Filters with illegal values: ' . implode(',', $illegal_filter_inputs));
      return;
    }
    
    $type = element('type', $query_string, null);
    $tag = element('tag', $query_string, null);
    $data_tag = element('data_tag', $query_string, null);
    $status = element('status', $query_string, null);
    $limit = element('limit', $query_string, null);
    $offset = element('offset', $query_string, null);
    $data_id = element('data_id', $query_string, null);
    $task_id = element('task_id', $query_string, null);
    $data_name = element('data_name', $query_string, null);
    $nr_insts = element('number_instances', $query_string, null);
    $nr_feats = element('number_features', $query_string, null);
    $nr_class = element('number_classes', $query_string, null);
    $nr_miss = element('number_missing_values', $query_string, null);
    
    if ($offset && !$limit) {
      $this->returnError(483, $this->version);
      return;
    }

    $where_type = $type === null ? '' : 'AND `t`.`ttid` = "'.$type.'" ';
    $where_tag = $tag === null ? '' : ' AND `t`.`task_id` IN (select id from task_tag where tag="' . $tag . '") ';
    $where_data_tag = $data_tag === null ? '' : ' AND `d`.`did` IN (select id from dataset_tag where tag="' . $data_tag . '") ';
    $where_did = $data_id === null ? '' : ' AND `d`.`did` IN ('. $data_id . ') ';
    $where_task_id = $task_id === null ? '' : ' AND `t`.`task_id` IN ('. $task_id . ') ';
    $where_data_name = $data_name === null ? '' : ' AND `d`.`name` = "'. $data_name . '"';
    $where_insts = $nr_insts === null ? '' : ' AND `d`.`did` IN (select data from data_quality dq where quality="NumberOfInstances" and value ' . (strpos($nr_insts, '..') !== false ? 'BETWEEN ' . str_replace('..',' AND ',$nr_insts) : '= '. $nr_insts) . ') ';
    $where_feats = $nr_feats === null ? '' : ' AND `d`.`did` IN (select data from data_quality dq where quality="NumberOfFeatures" and value ' . (strpos($nr_feats, '..') !== false ? 'BETWEEN ' . str_replace('..',' AND ',$nr_feats) : '= '. $nr_feats) . ') ';
    $where_class = $nr_class === null ? '' : ' AND `d`.`did` IN (select data from data_quality dq where quality="NumberOfClasses" and value ' . (strpos($nr_class, '..') !== false ? 'BETWEEN ' . str_replace('..',' AND ',$nr_class) : '= '. $nr_class) . ') ';
    $where_miss = $nr_miss === null ? '' : ' AND `d`.`did` IN (select data from data_quality dq where quality="NumberOfMissingValues" and value ' . (strpos($nr_miss, '..') !== false ? 'BETWEEN ' . str_replace('..',' AND ',$nr_miss) : '= '. $nr_miss) . ') ';
    $status_sql_variable = 'IFNULL(`s`.`status`, \'' . $this->config->item('default_dataset_status') . '\')';
    $where_status = $status === null ? ' AND ' . $status_sql_variable . ' = "active" ' : ($status != "all" ? ' AND ' . $status_sql_variable . ' = "'. $status . '" ' : '');

    $where_total = $where_type . $where_tag . $where_data_tag . $where_status . $where_task_id . $where_did . $where_data_name . $where_insts . $where_feats . $where_class . $where_miss;
    $where_task_total = $where_type . $where_tag;

    $where_limit = $limit === null ? '' : ' LIMIT ' . $limit;
    if($limit && $offset){
      $where_limit =  ' LIMIT ' . $offset . ',' . $limit;
    }

    // JvR: This query is bound to break in the near future, due to scalability
    $core = 'SELECT `t`.`task_id` , `t`.`ttid` , `tt`.`name` , `d`.`did` AS `did` , ' . $status_sql_variable . ' AS `status`, `d`.`format` , `d`.`name` AS `dataset_name` , CONCAT("{", GROUP_CONCAT(CONCAT("\"",`ti`.`input`,"\":\"", `ti`.`value`, "\"")), "}") AS task_inputs ' .
            'FROM `task` `t` , `task_type` `tt` , `task_inputs` `ti` , `task_inputs` `source` , `dataset` `d` ' .
            'LEFT JOIN (SELECT `did`, MAX(`status`) AS `status` FROM `dataset_status` GROUP BY `did`) s ON d.did = s.did ' .
            'WHERE `ti`.`task_id` = `t`.`task_id` AND `source`.`input` = "source_data" ' .
            'AND `source`.`task_id` = `t`.`task_id` AND `source`.`value` = `d`.`did` AND (`d`.`visibility` = "public" OR `d`.`uploader` = ' . $user_id . ')' .
            'AND `tt`.`ttid` = `t`.`ttid` AND `ti`.`input` IN ("' . implode('", "', $this->config->item('basic_taskinputs')).'") ' .
            $where_total . ' ' .
            'GROUP BY t.task_id ' . $where_limit;
    $dq = 'SELECT * FROM data_quality WHERE quality IN ("' . implode('", "', $this->config->item('basic_qualities')).'") AND evaluation_engine_id = ' . $this->config->item('default_evaluation_engine_id');
    $full = 'SELECT core.*, CONCAT("{", GROUP_CONCAT(CONCAT("\"",quality,"\":\"", value, "\"")), "}") AS `task_qualities` FROM (' . $dq . ') dq RIGHT JOIN (' . $core . ') core ON dq.data = core.did GROUP BY core.task_id;';

    $tasks_res = $this->Task->query($full);

    if(is_array($tasks_res) == false || count($tasks_res) == 0) {
      $this->returnError(482, $this->version);
      return;
    }

    $this->xmlContents( 'tasks', $this->version, array( 'tasks' => $tasks_res ) );
  }


  private function task($task_id) {
    $task = $this->Task->getById($task_id);
    if($task === false) {
      $this->returnError(151, $this->version);
      return;
    }

    $task_type = $this->Task_type->getById($task->ttid);
    if ($task_type === false) {
      $this->returnError(152, $this->version);
      return;
    }

    $inputs = $this->Task_inputs->getAssociativeArray('input', 'value', 'task_id = ' . $task_id);


    $parsed_io = $this->Task_type_inout->getParsed($task_id);
    $tags = $this->Task_tag->getColumnWhere('tag', 'id = ' . $task_id);

    $name = 'Task ' . $task_id . ' (' . $task_type->name . ')';

    if (array_key_exists('source_data', $inputs)) {
      $dataset = $this->Dataset->getById($inputs['source_data']);
      $name = 'Task ' . $task_id . ': ' . $dataset->name . ' (' . $task_type->name . ')';
    }


    $this->xmlContents('task-get', $this->version, array('task' => $task, 'task_type' => $task_type, 'name' => $name, 'parsed_io' => $parsed_io, 'tags' => $tags));
  }

  private function task_inputs($task_id) {
    $task = $this->Task->getById($task_id);
    if($task === false) {
      $this->returnError(156, $this->version);
      return;
    }

    $inputs = $this->Task_inputs->getAssociativeArray('input', 'value', 'task_id = ' . $task_id);
    if ($inputs === false) {
      $this->returnError(157, $this->version);
      return;
    }

    $this->xmlContents('task-inputs', $this->version, array('task' => $task, 'inputs' => $inputs));
  }

  private function task_delete($task_id) {

    $task = $this->Task->getById($task_id);
    if ($task == false) {
      $this->returnError(452, $this->version);
      return;
    }
    
    
    if ($task->creator != $this->user_id and !$this->user_has_admin_rights) {
      $this->returnError(453, $this->version);
      return;
    }
    
    $runs = $this->Run->getWhere('task_id = "' . $task->task_id . '"');

    if ($runs) {
      $this->returnError(454, $this->version);
      return;
    }


    $result = true;
    $result = $result && $this->Task_inputs->deleteWhere('task_id = ' . $task->task_id);

    if ($result) {
      $result = $this->Task->delete($task->task_id);
    }

    if ($result == false) {
      $this->returnError(455, $this->version);
      return;
    }

    try {
      $this->elasticsearch->delete('task', $task_id);
      $this->elasticsearch->index('user', $this->user_id);
    } catch (Exception $e) {
      $additionalMsg = get_class() . '.' . __FUNCTION__ . ':' . $e->getMessage();
      $this->returnError(105, $this->version, $this->openmlGeneralErrorCode, $additionalMsg);
      return;
    }

    $this->xmlContents( 'task-delete', $this->version, array( 'task' => $task ) );
  }

  public function task_upload() {

    $description = isset( $_FILES['description'] ) ? $_FILES['description'] : false;
    if( ! check_uploaded_file( $description ) ) {
      $this->returnError(611, $this->version);
      return;
    }

    $descriptionFile = $_FILES['description']['tmp_name'];

    $xsd = xsd('openml.task.upload', $this->controller, $this->version);
    if (!$xsd) {
      $this->returnError(612, $this->version, $this->openmlGeneralErrorCode);
      return;
    }

    if( validateXml( $descriptionFile, $xsd, $xmlErrors ) == false ) {
      // TODO: do later!
      $this->returnError(613, $this->version, $this->openmlGeneralErrorCode, $xmlErrors);
      return;
    }

    $xml = simplexml_load_file($descriptionFile);

    $task_type_id = intval($xml->children('oml', true)->{'task_type_id'});
    $inputs = array();
    $tags = array();
    
    foreach($xml->children('oml', true) as $input) {
      $input_value = $input . '';
      if ($input->getName() == 'input') {
        $name = $input->attributes() . '';
        
        // check if input is no duplicate
        if (array_key_exists($name, $inputs)) {
          $this->returnError(617, $this->version, 
                             $this->openmlGeneralErrorCode, 
                             'problematic input: ' . $name);
          return;
        }
        
        $inputs[$name] = $input_value;
      } elseif ($input->getName() == 'tag') {
        $tags[] = $input_value;
      }
    }
    $replace_array = $this->Task_type_inout->prepareRegex($inputs, $task_type_id);
    
    // for legal input check
    $legal_inputs = $this->Task_type_inout->getAssociativeArray('name', 'requirement', 'ttid = ' . $task_type_id . ' AND io = "input"');
    // for required input check
    $required_inputs = $this->Task_type_inout->getAssociativeArray('name', 'requirement', 'ttid = ' . $task_type_id . ' AND io = "input" AND requirement = "required"');
    $input_constraints = $this->Task_type_inout->getAssociativeArray('name', 'api_constraints', 'ttid = ' . $task_type_id . ' AND io = "input"');

    foreach($inputs as $name => $input_value) {
      // check if input is legal
      if (array_key_exists($name, $legal_inputs) == false) {
        $this->returnError(616, $this->version, $this->openmlGeneralErrorCode, 'problematic input: ' . $name);
        return;
      }
      
      $constraints = json_decode($input_constraints[$name]);
      if ($constraints == false) {
        $this->returnError(619, $this->version, $this->openmlGeneralErrorCode, 'problematic input: ' . $name);
        return;
      }
      
      // is_json lives in text_helper
      $type_check_mappings = array('numeric' => 'is_numeric', 'json' => 'is_json', 'string' => 'is_string');
      if (!property_exists($constraints, 'data_type') || !array_key_exists($constraints->data_type, $type_check_mappings)) {
        $this->returnError(620, $this->version, $this->openmlGeneralErrorCode, 'problematic input: ' . $name);
        return;
      }
      
      // check the type of the input
      $function = $type_check_mappings[$constraints->data_type];
      if ($function($input_value) == false) {
        $this->returnError(621, $this->version, $this->openmlGeneralErrorCode, 'problematic input: ' . $name . '; should be of type: ' . $constraints->data_type);
        return;
      }
      
      if (property_exists($constraints, 'select')) {
        $this->db->select($constraints->select)->from($constraints->from);
        if (property_exists($constraints, 'where')) {
          // TODO: format where
          $where = str_replace(array_keys($replace_array), array_values($replace_array), $constraints->where);
          $this->db->where($where);
        }
        
        $acceptable_inputs = array();
        foreach ($this->db->get()->result() as $obj) {
          $acceptable_inputs[] = $obj->{$constraints->select};
        }
        
        if (!in_array($input_value, $acceptable_inputs)) {
          $this->returnError(622, $this->version, $this->openmlGeneralErrorCode, 'problematic input: ' . $name);
          return;
        }
      }
      
      // hard constraint. If it is mapped to a dataset, the dataset should be active. TODO: review conditions
      if ($name == 'source_data' /*|| (trim($constraints['select']) == 'did' && trim($constraints['from'] == 'dataset'))*/) {
        $status_record = $this->Dataset_status->getWhereSingle('did = ' . $input_value, '`status` DESC');
        if (!$status_record || $status_record->status != 'active') {
          $this->returnError(623, $this->version, $this->openmlGeneralErrorCode, 'problematic input: ' . $name);
          return;
        }
      }
      
      // maybe a required input is satisfied
      unset($required_inputs[$name]);
    } 

    // required inputs should be empty by now
    if (count($required_inputs) > 0) {
      $this->returnError(618, $this->version, $this->openmlGeneralErrorCode, 'problematic input(s): ' . implode(', ', array_keys($required_inputs)));
      return;
    }

    $search = $this->Task->search($task_type_id, $inputs);
    if ($search) {
      $task_ids = array();
      foreach($search as $s) { $task_ids[] = $s->task_id; }

      $this->returnError(614, $this->version, $this->openmlGeneralErrorCode, 'matched id(s): [' . implode(',', $task_ids) . ']');
      return;
    }

    // THE INSERTION
    $task = array(
      'ttid' => '' . $task_type_id,
      'creator' => $this->user_id,
      'creation_date' => now()
    );

    $id = $this->Task->insert($task);
    // TODO: sanity check on input data!

    if ($id == false) {
      $this->returnError( 615, $this->version );
      return;
    }


    foreach($inputs as $name => $value) {
      $task_input = array(
        'task_id' => $id,
        'input' => $name,
        'value' => $value
      );
      $this->Task_inputs->insert($task_input);
    }

    foreach($tags as $tag) {
      $success = $this->entity_tag_untag('task', $id, $tag, false, 'task', false);
      // if tagging went wrong, an error is displayed. (TODO: something else?)
      if (!$success) return;
    }

    // update elastic search index.
    try {
      // TODO: uncomment when fixed
      $this->elasticsearch->index('task', $id);
      $this->elasticsearch->index('user', $this->user_id);
    } catch (Exception $e) {
      $additionalMsg = get_class() . '.' . __FUNCTION__ . ':' . $e->getMessage();
      $this->returnError(105, $this->version, $this->openmlGeneralErrorCode, $additionalMsg);
      return;
    }

    $this->xmlContents( 'task-upload', $this->version, array( 'id' => $id ) );
  }
}
?>
