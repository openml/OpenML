<?php
class Api_study extends MY_Api_Model {

  protected $version = 'v1';

  function __construct() {
    parent::__construct();

    $this->load->model('Study');
    
    $this->db = $this->Database_singleton->getWriteConnection();
  }

  function bootstrap($format, $segments, $request_type, $user_id) {
    $this->outputFormat = $format;

    $getpost = array('get','post');

    if (count($segments) == 1 && $segments[0] == 'list') {
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

    $legal_knowledge_types = array(
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
    
    $main_knowledge_type = $xml->children('oml', true)->{'main_knowledge_type'};
    $benchmark_suite = $xml->children('oml', true)->{'benchmark_suite'};
    if (!in_array($main_knowledge_type, $legal_knowledge_types)) {
      $this->returnError(1033, $this->version);
      return;
    }
    $link_entities = $this->_get_linked_entities_from_xml($xml);
    $errors = keys($link_entities) - array($main_knowledge_type);
    if (count($errors) > 0) {
      $this->returnError(1034, $this->version, 'Illegal knowledge_type(s): ' . implode(', ', $errors));
      return;
    }
    if ($benchmark_suite) {
      if ($main_knowledge_type != 'run') {
        $this->returnError(1035, $this->version);
        return;
      }
      
      $benchmark_suite = $this->Study->get_by_id($benchmark_suite);
      if (!$benchmark_suite) {
        $this->returnError(1036, $this->version);
        return;
      }
      
      if ($benchmark_suite->main_knowledge_type != 'task') {
        $this->returnError(1037, $this->version);
        return;
      }
    }
    
    $this->db->trans_start();
    
    $schedule_data = array(
      'alias' => $xml->children('oml', true)->{'alias'}, 
      'main_knowledge_type' => $main_knowledge_type,
      'benchmark_suite' => $benchmark_suite,
      'name' => $xml->children('oml', true)->{'name'}, 
      'description' => $xml->children('oml', true)->{'description'}, 
      'visibility' => 'public',
      'creation_date' => now(),
      'creator' => $this->user_id,
      'legacy' => 'n', 
    );
    $study_id = $this->insert($schedule_data);
    
    $this->_link_entities($study_id, $link_entities);
    
	  if ($this->db->trans_status() === FALSE) {
	    $this->returnError(1038, $this->version);
      return;
    }
  }

  private function study_delete($study_id) {

    $study = $this->Study->getById($study_id);
    if ($study == false) {
      $this->returnError(592, $this->version);
      return;
    }

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


  private function study_list() {
    $studies = $this->Study->getWhere('visibility = "public" or creator = ' . $this->user_id);

    if (count($studies) == 0) {
      $this->returnError(590, $this->version);
      return;
    }

    $this->xmlContents('study-list', $this->version, array('studies' => $studies));
  }

  private function study_by_id($study_id, $knowledge_type) {
    $study = $this->Study->getById($study_id);

    if ($study == false) {
      $this->returnError(601, $this->version);
      return;
    }

    $this->_legacy_study_get($study, $knowledge_type);
  }

  private function study_by_alias($study_alias, $knowledge_type) {
    $study = $this->Study->getWhereSingle('alias = "' . $study_alias . '"');

    if ($study == false) {
      $this->returnError(601, $this->version);
      return;
    }
    
    if ($study->legacy == 'y') {
      $this->_legacy_study_get($study, $knowledge_type);
    }
  }
  
  // TODO: remove ASAP
  private function _legacy_study_get($study, $knowledge_type) {
    $valid_knowlegde_types = array('runs', 'flows', 'setups', 'data', 'tasks', NULL);
    if (!in_array($knowledge_type, $valid_knowlegde_types)) {
      $this->returnError(600, $this->version);
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

    if ($knowledge_type == null || $knowledge_type == 'data') {
      $data = $this->Study_tag->getDataIdsFromStudy($study->id);
    }

    if ($knowledge_type == null || $knowledge_type == 'tasks') {
      $tasks = $this->Study_tag->getTaskIdsFromStudy($study->id);
    }

    if ($knowledge_type == null || $knowledge_type == 'flows') {
      $flows = $this->Study_tag->getFlowIdsFromStudy($study->id);
    }

    if ($knowledge_type == null || $knowledge_type == 'setups') {
      $setups = $this->Study_tag->getSetupIdsFromStudy($study->id);
    }

    if ($knowledge_type == null || $knowledge_type == 'runs') {
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
  
  private function _study_get($study, $knowledge_type) {
    $valid_knowlegde_types = array('runs', 'flows', 'setups', 'data', 'tasks', NULL);
    if (!in_array($knowledge_type, $valid_knowlegde_types)) {
      $this->returnError(600, $this->version);
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
  
  private function _get_linked_entities_from_xml($xml) {
    $linked_entities = array();
    foreach ($legal_knowledge_types as $lkt) {
      $outer_tag = $lkt . 's';
      $inner_tag = $lkt . '_id';
      if ($xml->children('oml', true)->{$outer_tag}) {
        $linked_entities[$lkt] = $xml->children('oml', true)->{$outer_tag}->{$inner_tag};
      }
    }
    return $linked_entities;
  }
  
  private function _link_entities($study_id, $link_entities) {
    // study_id is int, link_entities is array mapping from knowledge type to
    // array of integer ids
    $study = $this->Study->get_by_id($study_id);
    $model = ucfirst($study->main_knowledge_type) . '_study';
    $id_name = $study->main_knowledge_type . '_id';
    
    foreach ($link_entities[$study->main_knowlegde_type] as $id) {
      $data = array(
        'study_id' => $study_id,
        $id_name => $id,
        'uploader' => $uploader_id,
        'date' => now(),
      );
      $this->{$model}->insert_ignore($data);
    }
  }
}

?>

