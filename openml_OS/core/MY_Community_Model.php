<?php
/* TODO: HACK. THIS CORE CLASS IS NOT AUTOLOADED BUT INCLUDED FROM MY MODEL. */
class MY_Community_Model extends MY_Database_Write_Model {
  
  protected $table;
  
  function __construct() {
    parent::__construct();
    $this->load->model('Database_singleton');
    $this->db = $this->Database_singleton->getOpenmlConnection();
  }
  
}
?>
