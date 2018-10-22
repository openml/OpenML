<?php
class Task_type_inout extends MY_Database_Read_Model {
	
	function __construct() {
    parent::__construct();
    $this->table = 'task_type_inout';
    $this->id_column = array('ttid','name');
  }
	
	// @ deprecated, useless function
	public function getByTaskType($ttid, $orderBy = null) {
		return $this->getWhere('ttid = ' . $ttid, $orderBy);
	}
	
	function prepareRegex($name_value, $task_type_id, $template='[INPUT:INPUT_NAME]') {
	  // $name_value is an assoc array mapping from task_input_name to task_input_value
	  // task_input_name will be wrapped into template
	  $result = array();
	  foreach ($name_value as $name => $value) {
	    $template_name = str_replace('INPUT_NAME', $name, $template);
	    $result[$template_name] = $value;
	  }
	  $result['[TASK:ttid]'] = $task_type_id;
	  return $result; 
	}
	
	// JvR (07/04/18): Please deprecate this function and replace it with 
	// something much better
	public function getParsed($task_id) {
    $task = $this->Task->getById($task_id);
		$templates = $this->getAssociativeArray('name', 'template_api', '`template_api` IS NOT NULL AND `ttid` = ' . $task->ttid, null, '`order` ASC');
    $values = $this->Task_inputs->getTaskValuesAssoc($task_id);
    
    list($variables, $variable_names) = $this->getVariables($templates);
    
    // JvR (07/04/18): This functionality is ridiculous - legacy from Leuven2012
    // find additional tables to fetch variables from
    $tables = array();
    foreach ($variable_names as $var) {
      if (strpos($var, '.') !== false ) {
        $table = substr($var, 0, strpos($var, '.'));
        $tables[$table] = true;
      }
    }
    
    // now iterate over the tables and fetch the variables
    // JvR (07/04/18): See comment above, please drop ASAP
    foreach ($tables as $table => $dummy_var) {
      $sql = 'SELECT * FROM `'.$table.'` WHERE `id` = "'.$values[$table].'";';
      $additional = $this->query($sql);
      
      foreach ($additional[0] as $key => $value) {
        $values[$table.'.'.$key] = $value;
      }
    }
    
    // now create "replace" array:
    $replace = array();
    foreach ($variable_names as $var) {
      if (array_key_exists($var, $values)) {
        $replace[] = htmlspecialchars($values[$var]);
      } else {
        $replace[] = '';
      }
    }
    
    // add additional constants: // TODO: integrate this
    $variables[] = '[TASK:id]';
    $replace[] = $task_id;
    $variables[] = '[CONSTANT:base_url]';
    $replace[] = BASE_URL;
    // TODO: remove entrees of $templates with no content. (e.g., cost_matrix)
		return str_replace($variables, $replace, $templates);
	}
  
  private function getVariables($subjects) {
    // $subjects is a array of strings, with XML content. 
    // we are interested in the patterns that are listed below
    // returns an 
    $lookup_pattern = '/(?:\[LOOKUP\:)(.*)(?:\])/';
    $input_pattern = '/(?:\[INPUT\:)(.*)(?:\])/';
    
    $res = array(array(), array());
    
    foreach ($subjects as $subject) {
      $lookup_count = preg_match_all($lookup_pattern, $subject, $lookup_result);
      $input_count = preg_match_all($input_pattern, $subject, $input_result);
      $res = array(
        array_merge($res[0], $lookup_result[0], $input_result[0]), 
        array_merge($res[1], $lookup_result[1], $input_result[1])
      );
    }
    
    return $res;
  }
}
?>
