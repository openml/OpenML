<?php
if (!$this->ion_auth->logged_in()) {
  sm('Please login first');
  su('login');
}

$this->measures = array("area_under_roc_curve", "predictive_accuracy", "build_cpu_time", "usercpu_time_millis", "cortana_quality", "joint_entropy", "pattern_team_auroc10", "number_of_instances");

$sql = 'SELECT `m`.`id`, `m`.`request_date`, `m`.`type`, ' .
       'IF(CHAR_LENGTH(`m`.`datasets`)>20, CONCAT( "<span>", SUBSTRING(`m`.`datasets`, 1, 15), "...</span>"), `m`.`datasets`) AS `datasets`, ' .
       'IF(CHAR_LENGTH(`m`.`tasks`)>20, CONCAT( "<span>", SUBSTRING(`m`.`tasks`, 1, 15), "...</span>"), `m`.`tasks`) AS `tasks`, ' . 
       'IF(CHAR_LENGTH(`m`.`flows`)>20, CONCAT( "<span>", SUBSTRING(`m`.`flows`, 1, 15), "...</span>"), `m`.`flows`) AS `flows`, ' . 
       'IF(CHAR_LENGTH(`m`.`setups`)>20, CONCAT( "<span>", SUBSTRING(`m`.`setups`, 1, 15), "...</span>"), `m`.`setups`) AS `setups`, ' . 
       '`m`.`functions`, ' .
       'IF(`f`.`id` IS NOT NULL, CONCAT("<a href=\"'.DATA_URL.'download/", `f`.`id`, "/", `f`.`filename_original`, "\" target=\"_blank\"><i class=\"fa fa-file-excel-o\"></i></a>"), "") AS `download` ' .
       'FROM `meta_dataset` `m` LEFT JOIN `file` `f` ON `m`.`file_id` = `f`.`id` ' .
       'WHERE user_id = ' . $this->ion_auth->get_user_id() . ' ' .
       'ORDER BY request_date DESC; ';

$this->columns = array( 'id', 'request_date', 'type', 'datasets', 'tasks', 'flows', 'setups', 'functions', 'download' );
$this->items = $this->Author->query( $sql );
$this->name = false;
$this->task_types = $this->Task_type->get( );
$this->check = false;
$this->ttid=$this->input->post('task_type');

$legal_task_types = array();
foreach( $this->task_types as $tt ) {
  $legal_task_types[] = $tt->ttid;
}

/* previously: post.php */
if( $_POST || $this->input->get('check') ) {
  $this->check = $this->input->post('check');
  
  $type    = $this->input->post('type');
  $task_type=$this->input->post('task_type');
  $datasets= $this->input->post('datasets');
  $flows   = $this->input->post('flows');
  $setups  = $this->input->post('setups');
  $tasks   = $this->input->post('tasks');

  if( $this->input->post('check') == false && $this->input->post('create') == false && $this->input->get('check') == true ) {
    // obtain check based on 
    $tasks  = $this->input->get('check');
    $setups = $this->input->get('check');
    $this->check = true;
  }

  $dataset_ids = ($datasets)? $this->Dataset_tag->get_ids( explode( ',', $datasets ) ) : null;
  $flow_ids    = ($flows)   ? $this->Implementation_tag->get_ids( explode( ',', $flows ) ) : null;
  $task_ids    = ($tasks)   ? $this->Task_tag->get_ids( explode( ',', $tasks ) ) : null;
  $setup_ids   = ($setups)  ? $this->Setup_tag->get_ids( explode( ',', $setups ) ) : null;
  
  
  if($type != 'inputs' && $dataset_ids == false && $task_ids === false) {
    sm('Wrong input: Either of the input fields (datasets, tasks) had no results. ' );
    su('frontend/page/meta_dataset');
  }
  if($type != 'features' && $flow_ids == false && $setup_ids == false) {
    sm('Wrong input: Either of the input fields (implementations, setups) had no results. ' );
    su('frontend/page/meta_dataset');
  }

  if( $this->input->post('create') == true ) {
    $functions = $this->input->post('functions');

    $legal_functions = $this->Math_function->getColumnWhere('name', 'functionType = "EvaluationFunction"');

    $illegal_value = array();
    foreach( $functions as $f ) {
      if( in_array( $f, $legal_functions ) == false ) {
        $illegal_value[] = $f;
      }
    }
    if( $illegal_value ) {
      sm('Illegal value in function list: ' . implode( ', ', $illegal_value ) );
      su('frontend/page/meta_dataset');
    }

    if( $functions == false ) {
      sm('Please select at least one function. ' );
      su('frontend/page/meta_dataset');
    }
    
    if( in_array( $task_type, $legal_task_types ) == false ) {
      sm('Task type illegal. Please fill in the form correctly. ' );
      su('frontend/page/meta_dataset');
    }

    $functions = '"' . implode( '", "', $functions ) . '"';
    // TODO: don't store evaluation stuff if type = qualities
    $md = array(
      'request_date' => now(),
      'type' => $type,
      'task_type' => $task_type, 
      'datasets' => $dataset_ids ? implode( ', ', $dataset_ids ) : null,
      'tasks' => $task_ids ? implode( ', ', $task_ids ) : null,
      'flows' => $flow_ids ? implode( ', ', $flow_ids ) : null,
      'setups' => $setup_ids ? implode( ', ', $setup_ids ) : null,
      'functions' => $functions ? $functions : null,
      'user_id' => $this->ion_auth->get_user_id() );
    
    if ($type == 'qualities') {
      unset($md['functions']);
      unset($md['setups']);
      unset($md['flows']);
    }
    
    if ($type == 'inputs') {
      unset($md['functions']);
      unset($md['datasets']);
      unset($md['tasks']);
    }
      
    $res = $this->Meta_dataset->insert( $md );

    sm('Meta dataset will be created. It can take several minutes to be generated.');
    su('frontend/page/meta_dataset#overview');
    
  } elseif($this->check) {
    $this->runs_done = 0;
    $this->runs_total = 0;
    
    // TODO: strange behaviour. Only selects when having a setup. Can not replace
    // by a LEFT JOIN, as we always need a setup to perform the run
    $sql_setups = 
      'SELECT `s`.`sid`, `i`.`dependencies`, `i`.`name`, `s`.`setup_string` ' . 
      'FROM `algorithm_setup` `s`, `implementation` `i` '.
      'WHERE `s`.`implementation_id` = `i`.`id` ' . 
      (($setup_ids) ? ('AND `s`.`sid` IN (' . implode( ',', $setup_ids ) . ') ') : '' ) . 
      (($flow_ids) ? ('AND `i`.`id` IN (' . implode( ',', $flow_ids ) . ') ') : '' );
    $res_setups = $this->Algorithm_setup->query( $sql_setups );
    
    $sql_tasks = 
      'SELECT `t`.*, d.name, tt.name AS task_type ' .
      'FROM `task` `t`, `task_type` `tt`, `task_inputs` `i`, `dataset` `d` ' .
      'WHERE `t`.`task_id` = `i`.`task_id` ' . 
      'AND `i`.`input` = "source_data" ' .
      'AND `i`.`value` = `d`.`did` ' .
      'AND `t`.`ttid` = `tt`.`ttid` ' .
      'AND `t`.`ttid` = "'.$task_type.'" ' .
      (($dataset_ids) ? ('AND `i`.`value` IN (' . implode( ',', $dataset_ids ) . ') ') : '' ) . 
      (($task_ids) ? ('AND `t`.`task_id` IN (' . implode( ',', $task_ids ) . ') ') : '' );
    $res_tasks = $this->Task->query( $sql_tasks );
    
    // TODO: implementations
    $sql_runs = 
      'SELECT `r`.`task_id`,`r`.`setup`, GROUP_CONCAT(`r`.`error_message`) AS `error_message`, GROUP_CONCAT(`re`.`error`) AS `error` ' .
      'FROM `task_inputs` `d`, `task` `t`, `algorithm_setup` `s`, `run` `r` '.
      'LEFT JOIN `run_evaluated` `re` ON `r`.`rid` = `re`.`run_id` ' .
      'WHERE `r`.`task_id` = `d`.`task_id` ' . 
      'AND `d`.`input` = "source_data" ' .
      'AND `t`.`task_id` = `r`.`task_id` ' . 
      'AND `r`.`setup` = `s`.`sid` ' .
      (($dataset_ids) ? ('AND `d`.`value` IN (' . implode( ',', $dataset_ids ) . ') ') : '' ) . 
      (($task_ids) ? ('AND `r`.`task_id` IN (' . implode( ',', $task_ids ) . ') ') : '' ) . 
      (($flow_ids) ? ('AND `s`.`implementation_id` IN (' . implode( ',', $flow_ids ) . ') ') : '' ) . 
      (($setup_ids) ? ('AND `r`.`setup` IN (' . implode( ',', $setup_ids ) . ') ') : '' ) . 
      'AND `t`.`ttid` = "'.$task_type.'" ' .
      'GROUP BY `r`.`task_id`, `r`.`setup`;';
    $res_runs = $this->Run->query( $sql_runs );
    $this->data = array();
    $this->warning = array();
    $this->error = array();
    $this->runs_total = count($res_tasks) * count($res_setups);
    $this->runs_done = count($res_runs);

    if($res_runs == false || $res_tasks == false || $res_setups == false) {
      
      
    } else {
      $this->task_reference = array();
      $this->setup_reference = array();
      
      foreach($res_setups as $setup) {
        $this->setup_reference[$setup->sid] = array( 
          'name' => $setup->name,
          'dependencies' => $setup->dependencies, 
          'setup_string' => $setup->setup_string);
      }
      foreach($res_tasks as $task) {
        $this->task_reference[$task->task_id] = array(
          'ttid' => $task->ttid,
          'dataset' => $task->name,
          'task_type' => $task->task_type
        );
      }
      ksort($this->task_reference);
      ksort($this->setup_reference);
      
      foreach(array_keys($this->setup_reference) as $s) {
        $this->data[$s] = array();
        $this->error[$s] = array();
        $this->warning[$s] = array();
        foreach(array_keys($this->task_reference) as $t) {
          $this->data[$s][$t] = false;
          $this->error[$s][$t] = false;
          $this->warning[$s][$t] = false;
        }
      }
      
      foreach($res_runs as $res) {
        $this->data[$res->setup][$res->task_id] = true;
        if ($res->error != null) {
          $this->warning[$res->setup][$res->task_id] = $res->error;
        }
        if ($res->error_message != null) {
          $this->error[$res->setup][$res->task_id] = $res->error_message;
        }
      }
      
      if($this->input->post('schedule') && $this->ion_auth->is_admin()) {
        $schedule = array();
        foreach (array_keys($this->setup_reference) as $s) {
          foreach (array_keys($this->task_reference) as $t) {
            if ($this->data[$s][$t] == false) {
              $schedule[] = array(
                'sid' => $s,
                'task_id' => $t,
                'experiment' => 'form_request',
                'active' => true,
                'ttid' => $this->task_reference[$t]['ttid'],
                'dependencies' => $this->setup_reference[$s]['dependencies'],
                'setup_string' => $this->setup_reference[$s]['setup_string'] 
              );
              
              if (count($schedule) > 100000) {
                $this->Schedule->insert_batch($schedule);
                $schedule = array();
              }
            }
          }
        }
        $this->Schedule->insert_batch($schedule);
      }
    }
  }
}

?>
