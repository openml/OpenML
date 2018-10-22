<?php
class Schedule extends MY_Database_Write_Model { 

  function __construct() {
    parent::__construct();
    $this->table = 'schedule';
    $this->id_column = array('sid', 'task_id');
  }

  function getJob($workbench, $task_type, $task_tag = null, $setup_tag = null, $setup_id = null) {
    $task_tag_clause = $task_tag ? ' AND t.tag = "' . $task_tag . '" ' : '';
    $setup_tag_clause = $setup_tag ? ' AND s.tag = "' . $setup_tag . '" ' : '';
    $setup_id_clause = $setup_id ? ' AND sch.sid = "' . $setup_id . '" ' : '';
	  $sql = 
	    'SELECT `sch`.* FROM `schedule` `sch` '.
	    'LEFT JOIN `task_tag` `t` ON `sch`.`task_id` = `t`.`id` '.
	    'LEFT JOIN `setup_tag` `s` ON `sch`.`sid` = `s`.`id` ' .
	    'WHERE `dependencies` = "' . $workbench . '" AND `active` = "true" '.
	    'AND `ttid` = "' . $task_type . '" ' . $task_tag_clause . $setup_tag_clause . $setup_id_clause . ' '.
	    'ORDER BY last_assigned ASC limit 0,1; ';
    $res = $this->query($sql);

    if(is_array($res) == false) {
      return false;
    } else {
      $current = $res[0];
      $this->update(array($current->sid, $current->task_id), array('last_assigned' => now()));
      return $res[0];
    }
  }
}
?>
