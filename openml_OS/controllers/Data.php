<?php
class Data extends CI_Controller {

  function __construct() {
    parent::__construct();

    $this->controller = strtolower(get_class ($this));
    $this->nr_segments = count($this->uri->segments);
    
    $this->load->Library('ion_auth');
    $this->load->Model('Author');
    
    // authentication
    $getPostHash = @$this->input->get_post('api_key');
    $this->provided_hash = $getPostHash != false;
    $this->provided_valid_hash = $this->Author->getWhere('session_hash = "' . $getPostHash . '"'); // TODO: and add date?
    $this->authenticated = $this->provided_valid_hash || $this->ion_auth->logged_in();
    $this->user_id = false;
    if($this->provided_valid_hash) {
      $this->user_id = $this->provided_valid_hash[0]->id;
    } elseif($this->ion_auth->logged_in()) {
      $this->user_id = $this->ion_auth->user()->row()->id;
    }
    
    // in the case of session hash authentication, destroy the session (ION AUTH Can't be used anymore)
    if ($this->provided_valid_hash && !$this->ion_auth->logged_in()) {
      $this->session->sess_destroy();
    }
  }
  
  public function v1($type) {
    $this->load->Model('data/v1/Data_server');
    $this->bootstrap('1');
  }
  
  private function bootstrap($version) {
    $segs = $this->uri->segment_array();

    $controller = array_shift($segs);
    $this->version = array_shift($segs);
    $function = array_shift($segs);
    
    call_user_func_array(array($this->Data_server, $function), $segs);
  }
  
  /* LEGACY START */
  function download($id, $name = 'undefined') {
    $this->load->Model('data/v1/Data_server');
    $this->Data_server->download($id, $name);
  }
  
  function view($id, $name = 'undefined') {
    $this->load->Model('data/v1/Data_server');
    $this->Data_server->view($id, $name);
  }
  
  function get_csv($id, $name = 'undefined') {
    $this->load->Model('data/v1/Data_server');
    $this->Data_server->get_csv($id, $name);
  }
}
?>
