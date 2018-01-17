<?php
class Data_quality extends Database_write {

  function __construct() {
    parent::__construct();
    $this->table = 'data_quality';
    $this->id_column = array('data', 'quality', 'label');
  }

  function getByDid( $did ) {
    if(is_numeric($did) === false) {
      return false;
    } else {
      return $this->getWhere('data = ' . $did);
    }
  }

  function getQualitiesOrderedByPriority($id){
    $data = $this->db->select('`name`, `value`')->from('`data_quality`, `quality`')->where('`data_quality`.`quality` = `quality`.`name`')->where('`data`',$id)->order_by('`priority`','ASC')->get();
    return ($data && $data->num_rows() > 0) ? $data->result() : false;
  }

  function getFeature($did, $quality, $label = false) {
    if(is_numeric($did) === false) {
      return false;
    } else {
      $constraints = 'data = ' . $did . ' AND quality = "' . $quality . '"';
      if($label !== false) {
        $constraints .= ' AND label = "' . $label . '"';
      }
      $res = $this->getWhere($constraints);
      if($res === false) {
        return false;
      } else {
        return $res[0]->value;
      }
    }
  }
}
?>
