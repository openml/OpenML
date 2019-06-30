<?php
class Task_study extends MY_Tag_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'task_study';
  }
  
  function get_entities($study_id) {
    $select = 't.task_id, value as data_id';
    $from = 'task_study ts';
    $conditions = array(
      'ts.study_id' => $study_id,
    );
    $this->db->select($select)->from($from)->join('task_inputs t', 'ts.task_id = t.task_id AND t.input = "source_data"', 'inner')->where($conditions);
    $data = $this->db->get();
    if ($data) {
      $result = $data->result();
      
      $task_ids = array();
      $data_ids = array();
      
      foreach ($result as $entry) {
        $task_ids[] = $entry->task_id;
        $data_ids[] = $entry->data_id;
      }
      
      return array(
        'data' => array_unique($data_ids),
        'tasks' => array_unique($task_ids),
      );
    }
    return false;
  }
}
?>
