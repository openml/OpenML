<?php

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
	$this->p['index'] = 'openml';
	$this->p['type'] = 'measure';
	$this->p['id'] = $this->id;
	try{
		$this->measure = $this->searchclient->get($this->p)['_source'];
	} catch (Exception $e) {}

	if(false !== strpos($_SERVER['REQUEST_URI'],'/a/data-qualities')){
		$this->p['type'] = 'data';
		unset($this->p['id']);
		$this->p['size'] = '100';
		$this->p['_source'] = array("name", "version", "qualities");
		//$this->p['sort'] = $this->id;

		try{
			$this->results = $this->searchclient->search($this->p)['hits']['hits'];
		} catch (Exception $e) {print($e);}
	}

	if(false !== strpos($_SERVER['REQUEST_URI'],'/a/flow-qualities')){
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

// No idea what this is for, delete?
if(false !== strpos($_SERVER['REQUEST_URI'],'/a/quality-value')) {
	$parts = explode('/', $_SERVER['REQUEST_URI']);
	$did = $parts[count($parts)-1];
  $quality = $parts[count($parts)-2];

  $this->quality = $this->Data_quality->getById( array( $did, $quality, null ) );
  $this->data = $this->Dataset->getById( $this->quality->data );
  $sql = 'SELECT `d`.`did`, `d`.`name`, `d`.`version`, `q`.`quality`, `q`.`value` FROM `dataset` `d`, `data_quality` `q` ' .
         'WHERE `d`.`did` = `q`.`data` AND `q`.`quality` = "'.$this->quality->quality.'" ' .
         'ORDER BY abs(CAST(`q`.`value` AS DECIMAL(20,5)) - ' . $this->quality->value . ') LIMIT 0,5 ;';

  $this->similar = $this->Data_quality->query( $sql );
  usort($this->similar, function($a, $b) {
    return strcmp($a->value, $b->value);
  });
}

?>
