<?php
ini_set('display_errors',1);
ini_set('display_startup_errors',1);
error_reporting(-1);


if (!$this->ion_auth->logged_in()) {
	header('Location: ' . BASE_URL . 'login');
}

$this->task_ids = array();
$this->new_text = '';

$ttid_sel = $this->input->post( 'ttid' );
$this->task_types = $this->Task_type->get( );
for( $i = 0; $i < count($this->task_types); ++$i ) {
  $clause = '`io` = "input" AND `io` = "input" AND ' .
            '`ttid` = "'.$this->task_types[$i]->ttid.'"';
  $this->task_types[$i]->in = $this->Task_type_inout->getWhere( $clause, '`order` ASC' );
  $this->task_types[$i]->selected = ($ttid_sel == $this->task_types[$i]->ttid) ? 'selected="selected"' : '';
}

$this->responsetype = '';
$this->response = '';

$this->newtype = $this->subpage;

if ($this->subpage == 'task'){

	if($this->input->get('data')){
		$this->dataname = $this->input->get('data');
	}

$this->datasets 			= $this->Dataset->getColumnWhere( 'name', 'isOriginal = "true"', '`name` ASC' );
$this->datasetIds 			= $this->Dataset->getColumn( 'did', 'did' );
$this->datasetVersion		= $this->Dataset->getColumnFunctionWhere( 'CONCAT(`name`,"(",`version`,")")', 'did IN (select did from dataset_status where status = "active")', '`name` ASC' );
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
}

?>
