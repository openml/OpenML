<?php
class Input_data extends Database_write {
  
  function __construct() {
    parent::__construct();
    $this->table = 'input_data';
    $this->id_column = array( 'run', 'data' );
  }
  
}
?>
