<?php
class Implementation_component extends Database_write {
	
  function __construct() {
    parent::__construct();
    $this->table = 'implementation_component';
    $this->id_column = array('parent','child');
  }
  
  function compareToXML( $xml, $component_pid ) {
    $relevant = array('identifier');
    $where = array( 'parent' => $component_pid );
    
    foreach( $relevant as $item ) {
      if( property_exists( $xml->children('oml', true), $item ) ) {
        $where[$item] = trim($xml->children('oml', true)->$item);
      } else {
        $where[$item] = null;
      }
    }
    $candidates = $this->Implementation_component->getWhere($where);
    
    if( $candidates ) {
      foreach( $candidates as $candidate ) {
        if( $this->Implementation->compareToXML( $xml->children('oml', true)->implementation, $candidate->child ) )
          return $candidate->child;
      }
    }
    return false;
  }
}
?>
