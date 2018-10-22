<?php

$this->code = $this->input->get('code');
$this->user = $this->ion_auth->forgotten_password_check($this->code);

if($this->user == false ) {
  $this->session->set_flashdata('message', $this->ion_auth->errors());
	redirect('password_forgot', 'refresh');
}

$this->active = 'profile';
$this->message = (validation_errors()) ? validation_errors() : $this->session->flashdata('message');
$min_password_length = $this->config->item('min_password_length', 'ion_auth');

$this->new_password = array(
  'name' => 'new',
  'id'   => 'new',
  'type' => 'password',
  'pattern' => '^.{'.$min_password_length.'}.*$',
);
$this->new_password_confirm = array(
  'name' => 'new_confirm',
  'id'   => 'new_confirm',
  'type' => 'password',
  'pattern' => '^.{'.$min_password_length.'}.*$',
);
$this->user_id = array(
  'name'  => 'user_id',
  'id'    => 'user_id',
  'type'  => 'hidden',
  'value' => $this->user->id,
);

$this->csrf = get_csrf_nonce();

?>
