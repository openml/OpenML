<?php
class Run_study extends MY_Tag_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'run_study';
  }
  
  function get_entities($study_id) {
    $select = 't.task_id, s.setup_id, i.implementation_id AS flow_id, value as data_id';
    $from = 'run_study rs, algorithm_setup s, run r';
    $conditions = array(
      'rs.study_id' => $study_id,
      'rs.run_id' => 'r.rid',
      'r.setup' => 's.sid',
      't.task_id' => 't.task_id',
      
    );
    $this->db->select($select)->from($from)->join('task_inputs t', 'r.task_id = t.task_id AND t.input = "source_data"', 'left')->where($conditions);
    return $this->db->get();
  }
}
?>
