<?php
class Study extends MY_Database_Write_Model {

  function __construct() {
    parent::__construct();
    $this->table = 'study';
    $this->id_column = 'id';
    
    $this->load->model('Study_tag');
  }

  function legacy_create($name, $alias, $description, $creator) {
    // insert
    $schedule_data = array(
      'name' => $name, 
      'alias' => $alias, 
      'description' => $description, 
      'main_knowledge_type' => 'run',
      'benchmark_suite' => null,
      'visibility' => 'public',
      'legacy' => 'y', 
      'creation_date' => now(),
      'creator' => $creator
    );
    $study_id = $this->insert($schedule_data);
    
    $tag_data = array(
      'study_id' => $study_id,
      'tag' => 'study_' . $study_id,
      'window_start' => now(),
      'window_end' => null,
      'write_access' => 'private'
    );
    
    $this->Study_tag->insert($tag_data);
    
    // add to elastic search index.
    $this->elasticsearch->index('study', $study_id);

    // add to wiki
    $this->wiki->export_study_to_wiki($study_id);

    return $study_id;
  }
  
}
?>

