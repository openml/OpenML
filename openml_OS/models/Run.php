<?php
class Run extends MY_Database_Write_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'run';
    $this->id_column = 'rid';
    $this->user_column = 'uploader';
    
    $this->load->model('Algorithm');
    $this->load->model('Task');
    $this->load->model('Dataset');
    $this->load->model('Evaluation');
    $this->load->model('Evaluation_fold');
    $this->load->model('Evaluation_sample');
    $this->load->model('Implementation');
    $this->load->model('Math_function');
    $this->load->model('Runfile');
  }
  
  function getUploaderOf($rid){
      $sql = 'SELECT '.$this->user_column.' as uploader FROM '.$this->table.' WHERE '.$this->id_column.'='.$rid;
      
      return $this->Run->query($sql);
  }

  function getRunsByFlowId($fid, $from=null, $to=null, $limit=null){
      $sql = 'SELECT r.'.$this->id_column.' as id FROM `'.$this->table.'` r, `algorithm_setup` WHERE algorithm_setup.implementation_id='.$fid.' AND r.setup=algorithm_setup.sid';
      
      if ($from != null) {
        $sql .= ' AND r.processed>="'.$from.'"';
      }
      if ($to != null) {
        $sql .= ' AND r.processed<"'.$to.'"';
      }
      if ($limit != null) {
        $sql .= ' LIMIT ' . $limit;
      }
      
      return $this->Run->query($sql);
  }
  
  function getRunsOfUser($u_id, $from=null, $to=null){
      $sql = 'SELECT '.$this->id_column.' as id FROM '.$this->table.' WHERE '.$this->user_column.'='.$u_id;
      
      if($from!=null){
        $sql .= ' AND processed>="'.$from.'"';
      }
      if($to!=null){
        $sql .= ' AND processed<"'.$to.'"';
      }
      return $this->Run->query($sql);
  }
  
  function inputData( $run, $data, $table ) {
    if( !is_numeric($run) || !is_numeric($data) ) return false;
    $sql = 'INSERT INTO `input_data`(`run`,`data`,`name`) VALUES("'.$run.'","'.$data.'","'.$table.'"); ';
    return $this->db->query( $sql );
  }
  
  function outputData( $run, $data, $table, $field = NULL ) {
    if( !is_numeric($run) || !is_numeric($data) ) return false;
    $field = ( $field != NULL ) ? $field = '"' . $field . '"' : 'NULL';
    $sql = 'INSERT INTO `output_data`(`run`,`data`,`name`,`field`) VALUES("'.$run.'","'.$data.'","'.$table.'",'.$field.'); ';
    return $this->db->query( $sql );
  }
  
  function getInputData( $runId ) {
    if( !is_numeric($runId) ) return false;
    $sql = 'SELECT dataset.* FROM input_data, dataset WHERE input_data.data = dataset.did AND input_data.run = ' . $runId;
    $result = $this->db->query( $sql )->result();
    if(count($result)) 
      return $result;
    else
      return false;
  }
  
  function getOutputData( $runId ) {
    if( !is_numeric($runId) ) return false;
    $datasets = $this->Dataset->getWhere(array( 'source' => $runId ));
    $runfiles = $this->Runfile->getWhere(array( 'source' => $runId ));
    $evaluations = $this->Evaluation->getEvaluations($runId);
    $evaluations_fold = $this->Evaluation_fold->getEvaluations($runId);
    
    $result = array();
    if(is_array($datasets)) $result['dataset'] = $datasets;
    if(is_array($evaluations)) $result['evaluations'] = $evaluations;
    if(is_array($runfiles)) $result['runfile'] = $runfiles;
    if(is_array($evaluations_fold)) $result['evaluations_fold'] = $evaluations_fold;
    
    if(count($result)) 
      return $result;
    else
      return false;
  }
  
  function process( $run_id, &$errorCode, &$errorMessage ) {
    $run = $this->getById( $run_id );
    $task = $this->Task->getById( $run->task_id );
    
    $success = false;
    if( in_array( $task->ttid, array( 1, 2, 3, 4 ) ) ) {
      $success = $this->insertSupervisedClassificationRun( $run, $errorCode, $errorMessage ); 
      
      if( $success ) {
        $update = array( 'processed' => now(), 'error' => NULL );
      } else {
        $update = array( 'processed' => now(), 'error' => $errorMessage );
      }
      $this->Run->update( $run_id, $update );
    }
    
    return $success;
  }
}
?>
