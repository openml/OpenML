<?php
class Input extends MY_Database_Write_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'input';
    $this->id_column = 'id';
  }
  
}
?>
