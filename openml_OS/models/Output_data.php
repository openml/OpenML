<?php
class Output_data extends Database_write {
  
  function __construct() {
    parent::__construct();
    $this->table = 'output_data';
    $this->id_column = array( 'run', 'data' );
  }
  
}
?>
