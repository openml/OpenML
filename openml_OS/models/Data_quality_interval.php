<?php
class Data_quality_interval extends Database_write {
  
  function __construct() {
    parent::__construct();
    $this->table = 'data_quality_interval';
    $this->id_column = array( 'data', 'quality', 'interval_start', 'interval_end' );
  }
}
?>
