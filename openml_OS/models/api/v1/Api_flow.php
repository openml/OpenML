<?php

class Api_flow extends MY_Api_Model {

  protected $version = 'v1';

  function __construct() {
    parent::__construct();

    // load models
    $this->load->model('Algorithm_setup');

    $this->load->model('Implementation');
    $this->load->model('Implementation_tag');
    $this->load->model('Implementation_component');
    $this->load->model('Input_setting');


    $this->load->model('File');
    $this->load->model('Input');

    $this->load->model('Database_singleton');
    $this->db = $this->Database_singleton->getReadConnection();
  }

  function bootstrap($format, $segments, $request_type, $user_id) {
    $this->outputFormat = $format;

    $getpost = array('get','post');

    /**
     *@OA\Get(
     *	path="/flow/list/{filters}",
     *	tags={"flow"},
     *	summary="List and filter flows",
     *	description="List flows, possibly filtered by a range of properties. Any number of properties can be combined by listing them one after the other in the form '/task/list/{filter}/{value}/{filter}/{value}/...' Returns an array with all flows that match the constraints.",
     *	@OA\Parameter(
     *		name="filters",
     *		in="path",
     *		type="string",
     *		description="Any combination of these filters
    /limit/{limit}/offset/{offset} - returns only {limit} results starting from result number {offset}. Useful for paginating results. With /limit/5/offset/10, tasks 11..15 will be returned. Both limit and offset need to be specified.
    /tag/{tag} - returns only tasks tagged with the given tag.
    /uploader/{id} - return only evaluations uploaded by a specific user, specified by user ID.
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
     *		description="A list of flows",
     *		@OA\JsonContent(
     *			ref="#/components/schemas/FlowList",
     *			example={
     *			  "flows":
     *			    {
     *			      "flow":[
     *			        {
     *			          "id":"65",
     *			          "full_name":"weka.RandomForest(1)",
     *			          "name":"weka.RandomForest",
     *			          "version":"1",
     *			          "external_version":"Weka_3.7.10_9186",
     *			          "uploader":"1"
     *			        },
     *			        {
     *			          "id":"66",
     *			          "full_name":"weka.IBk(1)",
     *			          "name":"weka.IBk",
     *			          "version":"1",
     *			          "external_version":"Weka_3.7.10_8034",
     *			          "uploader":"1"
     *			        },
     *			        {
     *			          "id":"67",
     *			          "full_name":"weka.BayesNet_K2(1)",
     *			          "name":"weka.BayesNet_K2",
     *			          "version":"1",
     *			          "external_version":"Weka_3.7.10_8034",
     *			          "uploader":"1"
     *			        }
     *			      ]
     *			    }
     *			  }
     *		),
     *	),
     *	@OA\Response(
     *		response=412,
     *		description="Precondition failed. An error code and message are returned.\n500 - No results. There where no matches for the given constraints.\n501 - Illegal filter specified.\n502 - Filter values/ranges not properly specified.\n503 - Can not specify an offset without a limit.\n",
     *		@OA\JsonContent(
     *			ref="#/components/schemas/Error",
     *		),
     *	),
     *)
     */
    if (count($segments) >= 1 && $segments[0] == 'list') {
      array_shift($segments);
      $this->flow_list($segments);
      return;
    }

    // TODO: deprecate!
    /**
     *@OA\Get(
     *	path="/flow/exists/{name}/{version}",
     *	tags={"flow"},
     *	summary="Check whether flow exists",
     *	description="Checks whether a flow with the given name and (external) version exists.",
     *	@OA\Parameter(
     *		name="name",
     *		in="path",
     *		type="string",
     *		description="The name of the flow.",
     *		required="true",
     *	),
     *	@OA\Parameter(
     *		name="version",
     *		in="path",
     *		type="string",
     *		description="The external version of the flow",
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
     *		description="A list of flows",
     *		@OA\JsonContent(
     *			type="object",
     *			@OA\Property(
     *				property="flow_exists",
     *				ref="#/components/schemas/inline_response_200_10_flow_exists",
     *			),
     *			example={
     *			  "flow_exists": {
     *			    "exists": "true",
     *			    "id": "65"
     *			  }
     *			}
     *		),
     *	),
     *	@OA\Response(
     *		response=412,
     *		description="Precondition failed. An error code and message are returned.\n330 - Mandatory fields not present. Please provide name and external_version.\n",
     *		@OA\JsonContent(
     *			ref="#/components/schemas/Error",
     *		),
     *	),
     *)
     */
    if (count($segments) == 3 && $segments[0] == 'exists') {
      $this->flow_exists($segments[1],$segments[2]);
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'exists') {
      $this->flow_exists($this->input->post('name'),$this->input->post('external_version'));
      return;
    }

    /**
     *@OA\Get(
     *	path="/flow/{id}",
     *	tags={"flow"},
     *	summary="Get flow description",
     *	description="Returns information about a flow. The information includes the name, information about the creator, dependencies, parameters, run instructions and more.",
     *	@OA\Parameter(
     *		name="id",
     *		in="path",
     *		type="number",
     *		format="integer",
     *		description="ID of the flow.",
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
     *		description="A flow description",
     *		@OA\JsonContent(
     *			ref="#/components/schemas/Flow",
     *			example={
     *			  "flow": {
     *			    "id":"100",
     *			    "uploader":"1",
     *			    "name":"weka.J48",
     *			    "version":"2",
     *			    "external_version":"Weka_3.7.5_9117",
     *			    "description":"...",
     *			    "upload_date":"2014-04-23 18:00:36",
     *			    "language":"Java",
     *			    "dependencies":"Weka_3.7.5",
     *			    "parameter": [
     *			      {
     *			        "name":"A",
     *			        "data_type":"flag",
     *			        "default_value":[],
     *			        "description":"Laplace smoothing..."
     *			      },
     *			      {
     *			        "name":"C",
     *			        "data_type":"option",
     *			        "default_value":"0.25",
     *			        "description":"Set confidence threshold..."
     *			      }
     *			    ]
     *			  }
     *			}
     *		),
     *	),
     *	@OA\Response(
     *		response=412,
     *		description="Precondition failed. An error code and message are returned.\n180 - Please provide flow id.\n181 - Unknown flow. The flow with the given ID was not found in the database.\n",
     *		@OA\JsonContent(
     *			ref="#/components/schemas/Error",
     *		),
     *	),
     *)
     */
    if (count($segments) == 1 && is_numeric($segments[0]) && in_array($request_type, $getpost)) {
      $this->flow($segments[0]);
      return;
    }

    /**
     *@OA\Delete(
     *	path="/flow/{id}",
     *	tags={"flow"},
     *	summary="Delete a flow",
     *	description="Deletes a flow. Upon success, it returns the ID of the deleted flow.",
     *	@OA\Parameter(
     *		name="id",
     *		in="path",
     *		type="number",
     *		format="integer",
     *		description="Id of the flow.",
     *		required="true",
     *	),
     *	@OA\Parameter(
     *		name="api_key",
     *		in="query",
     *		type="string",
     *		description="API key to authenticate the user",
     *		required="true",
     *	),
     *	@OA\Response(
     *		response=200,
     *		description="ID of the deleted flow",
     *		@OA\JsonContent(
     *			type="object",
     *			@OA\Property(
     *				property="flow_delete",
     *				ref="#/components/schemas/inline_response_200_8_flow_delete",
     *			),
     *			example={
     *			  "flow_delete": {
     *			    "id": "4328"
     *			  }
     *			}
     *		),
     *	),
     *	@OA\Response(
     *		response=412,
     *		description="Precondition failed. An error code and message are returned.\n320 - Please provide API key. In order to remove your content, please authenticate.\n321 - Authentication failed. The API key was not valid. Please try to login again, or contact api administrators.\n322 - Flow does not exists. The flow ID could not be linked to an existing flow.\n323 - Flow is not owned by you. The flow is owned by another user. Hence you cannot delete it.\n324 - Flow is in use by other content. Can not be deleted. The flow is used in runs, evaluations or as a component of another flow. Delete other content before deleting this flow.\n325 - Deleting flow failed. Deleting the flow failed. Please contact\nsupport team.\n",
     *		@OA\JsonContent(
     *			ref="#/components/schemas/Error",
     *		),
     *	),
     *)
     */
    if (count($segments) == 1 && is_numeric($segments[0]) && $request_type == 'delete') {
      $this->flow_delete($segments[0]);
      return;
    }

    if (count($segments) == 2 && is_numeric($segments[0]) && $segments[1] == 'force' && $request_type == 'delete') {
      $this->flow_forcedelete($segments[0]);
      return;
    }

    /**
     *@OA\Post(
     *	path="/flow",
     *	tags={"flow"},
     *	summary="Upload a flow",
     *	description="Uploads a flow. Upon success, it returns the flow id.",
     *	@OA\Parameter(
     *		name="description",
     *		in="formData",
     *		type="file",
     *		description="An XML file describing the flow. Only name and description are required. Also see the [XSD schema](https://www.openml.org/api/v1/xsd/openml.implementation.upload) and an [XML example](https://www.openml.org/api/v1/xml_example/flow).",
     *		required="true",
     *	),
     *	@OA\Parameter(
     *		name="flow",
     *		in="formData",
     *		type="file",
     *		description="The actual flow, being a source (or binary) file.",
     *		required="false",
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
     *		description="Id of the uploaded flow",
     *		@OA\JsonContent(
     *			type="object",
     *			@OA\Property(
     *				property="upload_flow",
     *				ref="#/components/schemas/inline_response_200_9_upload_flow",
     *			),
     *			example={
     *			  "upload_flow": {
     *			    "id": "2520"
     *			  }
     *			}
     *		),
     *	),
     *	@OA\Response(
     *		response=412,
     *		description="Precondition failed. An error code and message are returned.\n160 - Error in file uploading. There was a problem with the file upload.\n161 - Please provide description xml.\n163 - Problem validating uploaded description file. The XML description format does not meet the standards.\n164 - Flow already stored in database. Please change name or version number\n165 - Failed to insert flow. There can be many causes for this error. If you included the implements field, it should be an existing entry in the algorithm or math_function table. Otherwise it could be an internal server error. Please contact API support team.\n166 - Failed to add flow to database. Internal server error, please contact API administrators\n167 - Illegal files uploaded. An non required file was uploaded.\n168 - The provided md5 hash equals not the server generated md5 hash of the file.\n169 - Please provide API key. In order to share content, please authenticate and provide API key.\n170 - Authentication failed. The API key was not valid. Please try to login again, or contact API administrators\n171 - Flow already exists. This flow is already in the database\n172 - XSD not found. Please contact API support team\n",
     *		@OA\JsonContent(
     *			ref="#/components/schemas/Error",
     *		),
     *	),
     *)
     */
    if (count($segments) == 0 && $request_type == 'post') {
      $this->flow_upload();
      return;
    }

    /**
     *@OA\Post(
     *	path="/flow/tag",
     *	tags={"flow"},
     *	summary="Tag a flow",
     *	description="Tags a flow.",
     *	@OA\Parameter(
     *		name="flow_id",
     *		in="formData",
     *		type="number",
     *		format="integer",
     *		description="Id of the flow.",
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
     *		description="The id of the tagged flow",
     *		@OA\JsonContent(
     *			type="object",
     *			@OA\Property(
     *				property="flow_tag",
     *				ref="#/components/schemas/inline_response_200_12_flow_tag",
     *			),
     *			example={
     *			  "flow_tag": {
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
      $this->entity_tag_untag('implementation', $this->input->post('flow_id'), $this->input->post('tag'), false, 'flow');
      return;
    }

    /**
     *@OA\Post(
     *	path="/flow/untag",
     *	tags={"flow"},
     *	summary="Untag a flow",
     *	description="Untags a flow.",
     *	@OA\Parameter(
     *		name="flow_id",
     *		in="formData",
     *		type="number",
     *		description="Id of the flow.",
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
     *		description="The id of the untagged flow",
     *		@OA\JsonContent(
     *			type="object",
     *			@OA\Property(
     *				property="flow_untag",
     *				ref="#/components/schemas/inline_response_200_13_flow_untag",
     *			),
     *			example={
     *			  "flow_untag": {
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
      $this->entity_tag_untag('implementation', $this->input->post('flow_id'), $this->input->post('tag'), true, 'flow');
      return;
    }

    if (count($segments) == 2 && $segments[0] == 'tag' && $segments[1] == 'list') {
      $this->list_tags('implementation', 'flow');
      return;
    }

    $this->returnError( 100, $this->version );
  }

  private function flow_list($segs) {
    $legal_filters = array('uploader', 'tag', 'limit', 'offset');
    
    list($query_string, $illegal_filters) = $this->parse_filters($segs, $legal_filters);
    if (count($illegal_filters) > 0) {
      $this->returnError(501, $this->version, $this->openmlGeneralErrorCode, 'Legal filter operators: ' . implode(',', $legal_filters) .'. Found illegal filter(s): ' . implode(', ', $illegal_filters));
      return;
    }
    
    $illegal_filter_inputs = $this->check_filter_inputs($query_string, $legal_filters, array('tag'));
    if (count($illegal_filter_inputs) > 0) {
      $this->returnError(502, $this->version, $this->openmlGeneralErrorCode, 'Filters with illegal values: ' . implode(',', $illegal_filter_inputs));
      return;
    }

    $uploader_id = element('uploader', $query_string, null);
    $tag = element('tag', $query_string, null);
    $limit = element('limit', $query_string, null);
    $offset = element('offset', $query_string, null);
    
    if ($offset && !$limit) {
      $this->returnError(503, $this->version);
      return;
    }

    $query = $this->db->select('`i`.*');
    $query->from('implementation i');
    if ($tag) {
      $query->join('implementation_tag t', 'i.id = t.id');
      $query->where('t.tag', $tag);
    }
    $query->group_start()->where('`visibility`', 'public')->or_where('i.uploader', $this->user_id)->group_end();
    if ($uploader_id !== null) {
      $query->where('i.uploader', $uploader_id);
    }
    if ($limit) {
      $query->limit($limit);
    }
    if ($offset) {
      $query->offset($offset);
    }
    $sql = $query->get_compiled_select();

    # TODO: can remove next statement and replace by original active record
    $implementations_res = $this->Implementation->query($sql);
    if($implementations_res == false) {
      $this->returnError(500, $this->version);
      return;
    }

    $this->xmlContents('implementations', $this->version, array('implementations' => $implementations_res));
  }


  private function flow_exists($name, $external_version) {

    $similar = false;
    if( $name !== false && $external_version !== false ) {
      $similar = $this->Implementation->getWhere( '`name` = "' . $name . '" AND `external_version` = "' . $external_version . '"' );
    } else {
      $this->returnError( 330, $this->version );
      return;
    }

    $result = array( 'exists' => 'false', 'id' => -1 );
    if( $similar ) {
      $result = array( 'exists' => 'true', 'id' => $similar[0]->id );
    }
    $this->xmlContents( 'implementation-exists', $this->version, $result );
  }

  //  TODO: check what is going wrong with implementation id 1
  private function flow($id) {
    if( $id == false ) {
      $this->returnError( 180, $this->version );
      return;
    }

    $implementation = $this->Implementation->fullImplementation( $id );

    if( $implementation === false ) {
      $this->returnError( 181, $this->version );
      return;
    }

    $this->xmlContents( 'implementation-get', $this->version, array( 'source' => $implementation ) );
  }

  private function flow_upload() {

    if(isset($_FILES['source']) && $_FILES['source']['error'] == 0) {
      $source = true;
    } else {
      $source = false;
      unset($_FILES['source']);
    }

    if(isset($_FILES['binary']) && $_FILES['binary']['error'] == 0) {
      $binary = true;
    } else {
      $binary = false;
      unset($_FILES['binary']);
    }

    foreach( $_FILES as $key => $file ) {
      if( check_uploaded_file( $file ) == false ) {
        $this->returnError( 160, $this->version );
        return;
      }
    }

    $xsd = xsd('openml.implementation.upload', $this->controller, $this->version);
    if (!$xsd) {
      $this->returnError( 172, $this->version, $this->openmlGeneralErrorCode );
      return;
    }

    // get correct description
    if( $this->input->post('description') ) {
      // get description from string upload
      $description = $this->input->post('description');
      $xmlErrors = "";
      if( validateXml( $description, $xsd, $xmlErrors, false ) == false ) {
        if (DEBUG) {
          $to = $this->user_email;
          $subject = 'OpenML Flow Upload DEBUG message. ';
          $content = "Uploaded by POST field.\nXSD Validation Message: " . $xmlErrors . "\n=====BEGIN XML=====\n" . $this->input->post('description');
          sendEmail($to, $subject, $content,'text');
        }

        $this->returnError( 163, $this->version, $this->openmlGeneralErrorCode, $xmlErrors );
        return;
      }
      $xml = simplexml_load_string( $description );
    } elseif(isset($_FILES['description'])) {
      // get description from file upload
      $description = $_FILES['description'];

      if (validateXml($description['tmp_name'], $xsd, $xmlErrors) == false) {
        if (DEBUG) {
          $to = $this->user_email;
          $subject = 'OpenML Flow Upload DEBUG message. ';
          $content = 'Filename: ' . $_FILES['description']['name'] . "\nXSD Validation Message: " . $xmlErrors . "\n=====BEGIN XML=====\n" . file_get_contents($description['tmp_name']);
          sendEmail($to, $subject, $content,'text');
        }

        $this->returnError( 163, $this->version, $this->openmlGeneralErrorCode, $xmlErrors );
        return;
      }
      $xml = simplexml_load_file( $description['tmp_name'] );
      $similar = $this->Implementation->compareToXML( $xml );
      if( $similar ) {
        $this->returnError( 171, $this->version, $this->openmlGeneralErrorCode, 'implementation_id:' . $similar );
        return;
      }
    } else {
      $this->returnError( 161, $this->version );
      return;
    }

    $name = ''.$xml->children('oml', true)->{'name'};

    $implementation = array(
      'uploadDate' => now(),
      'uploader' => $this->user_id
    );

    foreach( $_FILES as $key => $file ) {
      if( $key == 'description' ) { continue; }
      if( ! in_array( $key, array( 'description', 'source', 'binary' ) ) ) {
        $this->returnError( 167, $this->version );
        return;
      }

      $file_id = $this->File->register_uploaded_file($_FILES[$key], $this->data_folders['implementation'] . $key . '/', $this->user_id, 'implementation');
      if($file_id === false) {
        $this->returnError( 173, $this->version );
        return;
      }
      $file_record = $this->File->getById($file_id);

      //$implementation[$key.'Url'] = $this->data_controller . 'download/' . $file_id . '/' . $file_record->filename_original;
      $implementation[$key.'_md5'] = $file_record->md5_hash;
      $implementation[$key.'_file_id'] = $file_id;
      //$implementation[$key.'Format'] = $file_record->md5_hash;

      if( property_exists( $xml->children('oml', true), $key.'_md5' ) ) {
        if( $xml->children('oml', true)->{$key.'_md5'} != $file_record->md5_hash ) {
          $this->returnError( 168, $this->version );
          return;
        }
      }
    }

    $impl = $this->insertImplementationFromXML( $xml->children('oml', true), $this->xml_fields_implementation, $implementation );
    if( $impl == false ) {
      $this->returnError( 165, $this->version );
      return;
    }
    $implementation = $this->Implementation->getById( $impl );

    try {
      // update elastic search index.
      $this->elasticsearch->index('flow', $impl);

      // update counters
      $this->elasticsearch->index('user', $this->user_id);
    } catch (Exception $e) {
      // TODO: should be logged
    }

    $this->xmlContents( 'implementation-upload', $this->version, $implementation );
  }

  private function flow_delete($flow_id) {

    $implementation = $this->Implementation->getById($flow_id);
    if($implementation == false) {
      $this->returnError(322, $this->version);
      return;
    }

    if($implementation->uploader != $this->user_id && $this->user_has_admin_rights == false) {
      $this->returnError(323, $this->version);
      return;
    }

    $runs = $this->Run->getRunsByFlowId($implementation->id, null, null, 100);

    if ($runs) {
      $ids = array();
      foreach ($runs as $r) {
        $ids[] = $r->id;
      }
      $this->returnError(324, $this->version, $this->openmlGeneralErrorCode, '{'. implode(', ', $ids) .'} ()');
      return;
    }

    if ($this->Implementation->isComponent($implementation->id)) {
      $parent_ids = $this->Implementation_component->getColumnWhere('parent', 'child = "'.$implementation->id.'"');
      $this->returnError(328, $this->version, $this->openmlGeneralErrorCode, '{' . implode(', ', $parent_ids) . '}');
      return;
    }

    $remove_input_setting = $this->Input_setting->deleteWhere('setup IN (SELECT sid FROM algorithm_setup WHERE implementation_id = '.$implementation->id.')');
    if (!$remove_input_setting) {
      $this->returnError(326, $this->version);
      return;
    }
    $remove_setups = $this->Algorithm_setup->deleteWhere('implementation_id = ' . $implementation->id);
    if (!$remove_setups) {
      $this->returnError(327, $this->version);
      return;
    }

    $this->Input->deleteWhere('implementation_id =' . $implementation->id); // should be handled by constraints ..
    $this->Implementation_component->deleteWhere('parent = ' . $implementation->id);
    $result = $this->Implementation->delete($implementation->id);
    if( $implementation->binary_file_id != false ) { $this->File->delete_file($implementation->binary_file_id); }
    if( $implementation->source_file_id != false ) { $this->File->delete_file($implementation->source_file_id); }

    // TODO: also check component parts.

    if($result == false) {
      $this->returnError(325, $this->version);
      return;
    }

    try {
      $this->elasticsearch->delete('flow', $flow_id);
      $this->elasticsearch->index('user', $this->user_id);
    } catch (Exception $e) {
      $additionalMsg = get_class() . '.' . __FUNCTION__ . ':' . $e->getMessage();
      $this->returnError(105, $this->version, $this->openmlGeneralErrorCode, $additionalMsg);
      return;
    }

    $this->xmlContents('implementation-delete', $this->version, array('implementation' => $implementation));
  }

  private function flow_forcedelete($flow_id) {
    if( $this->ion_auth->is_admin($this->user_id) == false ) {
      $this->returnError( 550, $this->version );
      return;
    }

    $condition = 'SELECT rid FROM run r, algorithm_setup s WHERE s.sid = r.setup AND s.implementation_id = ' . $flow_id;

    $queries = array(
      'evaluation' => 'DELETE FROM evaluation WHERE source IN ('.$condition.');',
      'evaluation_fold' => 'DELETE FROM evaluation_fold WHERE source IN ('.$condition.');',
      'evaluation_sample' => 'DELETE FROM evaluation_sample WHERE source IN ('.$condition.');',
      'runfile' => 'DELETE FROM runfile WHERE source IN ('.$condition.');',
      'run' => 'DELETE FROM run WHERE setup IN (SELECT sid FROM algorithm_setup WHERE implementation_id = '.$flow_id.');',
      'algorithm_setup' => 'DELETE FROM algorithm_setup WHERE implementation_id = ' . $flow_id . ';'
    );

    foreach ($queries as $table => $query) {
      $res = $this->Implementation->query($query);
      if ($res == false) {
        $this->returnError(551, $this->version, $this->openmlGeneralErrorCode, 'In query table: ' . $table);
        return;
      }
    }

    $this->flow_delete($flow_id);
  }

  private function insertImplementationFromXML( $xml, $configuration, $implementation_base = array() ) {
    $implementation_objects = all_tags_from_xml( $xml, array_custom_filter($configuration, array('plain','array')) );
    $implementation = all_tags_from_xml( $xml, array_custom_filter($configuration, array('string','csv')), $implementation_base );

    // insert the implementation itself
    $version = $this->Implementation->incrementVersionNumber( $implementation['name'] );
    $implementation['fullName'] = $implementation['name'] . '(' . $version . ')';
    $implementation['version'] = $version;

    if( array_key_exists( 'source_md5', $implementation ) ) {
      if( array_key_exists( 'external_version', $implementation ) === false ) {
        $implementation['external_version'] = $implementation['source_md5'];
      }
    } elseif( array_key_exists( 'binary_md5', $implementation ) ) {
      if( array_key_exists( 'external_version', $implementation ) === false ) {
        $implementation['external_version'] = $implementation['binary_md5'];
      }
    }

    if( array_key_exists( 'implements', $implementation ) ) {
      if( in_array( $implementation['implements'], $this->supportedMetrics ) == false &&
          in_array( $implementation['implements'], $this->supportedAlgorithms == false ) ) {
        return false;
      }
    }

    // information illegal to insert
    unset($implementation['source_md5']);
    unset($implementation['binary_md5']);

    // tags also not insertable. but handled differently.
    $tags = array();
    if( array_key_exists( 'tag', $implementation ) ) {
      $tags = str_getcsv( $implementation['tag'] );
      unset( $implementation['tag'] );
    }
    $flow_id = $this->Implementation->insert( $implementation );
    if( $flow_id === false ) {
      return false;
    }

    // add to elastic search index.
    try {
      $this->elasticsearch->index('flow', $flow_id);
    } catch (Exception $e) {
      // TODO should be logged
    }


    foreach( $tags as $tag ) {
      $error = -1;
      $res = $this->entity_tag_untag('implementation', $flow_id, $tag, false, 'flow', true);
      if ($res != true) { // TODO: do something better
        exit();
      }
    }


    // insert all important "components"
    foreach( $implementation_objects as $key => $value ) {

      if( $key == 'component' ) {
        foreach($value as $entry) {
          $component = $entry->flow->children('oml', true);
          $similarComponent = $this->Implementation->compareToXml( $entry->flow );
          if( $similarComponent === false ) {
            $component->version = $this->Implementation->incrementVersionNumber( $component->name );
            $componentFullName = $component->name . '(' . $component->version . ')';
            $succes = $this->insertImplementationFromXML(
              $component,
              $configuration,
              array('uploadDate' => $implementation['uploadDate'], 'uploader' => $implementation['uploader']));

            if($succes == false) { return false; }
            $this->Implementation->addComponent( $flow_id, $succes, trim($entry->identifier) );
          } else {
            $this->Implementation->addComponent( $flow_id, $similarComponent, trim($entry->identifier) );
          }
        }
      } elseif( $key == 'parameter' ) {
        foreach( $value as $entry ) {
          $children = $entry->children('oml', true);
          $succes = $this->Input->insert(
            array(
              'implementation_id' => $flow_id,
              'name' => trim($children->name),
              'defaultValue' => property_exists( $children, 'default_value') ? trim($children->default_value) : null,
              'description' => property_exists( $children, 'description') ? trim($children->description) : null,
              'dataType' => property_exists( $children, 'data_type') ? trim($children->data_type) : null,
              'recommendedRange' => property_exists( $children, 'recommended_range') ? trim($children->recommendedRange) : null
            )
          );
        }
      }
    }
    return $flow_id;
  }
}
?>
