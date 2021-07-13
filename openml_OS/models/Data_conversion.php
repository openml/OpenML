<?php
class Data_conversion extends MY_Database_Write_Model {

  function __construct() {
    parent::__construct();
    $this->table = 'data_conversion';
    $this->id_column = array('did');
  }
  
}
?>
