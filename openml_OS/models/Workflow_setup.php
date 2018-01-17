<?php
class Workflow_setup extends Database_write {
	
	function __construct() {
		parent::__construct();
		$this->table = 'workflow_setup';
		$this->id_column = 'sid';
    }
	
}
?>
