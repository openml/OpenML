<?php
/* TODO: HACK. THIS CORE CLASS IS NOT AUTOLOADED BUT INCLUDED FROM MY MODEL. */
class MY_Database_Read_Model extends MY_Model {
  
  protected $table;
  protected $id_column;
  
  function __construct() {
    parent::__construct();
    $this->load->model('Log');
    $this->load->model('Database_singleton');
    $this->db = $this->Database_singleton->getReadConnection();
  }

  function get($orderby = null, $limit = null, $offset = null) {
    if ($orderby != null) {
      $this->db->order_by($orderby);
    }
    if ($limit) {
      $this->db->limit($limit);
    }
    if ($offset) {
      $this->db->offset($offset);
    }
    $data = $this->db->get($this->table);
    return ($data->num_rows() > 0) ? $data->result() : false;
  }
  
  function query($sql) {
    $this->Log->sql($sql);
    $data = $this->db->query($sql);
    if ($data === true || $data === false) { return $data; } 
    // although the expression above doesn't seem to make sense, 
    // it actually does. Since we used the === operator, we have 
    // covered all boolean responses. Now let's cover the other 
    // possibilities.
    return ($data->num_rows() > 0) ? $data->result() : false;
  }
  
  function getWhere($where, $orderby = null, $limit = null, $offset = null) {
    $this->db->where($where);
    return $this->get($orderby, $limit, $offset);
  }
  
  function getById($id, $orderby = null) {
    $data = $this->getWhere($this->_where_clause_on_id($id) , $orderby);
    return ($data !== false) ? $data[0] : false;
  }
  
  function getWhereSingle($where, $orderby = null) {
    $this->db->limit(1,0);
    $data = $this->getWhere($where, $orderby);
    return ($data !== false) ? end($data) : false;
  }
  
  function getColumn($column, $orderby = null, $limit = null, $offset = null) {
    $data = $this->get($orderby, $limit, $offset);
    if ($data == false) return false;
    $res = array();
    foreach ($data as $row) {
      $res[] = $row->{$column};
    }
    return $res;
  }
  
  function getColumnWhere($column, $where, $orderby = null, $limit = null, $offset = null) {
    $this->db->where($where);
    return $this->getColumn($column, $orderby, $limit, $offset);
  }
  
  function getColumnFunction($function, $orderby = null) {
    if ($orderby != null) {
      $this->db->order_by( $orderby );
    }
    $data = $this->db->select( $function . ' AS `name`', false )->get($this->table);
    $res = array();
    foreach($data->result() as $row) {
      $res[] = $row->{'name'};
    }
    return count($res) > 0 ? $res : false;
  }
  
  function getColumnFromSql($column, $sql) {
    $res = array();
    $data = $this->query($sql);
    if (!$data) return false;
    foreach($data as $row) {
      $res[] = $row->{$column};
    }
    return $res;
  }
  
  function getColumnFunctionWhere($function, $where, $orderby = null) {
    $this->db->where($where);
    return $this->getColumnFunction($function, $orderby);
  }
  
  function getAssociativeArray($key, $value, $where, $group_by = null, $orderby = null, $limit = null, $offset = null) {
    $this->db->select($key . ' AS `key`, ' . $value . ' AS `value`', false);
    if($group_by) {
      $this->db->group_by($group_by);
    }
    if ($where) {
      $data = $this->getWhere($where, $orderby, $limit, $offset);
    } else {
      $data = $this->get($orderby, $limit, $offset);
    }
    
    if($data === false) { return false; }
    
    $res = array();
    foreach($data as $item) {
      $res[$item->{'key'}] = $item->{'value'};
    }
    return $res;
  }
  
  function getDistinct( $column, $excludeEmpty = true ) {
    if( $excludeEmpty ) {
      $this->db->where( $column . ' IS NOT NULL AND ' . $column . ' != ""' );
    }
    $this->db->distinct();
    return $this->getColumn( $column );
  }
  
  function getColumns( $columns, $orderby = null ) {
    if( $orderby != null ) {
      $this->db->order_by( $orderby );
    }
    $data = $this->db->select( $columns, false )->get( $this->table );
    return $data->num_rows() > 0 ? $data->result() : false;
  }
  
  function getColumnsWhere( $columns, $where, $orderby = null ) {
    $this->db->where( $where );
    return $this->getColumns( $columns, $orderby );
  }
  
  function numberOfRecords() {
    return $this->db->count_all($this->table);
  }
  
  function getHighestIndex( $tables, $column ) {
    $res = array();
    foreach( $tables as $table ) {
      $data = $this->db->select_max( $column )->get( $table )->row();
      $res[] = $data->{$column};
    }    
    return max( $res ) + 1;
  }
  
  function mysqlErrorNo() {
    return $this->db->error()['code'];
  }
  
  function mysqlErrorMessage() {
    return $this->db->error()['message'];
  }
  
  function incrementVersionNumber( $name ) {
    $data = $this->getWhere( 'name = "' . $name . '"'  );
    if( $data === false ) {
      return 1;
    } 
    $highest = $data[0];
    for( $i = 1; $i < count($data); $i++ ) {
      if( version_compare( $highest->version, $data[$i]->version ) < 0 ) {
        $highest = $data[$i];
      }
    }
    
    $newVersion = explode( '.', $highest->version );
    return $newVersion[0] + 1;
  }
  
  protected function _where_clause_on_id($id_value) {
    $where_clauses = array();
    if(is_array($this->id_column)) {
      if(is_array($id_value) && count($id_value) == count($id_value) ) {
        for($i = 0; $i < count($this->id_column); $i++ ) {
          $where_clauses[] = '`'.$this->id_column[$i].'` = "'.$id_value[$i].'"';
        }
      } else {
        die('Function ' . get_class($this) . '::where_clause_on_id abused. Please fix. ');
      }
    } else {
      if(is_array($id_value)) {
        die('Function ' . get_class($this) . '::where_clause_on_id abused. Please fix. ');
      } else {
        $where_clauses[] = '`'.$this->id_column.'` = "'.$id_value.'"';
      }
    }
    $res = implode( ' AND ', $where_clauses );
    return $res;
  }
}
?>
