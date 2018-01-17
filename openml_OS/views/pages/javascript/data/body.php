function expdbDatasets() {
	return <?php echo array_to_js_array( $this->datasets ); ?>;
}

function expdbDatasetVersion() {
	return <?php echo array_to_js_array( $this->datasetVersion ); ?>;
}

function expdbDatasetVersionOriginal() {
	return <?php echo array_to_js_array( $this->datasetVersionOriginal ); ?>;
}

function expdbDatasetIDs() {
	return <?php echo array_to_js_array( $this->datasetIds ); ?>;
}

function expdbEvaluationMetrics() {
	return <?php echo array_to_js_array( $this->evaluationMetrics ); ?>;
}

function expdbAlgorithms() {
	return <?php echo array_to_js_array( $this->algorithms ); ?>;
}

function expdbImplementations() {
	return <?php echo array_to_js_array( $this->implementations ); ?>;
}

function expdbTaskTypes() {
	return <?php echo array_to_js_array( $this->taskTypes ); ?>;
}
