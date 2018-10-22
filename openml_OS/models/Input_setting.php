<?php
class Input_setting extends MY_Database_Write_Model {
	
	function __construct()
    {
		parent::__construct();
		$this->table = 'input_setting';
		$this->id_column = array( 'setup', 'input' );
    }
}
?>
