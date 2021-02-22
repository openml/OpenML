<?php
class Dataset_description extends MY_Database_Write_Model {

  function __construct() {
    parent::__construct();
    $this->table = 'dataset_description';
    $this->id_column = array('did', 'version');
  }
  
}
?>
