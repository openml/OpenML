<?php
class Api_job extends Api_model {
  
  protected $version = 'v1';
  
  function __construct() {
    parent::__construct();
    
    // load models
    $this->load->model('Schedule');
  }

  function bootstrap($format, $segments, $request_type, $user_id) {
    $this->outputFormat = $format;
    
    $getpost = array('get','post');
    
    if (count($segments) == 1 && $segments[0] == 'request') {
      $this->job_request();
      return;
    }
    
    $this->returnError( 100, $this->version );
  }
  
  
  private function job_request() {
    $workbench = safe($this->input->get_post('workbench'));
    $task_type_id = safe($this->input->get_post('task_type_id'));
    $task_tag = safe(@$this->input->get_post('task_tag'));
    $setup_tag = safe(@$this->input->get_post('setup_tag'));
    $setup_id = safe(@$this->input->get_post('setup_id'));

    if($workbench == false || $task_type_id == false) {
      $this->returnError(340, $this->version);
      return;
    }

    $job = $this->Schedule->getJob($workbench, $task_type_id, $task_tag, $setup_tag, $setup_id);

    if($job == false) {
      $this->returnError(341, $this->version);
      return;
    }

    $this->xmlContents('run-getjob', $this->version, array('source' => $job));
  }
}
?>
