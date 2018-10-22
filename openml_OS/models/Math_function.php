<?php
class Math_function extends MY_Database_Read_Model {
	
	function __construct() {
    parent::__construct();
    $this->table = 'math_function';
    $this->id_column = 'name';
  }
}
?>
