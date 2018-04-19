<?php
class Estimation_procedure_type extends MY_Database_Read_Model {
	
  function __construct() {
    parent::__construct();
    $this->table = 'estimation_procedure_type';
    $this->id_column = 'name';
  }
}
?>
