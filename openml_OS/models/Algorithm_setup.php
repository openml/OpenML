<?php
class Algorithm_setup extends MY_Database_Write_Model {

  function __construct() {
    parent::__construct();
    $this->table = 'algorithm_setup';
    $this->id_column = 'sid';
  }

  function getAssociativeArrayJoinedTag($key, $value, $where, $group_by = null, $orderby = null, $limit = null, $offset = null) {
    // use where parameter to specify tag
    $this->db->join('setup_tag', 'algorithm_setup.sid = setup_tag.id', 'left');
    return $this->getAssociativeArray($key, $value, $where, $group_by, $orderby, $limit, $offset);
  }

  function setup_runs($task_tag = null, $flow_tag = null) {
    // select sid, implementation_id, count(*) as num_runs from algorithm_setup s LEFT JOIN run r ON s.sid = r.setup GROUP BY sid ORDER BY num_runs ASC
    $tag_columns = '';
    $tag_where = '1=1';

    if ($task_tag != null) {
      $tag_columns .= 'task_tag tt, ';
      $tag_where .= ' AND run.task_id = tt.id AND tt.tag = "' . $task_tag . '"';
    }
    if ($flow_tag != null) {
      $tag_columns .= 'implementation_tag ft, ';
      $tag_where .= ' AND '.$this->table.'.implementation_id = ft.id AND ft.tag = "' . $task_tag . '"';
    }
    $query = $this->db->select('sid, implementation_id, count(*) as num_runs')->from($tag_columns . $this->table)->join('run', 'run.setup = algorithm_setup.sid', 'left')->where($tag_where)->group_by('sid')->order_by('num_runs ASC');
    $data = $this->db->get();
    return ($data && $data->num_rows() > 0) ? $data->result() : false;
  }
  
  // searches for a setup id and potentially creates one if not exists
  function getSetupId($implementation, $parameters, $create, $setup_string = null) {
    $result = $this->searchSetup($implementation, $parameters, false);
  
    if($result !== false) {
      return $result[0]->sid;
    } elseif($create === false) {
      return false;
    } else { // does not exist, but will create
      return $this->createSetup($implementation, $parameters, $setup_string);
    }
  }
  
  //inputs: 
  // - $implementation: complete implementation record
  // - $parameters: mapping parameter_id->value
  // - $partial: if false, the number of given parameters in $parameters should
  //             match exactly the number of parameters in the setup. 
  //             if true, it can be a subset (and multiple results can be found)
  function searchSetup($implementation, $parameters, $partial = false) {
    $select = '';
    $leftJoin = '';
    $where = '';
    
    foreach($parameters as $key => $value) {
      // key = the input_id, value = the value
      $select .= ', `i'.$key.'`.`value` AS `i'.$key.'`';
      $leftJoin .= 'LEFT JOIN `input_setting` `i'.$key.'` ON `i'.$key.'`.`setup` = `s`.`sid` AND `i'.$key.'`.`input_id` = "'.$key.'" ';
      $where .= ' AND `i'.$key.'`.`value` = '.$this->db->escape($value).' ';
    }
    
    $sql = 'SELECT `sid`,`implementation_id`' . $select .
           'FROM `algorithm_setup` AS `s` ' .
           $leftJoin .
           ' WHERE `implementation_id` = "' . $implementation->id . '" ' . 
           $where;
    // IMPORTANT: If partial is false we need to have same number of parameters
    if ($partial == false) {
      $sql .= ' AND (SELECT COUNT(*) FROM `input_setting` WHERE `setup` = `s`.`sid`) = ' . count($parameters) .
              ' LIMIT 0,1;';
    }

    $sql = $this->db->query($sql);
    if ($sql === false) {
      throw new Exception($this->db->_error_number() . ': ' . $this->db->_error_message());
    }
    
    $result = $sql->result();
    if(count($result) > 0) {
      return $result;
    } else {
      return false;
    }
  }
  
  //inputs: 
  // - $implementation: complete implementation record
  // - $parameters: mapping parameter_id->value
  // - $setup_string: obsolute
  function createSetup($implementation, $parameters, $setup_string) {
    // CREATE THE NEW SETUP
    $components = array_merge(array($implementation->id), $this->Implementation->getComponentIds($implementation->id));
    $legal_parameters = $this->Input->getAssociativeArray('id', 'defaultValue', 'implementation_id IN ("'.implode( '","', $components).'")');

    if (is_array($legal_parameters) === false) {
      // no legal parameters found, make it an array anyway
      $legal_parameters = array();
    }

    foreach ($parameters as $key => $value) {
      if (array_key_exists($key, $legal_parameters) == false) {
        // an illegal parameter was set.
        return false;
      }
    }

    // for checking the default.
    $isDefault = false;
    foreach ($legal_parameters as $key => $value) {
      if ($value == null) { unset($legal_parameters[$key]); }
    }

    if (count(array_diff_assoc($legal_parameters, $parameters)) === 0 &&
       count(array_diff_assoc($parameters, $legal_parameters)) === 0) {
      $isDefault = true;
    }

    $setupData = array(
      'implementation_id' => $implementation->id,
      'setup_string' => $setup_string,
      'isDefault' => $isDefault ? 'true' : 'false',
    );

    $setupId = $this->Algorithm_setup->insert($setupData);
    if (!$setupId) { return false; } // not going to happen :)

    // and register the parameters
    foreach ($parameters as $key => $value) {
      $insert = array('setup' => $setupId, 'input_id' => $key, 'value' => $value);
      $this->Input_setting->insert($insert);
      if (!$insert) {
        return false;
      }
    }
    return $setupId;
  }
}

?>
