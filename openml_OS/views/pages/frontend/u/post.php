<?php
if( $this->user->external_source != false ) {
	sm('Profile editing forbidden for social media users. ');
	redirect('frontend/page/home');
}

if(!empty($_POST['key-reset'])){
  $this->Author->query('UPDATE users SET session_hash = md5(rand()), session_hash_date = now() where id = '.$this->ion_auth->user()->row()->id);
	header('Location: '.$_SERVER['REQUEST_URI']);
	die();
}

if(!empty($_POST['key-upgrade'])){ // upgrade and change key
  $user_id = $this->ion_auth->user()->row()->id;
  $this->ion_auth->remove_from_group(NULL, $user_id);
  $this->Author->query('UPDATE users SET session_hash = md5(rand()), session_hash_date = now() where id = '.$this->ion_auth->user()->row()->id);
  $this->ion_auth->add_to_group(2, $user_id); // std members
  header('Location: '.$_SERVER['REQUEST_URI']);
	die();
}

if(!empty($_POST['key-degrade'])){
  $user_id = $this->ion_auth->user()->row()->id;
  $this->ion_auth->remove_from_group(NULL, $user_id);
  $this->ion_auth->add_to_group(3, $user_id); // readonly
  header('Location: '.$_SERVER['REQUEST_URI']);
	die();
}

$this->form_validation->set_rules('last_name', 'Last Name', 'required');
$this->form_validation->set_rules('password_confirm', 'Password Confirmation', 'min_length[' . $this->config->item('min_password_length', 'ion_auth') . ']|max_length[' . $this->config->item('max_password_length', 'ion_auth') . ']');

if ($this->form_validation->run() == true) {


	$user_data = clean_array($_POST, array( 'first_name', 'last_name', 'company', 'country', 'bio', 'gamification_visibility', 'image'));
  $user_id = $this->ion_auth->user()->row()->id;
  if (check_uploaded_file($_FILES['image'])) {
    $this->Users->update_image($user_id, $_FILES['image']['tmp_name']);
  }

	if($this->input->post('password') != false) {
		$identity = $this->session->userdata($this->config->item('identity', 'ion_auth'));

		$change = $this->ion_auth->change_password($identity, $this->input->post('password_old'), $this->input->post('password'));

		if ($change == false) {
			$this->session->set_flashdata('message', $this->ion_auth->errors());
			redirect('frontend/page/profile');
		}
	}


	$update = $this->ion_auth->update($this->ion_auth->user()->row()->id,$user_data);

	if($update) {
		//$this->session->set_flashdata('message', $this->ion_auth->messages());
		$this->elasticsearch->index('user', $this->ion_auth->user()->row()->id);
		redirect('u/'.$this->ion_auth->user()->row()->id);
	} else {
		$this->session->set_flashdata('message', $this->ion_auth->errors());
		redirect('frontend/page/profile');
	}

} else {

	$this->session->set_flashdata('message', validation_errors() );
	redirect('frontend/page/profile');

}
?>
