<?php
class Dataset_topic extends MY_Database_Write_Model {

  function __construct() {
    parent::__construct();
    $this->table = 'dataset_topic';
    $this->id_column = array('id', 'topic');
  }
  
}
?>
