<?php
if (!$this->ion_auth->logged_in()) {
	redirect('login');
}

$this->form_validation->set_rules('body', 		'Body', 	'required');

if ($this->form_validation->run() == true) {
	$thread = array(
		'title' => gp('title'),
		'activated' => 'y',
		'body' => gp('body'),
		'post_date' => now(),
		'author_id' => $this->ion_auth->user()->row()->id,
		'category_id' => 1
	);

	$id = $this->Thread->insert( $thread );
	if($id){
		redirect('discuss/tid/'.$id);
	} else {
		sm('Failed to create thread. Please try again. ');
		redirect('discuss');
	}
} else {
	sm( validation_errors() );
	redirect('discuss');
}
?>
