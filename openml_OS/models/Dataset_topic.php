<?php
class Dataset_topic extends Database_write {

  function __construct() {
    parent::__construct();
    $this->table = 'dataset_topic';
    $this->id_column = array('id', 'topic');
  }
  
}
?>
