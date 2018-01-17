<?php
if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Wiki {

  public function __construct() {
    $this->CI = &get_instance();
    $this->CI->load->model('Dataset');
    $this->CI->load->model('Implementation');
    $this->db = $this->CI->Dataset;
    $this->flow = $this->CI->Implementation;
    $this->study = $this->CI->Study;
    $this->userdb = $this->CI->Author;
    $this->CI->load->helper('file');
    $this->CI->load->Library('curlHandler');
  }

  public function export_to_wiki($id){

    $d = $this->db->getByID($id);
    $user = $this->userdb->getById($d->uploader);
  	$wikipage = 'data-'.$d->did;
    $preamble = "";
    if(!startswith($d->description,'**Author**')){
      $preamble = '**Author**: '.trim($d->creator, '"').'  '.PHP_EOL;
    	if($d->contributor)
    		$preamble .= trim($d->contributor, '"').'  '.PHP_EOL;
    	$preamble .= '**Source**: '.($d->original_data_url ? '[original]('.$d->original_data_url.')' : 'Unknown').' - '.($d->collection_date ? $d->collection_date : 'Date unknown').'  '.PHP_EOL;
    	$preamble .= '**Please cite**: '.$d->citation.'  '.PHP_EOL.PHP_EOL;
    }

	$data = $d->description;

	$post_data = array(
	      'page' => $wikipage,
	      'path' => '/',
	      'content' => $preamble.$data,
	      'format' => 'md',
	      'message' => 'Automatically added');

	$url = WIKI_URL . '/create';
	//call gollum
  	$api_response = $this->CI->curlhandler->post_multipart_helper( $url, $post_data );
	if(strlen($api_response)>0)
		return $api_response;

	return "Successfully added ".$wikipage;
  }

  public function export_flow_to_wiki($id){

  $f = $this->flow->getByID($id);
  $user = $this->userdb->getById($f->uploader);
	$wikipage = 'flow-'.$f->id;
  $preamble = "";
  if(!startswith($d->description,'**Author**')){
  	$preamble = '**Author**: '.trim($f->creator, '"').'  '.PHP_EOL;
  	if($f->contributor)
  		$preamble .= trim($f->contributor, '"').'  '.PHP_EOL;
  	$preamble .= '**Please cite**: '.$f->citation.'  '.PHP_EOL.PHP_EOL;
  }
	$data = $f->description;

	$post_data = array(
	      'page' => $wikipage,
	      'path' => '/',
	      'content' => $preamble.$data,
	      'format' => 'md',
	      'message' => 'Automatically added');

	$url = WIKI_URL . '/create';

	//call gollum
  	$api_response = $this->CI->curlhandler->post_multipart_helper( $url, $post_data );
	if(strlen($api_response)>0)
		return $api_response;

	return "Successfully added ".$wikipage;
  }

  public function export_study_to_wiki($id){

  $s = $this->study->getByID($id);
  $user = $this->userdb->getById($s->creator);
  $wikipage = 'study-'.$s->id;
  $preamble = "";
  $data = $s->description;

  $post_data = array(
        'page' => $wikipage,
        'path' => '/',
        'content' => $preamble.$data,
        'format' => 'md',
        'message' => 'Automatically added on creation');

  $url = WIKI_URL . '/create';

  //call gollum
    $api_response = $this->CI->curlhandler->post_multipart_helper( $url, $post_data );
  if(strlen($api_response)>0)
    return $api_response;

  return "Successfully added ".$wikipage;
  }

  public function import_from_wiki($id){

        $d = $this->db->getByID($id);
	$this->wikipage = str_replace('_','-',$d->name.'-'.$d->version);

	$myFile = "/openmldata/webdata/wiki/".$this->wikipage.".md";
	$fh = fopen($myFile, 'r');
	$theData = fread($fh, filesize($myFile));
	fclose($fh);

  	$this->db->query('update dataset set description = "'.addslashes($theData).'" where did='.$id);

	return "Successfully imported ".$this->wikipage;
  }
}
?>
