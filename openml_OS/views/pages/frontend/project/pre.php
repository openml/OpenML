<?php

$tag_name = gu('tag') ? gu('tag') : $this->input->get('tag');
$tag_by  = gu('u') ? gu('u') : $this->input->get('u');

$with_tag = '';
$where_tag_name = '';
$where_tag_by = '';
$tag_suffix = '';
$did_where = '';


if( $tag_name ) {
  $where_tag_name = 'AND LOWER(tag.tag) = LOWER("' . $tag_name . '") ';
  $with_tag = ' tagged with ' . $tag_name . ' ';
  $tag_suffix .= '/tag/'.$tag_name;
}
if( $tag_by ) {
  $where_tag_by = 'AND tag.uploader = ' . $tag_by . ' ';
  $tag_suffix .= '/u/'.$tag_by;
}

if( $tag_name || $tag_by ) {
  $did_where = 'AND ( d.did IN (SELECT `id` FROM `dataset_tag` `tag` WHERE 1 = 1 ' . $where_tag_name . $where_tag_by . ') OR did IN (SELECT value FROM task_inputs i, task_tag tag WHERE i.task_id = tag.id AND i.input = "source_data" ' . $where_tag_name . $where_tag_by . ')) ';
}

$setup_sql = 
  'SELECT CONCAT("<a href=\'f/",i.id,"'.$tag_suffix.'\'>", sid, "</a>") AS id, CONCAT("<a href=\'f/",i.id,"'.$tag_suffix.'\'>", i.name, "</a>") AS name, s.setup_string ' .
  'FROM `implementation` i, `algorithm_setup` s, `setup_tag` tag ' .
  'WHERE s.sid = tag.id ' .
  $where_tag_name . 
  $where_tag_by . 
  'AND i.id = s.implementation_id ' .
  'GROUP BY s.sid;';
$this->setup_columns = array( 'id', 'name', 'setup_string' );
$this->setup_items = $this->Algorithm_setup->query( $setup_sql );
$this->setup_name = 'Setups' . $with_tag;

$task_sql = 
  'SELECT CONCAT("<a href=\'t/",task.task_id,"'.$tag_suffix.'\'>", task.task_id, "</a>") AS id, '.
  't.name AS task_type, '.
  'CONCAT("<a href=\'t/",task.task_id,"'.$tag_suffix.'\'>", d.name, "</a>") AS name, '.
  'inst.value AS instances, attr.value AS features ' . 
  'FROM task, task_type t, task_tag `tag`, task_inputs `i`, dataset d ' . 
  'LEFT JOIN data_quality inst ON d.did = inst.data AND inst.quality = "NumberOfInstances" ' .
  'LEFT JOIN data_quality attr ON d.did = attr.data AND attr.quality = "NumberOfFeatures" ' .
  'WHERE task.task_id = tag.id ' .
  'AND task.ttid = t.ttid ' .
  'AND i.input = "source_data" AND i.task_id = task.task_id ' . 
  'AND i.value = d.did ' .
  $where_tag_name . 
  $where_tag_by . 
  'GROUP BY task.task_id;';
$this->task_columns = array( 'id', 'task_type', 'name', 'instances', 'features' );
$this->task_items = $this->Algorithm_setup->query( $task_sql );
$this->task_name = 'Tasks' . $with_tag;

$data_sql = 
  'SELECT d.did AS id, d.name, inst.value AS instances, attr.value AS features ' . 
  'FROM dataset_tag `tag`, dataset d ' . 
  'LEFT JOIN data_quality inst ON d.did = inst.data AND inst.quality = "NumberOfInstances" ' .
  'LEFT JOIN data_quality attr ON d.did = attr.data AND attr.quality = "NumberOfFeatures" ' .
  'WHERE 1=1 ' .
  $did_where .
  'GROUP BY d.did;';
$this->data_columns = array( 'id', 'name', 'instances', 'features' );
$this->data_items = $this->Dataset->query( $data_sql );
$this->data_name = 'Data' . $with_tag;

/*$run_sql = 
  'SELECT r.rid, i.id, i.name, r.task_id, r.error '.
  'FROM `run` `r`, `algorithm_setup` `s`, `setup_tag` `st`, task_tag `tt`, `implementation` `i` '.
  'WHERE `i`.`id` = `s`.`implementation_id` AND `r`.`task_id` = `tt`.`id` '.
  'AND `r`.`setup` = `s`.`sid` AND `s`.`sid` = `st`.`id` ' .
  'AND `r`.`error`  IS NOT NULL ' . 
  $where_tag_task . $where_tag_setup . ' ' .
  'GROUP BY r.rid;';
$this->run_columns = array( 'rid', 'task_id', 'id', 'name', 'error' );
$this->run_items = $this->Run->query( $run_sql );
$this->run_name = 'Runs (errors) ' . $with_tag;*/
?>
