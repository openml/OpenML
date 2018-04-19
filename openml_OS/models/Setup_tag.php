<?php
class Setup_tag extends MY_Tag_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'setup_tag';
  }
}
?>
