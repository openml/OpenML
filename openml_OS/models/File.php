<?php

class File extends MY_Community_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'file';
    $this->id_column = 'id';
    $this->deleted_activated = 'id IS NOT NULL ';
  }
  
  function register_uploaded_file($file, $to_folder, $creator_id, $type, $access_policy = 'public') {
    $md5_hash = md5_file($file['tmp_name']);
    if($md5_hash === false) {
      return false;
    }
    
    mkdir(DATA_PATH . $to_folder, $this->config->item('content_directories_mode'), true);
    $newName = getAvailableName( DATA_PATH . $to_folder, $file['name'] );
    
    if( move_uploaded_file( $file['tmp_name'], DATA_PATH . $to_folder . $newName ) === false ) {
      return false;
    }
    
    $file_record = array(
      'creator' => $creator_id,
      'creation_date' => now(),
      'filepath' => $to_folder . $newName,
      'filesize' => filesize( DATA_PATH . $to_folder . $newName ),
      'filename_original' => $file['name'],
      'extension' => pathinfo($file['name'], PATHINFO_EXTENSION),
      'mime_type' => $file['type'],
      'md5_hash' => $md5_hash,
      'type' => $type,
      'access_policy' => $access_policy
    );
 
    $file_id = $this->insert($file_record);
    return $file_id;
  }
  
  function register_url($url, $filename_original, $file_type, $creator_id, $access_policy = 'public') {
    $headers_request = get_headers($url);
    if ($headers_request == false) {
      return false;
    }
    $headers = $this->parse_headers($headers_request);
    
    $filesize = null;
    $mime_type = null;
    if (array_key_exists('Content-Length', $headers)) { 
      $filesize = $headers['Content-Length'];
    } 
    
    if (array_key_exists('Content-Type', $headers)) { 
      $mime_type = $headers['Content-Type'];
    }
    
    if (!$mime_type || !$filesize) {
      return false;
    }
    
    $md5_hash  = md5_file($url);
    
    $file_record = array(
      'creator' => $creator_id,
      'creation_date' => now(),
      'filepath' => $url,
      'filesize' => $filesize,
      'filename_original' => $filename_original,
      'extension' => $file_type,
      'mime_type' => $mime_type,
      'md5_hash' => $md5_hash,
      'type' => 'url',
      'access_policy' => $access_policy
    );
    return $this->insert($file_record);
  }
  
  function register_created_file($folder, $file, $creator_id, $type, $mime_type, $access_policy = 'public') {
    $full_path = DATA_PATH . $folder . $file;
    $md5_hash = md5_file($full_path);
    if($md5_hash === false) {
      return false;
    }
    $file_record = array(
      'creator' => $creator_id,
      'creation_date' => now(),
      'filepath' => $folder . $file,
      'filesize' => filesize($full_path),
      'filename_original' => $file,
      'extension' => pathinfo($file, PATHINFO_EXTENSION),
      'mime_type' => $mime_type,
      'md5_hash' => $md5_hash,
      'type' => $type,
      'access_policy' => $access_policy
    );
    return $this->insert($file_record);
  }
  
  function move_file($file_id, $to_folder) {
    // moves a registered file by copying it to another folder, updating the
    // database record, and removing the old record (to prevent losing a file
    // on failure). Note that this function requires the to_folder not have a 
    // file with this name already. 
    $record = $this->getById($file_id);
    if ($record === false) {
      return false;
    }
    $file_path_old = $record->filepath;
    $file_path_new = $to_folder . '/' . $record->filename_original;
    if (file_exists(DATA_PATH . $file_path_new )) {
      return false;
    }
    
    mkdir(DATA_PATH . $to_folder, $this->config->item('content_directories_mode'), true);
    $cp_result = copy(DATA_PATH . $file_path_old, DATA_PATH . $file_path_new);
    if ($cp_result === false) {
      return false;
    }
    
    $db_result = $this->File->update($file_id, array('filepath' => $file_path_new));
    if ($db_result === false) {
      // problem! try to remove the new copy
      unlink(DATA_PATH . $file_path_new);
      return false;
    } else {
      // all OK. try to remove the old copy
      unlink(DATA_PATH . $file_path_old);
      return true;
    }
  }
  
  function delete_file($id) {
    $file = $this->getById($id);
    if ($file == false) return false;
    
    $filepath = DATA_PATH . $file->filepath;
    if (file_exists($filepath)) {
      $success = unlink($filepath);
      if ($success) {
        $this->db->delete($this->table, array($this->id_column => $id));
        return true;
      } else {
        // TODO: log in DB
        return false;
      }
    } else {
      // TODO: log in db
      return false;
    }
  }
  
  private function parse_headers($headers) {
    $result = array();
    foreach($headers as $header) {
      $colon = strpos($header, ':');
      if ($colon != false) {
        $key = trim(substr($header, 0, $colon));
        $value = trim(substr($header, $colon+1));
        $result[$key] = $value;
      }
    }
    return $result;
  }
}
?>
