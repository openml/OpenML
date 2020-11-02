<?php

$config['data_controller'] = BASE_URL . 'data/';

$config['data_tables'] = array(
  'dataset', 'evaluation', 'evaluation_fold', 'evaluation_sample', 'runfile');

$config['xml_fields_dataset'] = array(
  'string'  => array(
     0 => 'name',
     1 => 'description',
     2 => 'format',
     5 => 'collection_date',
     6 => 'language',
     7 => 'licence',
     8 => 'url',
     9 => 'default_target_attribute',
    10 => 'row_id_attribute',
    12 => 'version_label',
    13 => 'citation',
    15 => 'visibility',
    16 => 'original_data_url',
    17 => 'paper_url'),
  'csv'     => array(
     3 => 'creator',
     4 => 'contributor',
    11 => 'ignore_attribute',
    14 => 'tag'
),
  'array'   => array(),
  'plain'   => array()
);

$config['xml_fields_features'] = array(
  'string' => array(
    'index',
    'name',
    'data_type',
    'is_target',
    'NumberOfDistinctValues',
    'NumberOfUniqueValues',
    'NumberOfMissingValues',
    'NumberOfIntegerValues',
    'NumberOfRealValues',
    'NumberOfNominalValues',
    'NumberOfValues',
    'MaximumValue',
    'MinimumValue',
    'MeanValue',
    'StandardDeviation',
    'ClassDistribution'
  ),
  'csv' => array(),
  'array'   => array('nominal_value'),
  'plain'   => array()
);

$config['xml_fields_implementation'] = array(
  'string'  => array(
	  0 => 'name',
	  1 => 'custom_name',
	  2 => 'class_name',
	  3 => 'external_version',
	  4 => 'description',
	  7 => 'licence',
	  8 => 'language',
	  9 => 'fullDescription',
	  10 => 'installationNotes',
	  11 => 'dependencies'),
  'csv'     => array(
	  5 => 'creator',
	  6 => 'contributor',
	  14 => 'tag'),
  'array'   => array(
	  12 => 'parameter',
	  13 => 'component'),
  'plain'   => array()
);

$config['xml_fields_run'] = array(
  'string'  => array('task_id', 'flow_id', 'setup_string', 'error_message'),
  'csv'     => array('tag'),
  'array'   => array('parameter_setting'),
  'plain'   => array('output_data')
);

$config['xml_fields_study'] = array(
  'string' => array('alias', 'main_entity_type', 'benchmark_suite', 'name', 'description'),
  'csv' => array(),
  'array' => array('data', 'tasks', 'flows', 'setups', 'runs'),
  'plain' => array()
);

// qualities to show in, e.g., task list
$config['basic_qualities'] = array(
  "NumberOfInstances", "NumberOfFeatures", "NumberOfClasses", "NumberOfMissingValues",
  "NumberOfInstancesWithMissingValues", "NumberOfNumericFeatures", "NumberOfSymbolicFeatures", 
  "MajorityClassSize", "MinorityClassSize", "MaxNominalAttDistinctValues", "NumBinaryAtts"
);

// task inputs to show in, e.g., task list
$config['basic_taskinputs'] = array(
  "cost_matrix", "estimation_procedure", "evaluation_measures", "source_data", 
  "target_feature", "target_value", "number_samples", "source_data_labeled", 
  "target_feature_event", "target_feature_left", "target_feature_right", 
  "quality_measure", "target_value", "time_limit"
);

$config['taggable_entities'] = array(
  'dataset' => 'Dataset_tag',
  'implementation' => 'Implementation_tag',
  'run' => 'Run_tag',
  'task' => 'Task_tag',
  'algorithm_setup' => 'Setup_tag',
);

?>
