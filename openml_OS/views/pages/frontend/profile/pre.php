<?php
$this->active = 'profile';
$this->message = (validation_errors()) ? validation_errors() : $this->session->flashdata('message');
if (!$this->ion_auth->logged_in())
{
	sm('Please login first. ');
	redirect('frontend/page/login');
}
$this->user = $this->ion_auth->user()->row();
$this->load->library('elasticSearch');

if( $this->user->external_source != false ) {
	sm('Profile editing forbidden for social media users. ');
	redirect('frontend/page/home');
}


$this->emailField = array(
	'placeholder' => 'Change Email',
	'data-hint' => 'You will occasionally receive account related emails. We promise not to share your email with anyone.',
	'name' => 'email',
	'id' => 'email',
	'type' => 'email',
	'value' => $this->user->email,
);

$this->password_new = array(
	'placeholder' => 'New Password',
	'data-hint' => 'Your password needs at least 8 characters',
	'name' => 'password',
	'id' => 'password',
	'type' => 'password',
);

$this->password_confirm = array(
	'placeholder' => 'Confirm Password',
	'data-hint' => 'Enter your password again to avoid mistakes',
	'name' => 'password_confirm',
	'id' => 'password_confirm',
	'type' => 'password',
);

$this->password_old = array(
	'placeholder' => 'Current Password (if you want to change it)',
	'data-hint' => 'If you wish to change your password, please enter it first',
	'name' => 'password_old',
	'id' => 'password_old',
	'type' => 'password',
);


$this->first_name = array(
	'placeholder' => 'First name',
	'data-hint' => 'Using your real name helps you connect to your social network (and your publications).',
	'name' => 'first_name',
	'id' => 'first_name',
	'type' => 'text',
	'value' => $this->user->first_name,
);

$this->last_name = array(
	'placeholder' => 'Last name',
	'name' => 'last_name',
	'id' => 'last_name',
	'type' => 'text',
	'value' => $this->user->last_name,
);

$this->country = array(
	'placeholder' => 'Country',
	'name' => 'country',
	'id' => 'country',
	'type' => 'text',
	'value' => $this->user->country,
);

$this->bio = array(
  'placeholder' => 'Bio',
	'data-hint' => 'A short bio or catchphrase to let others know a little bit about you.',
	'name' => 'bio',
	'id' => 'bio',
	'type' => 'text',
	'value' => $this->user->bio,
);

$this->affiliation = array(
	'placeholder' => 'Affiliation',
	'data-hint' => 'The organization where you work. This may help you connect to interesting people.',
	'name' => 'affiliation',
	'id' => 'affiliation',
	'type' => 'text',
	'value' => $this->user->affiliation,
);

$this->image = array(
	'placeholder' => 'Upload picture...',
	'data-hint' => 'Upload a nice image or avatar.',
	'name' => 'image',
	'id' => 'image',
	'type' => 'file',
);
?>
