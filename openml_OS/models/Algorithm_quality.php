<?php
class Algorithm_quality extends MY_Database_Write_Model {
	
  function __construct() {
    parent::__construct();
    $this->table = 'algorithm_quality';
    $this->id_column = array('implementation_id','quality','label');
  }
}
?>
