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
    if (count($segments) >= 3 && $segments[0] == 'request' && is_numeric($segments[1]) && in_array($segments[2], $order_values)) {
      array_shift($segments); // removes 'request'
      $eval_id = array_shift($segments);
      $order = array_shift($segments);

      $this->evaluation_request($eval_id, $order, $segments);
      return;
    }

    $this->returnError(100, $this->version);
  }

  private function evaluation_request($evaluation_engine_id, $order, $segs) {
    $legal_filters = array('ttid', 'task', 'tag', 'uploader');
    $query_string = array();
    for ($i = 0; $i < count($segs); $i += 2) {
      $query_string[$segs[$i]] = urldecode($segs[$i+1]);
      if (in_array($segs[$i], $legal_filters) == false) {
        $this->returnError(546, $this->version, $this->openmlGeneralErrorCode, 'Legal filter operators: ' . implode(',', $legal_filters) .'. Found illegal filter: ' . $segs[$i]);
        return;
      }
    }
    $ttid = element('ttid', $query_string, false);
    $tag = element('tag',$query_string, false);
    $uploader = element('uploader',$query_string, false);
    $task = element('task',$query_string, false);
    if ($task != false) {
      $task = explode(',', $task);
    }
    
    if ($ttid && !is_cs_natural_numbers($ttid)) {
      $this->returnError(547, $this->version);
      return;
    }
    $ttids = explode(',', $ttid);

    $res = $this->Run_evaluated->getUnevaluatedRun($evaluation_engine_id, $order, $ttids, $task, $tag, $uploader);
    if ($res == false) {
      $this->returnError(545, $this->version);
      return;
    }
    $this->xmlContents('evaluations-request', $this->version, array('res' => $res));
  }


  private function evaluation_list($segs, $user_id) {
    $legal_filters = array('task', 'setup', 'flow', 'uploader', 'run', 'tag', 'limit', 'offset', 'function');
    $query_string = array();
    for ($i = 0; $i < count($segs); $i += 2) {
      $query_string[$segs[$i]] = urldecode($segs[$i+1]);
      if (in_array($segs[$i], $legal_filters) == false) {
        $this->returnError(544, $this->version, $this->openmlGeneralErrorCode, 'Legal filter operators: ' . implode(',', $legal_filters) .'. Found illegal filter: ' . $segs[$i]);
        return;
      }
    }

    $task_id = element('task', $query_string);
    $setup_id = element('setup',$query_string);
    $implementation_id = element('flow',$query_string);
    $uploader_id = element('uploader',$query_string);
    $run_id = element('run',$query_string);
    $function_name = element('function',$query_string);
    $tag = element('tag',$query_string);
    $limit = element('limit',$query_string);
    $offset = element('offset',$query_string);

    if ($task_id == false && $setup_id == false && $implementation_id == false && $uploader_id == false && $run_id == false && $tag == false && $limit == false && $function_name == false) {
      $this->returnError( 540, $this->version );
      return;
    }

    if (!(is_safe($task_id) && is_safe($setup_id) && is_safe($implementation_id) && is_safe($uploader_id) && is_safe($run_id) && is_safe($function_name) && is_safe($tag) && is_safe($limit) && is_safe($offset))) {
      $this->returnError(541, $this->version );
      return;
    }

    $where_task = $task_id == false ? '' : ' AND `r`.`task_id` IN (' . $task_id . ') ';
    $where_setup = $setup_id == false ? '' : ' AND `r`.`setup` IN (' . $setup_id . ') ';
    $where_uploader = $uploader_id == false ? '' : ' AND `r`.`uploader` IN (' . $uploader_id . ') ';
    $where_impl = $implementation_id == false ? '' : ' AND `s`.`implementation_id` IN (' . $implementation_id . ') ';
    $where_run = $run_id == false ? '' : ' AND `r`.`rid` IN (' . $run_id . ') ';
    $where_function = $function_name == false ? '' : ' AND `f`.`name` = "' . $function_name . '" ';
    $where_tag = $tag == false ? '' : ' AND `r`.`rid` IN (select id from run_tag where tag="' . $tag . '") ';
    $where_limit = $limit == false ? '' : ' LIMIT ' . $limit;
    if($limit != false && $offset != false){
      $where_limit =  ' LIMIT ' . $offset . ',' . $limit;
    }
    $where_task_closed = ' AND (`t`.`embargo_end_date` is NULL OR `t`.`embargo_end_date` < NOW() OR `r`.`uploader` = '.$user_id.')';

    $where_runs = $where_task . $where_setup . $where_uploader . $where_impl . $where_run . $where_tag . $where_task_closed;

    //pre-test, should be quick??
    if($limit == false || (!$offset && $limit > 10000) || ($offset && $limit-$offset > 10000)) { // skip pre-test if less than 10000 are requested by definition
      $count = 0;
      //shortcuts
      if (!$implementation_id) {
        $sql_test =
          'SELECT count(distinct r.rid) as count ' .
          'FROM run r WHERE 1 ' . // 1 important to make concatenated query work
          $where_runs . $where_limit;
        $count = $this->Evaluation->query($sql_test)[0]->count;
      } else {
        $sql_test =
          'SELECT count(distinct r.rid) as count ' .
          'FROM run r, algorithm_setup s ' .
          'WHERE r.setup = s.sid ' .
          $where_runs .
          $where_limit ;
        $count = $this->Evaluation->query($sql_test)[0]->count;
      }
      if ($count > 10000) {
        $this->returnError(543, $this->version, $this->openmlGeneralErrorCode, 'Size of result set: ' . $count . ' runs; max size: 10000. Please use limit and offset. ');
        return;
      }
    }

    $where_total = $where_runs . $where_function;


    // Note: the ORDER BY makes this query super slow because all data needs to be loaded. The query optimizer does not use the index correctly to avoid this.
    // It seems to be related to the inclusion of the math_function table (it causes MySQL to use filesort).
    // Solution is to force the index used in the run and evaluation table (or not use ORDER BY at all).
    $sql =
      'SELECT r.rid, r.task_id, r.start_time, s.implementation_id, s.sid, f.name AS `function`, e.value, e.array_data, i.fullName, d.did, d.name ' .
      'FROM run r force index(PRIMARY), evaluation e force index(PRIMARY), algorithm_setup s, implementation i, dataset d, task_inputs t, math_function f ' .
      'WHERE r.setup = s.sid ' .
      'AND e.source = r.rid ' .
      'AND e.function_id = f.id ' .
      'AND s.implementation_id = i.id ' .
      'AND r.task_id = t.task_id ' .
      'AND t.input = "source_data" ' .
      'AND t.value = d.did ' . $where_total .
    //'ORDER BY r.rid' .
      $where_limit;
    $res = $this->Evaluation->query($sql);

    if ($res == false) {
      $this->returnError(542, $this->version);
      return;
    }

    $this->xmlContents('evaluations', $this->version, array('evaluations' => $res));
  }
}
?>
