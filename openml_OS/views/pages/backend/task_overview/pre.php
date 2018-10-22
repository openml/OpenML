<?php
$this->task_types = $this->Task_type->get( );
$this->task_ids = array();
$this->missingheader = array( 'task_id' => 'Task id', 'inputs' => 'inputs' );
$this->directories = directory_map(APPPATH.'views/pages/backend', 1);

$runs_task = $this->Run->getAssociativeArray( 'task_id', 'COUNT(*)', 'rid IS NOT NULL', 'task_id' );
if( $runs_task == false ) $runs_task = array();

foreach( $this->task_types as $key => $value ) {
  $this->task_types[$key]->inputs = array_merge(
    array( 'task_id' => 'task_id'),
    $this->Task_type_inout->getAssociativeArray( 'name', 'name', '`io` = "input" AND `requirement` <> "hidden" AND `ttid` = "' . $value->ttid . '"', 'order ASC' )
  );
  $this->task_types[$key]->tasks = $this->Task->tasks_crosstabulated( $value->ttid, true, array(), true );
  $this->task_types[$key]->duplicate_groups = array();
  if( $this->task_types[$key]->tasks ) {
    $previous = null;
    $previous_task_id = -1;
    $grouped = array();
    foreach( $this->task_types[$key]->tasks as $t ) {
      $this->task_ids[] = $t->task_id;
      unset( $t->task_id ); // for comparing purposes. access it with end($this->task_ids);

      if( $t == $previous ) {
        $grouped[] = $previous_task_id;
      } elseif( $grouped ) {
        $grouped[] = $previous_task_id;
        $this->task_types[$key]->duplicate_groups[] = $grouped;
        $grouped = array();
      }

      $previous = $t;
      $previous_task_id = end( $this->task_ids );
    }
  }

  $illegal_sql = 'SELECT `t`.`task_id`, `g`.`inputs` FROM `task_inputs` `i`, `task` `t` LEFT JOIN (SELECT `task_id`, GROUP_CONCAT(`input`) AS `inputs` FROM `task_inputs` GROUP BY `task_id`) AS `g` ON `t`.`task_id` = `g`.`task_id` WHERE `t`.`task_id` = `i`.`task_id` AND `i`.`input` NOT IN (SELECT `name` FROM `task_type_inout` `io` WHERE `io`.`ttid` = "' . $value->ttid . '") AND `t`.`ttid` = "' . $value->ttid . '"';

  $missing_sql = 'SELECT `t`.`task_id`, `g`.`inputs` FROM `task` `t` LEFT JOIN (SELECT `task_id`, GROUP_CONCAT(`input`) AS `inputs` FROM `task_inputs` GROUP BY `task_id`) AS `g` ON `t`.`task_id` = `g`.`task_id` WHERE `t`.`ttid` = "' . $value->ttid . '" AND `t`.`task_id` NOT IN (' . implode( ', ', $this->task_ids ) . ')'; // TODO

  $this->task_types[$key]->illegal = $this->Task->query( $illegal_sql );
  $this->task_types[$key]->missing = $this->Task->query( $missing_sql );

  $this->task_types[$key]->duplicates = array();
  for( $i = 0; $i < count($this->task_types[$key]->duplicate_groups); ++$i ) {
    $this->task_types[$key]->duplicates[$i] = array();
    for( $j = 0; $j < count($this->task_types[$key]->duplicate_groups[$i]); ++$j ) {
      $result = $this->Task->tasks_crosstabulated( $value->ttid, true, array(), false, $this->task_types[$key]->duplicate_groups[$i][$j] );
      $this->task_types[$key]->duplicates[$i][$j] = $result[0];

      $this->task_types[$key]->duplicates[$i][$j]->nr_of_runs = array_key_exists( $this->task_types[$key]->duplicates[$i][$j]->task_id, $runs_task ) ? $runs_task[$this->task_types[$key]->duplicates[$i][$j]->task_id] : 0;
    }
  }
}

?>
