<?php
class Api_setup extends Api_model {

  protected $version = 'v1';

  function __construct() {
    parent::__construct();

    // load models
    $this->load->model('Algorithm_setup');
    $this->load->model('Implementation');
    $this->load->model('Input');
    $this->load->model('Input_setting');
    $this->load->model('Schedule');
    $this->load->model('Setup_differences');
    $this->load->model('Setup_tag');

    $this->load->model('Database_singleton');
    $this->db = $this->Database_singleton->getReadConnection();
  }

  function bootstrap($format, $segments, $request_type, $user_id) {
    $this->outputFormat = $format;

    $getpost = array('get','post');

    if (count($segments) == 1 && is_numeric($segments[0]) && in_array($request_type, $getpost)) {
      $this->setup($segments[0]);
      return;
    }

    if (count($segments) >= 1 && $segments[0] == 'list') {
      array_shift($segments);
      $this->setup_list($segments);
      return;
    }

    if (count($segments) == 1 && is_numeric($segments[0]) && $request_type == 'delete') {
      $this->setup_delete($segments[0]);
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'tag' && $request_type == 'post') {
      $this->entity_tag_untag('algorithm_setup', $this->input->post('setup_id'), $this->input->post('tag'), false, 'setup');
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'untag' && $request_type == 'post') {
      $this->entity_tag_untag('algorithm_setup', $this->input->post('setup_id'), $this->input->post('tag'), true, 'setup');
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'exists' && $request_type == 'post') {
      $this->setup_exists();
      return;
    }

    if (count($segments) >= 1 && count($segments) <= 2 && $segments[0] == 'count' && in_array($request_type, $getpost)) {
      if (count($segments) == 2) {
        $this->setup_count($segments[1]);
      } else {
        $this->setup_count();
      }
      return;
    }

    if (count($segments) == 3 && $segments[0] == 'differences' &&
        is_numeric($segments[1]) && is_numeric($segments[2]) &&
        $request_type == 'post' && $this->input->post('task_id') != false) { // TODO: fix $this->inpout->post('task_id') requirement
    	$this->setup_differences_upload($segments[1],$segments[2]);
    	return;
    }

    if (count($segments) >= 3 && $segments[0] == 'differences' && is_numeric($segments[1]) && is_numeric($segments[2])) {
    	$task_id = null;
    	if (count($segments) > 3) {
    		$task_id = $segments[3];
    	}
    	$this->setup_differences($segments[1],$segments[2],$task_id);
    	return;
    }

    $this->returnError( 100, $this->version );
  }

  private function setup($setup_id) {
    if($setup_id == false) {
      $this->returnError(280, $this->version);
      return;
    }
    $setup = $this->Algorithm_setup->getById($setup_id);

    if ($setup == false) {
      $this->returnError(281, $this->version);
      return;
    } else {
      $this->db->select('*')->from('input_setting');
      $this->db->join('input', 'input_setting.input_id = input.id', 'inner');
      $this->db->where('setup = "'.$setup->sid.'"');
      $query = $this->db->get();
      $this->parameters = $query->result();

      $this->xmlContents('setup-parameters', $this->version, array('parameters' => $this->parameters, 'setup' => $setup));
    }
  }

  function setup_list($segs) {
    if (count($segs) == 0) {
      $this->returnError(670, $this->version);
      return;
    }

    $legal_filters = array('flow', 'setup', 'limit', 'offset', 'tag');
    $query_string = array();
    for ($i = 0; $i < count($segs); $i += 2) {
      $query_string[$segs[$i]] = urldecode($segs[$i+1]);
      if (in_array($segs[$i], $legal_filters) == false) {
        $this->returnError(671, $this->version, $this->openmlGeneralErrorCode, 'Legal filter operators: ' . implode(',', $legal_filters) .'. Found illegal filter: ' . $segs[$i]);
        return;
      }
    }

    $flows = element('flow',$query_string, null);
    $tag = element('tag',$query_string, null);
    $limit = element('limit',$query_string, null);
    $offset = element('offset',$query_string, null);
    $setups = element('setup',$query_string, null); 
    
    if ($flows !== null) {
      if (strlen($flows) == 0 || !is_cs_natural_numbers($flows)) {
        $this->returnError(672, $this->version, $this->openmlGeneralErrorCode, 'Non-numeric input: flow');
        return;
      }
    }
    
    if ($setups !== null) {
      if (strlen($setups) == 0 || !is_cs_natural_numbers($setups)) {
        $this->returnError(672, $this->version, $this->openmlGeneralErrorCode, 'Non-numeric input: setup');
        return;
      }
    }
    
    if ($limit !== null) {
      if (strlen($limit) == 0 || !is_numeric($limit)) {
        $this->returnError(672, $this->version, $this->openmlGeneralErrorCode, 'Non-numeric input: limit');
        return;
      }
    }
    
    if ($offset !== null) {
      if (strlen($offset) == 0 || !is_numeric($offset)) {
        $this->returnError(672, $this->version, $this->openmlGeneralErrorCode, 'Non-numeric input: offset');
        return;
      }
    }
    
    if ($tag !== null) {
      if (len($tag) == 0 || !is_safe($tag)) {
        $this->returnError(672, $this->version, $this->openmlGeneralErrorCode, 'Illegal input: tag');
        return;
      }
    }
    
    // JvR: Two queries, because I really don't know how to do it otherwise. 
    // TODO: improve code to remove 2 queries!
    
    // filters (unfortunatelly, they have to be at two places)
    $where = array();
    if ($flows) {
      $where[] = 'algorithm_setup.implementation_id IN (' . $flows . ')';
    }
    if ($tag) {
      $where[] = 'tag = "' . $tag . '"';
    }
    if ($setups) {
      $where[] = 'sid IN (' . $setups . ')';
    }
    if (count($where)) {
      $where = implode(' AND ', $where);
    } else {
      $where = null;
    }
    $setup_flows = $this->Algorithm_setup->getAssociativeArrayJoinedTag('sid', 'implementation_id', $where, 'sid', null, $limit, $offset);
    if ($setup_flows == false) {
      $this->returnError(674, $this->version);
      return;
    }
    
    $setups = array_keys($setup_flows);
    
    $maxAllowed = 1000;
    if (count($setups) > $maxAllowed) {
      $this->returnError(673, $this->version, $this->openmlGeneralErrorCode, 'Allowed: ' . $maxAllowed . ', found:' . count($setups));
      return;
    }

    // query fails for classifiers without parameters. is fixed further on.
    $this->db->select('input.*, input_setting.*, algorithm_setup.implementation_id AS flow_id')->from('input_setting');
    $this->db->join('input', 'input_setting.input_id = input.id', 'inner');
    $this->db->join('algorithm_setup', 'algorithm_setup.sid = input_setting.setup', 'inner');
    $this->db->join('setup_tag', 'input_setting.setup = setup_tag.id', 'left');
    $this->db->where_in('algorithm_setup.sid', $setups);

    $query = $this->db->get();
    $parameters = $query->result();

    $per_setup = array();
    // initialize the array
    foreach ($setups as $setup) {
      $per_setup[$setup] = array();
    }
    // now fill with parameters
    foreach ($parameters as $parameter) {
      $per_setup[$parameter->setup][] = $parameter;
    }
    $this->xmlContents('setup-list', $this->version, array('setups' => $per_setup, 'setup_flows' => $setup_flows));
  }

  function setup_count($tags = null) {
    $result = $this->Algorithm_setup->setup_runs($tags, $tags);

    if ($result == false) {
      $this->returnError(661, $this->version);
      return;
    }

    $this->xmlContents('setup-count', $this->version, array('setups' => $result));
  }

  private function setup_exists() {
    $description = isset($_FILES['description']) ? $_FILES['description'] : false;
    $uploadError = '';
    if(!check_uploaded_file($description,false,$uploadError)) {
      $this->returnError(581, $this->version,$this->openmlGeneralErrorCode,$uploadError);
      return;
    }

    // validate xml
    $xmlErrors = '';
    if(validateXml($description['tmp_name'], xsd('openml.run.upload', $this->controller, $this->version), $xmlErrors) == false) {
      $this->returnError(582, $this->version, $this->openmlGeneralErrorCode, $xmlErrors);
      return;
    }


    // fetch xml
    $xml = simplexml_load_file($description['tmp_name']);
    if($xml === false) {
      $this->returnError(583, $this->version);
      return;
    }

    $run_xml = all_tags_from_xml(
      $xml->children('oml', true),
      $this->xml_fields_run);

    $implementation_id = $run_xml['flow_id'];
    $parameter_objects = array_key_exists('parameter_setting', $run_xml) ? $run_xml['parameter_setting'] : array();

    // fetch implementation
    $implementation = $this->Implementation->getById($implementation_id);
    if($implementation === false) {
      $this->returnError(584, $this->version);
      return;
    }
    // makes sure that the implementation is not a math_function
    if(in_array($implementation->{'implements'}, $this->supportedMetrics)) {
      $this->returnError(585, $this->version);
      return;
    }

    $parameters = array();
    foreach( $parameter_objects as $p ) {
      // since 'component' is an optional XML field, we add a default option
      $component = property_exists($p, 'component') ? $p->component : $implementation->id;

      // now find the input id
      $input_id = $this->Input->getWhereSingle('`implementation_id` = ' . $component . ' AND `name` = "' . $p->name . '"');
      if($input_id === false) {
        $this->returnError(586, $this->version, $this->openmlGeneralErrorCode, 'Name: ' . $p->name . ', flow id (component): ' . $component);
        return;
      }

      $parameters[$input_id->id] = $p->value . '';
    }
    // search setup ... // TODO: do something about the new parameters. Are still retrieved by ID, does not work with Weka plugin.
    $setupId = $this->Algorithm_setup->getSetupId($implementation, $parameters, false);

    $result = array('exists' => 'false', 'id' => -1);
    if($setupId) {
      $result = array('exists' => 'true', 'id' => $setupId);
    }
    $this->xmlContents('setup-exists', $this->version, $result);
  }

  private function setup_delete($setup_id) {

    $setup = $this->Algorithm_setup->getById( $setup_id );
    if( $setup == false ) {
      $this->returnError( 402, $this->version );
      return;
    }

    $runs = $this->Run->getWhere( 'setup = "' . $setup->sid . '"' );
    $schedules = $this->Schedule->getWhere( 'sid = "' . $setup->sid . '"' );

    if( $runs || $schedules ) {
      $this->returnError( 404, $this->version );
      return;
    }


    $result = true;
    $result = $result && $this->Input_setting->deleteWhere('setup = ' . $setup->sid );

    if( $result ) {
      $result = $this->Algorithm_setup->delete( $setup->sid );
    }

    if( $result == false ) {
      $this->returnError( 405, $this->version );
      return;
    }

    $this->xmlContents( 'setup-delete', $this->version, array( 'setup' => $setup ) );
  }

  private function setup_differences($setupA, $setupB, $task_id) {
  	$sidA = min($setupA, $setupB);
  	$sidB = max($setupA, $setupB);
  	$taskWhere = '';

  	if ($task_id != null) {
  		$taskWhere = ' AND `task_id` = ' . $task_id;
  	}

  	$meta_array = $this->Setup_differences->getWhere(
  		  '`sidA` = ' . $sidA . ' AND `sidB` = ' . $sidB . $taskWhere);
  	if ($meta_array != false) {
  		$this->xmlContents(
  			'setup-differences', $this->version,
  			array('data' => $meta_array)
  		);
  	} else {
  		$this->returnError(520, $this->version);
  	}
  }

  private function setup_differences_upload($setupA, $setupB) {
  	$task_id = $this->input->post('task_id');
  	$task_size = $this->input->post('task_size');
  	$differences = $this->input->post('differences');

  	if($this->user_has_admin_rights == false) {
      $this->returnError( 104, $this->version );
      return;
    }

  	$data = array(
  		'sidA' => min($setupA,$setupB),
  		'sidB' => max($setupA,$setupB),
  		'task_id' => $task_id,
  		'task_size' => $task_size,
  		'differences' => $differences
  	);

  	$success = $this->Setup_differences->insert($data);

  	// if ($success == false) {
  	//	$this->returnError( 520, $this->version );
  	//	return;
  	//} else {
  		$meta_array = $this->Setup_differences->getWhere(
  		  '`sidA` = ' . $data['sidA'] . ' AND `sidB` = ' . $data['sidB'] . ' AND `task_id` = ' . $data['task_id']);
  		$this->xmlContents( 'setup-differences', $this->version, array( 'data' => $meta_array ) );
  	//}
  }
}
?>
