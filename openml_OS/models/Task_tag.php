<?php
class Task_tag extends MY_Tag_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'task_tag';
  }
}
?>
