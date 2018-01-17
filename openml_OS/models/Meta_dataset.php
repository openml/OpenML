<?php
class Meta_dataset extends Community {
	
	function __construct() {
		parent::__construct();
		$this->table = 'meta_dataset';
    $this->deleted_activated = null;
  }
	
  
}
?>
