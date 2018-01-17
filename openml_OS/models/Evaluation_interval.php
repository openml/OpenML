<?php
class Evaluation_interval extends Database_write {
	
  function __construct() {
    parent::__construct();
    $this->table = 'evaluation_interval';
    $this->id_column = array('did','function','label','interval_start','interval_end');
  }
}
?>
