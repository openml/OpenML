<?php

function shortenString($string){
  if (strlen($string) >= 60) {
    return substr($string, 0, 30). " ... " . substr($string, -30);
}
else {
    return $string;
}
}

//$this->load_javascript = array('js/libs/highcharts.js','js/libs/highcharts-more.js','js/libs/modules/exporting.js','js/libs/jquery.dataTables.min.js','js/libs/dataTables.tableTools.min.js','js/libs/dataTables.scroller.min.js','js/libs/dataTables.responsive.min.js','js/libs/dataTables.colVis.min.js');
$myjs = array('js/libs/highcharts.js','js/libs/jquery.dataTables.min.js','js/libs/sweetalert.min.js');
if(!isset($this->load_javascript))
  $this->load_javascript = $myjs;
else{
  foreach($myjs as $js){
    if(!in_array($js,$this->load_javascript)){
      $this->load_javascript[]=$js;
    }
  }
}

$mycss = array('css/jquery.dataTables.min.css','css/dataTables.colvis.min.css','css/dataTables.colvis.jqueryui.css','css/dataTables.responsive.min.css','css/dataTables.scroller.min.css','css/dataTables.tableTools.min.css');
if(!isset($this->load_css))
  $this->load_css = $mycss;
else{
  foreach($mycss as $css){
    if(!in_array($css,$this->load_css)){
      $this->load_css[]=$css;
    }
  }
}

// get user
if ($this->ion_auth->logged_in()) {
  $this->user_id = $this->ion_auth->user()->row()->id;
}

// helper functions
function microtime_float()
{
    list($usec, $sec) = explode(" ", microtime());
    return ((float)$usec + (float)$sec);
}

function addToGET($keyvalue){
  $attr = $_GET;
  foreach($keyvalue as $key => $value){
    if(array_key_exists($key,$attr) && ($value || $value == 0)) {
      unset($attr[$key]);
    }
    if($value) {
      $attr[$key]=$value;
    }
  }
  return http_build_query($attr);
}

/// SEARCH
$this->ref_url = 'search';
if(!isset($this->filtertype) and $this->input->get('type'))
	$this->filtertype = safe($this->input->get('type'));

if(isset ($this->specialterms))
	$this->terms = $this->specialterms;
else{
  $this->specialterms = "";
	$this->terms = htmlspecialchars(safe($this->input->get('q')), ENT_QUOTES);
  $this->terms = str_replace('lt;','<',$this->terms);
  $this->terms = str_replace('gt;','>',$this->terms);
  $this->terms = str_replace('&','',$this->terms);
  $this->terms = explode('/',$this->terms)[0];
}

$this->coreterms = "";
$this->filters = array();
$this->dataonly = 0;
$this->results = 0;

$pieces = str_getcsv($this->terms, ' ');

if(false !== strpos($_SERVER['REQUEST_URI'],'/t/type')) {
	$tasktypeid = end(explode('/', $_SERVER['REQUEST_URI']));
	$tasktypeid = explode('?',$tasktypeid)[0];
	$pieces[] = "tasktype.tt_id:".$tasktypeid;
}

foreach($pieces as $t){
	if(strpos($t,':') !== false){
	  $parts = explode(":",$t);
	  $this->filters[$parts[0]] = $parts[1];
	} else {
    if($this->coreterms==""){
       $this->coreterms .= $t;
    } else {
	     $this->coreterms .= ' '.$t;
    }
	}
}

$this->filterstring=implode(" ",$this->filters);

$this->listids = safe($this->input->get('listids'));
$this->table = safe($this->input->get('table'));

$size = explode('/',$this->input->get('size'))[0];
$this->size = min((safe($size) ? safe($size) : 100),10000);

// some fields can be set beforehand. If not, set them to appropriate defaults.
if($this->input->get('from'))
	$this->from = safe($this->input->get('from'));
if(!isset($this->from))
	$this->from = 0;
if($this->input->get('dataonly'))
	$this->dataonly = safe($this->input->get('dataonly'));
if(!isset($this->filtertype))
	$this->filtertype = false;
if($this->input->get('sort'))
	$this->sort = safe($this->input->get('sort'));
if(!isset($this->sort))
	$this->sort = false;

//default sort on number or runs, otherwise date
if(!$this->terms and !$this->sort)
	if($this->filtertype == 'data' or $this->filtertype == 'task' or $this->filtertype == 'flow')
   		$this->sort =  'runs';
	else
   		$this->sort =  'date';

$this->order = safe($this->input->get('order'));
if($this->sort and !$this->order)
   $this->order = 'desc';

$this->curr_sort = "best match";
if($this->sort=='runs' or $this->sort=='nr_of_likes' or $this->sort=='nr_of_downloads')
	$this->curr_sort = "most ".str_replace('nr_of_','',$this->sort);
if($this->sort=='reach' or $this->sort=='impact' or $this->sort=='activity')
	$this->curr_sort = "highest ".$this->sort;
if($this->sort=='date')
	$this->curr_sort = "most recent";
if($this->sort=='last_update')
	$this->curr_sort = "last update";
if($this->order=='asc' and ($this->sort=='runs' or $this->sort=='nr_of_likes' or $this->sort=='nr_of_downloads'))
	$this->curr_sort = "fewest ".str_replace('nr_of_','',$this->sort);
if($this->order=='asc' and ($this->sort=='reach' or $this->sort=='impact' or $this->sort=='activity'))
	$this->curr_sort = "lowest ".$this->sort;
if($this->order=='asc' and $this->sort=='date')
	$this->curr_sort = "least recent";
if(startsWith($this->sort,'qualities.NumberOf')){
  if($this->order=='asc'){
    $this->curr_sort = "fewest ";
  } else {
    $this->curr_sort = "most ";
  }
  $this->curr_sort .= substr($this->sort,18);
}
$attrs = $_GET;
unset($attrs['from']);
$this->rel_uri = "search?".http_build_query($attrs);

$this->icons = array( 'flow' => 'fa fa-cogs fa-lg', 'data' => 'fa fa-database fa-lg', 'run' => 'fa fa-star fa-lg', 'user' => 'fa fa-user fa-lg', 'task' => 'fa fa-trophy fa-lg', 'study' => 'fa fa-flask fa-lg', 'task_type' => 'fa fa-flag fa-lg', 'measure' => 'fa fa-bar-chart-o fa-lg');
$this->colors = array( 'flow' => '#428bca', 'data' => '#3d8b3d', 'run' => '#d9534f', 'user' => '#e91e63', 'task' => '#fb8c00', 'task_type' => '#ff5722', 'study' => '#9c27b0', 'measure' => '#607d8b');


$this->measures = array( 'estimation_procedure' => 'a/estimation-procedures', 'evaluation_measure' => 'a/evaluation-measures', 'data_quality' => 'a/data-qualities', 'flow_quality' => 'a/flow-qualities');

$query = '"match_all" : { }';
if($this->listids and $this->coreterms != ''){
	$query = '"query_string" : {
	            "query" : "'.$this->coreterms.'"
	          }';
}
elseif($this->terms != 'match_all' and $this->coreterms != ''){
	$query = '"query_string" : {
	            "query" : "'.$this->coreterms.'"
	          }';
}

$this->active_tab = gu('tab');
$jsonfilters = array();
$jsonshould = array();

//visibility defaults
$jsonshould[] = '{ "term" : { "visibility" : "public" } }';
if ($this->ion_auth->logged_in()) {
  $jsonshould[] = '{ "term" : { "uploader_id" : "'.$this->ion_auth->user()->row()->id.'" } }';
}
if($this->ion_auth->is_admin()){
  $jsonshould[] = '{ "term" : { "visibility" : "private" } }';
}


if($this->filtertype == 'data' and false === strpos($this->terms,'status')){
  $jsonfilters[] = '{ "term" : { "status" : "active" } }';
}

//print_r($this->filters);

//search filters
foreach($this->filters as $k => $v){
	if(strpos($v,'>') !== false and is_numeric(str_replace('>','',$v)))
		$jsonfilters[] = '{ "range" : { "'.$k.'" : { "gt" : '.str_replace('>','',$v).' } } }';
	elseif(strpos($v,'<') !== false and is_numeric(str_replace('<','',$v)))
		$jsonfilters[] = '{ "range" : { "'.$k.'" : { "lt" : '.str_replace('<','',$v).' } } }';
	elseif(strpos($v,'..') !== false and is_numeric(str_replace('..','',$v))){
		$parts = explode("..",$v);
		if(count($parts) == 2)
			$jsonfilters[] = '{ "range" : { "'.$k.'" : { "gte" : '.$parts[0].', "lte" : '.$parts[1].' } } }';
		}
  elseif($k == 'status'){
    if($v != 'all')
      $jsonfilters[] = '{ "term" : { "'.$k.'" : "'.$v.'"} }';}
	elseif($k == 'type' or $k == 'measure_type' or $k == 'evaluation_measures')
    $jsonfilters[] = '{ "term" : { "'.$k.'" : "'.$v.'"} }';
  elseif($k == 'tags.tag')
    $jsonfilters[] = '{ "nested": { "path": "tags", "query": { "term": { "tags.tag": "'.strtolower($v).'" } } } }';
  else
		$jsonfilters[] = '{ "term" : { "'.$k.'" : "'.str_replace('_',' ',$v).'"} }';
}
$fjson = implode(",",$jsonfilters);
if(count($jsonfilters)>1)
	$fjson = '['.$fjson.']';
$sjson = '['.implode(",",$jsonshould).'], "minimum_should_match" : 1 ';

$params['index'] = '_all';
if($this->filtertype){
  $params['index'] = $this->filtertype;
  $params['type'] = $this->filtertype;
}
$params['body']  = '{'.
    ($this->table ? '"_source" : ["data_id","name","version","runs","qualities"],' : '').
   '"from" : '. ($this->from ? $this->from : 0) .',
    "size" : '. $this->size .','.
    ($this->listids ? '"stored_fields" : [],' : '').'
    "query" : { "bool" : { "must" : {'.$query.'}, '. ($fjson ? '"filter": '.($fjson ? $fjson : '').', ' : '') .'"should": '.($sjson ? $sjson : '').' }},'.
    (($this->sort and $this->sort!='match') ? '"sort" : { "'.$this->sort.'" : { "order": "'.$this->order.'"}},' : '').
    ($this->coreterms == '' ? '' :
    '"highlight" : {
        "fields" : {
            "description" : {}
        }
    },').'
    "aggs" : {
        "type" : {
          "terms" : { "field" : "_type" }
        }
    }
}';
//print_r($params);

// prepare query for result counts over all types (will be loaded using JS)
$this->alltypes = $params;
unset($this->alltypes['type']);
$this->alltypes['body'] = str_replace('"size" : '. $this->size,'"size" : 0',$this->alltypes['body']);

$time_start = microtime_float();
// launch query
try {
	$this->results = $this->searchclient->search($params);
        //print_r($this->results);
} catch (Exception $e) {
	$this->results = array();
	$this->results['hits'] = array();
	$this->results['hits']['total'] = 0;
	$this->results['facets'] = array();
	$this->results['facets']['type'] = array();
	$this->results['facets']['type']['total'] = 0;
	$this->results['facets']['type']['terms'] = array();
  $this->results['aggregations'] = array();
  $this->results['aggregations']['type'] = array();
  $this->results['aggregations']['type']['buckets'] = array();
}

$time_end = microtime_float();
$time = $time_end - $time_start;

//echo "Query took $time seconds\n";

if($this->table) {
    $this->tableview = [];
    $this->cols = array("name" => "","runs" => "","NumberOfInstances" => "","NumberOfFeatures" => "","NumberOfClasses" => "","NumberOfMissingValues" => "","NumberOfNumericFeatures" => "");
    $this->mCols = json_decode('[{ "mData": "name" , "defaultContent": "", "bVisible":true},{ "mData": "runs" , "defaultContent": "", "sType":"numeric", "bVisible":true},{ "mData": "NumberOfInstances" , "defaultContent": "", "sType":"numeric", "bVisible":true},{ "mData": "NumberOfFeatures" , "defaultContent": "", "sType":"numeric", "bVisible":true},{ "mData": "NumberOfClasses" , "defaultContent": "", "sType":"numeric", "bVisible":true},{ "mData": "NumberOfMissingValues" , "defaultContent": "", "sType":"numeric", "bVisible":true},{ "mData": "NumberOfNumericFeatures" , "defaultContent": "", "sType":"numeric", "bVisible":true}]');

    foreach( $this->results['hits']['hits'] as $r ) {
    $rs = $r['_source'];
    $newrow = array();
    $id=0;
    foreach( $rs as $k => $v ) {
        if($k == 'data_id')
           $id = $v;
        elseif($k == 'name')
           $newrow[$k] = '<a href="d/'.$id.'">'.$v.'</a>';
        elseif($k == 'version')
           $newrow['name'] = str_replace('</a>',' ('.$v.')</a>',$newrow['name']);
        elseif($k == 'qualities')
          foreach( $v as $qk => $qv ) {
           $newrow[$qk] = $qv;
           if(!array_key_exists($qk,$this->cols)){
             $this->cols[$qk] = "";
             $newcol = array("mData" => $qk, "defaultContent" => "", "bVisible" => false);
             if(is_numeric($qv))
               $newcol["sType"] = "numeric";
             $this->mCols[] = $newcol;
          }
         }
        elseif($k == 'runs')
          $newrow[$k] = $v;
    }
    $this->tableview[] = $newrow;
    }
  }
?>
