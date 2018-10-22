<?php
class Evaluation_fold extends Evaluation {
	
  function __construct() {
    parent::__construct();
    $this->table = 'evaluation_fold';
    $this->id_column = array('did','function_id','label','repeat','fold');
  }
}
?>
