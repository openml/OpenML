<?php
/* TODO: HACK. THIS CORE CLASS IS NOT AUTOLOADED BUT INCLUDED FROM MY MODEL. */
class MY_Database_Write_Model extends MY_Database_Read_Model {
  
  function __construct() {
    parent::__construct();
    $this->db = $this->Database_singleton->getWriteConnection();
  }

  function insert($data) {
    $res = $this->db->insert( $this->table, $data);
    $insert_id = $this->db->insert_id();
    if( $insert_id ) {
      return $insert_id;
    } else {
      return $res;
    }
  }

  function insert_batch($data) {
    $this->db->trans_start();
    foreach ($data as $item) {
      $insert_query = $this->db->insert_string( $this->table, $item);
      $insert_query = str_replace('INSERT INTO', 'INSERT IGNORE INTO', $insert_query);
      $this->db->query($insert_query);
    }
    $this->db->trans_complete();
    
    if($data) {
      return true;
    } else {
      return false;
    }
  }

  function insert_ignore($data) {
    $insert_query = $this->db->insert_string($this->table, $data);
    $insert_query = str_replace('INSERT INTO', 'INSERT IGNORE INTO', $insert_query);
    $this->db->query($insert_query);
    return $this->db->insert_id();
  }
  
  function replace($data) {
    return $this->db->replace($this->table, $data);
  }
  
  function update($id, $data) {
    return $this->db->where($this->_where_clause_on_id($id))->update($this->table, $data);
  }
  
  function delete($id) {
    return $this->db->delete($this->table, $this->_where_clause_on_id($id));
  }
  
  function deleteWhere($clause) {
    return $this->db->where($clause)->delete($this->table);
  }
}

?>
