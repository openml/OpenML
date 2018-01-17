<?php

$this->form_validation->set_rules('email', 'Email', 'required|valid_email');

if ($this->form_validation->run() == false) {
  $this->session->set_flashdata('message', validation_errors() );
	redirect('frontend/page/password_forgot');
} 

$identity = $this->ion_auth->where('email', strtolower($this->input->post('email')))->users()->row();
if(empty($identity)) {
  $this->ion_auth->set_message('forgot_password_email_not_found');
  $this->session->set_flashdata('message', $this->ion_auth->messages());
  redirect('password_forgot');
}
            
//run the forgotten password method to email an activation code to the user
$forgotten = $this->ion_auth->forgotten_password($identity->{$this->config->item('identity', 'ion_auth')});

if ($forgotten) {
  //if there were no errors
	$this->session->set_flashdata('message', $this->ion_auth->messages());
	redirect('home'); //we should display a confirmation page here instead of the login page
} else {
  $this->session->set_flashdata('message', $this->ion_auth->errors());
	redirect('password_forgot');
}

?>
