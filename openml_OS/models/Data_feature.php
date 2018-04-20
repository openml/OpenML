<?php
class Data_feature extends MY_Database_Write_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'data_feature';
    $this->id_column = array( 'did', 'index' );
    }

  function getByDid( $did ) {
    if( is_numeric( $did ) === false ) {
      return false;
    } else {
      return $this->getWhere( 'did = ' . $did );
    }
  }
}
?>
