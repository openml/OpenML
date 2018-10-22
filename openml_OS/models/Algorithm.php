<?php
class Algorithm extends MY_Database_Read_Model {
	
  function __construct() {
    parent::__construct();
    $this->table = 'algorithm';
    $this->id_column = 'name';
  }
}
?>
