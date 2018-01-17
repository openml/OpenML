<?php
  $info = explode('/', $_SERVER['REQUEST_URI']);
  $run_id = $info[array_search('r',$info)+1];
  $outputname = $info[array_search('output',$info)+1];

  $getParams = array();
  $getParams['index'] = 'openml';
  $getParams['type']  = 'run';
  $getParams['id']    = $run_id;
  $searchclient = $this->searchclient->get($getParams);
  $url = $searchclient['_source']['output_files'][$outputname];
  $urlparts = explode('/', $url);
  $outputid = $urlparts[array_search('download',$urlparts)+1];
  $file = 'http://openml.org/data/download/'.$outputid;

  try{
		header('Content-Type: text/plain');
		header('Content-Disposition: inline; filename='.basename($file));
		readfile($file);
		exit;
	}
  catch (Exception $e){
	fopen($url);
  }
?>
