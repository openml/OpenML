<?php
class Evaluation_engine extends Database_read {
	
  function __construct() {
    parent::__construct();
    $this->table = 'evaluation_engine';
    $this->id_column = 'id';
  }
}
?>
