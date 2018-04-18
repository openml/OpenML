<?php
class Input extends MY_Database_Write_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'input';
    $this->id_column = 'fullName';
  }

  function compareToXML( $xml, $implementation_id ) {
    $relevant = array('name' => 'name', 
                      'description' => 'description', 
                      'dataType' => 'data_type', 
                      'defaultValue' => 'default_value');
    
    $where = array('implementation_id' => $implementation_id);
    foreach( $relevant as $key => $item ) {
      if( property_exists( $xml->children('oml', true), $item ) ) {
        $where[$key] = trim($xml->children('oml', true)->$item);
      } else {
        $where[$key] = null;
      }
    }
    $result = $this->getWhere($where);
    if( $result != false ) {
      return $result[0]->fullName;
    } else {
      return false;
    }
  }
}
?>
