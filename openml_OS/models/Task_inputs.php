<?php
class Task_inputs extends Database_write {
	
	function __construct() {
		parent::__construct();
		$this->table = 'task_inputs';
		$this->id_column = array('task_id', 'input');
    }

	function getTaskValuesAssoc($task_id) {
		$values = $this->getWhere('task_id = ' . $task_id);
		$res = array();
    if(is_array($values)) {
		  foreach($values as $value) {
			  $res[$value->input] = $value->value;
		  }
    }
		return $res;
	}
}
?>
