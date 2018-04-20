<?php
class MY_Api_Model extends CI_Model {
  protected $outputFormat = 'xml';
  function __construct() {
    parent::__construct();
    $this->load->helper('text');
    $this->legal_tag_entities = array('data','task','flow','setup','run');
    $this->openmlGeneralErrorCode = $this->config->item('general_http_error_code');
  }
  function xmlEscape($string) {
    return str_replace(array('&', '<', '>', '\'', '"'), array('&amp;', '&lt;', '&gt;', '&apos;', '&quot;'), $string);
  }
  // taken from: http://outlandish.com/blog/xml-to-json/
  function xmlToArray($xml, $options = array()) {
    $defaults = array(
        'namespaceSeparator' => '',//you may want this to be something other than a colon
        'attributePrefix' => '',    //to distinguish between attributes and nodes with the same name
        'alwaysArray' => array(),   //array of xml tag names which should always become arrays
        'autoArray' => true,        //only create arrays for tags which appear more than once
        'textContent' => 'value',   //key used for the text content of elements
        'autoText' => true,         //skip textContent key if node has no attributes or child nodes
        'keySearch' => false,       //optional search and replace on tag and attribute names
        'keyReplace' => false       //replace values for above search values (as passed to str_replace())
    );
    $options = array_merge($defaults, $options);
    $namespaces = $xml->getDocNamespaces();
    $namespaces[''] = null; //add base (empty) namespace
    //get attributes from all namespaces
    $attributesArray = array();
    foreach ($namespaces as $prefix => $namespace) {
        $prefix = ''; //ignore namespaces
        foreach ($xml->attributes($namespace) as $attributeName => $attribute) {
            //replace characters in attribute name
            if ($options['keySearch']) $attributeName =
                    str_replace($options['keySearch'], $options['keyReplace'], $attributeName);
            $attributeKey = $options['attributePrefix']
                    . ($prefix ? $prefix . $options['namespaceSeparator'] : '')
                    . $attributeName;
            $attributesArray[$attributeKey] = (string)$attribute;
        }
    }
    //get child nodes from all namespaces
    $tagsArray = array();
    foreach ($namespaces as $prefix => $namespace) {
        $prefix = ''; //ignore namespaces
        foreach ($xml->children($namespace) as $childXml) {
            //recurse into child nodes
            $childArray = $this->xmlToArray($childXml, $options);
            list($childTagName, $childProperties) = each($childArray);
            //replace characters in tag name
            if ($options['keySearch']) $childTagName =
                    str_replace($options['keySearch'], $options['keyReplace'], $childTagName);
            //add namespace prefix, if any
            if ($prefix) $childTagName = $prefix . $options['namespaceSeparator'] . $childTagName;
            if (!isset($tagsArray[$childTagName])) {
                //only entry with this key
                //test if tags of this type should always be arrays, no matter the element count
                $tagsArray[$childTagName] =
                        in_array($childTagName, $options['alwaysArray']) || !$options['autoArray']
                        ? array($childProperties) : $childProperties;
            } elseif (
                is_array($tagsArray[$childTagName]) && array_keys($tagsArray[$childTagName])
                === range(0, count($tagsArray[$childTagName]) - 1)
            ) {
                //key already exists and is integer indexed array
                $tagsArray[$childTagName][] = $childProperties;
            } else {
                //key exists so convert to integer indexed array with previous value in position 0
                $tagsArray[$childTagName] = array($tagsArray[$childTagName], $childProperties);
            }
        }
    }
    //get text content of node
    $textContentArray = array();
    $plainText = trim((string)$xml);
    if ($plainText !== '') $textContentArray[$options['textContent']] = $plainText;
    //stick it all together
    $propertiesArray = !$options['autoText'] || $attributesArray || $tagsArray || ($plainText === '')
            ? array_merge($attributesArray, $tagsArray, $textContentArray) : $plainText;
    //return node as array
    return array(
        $xml->getName() => $propertiesArray
    );
  }
  public function returnError($code, $version, $httpErrorCode = 412, $additionalInfo = null, $emailLog = false, $supress_output = false) {
    $this->Log->api_error('error', $_SERVER['REMOTE_ADDR'], $code, $_SERVER['QUERY_STRING'], $this->load->apiErrors[$code] . (($additionalInfo == null)?'':$additionalInfo) );
    $error['code'] = $code;
    $error['message'] = htmlentities( $this->load->apiErrors[$code] );
    $error['additional'] = htmlentities( $additionalInfo );
    if (!$supress_output) {
      http_response_code($httpErrorCode);
      $this->xmlContents('error-message', $version, $error);
    }
    if ($emailLog && defined('EMAIL_API_LOG')) {
      $to = EMAIL_API_LOG;
      $subject = 'OpenML API Exception: ' . $code;
      $content = 'Time: ' . now() . "\nUser: ". $this->user_id . ' (' . $this->user_email . ')' . "\nMessage: " . $error['message'] . "\nException Message: " . $emailLog;
      sendEmail($to, $subject, $content,'text');
    }
  }
  protected function xmlContents($xmlFile, $version, $source) {
    $view = 'pages/'.$this->controller.'/' . $version . '/' . $this->page.'/'.$xmlFile.'.tpl.php';
    if ($this->outputFormat == 'json') {
      $jsonTemplate = 'pages/'.$this->controller.'/' . $version . '/json/'.$xmlFile.'.tpl.php';
      if (file_exists(APPPATH . 'views/' . $jsonTemplate)) { // if we have native json templates
        $json = $this->load->view($jsonTemplate, $source, true);
        header('Content-length: ' . strlen($json) );
        header('Content-type: application/json; charset=utf-8');
        echo $json;
      } else { // use xml template and convert to json
        $data = $this->load->view($view, $source, true);
        $xml = simplexml_load_string($data);
        $json = json_encode($this->xmlToArray($xml));
        header('Content-length: ' . strlen($json));
        header('Content-type: application/json; charset=utf-8');
        echo $json;
      }
    } else { // output format = xml, use plain xml templates
      $data = $this->load->view($view, $source, true);
      header('Content-length: ' . strlen($data) );
      header('Content-type: text/xml; charset=utf-8');
      echo $data;
    }
  }
  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   * @function entity_tag_untag:
   *    tags or untags an entity (data, flow, task, setup, run)
   *
   * @param type (string):
   *    in {dataset, implementation, run, task, algorithm_setup} (pointing
   *    to a database table)
   * @param entity_id (int):
   *    the id in the table mentioned by type
   * @param tag (str):
   *    the name of the tag
   * @param do_untag (bool):
   *    tags iff false, untags iff false
   * @param special_name (str):
   *    used by ES and the xml tag. (data, flow, task, setup, run)
   *
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
  protected function entity_tag_untag($type, $id, $tag, $do_untag, $special_name, $supress_output = false) {
    // checks if type in {dataset, implementation, run, task, algorithm_setup}
    $taggable = $this->config->item('taggable_entities');
    if(!in_array($type, array_keys($taggable))) {
      $this->returnError(470, $this->version);
      return false;
    }
    if ($id == false || $tag == false) {
      $this->returnError(471, $this->version);
      return false;
    }
    $model_name_entity = ucfirst($type);
    $model_name_tag = ucfirst($taggable[$type]);
    $currentTime = now();
    $entity = $this->{$model_name_entity}->getById($id);
    if (!$entity) {
      $this->returnError(472, $this->version);
      return false;
    }
    if ($do_untag) {
      /* * * * * * * * * * *
       *     U N T A G     *
       * * * * * * * * * * */
      $tag_record = $this->{$model_name_tag}->getWhereSingle('id = ' . $id . ' AND tag = "' . $tag . '"');
      if ($tag_record == false) {
        $this->returnError(475, $this->version);
        return false;
      }
      $is_admin = $this->ion_auth->is_admin($this->user_id);
      if ($tag_record->uploader != $this->user_id && $is_admin == false) {
        $this->returnError(476, $this->version);
        return false;
      }
      $this->{$model_name_tag}->delete( array( $id, $tag ) );
    } else {
      /* * * * * * * * * * *
       *       T A G       *
       * * * * * * * * * * */
      $tags = $this->{$model_name_tag}->getColumnWhere('tag', 'id = ' . $id);
      if($tags != false && in_array($tag, $tags)) {
        $this->returnError(473, $this->version, 450, 'id=' . $id . '; tag=' . $tag);
        return false;
      }
      $tag_data = array(
        'id' => $id,
        'tag' => $tag,
        'uploader' => $this->user_id,
        'date' => $currentTime
      );
      $res = $this->{$model_name_tag}->insert($tag_data);
      if ($res == false) {
        $this->returnError(474, $this->version);
        return false;
      }
    }
    try {
      //update index
      if ($special_name != 'setup') { // setups can not be indexed
        $this->elasticsearch->update_tags($special_name, $id);
      }
      //update studies
      $studies_to_update = $this->Study_tag->studiesToUpdate($tag, $currentTime, $this->user_id);
      if ($studies_to_update != false) {
        foreach ($studies_to_update as $study_id) {
          $this->elasticsearch->index('study', $study_id);
        }
      }
    } catch (Exception $e) {
      $this->returnError(105, $this->version, $this->openmlGeneralErrorCode, $e->getMessage(), false, $supress_output);
      return false;
    }
    if (!$supress_output) {
      $tags = $this->{$model_name_tag}->getColumnWhere('tag', 'id = ' . $id);
      $this->xmlContents(
        'entity-tag',
        $this->version,
        array(
          'id' => $id,
          'xml_tag_name' => $special_name . '_' . ($do_untag ? 'untag' : 'tag'),
          'tags' => $tags)
      );
    }
    return true;
  }
}
?>
