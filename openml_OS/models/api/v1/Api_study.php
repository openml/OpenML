<?php
class Api_study extends MY_Api_Model {

  protected $version = 'v1';

  function __construct() {
    parent::__construct();

    $this->load->model('Study');
  }

  function bootstrap($format, $segments, $request_type, $user_id) {
    $this->outputFormat = $format;

    $getpost = array('get','post');

    if (count($segments) == 1 && $segments[0] == 'list') {
      array_shift($segments);
      $this->study_list($segments);
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

    $this->returnError( 100, $this->version );
  }

  private function study_delete($study_id) {

    $study = $this->Study->getById( $study_id );
    if( $study == false ) {
      $this->returnError( 592, $this->version );
      return;
    }

    $result = $this->Study->delete( $study_id );
    if( $result == false ) {
      $this->returnError( 593, $this->version );
      return;
    }

    try {
      $this->elasticsearch->delete('study', $study_id);
    } catch (Exception $e) {
      $additionalMsg = get_class() . '.' . __FUNCTION__ . ':' . $e->getMessage();
      $this->returnError(105, $this->version, $this->openmlGeneralErrorCode, $additionalMsg);
      return;
    }

    $this->xmlContents( 'study-delete', $this->version, array( 'study' => $study ) );
  }


  private function study_list() {
    $studies = $this->Study->getWhere('visibility = "public" or creator = ' . $this->user_id);

    if (count($studies) == 0) {
      $this->returnError(590, $this->version );
      return;
    }

    $this->xmlContents('study-list', $this->version, array('studies' => $studies));
  }

  private function study_by_id($study_id,$knowledge_type) {
    $study = $this->Study->getById($study_id);

    if ($study == false) {
      $this->returnError(601, $this->version);
      return;
    }

    $this->_study_get($study,$knowledge_type);
  }

  private function study_by_alias($study_alias,$knowledge_type) {
    $study = $this->Study->getWhereSingle('alias = "' . $study_alias . '"');

    if ($study == false) {
      $this->returnError(601, $this->version);
      return;
    }

    $this->_study_get($study,$knowledge_type);
  }

  private function _study_get($study,$knowledge_type) {
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
}
?>
