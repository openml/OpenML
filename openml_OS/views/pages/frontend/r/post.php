<?php

//Add and remove tags
if(isset($_POST["newtags"]) and !empty($_POST["newtags"])){
  $post_data = array('api_key' => $this->ion_auth->user()->row()->session_hash,
		     'run_id' => $this->id,
                     'tag' => $_POST["newtags"]);
  $url = BASE_URL.'api/v1/run/tag';
  $api_response = $this->curlhandler->post_helper($url,$post_data);
  redirect('r/'.$this->id);
}
elseif(isset($_POST["deletetag"]) and !empty($_POST["deletetag"])){
  $post_data = array('api_key' => $this->ion_auth->user()->row()->session_hash,
		     'run_id' => $this->id,
                     'tag' => $_POST["deletetag"]);
  $url = BASE_URL.'api/v1/run/untag';
  $api_response = $this->curlhandler->post_helper($url,$post_data);
  redirect('r/'.$this->id);
}

?>
