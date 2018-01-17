<?php
class Trace extends Database_write {
	
  function __construct() {
    parent::__construct();
    $this->table = 'trace';
    $this->id_column = array('run_id','repeat','fold','iteration');
  }
}
?>
