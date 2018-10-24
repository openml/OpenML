<?php
$this->load_javascript = array('https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.2/MathJax.js?config=TeX-MML-AM_CHTML');

if(false === strpos($_SERVER['REQUEST_URI'],'/a/')) {
  header('Location: search?type=measure');
  die();
}

$this->initialMsgClass = '';
$this->initialMsg = '';

if (!$this->ion_auth->logged_in()) {
	$this->initialMsgClass = 'alert alert-warning';
	$this->initialMsg = 'Before submitting content, please login first!';
}

function cleanName($string){
	return $safe = preg_replace('/^-+|-+$/', '', strtolower(preg_replace('/[^a-zA-Z0-9]+/', '-', $string)));
}

if(false !== strpos($_SERVER['REQUEST_URI'],'/a/evaluation-measures') or
	 false !== strpos($_SERVER['REQUEST_URI'],'/a/estimation-procedures') or
	 false !== strpos($_SERVER['REQUEST_URI'],'/a/data-qualities') or
	 false !== strpos($_SERVER['REQUEST_URI'],'/a/flow-qualities')) {
  $var = explode('/', $_SERVER['REQUEST_URI']);
	$this->id = end($var);

	// Get data from ES
	$this->p = array();
	$this->p['index'] = 'measure';
	$this->p['type'] = 'measure';
	$this->p['id'] = $this->id;
	try{
		$this->measure = $this->searchclient->get($this->p)['_source'];
	} catch (Exception $e) {}

	if(false !== strpos($_SERVER['REQUEST_URI'],'/a/data-qualities')){
        	$this->p['index'] = 'data';
		$this->p['type'] = 'data';
		unset($this->p['id']);
		$this->p['size'] = '1000';
		$this->p['_source'] = array("name", "version", "qualities");
		//$this->p['sort'] = $this->id;

		try{
			$this->results = $this->searchclient->search($this->p)['hits']['hits'];
		} catch (Exception $e) {print($e);}
	}

	if(false !== strpos($_SERVER['REQUEST_URI'],'/a/flow-qualities')){
                $this->p['index'] = 'flow';
		$this->p['type'] = 'flow';
		unset($this->p['id']);
		$this->p['size'] = '100';
		$this->p['_source'] = array("name", "version", $this->id);
		//$this->p['sort'] = $this->id;

		try{
			$this->results = $this->searchclient->search($this->p)['hits']['hits'];
		} catch (Exception $e) {}
	}
}

?>
