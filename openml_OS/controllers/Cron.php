<?php
class Cron extends CI_Controller {

  function __construct() {
    parent::__construct();

    $this->controller = strtolower(get_class ($this));
    if(!$this->input->is_cli_request()) {
      die('Cron Controller can only be accessed by CLI. ');
    }

    $this->load->model('Dataset');
    $this->load->model('Log');

    $this->load->model('Meta_dataset');
    $this->load->model('File');

    $this->load->helper('file_upload');
    $this->load->helper('text');
    $this->load->helper('directory');
    $this->load->helper('arff');

    $this->load->library('email');
    $this->email->from( EMAIL_FROM, 'The OpenML Team');

    $this->load->model('Algorithm_setup');
    $this->load->model('File');
    $this->load->model('Data_quality');
    $this->load->model('Dataset_status');
    $this->load->model('Implementation');
    $this->load->model('Math_function');
    $this->load->model('Schedule');
    $this->load->model('Study');
    $this->load->model('Task');
    $this->load->model('Task_type');
    $this->load->model('Task_type_inout');
    $this->load->model('Estimation_procedure');
    $this->load->model('Run');
    $this->load->Library('elasticSearch');

    $this->dir_suffix = 'dataset/cron/';

    $this->es_indices = array('downvote', 'study', 'data', 'task', 'download', 'user', 'like', 'measure', 'flow', 'task_type', 'run');
  }

  // indexes a single es item, unless $id = false
  public function index($type, $id = false){
      $time_start = microtime(true);

      if(!$id){
        echo "\r\n Starting ".$type." indexer... ";
        echo $this->elasticsearch->index($type);
      } else {
        echo "\r\n Starting ".$type." indexer for id ".$id." ";
        echo $this->elasticsearch->index($type, $id);
      }

      $time_end = microtime(true);
      $time = $time_end - $time_start;
      echo "\nIndexing done in $time seconds\n";
  }

  // indices a range of es items, starting by $id (or 0 if id == false)
  public function indexfrom($type, $id = false, $verbosity = 0){
      if(!$id){
        // TODO: I guess this function enables the exact same hevaiour as index($type, false) (JvR)
        echo "\r\n Starting ".$type." indexer... ";
        echo $this->elasticsearch->index($type);
      } else {
        echo "\r\n Starting ".$type." indexer from id ".$id."... ";
        echo $this->elasticsearch->index_from($type, $id, $verbosity);
      }
  }

  public function update_tag($type, $id){
      echo "tagging ".$type." ".$id;
      $this->elasticsearch->update_tags($type, $id);
  }

  // initialize all es indexes
  public function initialize_es_indices() {
    foreach($this->es_indices as $index) {
      $this->elasticsearch->initialize_index($index);
    }
  }

  // builds all es indexes
  public function build_es_indices() {
    foreach($this->es_indices as $index) {
      $this->indexfrom($index, 1);
    }
  }
  
  public function arff_parses($file_id) {
    $file = $this->File->getById($file_id);
    if ($file === false) {
      die('File does not exists.');
    }
    
    $ext = strtolower($file->extension);
    if ($ext != 'arff' && $ext != 'sparse_arff') {
      die('File does not have an arff extension.');
    }
    
    if ($file->type == 'url') {
      $result = ARFFcheck($file->filepath, 100);
    } else {
      $result = ARFFcheck(DATA_PATH . $file->filepath, 100);
    }
    
    if ($result === TRUE) {
      die('Valid arff. ');
    } else {
      die($result);
    }
  }
  
  function missing_file_report() {
    $start_time = now();
    $batch_size = 10000;
    $missing_files = array();
    for ($i = 0; $i == 0 || $all_records !== false; ++$i) {
      $all_records = $this->File->getWhere('type != "url"', null, $batch_size, $i * $batch_size);
      if ($all_records) {
        echo now() . ' Starting with batch ' . $i . ' with ids ' . ($i * $batch_size) . ' - ' . ($batch_size + ($i * $batch_size)) . "..\n";
        foreach ($all_records as $record) {
          if (file_exists(DATA_PATH . $record->filepath) == false) {
            $missing_files[] = $record->id;
            echo now() . ' Missing file from record with id: ' . $record->id . "\n";
          }
        }
      }
    }
    
    $to = EMAIL_API_LOG;
    $subject = 'OpenML Cronjob report - Missing files';
    $content = 'Start time: ' . $start_time . "\nFinish time: " . now() . "\nServer: " . BASE_URL . "\nMissing files from records with the following ID's: " . implode(', ', $missing_files);
    sendEmail($to, $subject, $content, 'text');
  }
  
  // temp 
  function move_run_files($start_index, $end_index) {
    $this->load->model('Runfile');
    $results = $this->Runfile->getWhere("source >= " . $start_index . ' AND source < ' . $end_index);
    $run_path = 'run_structured/';
    
    foreach ($results as $result) {
      $file = $this->File->getById($result->file_id);
      
      if (substr($file->filepath, 0, strlen($run_path)) === $run_path) {
        continue;
      }
      $this->content_folder_modulo = 10000;
      $runId = $result->source;
      $subdirectory = floor($runId / $this->content_folder_modulo) * $this->content_folder_modulo;
      $to_folder = $run_path . '/' . $subdirectory . '/' . $runId . '/';
      $success = $this->File->move_file($file->id, $to_folder);
      if (!$success) {
        echo now() . ' failure for file ' . $result->field . ' from run id ' . $runId . "..\n";
      }
    }
  }

  function install_database() {
    // note that this one does not come from DATA folder, as they are stored in github
    $models = directory_map('data/sql/', 1);
    $manipulated_order = array('file.sql', 'implementation.sql', 'algorithm_setup.sql', 'dataset.sql', 'study.sql', 'groups.sql', 'users.sql');

    // moves elements of $manipulated_order to the start of the models array
    foreach (array_reverse($manipulated_order) as $name) {
      if (in_array($name, $models)) {
        array_unshift($models, $name);
      }
    }
    $models = array_unique($models);

    foreach ($models as $m) {
      $modelname = ucfirst(substr($m, 0, strpos($m, '.')));
      if ($this->load->is_model_loaded($modelname) == false) { $this->load->model($modelname); }
      if ($this->$modelname->get() === false) {
        $sql = file_get_contents('data/sql/' . $m);
        
        echo 'inserting ' . $modelname . ', with ' . strlen($sql) . ' characters... ' . "\n";
        // might need to adapt this, because not all models are supposed to write
        $result = $this->$modelname->query($sql);
        
        if ($result === false) {
          echo 'failure: ' . $this->$modelname->mysqlErrorMessage() . "\n";
        }
      } else {
        echo 'skipping ' . $modelname . ', as it is not empty... ' . "\n";
      }
    }
  }

  function create_meta_dataset($id = false) {
    if ($id == false) {
      $meta_dataset = $this->Meta_dataset->getWhere('processed IS NULL');
      echo 'No id specified, requesting first dataset in queue.' . "\n";
    } else {
      $meta_dataset = $this->Meta_dataset->getWhere('id = "' . $id . '"');
      echo 'Requesting dataset with id ' . $id . ".\n";
    }

    if ($meta_dataset) {
      $meta_dataset = $meta_dataset[0];
      echo 'Processing meta-dataset with id ' . $meta_dataset->id . ".\n";
      $this->Meta_dataset->update( $meta_dataset->id, array('processed' => now()));
      $dataset_constr = ($meta_dataset->datasets) ? 'AND d.did IN (' . $meta_dataset->datasets . ') ' : '';
      $task_constr = ($meta_dataset->tasks) ? 'AND t.task_id IN (' . $meta_dataset->tasks . ') ' : '';
      $flow_constr = ($meta_dataset->flows) ? 'AND i.id IN (' . $meta_dataset->flows . ') ' : '';
      $setup_constr = ($meta_dataset->setups) ? 'AND s.sid IN (' . $meta_dataset->setups . ') ' : '';
      $function_constr = ($meta_dataset->functions) ? 'AND m.name IN (' . $meta_dataset->functions . ') ' : '';

      $quality_colum = 'data_quality';
      $evaluation_column = 'evaluation';
      $evaluation_keys = array('m.name');
      $quality_keys = array();

      if ($meta_dataset->task_type == 3) {
        $evaluation_keys = array('e.repeat', 'e.fold', 'e.sample', 'e.sample_size', 'm.name');
        $evaluation_column = 'evaluation_sample';
      }
      
      mkdir(DATA_PATH . $this->dir_suffix, $this->config->item('content_directories_mode'), true);

      $tmp_path = '/tmp/' . rand_string( 20 ) . '.csv';

      if ($meta_dataset->type == 'qualities') {
        $quality_keys_string = '';
        if ($quality_keys) {
          $quality_keys_string = implode(', ', $quality_keys) . ',';
          $quality_keys_key_string = '"' . implode('","', array_keys($quality_keys)) . '",';
        }
        // very important to not have any tailing spaces after a comma
        $header = '"data_id","task_id","name","quality",' . $quality_keys_key_string . '"value" ' . "\n";
        $sql =
          'SELECT d.did, t.task_id, d.name, q.quality, ' . $quality_keys_string . 'q.value ' .
          'FROM dataset d, '.$quality_colum.' q, task t, task_inputs i ' .
          'WHERE t.task_id = i.task_id ' .
          'AND i.input = "source_data" ' .
          'AND i.value = q.data AND q.evaluation_engine_id = 1 ' .
          'AND d.did = q.data ' .
          'AND t.ttid = "' . $meta_dataset->task_type . '" ' .
          $dataset_constr . $task_constr .
          'INTO OUTFILE "'. $tmp_path .'" ' .
          'FIELDS TERMINATED BY "," ' .
          'ENCLOSED BY "\"" ' .
          'LINES TERMINATED BY "\n" ' .
          ';';
      } elseif ($meta_dataset->type == 'evaluations') {
        // very important to not have any tailing spaces after a comma
        $header = '"run_id","setup_id","task_id","' . implode('","', $evaluation_keys) . '","value","task_name","flow_name"' . "\n";
        $sql =
          'SELECT MAX(r.rid) AS run_id, s.sid AS setup_id, t.task_id AS task_id, ' .
          implode(', ', $evaluation_keys) . ', MAX(e.value) ' .
          ', CONCAT("Task_", t.task_id, "_", MAX(d.name)) AS task_name, MAX(i.fullName) as flow_name ' .
//          ',s.setup_string ' .
//          ',CONCAT(i.fullName, " on ", d.name) as textual ' .
          'FROM run r, task t, task_inputs v, dataset d, algorithm_setup s, implementation i, ' . $evaluation_column . ' e, math_function m ' .
          'WHERE r.task_id = t.task_id AND v.task_id = t.task_id  ' .
          'AND v.input = "source_data" AND v.value = d.did ' .
          'AND r.setup = s.sid AND s.implementation_id = i.id ' .
          'AND e.source = r.rid ' .
          'AND e.function_id = m.id AND e.evaluation_engine_id = 1 ' .
          'AND t.ttid = "' . $meta_dataset->task_type . '"' .
          $dataset_constr . $task_constr . $flow_constr . $setup_constr . $function_constr .
           /* the GROUP BY line makes stuff slower, we might want to comment it out. */
          'GROUP BY r.setup, r.task_id, ' . implode(',', $evaluation_keys) . ' ' .
          'INTO OUTFILE "' . $tmp_path .'" ' .
          'FIELDS TERMINATED BY "," ' .
          'ENCLOSED BY "\"" ' .
          'LINES TERMINATED BY "\n" ' .
          ';';
      } elseif ($meta_dataset->type == 'inputs') {
        $header = null;
        $sql =
          'SELECT is.setup, i.fullname AS flowname, ip.name, is.value ' .
          'FROM `input_setting` `is` , input `ip`, algorithm_setup s, implementation i ' .
          'WHERE ip.id = is.input_id ' .
          'AND ip.implementation_id = i.id ' .
          'AND is.setup = s.sid ' .
          'AND s.implementation_id = i.id ' .
          $flow_constr . $setup_constr .
          'INTO OUTFILE "'. $tmp_path .'" ' .
          'FIELDS TERMINATED BY "," ' .
          'ENCLOSED BY "\"" ' .
          'LINES TERMINATED BY "\n" ' .
          ';';
      } else {
        $this->_error_meta_dataset($meta_dataset->id, 'Meta dataset type not recognized: ' . $meta_dataset->type, $meta_dataset->user_id);
        return;
      }

      // this query requires FILE privileges (which is by default disabled)
      $this->Dataset->query($sql);
      $success = file_exists($tmp_path);

      if (!$success) {
        $error = 'MySQL Error #' . $this->Dataset->mysqlErrorNo() . ': ' . $this->Dataset->mysqlErrorMessage();
        $this->_error_meta_dataset($meta_dataset->id, $error, $meta_dataset->user_id);
        return;
      }

      if ($header != null) {
        $res = prepend_to_file($header, $tmp_path);
        if (!$res) {
          $this->_error_meta_dataset($meta_dataset->id, 'Failed to prepend header. ', $meta_dataset->user_id);
          return;
        }
      }

      $filename = getAvailableName(DATA_PATH . $this->dir_suffix, 'meta_dataset.csv');
      $filepath = DATA_PATH . $this->dir_suffix . $filename;
      $success = rename($tmp_path, $filepath);

      if (!$success) {
        $this->_error_meta_dataset($meta_dataset->id, 'Failed to move csv to data directory. Filename: ' . $filename, $meta_dataset->user_id);
        return;
      }

      $file_id = $this->File->register_created_file($this->dir_suffix, $filename, $meta_dataset->user_id, 'dataset', 'text/csv', 'private');

      $this->Meta_dataset->update($meta_dataset->id, array( 'file_id' => $file_id));

      $user = $this->ion_auth->user($meta_dataset->user_id)->row();
      $this->email->to($user->email);
      $this->email->subject('OpenML Meta Dataset');
      $this->email->message("This is an automatically generated email. The your requested meta-dataset was created successfully and can be downloaded from " . BASE_URL);
      $this->email->send();
    } else {
      echo 'No meta-dataset to process. '."\n";
    }
  }

  private function _error_meta_dataset($id, $msg, $user_id) {
    echo $msg . "\n";
    $this->Meta_dataset->update($id, array('error_message' => $msg));

    $user = $this->ion_auth->user($user_id)->row();
    $this->email->to($user->email);
    $this->email->bcc($this->config->item('email_debug'));
    $this->email->subject('OpenML Meta Dataset');
    $this->email->message("This is an automatically generated email. \n\nUnfortunatelly, the creation of the Meta Dataset was unsuccessfull. \n\nThe full error message is available to the system administrators. In case of any questions, please don't hesitate to contact the OpenML Team. ");
    $this->email->send();
  }
}
?>
