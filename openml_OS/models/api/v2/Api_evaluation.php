<?php
class Api_evaluation extends MY_Api_Model {

  protected $version = 'v2';

  function __construct() {
    parent::__construct();

    // load models
    $this->load->model('Evaluation');
  }

  function bootstrap($format, $segments, $request_type, $user_id) {
    $this->outputFormat = $format;

    $getpost = array('get','post');

    if (count($segments) >= 1 && $segments[0] == 'setup' && $segments[1] == 'list') {
      array_shift($segments);
      array_shift($segments);
      $this->evaluation_list($segments, $user_id, true);
      return;
    } elseif (count($segments) >= 1 && $segments[0] == 'list') {
      array_shift($segments);
      $this->evaluation_list($segments, $user_id, false);
      return;
    }

    $order_values = array('random', 'reverse', 'normal');
    if (count($segments) >= 4 && $segments[0] == 'request' && is_numeric($segments[1]) && in_array($segments[2], $order_values) && is_numeric($segments[3])) {
      array_shift($segments); // removes 'request'
      $eval_id = array_shift($segments);
      $order = array_shift($segments);
      $num_requests = array_shift($segments);

      $this->evaluation_request($eval_id, $order, $num_requests, $segments);
      return;
    }

    $this->returnError(100, $this->version);
  }

  /**
   *@OA\Get(
   *	path="/evaluation/request/{evaluation_engine_id}/{order}",
   *	tags={"evaluation"},
   *	summary="Get an unevaluated run",
   *	description="This call is for people running their own evaluation engines. It returns the details of a run that is not yet evaluated by the given evaluation engine. It doesn't evaluate the run, it just returns the run info.",
   *	@OA\Parameter(
   *		name="evaluation_engine_id",
   *		in="path",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="The ID of the evaluation engine. You get this ID when you register a new evaluation engine with OpenML. The ID of the main evaluation engine is 1.",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="order",
   *		in="path",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="When there are multiple runs still to evaluate, this defines which one to return. Options are 'normal' - the oldest run, 'reverse' - the newest run, or 'random' - a random run.",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="A list of evaluations descriptions",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/EvaluationRequest",
   *			example={"evaluation_request": {"run": {{"setup_id": "68799271", "upload_time": "2018-04-03 21:05:38", "uploader": "1935", "task_id": "3021", "run_id": "8943712"}}}}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n100 - Function not valid.\n545 - No unevaluated runs according to the criteria.\n546 - Illegal filter.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function evaluation_request($evaluation_engine_id, $order,  $num_requests, $segs) {
    if (!$this->user_has_admin_rights) {
      $this->returnError(106, $this->version);
      return;
    }
    
    $legal_filters = array('ttid', 'task', 'tag', 'uploader', 'setup');
    $query_string = array();
    for ($i = 0; $i < count($segs); $i += 2) {
      if (in_array($segs[$i], $legal_filters) == false) {
        $this->returnError(1011, $this->version, $this->openmlGeneralErrorCode, 'Legal filter operators: ' . implode(',', $legal_filters) .'. Found illegal filter: ' . $segs[$i]);
        return;
      }
      if ($i + 1 < count($segs)) {
        $query_string[$segs[$i]] = urldecode($segs[$i+1]);  
      } else {
        $this->returnError(1014, $this->version, $this->openmlGeneralErrorCode, 'Filter: ' . $segs[$i]);
        return;  
      }
    }
    
    $illegal_filter_inputs = $this->check_filter_inputs($query_string, $legal_filters, array('tag'));
    if (count($illegal_filter_inputs) > 0) {
      $this->returnError(1012, $this->version, $this->openmlGeneralErrorCode, 'Filters with illegal values: ' . implode(',', $illegal_filter_inputs));
      return;
    }
    
    $ttid = element('ttid', $query_string, false);
    $tag = element('tag', $query_string, false);
    $uploader = element('uploader', $query_string, false);
    $task = element('task', $query_string, false);
    $setup = element('setup', $query_string, false);
    
    if ($task != false) {
      $task = explode(',', $task);
    }
    if ($ttid != false) {
      $ttid = explode(',', $ttid);
    }
    if ($uploader != false) {
      $uploader = explode(',', $uploader);
    }
    if ($setup != false) {
      $setup = explode(',', $setup);
    }
    $res = $this->Run_evaluated->getUnevaluatedRun($evaluation_engine_id, $order, $num_requests, $ttid, $task, $tag, $uploader, $setup);
    if ($res == false) {
      $this->returnError(1013, $this->version);
      return;
    }
    $this->xmlContents('evaluations-request', $this->version, array('res' => $res));
  }


  /**
   *@OA\Get(
   *	path="/evaluation/list/{filters}",
   *	tags={"evaluation"},
   *	summary="List and filter evaluations",
   *	description="List evaluations, filtered by a range of properties. Any number of properties can be combined by listing them one after the other in the form '/evaluation/list/{filter}/{value}/{filter}/{value}/...' Returns an array with all evaluations that match the constraints. A maximum of 10,000 results are returned, an error is returned if the result set is bigger. Use pagination (via limit and offset filters), or limit the results to certain tasks, flows, setups, uploaders or runs.",
   *	@OA\Parameter(
   *		name="filters",
   *		in="path",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="Any combination of these filters
  /function/{name} - name of the evaluation measure, e.g. area_under_auc or predictive_accuracy. See the OpenML website for the complete list of measures.
  /tag/{tag} - returns only evaluations of runs tagged with the given tag.
  /run/{ids} - return only evaluations for specific runs, specified as a comma-separated list of run IDs, e.g. ''1,2,3''
  /task/{ids} - return only evaluations for specific tasks, specified as a comma-separated list of task IDs, e.g. ''1,2,3''
  /flow/{ids} - return only evaluations for specific flows, specified as a comma-separated list of flow IDs, e.g. ''1,2,3''
  /setup/{ids} - return only evaluations for specific setups, specified as a comma-separated list of setup IDs, e.g. ''1,2,3''
  /uploader/{ids} - return only evaluations uploaded by specific users, specified as a comma-separated list of user IDs, e.g. ''1,2,3''
  /limit/{limit}/offset/{offset} - returns only {limit} results starting from result number {offset}. Useful for paginating results. With /limit/5/offset/10, results 11..15 will be returned. Both limit and offset need to be specified.
  /per_fold/{true,false} - whether or not to return crossvalidation scores per fold. Defaults to 'false'. Setting it to 'true' leads to large numbers of results, use only for very specific sets of runs.
  /sort_order/{asc,desc} - sorts the results by the evaluation value, according to the selected evaluation measure (function)
  ",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="A list of evaluations descriptions",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/EvaluationList",
   *			example={"evaluations": {"evaluation": {{"function": "area_under_roc_curve", "upload_time": "2014-04-06 23:30:40", "task_id": "68", "run_id": "1", "array_data": "[0,0.99113,0.898048,0.874862,0.791282,0.807343,0.820674]", "value": "0.839359", "uploader": "1", "flow_id": "61"}, {"function": "f_measure", "upload_time": "2014-04-06 23:30:40", "task_id": "68", "run_id": "1", "array_data": "[0,0,0.711934,0.735714,0.601363,0.435678,0.430913]", "value": "0.600026", "uploader": "1", "flow_id": "61"}, {"function": "predictive_accuracy", "upload_time": "2014-04-06 23:30:40", "task_id": "68", "run_id": "1", "array_data": {}, "value": "0.614634", "uploader": "1", "flow_id": "61"}}}}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n540 - Please provide at least task, flow or setup, uploader or run, to\nfilter results, or limit the number of responses.\n541 - The input parameters (task_id, setup_id, flow_id, run_id, uploader_id) did not meet the constraints (comma separated list of integers).\n542 - There where no results. Check whether there are runs under the given constraint.\n543 - Too many results. Given the constraints, there were still too many results. Please add filters to narrow down the list.\n544 - Illegal filter specified.\n545 - Offset specified without limit.\n546 - Requested result limit too high.\n547 - Per fold can only be set to value 'true' or 'false'.\n548 - Per fold queries are experimental and require a fair amount of filters on resulting run records to keep the query fast (use, e.g., flow, setup, task and uploader filter)\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function evaluation_list($segs, $user_id, $show_params) {
    $result_limit = 10000;
    $legal_filters = array('task', 'setup', 'flow', 'uploader', 'run', 'tag', 'limit', 'offset', 'function', 'per_fold', 'sort_order', 'study');
    list($query_string, $illegal_filters) = $this->parse_filters($segs, $legal_filters);
    if (count($illegal_filters) > 0) {
      $this->returnError(544, $this->version, $this->openmlGeneralErrorCode, 'Legal filter operators: ' . implode(',', $legal_filters) .'. Found illegal filter(s): ' . implode(', ', $illegal_filters));
      return;
    }

    $illegal_filter_inputs = $this->check_filter_inputs($query_string, $legal_filters, array('tag', 'function', 'per_fold', 'sort_order'));
    if (count($illegal_filter_inputs) > 0) {
      $this->returnError(541, $this->version, $this->openmlGeneralErrorCode, 'Filters with illegal values: ' . implode(',', $illegal_filter_inputs));
      return;
    }

    $task_id = element('task', $query_string, null);
    $setup_id = element('setup', $query_string, null);
    $implementation_id = element('flow', $query_string, null);
    $uploader_id = element('uploader', $query_string, null);
    $run_id = element('run', $query_string, null);
    $function_name = element('function', $query_string, null);
    $tag = element('tag', $query_string, null);
    $limit = element('limit', $query_string, null);
    $offset = element('offset', $query_string, null);
    $per_fold = element('per_fold', $query_string, null);
    $sort_order = element('sort_order', $query_string, null);
    $study_id = element('study', $query_string, null);
    if ($per_fold != 'true' && $per_fold != 'false' && $per_fold != null) {
      $this->returnError(547, $this->version, $this->openmlGeneralErrorCode, 'Filters with illegal values: ' . implode(',', $illegal_filter_inputs));
      return;
    }
    $per_fold = ($per_fold === 'true') ? true : false; // cast to boolean. we only accept lowercase entrees

    if ($offset && !$limit) {
      $this->returnError(545, $this->version);
      return;
    }
    if ($limit && $limit > $result_limit) {
      $this->returnError(546, $this->version);
      return;
    }
    if ($sort_order && !in_array($sort_order, array('asc', 'desc'))) {
      $this->returnError(549, $this->version);
      return;
    }

    if ($study_id) {
      $study = $this->Study->getById($study_id);
      if ($study === false || $study->legacy != 'n' || $study->main_entity_type != 'run') {
        $this->returnError(555, $this->version);
        return;
      }
    }

    if ($task_id === null && $setup_id === null && $implementation_id === null && $uploader_id === null && $run_id === null && $tag === null && $study_id === null && $limit === null && $function_name === null) {
      $this->returnError(540, $this->version);
      return;
    }

    $where_task = $task_id === null ? '' : ' AND `r`.`task_id` IN (' . $task_id . ') ';
    $where_setup = $setup_id === null ? '' : ' AND `r`.`setup` IN (' . $setup_id . ') ';
    $where_uploader = $uploader_id === null ? '' : ' AND `r`.`uploader` IN (' . $uploader_id . ') ';
    $where_impl = $implementation_id === null ? '' : ' AND `s`.`implementation_id` IN (' . $implementation_id . ') ';
    $where_run = $run_id === null ? '' : ' AND `r`.`rid` IN (' . $run_id . ') ';
    $where_function = $function_name === null ? '' : ' AND `f`.`name` = "' . $function_name . '" ';
    $where_tag = $tag === null ? '' : ' AND `r`.`rid` IN (select id from run_tag where tag="' . $tag . '") ';
    $where_study = $study_id === null ? '' : ' AND `r`.`rid` IN (SELECT `run_id` FROM `run_study` WHERE `study_id`="' . $study_id . '") ';
    $where_limit = $limit === null ? null : ' LIMIT ' . $limit;
    if ($limit && $offset) {
      $where_limit =  ' LIMIT ' . $offset . ',' . $limit;
    }
    $where_task_closed = ' AND (`t`.`embargo_end_date` is NULL OR `t`.`embargo_end_date` < NOW() OR `r`.`uploader` = '.$user_id.')';

    $where_runs = $where_task . $where_setup . $where_uploader . $where_impl . $where_run . $where_tag . $where_study . $where_task_closed;
    $where_total = $where_runs . $where_function;

    //pre-test, should be quick??
    if($limit == false || $per_fold) { // skip pre-test if less than 10000 are requested by definition
      $count = 0;
      //shortcuts
      if (!$implementation_id) {
        $sql_test =
            'SELECT count(distinct r.rid) as count ' .
            'FROM run r, task t '.
            'WHERE r.task_id = t.task_id ' .
            $where_runs;
        $count = $this->Evaluation->query($sql_test)[0]->count;
      } else {
        $sql_test =
            'SELECT count(distinct r.rid) as count ' .
            'FROM run r, task t, algorithm_setup s ' .
            'WHERE r.setup = s.sid AND r.task_id = t.task_id ' .
            $where_runs;
        $count = $this->Evaluation->query($sql_test)[0]->count;
      }
      if ($count > $result_limit) {
        if (!$per_fold) {
          $this->returnError(543, $this->version, $this->openmlGeneralErrorCode, 'Size of result set: ' . $count . ' runs; max size: ' . $result_limit);
          return;
        } else {
          // TODO: This must be extremely vague for the user. Will not scale as OpenML grows.
          $this->returnError(548, $this->version, $this->openmlGeneralErrorCode, 'Number of inspected run records: ' . $count . ' runs; max: ' . $result_limit);
          return;
        }
      }
    }

    $order_by = null;
    if ($sort_order) {
      $order_by = 'ORDER BY `e`.`value` ' . $sort_order;
    }

    if ($per_fold) {
      $eval_table = 'evaluation_fold';
      $columns = 'NULL as value, NULL as array_data, CONCAT("[", GROUP_CONCAT(e.value), "]") AS `values`';
      $group_by = 'GROUP BY r.rid, e.function_id, e.evaluation_engine_id';
    } else {
      $eval_table = 'evaluation';
      $columns = 'e.value, e.array_data, NULL AS `values`';
      $group_by = false;
    }

    // Note: the ORDER BY makes this query super slow because all data needs to be loaded. The query optimizer does not use the index correctly to avoid this.
    // It seems to be related to the inclusion of the math_function table (it causes MySQL to use filesort).
    // Solution is to force the index used in the run and evaluation table (or not use ORDER BY at all).
    // TODO: remove dependency on task_inputs and dataset table
    // TODO (2): transform into subquery where all columns except evaluation_fold are obtained in subquery (along with limit requirements, as MYSQL query optimizer does not seem to understand this query has an upper limit to the number of obtained runs that need to be inspected)
    $sql =
        'SELECT r.rid, r.task_id, r.start_time, r.uploader, s.implementation_id, s.sid, f.name AS `function`, i.fullName, d.did, d.name, e.evaluation_engine_id, ' . $columns . ' ' .
        'FROM run r, ' . $eval_table . ' e, algorithm_setup s, implementation i, dataset d, task t, task_inputs ti, math_function f ' .
        'WHERE r.setup = s.sid ' .
        'AND e.source = r.rid ' .
        'AND e.function_id = f.id ' .
        'AND s.implementation_id = i.id ' .
        'AND r.task_id = t.task_id ' .
        'AND r.task_id = ti.task_id ' .
        'AND ti.input = "source_data" ' .
        'AND ti.value = d.did ' . $where_total;

    if ($group_by) {
      $sql .= $group_by;
    }
    if ($order_by) {
      $sql .= $order_by;
    }
    if ($where_limit) {
      $sql .= $where_limit;
    }

    $res = $this->Evaluation->query($sql);

    if ($res == false) {
      $this->returnError(542, $this->version);
      return;
    }

    if ($show_params) {
      # 2 stage query .. unfortunately. Can break when too much results. let's take the damage for now
      $setup_ids = array();
      foreach ($res as $r) {
        $setup_ids[] = $r->sid;
      }
      $params = $this->Algorithm_setup->setup_ids_to_parameter_values(array_unique($setup_ids));
      for ($i = 0; $i < count($res); ++$i) {
        $res[$i]->parameters = $params[$res[$i]->sid];
      }
    }
    $this->xmlContents('evaluations', $this->version, array('evaluations' => $res));
  }
}
?>
