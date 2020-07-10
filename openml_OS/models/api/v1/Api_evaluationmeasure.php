<?php

class Api_evaluationmeasure extends MY_Api_Model {
  
  protected $version = 'v1';
  
  function __construct() {
    parent::__construct();
    
    // load models
    $this->load->model('Math_function');
  }
  
  function bootstrap($format, $segments, $request_type, $user_id) {
    $getpost = array('get','post');
    
    $this->outputFormat = $format;

    if (count($segments) == 1 && $segments[0] == 'list') {
      $this->evaluationmeasure_list();
      return;
    }
    
    
    $this->returnError( 100, $this->version );
  }

  /**
   *@OA\Get(
   *	path="/evaluationmeasure/list",
   *	tags={"evaluationmeasure"},
   *	summary="List all evaluation measures",
   *	description="Returns an array with all model evaluation measures in the system.",
   *	@OA\Response(
   *		response=200,
   *		description="A list of evaluation measures",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/EvaluationMeasureList",
   *			example={
   *			  "evaluation_measures":{
   *			    "measures":{
   *			      "measure":{
   *			        "area_under_roc_curve",
   *			        "average_cost",
   *			        "binominal_test",
   *			        "build_cpu_time"
   *			        }
   *			    }
   *			  }
   *			}
   *		),
   *	),
   *)
   */
  private function evaluationmeasure_list() {
    $data = new stdClass();
    $data->measures = $this->Math_function->getWhere( 'functionType = "EvaluationFunction"' );
    $this->xmlContents( 'evaluation-measures', $this->version, $data );
  }
}
?>
