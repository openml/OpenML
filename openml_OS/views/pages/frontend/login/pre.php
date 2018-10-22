<?php
// $this->active = 'profile';
$this->message = (validation_errors()) ? validation_errors() : $this->session->flashdata('message');

if( $this->session->flashdata('login_redirect') == false ) {
  if( array_key_exists( 'HTTP_REFERER', $_SERVER ) ) {
    if( startsWith( $_SERVER['HTTP_REFERER'], BASE_URL ) ) {
      $this->session->set_flashdata('login_redirect', $_SERVER['HTTP_REFERER'] );
    } else {
      $this->session->set_flashdata('login_redirect', false );
    }
  }
} else {
  $this->session->set_flashdata('login_redirect', $this->session->flashdata('login_redirect') );
}


$this->identity = array('name' => 'identity',
	'placeholder' => 'Email',
	'id' => 'identity',
	'type' => 'text',
	'value' => $this->form_validation->set_value('identity'),
);
$this->password = array('name' => 'password',
	'placeholder' => 'Password',
	'id' => 'password',
	'type' => 'password',
);
?>
