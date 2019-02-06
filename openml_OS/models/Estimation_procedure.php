<?php
class Estimation_procedure extends MY_Database_Read_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'estimation_procedure';
    $this->id_column = 'id';
  }
  
  function sql_constraints($id, $ttid, 
                           $indices = array('type', 'repeats', 'folds', 'percentage', 'stratified_sampling'), 
                           $columns = array('type','repeats','folds','percentage','stratified_sampling'),
                           $explicit = false) {
    $evaluation_method = $this->getById($id);
    if ($evaluation_method == false) { return false; }
    if ($evaluation_method->ttid != $ttid) { return false; }
    
    $str = array();
    for ($i = 0; $i < count($indices); ++$i) {
      if ($explicit) {
        $str[] = $columns[$i] . (($evaluation_method->{$indices[$i]} == NULL) ? ' IS NULL ' : ' = "' . $evaluation_method->{$indices[$i]} . '" ');
      } else {
        if( $evaluation_method->{$indices[$i]} != NULL) $str[] = $columns[$i] . ' = "' . $evaluation_method->{$indices[$i]} . '" ';
      }
    }
    
    return implode(' AND ', $str);
  }
  
  // TODO: only used by rest_api. get rid of it whenever possible. 
  function get_by_parameters($ttid, $type, $repeats, $folds, $percentage, $stratified) {
    
    $task_type  = 'ttid = ' . $ttid;
    $type       = ' AND type  = "' . $type . '"';
    $repeats    = ' AND repeats ' . (($repeats == NULL) ? ' IS NULL ' : ' = ' . $repeats);
    $folds      = ' AND folds ' . (($folds == NULL) ? ' IS NULL ' : ' = ' . $folds);
    $percentage = ' AND percentage ' . (($percentage == NULL) ? ' IS NULL ' : ' = ' . $percentage);
    $stratified = ' AND stratified_sampling ' . (($stratified == NULL) ? ' IS NULL ' : ' = ' . $stratified);
    
    $ep = $this->getWhere($task_type . $type . $repeats . $folds . $percentage);
    if ($ep === false) {
      return false;
    } else {
      return end($ep);
    }
  }
  
  public function trainingset_size($datasetsize, $ep_procedure) {
    if ($ep_procedure->type == 'crossvalidation') {
      $folds = $ep_procedure->folds;
      $foldsize = ceil($datasetsize / $folds);
      return $foldsize * ($folds-1);
    } elseif ($ep_procedure->type == 'leaveoneout') {
      return $datasetsize - 1;
    } else {
      throw new Exception('Trainset size procedure not determined yet');
    }
  }
  
  public function number_of_samples($trainingsetsize) {
    $i = 0;
    for( ; $this->sample_size($i, $trainingsetsize) < $trainingsetsize; ++$i ) { }
    return $i + 1; // + 1 for considering the "full" training set
  }
  
  public function check_legal($ep_record, $dataset_size, $repeat_nr, $fold_nr, $sample_nr) {
    if ($fold_nr) {
      if ($ep_record->folds == null || $fold_nr >= $ep_record->folds || !$repeat_nr == null) {
        return false;
      }
    }
    if ($repeat_nr != null) {
      if ($ep_record->repeats == null || $repeat_nr >= $ep_record->repeats) {
        return false;
      }
    }
    if ($sample_nr != null) {
      if ($ep_record->samples != 'true') {
        return false;
      }
      $total_samples = $this->number_of_samples($this->trainingset_size($dataset_size, $ep_record));
      if ($sample_nr >= $total_samples) {
        return false;
      }
    }
    return true;
  }
  
  public function eval_measure_to_string($fn_name, $repeat_nr, $fold_nr, $sample_nr) {
    $res = $fn_name;
    if ($repeat_nr == null && $fold_nr == null && $sample_nr == null) {
      return $res . '(global)';
    }
    $res .= '(';
    if ($repeat_nr != null) {
      $res .= $repeat_nr;
    }
    if ($fold_nr != null) {
      $res .= ', ' . $fold_nr;
    }
    if ($sample_nr != null) {
      $res .= ', ' . $sample_nr;
    }
    return $res . ')';
  }
  
  private function sample_size($number, $trainingsetsize) {
    return min($trainingsetsize, round(pow(2, 6 + ($number * 0.5))));
  }
  
}
?>
