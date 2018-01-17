<?php
class Task_io_types extends Database_write {
	
	function __construct() {
    parent::__construct();
    $this->table = 'task_io_types';
    $this->id_column = 'name';
  }
  
}
?>
