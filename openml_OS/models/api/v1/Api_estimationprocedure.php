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
    $getpost = array('get', 'post');

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

  /**
   *@OA\Get(
   *	path="/estimationprocedure/list",
   *	tags={"estimationprocedure"},
   *	summary="List all estimation procedures",
   *	description="Returns an array with all model performance estimation procedures in the system.",
   *	@OA\Parameter(
   *		name="api_key",
   *		in="query",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="API key to authenticate the user",
   *		required=false,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="A list of estimation procedures",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/EstimationProcedureList",
   *			example={
   *			  "estimationprocedures": {
   *			    "estimationprocedure": {
   *			      {
   *			         "id":"1",
   *			         "ttid":"1",
   *			         "name":"10-fold Crossvalidation",
   *			         "type":"crossvalidation",
   *			         "repeats":"1",
   *			         "folds":"10",
   *			         "stratified_sampling":"true"
   *			      },
   *			      {
   *			        "id":"2",
   *			        "ttid":"1",
   *			        "name":"5 times 2-fold Crossvalidation",
   *			        "type":"crossvalidation",
   *			        "repeats":"5",
   *			        "folds":"2",
   *			        "stratified_sampling":"true"
   *			      }
   *			    }
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n500 - No model performance estimation procedures available.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
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
