<?php
class Api_data extends Api_model {

  protected $version = 'v1';

  function __construct() {
    parent::__construct();

    // load models
    $this->load->model('Data_processed');
    $this->load->model('Dataset');
    $this->load->model('Dataset_tag');
    $this->load->model('Data_feature');
    $this->load->model('Data_quality');
    $this->load->model('Feature_quality');
    $this->load->model('Data_quality_interval');
    $this->load->model('Quality');
    $this->load->model('File');
    $this->load->model('Study_tag');

    $this->load->helper('file_upload');
    $this->db = $this->Database_singleton->getReadConnection();

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

    if (count($segments) >= 4 && $segments[0] == 'qualities' && $segments[1] == 'unprocessed' && is_numeric($segments[2]) && in_array($segments[3], $order_values)) {
      $feature = (count($segments) > 4 && $segments[4] == 'feature');
      $this->dataqualities_unprocessed($segments[2], $segments[3], $feature);
      return;
    }

    if (count($segments) == 1 && is_numeric($segments[0]) && $request_type == 'delete') {
      $this->data_delete($segments[0]);
      return;
    }

    if (count($segments) == 0 && $request_type == 'post') {
      $this->data_upload();
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
      $this->entity_tag_untag('dataset', $this->input->post('data_id'), $this->input->post('tag'), false, 'data');
      return;
    }

    if (count($segments) == 1 && $segments[0] == 'untag' && $request_type == 'post') {
      $this->entity_tag_untag('dataset', $this->input->post('data_id'), $this->input->post('tag'), true, 'data');
      return;
    }

    $this->returnError(100, $this->version);
  }

  private function data_list($segs) {
    $legal_filters = array('tag', 'status', 'limit', 'offset', 'data_name', 'data_version', 'number_instances', 'number_features', 'number_classes', 'number_missing_values');
    $query_string = array();
    for ($i = 0; $i < count($segs); $i += 2) {
      $query_string[$segs[$i]] = urldecode($segs[$i+1]);
      if (in_array($segs[$i], $legal_filters) == false) {
        $this->returnError(370, $this->version, $this->openmlGeneralErrorCode, 'Legal filter operators: ' . implode(',', $legal_filters) .'. Found illegal filter: ' . $segs[$i]);
        return;
      }
    }
    $tag = element('tag',$query_string);
    $name = element('data_name',$query_string);
    $version = element('data_version',$query_string);
    $status = element('status',$query_string);
    $limit = element('limit',$query_string);
    $offset = element('offset',$query_string);
    $nr_insts = element('number_instances',$query_string);
    $nr_feats = element('number_features',$query_string);
    $nr_class = element('number_classes',$query_string);
    $nr_miss = element('number_missing_values',$query_string);

    if (!(is_safe($tag) && is_safe($version) && is_safe($limit) && is_safe($offset) && is_safe($nr_insts) && is_safe($nr_feats) && is_safe($nr_class) && is_safe($nr_miss))) {
      $this->returnError(371, $this->version);
      return;
    }

    $where_tag = $tag == false ? '' : ' AND `did` IN (select id from dataset_tag where tag="' . $tag . '") ';
    $where_name = $name == false ? '' : ' AND `name` = "' . $name . '"';
    $where_version = $version == false ? '' : ' AND `version` = "' . $version . '" ';
    $where_insts = $nr_insts == false ? '' : ' AND `did` IN (select data from data_quality dq where quality="NumberOfInstances" and value ' . (strpos($nr_insts, '..') !== false ? 'BETWEEN ' . str_replace('..',' AND ',$nr_insts) : '= '. $nr_insts) . ') ';
    $where_feats = $nr_feats == false ? '' : ' AND `did` IN (select data from data_quality dq where quality="NumberOfFeatures" and value ' . (strpos($nr_feats, '..') !== false ? 'BETWEEN ' . str_replace('..',' AND ',$nr_feats) : '= '. $nr_feats) . ') ';
    $where_class = $nr_class == false ? '' : ' AND `did` IN (select data from data_quality dq where quality="NumberOfClasses" and value ' . (strpos($nr_class, '..') !== false ? 'BETWEEN ' . str_replace('..',' AND ',$nr_class) : '= '. $nr_class) . ') ';
    $where_miss = $nr_miss == false ? '' : ' AND `did` IN (select data from data_quality dq where quality="NumberOfMissingValues" and value ' . (strpos($nr_miss, '..') !== false ? 'BETWEEN ' . str_replace('..',' AND ',$nr_miss) : '= '. $nr_miss) . ') ';
    // by default, only return active datasets
    $where_status = $status == false ? ' AND status = "active" ' : ($status != "all" ? ' AND status = "'. $status . '" ' : '');
    $where_total = $where_tag . $where_name . $where_version . $where_insts . $where_feats . $where_class . $where_miss . $where_status;
    $where_limit = $limit == false ? '' : ' LIMIT ' . $limit;
    if($limit != false && $offset != false){
      $where_limit =  ' LIMIT ' . $offset . ',' . $limit;
    }

    // can be removed if noone needs it. Subsumed by the status filter
    $active = element('active',$query_string);
    if ($active == 'true') {
      $where_total .= ' AND status = "active" ';
    }

    $sql = 'select * from dataset where (visibility = "public" or uploader='.$this->user_id.') '. $where_total . $where_limit;
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
    $dq = $this->Data_quality->query('SELECT data, quality, value FROM data_quality WHERE `data` IN (' . implode(',', array_keys( $datasets) ) . ') AND evaluation_engine_id = ' . $this->config->item('default_evaluation_engine_id') . ' AND quality IN ("' .  implode('","', $this->config->item('basic_qualities') ) . '") AND value IS NOT NULL ORDER BY `data`');

    if ($dq != false) {
      foreach( $dq as $quality ) {
        $datasets[$quality->data]->qualities[$quality->quality] = $quality->value;
      }
    }

    $this->xmlContents('data', $this->version, array('datasets' => $datasets));
  }

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

    foreach( $this->xml_fields_dataset['csv'] as $field ) {
      $dataset->{$field} = getcsv( $dataset->{$field} );
    }

    # TMP hack for testing
    $studies = $this->Study_tag->getStudyIdsFromEntity('dataset', $dataset->did);
    $dataset->studies = $studies != false ? $studies : array();

    $this->xmlContents( 'data-get', $this->version, $dataset );
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

    $result = $this->Dataset->delete( $dataset->did );
    $this->Data_feature->deleteWhere('did =' . $dataset->did);
    $this->Data_quality->deleteWhere('data =' . $dataset->did);

    if( $result == false ) {
      $this->returnError( 355, $this->version );
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


  private function data_upload() {
    // get correct description
    $xsdFile = xsd('openml.data.upload', $this->controller, $this->version);

    if($this->input->post('description')) {
      // get description from string upload
      $description = $this->input->post('description', false);
      if(validateXml($description, $xsdFile, $xmlErrors, false ) == false) {
        $this->returnError(131, $this->version, $this->openmlGeneralErrorCode, $xmlErrors);
        return;
      }
      $xml = simplexml_load_string( $description );
    } elseif (isset($_FILES['description'])) {
      $uploadError = '';
      $xmlErrors = '';
      if (check_uploaded_file($_FILES['description'], false, $uploadError) == false) {
        $this->returnError(135, $this->version, $this->openmlGeneralErrorCode, $uploadError);
      }
      // get description from file upload
      $description = $_FILES['description'];

      if (validateXml($description['tmp_name'], $xsdFile, $xmlErrors) == false) {
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

      $file_id = $this->File->register_uploaded_file($_FILES['dataset'], $this->data_folders['dataset'], $this->user_id, 'dataset', $access_control);
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
      'file_id' => $file_id
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

    /* * * *
     * THE ACTUAL INSERTION
     * * * */
    $id = $this->Dataset->insert($dataset);
    if (!$id) {
      $this->returnError(134, $this->version);
      return;
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
      $success = $this->entity_tag_untag('dataset', $id, $tag, false, 'data', false);
    }

    // create initial wiki page
    $this->wiki->export_to_wiki($id);

    // create
    $this->xmlContents('data-upload', $this->version, array('id' => $id));
  }


  private function data_features($data_id) {
    if($data_id == false) {
      $this->returnError(270, $this->version);
      return;
    }
    $dataset = $this->Dataset->getById($data_id);
    if( $dataset === false ) {
      $this->returnError(271, $this->version);
      return;
    }

    if($dataset->visibility != 'public' && $dataset->uploader != $this->user_id) {
      $this->returnError(271, $this->version); // Add special error code for this case?
      return;
    }

    $data_processed = $this->Data_processed->getById(array($data_id, 1));

    if ($data_processed == false) {
      $this->returnError(273, $this->version);
      return;
    }

    // TODO: think of better policy
    //if ($data_processed->error) {
    //  $this->returnError(274, $this->version);
    //  return;
    //}

    $dataset->features = $this->Data_feature->getWhere('did = "' . $dataset->did . '"');

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

  private function data_features_upload() {
    if (!$this->user_has_admin_rights) {
      $this->returnError(106, $this->version);
      return;
    }

    // get correct description
    if (isset($_FILES['description']) == false || check_uploaded_file($_FILES['description']) == false) {
      $this->returnError( 432, $this->version );
      return;
    }

    // get description from string upload
    $description = $_FILES['description'];
    if (validateXml($description['tmp_name'], xsd('openml.data.features', $this->controller, $this->version), $xmlErrors) == false) {
      $this->returnError(433, $this->version, $this->openmlGeneralErrorCode, $xmlErrors);
      return;
    }

    $xml = simplexml_load_file($description['tmp_name']);
    $did = ''. $xml->children('oml', true)->{'did'};
    $eval_id = ''.$xml->children('oml', true)->{'evaluation_engine_id'};

    if (!is_numeric($did) || !is_numeric($eval_id) || $did <= 0 || $eval_id <= 0) {
      $this->returnError( 436, $this->version );
      return;
    }

    $dataset = $this->Dataset->getById($did);
    if ($dataset == false) {
      $this->returnError(434, $this->version);
      return;
    }

    if ($this->Data_processed->getWhere('did = ' . $did . ' AND evaluation_engine_id = ' . $eval_id)) {
      $this->returnError(431, $this->version);
      return;
    }

    // prepare array for updating data object
    $data = array('did' => $did,
                  'evaluation_engine_id' => $eval_id,
                  'user_id' => $this->user_id,
                  'processing_date' => now());
    if($xml->children('oml', true)->{'error'}) {
      $data['error'] = htmlentities($xml->children('oml', true)->{'error'});
    }

    $this->db->trans_start();

    $success = $this->Data_processed->insert($data);
    if (!$success) {
      $this->returnError(435, $this->version, $this->openmlGeneralErrorCode, 'Failed to create data processed record. ');
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

    foreach($xml->children('oml', true)->{'feature'} as $q) {
      $feature = xml2object( $q, true );
      $feature->did = $did;
      $feature->evaluation_engine_id = $eval_id;

      // add special features
      if(in_array($feature->name,$targets)) {
        $feature->is_target = 'true';
      } else {//this is needed because the Java feature extractor still chooses a target when there isn't any
        $feature->is_target = 'false';
      }
      if(in_array($feature->name,$rowids)) {
        $feature->is_row_identifier = 'true';
      }
      if(in_array($feature->name,$ignores)) {
        $feature->is_ignore = 'true';
      }

      //actual insert
      $this->Data_feature->insert($feature);

      // NOTE: this is commented out because not all datasets have targets, or they can have multiple ones. Targets should also be set more carefully.
      // if no specified attribute is the target, select the last one:
      //if( $dataset->default_target_attribute == false && $feature->index > $current_index ) {
      //  $current_index = $feature->index;
      //  $data['default_target_attribute'] = $feature->name;
      //}
    }
    $this->db->trans_complete();

    if ($success) {
      $this->xmlContents('data-features-upload', $this->version, array('did' => $dataset->did));
    } else {
      $this->returnError(435, $this->version);
      return;
    }
  }

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

    if($dataset->visibility != 'public' and $dataset->uploader != $this->user_id ) {
      $this->returnError( 361, $this->version ); // Add special error code for this case?
      return;
    }

    $data_processed = $this->Data_processed->getById(array(0 => $data_id, 1 => $evaluation_engine_id));

    if (!$data_processed) {
      $this->returnError(363, $this->version);
      return;
    }

    if($data_processed->error) {
      $this->returnError(364, $this->version, $this->openmlGeneralErrorCode, $data_processed->error);
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
    $this->db->trans_complete();

    // add to elastic search index.
    try {
      $this->elasticsearch->index('data', $did);
    } catch (Exception $e) {
      $additionalMsg = get_class() . '.' . __FUNCTION__ . ':' . $e->getMessage();
      $this->returnError(105, $this->version, $this->openmlGeneralErrorCode, $additionalMsg);
      return;
    }

    if ($success) {
      $this->xmlContents('data-qualities-upload', $this->version, array('did' => $did));
    } else {
      $this->returnError(389, $this->version);
      return;
    }
  }

  private function data_unprocessed($evaluation_engine_id, $order) {

    $this->db->select('d.*')->from('dataset d');
    $this->db->join('data_processed p', 'd.did = p.did AND evaluation_engine_id = ' . $evaluation_engine_id, 'left');
    $this->db->where('p.did IS NULL');
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

  private function dataqualities_unprocessed($evaluation_engine_id, $order, $feature_attributes = false, $priorityTag = null) {
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
      $tagJoin = "LEFT JOIN dataset_tag t ON q.data = t.id AND t.tag = '" . $priorityTag . "' ";
    }

    if (!$feature_attributes) {
      $sql = 'SELECT DISTINCT d.* FROM data_processed p, dataset d LEFT JOIN (' .
               ' SELECT q.data, COUNT(*) AS `numQualities`' . $tagSelect .
               ' FROM data_quality q ' . $tagJoin .
               ' WHERE q.quality in ("' . implode('","', $requiredMetafeatures) . '") AND q.evaluation_engine_id = ' . $evaluation_engine_id .
               ' GROUP BY q.data HAVING numQualities = ' . count($requiredMetafeatures) . ') as `qualityCount` ' .
             ' ON d.did = qualityCount.data '.
             ' WHERE qualityCount.data IS NULL ' .
             ' AND d.did = p.did AND p.evaluation_engine_id = ' . $this->config->item('default_evaluation_engine_id') .
             ' AND p.error IS NULL ' .
             ' ORDER BY ' . $tagSort . ' d.did ';
    } else {
      $sql = 'SELECT DISTINCT d.* FROM data_processed p, dataset d LEFT JOIN (' .
               ' SELECT q.data, COUNT(*) AS `numQualities`' . $tagSelect .
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
    $result = array($result[array_rand($result)]);

    $this->xmlContents('data-unprocessed', $this->version, array('res' => $result));
  }
}
?>
