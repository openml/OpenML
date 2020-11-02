<?php
class Api_study extends MY_Api_Model {

  protected $version = 'v1';

  function __construct() {
    parent::__construct();
    $this->load->model('Run_study');
    $this->load->model('Study');
    $this->load->model('Task_study');
    $this->db = $this->Database_singleton->getWriteConnection();
  }

  function bootstrap($format, $segments, $request_type, $user_id) {
    $this->outputFormat = $format;

    $getpost = array('get','post');

    if (count($segments) > 0 && $segments[0] == 'list') {
      array_shift($segments);
      $this->study_list($segments);
      return;
    }

    if (count($segments) == 0) {
      $this->study_create();
      return;
    }
    
    if (count($segments) == 2 && $segments[0] == 'status' && $segments[1] == 'update') {
      $this->status_update($this->input->post('study_id'), $this->input->post('status'));
      return;
    }

    if (count($segments) == 1 && is_numeric($segments[0]) && $request_type == 'delete') {
      $this->study_delete($segments[0]);
      return;
    }

    if (count($segments) == 2 && is_numeric($segments[0]) && $segments[1] == 'attach' && $request_type == 'post') {
      $this->study_attach($segments[0]);
      return;
    }

    if (count($segments) == 2 && is_numeric($segments[0]) && $segments[1] == 'detach' && $request_type == 'post') {
      $this->study_detach($segments[0]);
      return;
    }
    
    if (count($segments) == 1 || count($segments) == 2) {
      $type = null;
      if (count($segments) == 2) {
        $type = $segments[1];
      }
      $this->study_get($segments[0], $type);
      return;
    }

    $this->returnError(100, $this->version);
  }

  /**
   *@OA\Post(
   *	path="/study",
   *	tags={"study"},
   *	summary="Create new study",
   *	description="Creates a new study. Upon success, it returns the study id.",
   *	@OA\Parameter(
   *		name="description",
   *		in="query",
   *		@OA\Schema(
   *          type="file"
   *        ),
   *		description="An XML file describing the study. Also see the [XSD schema](https://www.openml.org/api/v1/xsd/openml.study.upload) and an [XML example](https://www.openml.org/api/v1/xml_example/study).",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="api_key",
   *		in="query",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="Api key to authenticate the user",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="Id of the uploaded study",
   *		@OA\JsonContent(
   *			type="object",
   *			@OA\Property(
   *				property="upload_study",
   *				ref="#/components/schemas/inline_response_200_25_upload_study",
   *			),
   *			example={
   *			  "upload_study": {
   *			    "id": "4328"
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n1031 - Description file not present. Please upload the study description.\n1032 - Problem validating uploaded description file. The XML description format does not meet the standards. See the XSD schema.\n1033 - Illegal main entity type. Currently only collections of tasks and can be created.\n1034 - Linked entities are not of the correct type fot this study.\n1035 - Benchmark suites can only be linked to run studies.\n1036 - Referred benchmark suite cannot be found.\n1037 - Referred benchmark suite should be a task collection.\n1038 - Study alias is not unique.\n1039 - Dataset insertion problem. Please contact the administrators.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function study_create() {
    $xsdFile = xsd('openml.study.upload', $this->controller, $this->version);

    $legal_entity_types = array(
      'task',
      'run'
    );
    
    if (isset($_FILES['description'])) {
      $uploadError = '';
      $xmlErrors = '';
      if (check_uploaded_file($_FILES['description'], false, $uploadError) == false) {
        $this->returnError(1031, $this->version, $this->openmlGeneralErrorCode, $uploadError);
      }
      // get description from file upload
      $description = $_FILES['description'];

      if (validateXml($description['tmp_name'], $xsdFile, $xmlErrors) == false) {
        $this->returnError(1032, $this->version, $this->openmlGeneralErrorCode, $xmlErrors);
        return;
      }
      $xml = simplexml_load_file($description['tmp_name']);
    }
    
    $study = all_tags_from_xml($xml->children('oml', true), $this->xml_fields_study);
    
    if (!in_array($study['main_entity_type'], $legal_entity_types)) {
      $this->returnError(1033, $this->version);
      return;
    }
    $link_entities = $this->_get_linked_entities_from_xml($xml, $legal_entity_types);
    $errors = array_diff(array_keys($link_entities), array($study['main_entity_type']));
    if (count($errors) > 0) {
      $this->returnError(1034, $this->version, 'Illegal entity_type(s): ' . implode(', ', $errors));
      return;
    }
    
    if (array_key_exists('benchmark_suite', $study)) {
      if ($study['main_entity_type'] != 'run') {
        $this->returnError(1035, $this->version);
        return;
      }
      
      $benchmark_suite = $this->Study->get_by_id($study['benchmark_suite']);
      if (!$benchmark_suite) {
        $this->returnError(1036, $this->version);
        return;
      }
      
      if ($benchmark_suite->main_entity_type != 'task') {
        $this->returnError(1037, $this->version);
        return;
      }
    }
    
    if (array_key_exists('alias', $study)) {
      $res = $this->Study->getWhereSingle('alias = "' . $study['alias']  . '"');
      if ($res) {
        $this->returnError(1038, $this->version);
        return;
      }
    }
    
    $this->db->trans_start();
    
    $schedule_data = array(
      'alias' => array_key_exists('alias', $study) ? $study['alias'] : null, 
      'main_entity_type' => $study['main_entity_type'],
      'benchmark_suite' => array_key_exists('benchmark_suite', $study) ? $study['benchmark_suite'] : null,
      'name' => $study['name'], 
      'description' => array_key_exists('description', $study) ? $study['description'] : null,
      'visibility' => 'public',
      'creation_date' => now(),
      'creator' => $this->user_id,
      'legacy' => 'n', 
    );
    
    $study_id = $this->Study->insert($schedule_data);
    if (!$study_id) {
      $this->db->trans_rollback();
      $this->returnError(1039, $this->version, $this->openmlGeneralErrorCode, 'Problem creating study record. Please check whether the alias is unique. ');
      return;
    }
    
    $res = $this->_link_entities($study_id, $this->user_id, $link_entities);
    
    if ($res === false || $this->db->trans_status() === false)
    {
      $this->db->trans_rollback();
      $this->returnError(1039, $this->version);
      return;
    }
    $this->db->trans_commit();
    
    // try making the ES stuff
    try {
      // update elastic search index.
      $this->elasticsearch->index('study', $study_id);

      // update counters
      $this->elasticsearch->index('user', $this->user_id);
    } catch (Exception $e) {
      // TODO: should log
    }
    
    $this->xmlContents('study-upload', $this->version, array('study_id' => $study_id));
  }
  
  private function status_update($study_id, $status) {
    // in_preparation is not a legal status to change to
    $legal_status = array('active', 'deactivated');
    if (!in_array($status, $legal_status)) {
      $this->returnError(1051, $this->version);
      return;
    }
    
    $study = $this->Study->getById($study_id);
    if ($study == false) {
      $this->returnError(1052, $this->version);
      return;
    }

    if ($study->creator != $this->user_id and !$this->user_has_admin_rights) {
      $this->returnError(1053, $this->version);
      return;
    }
    
    $result = $this->Study->update($study_id, array('status' => $status));
    if ($result === false) {
      $this->returnError(1054, $this->version);
      return;
    }
    // get updated study
    $study = $this->Study->getById($study_id);
    $template_vars = array(
      'id' => $study->id,
      'status' => $study->status
    );
    
    $this->xmlContents('study-status-update', $this->version, $template_vars);
  }

  /**
   *@OA\Post(
   *	path="/study/{id}/attach",
   *	tags={"study"},
   *	summary="Attach a new entity to a study",
   *	description="Attach a new entity to an exising study. Upon success, it returns the study id, type, and linked entities.",
   *	@OA\Parameter(
   *		name="id",
   *		in="path",
   *		@OA\Schema(
   *          type="integer"
   *        ),
   *		description="Id of the study. Supplied in the URL path.",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="ids",
   *		in="query",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="Comma-separated list of entity IDs to be attached to the study. For instance, if this is a run study, the list of run IDs that need to be added (attached) to the study. Must be supplied as a POST variable.",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="api_key",
   *		in="query",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="Api key to authenticate the user",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="Properties of the updated study",
   *		@OA\JsonContent(
   *			type="object",
   *			@OA\Property(
   *				property="study_attach",
   *				ref="#/components/schemas/inline_response_200_26_study_attach",
   *			),
   *			example={
   *			  "study_attach": {
   *			    "id": "1",
   *			    "main_entity_type": "task",
   *			    "linked_entities": "5"
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n1041 - Could not find study. Check the study ID in your request.\n1042 - Cannnot attach entities to legacy studies.\n1043 - Please provide POST field 'ids'.\n1044 - Please ensure that the 'ids' in the POST field is a list of natural numbers.\n1045 - Could not attach entities to the study. It appears as if the entity does not exist.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function study_attach($study_id) {
    $this->study_attach_detach($study_id, true);
  }

  /**
   *@OA\Post(
   *	path="/study/{id}/detach",
   *	tags={"study"},
   *	summary="Detach an entity from a study",
   *	description="Detach an entity from an exising study. Upon success, it returns the study id, type, and linked entities.",
   *	@OA\Parameter(
   *		name="id",
   *		in="path",
   *		@OA\Schema(
   *          type="integer"
   *        ),
   *		description="Id of the study.",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="ids",
   *		in="query",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="Comma-separated list of entity IDs to be detached from the study. For instance, if this is a run study, the list of run IDs that need to be removed (detached) from the study. Must be supplied as a POST variable.",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="api_key",
   *		in="query",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="Api key to authenticate the user",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="Properties of the updated study",
   *		@OA\JsonContent(
   *			type="object",
   *			@OA\Property(
   *				property="upload_study",
   *				ref="#/components/schemas/inline_response_200_26_study_attach",
   *			),
   *			example={
   *			  "study_detach": {
   *			    "id": "1",
   *			    "main_entity_type": "task",
   *			    "linked_entities": "5"
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n1041 - Could not find study. Check the study ID in your request.\n1042 - Cannot attach entities to legacy studies.\n1043 - Please provide POST field 'ids'.\n1044 - Please ensure that the 'ids' in the POST field is a list of natural numbers.\n1046 - Could not detach entities from the study. It appears as if the entity does not exist.       \n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function study_detach($study_id) {
    $this->study_attach_detach($study_id, false);
  }

  private function study_attach_detach($study_id, $attach) {
    $study = $this->Study->getById($study_id);
    if ($study === false) {
      $this->returnError(1041, $this->version);
      return;
    }
    
    if ($study->legacy == 'y') {
      $this->returnError(1042, $this->version);
      return;
    }
    
    $entity_ids = $this->input->post('ids');
    if ($entity_ids === false) {
      $this->returnError(1043, $this->version);
      return;
    }
    
    if (!is_cs_natural_numbers($entity_ids)) {
      $this->returnError(1044, $this->version);
      return;
    }
    
    if (!$study->status == 'in_preparation') {
      $this->returnError(1047, $this->version);
      return;
    }
    
    $link_entities = array(
      $study->main_entity_type => explode(',', $entity_ids)
    );
    
    if ($attach) {
      $this->db->trans_start();
      $res = $this->_link_entities($study_id, $this->user_id, $link_entities);
      if ($res === false || $this->db->trans_status() === false)
      {
        $this->db->trans_rollback();
        $this->returnError(1045, $this->version);
        return;
      }
      $this->db->trans_commit();
    } else {
      $model = ucfirst($study->main_entity_type) . '_study';
      $id_name = $study->main_entity_type . '_id';
      $result = $this->{$model}->deleteWhere('study_id = ' . $study_id . ' and ' . $id_name . ' IN (' . $entity_ids . ')');
      
      if ($result === false) {
        $this->returnError(1046, $this->version);
        return;
      }
    }
    
    if ($study->main_entity_type == 'run') {
      $res = $this->Run_study->get_entities($study->id)['runs'];
    } else if ($study->main_entity_type == 'task') {
      $res = $this->Task_study->get_entities($study->id)['tasks'];
    }
    
    $template_vars = array(
      'id' => $study->id,
      'main_entity_type' => $study->main_entity_type,
      'function_type' => $attach ? 'attach' : 'detach',
      'count' => count($res),
    );
    
    $this->xmlContents('study-attach-detach', $this->version, $template_vars);
  }

  /**
   *@OA\Delete(
   *	path="/study/{id}",
   *	tags={"study"},
   *	summary="Delete study",
   *	description="Deletes a study. Upon success, it returns the ID of the deleted study.",
   *	@OA\Parameter(
   *		name="id",
   *		in="path",
   *		@OA\Schema(
   *          type="integer"
   *        ),
   *		description="Id of the study.",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="api_key",
   *		in="query",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="Api key to authenticate the user",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="ID of the deleted study",
   *		@OA\JsonContent(
   *			type="object",
   *			@OA\Property(
   *				property="study_delete",
   *				ref="#/components/schemas/inline_response_200_24_study_delete",
   *			),
   *			example={
   *			  "study_delete": {
   *			    "id": "1"
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n591 - Please provide API key. In order to remove your content, please authenticate.\n592 - Study does not exists. The study ID could not be linked to an existing study.\n593 - Study deletion failed. Please try again later.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function study_delete($study_id) {

    $study = $this->Study->getById($study_id);
    if ($study == false) {
      $this->returnError(592, $this->version);
      return;
    }
    
    if ($study->creator != $this->user_id and !$this->user_has_admin_rights) {
      $this->returnError(594, $this->version);
      return;
    }
    
    if ($study->main_entity_type != 'run' && $study->status != 'in_preparation') {
      $this->returnError(595, $this->version);
      return;
    }
    
    $this->Run_study->deleteWhere('study_id = ' . $study->id);
    $this->Task_study->deleteWhere('study_id = ' . $study->id);
    $result = $this->Study->delete($study_id);
    if ($result == false) {
      $this->returnError(593, $this->version);
      return;
    }

    try {
      $this->elasticsearch->delete('study', $study_id);
    } catch (Exception $e) {
      $additionalMsg = get_class() . '.' . __FUNCTION__ . ':' . $e->getMessage();
      $this->returnError(105, $this->version, $this->openmlGeneralErrorCode, $additionalMsg);
      return;
    }

    $this->xmlContents('study-delete', $this->version, array('study' => $study));
  }

  /**
   *@OA\Get(
   *	path="/study/list/{filters}",
   *	tags={"study"},
   *	summary="List all studies (collections of items)",
   *	description="List studies, optionally filtered by a range of properties. Any number of properties can be combined by listing them one after the other in the form '/study/list/{filter}/{value}/{filter}/{value}/...' Returns an array with all studies that match the constraints.",
   *	@OA\Parameter(
   *		name="filters",
   *		in="path",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="Any combination of these filters
  /main_entity_type/{type} - only return studies collecting entities of a given type (e.g. 'task' or 'run').
  /uploader/{ids} - return only evaluations uploaded by specific users, specified as a comma-separated list of user IDs, e.g. ''1,2,3''
  /limit/{limit}/offset/{offset} - returns only {limit} results starting from result number {offset}. Useful for paginating results. With /limit/5/offset/10, results 11..15 will be returned. Both limit and offset need to be specified.
  ",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="A list of studies",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/StudyList",
   *			example={
   *			  "study_list":{
   *			    "study":{
   *			      {
   *			        "id":"1",
   *			        "alias":"Study_1",
   *			        "name":"A large-scale comparison of classification algorithms",
   *			        "creation_date":"2017-07-20 15:51:20",
   *			        "creator":"2"
   *			      },
   *			      {
   *			        "id":"2",
   *			        "alias":"Study_2",
   *			        "name":"Fast Algorithm Selection using Learning Curves",
   *			        "creation_date":"2017-07-20 15:51:20",
   *			        "creator":"2"
   *			      }
   *			    }
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function study_list($segs) {
    $legal_filters = array('limit', 'offset', 'main_entity_type', 'uploader', 'status', 'benchmark_suite');
    
    list($query_string, $illegal_filters) = $this->parse_filters($segs, $legal_filters);
    if (count($illegal_filters) > 0) {
      $this->returnError(596, $this->version, $this->openmlGeneralErrorCode, 'Legal filter operators: ' . implode(',', $legal_filters) .'. Found illegal filter(s): ' . implode(', ', $illegal_filters));
      return;
    }
    
    $illegal_filter_inputs = $this->check_filter_inputs($query_string, $legal_filters, array('main_entity_type', 'status'));
    if (count($illegal_filter_inputs) > 0) {
      $this->returnError(597, $this->version, $this->openmlGeneralErrorCode, 'Filters with illegal values: ' . implode(',', $illegal_filter_inputs));
      return;
    }
    
    $uploader = element('uploader', $query_string, null);
    $limit = element('limit', $query_string, null);
    $offset = element('offset', $query_string, null);
    $status = element('status', $query_string, null);
    $main_entity_type = element('main_entity_type', $query_string, null);
    $benchmark_suite = element('benchmark_suite', $query_string, null);
    
    if ($offset && !$limit) {
      $this->returnError(598, $this->version);
      return;
    }
    
    $whereClause = '(visibility = "public" or creator = ' . $this->user_id . ')';
    if ($uploader) {
      $whereClause .= ' AND creator = ' . $uploader;
    }
    if ($main_entity_type) {
      $whereClause .= ' AND main_entity_type = "' . $main_entity_type . '"';
    }
    if ($benchmark_suite) {
      $whereClause .= ' AND benchmark_suite = "' . $benchmark_suite . '"';
    }
    if ($status) {
      if ($status != 'all') {
        $whereClause .= ' AND status = "' . $status . '"';
      }
    } else {
      $whereClause .= ' AND status = "active"';
    }
    $studies = $this->Study->getWhere($whereClause, null, $limit, $offset);

    if (!$studies) {
      $this->returnError(599, $this->version);
      return;
    }

    $this->xmlContents('study-list', $this->version, array('studies' => $studies));
  }

  /**
   *@OA\Get(
   *	path="/study/{id}",
   *	tags={"study"},
   *	summary="Get study description by study id or alias",
   *	description="Returns information about the study with the given id or alias.",
   *	@OA\Parameter(
   *		name="id",
   *		in="path",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="ID or alias of the study.",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="A study description",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Study",
   *			example={
   *			  "study": {
   *			    "id": "99",
   *			    "main_entity_type": "task",
   *			    "name": "CC18 benchmark suite",
   *			    "description": "CC18 benchmark suite",
   *			    "creation_date": "2019-02-16T17:35:58",
   *			    "creator": "1159",
   *			    "data": {"data_id": {"1","2","3"}},
   *			    "tasks": {"task_id": {"1","2","3"}}
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n601 - Unknown study. The study with the given id or alias was not found in the database\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function study_get($study_id, $type) {
    if (is_numeric($study_id)) {
      $this->study_by_id($study_id, $type);
      return;
    } else {
      $this->study_by_alias($study_id, $type);
      return;
    }
  }

  private function study_by_id($study_id, $entity_type) {
    $study = $this->Study->getById($study_id);

    if ($study == false) {
      $this->returnError(601, $this->version);
      return;
    }

    if ($study->legacy == 'y') {
      $this->_legacy_study_get($study, $entity_type);
    } else {
      $this->_study_get($study, $entity_type);
    }
  }

  private function study_by_alias($study_alias, $entity_type) {
    $study = $this->Study->getWhereSingle('alias = "' . $study_alias . '"');

    if ($study == false) {
      $this->returnError(601, $this->version);
      return;
    }
    
    if ($study->legacy == 'y') {
      $this->_legacy_study_get($study, $entity_type);
    } else {
      $this->_study_get($study, $entity_type);
    }
  }
  
  // TODO: remove ASAP
  private function _legacy_study_get($study, $entity_type) {
    $valid_entity_types = array('runs', 'flows', 'setups', 'data', 'tasks', NULL);
    if (!in_array($entity_type, $valid_entity_types)) {
      $this->returnError(600, $this->version, $this->openmlGeneralErrorCode, 'Got: ' . $entity_type);
      return;
    }

    if ($study->creator != $this->user_id && $study->visibility != 'public') {
      $this->returnError(602, $this->version);
      return;
    }

    $tags = $this->Study_tag->getWhere('study_id = ' . $study->id);
    if ($tags == false) {
      $this->returnError(603, $this->version);
      return;
    }

    $data = null;
    $tasks = null;
    $flows = null;
    $setups = null;
    $runs = null;

    if ($entity_type == null || $entity_type == 'data') {
      $data = $this->Study_tag->getDataIdsFromStudy($study->id);
    }

    if ($entity_type == null || $entity_type == 'tasks') {
      $tasks = $this->Study_tag->getTaskIdsFromStudy($study->id);
    }

    if ($entity_type == null || $entity_type == 'flows') {
      $flows = $this->Study_tag->getFlowIdsFromStudy($study->id);
    }

    if ($entity_type == null || $entity_type == 'setups') {
      $setups = $this->Study_tag->getSetupIdsFromStudy($study->id);
    }

    if ($entity_type == null || $entity_type == 'runs') {
      $runs = $this->Study_tag->getRunIdsFromStudy($study->id);
    }

    $template_values = array(
      'study' => $study,
      'tags' => $tags,
      'data' => $data,
      'tasks' => $tasks,
      'flows' => $flows,
      'setups' => $setups,
      'runs' => $runs
    );

    $this->xmlContents('study-get', $this->version, $template_values);
  }
  
  private function _study_get($study, $entity_type) {
    $valid_entity_types = array('runs', 'flows', 'setups', 'data', 'tasks', NULL);
    if (!in_array($entity_type, $valid_entity_types)) {
      $this->returnError(600, $this->version, $this->openmlGeneralErrorCode, 'Got: ' . $entity_type);
      return;
    }

    if ($study->creator != $this->user_id && $study->visibility != 'public') {
      $this->returnError(602, $this->version);
      return;
    }
    
    if ($study->main_entity_type == 'run') {
      $res = $this->Run_study->get_entities($study->id);
    } else if ($study->main_entity_type == 'task') {
      $res = $this->Task_study->get_entities($study->id);
    } else {
      $this->returnError(604, $this->version);
      return;
    }
    
    $data = array_key_exists('data', $res) ? $res['data'] : null;
    $tasks = array_key_exists('tasks', $res) ? $res['tasks'] : null;
    $flows = array_key_exists('flows', $res) ? $res['flows'] : null;
    $setups = array_key_exists('setups', $res) ? $res['setups'] : null;
    $runs = array_key_exists('runs', $res) ? $res['runs'] : null;
    
    $template_values = array(
      'study' => $study,
      'tags' => null,
      'data' => $data,
      'tasks' => $tasks,
      'flows' => $flows,
      'setups' => $setups,
      'runs' => $runs
    ); 

    $this->xmlContents('study-get', $this->version, $template_values);
  }
  
  private function _get_linked_entities_from_xml($xml, $legal_entity_types) {
    $linked_entities = array();
    foreach ($legal_entity_types as $lkt) {
      $outer_tag = $lkt . 's';
      $inner_tag = $lkt . '_id';
      if ($xml->children('oml', true)->{$outer_tag}) {
        $linked_entities[$lkt] = $xml->children('oml', true)->{$outer_tag}->{$inner_tag};
      }
    }
    return $linked_entities;
  }
  
  private function _link_entities($study_id, $uploader_id, $link_entities) {
    // study_id is int, link_entities is array mapping from entity type to
    // array of integer ids
    $study = $this->Study->getById($study_id);
    if ($study == false) {
      return false;
    }
    $model = ucfirst($study->main_entity_type) . '_study';
    $id_name = $study->main_entity_type . '_id';
    
    foreach ($link_entities[$study->main_entity_type] as $id) {
      $data = array(
        'study_id' => $study_id,
        $id_name => $id,
        'uploader' => $uploader_id,
        'date' => now(),
      );
      $result = $this->{$model}->insert($data);
      if (!$result) {
        return false;
      }
    }
    return true;
  }
}

?>
