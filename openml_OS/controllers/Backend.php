<?php

class Backend extends CI_Controller {

  function __construct() {
    parent::__construct();

    $this->load->model('Algorithm_setup');
    $this->load->model('File');
    $this->load->model('Dataset');
    $this->load->model('Data_quality');
    $this->load->model('Implementation');
    $this->load->model('Math_function');
    $this->load->model('Schedule');
    $this->load->model('Study');
    $this->load->model('Task');
    $this->load->model('Task_type');
    $this->load->model('Task_type_inout');
    $this->load->model('Estimation_procedure');
    $this->load->model('Run');
    
    $this->load->model('Author');

    $this->load->helper('table');
    $this->load->helper('tasksearch');
    $this->load->helper('directory');

    $this->load->Library('ion_auth');
    $this->load->Library('session');
    $this->load->Library('curlHandler');
    $this->load->Library('elasticSearch');
    $this->load->Library('wiki');
    $this->load->Library('dataOverview');

    $this->controller = strtolower(get_class ($this));
    $this->query_string = $this->uri->uri_to_assoc(2);
    $this->data_controller = $this->config->item('data_controller');

    $this->page = 'home'; // default value

    // login is mandatory
    if (!$this->ion_auth->logged_in()) {
	    header('Location: ' . BASE_URL . 'login');
    }

    if(!$this->ion_auth->is_admin()) {
      die('Backend pages only available for admin users.');
    }
  }

  public function index() {
    $this->page( $this->page );
  }

  public function page( $indicator ) {
    $this->page = $indicator;
    $exploded_page = explode('_',$indicator);
    $this->active = $exploded_page[0]; // can be overridden.
    $this->message = $this->session->flashdata('message'); // can be overridden

    if(!loadpage($indicator,TRUE,'pre')) {
      $this->error404();
      return;
    }
    if($_POST) loadpage($indicator,TRUE,'post');

	  $this->load->view('frontend_main'); // frontend main will do fine for now.
  }

  public function error404() {
    header("Status: 404 Not Found");
    $this->load->view('404');
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
