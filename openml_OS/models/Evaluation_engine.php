<?php
class Evaluation_engine extends MY_Database_Read_Model {
	
  function __construct() {
    parent::__construct();
    $this->table = 'evaluation_engine';
    $this->id_column = 'id';
  }
}
?>
