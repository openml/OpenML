<?php

class Run_evaluated extends MY_Database_Write_Model {
  function __construct() {
    parent::__construct();
    $this->table = 'run_evaluated';
    $this->id_column = array('run_id', 'evaluation_engine_id');
  }

  function getUnevaluatedRun($evaluation_engine_id, $order, $num_requests, $ttids = false, $task_ids = false, $tag = false, $uploader = false) {
    $this->db->from('`run` `r`');
    
    if ($ttids != false) {
      $this->db->join('`task` `t`', '`t`.`task_id` = `r`.`task_id`', 'inner');
      $this->db->where_in('`t`.`ttid`', $ttids);
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
    
    $this->db->where('(`e`.`run_id` IS NULL OR (`e`.`error` IS NOT NULL AND `e`.`num_tries` < ' . $this->config->item('process_run_tries') . ' AND `e`.`evaluation_date` < "' . now_offset('-' . $this->config->item('process_run_offset')) . '"))');
    
    $limit_count = $num_requests;
    $this->db->limit($limit_count);
    
    // Reverse order if needed (slower query)
    if ($order == 'reverse') {
      $this->db->order_by('`r`.`rid` DESC');
    } elseif($order == 'random') {
      $this->db->order_by('RAND()');
    }

    // This always returns one result. Can easily be adapted to return multiple
    // results for batch processing
    $data = $this->db->select('r.*')->get();
    if ($data && $data->num_rows() > 0) {
      return $data->result();
    }
    return false;
  }
}
?>
