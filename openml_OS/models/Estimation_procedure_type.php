<?php
class Estimation_procedure_type extends Database_read {
	
  function __construct() {
    parent::__construct();
    $this->table = 'estimation_procedure_type';
    $this->id_column = 'name';
  }
}
?>
