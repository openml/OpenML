<?php
class Workflow_setup extends MY_Database_Write_Model {
	
	function __construct() {
		parent::__construct();
		$this->table = 'workflow_setup';
		$this->id_column = 'sid';
    }
	
}
?>
