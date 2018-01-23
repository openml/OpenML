<?php

// Description edit
if($this->input->post('page')){
  // prepare to send data to gollum
  // $session_hash = $this->Api_session->createByUserId( $this->ion_auth->user()->row()->id );

  $message = ($this->input->post('message') != 'Write a small message explaining the change.' ? $this->input->post('message') : '[no message]');

  $post_data = array(
      'page' => $this->input->post('page'),
      'path' => $this->input->post('path'),
      'content' => $this->input->post('content'),
      'message' => $this->editor.': '.$message );

if($this->input->post('versions')){
  foreach($this->input->post('versions') as $k => $v){
	$post_data['versions['.$k.']'] = $v;
  }
}

// check whether we are sending new data or requesting comparison
  if($this->input->post('content'))
	$url = WIKI_URL . '/edit/'.$this->wikipage;
  if($this->input->post('versions'))
	$url = WIKI_URL . '/compare/'.$this->wikipage;

//call gollum
  $api_response = $this->curlhandler->post_multipart_helper( $url, $post_data );

//sync DB, search index
  if($this->input->post('content')){
	//update database - TO DO: check if there is a better way
  	$this->Dataset->query('update study set description = "'.addslashes($this->input->post('content')).'" where id='.$this->id);

	//update index
  	$this->elasticsearch->index('study', $this->id);
  }

//save successful, redirect to detail page
  if($this->input->post('content'))
  	header('Location: '.BASE_URL.'s/'.$this->id);
}
if(isset($_POST["index_type"])){
  try{
    $this->elasticsearch->index($_POST["index_type"],$_POST["index_id"]);
    redirect('s/'.$this->id);
  } catch (Exception $e) {
    echo 'Caught exception: ',  $e->getMessage(), "\n";
  }
}
?>
