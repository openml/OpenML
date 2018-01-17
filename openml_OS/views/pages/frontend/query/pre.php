<?php
$this->load_javascript = array('js/libs/highcharts.js','js/libs/highcharts-more.js','js/libs/jquery.dataTables.min.js','js/libs/processing.js','js/libs/dat.gui.min.js','js/libs/codemirror.js','js/libs/mysql.js');
$this->load_css = array('css/jquery.dataTables.min.css','css/dataTables.colvis.min.css','css/dataTables.colvis.jqueryui.css','css/dataTables.responsive.min.css','css/dataTables.scroller.min.css','css/dataTables.tableTools.min.css','css/codemirror.css','css/eclipse.css');

$this->initialMsgClass = '';
$this->initialMsg = '';

if (!$this->ion_auth->logged_in()) {
	$this->initialMsgClass = 'alert alert-warning';
	$this->initialMsg = 'Before submitting content, please login first!';
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

$this->active_tab = "sqltab";

?>
