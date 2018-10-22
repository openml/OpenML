<?php
/* TASK SEARCH */
$ttid = $this->input->post('task_type');
$this->att = 'results';

$inputs = $this->Task_type_inout->getColumnWhere('name','`io` = "input" AND `ttid` = "' . $ttid . '"' );

$search_constraints = array();
foreach( $inputs as $input ) {
  if( $this->input->post( $input ) != false ) {
    $search_constraints[$input] = $this->input->post( $input );
  }
}

$this->found_tasks = $this->Task->search( $ttid, $search_constraints );
if($this->found_tasks == false) {
  $this->task_message = 'None of the tasks met the search criteria. Please try again. ';
}

?>
