<?php
class Api_setup extends MY_Api_Model {

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

    /**
     *@OA\Get(
     *	path="/setup/{id}",
     *	tags={"setup"},
     *	summary="Get a hyperparameter setup",
     *	description="Returns information about a setup. The information includes the list of hyperparameters, with name, value, and default value.",
     *	@OA\Parameter(
     *		name="id",
     *		in="path",
     *		type="number",
     *		format="integer",
     *		description="ID of the hyperparameter setup (configuration). These IDs are stated in run descriptions.",
     *		required="true",
     *	),
     *	@OA\Parameter(
     *		name="api_key",
     *		in="query",
     *		type="string",
     *		description="API key to authenticate the user",
     *		required="false",
     *	),
     *	@OA\Response(
     *		response=200,
     *		description="A setup description",
     *		@OA\JsonContent(
     *			ref="#/components/schemas/Setup",
     *			example={
     *			  "setup_parameters":{
     *			    "flow_id":"59",
     *			    "parameter":[
     *			      {
     *			        "full_name":"weka.JRip(1)_F",
     *			        "parameter_name":"F",
     *			        "data_type":"option",
     *			        "default_value":"3",
     *			        "value":"3"
     *			      },{
     *			        "full_name":"weka.JRip(1)_N",
     *			        "parameter_name":"N",
     *			        "data_type":"option",
     *			        "default_value":"2.0",
     *			        "value":"2.0"
     *			      },{
     *			        "full_name":"weka.JRip(1)_O",
     *			        "parameter_name":"O",
     *			        "data_type":"option",
     *			        "default_value":"2",
     *			        "value":"2"
     *			      },{
     *			        "full_name":"weka.JRip(1)_S",
     *			        "parameter_name":"S",
     *			        "data_type":"option",
     *			        "default_value":"1",
     *			        "value":"1"
     *			      }]
     *			  }
     *			}
     *		),
     *	),
     *	@OA\Response(
     *		response=412,
     *		description="Precondition failed. An error code and message are returned.\n280 - Please provide setup ID. In order to view setup details, please provide the run ID\n281 - Setup not found. The setup ID was invalid, or setup does not exist (anymore).\n",
     *		@OA\JsonContent(
     *			ref="#/components/schemas/Error",
     *		),
     *	),
     *)
     */
    if (count($segments) == 1 && is_numeric($segments[0]) && in_array($request_type, $getpost)) {
      $this->setup($segments[0]);
      return;
    }

    /**
     *@OA\Get(
     *	path="/setup/list/{filters}",
     *	tags={"setup"},
     *	summary="List and filter setups",
     *	description="List setups, filtered by a range of properties. Any number of properties can be combined by listing them one after the other in the form '/setup/list/{filter}/{value}/{filter}/{value}/...' Returns an array with all evaluations that match the constraints. A maximum of 1,000 results are returned at a time, an error is returned if the result set is bigger. Use pagination (via limit and offset filters), or limit the results to certain flows, setups, or tags.",
     *	@OA\Parameter(
     *		name="filters",
     *		in="path",
     *		type="string",
     *		description="Any combination of these filters
    /tag/{tag} - returns only setups tagged with the given tag.
    /flow/{ids} - return only setups for specific flows, specified as a comma-separated list of flow IDs, e.g. ''1,2,3''
    /setup/{ids} - return only specific setups, specified as a comma-separated list of setup IDs, e.g. ''1,2,3''
    /limit/{limit}/offset/{offset} - returns only {limit} results starting from result number {offset}. Useful for paginating results. With /limit/5/offset/10, results 11..15 will be returned. Both limit and offset need to be specified.
    ",
     *		required="true",
     *	),
     *	@OA\Parameter(
     *		name="api_key",
     *		in="query",
     *		type="string",
     *		description="API key to authenticate the user",
     *		required="false",
     *	),
     *	@OA\Response(
     *		response=200,
     *		description="A list of setup descriptions",
     *		@OA\JsonContent(
     *			ref="#/components/schemas/SetupList",
     *			example={
     *			  "setups": {
     *			    "setup": [
     *			      {
     *			        "setup_id":"10",
     *			        "flow_id":"65",
     *			        "parameter": [
     *			          {
     *			            "id":"4144",
     *			            "flow_id":"65",
     *			            "flow_name":"weka.RandomForest",
     *			            "full_name":"weka.RandomForest(1)_I",
     *			            "parameter_name":"I",
     *			            "data_type":"option",
     *			            "default_value":"10",
     *			            "value":"10"
     *			          },
     *			          {
     *			            "id":"4145",
     *			            "flow_id":"65",
     *			            "flow_name":"weka.RandomForest",
     *			            "full_name":"weka.RandomForest(1)_K",
     *			            "parameter_name":"K",
     *			            "data_type":"option",
     *			            "default_value":"0",
     *			            "value":"0"
     *			          }
     *			        ]
     *			      }
     *			    ]
     *			  }
     *			}
     *		),
     *	),
     *	@OA\Response(
     *		response=412,
     *		description="Precondition failed. An error code and message are returned.\n670 - Please specify at least one filter.\n671 - Illegal filter.\n672 - Illegal filter input.\n673 - Result set too big. Please use one of the filters or the limit option.\n674 - No results, please check the filter.\n675 - Cannot specify offset without limit.\n676 - Requested result limit too high.\n",
     *		@OA\JsonContent(
     *			ref="#/components/schemas/Error",
     *		),
     *	),
     *)
     */
    if (count($segments) >= 1 && $segments[0] == 'list') {
      array_shift($segments);
      $this->setup_list($segments);
      return;
    }

    /**
     *@OA\Delete(
     *	path="/setup/{id}",
     *	tags={"setup"},
     *	summary="Delete setup",
     *	description="Deletes a setup. Upon success, it returns the ID of the deleted setup.",
     *	@OA\Parameter(
     *		name="id",
     *		in="path",
     *		type="number",
     *		format="integer",
     *		description="Id of the setup.",
     *		required="true",
     *	),
     *	@OA\Parameter(
     *		name="api_key",
     *		in="query",
     *		type="string",
     *		description="Api key to authenticate the user",
     *		required="true",
     *	),
     *	@OA\Response(
     *		response=200,
     *		description="ID of the deleted setup",
     *		@OA\JsonContent(
     *			type="object",
     *			@OA\Property(
     *				property="study_delete",
     *				ref="#/components/schemas/inline_response_200_14_study_delete",
     *			),
     *			example={
     *			  "setup_delete": {
     *			    "id": "1"
     *			  }
     *			}
     *		),
     *	),
     *	@OA\Response(
     *		response=412,
     *		description="Precondition failed. An error code and message are returned.\n401 - Authentication failed. Please provide API key. In order to remove your content, please authenticate.\n402 - Setup does not exists. The setup ID could not be linked to an existing setup.\n404 - Setup deletion failed. Setup is in use by other content (runs, schedules, etc). Can not be deleted.\n405 - Setup deletion failed. Please try again later.\n",
     *		@OA\JsonContent(
     *			ref="#/components/schemas/Error",
     *		),
     *	),
     *)
     */
    if (count($segments) == 1 && is_numeric($segments[0]) && $request_type == 'delete') {
      $this->setup_delete($segments[0]);
      return;
    }

    /**
     *@OA\Post(
     *	path="/setup/tag",
     *	tags={"setup"},
     *	summary="Tag a setup",
     *	description="Tags a setup.",
     *	@OA\Parameter(
     *		name="setup_id",
     *		in="formData",
     *		type="number",
     *		format="integer",
     *		description="Id of the setup.",
     *		required="true",
     *	),
     *	@OA\Parameter(
     *		name="tag",
     *		in="formData",
     *		type="string",
     *		description="Tag name",
     *		required="true",
     *	),
     *	@OA\Parameter(
     *		name="api_key",
     *		in="formData",
     *		type="string",
     *		description="Api key to authenticate the user",
     *		required="true",
     *	),
     *	@OA\Response(
     *		response=200,
     *		description="The id of the tagged setup",
     *		@OA\JsonContent(
     *			type="object",
     *			@OA\Property(
     *				property="flow_tag",
     *				ref="#/components/schemas/inline_response_200_15_flow_tag",
     *			),
     *			example={
     *			  "setup_tag": {
     *			    "id": "2"
     *			  }
     *			}
     *		),
     *	),
     *	@OA\Response(
     *		response=412,
     *		description="Precondition failed. An error code and message are returned.\n470 - In order to add a tag, please upload the entity id (either data_id, flow_id, run_id) and tag (the name of the tag).\n471 - Entity not found. The provided entity_id {data_id, flow_id, run_id} does not correspond to an existing entity.\n472 - Entity already tagged by this tag. The entity {dataset, flow, run} already had this tag.\n473 - Something went wrong inserting the tag. Please contact OpenML Team.\n474 - Internal error tagging the entity. Please contact OpenML Team.\n",
     *		@OA\JsonContent(
     *			ref="#/components/schemas/Error",
     *		),
     *	),
     *)
     */
    if (count($segments) == 1 && $segments[0] == 'tag' && $request_type == 'post') {
      $this->entity_tag_untag('algorithm_setup', $this->input->post('setup_id'), $this->input->post('tag'), false, 'setup');
      return;
    }

    /**
     *@OA\Post(
     *	path="/setup/untag",
     *	tags={"setup"},
     *	summary="Untag a setup",
     *	description="Untags a setup.",
     *	@OA\Parameter(
     *		name="setup_id",
     *		in="formData",
     *		type="number",
     *		description="Id of the setup.",
     *		required="true",
     *	),
     *	@OA\Parameter(
     *		name="tag",
     *		in="formData",
     *		type="string",
     *		description="Tag name",
     *		required="true",
     *	),
     *	@OA\Parameter(
     *		name="api_key",
     *		in="formData",
     *		type="string",
     *		description="Api key to authenticate the user",
     *		required="true",
     *	),
     *	@OA\Response(
     *		response=200,
     *		description="The id of the untagged setup",
     *		@OA\JsonContent(
     *			type="object",
     *			@OA\Property(
     *				property="flow_untag",
     *				ref="#/components/schemas/inline_response_200_16_flow_untag",
     *			),
     *			example={
     *			  "setup_untag": {
     *			    "id": "2"
     *			  }
     *			}
     *		),
     *	),
     *	@OA\Response(
     *		response=412,
     *		description="Precondition failed. An error code and message are returned.\n475 - Please give entity_id {data_id, flow_id, run_id} and tag. In order to remove a tag, please upload the entity id (either data_id, flow_id, run_id) and tag (the name of the tag).\n476 - Entity {dataset, flow, run} not found. The provided entity_id {data_id, flow_id, run_id} does not correspond to an existing entity.\n477 - Tag not found. The provided tag is not associated with the entity {dataset, flow, run}.\n478 - Tag is not owned by you. The entity {dataset, flow, run} was tagged\nby another user. Hence you cannot delete it.\n479 - Internal error removing the tag. Please contact OpenML Team.\n",
     *		@OA\JsonContent(
     *			ref="#/components/schemas/Error",
     *		),
     *	),
     *)
     */
    if (count($segments) == 1 && $segments[0] == 'untag' && $request_type == 'post') {
      $this->entity_tag_untag('algorithm_setup', $this->input->post('setup_id'), $this->input->post('tag'), true, 'setup');
      return;
    }
    
    if (count($segments) == 2 && $segments[0] == 'tag' && $segments[1] == 'list') {
      $this->list_tags('algorithm_setup', 'setup');
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'exists' && $request_type == 'post') {
      $this->setup_exists(false);
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'partial' && $request_type == 'post') {
      $this->setup_exists(true); // re-uses setup exists .. but has different output (please synchronize)
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
      $this->db->select('`input_setting`.*, `input`.*, `implementation`.`name` AS `flow_name`, `implementation`.`fullName` AS `flow_fullName`')->from('input_setting');
      $this->db->join('input', 'input_setting.input_id = input.id', 'inner');
      $this->db->join('implementation', 'input.implementation_id = implementation.id', 'inner');
      $this->db->where('setup = "'.$setup->sid.'"');
      $query = $this->db->get();
      $this->parameters = $query->result();

      $this->xmlContents('setup-parameters', $this->version, array('parameters' => $this->parameters, 'setup' => $setup));
    }
  }
  
  private function _setup_ids_to_parameter_values($setups) {
    // query fails for classifiers without parameters. is fixed further on.
    $this->db->select('input.*, input_setting.*, `implementation`.`name` AS `flow_name`, `implementation`.`fullName` AS `flow_fullName`')->from('input_setting');
    $this->db->join('input', 'input_setting.input_id = input.id', 'inner');
    $this->db->join('implementation', 'input.implementation_id = implementation.id', 'inner');
    // note that algorithm setup can not be linked to implementation id, otherwise we will only get parameters of the root classifier
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
    
    return $per_setup;
  }
  
  function setup_list($segs) { 
    $result_limit = 1000;
    $legal_filters = array('flow', 'setup', 'limit', 'offset', 'tag');
    list($query_string, $illegal_filters) = $this->parse_filters($segs, $legal_filters);
    if (count($illegal_filters) > 0) {
      $this->returnError(671, $this->version, $this->openmlGeneralErrorCode, 'Legal filter operators: ' . implode(',', $legal_filters) .'. Found illegal filter(s): ' . implode(', ', $illegal_filters));
      return;
    }
    
    $illegal_filter_inputs = $this->check_filter_inputs($query_string, $legal_filters, array('tag'));
    if (count($illegal_filter_inputs)) {
      $this->returnError(672, $this->version, $this->openmlGeneralErrorCode, 'Filters with illegal values: ' . implode(',', $illegal_filter_inputs));
      return;
    }
    
    if (count($segs) == 0) {
      $this->returnError(670, $this->version);
      return;
    }
    
    $flows = element('flow',$query_string, null);
    $tag = element('tag',$query_string, null);
    $limit = element('limit',$query_string, null);
    $offset = element('offset',$query_string, null);
    $setups = element('setup',$query_string, null); 
    if ($offset && !$limit) {
      $this->returnError(675, $this->version);
      return;
    }
    if ($limit && $limit > $result_limit) {
      $this->returnError(676, $this->version);
      return;
    }
    
    // JvR: Two queries, because I really don't know how to do it otherwise. 
    // TODO: improve code to remove 2 queries!
    
    // filters (unfortunatelly, they have to be at two places)
    $where = array();
    if ($flows !== null) {
      $where[] = 'algorithm_setup.implementation_id IN (' . $flows . ')';
    }
    if ($tag !== null) {
      $where[] = 'tag = "' . $tag . '"';
    }
    if ($setups !== null) {
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
    
    if (count($setups) > $result_limit) {
      $this->returnError(673, $this->version, $this->openmlGeneralErrorCode, 'Allowed: ' . $result_limit . ', found:' . count($setups));
      return;
    }
    
    $per_setup = $this->_setup_ids_to_parameter_values($setups);
    
    $this->xmlContents('setup-list', $this->version, array('setups' => $per_setup, 'setup_flows' => $setup_flows));
  }

  private function setup_exists($partial) {
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
    foreach($parameter_objects as $p) {
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
    
    try {
      $setups = $this->Algorithm_setup->searchSetup($implementation, $parameters, $partial); 
    } catch(Exception $e) {
      $additional_message = null;
      if (substr($e->getMessage(), 0, 5) == '1116:') {
        $additional_message = 'Flow might not be suitable for this operation (feature request)';
      }
      $this->returnError(588, $this->version, $this->openmlGeneralErrorCode, $additional_message);
      return;
    }
    
    
    // ===== THIS FUNCTION CONTAINS BOTH SETUP EXISTS AND SETUP PARTIAL =====
    // TODO: merge them in a later stage 
    if (!$partial) {
      
      // ===== SETUP EXISTS =====
    
      $result = array('exists' => 'false', 'id' => -1);
      if ($setups != false) {
        $result = array('exists' => 'true', 'id' => $setups[0]->sid);
      }
      
      $this->xmlContents('setup-exists', $this->version, $result);
    } else {
      
      // ===== SETUP PARTIAL =====
      
      if ($setups == false) {
        $this->returnError(587, $this->version);
        return;
      }
      
      $setup_flows = array();
      foreach ($setups as $value) {
        $setup_flows[$value->sid] = $value->implementation_id;
      }
      
      // TODO: two-stage query, not ideal please fix! 
      $per_setup = $this->_setup_ids_to_parameter_values(array_keys($setup_flows));
      
      $this->xmlContents('setup-list', $this->version, array('setups' => $per_setup, 'setup_flows' => $setup_flows));
    }
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
