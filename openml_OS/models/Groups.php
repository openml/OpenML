<?php
class Groups extends MY_Community_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'groups';
    $this->id_column = 'id';
    $this->deleted_activated = 'id IS NOT NULL ';
  }
}
?>
