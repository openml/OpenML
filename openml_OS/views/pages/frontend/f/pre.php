<?php

if(false === strpos($_SERVER['REQUEST_URI'],'/f/')) {
  header('Location: search?type=flow');
  die();
}

//$this->load_javascript = array('js/libs/highcharts.js','js/libs/highcharts-more.js','js/libs/modules/exporting.js','js/libs/jquery.dataTables.min.js','js/libs/dataTables.tableTools.min.js','js/libs/dataTables.scroller.min.js','js/libs/dataTables.responsive.min.js','js/libs/dataTables.colVis.min.js');
$this->load_javascript = array('js/libs/mousetrap.min.js','js/libs/gollum.js','js/libs/rainbowvis.js',
'js/libs/highcharts.js','js/libs/highcharts-more.js',
'https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js',
'https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js',
'//cdnjs.cloudflare.com/ajax/libs/jszip/2.5.0/jszip.min.js',
'//cdn.rawgit.com/bpampuch/pdfmake/0.1.24/build/pdfmake.min.js',
'//cdn.rawgit.com/bpampuch/pdfmake/0.1.24/build/vfs_fonts.js',
'//cdn.datatables.net/buttons/1.2.4/js/buttons.html5.min.js',
'//cdn.datatables.net/buttons/1.2.4/js/buttons.print.min.js');
$this->load_css = array('https://cdn.datatables.net/1.10.13/css/jquery.dataTables.min.css',
'https://cdn.datatables.net/buttons/1.2.4/css/buttons.dataTables.min.css',
'css/gollum.css','css/dataTables.colvis.min.css','css/dataTables.colvis.jqueryui.css','css/dataTables.responsive.min.css','css/dataTables.scroller.min.css','css/dataTables.tableTools.min.css');



/// SEARCH
$this->filtertype = 'flow';
$this->sort = 'runs';
if($this->input->get('sort'))
	$this->sort = safe($this->input->get('sort'));

/// DETAIL

// Making sure we know who is editing
$this->editor = 'Anonymous';
$this->is_owner = false;
$this->editing = false;
if(false !== strpos($_SERVER['REQUEST_URI'],'/edit')){
  if (!$this->ion_auth->logged_in()) {
  header('Location: ' . BASE_URL . 'login');
  }
  else{
  $user = $this->Author->getById($this->ion_auth->user()->row()->id);
  $this->editor = $user->first_name . ' ' . $user->last_name;
  $this->editing = true;
  }
}
$this->user_id = -1;
if ($this->ion_auth->logged_in()) {
  $this->user_id = $this->ion_auth->user()->row()->id;
}

$this->type = 'implementation';
$this->record = false;
$this->displayName = false;
$this->allmeasures = $this->Math_function->getColumnWhere('name','functionType = "EvaluationFunction"');
$this->current_measure = 'predictive_accuracy';
$this->current_task = "Supervised Classification";

if(false !== strpos($_SERVER['REQUEST_URI'],'/f/')) {
	$this->info = explode('/', $_SERVER['REQUEST_URI']);
	$this->id = explode('?',$this->info[array_search('f',$this->info)+1])[0];

	$this->record = $this->Implementation->getByID($this->id);
  $bfilerecord = $this->File->getById( $this->record->binary_file_id );
  if($bfilerecord)
    $this->flow_binary_url =  BASE_URL . 'data/download/' . $bfilerecord->id . '/'. $this->record->fullName;
  $sfilerecord = $this->File->getById( $this->record->source_file_id );
  if($sfilerecord)
    $this->flow_source_url = BASE_URL . 'data/download/' . $sfilerecord->id . '/'. $this->record->fullName;

	// Get data from ES
	$this->p = array();
	$this->p['index'] = 'flow';
	$this->p['type'] = 'flow';
	$this->p['id'] = $this->id;

        $this->down = array();
        $this->down['index'] = 'downvote';
        $this->down['type'] = 'downvote';
        $json = '{
                    "query": {
                      "bool": {
                        "must": [
                          { "match": { "knowledge_type":  "f" }},
                          { "match": { "knowledge_id": '.$this->id.'   }}
                        ]
                      }
                    }
                  }';
        $this->down['body'] = $json;
        if ($this->ion_auth->logged_in()) {
            $this->l = array();
            $this->l['index'] = 'like';
            $this->l['type'] = 'like';
            $json = '{
                        "query": {
                          "bool": {
                            "must": [
                              { "match": { "knowledge_type":  "f" }},
                              { "match": { "knowledge_id": '.$this->id.'   }},
                              { "match": { "user_id": '.$this->ion_auth->user()->row()->id.'}}
                            ]
                          }
                        }
                      }';
            $this->l['body'] = $json;
        }
	try{
		$this->flow = $this->searchclient->get($this->p)['_source'];
                $this->downvotes = $this->searchclient->search($this->down)['hits']['hits'];
                if ($this->ion_auth->logged_in()) {
                  $this->activeuserlike = $this->searchclient->search($this->l)['hits']['hits'];
                }
	} catch (Exception $e) {}

  if(isset($this->flow)){

	$this->displayName = $this->flow['name'];
	$this->versions = $this->Implementation->getAssociativeArray('id', 'version', 'name = "'.$this->displayName.'"');
	ksort($this->versions);

  //wiki import
  $this->wikipage = 'flow-'.$this->id;
  $this->url = $this->wikipage;
  $this->show_history = true;

  $this->preamble = '';
  if(end($this->info) == 'edit')
    $this->url = 'edit/'.$this->wikipage;
  elseif(end($this->info) == 'history')
    $this->url = 'history/'.$this->wikipage;
  elseif(in_array('compare',$this->info)){
    $p = $this->input->post('versions');
    $this->url = 'compare/'.$this->wikipage.'/'.$p[0].'...'.$p[1];}
  elseif(in_array('view',$this->info)){
    $this->url = $this->wikipage.'/'.end($this->info);
    $this->preamble = '<span class="label label-danger" style="font-weight:200">You are viewing version: '.end($this->info).'</span><br><br>';}
  elseif(end($this->info) == 'preview')
    $this->url = 'preview';
  else
    $this->show_history = false;

  // tables -> remove?
	$this->dt_main['columns'] 		= array('r.rid','rid','sid','name','value');
	$this->dt_main['column_widths']		= array(1,1,0,60,30);
	$this->dt_main['column_content']	= array('<a data-toggle="modal" href="r/[CONTENT]/html" data-target="#runModal"><i class="fa fa-info-circle"></i></a>',null,null,'<a href="d/[CONTENT1]">[CONTENT2]</a>',null);
	$this->dt_main['column_source'] 	= array('wrapper','db','db','doublewrapper','db');
	$this->dt_main['base_sql'] 		= 	'SELECT SQL_CALC_FOUND_ROWS `r`.`rid`, `l`.`sid`, concat(d.did, "~", d.name) as name, round(e.value,4) as value '.
										'FROM algorithm_setup l, evaluation e, run r, input_data rd, dataset d '.
										'WHERE r.setup=l.sid  '.
										'AND l.implementation_id="'.$this->id.'"  '.
										'AND l.isDefault="true"' .
										'AND r.rid=rd.data  '.
                    'AND rd.data = d.did ' .
										'AND e.source=r.rid  ';

	$this->dt_main_all['columns'] 		= array('r.rid','rid','sid','name','value');
	$this->dt_main_all['column_widths']		= array(1,1,0,60,30);
	$this->dt_main_all['column_content']	= array('<a data-toggle="modal" href="r/[CONTENT]/html" data-target="#runModal"><i class="fa fa-info-circle"></i></a>',null,null,'<a href="d/[CONTENT1]">[CONTENT2]</a>',null);
	$this->dt_main_all['column_source'] 	= array('wrapper','db','db','doublewrapper','db');

	$this->dt_main_all['base_sql'] 		= 	'SELECT SQL_CALC_FOUND_ROWS `r`.`rid`, `l`.`sid`, concat(d.did, "~", d.name) as name, round(e.value,4) as value '.
										'FROM algorithm_setup l, evaluation e, run r, input_data rd, dataset d  '.
										'WHERE r.setup=l.sid  '.
										'AND l.implementation_id="'.$this->id.'"  '.
										'AND r.rid=rd.data  '.
                    'AND rd.data = d.did ' .
										'AND e.source=r.rid  ';

	$this->dt_params = array();
	$this->dt_params['columns'] 		= array('name', 'generalName', 'defaultValue', 'min', 'max');
	$this->dt_params['column_widths']	= array(10,20,30,10,10,10,10);
	$this->dt_params['base_sql']		= 'SELECT SQL_CALC_FOUND_ROWS `' . implode('`,`',$this->dt_params['columns']) . '` FROM input WHERE implementation_id ="'.$this->id.'" ';

	$this->dt_qualities = array();
	$this->dt_qualities['columns'] 		= array('name','description','value');
	$this->dt_qualities['column_widths']= array(25,50,25);
	$this->dt_qualities['base_sql']		= 'SELECT SQL_CALC_FOUND_ROWS `' . implode('`,`',$this->dt_qualities['columns']) . '` FROM `algorithm_quality`,`quality` WHERE `algorithm_quality`.`quality` = `quality`.`name` AND `algorithm_quality`.`implementation_id`="'.$this->id.'"';


      // licences
      $this->licences = array();
      $this->licences['public domain'] = array( "name" => 'Publicly available', "url" => 'https://creativecommons.org/choose/mark/' );
      $this->licences['Public'] = array( "name" => 'Publicly available', "url" => 'https://creativecommons.org/choose/mark/' );
      $this->licences['CC_BY'] = array( "name" => 'Attribution (CC BY)', "url" => 'http://creativecommons.org/licenses/by/4.0/' );
      $this->licences['CC_BY-SA'] = array( "name" => 'Attribution-ShareAlike (CC BY-SA)', "url" => 'http://creativecommons.org/licenses/by-sa/4.0/' );
      $this->licences['CC_BY-ND'] = array( "name" => 'Attribution-NoDerivs (CC BY-ND)', "url" => 'http://creativecommons.org/licenses/by-nd/4.0/' );
      $this->licences['CC_BY-NC'] = array( "name" => 'Attribution-NonCommercial (CC BY-NC)', "url" => 'http://creativecommons.org/licenses/by-nc/4.0/' );
      $this->licences['CC_BY-NC-SA'] = array( "name" => 'Attribution-NonCommercial-ShareAlike (CC BY-NC-SA)', "url" => 'http://creativecommons.org/licenses/by-nc-sa/4.0/' );
      $this->licences['CC-BY-NC-ND'] = array( "name" => 'Attribution-NonCommercial-NoDerivs (CC BY-NC-ND)', "url" => 'http://creativecommons.org/licenses/by-nc-nd/4.0/' );
      $this->licences['CC0'] = array( "name" => 'Public Domain (CC0)', "url" => 'http://creativecommons.org/about/cc0' );
}
}
function cleanName($string){
	return $safe = preg_replace('/^-+|-+$/', '', strtolower(preg_replace('/[^a-zA-Z0-9]+/', '-', $string)));
}

?>
