<?php
class Workflow extends MY_Database_Read_Model {
	
	function __construct()
    {
		parent::__construct();
		$this->table = 'workflow';
		$this->id_column = 'name';
    }
}
?>
