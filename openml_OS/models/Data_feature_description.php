<?php
class Data_feature_description extends MY_Database_Write_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'data_feature_description';
    $this->id_column = array('did', 'index', 'value');
  }
}
?>
