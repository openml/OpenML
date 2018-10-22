<?php
class Api_tasktype extends MY_Api_Model {

  protected $version = 'v1';

  function __construct() {
    parent::__construct();

    // load models
    $this->load->model('Task_type');
    $this->load->model('Task_type_inout');
  }

  function bootstrap($format, $segments, $request_type, $user_id) {
    $this->outputFormat = $format;
    
    $getpost = array('get','post');

    if (count($segments) == 1 && $segments[0] == 'list') {
      $this->tasktype_list();
      return;
    }

    if (count($segments) == 1 && is_numeric($segments[0]) && in_array($request_type, $getpost)) {
      $this->tasktype($segments[0]);
      return;
    }

    $this->returnError( 100, $this->version );
  }

  private function tasktype_list() {
    $data = new stdClass();
    $data->task_types = $this->Task_type->get();
    $this->xmlContents( 'task-types', $this->version, $data );
  }

  private function tasktype($task_type_id) {
    if ($task_type_id == false) {
      $this->returnError(240, $this->version);
      return;
    }
    
    $taskType = $this->Task_type->getById($task_type_id);
    if ($taskType === false) {
      $this->returnError(241, $this->version);
      return;
    }
    
    $taskTypeIos = $this->Task_type_inout->getWhere('io = "input" AND ttid = ' . $task_type_id, 'order ASC');
    for ($i = 0; $i < count($taskTypeIos); ++$i) {
      if ($taskTypeIos[$i]->api_constraints) {
        $taskTypeIos[$i]->api_constraints = json_decode($taskTypeIos[$i]->api_constraints);
      } 
    }
    
    $this->xmlContents('task-types-search', $this->version, array( 'task_type' => $taskType, 'io' => $taskTypeIos));
  }
}
?>
