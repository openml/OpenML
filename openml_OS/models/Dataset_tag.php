<?php
class Dataset_tag extends MY_Tag_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'dataset_tag';
  }
}
?>
