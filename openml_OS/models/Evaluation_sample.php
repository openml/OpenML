<?php
class Evaluation_sample extends Evaluation {
	
  function __construct() {
    parent::__construct();
    $this->table = 'evaluation_sample';
    $this->id_column = array('did','function_id','label');
  }
}
?>
