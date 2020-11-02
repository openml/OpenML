<?php
class Trace extends MY_Database_Write_Model {
	
  function __construct() {
    parent::__construct();
    $this->table = 'trace';
    $this->id_column = array('run_id','repeat','fold','iteration');
  }
}
?>
