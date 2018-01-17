<?php
class Evaluation extends Database_write {
	
  function __construct() {
    parent::__construct();
    $this->table = 'evaluation';
    $this->id_column = array('did','function_id','label');
  }
  
  function getEvaluations($run_id) {
    $query = $this->db->select($this->table . '.*, `math_function`.`name`')->from($this->table . ', math_function')->where($this->table . '.`function_id` = `math_function`.`id` AND source = "' . $run_id . '"');
    $data = $this->db->get();
    return ( $data && $data->num_rows() > 0 ) ? $data->result() : false;
  }
}
?>
