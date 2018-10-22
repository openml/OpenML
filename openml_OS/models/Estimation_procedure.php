<?php
class Estimation_procedure extends MY_Database_Read_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'estimation_procedure';
    $this->id_column = 'id';
  }
  
  function sql_constraints( $id, $ttid, 
                            $indices = array('type', 'repeats', 'folds', 'percentage', 'stratified_sampling'), 
                            $columns = array('type','repeats','folds','percentage','stratified_sampling'),
                            $explicit = false ) {
    $evaluation_method = $this->getById( $id );
    if( $evaluation_method == false ) return false;
    if( $evaluation_method->ttid != $ttid ) return false;
    
    $str = array();
    for( $i = 0; $i < count($indices); ++$i ) {
      if( $explicit ) {
        $str[] = $columns[$i] . (($evaluation_method->{$indices[$i]} == NULL) ? ' IS NULL ' : ' = "' . $evaluation_method->{$indices[$i]} . '" ');
      } else {
        if( $evaluation_method->{$indices[$i]} != NULL) $str[] = $columns[$i] . ' = "' . $evaluation_method->{$indices[$i]} . '" ';
      }
    }
    
    return implode( ' AND ', $str );
  }
  
  // TODO: only used by rest_api. get rid of it whenever possible. 
  function get_by_parameters( $ttid, $type, $repeats, $folds, $percentage, $stratified ) {
    
    $task_type  = 'ttid = ' . $ttid;
    $type       = ' AND type  = "' . $type . '"';
    $repeats    = ' AND repeats ' . (($repeats == NULL) ? ' IS NULL ' : ' = ' . $repeats);
    $folds      = ' AND folds ' . (($folds == NULL) ? ' IS NULL ' : ' = ' . $folds);
    $percentage = ' AND percentage ' . (($percentage == NULL) ? ' IS NULL ' : ' = ' . $percentage);
    $stratified = ' AND stratified_sampling ' . (($stratified == NULL) ? ' IS NULL ' : ' = ' . $stratified);
    
    $ep = $this->getWhere( $task_type . $type . $repeats . $folds . $percentage );
    if($ep === false) 
      return false;
    else
      return end($ep);
  }
  
  function splits_arff_size( $ep, $instances ) {
    if( $ep->type == 'crossvalidation' ) {
      return $instances * $ep->folds * $ep->repeats;
    } elseif( $ep->type == 'leaveoneout' ) {
      return $instances * $instances;
    } elseif( $ep->type == 'holdout' ) {
      return $instances * $ep->repeats;
    } elseif( $ep->type == 'testthentrain' ) {
      return 0;
    } elseif( $ep->type == 'learningcurve' ) {
      $foldsize = ceil($instances / $ep->folds);
      $trainingsetsize = $foldsize * ($ep->folds-1);
      $totalsize = 0;
      for( $i = 0; $i < $this->number_of_samples( $trainingsetsize ); ++$i ) {
        // size of the sample and size of the 'test set'
        $totalsize += $this->sample_size($i, $trainingsetsize ) + $foldsize;
      }
      return $totalsize * $ep->folds * $ep->repeats; 
    } else {
      // TODO: implement other types.
      return -1;
    }
  }
  
  public function trainingset_size( $datasetsize, $folds ) {
    $foldsize = ceil($datasetsize / $folds);
    return $foldsize * ($folds-1);
  }
  
  public function number_of_samples( $trainingsetsize ) {
    $i = 0;
    for( ; $this->sample_size( $i, $trainingsetsize ) < $trainingsetsize; ++$i ) { }
    return $i + 1; // + 1 for considering the "full" training set
  }
    
  private function sample_size( $number, $trainingsetsize ) {
    return min( $trainingsetsize, round( pow( 2, 6 + ( $number * 0.5 ) ) ) );
  }
  
  function toString( $ttep ) {
    $res = $ttep->type;
    if( $ttep->repeats != null )
      $res .= '_' . $ttep->repeats;
    if( $ttep->folds != null )
      $res .= '_' . $ttep->folds;
    if( $ttep->percentage != null )
      $res .= '_' . $ttep->percentage;
    return $res;
  }
}
?>
