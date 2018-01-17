<?php
class Input_setting extends Database_write {
	
	function __construct()
    {
		parent::__construct();
		$this->table = 'input_setting';
		$this->id_column = array( 'setup', 'input' );
    }
}
?>
