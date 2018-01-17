<?php
class Task_type extends Database_write {
	
  function __construct() {
    parent::__construct();
    $this->table = 'task_type';
    $this->id_column = 'ttid';
  }
}
?>
