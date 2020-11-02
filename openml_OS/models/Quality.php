<?php
class Quality extends MY_Database_Read_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'quality';
    $this->id_column = 'id';
  }
  
  function allUsed() {
    // this query selects only the data qualities that are actually used at least once
    $sql = '
      SELECT `q`.`name`, count(*) AS `number` 
      FROM `quality` `q`, `data_quality` `dq` 
      WHERE `q`.`type`= "DataQuality" 
      AND `q`.`name` = `dq`.`quality` 
      GROUP BY `q`.`name`';
    return $this->query( $sql );
    
  }
  
  function allFeatureQualitiesUsed() {
    // this query selects only the feature qualities that are actually used at least once
    $sql = '
      SELECT `q`.`name`, count(*) AS `number` 
      FROM `quality` `q`, `feature_quality` `fq` 
      WHERE `q`.`type`= "FeatureQuality" 
      AND `q`.`name` = `fq`.`quality` 
      GROUP BY `q`.`name`';
    return $this->query( $sql );
    
  }
}
?>
