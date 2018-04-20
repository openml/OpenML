<?php
class Meta_dataset extends MY_Community_Model {
	
	function __construct() {
		parent::__construct();
		$this->table = 'meta_dataset';
    $this->deleted_activated = null;
  }
	
  
}
?>
