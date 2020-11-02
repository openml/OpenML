<?php
class Task_io_types extends MY_Database_Write_Model {
	
	function __construct() {
    parent::__construct();
    $this->table = 'task_io_types';
    $this->id_column = 'name';
  }
  
}
?>
