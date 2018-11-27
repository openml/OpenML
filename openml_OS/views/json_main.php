      <?php
	$getParams = array();

	$info = explode('/', $_SERVER['REQUEST_URI']);
	if(false !== strpos($_SERVER['REQUEST_URI'],'/r/')){
    $getParams['index'] = 'run';
		$getParams['type']  = 'run';
		$getParams['id']    = $info[array_search('r',$info)+1];
	} elseif(false !== strpos($_SERVER['REQUEST_URI'],'/d/')){
    $getParams['index'] = 'data';
		$getParams['type']  = 'data';
		$getParams['id']    = $info[array_search('d',$info)+1];
	} elseif(false !== strpos($_SERVER['REQUEST_URI'],'/f/')){
    $getParams['index'] = 'flow';
		$getParams['type']  = 'flow';
		$getParams['id']    = $info[array_search('f',$info)+1];
	} elseif(false !== strpos($_SERVER['REQUEST_URI'],'/t/')){
    $getParams['index'] = 'task';
		$getParams['type']  = 'task';
		$getParams['id']    = $info[array_search('t',$info)+1];
	}

	echo json_encode($this->searchclient->get($getParams)['_source'], JSON_PRETTY_PRINT);
    ?>
