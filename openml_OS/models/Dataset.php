<?php
class Dataset extends MY_Database_Write_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'dataset';
    $this->id_column = 'did';
    $this->user_column = 'uploader';
  }
  
  function getUploaderOf($did){
      $sql = 'SELECT '.$this->user_column.' as uploader FROM '.$this->table.' WHERE '.$this->id_column.'='.$did;
      
      return $this->Dataset->query($sql);
  }
  
  function getDatasetsOfUser($u_id, $from=null, $to=null){
      $sql = 'SELECT '.$this->id_column.' as id FROM '.$this->table.' WHERE '.$this->user_column.'='.$u_id;
      
      if($from!=null){
        $sql .= ' AND upload_date>="'.$from.'"';
      }
      if($to!=null){
        $sql .= ' AND upload_date<"'.$to.'"';
      }
      
      //var_dump($sql);
      
      return $this->Dataset->query($sql);
  }
  
  // returns all dataset with a given feature $feature and data type $type.
  function getDatasetsWithFeature( $datasets, $feature, $type, $onlyOriginal = false ) {
    $sql = '
      SELECT `d`.`did` , `d`.`name` , `df`.`index` , `df`.`name` AS `feature` , `df`.`data_type`, `dq`.`value` AS `instances`
      FROM `dataset` AS `d` , `data_feature` AS `df`, `data_quality` AS `dq`
      WHERE `d`.`did` = `df`.`did`
      AND LOWER(`d`.`format`) = "arff"
      AND `dq`.`quality` = "NumberOfInstances"
      AND `dq`.`data` = `d`.`did`
      AND `df`.`data_type` IN ("'.implode('","',$type).'") 
      AND `df`.`name` = ' . ( $feature ? '"'.safe($feature).'" ' : '`d`.`default_target_attribute`').'
      AND `d`.`did` IN ('.implode(',',$datasets).') ';
    
    if($onlyOriginal) $sql .= ' AND `d`.`isOriginal` = "true"';
    return $this->Dataset->query($sql);
  }
  
  // given an array with qualities, and an array with predicates about these arrays, 
  // this function returns all datasets that comply to the predicates stated on these
  // predicates.
  function getDatasetWithQualities( $qualities, $predicates, $onlyOriginal = false, $restricted = false ) {
    $sql = 'SELECT `d`.`did`, `d`.`name`, `d`.`url`';
    for( $i = 0; $i < count( $qualities ); ++$i ) {
      $sql .= ', `dq' . $i . '`.`value` AS `' . $qualities[$i] . '`'; 
    }
    $sql .= "\nFROM `dataset` `d` \n";
    for( $i = 0; $i < count( $qualities ); ++$i ) {
      $sql .= 'LEFT JOIN `data_quality` `dq' . $i . '` ON `dq' . $i . '`.`data` = `d`.`did` '.
              'AND `dq' . $i . '`.`quality` = "' . $qualities[$i] . '"' . "\n";
    }
    $sql .= 'WHERE 1 ';
    for( $i = 0; $i < count( $qualities ); ++$i ) {
      if( $predicates[$i] != NULL ) {
        $sql .= 'AND `dq' . $i . '`.`value` ' . $predicates[$i] . "\n";
      }
    }
    if($onlyOriginal) $sql .= ' AND `d`.`isOriginal` = "true"';
    if($restricted) $sql .= ' AND `d`.`error` = "false" AND `d`.`processed` IS NOT NULL ';
    
    return $this->query( $sql );
  }
  
  // given an array of dataset identifiers the form dataset_name(dataset_version), this 
  // function returns all ids that comply to these datasets
  function nameVersionConstraints( $datasets, $namespace = 'dataset' ) {
    if(trim($datasets) === '') return '1';
    $datasets = explode( ',', $datasets );
    $constraint_string = '';
    foreach( $datasets as $dataset ) {
      $name = strstr( $dataset, '(', true );
      $version = substr( $dataset, strlen($name)+1, -1 );
      if(trim(safe($name)) && trim(safe($version))) {
        $constraint_string .= 'OR (`'.$namespace.'`.`name` = "'.trim(safe($name)).
          '" AND `'.$namespace.'`.`version` = "'.trim(safe($version)).'") ';
      }
    }
    $res = '(' . substr($constraint_string, 3) . ')';
    return $res;
  }
}
?>
