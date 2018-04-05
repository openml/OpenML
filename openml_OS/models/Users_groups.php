<?php
class Users_groups extends Community {
  
  function __construct() {
    parent::__construct();
    $this->table = 'users_groups';
    $this->id_column = 'id';
    $this->deleted_activated = 'id IS NOT NULL ';
  }
}
?>
