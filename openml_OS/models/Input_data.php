<?php
class Input_data extends MY_Database_Write_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'input_data';
    $this->id_column = array( 'run', 'data' );
  }
  
}
?>
