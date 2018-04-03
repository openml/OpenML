<?php
$config['max_avatar_size'] = 256;

$config['max_split_arff_size'] = 10000000;

$config['double_epsilon'] = 0.0001; // TODO: calibrate

$config['community_teaser_length'] = 300;

$config['content_directories_create'] = TRUE;
$config['content_directories_mode'] = 0777;

$config['api_session_length'] = '1 hour';

$config['java_heap_space_web'] = '128M';
$config['java_heap_space_cli'] = '2G';

$config['allowed_extensions'] = array('arff','csv','model','xml','rds','txt');

$config['general_http_error_code'] = 412;

$config['default_evaluation_engine_id'] = 1;

?>
