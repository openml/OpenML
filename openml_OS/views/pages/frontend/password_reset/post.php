<?php
$min_password_length = $this->config->item('min_password_length', 'ion_auth');
$max_password_length = $this->config->item('max_password_length', 'ion_auth');

$this->form_validation->set_rules('new', $this->lang->line('reset_password_validation_new_password_label'), 'required|min_length[' . $min_password_length . ']|max_length[' . $max_password_length . ']|matches[new_confirm]');
$this->form_validation->set_rules('new_confirm', $this->lang->line('reset_password_validation_new_password_confirm_label'), 'required');

if ($this->form_validation->run() == false) {
	$this->session->set_flashdata('message', validation_errors() );
	redirect('password_reset/?code=' . $this->code);
}

//if (valid_csrf_nonce() === FALSE || $this->user->id != $this->input->post('user_id')) {
  //something fishy might be up
//  $this->ion_auth->clear_forgotten_password_code($this->code);
//  $this->session->set_flashdata('message', $this->lang->line('error_csrf') );
//}
else {
	// finally change the password
	$identity = $this->user->{$this->config->item('identity', 'ion_auth')};
  
  $change = $this->ion_auth->reset_password($identity, $this->input->post('new'));

	if ($change) {
		//if the password was successfully changed
		$this->session->set_flashdata('message', $this->ion_auth->messages());
		$this->ion_auth->logout();
    redirect('login');
	} else {
		$this->session->set_flashdata('message', $this->ion_auth->errors());
		redirect('password_reset/?code=' . $this->code);
	}
}

?>
