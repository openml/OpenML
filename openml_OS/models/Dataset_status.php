<?php
class Dataset_status extends MY_Database_Write_Model {

  function __construct() {
    parent::__construct();
    $this->table = 'data_status';
    $this->id_column = array('did', 'status');
  }
  
}
?>
