<?php
class Api_estimationprocedure extends MY_Api_Model {

  protected $version = 'v1';

  function __construct() {
    parent::__construct();

    // load models
    $this->load->model('Estimation_procedure');
  }
  
  function bootstrap($format, $segments, $request_type, $user_id) {
    $this->outputFormat = $format;
    
    if (count($segments) == 1 && $segments[0] == 'list') {
      $this->estimationprocedure_list();
      return;
    }

    if (count($segments) == 1 && is_numeric($segments[0]) && in_array($request_type, $getpost)) {
      $this->estimationprocedure($segments[0]);
      return;
    }

    $this->returnError( 100, $this->version );
  }


  private function estimationprocedure($id) {
    if( $id == false ) {
      $this->returnError( 730, $this->version );
      return;
    }

    $ep = $this->Estimation_procedure->getById( $id );
    if( $ep == false ) {
      $this->returnError( 731, $this->version );
      return;
    }
    $this->xmlContents( 'estimationprocedure-get', $this->version, array( 'ep' => $ep ) );
  }
  private function estimationprocedure_list() {

    $estimationprocedures = $this->Estimation_procedure->get();
    if( $estimationprocedures == false ) {
      $this->returnError( 500, $this->version );
      return;
    }
    $this->xmlContents( 'estimationprocedures', $this->version, array( 'eps' => $estimationprocedures ) );
  }
}
?>
