<?php
/* TODO: HACK. THIS CORE CLASS IS NOT AUTOLOADED BUT INCLUDED FROM MY MODEL. */
class MY_Tag_Model extends MY_Database_Write_Model {
  
  function __construct() {
    parent::__construct();
    $this->id_column = array('id', 'tag');
  }
  
  // @param: $tags (array)
  function get_ids($tags) {
    for($i = 0; $i < count($tags); ++$i) {
      $tags[$i] = trim($tags[$i]);
    }
    return $this->getColumnWhere('id', '`tag` IN ("'.implode('","',$tags).'")');
  }
}
?>
