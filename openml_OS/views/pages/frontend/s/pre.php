<?php

$this->load_javascript = array('js/libs/mousetrap.min.js');
$this->load_css = array('css/gollum.css');

if(false !== strpos($_SERVER['REQUEST_URI'],'/s/')) {
	$this->info = explode('/', $_SERVER['REQUEST_URI']);
	$this->activepage = '';
	$this->baseurl = $_SERVER['REQUEST_URI'];
	if(array_search('s',$this->info)+2 < count($this->info))
		$this->activepage = $this->info[array_search('s',$this->info)+2];
	$this->id = $this->subpage;
	$this->activity_subpages = array('flows','tasks','data','runs');

	//get data from ES
	$this->p = array();
	$this->p['index'] = 'study';
	$this->p['type'] = 'study';
	$this->p['id'] = $this->id;
	try{
		$this->study = $this->searchclient->get($this->p)['_source'];
	} catch (Exception $e) {}

	// Making sure we know who is editing
  $this->editor = 'Anonymous';
  $this->is_owner = false;
  $this->editing = false;
  if(false !== strpos($_SERVER['REQUEST_URI'],'/edit')){
    if (!$this->ion_auth->logged_in()) {
    header('Location: ' . BASE_URL . 'login');
    exit();
    }
    else{
    $user = $this->Author->getById($this->ion_auth->user()->row()->id);
    $this->editor = $user->first_name . ' ' . $user->last_name;
    $this->editing = true;
    }
  }

	if ($this->ion_auth->logged_in() and $this->study['uploader_id'] == $this->ion_auth->user()->row()->id){
		$this->is_owner = true;
	}

	//wiki
	$this->wikipage = 'study-'.$this->id;
	$this->url = $this->wikipage;
	$this->show_history = true;

	$this->preamble = '';
	if(end($this->info) == 'edit')
		$this->url = 'edit/'.$this->wikipage;
	elseif(end($this->info) == 'history')
		$this->url = 'history/'.$this->wikipage;
	elseif(in_array('compare',$this->info)){
		$p = $this->input->post('versions');
		$this->url = 'compare/'.$this->wikipage.'/'.$p[0].'...'.$p[1];
	}
	elseif(in_array('view',$this->info)){
		$this->url = $this->wikipage.'/'.end($this->info);
		$this->preamble = '<span class="label label-danger" style="font-weight:200">You are viewing version: '.end($info).'</span><br><br>';}
	elseif(end($this->info) == 'preview')
		$this->url = 'preview';
	else
		$this->show_history = false;

}


?>
