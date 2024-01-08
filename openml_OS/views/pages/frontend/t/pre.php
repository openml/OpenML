<?php

$this->load_javascript = array('js/libs/highcharts.js','js/libs/highcharts-more.js',
'https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js',
'https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js',
'//cdnjs.cloudflare.com/ajax/libs/jszip/2.5.0/jszip.min.js',
'//cdn.rawgit.com/bpampuch/pdfmake/0.1.24/build/pdfmake.min.js',
'//cdn.rawgit.com/bpampuch/pdfmake/0.1.24/build/vfs_fonts.js',
'//cdn.datatables.net/buttons/1.2.4/js/buttons.html5.min.js',
'//cdn.datatables.net/buttons/1.2.4/js/buttons.print.min.js');
$this->load_css = array('https://cdn.datatables.net/1.10.13/css/jquery.dataTables.min.css',
'https://cdn.datatables.net/buttons/1.2.4/css/buttons.dataTables.min.css');

$this->user_id = -1;
$this->is_admin = false;
if ($this->ion_auth->logged_in()) {
	$this->user_id = $this->ion_auth->user()->row()->id;
	if ($this->ion_auth->is_admin()){
		$this->is_admin = true;
	}
}

/// SEARCH
$this->filtertype = 'task';
$this->sort = 'runs';
if($this->input->get('sort'))
	$this->sort = safe($this->input->get('sort'));

$this->active_tab = gu('tab');
if($this->active_tab == false) $this->active_tab = 'searchtab';

$this->current_measure = 'predictive_accuracy';
/// TASK DETAIL

if(false === strpos($_SERVER['REQUEST_URI'],'type') && false !== strpos($_SERVER['REQUEST_URI'],'/t/')) {
	$info = explode('/', $_SERVER['REQUEST_URI']);
	$this->id = explode('?',$info[array_search('t',$info)+1])[0];
	$this->task_id = $this->id;

	//get data from ES
	$this->p = array();
	$this->p['index'] = 'task';
	$this->p['type'] = 'task';
	$this->p['id'] = $this->task_id;

  $this->down = array();
  $this->down['index'] = 'downvote';
  $this->down['type'] = 'downvote';
  $json = '{
              "query": {
                "bool": {
                  "must": [
                    { "match": { "knowledge_type":  "t" }},
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
                        { "match": { "knowledge_type":  "t" }},
                        { "match": { "knowledge_id": '.$this->id.'   }},
                        { "match": { "user_id": '.$this->ion_auth->user()->row()->id.'}}
                      ]
                    }
                  }
                }';
      $this->l['body'] = $json;
  }
	try{
		$result = $this->searchclient->get($this->p);
		$this->task = $result['_source'];

		$this->tt = array();
		$this->tt['index'] = 'task_type';
		$this->tt['type'] = 'task_type';
		$this->tt['id'] = $this->task['tasktype']['tt_id'];
		$this->tasktype = $this->searchclient->get($this->p)['_source'];

    $this->downvotes = $this->searchclient->search($this->down)['hits']['hits'];
    if ($this->ion_auth->logged_in()) {
      $this->activeuserlike = $this->searchclient->search($this->l)['hits']['hits'];
    }

		// evaluations
		if(array_key_exists('evaluation_measures',$this->task))
			$this->current_measure = $this->task['evaluation_measures'];
	} catch (Exception $e) {
    echo 'Caught exception: ',  $e->getMessage(), "\n";
	}

}

// TODO: Update ES to replace these two DB calls
$this->allmeasures = $this->Math_function->getColumnWhere('name','functionType = "EvaluationFunction"');

$io = $this->Implementation->query('SELECT io.name, io.type, io.description, tt.description as typedescription, io.io, io.requirement, ti.value FROM task_type_inout io left join task_inputs ti on (io.name = ti.input and ti.task_id=' . $this->task_id . ") left join task_io_types tt on io.type=tt.name WHERE io.ttid=" . $this->task['tasktype']['tt_id'] );
	if( $io != false ) {
	  foreach( $io as $i ) {
		$inout = array(
			  'name' => $i->name,
			  'type' => $i->type,
			  'description' => $i->description,
			  'typedescription' => $i->typedescription,
			  'category' => $i->io,
			  'value' => $i->value,
			  'requirement' => $i->requirement
			);
		if($i->type == 'Dataset' && is_numeric($i->value)){
			$dataset = $this->Implementation->query('SELECT name, version, url FROM dataset where did=' . $i->value);
			$inout['dataset'] = $dataset[0]->name . " (" . $dataset[0]->version . ")";
			$this->sourcedata_id = $i->value;
			$this->sourcedata_name = $inout['dataset'];
			$this->sourcedata_url = $dataset[0]->url;
		}
		elseif($i->type == 'Estimation Procedure'){
			$ep = $this->Implementation->query('SELECT name FROM estimation_procedure where id=' . $i->value);
			$inout['evalproc'] = $ep[0]->name;
		}
		if($inout['name'] == 'evaluation_measures' and strlen($inout['value'])>0)
				$this->default_measure = str_replace(' ','_',$inout['value']);
		$this->taskio[] = $inout;
	  }
	}

?>
