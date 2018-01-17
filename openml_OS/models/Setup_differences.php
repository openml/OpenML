<?php
class Setup_differences extends Database_write {
  
  function __construct() {
    parent::__construct();
    $this->table = 'setup_differences';
    $this->id_column = array('sidA','sidB','task_id');
  }
  
}
?>
