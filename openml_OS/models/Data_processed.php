<?php
class Data_processed extends MY_Database_Write_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'data_processed';
    $this->id_column = array('did', 'evaluation_engine_id');
  }
  
}
?>
