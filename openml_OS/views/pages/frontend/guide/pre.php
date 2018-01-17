<?php
  $this->load_javascript = array('//cdnjs.cloudflare.com/ajax/libs/highlight.js/8.3/highlight.min.js');
  $this->load_css = array('css/highlight.css');

  if($this->ion_auth->logged_in()){
    $this->api_key = $this->Author->getById($this->ion_auth->user()->row()->id)->session_hash;
  }

  $this->info = explode('/', $_SERVER['REQUEST_URI']);
	$this->activepage = 'bootcamp';
	$this->baseurl = $_SERVER['REQUEST_URI'];
	if(array_search('guide',$this->info)+1 < count($this->info))
		$this->activepage = $this->info[array_search('guide',$this->info)+1];
	$this->activity_subpages = array('bootcamp','api','integrations','benchmark','developers','terms','rest','altmetrics');
?>
