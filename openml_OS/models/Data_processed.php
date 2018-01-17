<?php
class Data_processed extends Database_write {
  
  function __construct() {
    parent::__construct();
    $this->table = 'data_processed';
    $this->id_column = array('did', 'evaluation_engine_id');
  }
  
}
?>
