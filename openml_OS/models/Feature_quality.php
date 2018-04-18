<?php
class Feature_quality extends MY_Database_Write_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'feature_quality';
    $this->id_column = array( 'data', 'feature_index', 'quality' );
  }
  
}
?>
