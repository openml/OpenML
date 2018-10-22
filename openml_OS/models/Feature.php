<?php
class Feature extends MY_Database_Write_Model {
	
	function __construct()
    {
		parent::__construct();
		$this->table = 'data_feature';
		$this->id_column = array( 'did', 'index' );
    }
}
?>
