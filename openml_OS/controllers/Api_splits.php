<?php
class Api_splits extends CI_Controller {
  
  function __construct() {
    parent::__construct();
    
    $this->content_folder_modulo = 10000; // DO NOT CHANGE
    $this->directory = DATA_PATH . '/.cache/tasks/';
    
    $this->load->model('Dataset');
    $this->load->model('Task');
    $this->load->model('Task_inputs');
    $this->load->model('Estimation_procedure');
    $this->load->model('Log');
    $this->load->model('Run');
    
    $this->load->helper('file_upload');
    
    $this->db = $this->load->database('read',true);
    $this->task_types = array(1, 2, 3, 6, 7, 9, 10);
    $this->challenge_types = array(9);
    $this->evaluation = APPPATH . 'third_party/OpenML/Java/evaluate.jar';
    $this->eval_engine_config = " -config 'cache_allowed=false;server=".BASE_URL.";api_key=".API_KEY."' ";
  }
  
  function different_predictions($run_ids) {
    if (is_safe($run_ids) == false) {
      die('run id input not safe. ');
    }
    
    $runs = $this->Run->getWhere('`rid` IN (' . $run_ids . ')');
    if (count($runs) == 0) {
      die('no runs found.');
    }
    
    $task_id = $runs[0]->task_id;
    
    $command = 'java -jar ' . $this->evaluation . ' -f "different_predictions" -t ' . $task_id . ' -r ' . $run_ids . $this->eval_engine_config;
    
    $this->Log->cmd('API Splits::different_predictions(' . $run_ids . ')', $command);
    
    if (function_enabled('system')) {
      header('Content-type: text/plain');
      system(CMD_PREFIX . $command);
    } else {
      die('failed to generate arff file: php "system" function disabled. ');
    }
  }
  
  function all_wrong($run_ids) {
    if (is_safe($run_ids) == false) {
      die('run id input not safe. ');
    }
    
    $runs = $this->Run->getWhere('`rid` IN (' . $run_ids . ')');
    if (count($runs) == 0) {
      die('no runs found.');
    }
    
    $task_id = $runs[0]->task_id;
    
    $command = 'java -jar ' . $this->evaluation . ' -f "all_wrong" -t ' . $task_id . ' -r ' . $run_ids . $this->eval_engine_config;
    
    $this->Log->cmd('API Splits::all_wrong(' . $run_ids . ')', $command);
    
    if (function_enabled('system')) {
      header('Content-type: text/plain');
      system(CMD_PREFIX . $command);
    } else {
      die('failed to generate arff file: php "system" function disabled. ');
    }
  }
  
  function challenge($task_id, $testtrain, $offset_arg, $size_arg) {
    if (is_numeric($task_id) == false) {
      die('argument 1 should be numeric');
    }
    if (in_array($testtrain, array('test', 'train')) == false) {
      die('argument 2 should be in {test,train}');
    }
    $offset = "";
    $size = "";
    if (is_numeric($offset_arg)) {
      $offset = ' -o ' . $offset_arg . ' ';
      
      if (is_numeric($size_arg)) {
        $size = ' -size ' . $size_arg . ' ';
      }
    }
    
    $task = $this->Task->getById($task_id);
    if ($task === false || in_array($task->ttid, $this->challenge_types) === false) {
      die('Task not valid challenge.');
    }
    
    $command = 'java -jar ' . $this->evaluation . ' -f "challenge" -t ' . $task_id . ' -mode "' . $testtrain . '" ' . $offset . $size . $this->eval_engine_config;
    
    $this->Log->cmd('API Splits::challenge(' . $task_id . ', ' . $testtrain . ')', $command);
    
    if (function_enabled('system')) {
      header('Content-type: text/plain');
      system(CMD_PREFIX . $command);
    } else {
      die('failed to generate arff file: php "system" function disabled. ');
    }
  }
  
  function merge_datasets($task_id) {
    $dir_idx = floor($task_id / $this->content_folder_modulo) * $this->content_folder_modulo;
    $directory = $this->directory . '/' . $dir_idx . '/' . $task_id;
    
    $filepath = $directory . '/merged_dataset.arff';
    if (file_exists($filepath) == false) {
      $this->generate("merge_datasets", $task_id, $filepath);
    }
    
    header('Content-type: text/plain');
    header('Content-Length: ' . filesize($filepath));
    readfile_chunked($filepath);
  }
  
  function get($task_id) {
    $dir_idx = floor($task_id / $this->content_folder_modulo) * $this->content_folder_modulo;
    $directory = $this->directory . '/' . $dir_idx . '/' . $task_id;
    
    $filepath = $directory . '/splits.arff';
    if (file_exists($filepath) == false) {
      $this->generate("generate_folds", $task_id, $filepath);
    }
    
    header('Content-type: text/plain');
    header('Content-Length: ' . filesize($filepath));
    readfile_chunked($filepath);
  }
  
  private function generate($function, $task_id, $filepath) {
    $task = $this->Task->getById($task_id);
    // JvR: in hindsight, this check belongs in the eval engine, and not here.
    if ($task === false || in_array($task->ttid, $this->task_types) === false) {
      http_response_code($this->config->item('general_http_error_code'));
      die('Task not providing datasplits.');
    }
    $values = $this->Task_inputs->getTaskValuesAssoc($task_id);
    
    // TODO: very important. sanity check input
    $testset_str = array_key_exists('custom_testset', $values) && is_cs_natural_numbers($values['custom_testset']) ? '-test "' . $values['custom_testset'] . '"' : '';
    
    $command = 'java -jar ' . $this->evaluation . ' -f "' . $function . '" -id ' . $task_id . ' ' . $this->eval_engine_config;
    
    if (array_key_exists('custom_testset', $values)) {
      $command .= '-test "' . $values['custom_testset'] . '" ';
    }
    
    if (!file_exists(dirname($filepath))) {
      mkdir(dirname($filepath), 0755, true);
    }
    
    $command .= ' -o ' . $filepath;
    
    if (function_enabled('exec')) {
      header('Content-type: text/plain');
      $result_status = 0;
      // note that result does not need to be displayed, this 
      // is just a generate function
      $result = array();
      exec(CMD_PREFIX . $command, $result, $return_status);
      
      if ($return_status != 0 && defined('EMAIL_API_LOG')) {
        $to      = EMAIL_API_LOG;
        $subject = 'OpenML API [' . $function . '] Exception: ' . $result_status;
        $content = 'Time: ' . now() . "\nTask_id:" . $task_id . "\nOutput: " . implode("\n", $result);
        sendEmail($to, $subject, $content, 'text');
        http_response_code($this->config->item('general_http_error_code'));
        die('failed to perform action ' . $function . '. Evaluation Engine result send to EMAIL_API_LOG account.');
      }
      
      if ($return_status != 0) {
        http_response_code($this->config->item('general_http_error_code'));
        die('failed to perform action ' . $function . '. Evaluation Engine result omitted (no EMAIL_API_LOG defined). ');
      }
    } else {
      http_response_code($this->config->item('general_http_error_code'));
      die('failed to perform action ' . $function . ': php "exec" function disabled. ');
    }
  }
}
?>
