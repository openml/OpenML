<?php

if(false === strpos($_SERVER['REQUEST_URI'],'/r/')) {
  header('Location: search?type=run');
  die();
}

$this->load_javascript = array('js/libs/highcharts.js','js/libs/highcharts-more.js','js/libs/jquery.dataTables.min.js');

$this->initialMsgClass = '';
$this->initialMsg = '';

if (!$this->ion_auth->logged_in()) {
	$this->initialMsgClass = 'alert alert-warning';
	$this->initialMsg = 'Before submitting content, please login first!';
}

$this->user_id = -1;
if ($this->ion_auth->logged_in()) {
  $this->user_id = $this->ion_auth->user()->row()->id;
}

$this->datasets 			= $this->Dataset->getColumnWhere( 'name', 'isOriginal = "true"', '`name` ASC' );
$this->datasetIds 			= $this->Dataset->getColumn( 'did', 'did' );
$this->datasetVersion		= $this->Dataset->getColumnFunction( 'CONCAT(`name`,"(",`version`,")")', '`name` ASC' );
$this->datasetVersionOriginal= $this->Dataset->getColumnFunctionWhere( 'CONCAT(`name`,"(",`version`,")")', 'isOriginal = "true"', '`name` ASC' );

$this->formats				= $this->Dataset->getDistinct( 'format' );
$this->licences				= $this->Dataset->getDistinct( 'licence' );

$this->evaluationMetrics	= $this->Math_function->getColumnWhere( 'name', 'functionType = "EvaluationFunction"' );
//$this->classificationEvaluationMetrics	= $this->Task_type_function->getColumnWhere( 'math_function', 'ttid = 1' );
//$this->regressionEvaluationMetrics	= $this->Task_type_function->getColumnWhere( 'math_function', 'ttid = 2' );

$this->taskTypes			= $this->Task_type->getColumn( 'name' );

$this->collections			= $this->Dataset->getDistinct( 'collection' );

$this->algorithms = array();
$this->implementations = array();
$implementationsAlgorithms  = $this->Implementation->getColumns( '`implementation`.`fullName`, `implementation`.`implements`', '`implements` ASC' );
foreach( $implementationsAlgorithms as $i ) {
	if( $i->implements != false )
		$this->algorithms[] = $i->implements;
	$this->implementations[] = $i->fullName;
}
$this->active_tab = "";

function format_eval_name($name){
	$name = str_replace('_',' ',$name);
	$name = str_replace(' roc ',' ROC ',$name);
	$name = str_replace('kb ','KB ',$name);
	$name = str_replace('os ', 'OS ',$name);
	$name = str_replace('scimark ','SciMark ',$name);
	$name = str_replace('cpu ','CPU ',$name);
	return ucfirst($name);
}

function box_plot_values($array)
{
    $return = array(
        'lower_outlier'  => 0,
        'min'            => 0,
        'q1'             => 0,
        'median'         => 0,
        'q3'             => 0,
        'max'            => 0,
        'higher_outlier' => 0,
    );

    $array_count = count($array);
    sort($array, SORT_NUMERIC);

    $return['min']            = $array[0];
    $return['lower_outlier']  = $return['min'];
    $return['max']            = $array[$array_count - 1];
    $return['higher_outlier'] = $return['max'];
    $middle_index             = floor($array_count / 2);
    $return['median']         = $array[$middle_index]; // Assume an odd # of items
    $lower_values             = array();
    $higher_values            = array();

    // If we have an even number of values, we need some special rules
    if ($array_count % 2 == 0)
    {
        // Handle the even case by averaging the middle 2 items
        $return['median'] = round(($return['median'] + $array[$middle_index - 1]) / 2, 4);

        foreach ($array as $idx => $value)
        {
            if ($idx < ($middle_index - 1)) $lower_values[]  = $value; // We need to remove both of the values we used for the median from the lower values
            elseif ($idx > $middle_index)   $higher_values[] = $value;
        }
    }
    else
    {
        foreach ($array as $idx => $value)
        {
            if ($idx < $middle_index)     $lower_values[]  = $value;
            elseif ($idx > $middle_index) $higher_values[] = $value;
        }
    }

    $lower_values_count = count($lower_values);
    $lower_middle_index = floor($lower_values_count / 2);
		if($lower_values_count>0){
    $return['q1']       = $lower_values[$lower_middle_index];
    if ($lower_values_count % 2 == 0){
			if ($lower_middle_index != 0) {
				$return['q1'] = round(($return['q1'] + $lower_values[$lower_middle_index - 1]) / 2, 4);
			}
			else {
				$return['q1'] = round(($return['q1'] + $lower_values[0]) / 2, 4);
			}
		}
	  }

    $higher_values_count = count($higher_values);
    $higher_middle_index = floor($higher_values_count / 2);
		if($higher_values_count>0){
    $return['q3']        = $higher_values[$higher_middle_index];
    if ($higher_values_count % 2 == 0){
			if ($lower_middle_index != 0) {
        $return['q3'] = round(($return['q3'] + $higher_values[$higher_middle_index - 1]) / 2, 4);
			}
			else {
				$return['q3'] = round(($return['q3'] + $higher_values[0]) / 2, 4);
			}
		}
	  }
    // Check if min and max should be capped
    $iqr = $return['q3'] - $return['q1']; // Calculate the Inner Quartile Range (iqr)
    if ($return['q1'] > $iqr)                  $return['min'] = $return['q1'] - $iqr;
    if ($return['max'] - $return['q3'] > $iqr) $return['max'] = $return['q3'] + $iqr;

    return $return;
}

$this->record = array();
$this->runsetup = array();
$this->runevaluations = array();



if(false !== strpos($_SERVER['REQUEST_URI'],'/r/')) { // DETAIL
  $info = explode('/', $_SERVER['REQUEST_URI']);
	$this->id = explode('?',$info[array_search('r',$info)+1])[0];
  $this->run_id = $this->id;
  if(!$this->run_id)  {  su('r'); }
}
 if (isset($this->run_id)){


   //get data from ES
   $this->p = array();
   $this->p['index'] = 'openml';
   $this->p['type'] = 'run';
   $this->p['id'] = $this->run_id;

    $this->down = array();
    $this->down['index'] = 'openml';
    $this->down['type'] = 'downvote';
    $json = '{
                "query": {
                  "bool": {
                    "must": [
                      { "match": { "knowledge_type":  "r" }},
                      { "match": { "knowledge_id": '.$this->id.'   }}
                    ]
                  }
                }
              }';
    $this->down['body'] = $json;
    if ($this->ion_auth->logged_in()) {
        $this->l = array();
        $this->l['index'] = 'openml';
        $this->l['type'] = 'like';
        $json = '{
                    "query": {
                      "bool": {
                        "must": [
                          { "match": { "knowledge_type":  "r" }},
                          { "match": { "knowledge_id": '.$this->id.'   }},
                          { "match": { "user_id": '.$this->ion_auth->user()->row()->id.'}}
                        ]
                      }
                    }
                  }';
        $this->l['body'] = $json;
    }
   try{
     $this->run = $this->searchclient->get($this->p)['_source'];
     $this->downvotes = $this->searchclient->search($this->down)['hits']['hits'];
        if ($this->ion_auth->logged_in()) {
          $this->activeuserlike = $this->searchclient->search($this->l)['hits']['hits'];
        }

     $this->p['type'] = 'flow';
     $this->p['id'] = $this->run['run_flow']['flow_id'];
     $this->flow = $this->searchclient->get($this->p)['_source'];
     $this->flow_parameters = array();
     foreach( $this->flow['parameters'] as $p ){
       $this->flow_parameters[$p['full_name']] = $p;
     }
   } catch (Exception $e) {}

   $this->file_descriptions = array(
     "description" => "XML file describing the run, including user-defined evaluation measures.",
     "model_readable" => "A human-readable description of the model that was built.",
     "model_serialized" => "A serialized description of the model that can be read by the tool that generated it.",
     "predictions" => "ARFF file with instance-level predictions generated by the model.",
     "trace" => "ARFF file with the trace of all hyperparameter settings tried during optimization, and their performance."
   );

   $this->benchmarks = array("Fast Fourier Transform","Jacobi SOR", "Monte Carlo integration", "Sparse matrix multiply", "Dense LU maxtrix factorization");
   $this->binary_measures = array("area_under_roc_curve","f_measure","number_of_instances","precision","recall");
}
   ?>
