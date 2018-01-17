<?php
class Algorithm extends Database_read {
	
  function __construct() {
    parent::__construct();
    $this->table = 'algorithm';
    $this->id_column = 'name';
  }
}
?>
