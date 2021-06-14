<?php

class Api_data extends MY_Api_Model {

// we still use errors, xml content from v1. We need to change this if something changes.
  protected $version = 'v1';

  function __construct() {
    parent::__construct();

    // load models
    $this->load->model('Data_processed');
    $this->load->model('Dataset');
    $this->load->model('Dataset_status');
    $this->load->model('Dataset_tag');
    $this->load->model('Dataset_topic');
    $this->load->model('Dataset_description');
    $this->load->model('Data_feature');
    $this->load->model('Data_feature_value');
    $this->load->model('Data_quality');
    $this->load->model('Feature_quality');
    $this->load->model('Data_quality_interval');
    $this->load->model('Quality');
    $this->load->model('File');
    $this->load->model('Study_tag');

    $this->load->helper('file_upload');
    $this->db = $this->Database_singleton->getWriteConnection();

    $this->legal_formats = array('arff', 'sparse_arff');
  }

  function bootstrap($format, $segments, $request_type, $user_id) {
    $this->outputFormat = $format;

    $getpost = array('get','post');
    if (count($segments) >= 1 && $segments[0] == 'list') {
      array_shift($segments);
      $this->data_list($segments);
      return;
    }

    if (count($segments) == 1 && is_numeric($segments[0]) && in_array($request_type, $getpost)) {
      $this->data($segments[0]);
      return;
    }

    $order_values = array('random', 'normal');

    if (count($segments) == 3 && $segments[0] == 'unprocessed' && is_numeric($segments[1]) && in_array($segments[2], $order_values)) {
      $this->data_unprocessed($segments[1], $segments[2]);
      return;
    }

    if (count($segments) >= 4 && count($segments) <= 6 && $segments[0] == 'qualities' && $segments[1] == 'unprocessed' && is_numeric($segments[2]) && in_array($segments[3], $order_values)) {
      $feature = (count($segments) > 4 && $segments[4] == 'feature');
      // oops, badly defined api call with two optional parameters. boolean feature and string priority tag. 
      // we will try to fix this here. 
      if ($feature && count($segments) == 6) {
        $priorityTag = $segments[5];
      } elseif ($feature == false && count($segments) == 5) {
        $priorityTag = $segments[4];
      } else {
        $priorityTag = null;
      }
      
      $this->dataqualities_unprocessed($segments[2], $segments[3], $feature, $priorityTag);
      return;
    }

    if (count($segments) == 1 && is_numeric($segments[0]) && $request_type == 'delete') {
      $this->data_delete($segments[0]);
      return;
    }

    if (count($segments) == 2 && $segments[0] == 'reset' && is_numeric($segments[1]) && $request_type == 'post') {
      $this->data_reset($segments[1]);
      return;
    }

    if (count($segments) == 0 && $request_type == 'post') {
      $this->data_upload();
      return;
    }

    if ( $segments[0] == 'fork' && $request_type == 'post') {
      $this->data_fork();
      return;
    }

    if ( $segments[0] == 'edit' && $request_type == 'post') {
      $this->data_edit();
      return;
    }

    if (count($segments) == 2 && $segments[0] == 'features' && is_numeric($segments[1]) && in_array($request_type, $getpost)) {
      $this->data_features($segments[1]);
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'features' && $request_type == 'post') {
      $this->data_features_upload($segments[0]);
      return;
    }

    if (count($segments) == 2 && $segments[0] == 'qualities' && $segments[1] == 'list' && in_array($request_type, $getpost)) {
      $this->data_qualities_list($segments[1]);
      return;
    }

    if (count($segments) == 3 && $segments[0] == 'description' && $segments[1] == 'list' && is_numeric($segments[2]) && in_array($request_type, $getpost)) {
      $this->data_description_list($segments[2]);
      return;
    }

    if (count($segments) == 2 && $segments[0] == 'qualities' && is_numeric($segments[1]) && in_array($request_type, $getpost)) {
      $this->data_qualities($segments[1], $this->config->item('default_evaluation_engine_id'));
      return;
    } elseif(count($segments) == 3 && $segments[0] == 'qualities' && is_numeric($segments[1]) && is_numeric($segments[2]) && in_array($request_type, $getpost)) {
      $this->data_qualities($segments[1], $segments[2]);
      return;
    }

    if (count($segments) == 3 && $segments[0] == 'features' && $segments[1] == 'qualities' && $segments[2] == 'list' && in_array($request_type, $getpost)) {
      $this->feature_qualities_list($segments[2]);
      return;
    }

    if (count($segments) == 3 && $segments[0] == 'features' && $segments[1] == 'qualities' && is_numeric($segments[2]) && in_array($request_type, $getpost)) {
      $this->feature_qualities($segments[2], $this->config->item('default_evaluation_engine_id'));
      return;
    } elseif (count($segments) == 4 && $segments[0] == 'features' && $segments[1] == 'qualities' && is_numeric($segments[2]) && is_numeric($segments[3]) && in_array($request_type, $getpost)) {
      $this->feature_qualities($segments[2], $segments[3]);
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'qualities' && $request_type == 'post') {
      $this->data_qualities_upload($segments[0]);
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'tag' && $request_type == 'post') {
      $this->data_tag($this->input->post('data_id'), $this->input->post('tag'));
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'untag' && $request_type == 'post') {
      $this->data_untag($this->input->post('data_id'), $this->input->post('tag'));
      return;
    }

    if ( $segments[0] == 'topicadd' && $request_type == 'post') {
      $this->data_add_topic($this->input->post('data_id'), $this->input->post('topic'));
      return;
    }

    if ( $segments[0] == 'topicdelete' && $request_type == 'post') {
      $this->data_delete_topic($this->input->post('data_id'), $this->input->post('topic'));
      return;
    }
    
    if (count($segments) == 2 && $segments[0] == 'tag' && $segments[1] == 'list') {
      $this->list_tags('dataset', 'data');
      return;
    }

    if (count($segments) == 2 && $segments[0] == 'status' && $segments[1] == 'update') {
      $this->status_update($this->input->post('data_id'), $this->input->post('status'));
      return;
    }

    $this->returnError(100, $this->version);
  }

  /**
   *@OA\Post(
   *	path="/data/tag",
   *	tags={"data"},
   *	summary="Tag a dataset",
   *	description="Tags a dataset.",
   *	@OA\Parameter(
   *		name="data_id",
   *		in="query",
   *		@OA\Schema(
   *          type="integer"
   *        ),
   *		description="Id of the dataset.",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="tag",
   *		in="query",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="Tag name",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="api_key",
   *		in="query",
   *		description="Api key to authenticate the user",
   *		required=true,
   *        @OA\Schema(
   *          type="string"
   *        )
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="The id of the tagged dataset",
   *		@OA\JsonContent(
   *			type="object",
   *			@OA\Property(
   *				property="data_tag",
   *				ref="#/components/schemas/inline_response_200_2_data_tag",
   *			),
   *			example={
   *			  "data_tag": {
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
  private function data_tag($data_id, $tag) {
    $this->data_tag_untag($data_id, $tag, false);
  }

  /**
   *@OA\Post(
   *	path="/data/untag",
   *	tags={"data"},
   *	summary="Untag a dataset",
   *	description="Untags a dataset.",
   *	@OA\Parameter(
   *		name="data_id",
   *		in="query",
   *		@OA\Schema(
   *          type="integer"
   *        ),
   *		description="Id of the dataset.",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="tag",
   *		in="query",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="Tag name",
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
   *		description="The ID of the untagged dataset",
   *		@OA\JsonContent(
   *			type="object",
   *			@OA\Property(
   *				property="data_untag",
   *				ref="#/components/schemas/inline_response_200_3_data_untag",
   *			),
   *			example={
   *			  "data_untag": {
   *			    "id": "2"
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n475 - Please give entity_id {data_id, flow_id, run_id} and tag. In order to remove a tag, please upload the entity id (either data_id, flow_id, run_id) and tag (the name of the tag).\n476 - Entity {dataset, flow, run} not found. The provided entity_id {data_id, flow_id, run_id} does not correspond to an existing entity.\n477 - Tag not found. The provided tag is not associated with the entity {dataset, flow, run}.\n478 - Tag is not owned by you. The entity {dataset, flow, run} was tagged by another user. Hence you cannot delete it.\n479 - Internal error removing the tag. Please contact OpenML Team.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function data_untag($data_id, $tag) {
    $this->data_tag_untag($data_id, $tag, true);
  }

  private function data_tag_untag($data_id,$tag, $do_untag) {
    // forward action to superclass
    $this->entity_tag_untag('dataset', $data_id, $tag, $do_untag, 'data');
  }

  /**
   *@OA\Get(
   *	path="/data/list/{filters}",
   *	tags={"data"},
   *	summary="List and filter datasets",
   *	description="List datasets, possibly filtered by a range of properties. Any number of properties can be combined by listing them one after the other in the form '/data/list/{filter}/{value}/{filter}/{value}/...' Returns an array with all datasets that match the constraints.",
   *	@OA\Parameter(
   *		name="filters",
   *		in="path",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="Any combination of these filters
  /limit/{limit}/offset/{offset} - returns only {limit} results starting from result number {offset}. Useful for paginating results. With /limit/5/offset/10, results 11..15 will be returned. Both limit and offset need to be specified.
  /status/{status} - returns only datasets with a given status, either 'active', 'deactivated', or 'in_preparation'.
  /tag/{tag} - returns only datasets tagged with the given tag.
  /{data_quality}/{range} - returns only tasks for which the underlying datasets have certain qualities. {data_quality} can be data_id, data_name, data_version, number_instances, number_features, number_classes, number_missing_values. {range} can be a specific value or a range in the form 'low..high'. Multiple qualities can be combined, as in 'number_instances/0..50/number_features/0..10'.
  ",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="A list of datasets with the given task",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/DataList",
   *			example={
   *			  "data": {
   *			    "dataset": {
   *			      {
   *			        "did":"1",
   *			        "name":"anneal",
   *			        "status":"active",
   *			        "format":"ARFF",
   *			        "quality":{
   *			          {
   *			            "name":"MajorityClassSize",
   *			            "value":"684"
   *			          },
   *			          {
   *			            "name":"MaxNominalAttDistinctValues",
   *			            "value":"10.0"
   *			          },
   *			          {
   *			            "name":"MinorityClassSize"
   *			            ,"value":"0"
   *			          },
   *			          {
   *			            "name":"NumBinaryAtts",
   *			            "value":"14.0"
   *			          },
   *			          {
   *			            "name":"NumberOfClasses",
   *			            "value":"6"
   *			          },
   *			          {
   *			            "name":"NumberOfFeatures",
   *			            "value":"39"
   *			          },
   *			          {
   *			            "name":"NumberOfInstances",
   *			            "value":"898"
   *			          },
   *			          {
   *			            "name":"NumberOfInstancesWithMissingValues",
   *			            "value":"0"
   *			          },
   *			          {
   *			            "name":"NumberOfMissingValues",
   *			            "value":"0"
   *			          },
   *			          {
   *			            "name":"NumberOfNumericFeatures",
   *			            "value":"6"
   *			          },
   *			          {
   *			            "name":"NumberOfSymbolicFeatures",
   *			            "value":"32"
   *			          }
   *			        }
   *			      }
   *			    }
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n370 - Illegal filter specified.\n371 - Filter values/ranges not properly specified.\n372 - No results. There where no matches for the given constraints.\n373 - Can not specify an offset without a limit.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function data_list($segs) {
    $legal_filters = array('tag', 'status', 'limit', 'offset', 'data_id', 'data_name', 'data_version', 'uploader', 'number_instances', 'number_features', 'number_classes', 'number_missing_values');

    list($query_string, $illegal_filters) = $this->parse_filters($segs, $legal_filters);
    if (count($illegal_filters) > 0) {
      $this->returnError(370, $this->version, $this->openmlGeneralErrorCode, 'Legal filter operators: ' . implode(',', $legal_filters) .'. Found illegal filter(s): ' . implode(', ', $illegal_filters));
      return;
    }

    $illegal_filter_inputs = $this->check_filter_inputs($query_string, $legal_filters, array('tag', 'status', 'data_name', 'number_instances', 'number_features', 'number_classes', 'number_missing_values'));
    if (count($illegal_filter_inputs) > 0) {
      $this->returnError(371, $this->version, $this->openmlGeneralErrorCode, 'Filters with illegal values: ' . implode(',', $illegal_filter_inputs));
      return;
    }

    $tag = element('tag', $query_string, null);
    $name = element('data_name', $query_string, null);
    $data_id = element('data_id', $query_string, null);
    $uploader = element('uploader', $query_string, null);
    $version = element('data_version', $query_string, null);
    $status = element('status', $query_string, null);
    $limit = element('limit', $query_string, null);
    $offset = element('offset', $query_string, null);
    $nr_insts = element('number_instances', $query_string, null);
    $nr_feats = element('number_features', $query_string, null);
    $nr_class = element('number_classes', $query_string, null);
    $nr_miss = element('number_missing_values', $query_string, null);

    if ($offset && !$limit) {
      $this->returnError(373, $this->version);
      return;
    }

    $where_tag = $tag === null ? '' : ' AND `d`.`did` IN (select id from dataset_tag where tag="' . $tag . '") ';
    $where_did = $data_id === null ? '' : ' AND `d`.`did` IN ('. $data_id . ') ';
    $where_name = $name === null ? '' : ' AND `name` = "' . $name . '"';
    $where_uploader = $uploader === null ? '' : ' AND `uploader` IN ("' . $uploader . '")';
    $where_version = $version === null ? '' : ' AND `version` = "' . $version . '" ';
    $where_insts = $nr_insts === null ? '' : ' AND `d`.`did` IN (select data from data_quality dq where quality="NumberOfInstances" and value ' . (strpos($nr_insts, '..') !== false ? 'BETWEEN ' . str_replace('..',' AND ',$nr_insts) : '= '. $nr_insts) . ') ';
    $where_feats = $nr_feats === null ? '' : ' AND `d`.`did` IN (select data from data_quality dq where quality="NumberOfFeatures" and value ' . (strpos($nr_feats, '..') !== false ? 'BETWEEN ' . str_replace('..',' AND ',$nr_feats) : '= '. $nr_feats) . ') ';
    $where_class = $nr_class === null ? '' : ' AND `d`.`did` IN (select data from data_quality dq where quality="NumberOfClasses" and value ' . (strpos($nr_class, '..') !== false ? 'BETWEEN ' . str_replace('..',' AND ',$nr_class) : '= '. $nr_class) . ') ';
    $where_miss = $nr_miss === null ? '' : ' AND `d`.`did` IN (select data from data_quality dq where quality="NumberOfMissingValues" and value ' . (strpos($nr_miss, '..') !== false ? 'BETWEEN ' . str_replace('..',' AND ',$nr_miss) : '= '. $nr_miss) . ') ';
    // by default, only return active datasets
    $status_sql_variable = 'IFNULL(`s`.`status`, \'' . $this->config->item('default_dataset_status') . '\')';
    $where_status = $status === null ? ' AND ' . $status_sql_variable . ' = "active" ' : ($status != "all" ? ' AND ' . $status_sql_variable . ' = "'. $status . '" ' : '');
    $where_total = $where_tag . $where_did . $where_name . $where_version . $where_uploader . $where_insts . $where_feats . $where_class . $where_miss . $where_status;

    $where_limit = $limit === null ? '' : ' LIMIT ' . $limit;
    if($limit && $offset){
      $where_limit =  ' LIMIT ' . $offset . ',' . $limit;
    }

    $sql = 'SELECT d.*, ' . $status_sql_variable . ' AS `status` '.
           'FROM dataset d ' .
           'LEFT JOIN (SELECT `did`, MAX(`status`) AS `status` FROM `dataset_status` GROUP BY `did`) s ON d.did = s.did ' .
           'WHERE (visibility = "public" or uploader='.$this->user_id.') '. $where_total . $where_limit;

    $datasets_res = $this->Dataset->query($sql);
    if( is_array( $datasets_res ) == false || count( $datasets_res ) == 0 ) {
      $this->returnError( 372, $this->version );
      return;
    }

    // make associative
    $datasets = array();
    foreach( $datasets_res as $dataset ) {
      $dataset->qualities = array();
      $datasets[$dataset->did] = $dataset;
    }

    # JvR: This is a BAD idea and this will break in the future, when OpenML grows.
    $sql =
      'SELECT data, quality, value FROM data_quality ' .
      'WHERE `data` IN (' . implode(',', array_keys($datasets)) . ') ' .
      'AND evaluation_engine_id = ' . $this->config->item('default_evaluation_engine_id') . ' ' .
      'AND quality IN ("' . implode('","', $this->config->item('basic_qualities')) . '") ' .
      'AND value IS NOT NULL ' .
      'ORDER BY `data`;';
    $dq = $this->Data_quality->query($sql);

    if ($dq != false) {
      foreach($dq as $quality) {
        $datasets[$quality->data]->qualities[$quality->quality] = $quality->value;
      }
    }

    $this->xmlContents('data', $this->version, array('datasets' => $datasets));
  }
  
  private function data_fork() {
    // get data id
    $data_id = $this->input->post('data_id');
    // If data id is not given
    if( $data_id == false ) {
      $this->returnError( 1070, $this->version );
      return;
    }
    // If dataset does not exist
    $dataset = $this->Dataset->getById( $data_id );
    if( $dataset == false ) {
      $this->returnError( 1071, $this->version );
      return;
    }
    // create a copy
    $dataset->uploader = $this->user_id;   
    $latest_version = $this->Dataset-> getWhereSingle('`name` = "' . $dataset->name . '"', 'CAST(`version` AS DECIMAL) DESC');
    $dataset->version = $latest_version->version + 1;
    unset($dataset->did);
    $data_id = $this->Dataset->insert($dataset);
    if (!$data_id) {
      $this->returnError(1072, $this->version);
      return;
    }

    // update elastic search index.  
    try {
      $this->elasticsearch->index('data', $data_id);
    } catch (Exception $e) {
      $this->returnError(105, $this->version, $this->openmlGeneralErrorCode, $e->getMessage());
      return;
    }

    // Return data id, for user to verify changes
    $this->xmlContents( 'data-fork', $this->version, array( 'id' => $data_id) );
  }

  private function data_edit() {
    // get data id
    $data_id = $this->input->post('data_id');
    // get edit parameters as xml
    $xsdFile = xsd('openml.data.edit', $this->controller, $this->version);

    if($this->input->post('edit_parameters')) {
      // get fields from string upload
      $edit_parameters = $this->input->post('edit_parameters', false);
      if(validateXml($edit_parameters, $xsdFile, $xmlErrors, false ) == false) {
        $this->returnError(1060, $this->version, $this->openmlGeneralErrorCode, $xmlErrors);
        return;
      }
      $xml = simplexml_load_string( $edit_parameters );
    } elseif (isset($_FILES['edit_parameters'])) {
      $uploadError = '';
      $xmlErrors = '';
      if (check_uploaded_file($_FILES['edit_parameters'], false, $uploadError) == false) {
        $this->returnError(1061, $this->version, $this->openmlGeneralErrorCode, $uploadError);
      }
      // get fields from file upload
      $edit_parameters = $_FILES['edit_parameters'];

      if (validateXml($edit_parameters['tmp_name'], $xsdFile, $xmlErrors) == false) {
        $this->returnError(1060, $this->version, $this->openmlGeneralErrorCode, $xmlErrors);
        return;
      }
      $xml = simplexml_load_file($edit_parameters['tmp_name']);
    } else {
      $this->returnError(1061, $this->version);
      return;
    }

    // create an array of update fields for the update
    $update_fields = array();
    foreach($xml->children('oml', true) as $input) {
      // iterate over all fields, does not check for legal fields, as it wont match the xsd.  
      $name = $input->getName() . '';
      $update_fields[$name] = $input . '';
    }

    // If data id is not given
    if( $data_id == false ) {
      $this->returnError( 1062, $this->version );
      return;
    }

    // If dataset does not exist
    $dataset = $this->Dataset->getById( $data_id );
    if( $dataset == false ) {
      $this->returnError( 1063, $this->version );
      return;
    }

    // If all the fields are false, there is nothing to update, return error 
    if(!$update_fields) {
      $this->returnError( 1064, $this->version );
      return;
    }

    // If critical fields need to be edited
    if (isset($update_fields['default_target_attribute']) || isset($update_fields['row_id_attribute']) || isset($update_fields['ignore_attribute'])) {
      # Only owner can edit critical features
      if($dataset->uploader != $this->user_id and !$this->user_has_admin_rights) {
        $this->returnError(1065, $this->version);
        return;
      }
      # Only datasets without tasks can allow critical feature edits
      $tasks = $this->Task->getTasksWithValue( array( 'source_data' => $dataset->did ) );
      if( $tasks !== false ) {
        $this->returnError( 1066, $this->version );
        return;
      }
    }

    // Add description in the description table as a new version
    if (isset($update_fields['description'])) {
      $description_record = $this->Dataset_description->getWhereSingle('did =' . $data_id, 'version DESC');
      $version_new = $description_record->version + 1;
      $desc = array(
        'did'=>$data_id,
        'version' => $version_new,
        'description'=>$update_fields['description'],
        'uploader' => $dataset->uploader
      );
      unset($update_fields['description']);
      $desc_id = $this->Dataset_description->insert($desc);
      if (!$desc_id) {
        $this->returnError(1067, $this->version);
        return;
      }
    }

    if ($update_fields) {
      $update_result = $this->Dataset->update($data_id, $update_fields);
      // If result returns error
      if($update_result == false) {
        $this->returnError(1068, $this->version);
        return;
      }
    }
    // update elastic search index.  
    try {
      $this->elasticsearch->index('data', $data_id);
    } catch (Exception $e) {
      $this->returnError(105, $this->version, $this->openmlGeneralErrorCode, $e->getMessage());
    
    }

    // Return data id, for user to verify changes    
    $this->xmlContents( 'data-edit', $this->version, array( 'id' => $data_id) );
  }

  private function data_description_list($data_id) {
  // Get descriptions for given id
    $description_records = $this->Dataset_description->getWhere('did =' . $data_id, 'version DESC');
    if( is_array( $description_records ) == false || count( $description_records ) == 0 ) {
      $this->returnError( 1090, $this->version );
      return;
    }
    // Return history
    $this->xmlContents( 'data-description-list', $this->version, array('descriptions' => $description_records));
  }

  /**
   *@OA\Get(
   *	path="/data/{id}",
   *	tags={"data"},
   *	summary="Get dataset description",
   *	description="Returns information about a dataset. The information includes the name, information about the creator, URL to download it and more.",
   *	@OA\Parameter(
   *		name="id",
   *		in="path",
   *		@OA\Schema(
   *          type="integer"
   *        ),
   *		description="Id of the dataset.",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="A dataset description",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Data",
   *			example={
   *			  "data_set_description": {
   *			    "id": "1",
   *			    "name": "anneal",
   *			    "version": "2",
   *			    "description": "...",
   *			    "format": "ARFF",
   *			    "upload_date": "2014-04-06 23:19:20",
   *			    "licence": "Public",
   *			    "url": "https://www.openml.org/data/download/1/dataset_1_anneal.arff",
   *			    "file_id": "1",
   *			    "default_target_attribute": "class",
   *			    "version_label": "2",
   *			    "tag": {
   *			      "study_1",
   *			      "uci"
   *			    },
   *			    "visibility": "public",
   *			    "original_data_url": "https://www.openml.org/d/2",
   *			    "status": "active",
   *			    "md5_checksum": "d01f6ccd68c88b749b20bbe897de3713"
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned\n110 - Please provide data_id.\n111 - Unknown dataset. Data set description with data_id was not found in the database.\n112 - No access granted. This dataset is not shared with you.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function data($data_id) {
    if( $data_id == false ) {
      $this->returnError( 110, $this->version );
      return;
    }

    $dataset = $this->Dataset->getById( $data_id );
    if( $dataset === false ) {
      $this->returnError( 111, $this->version );
      return;
    }

    if($dataset->visibility != 'public' &&
       $dataset->uploader != $this->user_id &&
       !$this->user_has_admin_rights) {
      $this->returnError( 112, $this->version );
      return;
    }

    // overwrite url field
    if ($dataset->file_id != NULL) {
      $dataset->url = BASE_URL . 'data/v1/download/' . $dataset->file_id . '/' . htmlspecialchars($dataset->name) . '.' . strtolower($dataset->format);
    }


    $file = $this->File->getById($dataset->file_id);
    if (!$file) {
      $this->returnError(113, $this->version);
      return;
    }

    $dataset->md5_checksum = $file->md5_hash;

    $tags = $this->Dataset_tag->getColumnWhere('tag', 'id = ' . $dataset->did);
    $dataset->tag = $tags != false ? '"' . implode( '","', $tags ) . '"' : array();

    $description_record = $this->Dataset_description->getWhereSingle('did =' . $data_id, 'version DESC');
    $dataset->description_version = $description_record->version;
    $dataset->description = $description_record->description;

    foreach( $this->xml_fields_dataset['csv'] as $field ) {
      $dataset->{$field} = getcsv( $dataset->{$field} );
    }

    $data_processed = $this->Data_processed->getById(array($data_id, $this->config->item('default_evaluation_engine_id')));
    $relevant_fields = array('processing_date', 'error', 'warning');
    foreach ($relevant_fields as $field) {
      if ($data_processed !== false) {
        $dataset->{$field} = $data_processed->{$field};
      } else {
        $dataset->{$field} = null;
      }
    }

    $dataset->status = $this->config->item('default_dataset_status');
    $data_status = $this->Dataset_status->getWhereSingle('did =' . $data_id, 'status_date DESC');
    if ($data_status != false) {
      $dataset->status = $data_status->status;
    }
     
    $dataset->minio_url = 'https://openml1.win.tue.nl/dataset' . $data_id . '/dataset_' . $data_id . '.pq';
    $this->xmlContents( 'data-get', $this->version, $dataset );
  }

  private function data_reset($data_id) {
    $dataset = $this->Dataset->getById($data_id);
    if ($dataset == false) {
      $this->returnError(1021, $this->version);
      return;
    }

    if($dataset->uploader != $this->user_id and !$this->user_has_admin_rights) {
      $this->returnError(1022, $this->version);
      return;
    }

    $result = $this->Data_processed->deleteWhere('`did` = "' . $dataset->did . '" ');

    if ($result == false) {
      $this->returnError(1023, $this->version);
      return;
    }
    $this->xmlContents('data-reset', $this->version, array('dataset' => $dataset));
  }

  /**
   *@OA\Delete(
   *	path="/data/{id}",
   *	tags={"data"},
   *	summary="Delete dataset",
   *	description="Deletes a dataset. Upon success, it returns the ID of the deleted dataset.",
   *	@OA\Parameter(
   *		name="id",
   *		in="path",
   *		@OA\Schema(
   *          type="integer"
   *        ),
   *		description="Id of the dataset.",
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
   *		description="ID of the deleted dataset",
   *		@OA\JsonContent(
   *			type="object",
   *			@OA\Property(
   *				property="data_delete",
   *				ref="#/components/schemas/inline_response_200_data_delete",
   *			),
   *			example={
   *			  "data_delete": {
   *			    "id": "4328"
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned\n- 350 - Please provide API key. In order to remove your content, please authenticate.\n- 351 - Authentication failed. The API key was not valid. Please try to login again, or contact api administrators.\n- 352 - Dataset does not exists. The data ID could not be linked to an existing dataset.\n- 353 - Dataset is not owned by you. The dataset is owned by another user. Hence you cannot delete it.\n- 354 - Dataset is in use by other content. Can not be deleted. The data is used in tasks or runs. Delete other content before deleting this dataset.\n- 355 - Deleting dataset failed. Deleting the dataset failed. Please contact support team.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */

  private function data_add_topic($id, $topic) {
    # Data id and topic are required
    if ($id == false || $topic == false) {
      $this->returnError(1080, $this->version);
      return false;
    }
    # If dataset does not exist
    $dataset = $this->Dataset->getById($id);
    if($dataset == false) {
      $this->returnError(1081, $this->version);
      return;
    }
    # Restrict only to admin
    if(!$this->user_has_admin_rights) {
      $this->returnError(1082, $this->version);
      return;
    }
    # Check if topic and id combination exists
    $topics = $this->Dataset_topic->getColumnWhere('topic', 'id = ' . $id);
      if($topics != false && in_array($topic, $topics)) {
        $this->returnError(1083, $this->version);
        return false;
    }
    $currentTime = now();
    $topic_data = array(
      'id' => $id,
      'topic' => $topic,
      'uploader' => $this->user_id,
      'date' => $currentTime
    );
    # Insert into DB
    $res = $this->Dataset_topic->insert($topic_data);
    if ($res == false) {
        $this->returnError(1084, $this->version);
        return false;
      }

     try {
      //update index
      $this->elasticsearch->update_topics($id);
      
    } catch (Exception $e) {
      $this->returnError(105, $this->version, $this->openmlGeneralErrorCode, $e->getMessage(), false, $surpressOutput);
      return false;
    }
    $this->xmlContents( 'data-topic', $this->version, array( 'id' => $id) );
  }


  private function data_delete_topic($id, $topic) {
    # Data id and topic are required
    if ($id == false || $topic == false) {
      $this->returnError(1080, $this->version);
      return false;
    }
    # If dataset does not exist
    $dataset = $this->Dataset->getById($id);
    if($dataset == false) {
      $this->returnError(1081, $this->version);
      return;
    }
    # Restrict only to admin
    if(!$this->user_has_admin_rights) {
      $this->returnError(1082, $this->version);
      return;
    }
    $topic_record = $this->Dataset_topic->getWhereSingle('id = ' . $id . ' AND topic = "' . $topic . '"');
    if ($topic_record == false) {
      $this->returnError(1085, $this->version);
      return false;
    }
   $this->Dataset_topic->delete(array($id, $topic));
   try {
      //update index
      $this->elasticsearch->update_topics($id);
      
    } catch (Exception $e) {
      $this->returnError(105, $this->version, $this->openmlGeneralErrorCode, $e->getMessage());
      return false;
    }
    $this->xmlContents( 'data-topic', $this->version, array( 'id' => $id) );
  }
    
    
  private function data_delete($data_id) {

    $dataset = $this->Dataset->getById( $data_id );
    if( $dataset == false ) {
      $this->returnError( 352, $this->version );
      return;
    }

    if($dataset->uploader != $this->user_id and !$this->user_has_admin_rights) {
      $this->returnError( 353, $this->version );
      return;
    }

    $tasks = $this->Task->getTasksWithValue( array( 'source_data' => $dataset->did ) );

    if( $tasks !== false ) {
      //$task_ids = array();
      //foreach( $tasks as $t ) { $task_ids[] = $t->task_id; }

      //$runs = $this->Run->getWhere( 'task_id IN ("'.implode('","', $task_ids).'")' );


      //if( $runs ) {
        $this->returnError( 354, $this->version );
        return;
      //}
    }

    $result = $this->Dataset->delete($dataset->did);

    if ($result == false) {
      $this->returnError(355, $this->version);
      return;
    }

    try {
      $this->elasticsearch->delete('data', $data_id);
      $this->elasticsearch->index('user', $this->user_id);
    } catch (Exception $e) {
      $additionalMsg = get_class() . '.' . __FUNCTION__ . ':' . $e->getMessage();
      $this->returnError(105, $this->version, $this->openmlGeneralErrorCode, $additionalMsg);
      return;
    }

    $this->xmlContents( 'data-delete', $this->version, array( 'dataset' => $dataset ) );
  }


  /**
   *@OA\Post(
   *	path="/data",
   *	tags={"data"},
   *	summary="Upload dataset",
   *	description="Uploads a dataset. Upon success, it returns the data id.",
   *	@OA\Parameter(
   *		name="description",
   *		in="query",
   *		@OA\Schema(
   *          type="file"
   *        ),
   *		description="An XML file describing the dataset. Only name, description, and data format are required. Also see the [XSD schema](https://www.openml.org/api/v1/xsd/openml.data.upload) and an [XML example](https://www.openml.org/api/v1/xml_example/data).",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="dataset",
   *		in="query",
   *		@OA\Schema(
   *          type="file"
   *        ),
   *		description="The actual dataset, being an ARFF file.",
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
   *		description="Id of the uploaded dataset",
   *		@OA\JsonContent(
   *			type="object",
   *			@OA\Property(
   *				property="upload_data_set",
   *				ref="#/components/schemas/inline_response_200_1_upload_data_set",
   *			),
   *			example={
   *			  "upload_data_set": {
   *			    "id": "4328"
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n130 - Problem with file uploading. There was a problem with the file upload.\n131 - Problem validating uploaded description file. The XML description format does not meet the standards.\n132 - Failed to move the files. Internal server error, please contact API administrators.\n133 - Failed to make checksum of datafile. Internal server error, please contact API administrators.\n134 - Failed to insert record in database. Internal server error, please contact API administrators.\n135 - Please provide description xml.\n136 - File failed format verification. The uploaded file is not valid according to the selected file format. Please check the file format specification and try again.\n137 - Please provide API key. In order to share content, please log in or provide your API key.\n138 - Authentication failed. The API key was not valid. Please try to login again, or contact API administrators\n139 - Combination name / version already exists. Leave version out for auto increment\n140 - Both dataset file and dataset url provided. The system is confused since both a dataset file (post) and a dataset url (xml) are provided. Please remove one.\n141 - Neither dataset file or dataset url are provided. Please provide either a dataset file as POST variable, or a dataset url in the description XML.\n142 - Error in processing arff file. Can be a syntax error, or the specified target feature does not exists. For now, we only check on arff files. If a dataset is claimed to be in such a format, and it can not be parsed, this error is returned.\n143 - Suggested target feature not legal. It is possible to suggest a default target feature (for predictive tasks). However, it should be provided in the data.\n144 - Unable to update dataset. The dataset with id could not be found in the database. If you upload a new dataset, unset the id.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function data_upload() {
    // get correct description
    $xsdFile = xsd('openml.data.upload', $this->controller, $this->version);

    if($this->input->post('description')) {
      // get description from string upload
      $description = $this->input->post('description', false);
      if(validateXml($description, $xsdFile, $xmlErrors, false ) == false) {
        if (DEBUG_XSD_EMAIL) {
          $to = $this->user_email;
          $server = 'Server:' . $_SERVER['SERVER_ADDR'] . ':' . $_SERVER['SERVER_PORT'];
          $subject = 'OpenML Data Upload DEBUG message (' . $server . ')';
          $content = $server . "\nUploaded Post Field\nXSD Validation Message: " . $xmlErrors . "\n=====BEGIN XML=====\n" . file_get_contents($description['tmp_name']);
          sendEmail($to, $subject, $content,'text');
        }
        $this->returnError(131, $this->version, $this->openmlGeneralErrorCode, $xmlErrors);
        return;
      }
      $xml = simplexml_load_string( $description );
    } elseif (isset($_FILES['description'])) {
      $uploadError = '';
      $xmlErrors = '';
      if (check_uploaded_file($_FILES['description'], false, $uploadError) == false) {
        $this->returnError(135, $this->version, $this->openmlGeneralErrorCode, $uploadError);
        return;
      }
      // get description from file upload
      $description = $_FILES['description'];

      if (validateXml($description['tmp_name'], $xsdFile, $xmlErrors) == false) {
        if (DEBUG_XSD_EMAIL) {
          $to = $this->user_email;
          $server = 'Server:' . $_SERVER['SERVER_ADDR'] . ':' . $_SERVER['SERVER_PORT'];
          $subject = 'OpenML Data Upload DEBUG message (' . $server . ')';
          $content = $server . "\nFilename: " . $description['name'] . "\nXSD Validation Message: " . $xmlErrors . "\n=====BEGIN XML=====\n" . file_get_contents($description['tmp_name']);
          sendEmail($to, $subject, $content,'text');
        }
        $this->returnError(131, $this->version, $this->openmlGeneralErrorCode, $xmlErrors);
        return;
      }
      $xml = simplexml_load_file($description['tmp_name']);
    } else {
      $this->returnError(135, $this->version);
      return;
    }

    $format = strtolower($xml->children('oml', 'true')->format);
    if (!in_array($format, $this->legal_formats)) {
      $this->returnError(133, $this->version);
      return;
    }

    // determine level of access control
    $access_control = 'public';
    $access_control_option = $xml->children('oml', true)->{'visibility'};
    if ($access_control_option != false) {
      $access_control = $access_control_option;
    }

    // obtain some other fields
    $name = '' . $xml->children('oml', true)->{'name'};
    $version = $this->Dataset->incrementVersionNumber($name);

    //check and register the data files, return url
    $file_id = null;
    $datasetUrlProvided = property_exists($xml->children('oml', true), 'url');
    $datasetFileProvided = isset($_FILES['dataset']);
    if ($datasetUrlProvided && $datasetFileProvided) {
      $this->returnError(140, $this->version);

      return;
    } elseif($datasetFileProvided) {
      $message = '';
      if (!check_uploaded_file($_FILES['dataset'], false, $message)) {
        $this->returnError(130, $this->version, $this->openmlGeneralErrorCode, 'File dataset: ' . $message);
        return;
      }

      $uploadedFileCheck = ARFFcheck($_FILES['dataset']['tmp_name'], 1000);
      if ($uploadedFileCheck !== true) {
        $this->returnError(145, $this->version, $this->openmlGeneralErrorCode, 'Arff error in dataset file: ' . $uploadedFileCheck);
        return;
      }

      $to_folder = $this->data_folders['dataset'];
      $file_id = $this->File->register_uploaded_file($_FILES['dataset'], $to_folder, $this->user_id, 'dataset', $access_control);
      if ($file_id === false) {
        $this->returnError(132, $this->version);
        return;
      }

      $file_record = $this->File->getById($file_id);
      $destinationUrl = $this->data_controller . 'download/' . $file_id . '/' . $file_record->filename_original;
    } elseif ($datasetUrlProvided) {
      $destinationUrl = '' . $xml->children('oml', true)->url;

      $uploadedFileCheck = ARFFcheck($destinationUrl, 1000);
      if ($uploadedFileCheck !== true) {
        $this->returnError(145, $this->version, $this->openmlGeneralErrorCode, 'Arff error in dataset url: ' . $uploadedFileCheck);
        return;
      }

      $file_id = $this->File->register_url($destinationUrl, $name . '.arff', 'arff', $this->user_id, $access_control);
      if ($file_id === false) {
        $this->returnError(136, $this->version);
        return;
      }

      $file_record = $this->File->getById($file_id);
      $destinationUrl = $this->data_controller . 'download/' . $file_id . '/' . $file_record->filename_original;
    } else {
      $this->returnError(141, $this->version);
      return;
    }

    // ***** NEW DATASET *****

    $dataset = array(
      'name' => $name,
      'version' => $version,
      'url' => $destinationUrl,
      'upload_date' => now(),
      'last_update' => now(),
      'uploader' => $this->user_id,
      'isOriginal' => 'true',
      'file_id' => $file_id,
    );

    // extract all other necessary info from the XML description
    $dataset = all_tags_from_xml(
      $xml->children('oml', true),
      $this->xml_fields_dataset, $dataset);

    // handle tags
    $tags = array();
    if (array_key_exists('tag', $dataset)) {
      $tags = str_getcsv($dataset['tag']);
      unset($dataset['tag']);
    }

    $desc = array(
      'version' => 1,
      'description'=>$dataset['description'],
      'uploader' => $this->user_id
    );

    unset($dataset['description']);
 
    /* * * *
     * THE ACTUAL INSERTION
     * * * */
    $id = $this->Dataset->insert($dataset);
    if (!$id) {
      $this->returnError(134, $this->version);
      return;
    }
    $desc['did'] = $id;
    $desc_id = $this->Dataset_description->insert($desc);
    if (!$desc_id) {
      $this->returnError(134, $this->version);
      return;
    }

    // try to move the file to a new directory. If it fails, the dataset is
    // still valid, but we probably want to make some mechanism to inform administrators
    if ($file_record->type != 'url') {
      $subdirectory = floor($id / $this->content_folder_modulo) * $this->content_folder_modulo;
      $to_folder = $this->data_folders['dataset'] . '/' . $subdirectory . '/' . $id . '/';
      $this->File->move_file($file_id, $to_folder);
    }

    // try making the ES stuff
    try {
      // update elastic search index.
      $this->elasticsearch->index('data', $id);

      // update counters
      $this->elasticsearch->index('user', $this->user_id);
    } catch (Exception $e) {
      // TODO: should log
    }

    // insert tags. This relies on the ES record to exist.
    foreach ($tags as $tag) {
      // function relies on ES to know the document ID
      $success = $this->entity_tag_untag('dataset', $id, $tag, false, 'data', true);
      // if tagging went wrong, the error is surpressed (as this is usually ES)
    }

    // create initial wiki page
    $this->wiki->export_to_wiki($id);

    // create
    $this->xmlContents('data-upload', $this->version, array('id' => $id));
  }

  /**
   *@OA\Post(
   *	path="/data/status/update/",
   *	tags={"data"},
   *	summary="Change the status of a dataset",
   *	description="Change the status of a dataset, either 'active' or 'deactivated'",
   *	@OA\Parameter(
   *		name="data_id",
   *		in="query",
   *		@OA\Schema(
   *          type="integer"
   *        ),
   *		description="Id of the dataset.",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="status",
   *		in="query",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="The status on which to filter the results, either 'active' or 'deactivated'.",
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
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n691 - Illegal status\n692 - Dataset does not exists\n693 - Dataset is not owned by you\n694 - Illegal status transition\n695 - Status update failed\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function status_update($data_id, $status) {
    // in_preparation is not a legal status to change to
    $legal_status = array('active', 'deactivated');
    if (!in_array($status, $legal_status)) {
      $this->returnError(691, $this->version);
      return;
    }

    $dataset = $this->Dataset->getById($data_id);
    if ($dataset == false) {
      $this->returnError(692, $this->version);
      return;
    }

    if ($dataset->uploader != $this->user_id and !$this->user_has_admin_rights) {
      $this->returnError(693, $this->version);
      return;
    }

    if ($status == 'active' && !$this->user_has_admin_rights) {
      $this->returnError(696, $this->version);
      return;
    }

    $status_record = $this->Dataset_status->getWhereSingle('did = ' . $data_id, 'status DESC');
    $in_preparation = $this->config->item('default_dataset_status');
    if ($status_record == false) {
      $old_status = $in_preparation;
    } else {
      $old_status = $status_record->status;
    }

    $record = array(
      'did' => $data_id,
      'status' => $status,
      'status_date' => now(),
      'user_id' => $this->user_id
    );

    if (
        ($old_status == $in_preparation && $status == 'active') ||
        ($old_status == $in_preparation && $status == 'deactivated') ||
        ($old_status == 'active' && $status == 'deactivated')
       ) {
      $this->Dataset_status->insert($record);
    } elseif ($old_status == 'deactivated' && $status == 'active') {
      $this->Dataset_status->delete(array($data_id, 'deactivated'));

      // see if the dataset is still active
      $status_record = $this->Dataset_status->getWhereSingle('did = ' . $data_id, 'status DESC');

      $result = true;
      if (!$status_record || $status_record->status != 'active') {
        $result = $this->Dataset_status->insert($record);
      }
      if (!$result) {
        $this->returnError(695, $this->version);
        return;
      }
    } else {
      $this->returnError(694, $this->version);
      return;
    }

    $this->xmlContents('data-status-update', $this->version, array('did' => $data_id, 'status' => $status));
  }

  /**
   *@OA\Get(
   *	path="/data/features/{id}",
   *	tags={"data"},
   *	summary="Get data features",
   *	description="Returns the features of a dataset.",
   *	@OA\Parameter(
   *		name="id",
   *		in="path",
   *		@OA\Schema(
   *          type="integer"
   *        ),
   *		description="Id of the dataset.",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="All the features of the dataset",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/DataFeatures",
   *			example={
   *			  "data_features": {
   *			    "feature": {
   *			      {
   *			        "index": "0",
   *			        "name": "sepallength",
   *			        "data_type": "numeric",
   *			        "is_target": "false",
   *			        "is_ignore": "false",
   *			        "is_row_identifier": "false"
   *			      },
   *			      {
   *			        "index": "1",
   *			        "name": "sepalwidth",
   *			        "data_type": "numeric",
   *			        "is_target": "false",
   *			        "is_ignore": "false",
   *			        "is_row_identifier": "false"
   *			      },
   *			      {
   *			        "index": "2",
   *			        "name": "petallength",
   *			        "data_type": "numeric",
   *			        "is_target": "false",
   *			        "is_ignore": "false",
   *			        "is_row_identifier": "false"
   *			      },
   *			      {
   *			        "index": "3",
   *			        "name": "petalwidth",
   *			        "data_type": "numeric",
   *			        "is_target": "false",
   *			        "is_ignore": "false",
   *			        "is_row_identifier": "false"
   *			      },
   *			      {
   *			        "index": "4",
   *			        "name": "class",
   *			        "data_type": "nominal",
   *			        "is_target": "true",
   *			        "is_ignore": "false",
   *			        "is_row_identifier": "false"
   *			      }
   *			    }
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n270 - Please provide dataset ID.\n271 - Unknown dataset. Data set with the given data ID was not found (or is not shared with you).\n272 - No features found. The dataset did not contain any features, or we could not extract them.\n273 - Dataset not processed yet. The dataset was not processed yet, features are not yet available. Please wait for a few minutes.\n274 - Dataset processed with error. The feature extractor has run into an error while processing the dataset. Please check whether it is a valid supported file. If so, please contact the API admins.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function data_features($data_id) {
    $dataset = $this->Dataset->getById($data_id);
    if ($dataset === false) {
      $this->returnError(271, $this->version);
      return;
    }

    if ($dataset->visibility != 'public' && !($dataset->uploader == $this->user_id || $this->user_has_admin_rights)) {
      $this->returnError(275, $this->version); // Add special error code for this case?
      return;
    }

    $data_processed = $this->Data_processed->getById(array($data_id, $this->config->item('default_evaluation_engine_id')));

    if ($data_processed == false) {
      $this->returnError(273, $this->version);
      return;
    }

    $dataset->features = $this->Data_feature->getWhere('did = "' . $dataset->did . '"');
    $dataset->features_values = $this->Data_feature_value->getWhere('did = "' . $dataset->did . '"');
    $index_values = array();
    if ($dataset->features_values) {
      foreach($dataset->features_values as $val) {
        if (!array_key_exists($val->index, $index_values)) {
          $index_values[$val->index] = array();
        }
        $index_values[$val->index][] = $val->value;
      }
    }
    $dataset->index_values = $index_values;

    if ($data_processed->error && $dataset->features === false) {
      $this->returnError(274, $this->version);
      return;
    }

    if ($dataset->features === false) {
      $this->returnError(272, $this->version);
      return;
    }
    if (is_array($dataset->features) === false) {
      $this->returnError(272, $this->version);
      return;
    }
    if (count($dataset->features) === 0) {
      $this->returnError(272, $this->version);
      return;
    }

    $this->xmlContents('data-features', $this->version, $dataset);
  }

  /**
   *@OA\Post(
   *	path="/data/features",
   *	tags={"data"},
   *	summary="Upload dataset feature description",
   *	description="Uploads dataset feature description. Upon success, it returns the data id.",
   *	@OA\Parameter(
   *		name="description",
   *		in="query",
   *		@OA\Schema(
   *          type="file"
   *        ),
   *		description="An XML file describing the dataset. Only name, description, and data format are required. Also see the [XSD schema](https://www.openml.org/api/v1/xsd/openml.data.features) and an [XML example](https://www.openml.org/api/v1/xml_example/data.features).",
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
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n431 - Dataset already processed\n432 - Please provide description xml\n433 - Problem validating uploaded description file\n434 - Could not find dataset\n436 - Something wrong with XML, check data id and evaluation engine id\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function data_features_upload() {
    if (!$this->user_has_admin_rights) {
      $this->returnError(106, $this->version);
      return;
    }

    // get correct description
    if (isset($_FILES['description']) == false || check_uploaded_file($_FILES['description']) == false) {
      $this->returnError(442, $this->version);
      return;
    }

    // get description from file upload. Note that we will check the XSD later on (after we have assembled fields for error handling)
    $description = $_FILES['description'];
    $xml = simplexml_load_file($description['tmp_name']);

    // precheck XSD (if this pre-check succeeds, we can do database error logging later)
    if (!($xml->children('oml', true)->{'did'} && $xml->children('oml', true)->{'evaluation_engine_id'})) {
      $this->returnError(443, $this->version, $this->openmlGeneralErrorCode, 'XML misses basic fields did or evaluation_engine_id');
      return;
    }
    $did = ''. $xml->children('oml', true)->{'did'}; // Note that this relies on this field being in the xml
    $eval_id = ''.$xml->children('oml', true)->{'evaluation_engine_id'}; // Note that this relies on this field being in the xml

    if (!is_numeric($did) || !is_numeric($eval_id) || $did <= 0 || $eval_id <= 0) {
      $this->returnError(446, $this->version);
      return;
    }

    $dataset = $this->Dataset->getById($did);
    if ($dataset == false) {
      $this->returnError(444, $this->version);
      return;
    }

    $data_processed_record = $this->Data_processed->getById(array($did, $eval_id));
    if ($data_processed_record && $data_processed_record->error == null) {
      $this->returnError(441, $this->version);
      return;
    }

    $num_tries = 0;
    if ($data_processed_record) {
      $num_tries = $data_processed_record->num_tries;
    }

    // prepare array for updating data object
    $data = array('did' => $did,
                  'evaluation_engine_id' => $eval_id,
                  'user_id' => $this->user_id,
                  'processing_date' => now(),
                  'num_tries' => $num_tries + 1);
    if ($xml->children('oml', true)->{'error'}) {
      $data['error'] = htmlentities($xml->children('oml', true)->{'error'});
    }

    if (validateXml($description['tmp_name'], xsd('openml.data.features', $this->controller, $this->version), $xmlErrors) == false) {
      $data['error'] = 'XSD does not comply. XSD errors: ' . $xmlErrors;
      $success = $this->Data_processed->replace($data);
      if (DEBUG_XSD_EMAIL) {
        $to = $this->user_email;
        $server = 'Server:' . $_SERVER['SERVER_ADDR'] . ':' . $_SERVER['SERVER_PORT'];
        $subject = 'OpenML Data Feature Upload DEBUG message (' . $server . ')';
        $content = $server . "\nFilename: " . $description['name'] . "\nXSD Validation Message: " . $xmlErrors . "\n=====BEGIN XML=====\n" . file_get_contents($description['tmp_name']);
        sendEmail($to, $subject, $content, 'text');
      }
      $this->returnError(443, $this->version, $this->openmlGeneralErrorCode, $xmlErrors);
      return;
    }

    $this->db->trans_start();

    # replace is delete then insert again
    $success = $this->Data_processed->replace($data);
    if (!$success) {
      $this->returnError(445, $this->version, $this->openmlGeneralErrorCode, 'Failed to create data processed record. ');
      return;
    }
    //$current_index = -1;

    //copy special features into data_features
    $targets = array_map('trim',explode(",",$dataset->default_target_attribute));
    $rowids = array_map('trim',explode(",",$dataset->row_id_attribute));
    $ignores = getcsv($dataset->ignore_attribute);
    if(!$ignores) {
      $ignores = array();
    }

    foreach($xml->children('oml', true)->{'feature'} as $feature_xml) {
      $feature = all_tags_from_xml(
        $feature_xml->children('oml', true),
        $this->xml_fields_features, array());
      $feature['did'] = $did;
      $feature['evaluation_engine_id'] = $eval_id;

      // add special features
      if (in_array($feature['name'], $targets)) {
        $feature['is_target'] = 'true';
      } else { //this is needed because the Java feature extractor still chooses a target when there isn't any
        $feature['is_target'] = 'false';
      }
      if (in_array($feature['name'], $rowids)) {
        $feature['is_row_identifier'] = 'true';
      }
      if (in_array($feature['name'], $ignores)) {
        $feature['is_ignore'] = 'true';
      }

      if (in_array('ClassDistribution', $feature)) {
        // check class distributions field
        json_decode($feature['ClassDistribution']);
        if (json_last_error()) {
          $this->db->trans_rollback();
          $this->returnError(447, $this->version, $this->openmlGeneralErrorCode, 'feature: ' . $feature['name']);
          return;
        }
      }

      //actual insert of the feature
      if (array_key_exists('nominal_value', $feature)) {
        $nominal_values = $feature['nominal_value'];
        unset($feature['nominal_value']);
      } else {
        $nominal_values = false;
      }

      $result = $this->Data_feature->insert($feature);
      if (!$result) {
        $this->db->trans_rollback();
        $this->returnError(450, $this->version, $this->openmlGeneralErrorCode, 'feature: ' . $feature['name']);
        return;
      }

      if ($nominal_values) {
        // check the nominal value property
        foreach ($nominal_values as $value) {
          $data = array(
            'did' => $did,
            'index' => $feature['index'],
            'value' => $value
          );
          $result = $this->Data_feature_value->insert($data);
          if (!$result) {
            $this->db->trans_rollback();
            $this->returnError(450, $this->version, $this->openmlGeneralErrorCode, 'feature: ' . $feature['name'] . ', value: ' . $value);
            return;
          }
        }

        if ($feature['data_type'] != 'nominal') {
          // only allowed for nominal values
          $this->db->trans_rollback();
          $this->returnError(449, $this->version, $this->openmlGeneralErrorCode, 'feature: ' . $feature['name']);
          return;
        }
      } elseif ($feature['data_type'] == 'nominal') {
        // required for nominal values.. missing so throw error
        $this->db->trans_rollback();
        $this->returnError(448, $this->version, $this->openmlGeneralErrorCode, 'feature: ' . $feature['name']);
        return;
      }

      // NOTE: this is commented out because not all datasets have targets, or they can have multiple ones. Targets should also be set more carefully.
      // if no specified attribute is the target, select the last one:
      //if( $dataset->default_target_attribute == false && $feature->index > $current_index ) {
      //  $current_index = $feature->index;
      //  $data['default_target_attribute'] = $feature->name;
      //}
    }
    if ($this->db->trans_status() === FALSE) {
      $this->db->trans_rollback();
      $this->returnError(445, $this->version);
      return;
    } else {
      $this->db->trans_commit();
      $this->xmlContents('data-features-upload', $this->version, array('did' => $dataset->did));
    }
  }

  /**
   *@OA\Get(
   *	path="/data/qualities/list",
   *	tags={"data"},
   *	summary="List all data qualities",
   *	description="Returns a list of all data qualities in the system.",
   *	@OA\Response(
   *		response=200,
   *		description="A list of data qualities",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/DataQualityList",
   *			example={
   *			  "data_qualities_list":{
   *			    "quality":{
   *			      "NumberOfClasses",
   *			      "NumberOfFeatures",
   *			      "NumberOfInstances",
   *			      "NumberOfInstancesWithMissingValues",
   *			      "NumberOfMissingValues",
   *			      "NumberOfNumericFeatures",
   *			      "NumberOfSymbolicFeatures"
   *			    }
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned\n370 - No data qualities available. There are no data qualities in the system.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function data_qualities_list() {
    $result = $this->Quality->allUsed( );
    $qualities = array();
    if($result != false) {
      foreach( $result as $r ) {
        $qualities[] = $r->name;
      }
    } else {
      $this->returnError( 641, $this->version );
      return;
    }
    $this->xmlContents( 'data-qualities-list', $this->version, array( 'qualities' => $qualities ) );
  }


  private function feature_qualities_list() {
    $result = $this->Quality->allFeatureQualitiesUsed( );
    $feature_qualities = array();
    if($result != false) {
      foreach( $result as $r ) {
        $feature_qualities[] = $r->name;
      }
    } else {
      $this->returnError( 651, $this->version );
      return;
    }
    $this->xmlContents( 'feature-qualities-list', $this->version, array( 'feature_qualities' => $feature_qualities ) );
  }


  /**
   *@OA\Get(
   *	path="/data/qualities/{id}",
   *	tags={"data"},
   *	summary="Get data qualities",
   *	description="Returns the qualities of a dataset.",
   *	@OA\Parameter(
   *		name="id",
   *		in="path",
   *		@OA\Schema(
   *          type="integer"
   *        ),
   *		description="Id of the dataset.",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="All the qualities of the dataset",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/DataQualities",
   *			example={
   *			  "data_qualities": {
   *			    "quality": {
   *			      {
   *			        "name": "ClassCount",
   *			        "value": "3.0"
   *			      },
   *			      {
   *			        "name": "ClassEntropy",
   *			        "value": "1.584962500721156"
   *			      },
   *			      {
   *			        "name": "NumberOfClasses",
   *			        "value": "3"
   *			      },
   *			      {
   *			        "name": "NumberOfFeatures",
   *			        "value": "5"
   *			      },
   *			      {
   *			        "name": "NumberOfInstances",
   *			        "value": "150"
   *			      },
   *			      {
   *			        "name": "NumberOfInstancesWithMissingValues",
   *			        "value": "0"
   *			      },
   *			      {
   *			        "name": "NumberOfMissingValues",
   *			        "value": "0"
   *			      },
   *			      {
   *			        "name": "NumberOfNumericFeatures",
   *			        "value": "4"
   *			      },
   *			      {
   *			        "name": "NumberOfSymbolicFeatures",
   *			        "value": "0"
   *			      }
   *			    }
   *			  }
   *			}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n360 - Please provide data set ID\n361 - Unknown dataset. The data set with the given ID was not found in the database, or is not shared with you.\n362 - No qualities found. The registered dataset did not contain any calculated qualities.\n363 - Dataset not processed yet. The dataset was not processed yet, no qualities are available. Please wait for a few minutes.\n364 - Dataset processed with error. The quality calculator has run into an error while processing the dataset. Please check whether it is a valid supported file. If so, contact the support team.\n365 - Interval start or end illegal. There was a problem with the interval\nstart or end.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function data_qualities($data_id, $evaluation_engine_id) {
    if( $data_id == false ) {
      $this->returnError( 360, $this->version );
      return;
    }
    $dataset = $this->Dataset->getById( $data_id );
    if( $dataset === false ) {
      $this->returnError( 361, $this->version );
      return;
    }

    if($dataset->visibility != 'public' and $dataset->uploader != $this->user_id  && !$this->user_has_admin_rights) {
      $this->returnError( 361, $this->version ); // Add special error code for this case?
      return;
    }

    $data_processed = $this->Data_processed->getById(array(0 => $data_id, 1 => $evaluation_engine_id));

    if (!$data_processed) {
      $this->returnError(363, $this->version);
      return;
    }

    $interval_start = false; // $this->input->get( 'interval_start' );
    $interval_end   = false; // $this->input->get( 'interval_end' );
    $interval_size  = false; // $this->input->get( 'interval_size' );

    if($interval_start !== false || $interval_end !== false || $interval_size !== false) {
      $interval_constraints = '';
      if( $interval_start !== false && is_numeric( $interval_start ) ) {
        $interval_constraints .= ' AND `interval_start` >= ' . $interval_start;
      }
      if( $interval_end !== false && is_numeric( $interval_end ) ) {
        $interval_constraints .= ' AND `interval_end` <= ' . $interval_end;
      }
      if( $interval_size !== false && is_numeric( $interval_size ) ) {
        $interval_constraints .= ' AND `interval_end` - `interval_start` = ' . $interval_size;
      }
      $dataset->qualities = $this->Data_quality_interval->getWhere( 'data = "' . $dataset->did . '" ' . $interval_constraints . ' AND evaluation_engine_id = ' . evaluation_engine_id );
    } else {
      $dataset->qualities = $this->Data_quality->getWhere('data = "' . $dataset->did . '" AND evaluation_engine_id = ' . $evaluation_engine_id);
    }

    if($data_processed->error && $dataset->qualities === false) {
      $this->returnError(364, $this->version, $this->openmlGeneralErrorCode, $data_processed->error);
      return;
    }

    if( $dataset->qualities === false ) {
      $this->returnError( 362, $this->version );
      return;
    }
    if( is_array( $dataset->qualities ) === false ) {
      $this->returnError( 362, $this->version );
      return;
    }
    if( count( $dataset->qualities ) === 0 ) {
      $this->returnError( 362, $this->version );
      return;
    }

    $this->xmlContents( 'data-qualities', $this->version, $dataset );
  }

  private function feature_qualities($data_id, $evaluation_engine_id) {
    if( $data_id == false ) {
      $this->returnError( 631, $this->version );
      return;
    }
    $dataset = $this->Dataset->getById( $data_id );
    if( $dataset === false ) {
      $this->returnError( 632, $this->version );
      return;
    }

    if($dataset->visibility != 'public' and $dataset->uploader != $this->user_id ) {
      $this->returnError( 632, $this->version ); // Add special error code for this case?
      return;
    }


    $data_processed = $this->Data_processed->getById(array(0 => $data_id, 1 => $evaluation_engine_id));

    if($data_processed === false) {
      $this->returnError( 634, $this->version );
      return;
    }

    if( $dataset->error != "false") {
      $this->returnError( 635, $this->version );
      return;
    }

    $dataset->feature_qualities = $this->Feature_quality->getWhere('data = "' . $dataset->did . '" AND evaluation_engine_id = ' . $evaluation_engine_id);

    if( $dataset->feature_qualities === false ) {
      $this->returnError( 633, $this->version );
      return;
    }
    if( is_array( $dataset->feature_qualities ) === false ) {
      $this->returnError( 633, $this->version );
      return;
    }
    if( count( $dataset->feature_qualities ) === 0 ) {
      $this->returnError( 633, $this->version );
      return;
    }

    $this->xmlContents( 'feature-qualities', $this->version, $dataset );
  }

  /**
   *@OA\Post(
   *	path="/data/qualities",
   *	tags={"data"},
   *	summary="Upload dataset qualities",
   *	description="Uploads dataset qualities. Upon success, it returns the data id.",
   *	@OA\Parameter(
   *		name="description",
   *		in="query",
   *		@OA\Schema(
   *          type="file"
   *        ),
   *		description="An XML file describing the dataset. Only name, description, and data format are required. Also see the [XSD schema](https://www.openml.org/api/v1/xsd/openml.data.qualities) and an [XML example](https://www.openml.org/api/v1/xml_example/data.qualities).",
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
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n381 - Something wrong with XML, please check did and evaluation_engine_id\n382 - Please provide description xml\n383 - Problem validating uploaded description file\n384 - Dataset not processed yet\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function data_qualities_upload() {
    if (!$this->user_has_admin_rights) {
      $this->returnError(106, $this->version);
      return;
    }

    // get correct description
    if (isset($_FILES['description']) == false || check_uploaded_file($_FILES['description']) == false) {
      $this->returnError(382, $this->version);
      return;
    }

    // get description from string upload
    $description = $_FILES['description'];
    if (validateXml($description['tmp_name'], xsd('openml.data.qualities', $this->controller, $this->version), $xmlErrors) == false) {
      $this->returnError(383, $this->version, $this->openmlGeneralErrorCode, $xmlErrors);
      return;
    }

    $xml = simplexml_load_file($description['tmp_name']);
    $did = ''. $xml->children('oml', true)->{'did'};
    $eval_id = ''.$xml->children('oml', true)->{'evaluation_engine_id'};

    if (!is_numeric($did) || !is_numeric($eval_id) || $did <= 0 || $eval_id <= 0) {
      $this->returnError( 381, $this->version );
      return;
    }

    $data_processed = $this->Data_processed->getById(array(0 => $did, 1 => $this->config->item('default_evaluation_engine_id')));
    if ($data_processed == false) {
      $this->returnError(384, $this->version);
      return;
    }
    $did = $data_processed->did;

    $all_data_qualities = $this->Quality->getColumnWhere('name', '`type` = "DataQuality"');
    $all_feature_qualities = $this->Quality->getColumnWhere('name', '`type` = "FeatureQuality"');

    // check and collect the qualities
    $newQualities = array();
    foreach ($xml->children('oml', true)->{'quality'} as $q) {
      $quality = xml2object($q, true);

      if (property_exists($quality, 'feature_index')) {
        // check if valid feature quality
        if (is_array($all_feature_qualities) == false || in_array($quality->name, $all_feature_qualities) == false) {
          $this->returnError(387, $this->version, $this->openmlGeneralErrorCode, 'Feature Quality: ' . $quality->name . '. Legal Feature Qualities: ' . implode(', ', $all_feature_qualities));
          return;
        }
      } else {
        // check if valid data quality
        if (is_array($all_data_qualities) == false || in_array($quality->name, $all_data_qualities) == false) {
          $this->returnError(387, $this->version, $this->openmlGeneralErrorCode, 'Data quality: ' . $quality->name . '. Legal Data Qualities: ' . implode(', ', $all_data_qualities));
          return;
        }
      }

      $newQualities[] = $quality;
    }

    if (count($newQualities) == 0) {
      $this->returnError(385, $this->version);
      return;
    }

    $success = true; // TODO: something with this

    $data = array('did' => $did,
                  'evaluation_engine_id' => $eval_id,
                  'user_id' => $this->user_id,
                  'processing_date' => now());
    if($xml->children('oml', true)->{'error'}) {
      $data['error'] = htmlentities($xml->children('oml', true)->{'error'});
    }

    $this->db->trans_start();

    $result = $this->Data_processed->insert_ignore($data);

    foreach ($newQualities as $index => $quality) {
      if (property_exists($quality, 'interval_start')) {
        $data = array(
          'data' => $did,
          'quality' => $quality->name,
          'evaluation_engine_id' => $eval_id,
          'interval_start' => $quality->interval_start,
          'interval_end' => $quality->interval_end);
        if (property_exists($quality, 'value')) { $data['value'] = $quality->value; }
        $result = $this->Data_quality_interval->insert_ignore($data);
      } elseif (property_exists($quality, 'feature_index')) {
        $data = array(
          'data' => $did,
          'feature_index' => $quality->feature_index,
          'quality' => $quality->name,
          'evaluation_engine_id' => $eval_id);
        if (property_exists($quality, 'value')) { $data['value'] = $quality->value; }
        $result = $this->Feature_quality->insert_ignore($data);
      } else {
        $data = array(
          'data' => $did,
          'quality' => $quality->name,
          'evaluation_engine_id' => $eval_id);
        if (property_exists($quality, 'value')) { $data['value'] = $quality->value; }
        $result = $this->Data_quality->insert_ignore($data);
      }
    }

    if ($this->db->trans_status() === FALSE) {
      $this->db->trans_rollback();
      $this->returnError(389, $this->version);
      return;
    } else {
      $this->db->trans_commit();
      $this->xmlContents('data-qualities-upload', $this->version, array('did' => $did));
    }

    // add to elastic search index.
    try {
      $this->elasticsearch->index('data', $did);
    } catch (Exception $e) {
      $additionalMsg = get_class() . '.' . __FUNCTION__ . ':' . $e->getMessage();
      $this->returnError(105, $this->version, $this->openmlGeneralErrorCode, $additionalMsg);
      return;
    }
  }

  /**
   *@OA\Get(
   *	path="/data/unprocessed/{data_engine_id}/{order}",
   *	tags={"data"},
   *	summary="Get a list of unprocessed datasets",
   *	description="This call is for people running their own dataset processing engines. It returns the details of datasets that are not yet processed by the given processing engine. It doesn't process the datasets, it just returns the dataset info.",
   *	@OA\Parameter(
   *		name="data_engine_id",
   *		in="path",
   *		@OA\Schema(
   *          type="integer"
   *        ),
   *		description="The ID of the data processing engine. You get this ID when you register a new data processing engine with OpenML. The ID of the main data processing engine is 1.",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="order",
   *		in="path",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="When there are multiple datasets still to process, this defines which ones to return. Options are 'normal' - the oldest datasets, or 'random'.",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="A list of unprocessed datasets",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/DataUnprocessed",
   *			example={"data_unprocessed": {"run": {{"did": "1", "status": "deactivated", "version": "2", "name": "anneal", "format": "ARFF"}}}}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n681 - No unprocessed datasets.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function data_unprocessed($evaluation_engine_id, $order) {
    if (!$this->user_has_admin_rights) {
      $this->returnError(106, $this->version);
      return;
    }

    $this->db->select('d.*')->from('dataset d');
    $this->db->join('data_processed p', 'd.did = p.did AND evaluation_engine_id = ' . $evaluation_engine_id, 'left');
    $this->db->where('(p.did IS NULL OR (p.error IS NOT NULL AND p.num_tries < ' . $this->config->item('process_data_tries') . ' AND p.processing_date < "' . now_offset('-' . $this->config->item('process_data_offset')) . '"))');
    // JvR TODO: Because of legacy datasets. We should later make 'file_id' a non null field.
    $this->db->where('d.file_id IS NOT NULL');

    $randomcount = 200;
    if ($order == 'random') {
      $this->db->limit($randomcount);
    } else {
      $this->db->limit('1');
    }

    // Reverse order if needed (slower query)
    if ($order == 'reverse') {
      $this->db->order_by('d.did DESC');
    }

    $data = $this->db->get();
    if ($data && $data->num_rows() > 0){
      if ($order == 'random'){
        $result = $data->result();
        $result = array($result[array_rand($result)]);
      } else {
        $result = $data->result();
      }
    } else {
      $this->returnError(681, $this->version);
      return;
    }

    $this->xmlContents('data-unprocessed', $this->version, array('res' => $result));
  }

  /**
   *@OA\Post(
   *	path="/data/qualities/unprocessed/{data_engine_id}/{order}",
   *	tags={"data"},
   *	summary="Get a list of datasets with unprocessed qualities",
   *	description="This call is for people running their own dataset processing engines. It returns the details of datasets for which certain qualities are not yet processed by the given processing engine. It doesn't process the datasets, it just returns the dataset info.",
   *	@OA\Parameter(
   *		name="data_engine_id",
   *		in="path",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="The ID of the data processing engine. You get this ID when you register a new data processing engine with OpenML. The ID of the main data processing engine is 1.",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="order",
   *		in="path",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="When there are multiple datasets still to process, this defines which ones to return. Options are 'normal' - the oldest datasets, or 'random'.",
   *		required=true,
   *	),
   *	@OA\Parameter(
   *		name="api_key",
   *		in="query",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="API key to authenticate the user",
   *		required=false,
   *	),
   *	@OA\Parameter(
   *		name="qualities",
   *		in="query",
   *		@OA\Schema(
   *          type="string"
   *        ),
   *		description="Comma-separated list of (at least two) quality names, e.g. 'NumberOfInstances,NumberOfFeatures'.",
   *		required=true,
   *	),
   *	@OA\Response(
   *		response=200,
   *		description="A list of unprocessed datasets",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/DataUnprocessed",
   *			example={"data_unprocessed": {"run": {{"did": "1", "status": "deactivated", "version": "2", "name": "anneal", "format": "ARFF"}}}}
   *		),
   *	),
   *	@OA\Response(
   *		response=412,
   *		description="Precondition failed. An error code and message are returned.\n686 - Please specify the features the evaluation engine wants to calculate (at least 2).\n687 - No unprocessed datasets according to the given set of meta-features.\n688 - Illegal qualities.\n",
   *		@OA\JsonContent(
   *			ref="#/components/schemas/Error",
   *		),
   *	),
   *)
   */
  private function dataqualities_unprocessed($evaluation_engine_id, $order, $feature_attributes = false, $priorityTag = null) {
    if (!$this->user_has_admin_rights) {
      $this->returnError(106, $this->version);
      return;
    }
    
    $requiredMetafeatures = explode(',', $this->input->get_post('qualities')); // TODO: remove get
    if (count($requiredMetafeatures) < 2) {
      $this->returnError(686, $this->version);
      return;
    }

    $type = $feature_attributes ? 'FeatureQuality' : 'DataQuality';
    $legal_qualities = $this->Quality->getColumnWhere('name', 'type = "' . $type . '"');
    $illegal_qualities = array_diff($requiredMetafeatures, $legal_qualities);

    if (count($illegal_qualities)) {
      $this->returnError(688, $this->version, $this->openmlGeneralErrorCode, 'Illegal qualities: ' . implode(',', $illegal_qualities) . '');
      return;
    }

    $tagJoin = "";
    $tagSelect = "";
    $tagSort = "";
    if ($priorityTag != null) {
      $tagSelect = ", t.tag ";
      $tagSort = "t.tag DESC, "; // to avoid NULL values first
      $tagJoin = "LEFT JOIN dataset_tag t ON d.did = t.id AND t.tag = '" . $priorityTag . "' ";
    }

    if (!$feature_attributes) {
      $sql = 'SELECT DISTINCT d.* ' .  $tagSelect .
             'FROM data_processed p, dataset d LEFT JOIN (' .
               ' SELECT q.data, COUNT(*) AS `numQualities`' .
               ' FROM data_quality q ' . 
               ' WHERE q.quality in ("' . implode('","', $requiredMetafeatures) . '") AND q.evaluation_engine_id = ' . $evaluation_engine_id .
               ' GROUP BY q.data HAVING numQualities = ' . count($requiredMetafeatures) . ') as `qualityCount` ' .
             ' ON d.did = qualityCount.data '. $tagJoin . 
             ' WHERE qualityCount.data IS NULL ' .
             ' AND d.did = p.did AND p.evaluation_engine_id = ' . $this->config->item('default_evaluation_engine_id') .
             ' AND p.error IS NULL ' .
             ' ORDER BY ' . $tagSort . ' d.did ';
    } else {
      $sql = 'SELECT DISTINCT d.* ' . $tagSelect .
             'FROM data_processed p, dataset d LEFT JOIN (' .
               ' SELECT q.data, COUNT(*) AS `numQualities`' . 
               ' FROM feature_quality q ' . $tagJoin .
               ' JOIN (SELECT data_feature.did, COUNT(*) as `number_of_attributes` FROM data_feature GROUP BY data_feature.did) as `attCounts` ON attCounts.did = q.data' .
               ' WHERE q.quality in ("' . implode('","', $requiredMetafeatures) . '") AND q.evaluation_engine_id = ' . $evaluation_engine_id .
               ' GROUP BY q.data HAVING numQualities = max(attCounts.number_of_attributes)*' . count($requiredMetafeatures) . ') as `qualityCount`' .
             ' ON d.did = qualityCount.data ' .
             ' WHERE qualityCount.data IS NULL ' .
             ' AND d.did = p.did AND p.evaluation_engine_id = ' . $this->config->item('default_evaluation_engine_id') .
             ' AND p.error IS NULL ' .
             ' ORDER BY ' . $tagSort . ' d.did ';
    }
    if ($order == 'random') {
      $sql .= ' LIMIT 100; ';
    } else {
      $sql .= ' LIMIT 1;';
    }
    $result = $this->Dataset->query($sql);
    if ($result === false) {
      $this->returnError(687, $this->version);
      return;
    }
    
    // TODO: random only in case of random argument. Also take priority tag into account
    $result = array($result[array_rand($result)]);

    $this->xmlContents('data-unprocessed', $this->version, array('res' => $result));
  }
}
?>
