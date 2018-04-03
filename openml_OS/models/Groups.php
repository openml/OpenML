<?php
class Groups extends Community {
  
  function __construct() {
    parent::__construct();
    $this->table = 'groups';
    $this->id_column = 'id';
    $this->deleted_activated = 'id IS NOT NULL ';
  }
}
?>
