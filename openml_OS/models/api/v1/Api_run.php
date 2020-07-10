<?php


class Api_run extends MY_Api_Model {

  protected $version = 'v1';

  function __construct() {
    parent::__construct();

    // load models
    $this->load->model('Run');
    $this->load->model('Run_evaluated');
    $this->load->model('Dataset');
    $this->load->model('Run_tag');
    $this->load->model('Runfile');
    $this->load->model('Algorithm_setup');
    $this->load->model('Input_setting');
    $this->load->model('Output_data');
    $this->load->model('Input_data');
    $this->load->model('Task');
    $this->load->model('Task_inputs');
    $this->load->model('Author');
    $this->load->model('Implementation');
    $this->load->model('Trace');

    $this->load->model('Estimation_procedure');
    $this->load->model('Evaluation');
    $this->load->model('Evaluation_fold');
    $this->load->model('Evaluation_sample');

    $this->load->helper('arff');

    $this->load->model('File');

    $this->db = $this->Database_singleton->getWriteConnection();

    // Currently default
    $this->weka_engine_id = 1;
  }

  function bootstrap($format, $segments, $request_type, $user_id) {
    $this->outputFormat = $format;

    $getpost = array('get','post');

    if (count($segments) >= 1 && $segments[0] == 'list') {
      array_shift($segments);
      $this->run_list($segments, $user_id);
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'evaluate' && $request_type == 'post') {
      $this->run_evaluate();
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'trace' && $request_type == 'post') {
      $this->run_trace_upload();
      return;
    }

    if (count($segments) == 2 && $segments[0] == 'trace' && is_numeric($segments[1])) {
      $this->run_trace($segments[1]);
      return;
    }

    if (count($segments) == 1 && is_numeric($segments[0]) && in_array($request_type, $getpost)) {
      $this->run($segments[0]);
      return;
    }

    if (count($segments) == 2 && is_numeric($segments[1]) && $segments[0] == 'reset' && in_array($request_type, $getpost)) {
      $this->run_reset($segments[1]);
      return;
    }

    if (count($segments) == 1 && is_numeric($segments[0]) && $request_type == 'delete') {
      $this->run_delete($segments[0]);
      return;
    }

    if (count($segments) == 0 && $request_type == 'post') {
      $this->run_upload();
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'tag' && $request_type == 'post') {
      $this->run_tag($this->input->post('run_id'), $this->input->post('tag'));
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'untag' && $request_type == 'post') {
      $this->run_untag($this->input->post('run_id'), $this->input->post('tag'));
      return;
    }
    
    if (count($segments) == 2 && $segments[0] == 'tag' && $segments[1] == 'list') {
      $this->list_tags('run', 'run');
      return;
    }

    $this->returnError( 100, $this->version );
  }

  /**
   *@OA\Post(
   *	path="/run/tag",
   *	tags={"run"},
   *	summary="Tag a run",
   *	description="Tags a run.",
   *	@OA\Parameter(
   *		name="run_id",
   *		in="query",
   *		@OA\Schema(
   *          type="integer"
   *        ),
   *		description="Id of the run.",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="tag",
   *		in="query",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="Tag name",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="api_key",
   *		in="query",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="Api key to authenticate the user",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="The id of the tagged run",
   *		@OA\JsonContent(
   *			type="object",
   *			@OA\Property(
   *				property="run_tag",
   *				ref="#/components/schemas/inline_response_200_19_run_tag",
   *			),
   *			example={
   *			  "run_tag": {
   *			    "id": "2"
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n470 - In order to add a tag, please upload the entity id (either data_id, flow_id, run_id) and tag (the name of the tag).\n471 - Entity not found. The provided entity_id {data_id, flow_id, run_id} does not correspond to an existing entity.\n472 - Entity already tagged by this tag. The entity {dataset, flow, run} already had this tag.\n473 - Something went wrong inserting the tag. Please contact OpenML Team.\n474 - Internal error tagging the entity. Please contact OpenML Team.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function run_tag($run_id, $tag) {
    $this->run_tag_untag($run_id, $tag, false);
  }

  /**
   *@OA\Post(
   *	path="/run/untag",
   *	tags={"run"},
   *	summary="Untag a run",
   *	description="Untags a run.",
   *	@OA\Parameter(
   *		name="run_id",
   *		in="query",
   *		@OA\Schema(
   *          type="integer"
   *        ),
   *		description="Id of the run.",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="tag",
   *		in="query",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="Tag name",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="api_key",
   *		in="query",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="Api key to authenticate the user",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="The id of the untagged run",
   *		@OA\JsonContent(
   *			type="object",
   *			@OA\Property(
   *				property="run_untag",
   *				ref="#/components/schemas/inline_response_200_20_run_untag",
   *			),
   *			example={
   *			  "run_untag": {
   *			    "id": "2"
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n475 - Please give entity_id {data_id, flow_id, run_id} and tag. In order to remove a tag, please upload the entity id (either data_id, flow_id, run_id) and tag (the name of the tag).\n476 - Entity {dataset, flow, run} not found. The provided entity_id {data_id, flow_id, run_id} does not correspond to an existing entity.\n477 - Tag not found. The provided tag is not associated with the entity {dataset, flow, run}.\n478 - Tag is not owned by you. The entity {dataset, flow, run} was tagged by another user. Hence you cannot delete it.\n479 - Internal error removing the tag. Please contact OpenML Team.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function run_untag($run_id, $tag) {
    $this->run_tag_untag($run_id, $tag, true);
  }

  private function run_tag_untag($run_id, $tag, $do_untag) {
    $this->entity_tag_untag('run',$run_id, $tag, $do_untag, 'run');
  }

  /**
   *@OA\Get(
   *	path="/run/list/{filters}",
   *	tags={"run"},
   *	summary="List and filter runs",
   *	description="List runs, filtered by a range of properties. Any number of properties can be combined by listing them one after the other in the form '/run/list/{filter}/{value}/{filter}/{value}/...' Returns an array with all runs that match the constraints. A maximum of 10,000 results are returned, an error is returned if the result set is bigger. Use pagination (via limit and offset filters), or limit the results to certain tasks, flows, setups, or uploaders.",
   *	@OA\Parameter(
   *		name="filters",
   *		in="path",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="Any combination of these filters
  /tag/{tag} - return only runs tagged with the given tag.
  /run/{ids} - return only specific runs, specified as a comma-separated list of run IDs, e.g. ''1,2,3''
  /task/{ids} - return only runs on specific tasks, specified as a comma-separated list of task IDs, e.g. ''1,2,3''
  /flow/{ids} - return only runs on specific flows, specified as a comma-separated list of flow IDs, e.g. ''1,2,3''
  /setup/{ids} - return only runs with specific setups, specified as a comma-separated list of setup IDs, e.g. ''1,2,3''
  /uploader/{ids} - return only runs uploaded by specific users, specified as a comma-separated list of user IDs, e.g. ''1,2,3''
  /limit/{limit}/offset/{offset} - returns only {limit} results starting from result number {offset}. Useful for paginating results. With /limit/5/offset/10, results 11..15 will be returned. Both limit and offset need to be specified.
  ",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="A list of runs descriptions",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/RunList",
   *			example={"runs": {"run": {{"upload_time": "2014-04-06 23:30:40", "task_id": "28", "run_id": "100", "error_message": {}, "setup_id": "12", "uploader": "1", "flow_id": "67"}, {"upload_time": "2014-04-06 23:30:40", "task_id": "48", "run_id": "101", "error_message": {}, "setup_id": "6", "uploader": "1", "flow_id": "61"}, {"upload_time": "2014-04-06 23:30:40", "task_id": "41", "run_id": "102", "error_message": {}, "setup_id": "3", "uploader": "1", "flow_id": "58"}}}}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n510 - Please provide at least task, flow or setup, uploader or run, to filter results, or limit the number of responses. The number of runs is huge. Please limit the result space.\n511 - Input not safe. The input parameters (task_id, setup_id, flow_id, run_id, uploader_id) did not meet the constraints (comma separated list of integers).\n512 - There where no results. Check whether there are runs under the given constraint.\n513 - Too many results. Given the constraints, there were still too many results. Please add filters to narrow down the list.\n514 - Illegal filter specified.\n515 - Offset specified without limit.\n516 - Requested result limit too high.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function run_list($segs, $user_id) {
    $result_limit = 10000;
    $legal_filters = array('task', 'setup', 'flow', 'uploader', 'run', 'tag', 'limit', 'offset', 'task_type', 'study', 'show_errors');
    
    list($query_string, $illegal_filters) = $this->parse_filters($segs, $legal_filters);
    if (count($illegal_filters) > 0) {
      $this->returnError(514, $this->version, $this->openmlGeneralErrorCode, 'Legal filter operators: ' . implode(',', $legal_filters) .'. Found illegal filter(s): ' . implode(', ', $illegal_filters));
      return;
    }
    
    $illegal_filter_inputs = $this->check_filter_inputs($query_string, $legal_filters, array('tag', 'show_errors'));
    if (count($illegal_filter_inputs) > 0) {
      $this->returnError(511, $this->version, $this->openmlGeneralErrorCode, 'Filters with illegal values: ' . implode(',', $illegal_filter_inputs));
      return;
    }

    $task_id = element('task', $query_string, null);
    $task_type_id = element('task_type', $query_string, null);
    $setup_id = element('setup',$query_string, null);
    $implementation_id = element('flow',$query_string, null);
    $uploader_id = element('uploader',$query_string, null);
    $run_id = element('run',$query_string, null);
    $tag = element('tag',$query_string, null);
    $limit = element('limit',$query_string, null);
    $offset = element('offset',$query_string, null);
    $study_id = element('study', $query_string, null);
    $show_errors = element('show_errors',$query_string, null);
    
    if ($offset && !$limit) {
      $this->returnError(515, $this->version);
      return;
    }
    if ($limit && $limit > $result_limit) {
      $this->returnError(516, $this->version);
      return;
    }
    
    if ($study_id) {
      $study = $this->Study->getById($study_id);
      if ($study === false || $study->legacy != 'n' || $study->main_entity_type != 'run') {
        $this->returnError(517, $this->version);
        return;
      }
    }
    
    if ($task_id === null && $task_type_id === null && $setup_id === null && $implementation_id === null && $uploader_id === null && $run_id === null && $tag === null && $limit === null && $study_id === null) {
      $this->returnError(510, $this->version);
      return;
    }

    $where_task = $task_id === null ? '' : ' AND `r`.`task_id` IN (' . $task_id . ') ';
    $where_task_type = $task_type_id === null ? '' : ' AND `t`.`ttid` IN (' . $task_type_id . ') ';
    $where_setup = $setup_id === null ? '' : ' AND `r`.`setup` IN (' . $setup_id . ') ';
    $where_uploader = $uploader_id === null ? '' : ' AND `r`.`uploader` IN (' . $uploader_id . ') ';
    $where_impl = $implementation_id === null ? '' : ' AND `i`.`id` IN (' . $implementation_id . ') ';
    $where_run = $run_id === null ? '' : ' AND `r`.`rid` IN (' . $run_id . ') ';
    $where_tag = $tag === null ? '' : ' AND `r`.`rid` IN (select id from run_tag where tag="' . $tag . '") ';
    $where_study = $study_id === null ? '' : ' AND `r`.`rid` IN (SELECT `run_id` FROM `run_study` WHERE `study_id`="' . $study_id . '") ';
    // TODO: runs with errors are always removed?
    $where_server_error = ' AND `e`.`error` IS NULL ';
    if (strtolower($show_errors) == 'true') {
      $where_server_error = '';
    }
    // Don't return runs of closed runs, unless the user uploaded them
    $where_task_closed = ' AND (`t`.`embargo_end_date` is NULL OR `t`.`embargo_end_date` < NOW() OR `r`.`uploader` = '.$user_id.')';

    $where_limit = $limit === null ? '' : ' LIMIT ' . $limit;
    if ($limit && $offset) {
      $where_limit =  ' LIMIT ' . $offset . ', ' . $limit;
    }

    $where_total = $where_task . $where_task_type . $where_setup . $where_uploader . $where_impl . $where_run . $where_tag . $where_study . $where_server_error . $where_task_closed;

    $sql =
      'SELECT r.rid, r.uploader, r.task_id, r.start_time, t.ttid, d.did AS dataset_id, d.name AS dataset_name,' .
             'r.setup, i.id AS flow_id, i.name AS flow_name, r.error_message, r.run_details ' .
             //', GROUP_CONCAT(tag) AS tags ' .
      'FROM algorithm_setup s, implementation i, task t ' .
      'LEFT JOIN task_inputs ti ON t.task_id = ti.task_id AND ti.input = "source_data" ' .
      'LEFT JOIN dataset d ON ti.value = d.did, ' .
      'run r ' .
      'LEFT JOIN run_evaluated e ON r.rid = e.run_id ' .
      'WHERE r.setup = s.sid AND i.id = s.implementation_id AND t.task_id = r.task_id ' .
      $where_total .
      // 'GROUP BY r.rid ' .
      $where_limit;
    $res = $this->Run->query($sql);

    if ($res == false) {
      $this->returnError(512, $this->version);
      return;
    }

    if (count($res) > $result_limit) {
      $this->returnError(513, $this->version, $this->openmlGeneralErrorCode, 'Size of result set: ' . count($res) . '; max size: ' . $result_limit);
      return;
    }

    $this->xmlContents('runs', $this->version, array('runs' => $res));
  }

  /**
   *@OA\Get(
   *	path="/run/{id}",
   *	tags={"run"},
   *	summary="Get run description",
   *	description="Returns information about a run. The information includes the name, information about the creator, dependencies, parameters, run instructions and more.",
   *	@OA\Parameter(
   *		name="id",
   *		in="path",
   *		@OA\Schema(
   *          type="integer"
   *        ),
   *		description="ID of the run.",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="A run description",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Run",
   *			example={
   *			  "run": {
   *			    "run_id":"100",
   *			    "uploader":"1",
   *			    "uploader_name":"Jan van Rijn",
   *			    "task_id":"28",
   *			    "task_type":"Supervised Classification",
   *			    "task_evaluation_measure":"predictive_accuracy",
   *			    "flow_id":"67",
   *			    "flow_name":"weka.BayesNet_K2(1)",
   *			    "setup_string":"weka.classifiers.bayes.BayesNet -- -D -Q weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5",
   *			    "parameter_setting": {
   *			      {
   *			        "name":"D",
   *			        "value":"true"
   *			      },
   *			      {
   *			        "name":"Q",
   *			        "value":"weka.classifiers.bayes.net.search.local.K2"
   *			      },
   *			      {
   *			        "name":"P",
   *			        "value":"1"
   *			      },
   *			      {
   *			        "name":"S",
   *			        "value":"BAYES"
   *			      }
   *			    },
   *			    "input_data":
   *			      {
   *			        "dataset":
   *			          {
   *			            "did":"28",
   *			            "name":"optdigits",
   *			            "url":"https:\\/\\/www.openml.org\\/data\\/download\\/28\\/dataset_28_optdigits.arff"
   *			          }
   *			      },
   *			    "output_data":
   *			      {
   *			        "file": {
   *			          {
   *			            "did":"48838",
   *			            "file_id":"261",
   *			            "name":"description",
   *			            "url":"https:\\/\\/www.openml.org\\/data\\/download\\/261\\/weka_generated_run935374685998857626.xml"
   *			          },
   *			          {
   *			            "did":"48839",
   *			            "file_id":"262",
   *			            "name":"predictions",
   *			            "url":"https:\\/\\/www.openml.org\\/data\\/download\\/262\\/weka_generated_predictions576954524972002741.arff"
   *			          }
   *			        },
   *			        "evaluation": {
   *			          {
   *			            "name":"area_under_roc_curve",
   *			            "flow_id":"4",
   *			            "value":"0.990288",
   *			            "array_data":"[0.99724,0.989212,0.992776,0.994279,0.980578,0.98649,0.99422,0.99727,0.994858,0.976143]"
   *			          },
   *			          {
   *			            "name":"confusion_matrix",
   *			            "flow_id":"10",
   *			            "array_data":"[[544,1,0,0,7,0,1,0,0,1],[0,511,21,1,0,1,3,1,5,28],[0,7,511,1,0,1,0,3,23,11],[0,2,2,519,0,3,0,12,16,18],[0,3,0,0,528,0,4,21,6,6],[0,1,0,7,5,488,2,0,4,51],[1,7,0,0,2,0,548,0,0,0],[0,2,0,1,9,1,0,545,4,4],[1,25,2,2,3,6,2,1,503,9],[0,7,0,20,16,5,0,19,9,486]]"
   *			          },
   *			          {
   *			            "name":"f_measure",
   *			            "flow_id":"12",
   *			            "value":"0.922723",
   *			            "array_data":"[0.989091,0.898857,0.935041,0.92431,0.927944,0.918156,0.980322,0.933219,0.895018,0.826531]"
   *			          },
   *			          {
   *			            "name":"kappa",
   *			            "flow_id":"13",
   *			            "value":"0.913601"
   *			          }
   *			        }
   *			      }
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n220 - Please provide run ID. In order to view run details, please provide the run ID.\n221 - Run not found. The run ID was invalid, run does not exist (anymore).\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function run($run_id) {
    if( $run_id == false ) {
      $this->returnError( 235, $this->version );
      return;
    }
    $run = $this->Run->getById($run_id);
    if( $run === false ) {
      $this->returnError( 236, $this->version );
      return;
    }

    $run->eval_data = $this->Run_evaluated->getById(array($run_id,$this->weka_engine_id)); # TODO: currently, by default we get weka evaluation results
    $run->inputData = $this->Run->getInputData( $run->rid );
    $run->outputData = $this->Run->getOutputData( $run->rid );
    $run->setup = $this->Algorithm_setup->getById( $run->setup );
    $run->tags = $this->Run_tag->getColumnWhere( 'tag', 'id = ' . $run->rid );
    $run->inputSetting = $this->Input_setting->query('SELECT i.name, i.implementation_id, s.value from input i, input_setting s where i.id=s.input_id and setup = ' . $run->setup->sid );
    $run->task_type = $this->Task->query('SELECT tt.name from task t, task_type tt where t.ttid=tt.ttid and t.task_id = ' . $run->task_id )[0]->name;
    $user = $this->Author->getById($run->uploader);
    $run->user_name = $user->first_name . ' ' . $user->last_name;
    $run->flow_name = $this->Implementation->getById($run->setup->implementation_id)->fullName;
    $run->task_evaluation = $this->Task_inputs->getWhere("task_id = " . $run->task_id . " and input = 'evaluation_measures'")[0];

    $this->xmlContents( 'run-get', $this->version, array( 'source' => $run ) );
  }

  /**
   *@OA\Delete(
   *	path="/run/{id}",
   *	tags={"run"},
   *	summary="Delete run",
   *	description="Deletes a run. Upon success, it returns the ID of the deleted run.",
   *	@OA\Parameter(
   *		name="id",
   *		in="path",
   *		@OA\Schema(
   *          type="integer"
   *        ),
   *		description="Id of the run.",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="api_key",
   *		in="query",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="API key to authenticate the user",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="ID of the deleted run",
   *		@OA\JsonContent(
   *			type="object",
   *			@OA\Property(
   *				property="data_delete",
   *				ref="#/components/schemas/inline_response_200_17_data_delete",
   *			),
   *			example={
   *			  "run_delete": {
   *			    "id": "2520"
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n390 - Please provide API key. In order to remove your content, please authenticate.\n391 - Authentication failed. The API key was not valid. Please try to login again, or contact api administrators\n392 - Run does not exists. The run ID could not be linked to an existing run.\n393 - Run is not owned by you. The run was owned by another user. Hence you cannot delete it.\n394 - Deleting run failed. Deleting the run failed. Please contact support team.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function run_delete($run_id) {

    $run = $this->Run->getById( $run_id );
    if( $run == false ) {
      $this->returnError( 392, $this->version );
      return;
    }

    if($run->uploader != $this->user_id && $this->user_has_admin_rights == false) {
      $this->returnError( 393, $this->version );
      return;
    }
    
    $this->db->trans_start();
    
    $this->Input_data->deleteWhere( 'run =' . $run->rid );
    $this->Output_data->deleteWhere( 'run =' . $run->rid );
    
    $additional_sql = ''; //' AND `did` NOT IN (SELECT `data` FROM `input_data` UNION SELECT `data` FROM `output_data`)';
    $this->Runfile->deleteWhere('`source` = "' . $run->rid . '" ' . $additional_sql);
    $this->Evaluation->deleteWhere('`source` = "' .  $run->rid. '" ' . $additional_sql);
    $this->Evaluation_fold->deleteWhere('`source` = "' . $run->rid . '" ' . $additional_sql);
    $this->Evaluation_sample->deleteWhere('`source` = "' . $run->rid . '" ' . $additional_sql);
    $this->Run_evaluated->deleteWhere('`run_id` = "' . $run->rid . '" ');
    $this->Run->delete( $run->rid );

    if ($this->db->trans_status() === FALSE) {
      $this->db->trans_rollback();
      $this->returnError( 394, $this->version );
      return;
    }
    $this->db->trans_commit();

    try {
      $this->elasticsearch->delete('run', $run_id);
      $this->elasticsearch->index('user', $this->user_id);
    } catch (Exception $e) {
      $additionalMsg = get_class() . '.' . __FUNCTION__ . ':' . $e->getMessage();
      $this->returnError(105, $this->version, $this->openmlGeneralErrorCode, $additionalMsg);
      return;
    }

    $this->xmlContents( 'run-delete', $this->version, array( 'run' => $run ) );
  }

  /**
   *@OA\Get(
   *	path="/run/reset/{id}",
   *	tags={"run"},
   *	summary="Resets a run.",
   *	description="Removes all run evaluations. When a run is reset, the runs will automatically be evaluated as soon as they are picked up by the evaluation engine again.",
   *	@OA\Parameter(
   *		name="id",
   *		in="path",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="Run ID.",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="Id of the evaluated run",
   *		@OA\JsonContent(
   *			type="object",
   *			@OA\Property(
   *				property="run_reset",
   *				ref="#/components/schemas/inline_response_200_21_upload_flow",
   *			),
   *			example={
   *			  "run_reset": {
   *			    "id": "2520"
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n412 - Run does not exist\n413 - Run is not owned by you\n394 - Resetting run failed\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function run_reset($run_id) {

    $run = $this->Run->getById( $run_id );
    if( $run == false ) {
      $this->returnError( 412, $this->version );
      return;
    }

    if($run->uploader != $this->user_id && $this->user_has_admin_rights == false) {
      $this->returnError(413, $this->version);
      return;
    }
    
    $result = $this->Run_evaluated->deleteWhere('`run_id` = "' . $run->rid . '" ');

    if( $result == false ) {
      $this->returnError(394, $this->version);
      return;
    }
    $this->xmlContents( 'run-reset', $this->version, array( 'run' => $run ) );
  }

  /**
   *@OA\Post(
   *	path="/run",
   *	tags={"run"},
   *	summary="Upload run",
   *	description="Uploads a run. Upon success, it returns the run id.",
   *	@OA\Parameter(
   *		name="description",
   *		in="query",
   *		@OA\Schema(
   *          type="file"
   *        ),
   *		description="An XML file describing the dataset. Only name, description, and data format are required. Also see the [XSD schema](https://www.openml.org/api/v1/xsd/openml.run.upload) and an [XML example](https://www.openml.org/api/v1/xml_example/run).",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="predictions",
   *		in="query",
   *		@OA\Schema(
   *          type="file"
   *        ),
   *		description="The predictions generated by the run",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="model_readable",
   *		in="query",
   *		@OA\Schema(
   *          type="file"
   *        ),
   *		description="The human-readable model generated by the run",
   *		required=false,
   *	),
   *	@OA\Parameter(
   *		name="model_serialized",
   *		in="query",
   *		@OA\Schema(
   *          type="file"
   *        ),
   *		description="The serialized model generated by the run",
   *		required=false,
   *	),
   *	@OA\Parameter(
   *		name="api_key",
   *		in="query",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="Api key to authenticate the user",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="Id of the uploaded run",
   *		@OA\JsonContent(
   *			type="object",
   *			@OA\Property(
   *				property="upload_flow",
   *				ref="#/components/schemas/inline_response_200_18_upload_flow",
   *			),
   *			example={
   *			  "upload_run": {
   *			    "id": "2520"
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n201 - Authentication failed. The API key was not valid. Please try to login again, or contact api administrators.\n202 - Please provide run XML.\n203 - Could not validate run xml by XSD. Please double check that the xml is valid.\n204 - Unknown task. The task with the given ID was not found in the database.\n205 - Unknown flow. The flow with the given ID was not found in the database.\n206 - Invalid number of files. The number of uploaded files did not match the number of files expected for the task type\n207 - File upload failed. One of the files uploaded has a problem.\n208 - Error inserting setup record. Please contact api administrators\n210 - Unable to store run. Please contact api administrators.\n211 - Dataset not in database. One of the datasets of the task was not included in database, please contact api administrators.\n212 - Unable to store file. Please contact api administrators.\n213 - Parameter in run xml unknown. One of the parameters provided in the run xml is not registered as parameter for the flow nor its components.\n214 - Unable to store input setting. Please contact API support team.\n215 - Unable to evaluate predictions. Please contact API support team.\n216 - Error thrown by Java Application. Additional information field is provided.\n217 - Error processing output data. Unknown or inconsistent evaluation measure. One of the provided evaluation measures could not be matched with a record in the math_function or flow table.\n218 - Wrong flow associated with run. The flow implements a math_function, which is unable to generate predictions. Please select another flow.\n219 - Error reading the XML document. The XML description file could not be verified.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function run_upload() {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Everything that needs to be done for EVERY task,        *
     * Including the unsupported tasks                         *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    // IMPORTANT! This function is sort of similar to "setup exists".
    // If changing something big, also test that function.

    $timestamps = array(microtime(true)); // profiling 0

    // check uploaded file
    $description = isset($_FILES['description']) ? $_FILES['description'] : false;
    $uploadError = '';
    if(!check_uploaded_file($description,false,$uploadError)) {
      $this->returnError(201, $this->version,$this->openmlGeneralErrorCode,$uploadError);
      return;
    }

    // validate xml
    $xmlErrors = '';
    if(validateXml($description['tmp_name'], xsd('openml.run.upload', $this->controller, $this->version), $xmlErrors) == false) {
      if (DEBUG) {
        $to = $this->user_email;
        $subject = 'OpenML Flow Upload DEBUG message. ';
        $content = 'Filename: ' . $_FILES['description']['name'] . "\nXSD Validation Message: " . $xmlErrors . "\n=====BEGIN XML=====\n" . file_get_contents($description['tmp_name']);
        sendEmail($to, $subject, $content,'text');
      }

      $this->returnError(202, $this->version, $this->openmlGeneralErrorCode, $xmlErrors);
      return;
    }

    // fetch xml
    $xml = simplexml_load_file($description['tmp_name']);
    if($xml === false) {
      $this->returnError(203, $this->version);
      return;
    }

    $run_xml = all_tags_from_xml(
      $xml->children('oml', true),
      $this->xml_fields_run);

    $task_id = $run_xml['task_id'];
    $implementation_id = $run_xml['flow_id'];
    $setup_string = array_key_exists('setup_string', $run_xml) ? $run_xml['setup_string'] : null;
    $error_message = array_key_exists('error_message', $run_xml) ? $run_xml['error_message'] : false;
    $run_details = array_key_exists('run_details', $run_xml) ? $run_xml['run_details'] : null;
    $parameter_objects = array_key_exists('parameter_setting', $run_xml) ? $run_xml['parameter_setting'] : array();
    $output_data = array_key_exists('output_data', $run_xml) ? $run_xml['output_data'] : array();
    $tags = array_key_exists('tag', $run_xml) ? str_getcsv ($run_xml['tag']) : array();

    $predictionsUrl   = false;

    // fetch implementation
    $implementation = $this->Implementation->getById($implementation_id);
    if($implementation === false) {
      $this->returnError(205, $this->version);
      return;
    }
    if(in_array($implementation->{'implements'}, $this->supportedMetrics)) {
      $this->returnError(218, $this->version);
      return;
    }

    // check whether uploaded files are present.

    foreach ($_FILES as $key => $value) {
      $message = '';
      $extension = getExtension($_FILES[$key]['name']);

      if (/*in_array($extension,$this->config->item('allowed_extensions')) == false ||*/ $extension == false) {
        $this->returnError(206, $this->version, $this->openmlGeneralErrorCode, 'Invalid extension for file "' . $key . '". Original filename: ' . $_FILES[$key]['name']);
        return;
      }

      if (!check_uploaded_file($_FILES[$key], false, $message)) {
        $this->returnError(207, $this->version, $this->openmlGeneralErrorCode, 'Upload problem with file "' . $key . '": ' . $message);
        return;
      }

      if ($extension == 'arff') {
        $arffCheck = ARFFcheck($_FILES[$key]['tmp_name'], 1000);
        if ($arffCheck !== true) {
          $this->returnError(209, $this->version, $this->openmlGeneralErrorCode, 'Arff error in ' . $key . ' file: ' . $arffCheck);
          return;
        }
      }

      if ($extension == 'xml') {
        $xmlCheck = simplexml_load_file($_FILES[$key]['tmp_name']);
        if($xmlCheck === false) {
          $this->returnError(209, $this->version, $this->openmlGeneralErrorCode, 'XML error in ' . $key . ' file: ' . $xmlCheck);
          return;
        }
      }
    }

    $timestamps[] = microtime(true); // profiling 1

    $parameters = array();
    foreach($parameter_objects as $p) {
      // since 'component' is an optional XML field, we add a default option
      $component = property_exists($p, 'component') ? $p->component : $implementation->id;

      // now find the input id
      $input_id = $this->Input->getWhereSingle('`implementation_id` = ' . $component . ' AND `name` = "' . $p->name . '"');
      if($input_id === false) {
        $this->returnError(213, $this->version, $this->openmlGeneralErrorCode, 'Name: ' . $p->name . ', flow id (component): ' . $component);
        return;
      }

      $parameters[$input_id->id] = $p->value . '';
    }
    
    // search setup ... // TODO: do something about the new parameters. Are still retrieved by ID, does not work with Weka plugin.
    try {
      $setupId = $this->Algorithm_setup->getSetupId($implementation, $parameters, true, $setup_string);
    } catch(Exception $e) {
      $this->returnError(215, $this->version);
      return;
    }
    
    if( $setupId === false ) {
      $this->returnError(214, $this->version);
      return;
    }

    $timestamps[] = microtime(true); // profiling 2

    // fetch task
    $taskRecord = $this->Task->getById($task_id);
    if($taskRecord === false) {
      $this->returnError(204, $this->version);
      return;
    }

    $task = $this->Task_inputs->getTaskValuesAssoc($task_id);
    if (!array_key_exists('source_data', $task)) {
      $this->returnError(219, $this->version);
      return;
    }
    if (!array_key_exists('estimation_procedure', $task)) {
      $this->returnError(220, $this->version);
      return;
    }
    // check if dataset record associated to task exists
    $dataset_record = $this->Dataset->getById($task['source_data']);
    if ($dataset_record === false) {
      $this->returnError(221, $this->version);
      return;
    }
    // get data quality
    $num_instances_record = $this->Data_quality->getById(array($task['source_data'], 'NumberOfInstances', $this->weka_engine_id));
    if ($num_instances_record === false) {
      $this->returnError(208, $this->version);
      return;
    }
    // check if estimation procedure record associated to task exists
    $ep_record = $this->Estimation_procedure->getById($task['estimation_procedure']);
    if ($ep_record === false) {
      $this->returnError(222, $this->version);
      return;
    }
    
    $supported_evaluation_measures = $this->Math_function->getColumnWhere('name', '`functionType` = "EvaluationFunction"');
    // the user can specify his own metrics. here we check whether these exists in the database.
    if($output_data != false && array_key_exists('evaluation', $output_data)) {
      // php does not have a set data structure, use hashmap instead
      $used_evaluation_measures = array();
      $illegal_measures = array();
      foreach($output_data->children('oml',true)->{'evaluation'} as $e) {
        // record the evaluation measures that were used
        $eval = xml2assoc($e, true);
        $used_evaluation_measures[$eval['name']] = true;
        // check whether it was a legal measure w.r.t. the estimation procedure
        // first add null values, in case a propoerty doesn't exist
        $repeat_nr = array_key_exists('repeat', $eval) ? $eval['repeat'] : null;
        $fold_nr = array_key_exists('fold', $eval) ? $eval['fold'] : null;
        $sample_nr = array_key_exists('sample', $eval) ? $eval['sample'] : null;
        $num_inst = $num_instances_record->value;
        if (!$this->Estimation_procedure->check_legal($ep_record, $num_inst, $repeat_nr, $fold_nr, $sample_nr)) {
          $illegal_measures[] = $this->Estimation_procedure->eval_measure_to_string($eval['name'], $repeat_nr, $fold_nr, $sample_nr);
        }
      }
      $used_evaluation_measures = array_keys($used_evaluation_measures);
      $unknown_measures = array_diff($used_evaluation_measures, $supported_evaluation_measures);
      if (count($unknown_measures) > 0) {
        $this->returnError(217, $this->version, $this->openmlGeneralErrorCode, 'Measure(s): ' . implode(', ', $unknown_measures));
        return;
      }
      if (count($illegal_measures) > 0) {
        $this->returnError(216, $this->version, $this->openmlGeneralErrorCode, 'Measure(s): ' . implode(', ', $illegal_measures));
        return;
      }
    }
    
    // now create a run
    $runData = array(
      'uploader' => $this->user_id,
      'setup' => $setupId,
      'task_id' => $task_id,
      'start_time' => now(),
      'error_message' => ($error_message == false) ? null : $error_message,
      'run_details' => ($run_details == false) ? null : $run_details
    );

    $this->db->trans_start();
    $runId = $this->Run->insert($runData);
    if($runId === false) {
      $this->returnError(210, $this->version);
      return;
    }
    // and fetch the run record
    $run = $this->Run->getById($runId);
    $result = new stdClass();
    $result->run_id = $runId; // for output

    // attach uploaded files as output to run
    foreach($_FILES as $key => $value) {
      $file_type = 'run_uploaded_file';
      if ($key == 'predictions') {
        $file_type = 'predictions';
      } elseif ($key == 'trace') {
        $file_type = 'run_trace';
      }

      // it is important to put the runs in various directories
      $subdirectory = floor($runId / $this->content_folder_modulo) * $this->content_folder_modulo;
      $to_folder = $this->data_folders['run'] . '/' . $subdirectory . '/' . $runId . '/';
      $file_id = $this->File->register_uploaded_file($value, $to_folder, $this->user_id, $file_type);
      if(!$file_id) {
        $this->returnError(223, $this->version);
        return;
      }
      $file_record = $this->File->getById($file_id);

      $record = array(
        'source' => $runId,
        'field' => $key,
        'name' => $value['name'],
        'format' => $file_record->extension,
        'file_id' => $file_id,
        'upload_time' => now()
      );

      $did = $this->Runfile->insert($record);
      if( $did == false ) {
        $this->db->trans_rollback();
        $this->returnError(212, $this->version);
        return;
      }
      $this->Run->outputData($runId, $did, 'runfile', $key);
    }

    // attach input data TODO: JvR: this is legacy and implicit with the task. Remove?
    $inputData = $this->Run->inputData($runId, $task['source_data'], 'dataset'); // Based on the query, it has been garantueed that the dataset id exists.
    if($inputData === false) {
      $errorCode = 211;
      return false;
    }
    
    if ($this->db->trans_status() === FALSE) {  
      $this->db->trans_rollback();
      $this->returnError(224, $this->version);
      return;
    } else {  
      $this->db->trans_commit();
    }

    $timestamps[] = microtime(true); // profiling 3
    // add to elastic search index.

    try {
      $this->elasticsearch->index('run', $runId);
    } catch (Exception $e) {
      // TODO: should log
    }

    $timestamps[] = microtime(true); // profiling 4
    if (DEBUG) {
      $this->Log->profiling(__FUNCTION__, $timestamps,
        array(
          'uploaded file handling',
          'setup searching / creation',
          'database insertions',
          'elastic search')
      );
    }

    // tag it, if neccessary
    foreach($tags as $tag) {
      $success = $this->entity_tag_untag('run', $runId, $tag, false, 'run', true);
      // on failure, we ignore it (just a tag)
    }

    // and present result, in effect only a run_id.
    $this->xmlContents( 'run-upload', $this->version, $result );
  }

  /**
   *@OA\Get(
   *	path="/run/trace/{id}",
   *	tags={"run"},
   *	summary="Get run trace",
   *	description="Returns the optimization trace of run. The trace contains every setup tried, its evaluation, and whether it was selected.",
   *	@OA\Parameter(
   *		name="id",
   *		in="path",
   *		@OA\Schema(
   *          type="integer"
   *        ),
   *		description="ID of the run.",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="A run trace description",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/RunTrace",
   *			example={
   *			  "trace": {
   *			    "run_id":"573055",
   *			    "trace_iteration": {
   *			        "repeat":"0",
   *			        "fold":"0",
   *			        "repeat":"0",
   *			        "iteration":"0",
   *			        "setup_string":{"parameter_minNumObj": "1",
   *			                        "parameter_confidenceFactor": "0.1"},
   *			        "evaluation":"94.814815",
   *			        "selected": "true"
   *			      },
   *			    "trace_iteration": {
   *			        "repeat":"0",
   *			        "fold":"0",
   *			        "repeat":"0",
   *			        "iteration":"1",
   *			        "setup_string":{"parameter_minNumObj": "1",
   *			                        "parameter_confidenceFactor": "0.25"},
   *			        "evaluation": "94.074074",
   *			        "selected": "true"
   *			      }
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n570 - No successful trace associated with this run\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function run_trace($run_id) {
    $run = $this->Run->getById($run_id);
    if ($run === false) {
      $this->returnError(571, $this->version);
      return;
    }
    
    $trace = $this->Trace->getWhere('run_id = ' . $run_id, 'repeat ASC, fold ASC, iteration ASC');
    if ($trace === false) {
      $this->returnError(572, $this->version);
      return;
    }

    $this->xmlContents('run-trace-get', $this->version, array('run_id' => $run_id, 'trace' => $trace));
  }

  /**
   *@OA\Post(
   *	path="/run/trace/{id}",
   *	tags={"run"},
   *	summary="Upload run trace",
   *	description="Uploads a run trace. Upon success, it returns the run id.",
   *	@OA\Parameter(
   *		name="id",
   *		in="path",
   *		@OA\Schema(
   *          type="integer"
   *        ),
   *		description="ID of the run.",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="description",
   *		in="query",
   *		@OA\Schema(
   *          type="file"
   *        ),
   *		description="An XML file describing the trace. Also see the [XSD schema](https://www.openml.org/api/v1/xsd/openml.run.trace) and an [XML example](https://www.openml.org/api/v1/xml_example/run.trace).",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="api_key",
   *		in="query",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="Api key to authenticate the user",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="Id of the run with the trace",
   *		@OA\JsonContent(
   *			type="object",
   *			@OA\Property(
   *				property="upload_flow",
   *				ref="#/components/schemas/inline_response_200_23_upload_flow",
   *			),
   *			example={
   *			  "run_trace": {
   *			    "id": "2520"
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n561 - Problem with uploaded trace file.\n562 - Problem validating xml trace file.\n563 - Problem loading xml trace file.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function run_trace_upload() {
    if (!$this->user_has_admin_rights) {
      $this->returnError(106, $this->version);
      return;
    }

    // check uploaded file
    $trace = isset($_FILES['trace']) ? $_FILES['trace'] : false;
    if(!check_uploaded_file($trace)) {
      $this->returnError(561,$this->version);
      return;
    }

    $xsd = xsd('openml.run.trace', $this->controller, $this->version);

    // validate xml
    if(validateXml($trace['tmp_name'], $xsd, $xmlErrors) == false) {
      $this->returnError(562, $this->version, $this->openmlGeneralErrorCode, $xmlErrors);
      return;
    }

    // fetch xml
    $xml = simplexml_load_file($trace['tmp_name']);
    if($xml === false) {
      $this->returnError(563, $this->version);
      return;
    }

    $run_id = (string) $xml->children('oml', true)->{'run_id'};

    $this->db->trans_start();
    foreach($xml->children('oml', true)->{'trace_iteration'} as $t) {
      $iteration = xml2assoc($t, true);

      $iteration['run_id'] = $run_id;

      $this->Trace->insert($iteration);
    }
    
    if ($this->db->trans_status() === FALSE) {
      $this->db->trans_rollback();
      $this->returnError(564, $this->version);
      return;
    } else {
      $this->db->trans_commit();
    }

    $this->xmlContents('run-trace', $this->version, array('run_id' => $run_id));
  }

  /**
   *@OA\Post(
   *	path="/run/evaluate",
   *	tags={"run"},
   *	summary="Uploads a run evaluation",
   *	description="Uploads a run evaluation. When successful, it returns the run id.",
   *	@OA\Parameter(
   *		name="description",
   *		in="query",
   *		@OA\Schema(
   *          type="file"
   *        ),
   *		description="An XML file describing the run evaluation.Also see the [XSD schema](https://www.openml.org/api/v1/xsd/openml.run.evaluate) and an [XML example](https://www.openml.org/api/v1/xml_example/run.evaluate).",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="api_key",
   *		in="query",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="Api key to authenticate the user",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="Id of the evaluated run",
   *		@OA\JsonContent(
   *			type="object",
   *			@OA\Property(
   *				property="upload_flow",
   *				ref="#/components/schemas/inline_response_200_21_upload_flow",
   *			),
   *			example={
   *			  "run_evaluate": {
   *			    "id": "2520"
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n422 - Upload problem description XML\n423 - Problem validating uploaded description file\n424 - Problem opening description xml\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function run_evaluate() {
    $timestamps = array(microtime(true)); // profiling 0
    if (!$this->user_has_admin_rights) {
      $this->returnError(106, $this->version);
      return;
    }

    // check uploaded file
    $description = isset( $_FILES['description']) ? $_FILES['description'] : false;
    $error_message = null;
    if(!check_uploaded_file($description, false, $error_message)) {
      $this->returnError(422, $this->version, $this->openmlGeneralErrorCode, $error_message);
      return;
    }

    $xsd = xsd('openml.run.evaluate', $this->controller, $this->version);

    // validate xml
    if(validateXml( $description['tmp_name'], $xsd, $xmlErrors) == false) {
      $this->returnError(423, $this->version, $this->openmlGeneralErrorCode, $xmlErrors);
      return;
    }

    // fetch xml
    $xml = simplexml_load_file($description['tmp_name']);
    if($xml === false) {
      $this->returnError(424, $this->version);
      return;
    }

    // TODO: check if user id and evaluation_engine_id are compatible
    // (i.e., is the user allowed to run the engine)

    $run_id = (string) $xml->children('oml', true)->{'run_id'};
    $eval_engine_id = '' . $xml->children('oml', true)->{'evaluation_engine_id'};
    
    // obtain evaluation record, if exists
    $evaluation_record = $this->Run_evaluated->getById(array($run_id, $eval_engine_id));
    $num_tries = 0;
    if ($evaluation_record) {
      $num_tries = $evaluation_record->num_tries;
    }
      
    // initiate run_evaluated record 
    $data = array(
        'evaluation_date' => now(),
        'run_id' => $run_id,
        'evaluation_engine_id' => $eval_engine_id,
        'user_id' => $this->user_id,
        'num_tries' => $num_tries + 1,
    );
    if (isset($xml->children('oml', true)->{'error'})) {
      $data['error'] = '' . $xml->children('oml', true)->{'error'};
    }
    if (isset( $xml->children('oml', true)->{'warning'})) {
      $data['warning'] = '' . $xml->children('oml', true)->{'warning'};
    }

    $runRecord = $this->Run->getById($run_id);
    if($runRecord == false) {
      $this->returnError(425, $this->version);
      return;
    }
    
    $task = $this->Task_inputs->getTaskValuesAssoc($runRecord->task_id);
    if (!array_key_exists('source_data', $task)) {
      // also add error in database
      $error_code = 429;
      $data['error'] = $this->load->apiErrors[$error_code];
      $this->Run_evaluated->replace($data);
      $this->returnError($error_code, $this->version);
      return;
    }
    if (!array_key_exists('estimation_procedure', $task)) {
      // also add error in database
      $error_code = 430;
      $data['error'] = $this->load->apiErrors[$error_code];
      $this->Run_evaluated->replace($data);
      $this->returnError($error_code, $this->version);
      return;
    }
    // check if dataset record associated to task exists
    $dataset_record = $this->Dataset->getById($task['source_data']);
    if ($dataset_record === false) {
      // also add error in database
      $error_code = 431;
      $data['error'] = $this->load->apiErrors[$error_code];
      $this->Run_evaluated->replace($data);
      $this->returnError($error_code, $this->version);
      return;
    }
    // get data quality
    $num_instances_record = $this->Data_quality->getById(array($task['source_data'], 'NumberOfInstances', $this->weka_engine_id));
    if ($num_instances_record === false) {
      // also add error in database
      $error_code = 433;
      $data['error'] = $this->load->apiErrors[$error_code];
      $this->Run_evaluated->replace($data);
      $this->returnError($error_code, $this->version);
      return;
    }
    // check if estimation procedure record associated to task exists
    $ep_record = $this->Estimation_procedure->getById($task['estimation_procedure']);
    if ($ep_record === false) {
      // also add error in database
      $error_code = 432;
      $data['error'] = $this->load->apiErrors[$error_code];
      $this->Run_evaluated->replace($data);
      $this->returnError($error_code, $this->version);
      return;
    }
    
    $math_functions = $this->Math_function->getAssociativeArray('name', 'id', 'functionType = "EvaluationFunction"');
    $evaluations_stored = $this->Evaluation->getWhere('source = "' . $run_id . '" AND evaluation_engine_id = "' . $eval_engine_id . '"');

    if($evaluation_record && $evaluation_record->error == null) {
      $this->returnError(426, $this->version);
      return;
    }

    if ($evaluations_stored && !$evaluation_record) {
      // also add error in database
      $error_code = 427;
      $data['error'] = $this->load->apiErrors[$error_code];
      $this->Run_evaluated->replace($data);
      $this->returnError($error_code, $this->version);
      return;
    }

    $timestamps[] = microtime(true); // profiling 1
    
    // pre-check
    $illegal_measures = array();
    foreach($xml->children('oml', true)->{'evaluation'} as $e) {
      $eval = xml2assoc($e, true);
      $repeat_nr = array_key_exists('repeat', $eval) ? $eval['repeat'] : null;
      $fold_nr = array_key_exists('fold', $eval) ? $eval['fold'] : null;
      $sample_nr = array_key_exists('sample', $eval) ? $eval['sample'] : null;
      $num_inst = $num_instances_record->value;
      if (!$this->Estimation_procedure->check_legal($ep_record, $num_inst, $repeat_nr, $fold_nr, $sample_nr)) {
        $illegal_measures[] = $this->Estimation_procedure->eval_measure_to_string($eval['name'], $repeat_nr, $fold_nr, $sample_nr);
      }
    }
    if (count($illegal_measures) > 0) {
      $this->returnError(434, $this->version, $this->openmlGeneralErrorCode, 'Measure(s): ' . implode(', ', $illegal_measures));
      return;
    }

    $this->db->trans_start();
    $this->Run_evaluated->replace($data);

    foreach($xml->children('oml', true)->{'evaluation'} as $e) {
      $evaluation = xml2assoc($e, true);

      // adding rid
      $evaluation['source'] = $run_id;
      // adding evaluation engine id
      $evaluation['evaluation_engine_id'] = $eval_engine_id;

      // TODO: this responsibility should be shifted to the evaluation engine
      if (array_key_exists($evaluation['name'], $math_functions)) {
        $evaluation['function_id'] = $math_functions[$evaluation['name']];
      } else {
        // there will be a DB error due to the absence of 'function_id'
      }
      // unset function field
      unset($evaluation['name']);

      if(array_key_exists('fold', $evaluation) && array_key_exists('repeat', $evaluation) &&  array_key_exists('sample', $evaluation)) {
        // evaluation_sample
        $this->Evaluation_sample->insert($evaluation);
      } elseif(array_key_exists('fold', $evaluation) && array_key_exists('repeat', $evaluation)) {
        // evaluation_fold
        $this->Evaluation_fold->insert($evaluation);
      } else {
        // global
        $this->Evaluation->insert($evaluation);
      }
    }
      
    if ($this->db->trans_status() === FALSE) {
      $this->db->trans_rollback();
      $this->returnError(428, $this->version);
      return;
    } else {  
      $this->db->trans_commit();
    }


    $timestamps[] = microtime(true); // profiling 2

    // update elastic search index.
    try {
      $this->elasticsearch->index('run', $run_id);
      $this->elasticsearch->index('user', $this->user_id);
    } catch (Exception $e) {
      $additionalMsg = get_class() . '.' . __FUNCTION__ . ':' . $e->getMessage();
      $this->returnError(105, $this->version, $this->openmlGeneralErrorCode, $additionalMsg);
      return;
    }

    $timestamps[] = microtime(true); // profiling 3
    if (DEBUG) {
      $this->Log->profiling(__FUNCTION__, $timestamps,
        array(
          'basic checks of inputs and xml',
          'database insertions',
          'elastic search indexing')
      );
    }

    $this->xmlContents('run-evaluate', $this->version, array('run_id' => $run_id));
  }
}
?>
