<?php
class Dataset_status extends MY_Database_Write_Model {

  function __construct() {
    parent::__construct();
    $this->table = 'dataset_status';
    $this->id_column = array('did', 'status');
  }
  
}
?>
