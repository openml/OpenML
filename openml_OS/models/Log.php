<?php
class Log extends CI_Model {

  private $handle;
  private $dir;

  function __construct() {
    parent::__construct();
    $this->dir = DATA_PATH . 'log/';
    
    mkdir($this->dir, $this->config->item('content_directories_mode'), true);
  }
  
  function cronjob($level, $function, $message) {
    $this->handle = fopen($this->dir . 'cron.log', 'a');
    if($this->handle) {
      fwrite($this->handle, '[' . now() . '] [' . $level . '] ' . $function . ': ' . $message . "\n");
      fclose($this->handle);
    } else {
      $this->email_log();
    }
  }
  
  function api_error($level, $user, $code, $function, $message) {
    $this->handle = fopen($this->dir . 'api_errors.log', 'a');
    if($this->handle) {
      fwrite($this->handle, '[' . now() . '] ['.$level.'] [' . $user . '] Errorcode: ' . $code . '. Function: ' . $function . '. Response: ' . $message . "\n");
      fclose($this->handle);
    } else {
      $this->email_log();
    }
  }
  
  function sql($query, $type='server') {
    $this->handle = fopen($this->dir . 'sql.log', 'a');
    if( $this->handle ) {
      fwrite($this->handle, '[' . now() . '] [' . $type . '] ' . str_replace( "\n", '', $query ) . "\n");
      fclose($this->handle);
    } else {
      $this->email_log();
    }
  }

  function cmd($source, $cmd) {
    $this->handle = fopen($this->dir . 'cmd.log', 'a');
    if($this->handle) {
      fwrite( $this->handle, '[' . now() . '] [' . $source . '] ' . $cmd . "\n");
      fclose($this->handle);
    } else {
      $this->email_log();
    }
  }
  
  function mapping($file, $line, $message) {
    $this->handle = fopen($this->dir . 'mapping.log', 'a');
    if($this->handle) {
      fwrite($this->handle, '[' . now() . '] [' . $file . ': ' . $line . '] Inconsistent mapping: ' . $message . "\n");
      fclose($this->handle);
    } else {
      $this->email_log();
    }
  }
  
  // $function = the api function in which profiling occurs, 
  // for n actions, we have count($actions) = n and count($timestamps) = n + 1 
  function profiling($function, $timestamps, $actions) {
    $this->handle = fopen($this->dir . 'profiling.log', 'a');
    if($this->handle) {
      fwrite($this->handle, '[' . now() . '] [' . $function . '] '."\n");
      for($i = 0; $i < count($actions); $i+=1) {
        fwrite($this->handle, '-- ' . $actions[$i] . ': ' . ($timestamps[$i+1] - $timestamps[$i]) . " sec\n");
      }
      fclose($this->handle);
    } else {
      $this->email_log();
    }
  }
  
  // send an email when logging seemed to fail
  private function email_log( ) {
    // TODO! 
  }
}
?>
