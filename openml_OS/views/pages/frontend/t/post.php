<?php

//Add and remove tags
if(isset($_POST["newtags"]) and !empty($_POST["newtags"])){
  $post_data = array('api_key' => $this->ion_auth->user()->row()->session_hash,
		     'task_id' => $this->id,
                     'tag' => $_POST["newtags"]);
  $url = BASE_URL.'api/v1/task/tag';
  $api_response = $this->curlhandler->post_helper($url,$post_data);
  redirect('t/'.$this->id);
}
elseif(isset($_POST["deletetag"]) and !empty($_POST["deletetag"])){
  $post_data = array('api_key' => $this->ion_auth->user()->row()->session_hash,
		     'task_id' => $this->id,
                     'tag' => $_POST["deletetag"]);
  $url = BASE_URL.'api/v1/task/untag';
  $api_response = $this->curlhandler->post_helper($url,$post_data);
  redirect('t/'.$this->id);
}

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
