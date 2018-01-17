<?php
class Runfile extends Database_write {
  
  function __construct() {
    parent::__construct();
    $this->table = 'runfile';
    $this->id_column = array('source', 'field');
    
    $this->load->model('File');
  }
}
?>