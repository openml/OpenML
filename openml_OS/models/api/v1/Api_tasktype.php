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

    /**
     *@OA\Get(
     *	path="/tasktype/list",
     *	tags={"tasktype"},
     *	summary="List all task types",
     *	description="Returns an array with all task types in the system.",
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
     *		response="default",
     *		description="Unexpected error",
     *		@OA\JsonContent(
     *			ref="#/components/schemas/Error",
     *		),
     *	),
     *	@OA\Response(
     *		response=200,
     *		description="A task description",
     *		@OA\JsonContent(
     *			ref="#/components/schemas/TaskTypeList",
     *			example={
     *			  "task_types":{
     *			    "task_type":{
     *			      {
     *			        "id":"1",
     *			        "name":"Supervised Classification",
     *			        "description":"In supervised classification, you are given ...",
     *			        "creator":"Joaquin Vanschoren, Jan van Rijn, Luis Torgo, Bernd Bischl"
     *			      },
     *			      {
     *			        "id":"2",
     *			        "name":"Supervised Regression",
     *			        "description":"Given a dataset with a numeric target ...",
     *			        "creator":"Joaquin Vanschoren, Jan van Rijn, Luis Torgo, Bernd Bischl"
     *			      }
     *			    }
     *			  }
     *			}
     *		),
     *	),
     *)
     */
    if (count($segments) == 1 && $segments[0] == 'list') {
      $this->tasktype_list();
      return;
    }

    /**
     *@OA\Get(
     *	path="/tasktype/{id}",
     *	tags={"tasktype"},
     *	summary="Get task type description",
     *	description="Returns information about a task type. The information includes a description, the given inputs and the expected outputs.",
     *	@OA\Parameter(
     *		name="id",
     *		in="path",
     *		@OA\Schema(
     *          type="integer"
     *        ),
     *		description="ID of the task.",
     *		required=true,
     *	),
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
     *		description="A task type description",
     *		@OA\JsonContent(
     *			ref="#/components/schemas/TaskType",
     *			example={
     *			  "task_type": {
     *			    "id": "1",
     *			    "name": "Supervised Classification",
     *			    "description": "In supervised classification, you are given an input dataset in which instances are labeled with a certain class. The goal is to build a model that predicts the class for future unlabeled instances. The model is evaluated using a train-test procedure, e.g. cross-validation.<br><br>\
     *			\
     *			To make results by different users comparable, you are given the exact train-test folds to be used, and you need to return at least the predictions generated by your model for each of the test instances. OpenML will use these predictions to calculate a range of evaluation measures on the server.<br><br>\
     *			\
     *			You can also upload your own evaluation measures, provided that the code for doing so is available from the implementation used. For extremely large datasets, it may be infeasible to upload all predictions. In those cases, you need to compute and provide the evaluations yourself.<br><br>\
     *			\
     *			Optionally, you can upload the model trained on all the input data. There is no restriction on the file format, but please use a well-known format or PMML.",
     *			    "creator": {
     *			      "Joaquin Vanschoren",
     *			      "Jan van Rijn",
     *			      "Luis Torgo",
     *			      "Bernd Bischl"
     *			    },
     *			    "contributor": {
     *			      "Bo Gao",
     *			      "Simon Fischer",
     *			      "Venkatesh Umaashankar",
     *			      "Michael Berthold",
     *			      "Bernd Wiswedel",
     *			      "Patrick Winter"
     *			    },
     *			    "creation_date": "2013-01-24 00:00:00",
     *			    "input": {
     *			      {
     *			        "name": "source_data",
     *			        "requirement": "required",
     *			        "data_type": "numeric"
     *			      },
     *			      {
     *			        "name": "target_feature",
     *			        "requirement": "required",
     *			        "data_type": "string"
     *			      },
     *			      {
     *			        "name": "estimation_procedure",
     *			        "requirement": "required",
     *			        "data_type": "numeric"
     *			      },
     *			      {
     *			        "name": "cost_matrix",
     *			        "data_type": "json"
     *			      },
     *			      {
     *			        "name": "custom_testset",
     *			        "data_type": "json"
     *			      },
     *			      {
     *			        "name": "evaluation_measures",
     *			        "data_type": "string"
     *			      }
     *			    }
     *			  }
     *			}
     *		),
     *	),
     *	@OA\Response(
     *		response=412,
     *		description="Precondition failed. An error code and message are returned.\n240 - Please provide task type ID.\n241 - Unknown task type. The task type with the given id was not found in the database\n",
     *		@OA\JsonContent(
     *			ref="#/components/schemas/Error",
     *		),
     *	),
     *)
     */
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
