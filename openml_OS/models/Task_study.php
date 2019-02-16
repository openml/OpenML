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
    $this->db->select($select)->from($from)->join('task_inputs t', 'ts.task_id = t.task_id AND t.input = "source_data"', 'left')->where($conditions);
    return $this->db->get();
  }
}
?>
