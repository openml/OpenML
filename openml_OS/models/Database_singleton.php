<?php

class Database_singleton extends CI_Model {
  
  function __construct() {
    parent::__construct();
    $this->db_read = $this->load->database('read',true);
    $this->db_write = $this->load->database('write',true);
    $this->db_openml = $this->load->database('default',true);
  }
  
  function getReadConnection() {
    return $this->db_read;
  }
  
  function getWriteConnection() {
    return $this->db_write;
  }
  
  function getOpenmlConnection() {
    return $this->db_openml;
  }
}

?>
