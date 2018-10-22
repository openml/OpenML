<?php
class Data_feature_value extends MY_Database_Write_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'data_feature_value';
    $this->id_column = array('did', 'index', 'value');
  }
  
}
?>
