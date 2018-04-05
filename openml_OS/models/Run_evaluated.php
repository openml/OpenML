<?php

class Run_evaluated extends Database_write {
  function __construct() {
    parent::__construct();
    $this->table = 'run_evaluated';
    $this->id_column = array('run_id', 'evaluation_engine_id');
  }

  function getUnevaluatedRun($evaluation_engine_id, $order, $ttid = false, $task_ids = false, $tag = false, $uploader = false) {
    $this->db->from('`run` `r`');
    
    if ($ttid != false) {
      $this->db->join('`task` `t`', '`t`.`task_id` = `r`.`task_id`', 'inner');
      $this->db->where('`t`.`ttid` = "' . $ttid . '"');
    }
    
    if ($tag != false) {
      $this->db->join('`run_tag` `rt`', '`rt`.`id` = `r`.`rid`', 'inner');
      $this->db->where('`rt`.`tag` = "'.$tag.'"');
    }
    
    if ($uploader != false) {
      $this->db->where('`r`.`uploader` = "'.$uploader.'"');
    }
    
    if ($task_ids != false) {
      $this->db->where_in('`r`.`task_id`', $task_ids);
    }
    
    $this->db->join('`run_evaluated` `e`', '`r`.`rid` = `e`.`run_id` AND `e`.`evaluation_engine_id` = ' . $evaluation_engine_id, 'left');
    
    $this->db->where('(e.run_id IS NULL OR (e.error IS NOT NULL AND e.num_tries < ' . $this->config->item('process_run_tries') . ' AND e.evaluation_date < "' . now_offset('-' . $this->config->item('process_run_offset')) . '"))');
    
    // When random results are needed, to avoid that multiple evaluators evaluate the same run,
    // get 2000 unordered results and randomly select one (or more) of them.
    // This is much faster than randomizing the results in the query.
    $randomcount = 200;
    if ($order == 'random') {
      $this->db->limit($randomcount);
    } else {
      $this->db->limit('1');
    }
    
    // Reverse order if needed (slower query)
    if ($order == 'reverse') {
      $this->db->order_by('r.rid DESC');
    }

    // This always returns one result. Can easily be adapted to return multiple
    // results for batch processing
    $data = $this->db->select('r.*')->get();
    if ($data && $data->num_rows() > 0){
      if ($order == 'random'){
        $result = $data->result();
        return array($result[array_rand($result)]);
      } else {
        return $data->result();
      }
    }
  }
}
?>
