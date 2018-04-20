<?php
class Task_type extends MY_Database_Write_Model {
	
  function __construct() {
    parent::__construct();
    $this->table = 'task_type';
    $this->id_column = 'ttid';
  }
}
?>
