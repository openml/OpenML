<?php
class Math_function extends Database_read {
	
	function __construct() {
    parent::__construct();
    $this->table = 'math_function';
    $this->id_column = 'name';
  }
}
?>
