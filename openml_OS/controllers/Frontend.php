<?php

class Frontend extends CI_Controller {

  function __construct() {
    parent::__construct();

    $this->load->model('File');
    $this->load->model('Dataset');
    $this->load->model('Data_quality');
    $this->load->model('Implementation');
    $this->load->model('Implementation_component');
    $this->load->model('Input');
    $this->load->model('Math_function');
    $this->load->model('Study');
    $this->load->model('Task');
    $this->load->model('Task_type');
    $this->load->model('Task_type_inout');
    $this->load->model('Estimation_procedure');
    $this->load->model('Run');
    $this->load->model('Schedule');
    $this->load->model('Algorithm_setup');

    $this->load->model('Dataset_tag');
    $this->load->model('Implementation_tag');
    $this->load->model('Setup_tag');
    $this->load->model('Task_tag');
    $this->load->model('Run_tag');

    $this->load->model('Meta_dataset');
    $this->load->model('Author');
    $this->load->model('Users');

    $this->load->helper('table');
    $this->load->helper('tasksearch');

    $this->load->Library('dataOverview');
    $this->load->Library('elasticSearch');
    $this->load->Library('wiki');
    $this->load->Library('curlHandler');
    $this->load->Library('ion_auth');
    $this->load->Library('session');

    $this->controller = strtolower(get_class ($this));
    $this->query_string = $this->uri->uri_to_assoc(2);
    $this->data_controller = $this->config->item('data_controller');

    $this->page = 'home'; // default value
    $this->subpage = false;

    $this->searchclient = $this->elasticsearch->client;
  }

  public function index() {
    $this->page( $this->page );
  }

  public function js($indicator,$subindicator = false) {
    $this->page = $indicator;
    $this->subpage = $subindicator;

    $exploded_page = explode('_',$indicator);
    $this->active = $exploded_page[0]; // can be overridden.
    $this->message = $this->session->flashdata('message'); // can be overridden

    if(!loadpage($indicator,TRUE,'pre')) {
      $this->error404();
      return;
    }
    loadpage($indicator,TRUE,'javascript');
  }

  public function page( $indicator, $subindicator = false ) {
    $this->page = $indicator;
    $this->subpage = $subindicator;
    $exploded_page = explode('_',$indicator);
    $this->active = $exploded_page[0]; // can be overridden.
    $this->message = $this->session->flashdata('message'); // can be overridden

    if(false === strpos($_SERVER['REQUEST_URI'],'/json') && false === strpos($_SERVER['REQUEST_URI'],'/rdf')){
      if(!loadpage($indicator,TRUE,'pre')) {
        $this->error404();
        return;
      }
      if($_POST) loadpage($indicator,TRUE,'post');
    }
    if(false !== strpos($_SERVER['REQUEST_URI'],'/html')){
	    $this->load->view('html_main');
    } elseif(false !== strpos($_SERVER['REQUEST_URI'],'/json')){
	    $this->load->view('json_main');
    } elseif(false !== strpos($_SERVER['REQUEST_URI'],'/rdf')){
	    $this->load->view('rdf_main');
    } elseif(false !== strpos($_SERVER['REQUEST_URI'],'/output')){
	    $this->load->view('output_main');
    } else {
	    $this->load->view('frontend_main');
    }
  }

  public function error404() {
    header("Status: 404 Not Found");
    $this->load->view('404');
  }

  public function logout() {
    $logout = $this->ion_auth->logout();
    $this->session->set_flashdata('message', $this->ion_auth->messages());
    redirect('home');
  }

  public function result_output() {
    $filetype  = $this->input->post('type');
    $filename  = preg_replace("/[^a-zA-Z0-9.-_]+/", "", $this->input->post('name') );
    $data    = json_decode( $this->input->post('data') );

    $allowedFiletypes = array( 'csv' );

    if( $filename == false ) $filename = 'results.' . $filetype;

    if( ( ! in_array( $filetype, $allowedFiletypes ) ) ) {
      header('Content-type: text/html' );
      die ( 'Unfortunately, an error has occured. ' );
    } else {
      header('Content-type: text/' . $filetype );
      header('Content-Disposition: attachment; filename="'.$filename.'"');

      foreach( $data->columns as $column ) {
        echo '"' . addslashes(safe(html_entity_decode($column->title))) . '",';
      }
      echo "\n";

      foreach( $data->data as $record ) {
        for( $i = 0; $i < count( $data->columns ); $i++ ) {
          echo '"' . addslashes(safe(html_entity_decode(str_replace("\n",'',$record[$i])))) . '",';
        }
        echo "\n";
      }
    }
  }
}
?>
