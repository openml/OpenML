<?php
class Api_new extends CI_Controller {

  private $version;

  function __construct() {
    parent::__construct();

    $this->controller = 'api_new';
    $this->page = 'xml';
    $this->active = 'learn';
    
    $this->load->model('Database_singleton');
    
    //$this->load->model('api/v1/Api_test');
    $this->load->model('api/v1/Api_data');
    $this->load->model('api/v1/Api_votes');
    $this->load->model('api/v1/Api_downloads');
    $this->load->model('api/v1/Api_gamification');
    $this->load->model('api/v1/Api_badges');
    $this->load->model('api/v1/Api_task');
    $this->load->model('api/v1/Api_tasktype');
    $this->load->model('api/v1/Api_flow');
    $this->load->model('api/v1/Api_run');
    $this->load->model('api/v1/Api_setup');
    $this->load->model('api/v1/Api_evaluation');
    $this->load->model('api/v1/Api_estimationprocedure');
    $this->load->model('api/v1/Api_evaluationmeasure');
    $this->load->model('api/v1/Api_file');
    $this->load->model('api/v1/Api_job');
    $this->load->model('api/v1/Api_user');
    $this->load->model('api/v1/Api_study');
    
    $this->load->model('Study');
    $this->load->model('Math_function');

    $this->load->model('Log');
    $this->load->model('Author');

    // helper
    $this->load->helper('api');

    $this->load->Library('session');
    $this->load->Library('ion_auth');
    $this->load->library('elasticSearch');
    $this->load->library('wiki');

    $this->groups_upload_rights = array(1,2); // must be part of this group to upload stuff
    $this->groups_admin = array(1); // must be part of this group to do really important stuff

    // XML maintainance. TODO: remove and place in appropriate models
    $this->xml_fields_dataset = $this->config->item('xml_fields_dataset');
    $this->xml_fields_features = $this->config->item('xml_fields_features');
    $this->xml_fields_dataset_update = $this->config->item('xml_fields_dataset_update');
    $this->xml_fields_implementation = $this->config->item('xml_fields_implementation');
    $this->xml_fields_run = $this->config->item('xml_fields_run');
    $this->xml_fields_study = $this->config->item('xml_fields_study');

    $this->data_controller = $this->config->item('data_controller');
    
    
    // ------------- EVERYTHING BELOW SHOULD NOT BE IN CONSTRUCTOR -------------
    //               (because of undesired return statement)
    $this->database_connection_error = true;
    if ($this->Database_singleton->connected()) {
      $this->database_connection_error = false;
    } else {
      return;
    }

    // some user authentication things. 
    // used the stfu operator as CI throws notice otherwise
    // (http://php.net/manual/en/language.operators.errorcontrol.phps)
    $getPostHash = @$this->input->get_post('api_key');
    $this->provided_hash = $getPostHash != false;
    $this->provided_valid_hash = $this->Author->getWhere('session_hash = "' . $getPostHash . '"'); // TODO: and add date?
    $this->authenticated = $this->provided_valid_hash || $this->ion_auth->logged_in();
    $this->user_id = false;
    $this->user_email = false;
    if ($this->provided_hash && $this->provided_valid_hash) {
      $this->user_id = $this->provided_valid_hash[0]->id;
      $this->user_email = $this->ion_auth->user($this->user_id)->row()->email;
    } elseif ($this->ion_auth->logged_in()) {
      $this->user_id = $this->ion_auth->user()->row()->id;
      $this->user_email = $this->ion_auth->user()->row()->email;
    } else {
      // not always neccessary 
      $this->user_id = -1;
      $this->user_email = null;
    }
    
    // determine the writing and admin rights. 
    $user_groups = $this->ion_auth->get_users_groups($this->user_id)->result();
    $this->user_has_writing_rights = false;
    $this->user_has_admin_rights = false;
    // fetch user groups
    foreach ($user_groups as $group) {
      if (in_array($group->id,  $this->groups_upload_rights)) {
        $this->user_has_writing_rights = true;
      }
      if (in_array($group->id, $this->groups_admin)) {
        $this->user_has_admin_rights = true;
      }
    }
    
    $this->openmlGeneralErrorCode = $this->config->item('general_http_error_code');
    $this->supportedMetrics = $this->Math_function->getColumnWhere('name', 'functionType = "EvaluationFunction"');
    
    // in the case of session hash authentication, destroy the session. 
    // No ION AUTH usage after this point!!
    if ($this->provided_valid_hash && !$this->ion_auth->logged_in()) {
      $this->session->sess_destroy();
    }
  }

  public function index() {
    $this->_show_webpage();
  }

  public function v1($type) {
    $this->bootstrap('1');
  }

  private function bootstrap($version) {
    $outputFormats = array('xml','json');
    
    loadpage('v'.$version.'/'.$this->page,false,'pre');
    $segs = $this->uri->segment_array();

    $controller = array_shift($segs);
    $this->version = array_shift($segs);
    $type = array_shift($segs);
    $outputFormat = 'xml';

    // TODO: currently, we support the possibility of absent
    // $outputFormat field, which should be mandatory in the future.
    if (in_array($type,$outputFormats)) {
      $outputFormat = $type;
      $type = array_shift($segs);
    }
    
    // TODO: very important (for future versions of the API)! 
    if ($this->database_connection_error) {
      $this->Api_data->returnError(107, $this->version);
      return;
    }
    
    $request_type = strtolower($_SERVER['REQUEST_METHOD']);
    if ($this->authenticated == false && $request_type != 'get') {
      if ($this->provided_hash) {
        $this->Api_data->returnError(103, $this->version);
      } else {
        $this->Api_data->returnError(102, $this->version);
      }
    } else if ($this->authenticated == false && $this->provided_hash) {
        $this->Api_data->returnError(103, $this->version);
    } else if ($this->user_has_writing_rights == false && $request_type != 'get') {
      $this->Api_data->returnError(104, $this->version, $this->openmlGeneralErrorCode, 'API calls of the read-only user can only be of type GET. ');
    } else if (file_exists(APPPATH.'models/api/' . $this->version . '/Api_' . $type . '.php') == false && $type != 'xsd' && $type != 'xml_example') {
       $this->Api_data->returnError(100, $this->version);
    } else if($type == 'xsd') {
      $this->xsd($segs[0], 'v1');
    } else if($type == 'xml_example') {
      $this->xml_example($segs[0], 'v1');
    } else {
      $this->{'Api_'.$type}->bootstrap($outputFormat, $segs, $request_type, $this->user_id);
    }
  }

  private function xsd($filename,$version) {
    $filepath = APPPATH.'views/pages/' . $this->controller . '/' . $version . '/xsd/' . $filename . '.xsd';
    if(is_safe($filename) && file_exists($filepath)) {
      header('Content-type: text/xml; charset=utf-8');
      echo file_get_contents($filepath);
    } else {
      $this->error404();
    }
  }

  private function xml_example($filename,$version) {
    $filepath = APPPATH.'views/pages/' . $this->controller . '/' . $version . '/xml_example/' . $filename . '.xml';
    if(is_safe($filename) && file_exists($filepath)) {
      header('Content-type: text/xml; charset=utf-8');
      echo file_get_contents($filepath);
    } else {
      $this->error404();
    }
  }

  public function error404() {
    header("Status: 404 Not Found");
    $this->load->view('404');
  }


  /************************************* DISPLAY *************************************/

  private function _show_webpage() {
    $this->page = 'api_docs';
    if(!loadpage($this->page,true,'pre'))
      $this->page_body = '<div class="alertbox col-md-12"><div class="alert alert-dismissible alert-warning">API request unknown. Please check the <a href="api_docs">API Documentation</a>.</div></div>';
    $this->load->view('frontend_main');
  }
}
?>
