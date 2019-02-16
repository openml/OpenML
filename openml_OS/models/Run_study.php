<?php
class Run_study extends MY_Tag_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'run_study';
  }
  
  function get_entities($study_id) {
    $select = 't.task_id, s.sid AS setup_id, i.implementation_id AS flow_id, value as data_id';
    $from = 'run_study rs, algorithm_setup s, run r';
    $conditions = '`rs`.`study_id` = ' . $study_id . ' ' .
      'AND `rs`.`run_id` = `r`.`rid` ' .
      'AND `r`.`setup` = `s`.`sid` ' .
      'AND `t`.`task_id` = `t`.`task_id` ';
    
    $this->db->select($select)->from($from)->join('task_inputs t', 'r.task_id = t.task_id AND t.input = "source_data"', 'left')->where($conditions);
    $data = $this->db->get();
    if ($data) {
      $result = $data->result();
      
      $task_ids = array();
      $data_ids = array();
      $flow_ids = array();
      $setup_ids = array();
      $run_ids = array();
      
      foreach ($result as $entry) {
        $task_ids[] = $entry->task_id;
        $data_ids[] = $entry->data_id;
        $flow_ids[] = $entry->flow_id;
        $setup_ids[] = $entry->setup_id;
        $run_ids[] = $entry->run_id;
      }
      
      return array(
        'data' => array_unique($data_ids),
        'tasks' => array_unique($task_ids),
        'flows' => array_unique($flow_ids),
        'setups' => array_unique($setup_ids),
        'runs' => array_unique($run_ids),
      );
    }
    return false;
  }
}
?>
