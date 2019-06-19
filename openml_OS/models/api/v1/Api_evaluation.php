<?php
class Api_evaluation extends MY_Api_Model {

  protected $version = 'v1';

  function __construct() {
    parent::__construct();

    // load models
    $this->load->model('Evaluation');
  }

  function bootstrap($format, $segments, $request_type, $user_id) {
    $this->outputFormat = $format;

    $getpost = array('get','post');

    if (count($segments) >= 1 && $segments[0] == 'list') {
      array_shift($segments);
      $this->evaluation_list($segments, $user_id);
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


  private function evaluation_list($segs, $user_id) {
    $result_limit = 10000;
    $legal_filters = array('task', 'setup', 'flow', 'uploader', 'run', 'tag', 'limit', 'offset', 'function', 'per_fold', 'sort_order');
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

    if ($task_id === null && $setup_id === null && $implementation_id === null && $uploader_id === null && $run_id === null && $tag === null && $limit === null && $function_name === null) {
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
    $where_limit = $limit === null ? null : ' LIMIT ' . $limit;
    if ($limit && $offset) {
      $where_limit =  ' LIMIT ' . $offset . ',' . $limit;
    }
    $where_task_closed = ' AND (`t`.`embargo_end_date` is NULL OR `t`.`embargo_end_date` < NOW() OR `r`.`uploader` = '.$user_id.')';
    
    $where_runs = $where_task . $where_setup . $where_uploader . $where_impl . $where_run . $where_tag . $where_task_closed;
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
      'SELECT r.rid, r.task_id, r.start_time, s.implementation_id, s.sid, f.name AS `function`, i.fullName, d.did, d.name, e.evaluation_engine_id, ' . $columns . ' ' .
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

    $this->xmlContents('evaluations', $this->version, array('evaluations' => $res));
  }
}
?>
