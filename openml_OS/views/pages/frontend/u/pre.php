<?php

$this->load_javascript = array('js/libs/highcharts-2016.js','js/libs/highcharts-heatmap.js','js/libs/jquery.dataTables.min.js');

if(false !== strpos($_SERVER['REQUEST_URI'],'/u/')) {
	$info = explode('/', $_SERVER['REQUEST_URI']);
	if(array_search('u',$info)+2 < count($info))
		$this->subpage = $info[array_search('u',$info)+2];
	$this->user_id = $info[array_search('u',$info)+1];
	$this->baseurl = $_SERVER['REQUEST_URI'];
	$this->author = $this->Author->getById($this->user_id);
	$this->activity_subpages = array('flows','data','runs','tasks');

	//get data from ES
	$this->p = array();
	$this->p['index'] = 'user';
	$this->p['type'] = 'user';
	$this->p['id'] = $this->user_id;
	try{
		$this->userinfo = $this->searchclient->get($this->p)['_source'];
	} catch (Exception $e) {}

	$this->is_owner = false;
	if($this->ion_auth->logged_in() and ($this->ion_auth->user()->row()->id == $this->user_id || $this->ion_auth->is_admin()))
	   $this->is_owner = true;


// PROFILE EDIT FORM
$this->message = (validation_errors()) ? validation_errors() : $this->session->flashdata('message');
if ($this->ion_auth->logged_in())
{
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
	'class' => 'form-control floating-label'
);

$this->password_new = array(
	'placeholder' => 'New Password',
	'data-hint' => 'Your password needs at least 8 characters',
	'name' => 'password',
	'id' => 'password',
	'type' => 'password',
	'class' => 'form-control floating-label'
);

$this->password_confirm = array(
	'placeholder' => 'Confirm Password',
	'data-hint' => 'Enter your password again to avoid mistakes',
	'name' => 'password_confirm',
	'id' => 'password_confirm',
	'type' => 'password',
	'class' => 'form-control floating-label'
);

$this->password_old = array(
	'placeholder' => 'Current Password (if you want to change it)',
	'data-hint' => 'If you wish to change your password, please enter it first',
	'name' => 'password_old',
	'id' => 'password_old',
	'type' => 'password',
	'class' => 'form-control floating-label'
);


$this->first_name = array(
	'placeholder' => 'First name',
	'data-hint' => 'Using your real name helps you connect to your social network (and your publications).',
	'name' => 'first_name',
	'id' => 'first_name',
	'type' => 'text',
	'value' => $this->user->first_name,
	'class' => 'form-control floating-label'
);

$this->last_name = array(
	'placeholder' => 'Last name',
	'name' => 'last_name',
	'id' => 'last_name',
	'type' => 'text',
	'value' => $this->user->last_name,
	'class' => 'form-control floating-label'
);

$this->country = array(
	'placeholder' => 'Country',
	'name' => 'country',
	'id' => 'country',
	'type' => 'text',
	'value' => $this->user->country,
	'class' => 'form-control floating-label'
);

$this->bio = array(
  'placeholder' => 'Bio',
	'data-hint' => 'A short bio or catchphrase to let others know a little bit about you.',
	'name' => 'bio',
	'id' => 'bio',
	'type' => 'text',
	'value' => $this->user->bio,
	'class' => 'form-control floating-label'
);

$this->affiliation = array(
	'placeholder' => 'Affiliation',
	'data-hint' => 'The organization where you work. This may help you connect to interesting people.',
	'name' => 'affiliation',
	'id' => 'affiliation',
	'type' => 'text',
	'value' => $this->user->affiliation,
	'class' => 'form-control floating-label'
);

$this->image = array(
	'placeholder' => 'Upload picture...',
	'data-hint' => 'Upload a nice image or avatar.',
	'name' => 'image',
	'id' => 'image',
	'type' => 'file',
	'class' => 'form-control floating-label'
);

$this->optin_gamification = array(
    'data-hint'=> 'Whether you want to see altmetric/gamification statistics in OpenML, or not.',
    'name' => 'gamification_visibility',
    'id' => 'gamification_setting_in',
    'type' => 'radio',
    'value' => 'show',
    'checked' => $this->user->gamification_visibility=='show'?TRUE:FALSE
);

$this->optout_gamification = array(
    'data-hint'=> 'Whether you want to see altmetric/gamification statistics in OpenML, or not.',
    'name' => 'gamification_visibility',
    'id' => 'gamification_setting_out',
    'type' => 'radio',
    'value' => 'hidden',
    'checked' => $this->user->gamification_visibility=='show'?FALSE:TRUE
);

}
} elseif($this->ion_auth->logged_in()){
		header('Location: '.str_replace('/u','/u/'.$this->ion_auth->user()->row()->id, $_SERVER['REQUEST_URI']));
		die();
} else {
	  header('Location: ' . BASE_URL . 'login');
}

?>
