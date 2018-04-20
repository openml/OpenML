<?php
/* TODO: HACK. THIS CORE CLASS IS NOT AUTOLOADED BUT INCLUDED FROM MY MODEL. */
class MY_Community_Model extends MY_Model {
  
  protected $table;
  protected $include_deleted_activated;
  
  function __construct() {
    parent::__construct();
    $this->load->model('Database_singleton');
    $this->load->model('Log');
    $this->deleted_activated = 'deleted = "n" AND activated = "y" ';
    $this->db = $this->Database_singleton->getOpenmlConnection();
  }
  
  function query( $sql ) {
    $this->Log->sql( $sql);
    $data = $this->db->query( $sql );
    if($data === true || $data === false) return $data;
    return ( $data->num_rows() > 0 ) ? $data->result() : false;
  }
  
  function getById( $id, $orderby = null ) {
    if( is_numeric( $id ) == false )
      return false;
    
    $this->db->where( $this->deleted_activated . ' AND id = "'.$id.'"' );
    if( $orderby != null ) 
      $this->db->order_by( $orderby );
    $data = $this->db->get( $this->table );
    return ( $data->num_rows() > 0 ) ? $data->row() : false;
  }
  
  function getWhere( $where, $orderby = null ) {
    if( $orderby != null ) 
      $this->db->order_by( $orderby );
    $data = $this->db->where( $where )->get( $this->table );
    return ( $data->num_rows() > 0 ) ? $data->result() : false;
  }
  
  function getColumn( $column, $orderby = null) {
    if( $orderby != null ) 
      $this->db->order_by( $orderby );
    $data = $this->db->select( $column )->get( $this->table );
    $res = array();
    foreach( $data->result() as $row )
      $res[] = $row->{$column};
    
    return count( $res ) > 0 ? $res : false;
  }
  
  function getColumnWhere( $column, $where, $orderby = null ) {
    $this->db->where( $where );
    return $this->getColumn( $column, $orderby );
  }

  function get( $orderby = null ) {
    if( $this->deleted_activated ) {
      $this->db->where( $this->deleted_activated );
    }
    if( $orderby != null ) 
      $this->db->order_by( $orderby );
    $data = $this->db->get( $this->table );
    return ( $data->num_rows() > 0 ) ? $data->result() : false;
  }

  function insert( $data ) {
    $this->db->insert( $this->table, $data);
    return $this->db->insert_id();
  }
  
  function update( $id, $data ) {
    return $this->db->where( '`id` = ' . $id )->update( $this->table, $data );
  }
}
?>
