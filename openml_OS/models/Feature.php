<?php
class Feature extends Database_write {
	
	function __construct()
    {
		parent::__construct();
		$this->table = 'data_feature';
		$this->id_column = array( 'did', 'index' );
    }
}
?>
