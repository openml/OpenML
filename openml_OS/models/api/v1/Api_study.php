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
    
    if (count($segments) == 1 && is_numeric($segments[0]) && $request_type == 'delete') {
      $this->study_delete($segments[0]);
      return;
    }
    
    if (count($segments) == 2 && is_numeric($segments[0]) && $segments[1] == 'attach' && $request_type == 'post') {
      $this->study_attach_detach($segments[0], true);
      return;
    }
    
    if (count($segments) == 2 && is_numeric($segments[0]) && $segments[1] == 'detach' && $request_type == 'post') {
      $this->study_attach_detach($segments[0], false);
      return;
    }
    
    if (count($segments) == 1 || count($segments) == 2) {
      $type = null;
      if (count($segments) == 2) {
        $type = $segments[1];
      }

      if (is_numeric($segments[0])) {
        $this->study_by_id($segments[0], $type);
        return;
      } else {
        $this->study_by_alias($segments[0], $type);
        return;
      }
    }

    $this->returnError(100, $this->version);
  }
  
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


  private function study_list($segs) {
    $legal_filters = array('limit', 'offset', 'main_entity_type', 'uploader');
    
    list($query_string, $illegal_filters) = $this->parse_filters($segs, $legal_filters);
    if (count($illegal_filters) > 0) {
      $this->returnError(591, $this->version, $this->openmlGeneralErrorCode, 'Legal filter operators: ' . implode(',', $legal_filters) .'. Found illegal filter(s): ' . implode(', ', $illegal_filters));
      return;
    }
    
    $illegal_filter_inputs = $this->check_filter_inputs($query_string, $legal_filters, array('main_entity_type'));
    if (count($illegal_filter_inputs) > 0) {
      $this->returnError(592, $this->version, $this->openmlGeneralErrorCode, 'Filters with illegal values: ' . implode(',', $illegal_filter_inputs));
      return;
    }
    
    $uploader = element('uploader', $query_string, null);
    $limit = element('limit', $query_string, null);
    $offset = element('offset', $query_string, null);
    $main_entity_type = element('main_entity_type', $query_string, null);
    
    if ($offset && !$limit) {
      $this->returnError(593, $this->version);
      return;
    }
    
    $whereClause = '(visibility = "public" or creator = ' . $this->user_id . ')';
    if ($uploader) {
      $whereClause .= ' AND creator = ' . $uploader;
    }
    if ($main_entity_type) {
      $whereClause .= ' AND main_entity_type = ' . $main_entity_type;
    }
    $studies = $this->Study->getWhere($whereClause, null, $limit, $offset);

    if (count($studies) == 0) {
      $this->returnError(594, $this->version);
      return;
    }

    $this->xmlContents('study-list', $this->version, array('studies' => $studies));
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
      $this->{$model}->insert_ignore($data);
    }
    return true;
  }
}

?>

