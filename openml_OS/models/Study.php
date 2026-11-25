<?php
class Study extends MY_Database_Write_Model {

  function __construct() {
    parent::__construct();
    $this->table = 'study';
    $this->id_column = 'id';
    $this->load->model('Study_tag');
  }

  function legacy_create($name, $alias, $description, $creator) {
    die("Creation of legacy studies is no longer supported.");
  }
  
}
?>
