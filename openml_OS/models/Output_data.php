<?php
class Output_data extends MY_Database_Write_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'output_data';
    $this->id_column = array( 'run', 'data' );
  }
  
}
?>
