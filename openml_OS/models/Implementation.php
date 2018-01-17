<?php
class Implementation extends Database_write {
	
	function __construct() {
		parent::__construct();
		$this->table = 'implementation';
		$this->id_column = 'id';
                $this->user_column = 'uploader';
    
    $this->load->model('File');
  }
  
  function getUploaderOf($fid){
      $sql = 'SELECT '.$this->user_column.' as uploader FROM '.$this->table.' WHERE '.$this->id_column.'='.$fid;
      
      return $this->Implementation->query($sql);
  }

  function getImplementationsOfUser($u_id, $from=null, $to=null){
      $sql = 'SELECT '.$this->id_column.' as id FROM '.$this->table.' WHERE '.$this->user_column.'='.$u_id;
      
      if($from!=null){
        $sql .= ' AND uploadDate>="'.$from.'"';
      }
      if($to!=null){
        $sql .= ' AND uploadDate<"'.$to.'"';
      }
      return $this->Implementation->query($sql);
  }
  
	function addComponent( $parent, $child, $identifier ) {
    $insert = array( 'parent' => $parent, 'child' => $child, 'identifier' => $identifier );
		return $this->db->insert( 'implementation_component', $insert );
	}
  
  function isComponent( $id ) {
    $parents = $this->query('SELECT * FROM implementation_component WHERE child = "'.$id.'"');
    return $parents == true;
  }
	
	function getByFullName( $fullName ) {
		$implementation = $this->getWhere('fullName = "'.$fullName.'"');
		if($implementation == false) return false;
		return $implementation[0];
	}

	function fullImplementation( $id ) {
	  $impls = $this->getWhere( 'id = ' . $id );
    if(count($impls) == 0) return false;
    $implementation = $impls[0];
    
		return ( $implementation == false ) ? false : $this->_extendImplementation($implementation);
	}
	
	function getComponents( $parent ) {
    $components = $this->Implementation_component->getWhere('parent = ' . $parent->id);
    if( is_array( $components ) ) {
      for( $i = 0; $i < count($components); $i++ ) {
        $implementation = $this->getById( $components[$i]->child );
        $components[$i]->implementation = $this->_extendImplementation( $implementation );
      }
      return $components;
    } else {
      return array();
    }
	}
  
  // TODO: test getComponentIds()
  function getComponentIds( $parent_id ) {
    $results = array();
    $components = $this->Implementation_component->getWhere('parent = ' . $parent_id);
    if( is_array( $components ) ) {
      foreach( $components as $c ) {
        $results[] = $c->child;
        $sub_components = $this->getComponentIds( $c->child );
        foreach( $sub_components as $s ) {
          if( in_array( $s, $results ) == false ) $results[] = $s;
        }
      }
    }
    return $results;
  }
  
  public function compareToXML( $xml, $implementation_id = false ) {
    $relevant = array('name','external_version'/*,'creator','contributor','description','fullDescription','installationNotes','dependencies','implements'*/);
    $where = array();
    if($implementation_id !== false)
      $where['id'] = $implementation_id;
    
    foreach( $relevant as $item ) {
      if( property_exists( $xml->children('oml', true), $item ) ) {
        if(in_array($item, array('creator','contributor') ) ) {
          $where[$item] = putcsv(xml_array_to_plain_array($xml, $item));
        } else {
          $where[$item] = trim($xml->children('oml', true)->$item);
        }
      } else {
        $where[$item] = null;
      }
    }
    $candidates = $this->Implementation->getColumnWhere('id', $where);
    if(is_array($candidates)) {
      return implode(',', $candidates);
    }
    // none of the implementations matched
    return false;
  }
	
	private function _extendImplementation( $implementation ) {
		$implementation->creator = getcsv( $implementation->creator );
		$implementation->contributor = getcsv( $implementation->contributor );
		$implementation->parameterSetting = $this->Input->getWhere( 'implementation_id = "' . $implementation->id . '"' );
		$implementation->components = $this->getComponents( $implementation );
		$implementation->tag = $this->Implementation_tag->getColumnWhere( 'tag', 'id = ' . $implementation->id );
    
    foreach( array('binary','source') as $type ) {
      if( $implementation->{$type.'_file_id'} != false ) {
        $file = $this->File->getById( $implementation->{$type.'_file_id'} );
        if( $file != false ) {
          $implementation->{$type.'Url'} = fileRecordToUrl( $file );
          $implementation->{$type.'Format'} = $file->extension;
          $implementation->{$type.'Md5'} = $file->md5_hash;
        }
      }
    }
		return $implementation;
	}
}
?>
