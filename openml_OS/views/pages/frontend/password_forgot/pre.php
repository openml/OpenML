<?php

$this->active = 'profile';
$this->message = (validation_errors()) ? validation_errors() : $this->session->flashdata('message');

$this->emailField = array(
	'name' => 'email',
	'id' => 'email',
	'type' => 'text',
	'value' => $this->input->post('email'),
);
?>
