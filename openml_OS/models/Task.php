<?php
class Task extends Database_write {
  
  function __construct() {
    parent::__construct();
    $this->table = 'task';
    $this->id_column = 'task_id';
    $this->user_column = 'creator';
    
    $this->load->model('Estimation_procedure');
    $this->load->model('Task_inputs');
  }
  
  function getUploaderOf($tid){
      $sql = 'SELECT '.$this->user_column.' as uploader FROM '.$this->table.' WHERE '.$this->id_column.'='.$tid;
      
      return $this->Task->query($sql);
  }
  
  function getTasksByDataId($did){
      $sql = 'SELECT '.$this->id_column.' as id FROM `task_inputs` WHERE task_inputs.value='.$did.' AND task_inputs.input="source_data"';
      
      return $this->Task->query($sql);      
  }
  
  function getTasksOfUser($u_id, $from=null, $to=null){
      $sql = 'SELECT '.$this->id_column.' as id FROM '.$this->table.' WHERE '.$this->user_column.'='.$u_id;
      
      if($from!=null){
        $sql .= ' AND creation_date>="'.$from.'"';
      }
      if($to!=null){
        $sql .= ' AND creation_date<"'.$to.'"';
      }
      return $this->Task->query($sql);
  }
  
  function search( $task_type_id, $keyValues ) {
    // function that searches through the tasks, based on the values in the task_inputs table.
    // source_data is automatically added, because every task has source data. 
    // source_data is searched by dataset name, other fields by the value of the task_inputs table
    $sql = 'SELECT `task`.`task_id`, `task`.`ttid`, CONCAT("Task ", `task`.`task_id`, ": ", `task_type`.`name`, " - ", `dataset`.`name`) AS `name`';
    foreach( $keyValues as $key => $value ) {
      $sql .= ', `'.$key.'`.`value` AS `'.$key.'`';
    }
    
    $sql .= ' FROM `task_type`, `dataset`, `task_inputs` AS `source_data`, `task` LEFT JOIN (SELECT `task_id`, count(*) AS `inputs_count` FROM `task_inputs` GROUP BY `task_id`) AS `c` ON `c`.`task_id` = `task`.`task_id` ';
    foreach( $keyValues as $key => $value ) {
      if($key == "source_data") continue; // already added hardcoded
      $sql .= ', `task_inputs` AS `'.$key.'`';
    }
    
    $sql .= ' WHERE `task`.`ttid` = `task_type`.`ttid`'.
            ' AND `task`.`ttid` = "' . $task_type_id . '"'.
            ' AND `dataset`.`did` = `source_data`.`value`'.
            ' AND `task`.`task_id` = `source_data`.`task_id`'.
            ' AND `source_data`.`input` = "source_data"';
    
    foreach( $keyValues as $key => $value ) {
      $sql .= ' AND `task`.`task_id` = `' . $key . '`.`task_id` AND `' . $key . '`.`input` = "' . $key . '" AND (`' . $key . '`.`value` = "' . $value . '")'; 
    }
	
	$sql .= ' AND `c`.`inputs_count` = ' . count($keyValues);
    
    return $this->query( $sql );
  }
  
  function create_batch( $ttid, $task_batch ) {
    $result = array();
    $to_insert = array();
    $existing_tasks = $this->tasks_crosstabulated( $ttid );
 
    if( $existing_tasks == false ) { $existing_tasks = array(); }
    foreach( $task_batch as $task ) {
      $current_task_obj = json_decode(json_encode($task), false); // convert array to obj, using json lib
      
      if( in_array( $current_task_obj, $existing_tasks ) == false ) {
        $task_id = $this->insert( array( 'ttid' => $ttid, 'creation_date' => now() ) );
        foreach( $task as $key => $value ) {
          $to_insert[] = array( 'task_id' => $task_id, 'input' => $key, 'value' => $value );
        }
        // additional hidden inputs, specific for "official" OpenML inputs.
        // TODO: integrate?
        if( $ttid == 3 ) {
          // "number_samples"
          $numInstances = $this->Data_quality->getFeature( $task['source_data'], 'NumberOfInstances' );
          $estimation_procedure = $this->Estimation_procedure->getById( $task['estimation_procedure'] );
          $numSamples = $this->Estimation_procedure->number_of_samples(
            $this->Estimation_procedure->trainingset_size( $numInstances, $estimation_procedure->folds ) 
          );
          $to_insert[] = array( 'task_id' => $task_id, 'input' => 'number_samples', 'value' => $numSamples );
        }
        $result[] = $task_id;
      }
    }
    $this->Task_inputs->insert_batch( $to_insert );
    // add to elastic search index. 
    foreach( $result as $r ) {
      $this->elasticsearch->index('task', $r ); 
    }
    
    return $result;
  }

  function tasks_crosstabulated( $ttid, $include_task_id = false, $where_additional = array(), $order_by_values = false, $single_task_id = false ) {
    $inputs = $this->Task_type_inout->getWhere( '`io` = "input" AND `ttid` = "' . $ttid . '"' );
    $select = array();
    $left_join = array();
    $from = array();
    $where = array( '`t`.`ttid` = ' . $ttid );
    $order_by = array();
    if( $order_by_values == false ) { $order_by[] = '`t`.`task_id` ASC';}
    if( $single_task_id ) { $where[] = '`t`.`task_id` = "'.$single_task_id.'"'; }

    if( $include_task_id ) {
      $select[] = '`t`.`task_id`';
      $select[] = '`t`.`ttid`';
    }
    foreach( $inputs as $in ) {
      $select[] = '`' . $in->name . '`.`value` AS `' . $in->name . '`';
      
      // use a left join for the "optional" fields, since these might be missing. 
      if( $in->requirement == 'required' ) {
        $from[] = '`task_inputs` AS `' . $in->name . '`';
        $where[] = '`' . $in->name . '`.`task_id` = `t`.`task_id` AND `' . $in->name . '`.`input` = "' . $in->name . '"';
        // this might speed up the query
        if( $single_task_id ) { $where[] = '`'.$in->name.'`.`task_id` = "'.$single_task_id.'"'; }
      } else { // ( $in->requirement == optional or hidden  ) 
        $left_join[] = ' LEFT JOIN `task_inputs` AS `' . $in->name . '` ON `' . $in->name . '`.`task_id` = `t`.`task_id` AND `' . $in->name . '`.`input` = "' . $in->name . '"';
      } 
      
      // and order it by values
      if( $order_by_values ) { $order_by[] = '`' . $in->name . '`.`value` ASC';}
    }
    foreach( $where_additional as $key => $value ) {
      // we don't need to connect key.input to "key", since this is already done. 
      if( is_array( $value ) ) {
        // multiple possibilities, use WHERE ... IN
        $where[] = '`' . $key . '`.`value` IN ("' . implode( '", "', $value ) . '")';
      } else {
        // only one possibility, use WHERE ... IS
        $where[] = '`' . $key . '`.`value` = "'.$value.'"';
      }
    }
    $sql = 'SELECT ' . implode( ', ', $select ) . ' FROM `task` `t` ' . implode( ' ', $left_join ) . ', ' . implode( ', ', $from ) . ' WHERE ' . implode( ' AND ', $where ) . ' ORDER BY ' . implode( ', ', $order_by );
    $result = $this->query( $sql );
    
    // remove "NULL" values
    if( $result ) {
      for( $i = 0; $i < count($result); ++$i ) {
        foreach( $result[$i] as $key => $value ) {
          if( $value == NULL ) unset( $result[$i]->{$key} );
        }
      }
    }
    return $result;
  }
  
  function getTasksWithValue( $assoc ) {
    $select = array();
    $from = array();
    $where = array();
    
    foreach( $assoc as $key => $value ) {
      $select[] = '`' . $key . '`.`value` AS `' . $key . '`';
      $from[] = '`task_inputs` AS `' . $key . '`';
      $where[] = '`' . $key . '`.`task_id` = `t`.`task_id` AND `' . $key . '`.`input` = "' . $key . '" AND `' . $key . '`.`value` = "' . $value . '"';
    }
    $sql = 'SELECT `t`.`task_id`, ' . implode( ', ', $select ) . ' FROM `task` `t`, ' . implode( ', ', $from ) . ' WHERE ' . implode( ' AND ', $where );
    return $this->query( $sql );
  }
  
}
?>
