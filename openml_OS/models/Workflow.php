<?php
class Workflow extends Database_read {
	
	function __construct()
    {
		parent::__construct();
		$this->table = 'workflow';
		$this->id_column = 'name';
    }
}
?>
