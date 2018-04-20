<?php
class Runfile extends MY_Database_Write_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'runfile';
    $this->id_column = array('source', 'field');
    
    $this->load->model('File');
  }
}
?>
