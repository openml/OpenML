<?php
  $rdfize_path = LIB_PATH . 'openml-rdf';
	$getParams = array();
	$getParams['index'] = 'openml';
	$getParams['type']  = 'none';
	$getParams['id']    = '0';

	$info = explode('/', $_SERVER['REQUEST_URI']);
	if(false !== strpos($_SERVER['REQUEST_URI'],'/r/')){
		$getParams['type']  = 'Run';
		$getParams['id']    = $info[array_search('r',$info)+1];
	} elseif(false !== strpos($_SERVER['REQUEST_URI'],'/d/')){
		$getParams['type']  = 'Dataset';
		$getParams['id']    = $info[array_search('d',$info)+1];
	} elseif(false !== strpos($_SERVER['REQUEST_URI'],'/f/')){
		$getParams['type']  = 'Flow';
		$getParams['id']    = $info[array_search('f',$info)+1];
	} elseif(false !== strpos($_SERVER['REQUEST_URI'],'/t/')){
		$getParams['type']  = 'Task';
		$getParams['id']    = $info[array_search('t',$info)+1];
	}
  $command = 'cd ' . $rdfize_path . '&& ./rdfize.sh ' . $getParams['type'] . ' ' . $getParams['id'];
  echo shell_exec($command);
?>
