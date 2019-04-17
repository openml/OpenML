<?php
class Data_server extends CI_Model {

  function __construct() {
    parent::__construct();

    // load models
    $this->load->Model('Dataset');
    $this->load->Model('Data_feature');
    $this->load->Model('File');
    $this->load->Model('Implementation');

    $this->load->helper('file_upload');

    $this->load->Library('ion_auth');
  }


  function download($id, $name = 'undefined') {
    $file = $this->File->getById($id);
    if (!$file) {
      $this->_error404();
      return;
    }

    if (!$this->_check_rights($file)) {
      $this->_error403();
      return;
    }

    if (!file_exists(DATA_PATH . $file->filepath) && $file->type != 'url') {
      $this->_error404();
      return;
    }
    
    if (filesize(DATA_PATH . $file->filepath) != $file->filesize) {
      $this->_email_filesize_error($file);
      $this->_error404();
      return;
    }

    // in case of externally linked file, handle alternativelly
    if ($file->{'type'} == 'url') {
      header('Location: ' . $file->filepath);
    } else {
      $this->_header_download($file->filename_original, $file->filesize, $file->extension, $file->mime_type);
      readfile_chunked(DATA_PATH . $file->filepath);
    }
  }

  function view($id, $name = 'undefined') {
    $file = $this->File->getById($id);
    if ($file === false) {
      $this->_error404();
      return;
    }

    if (!$this->_check_rights($file)) {
      $this->_error403();
      return;
    }

    if (!file_exists(DATA_PATH . $file->filepath) && $file->type != 'url') {
      $this->_error404();
      return;
    }
    
    if (filesize(DATA_PATH . $file->filepath) != $file->filesize) {
      $this->_email_filesize_error($file);
      $this->_error404();
      return;
    }

    // in case of externally linked file, handle alternativelly
    if ($file->{'type'} == 'url') {
      header('Location: ' . $file->filepath);
    } else {
      header('Content-type: ' . $file->mime_type);
      header('Content-Length: ' . $file->filesize);
      readfile(DATA_PATH . $file->filepath);
    }
  }

  function get_csv($id, $name='undefined') {
    # TODO: caching mechanism to
    $file = $this->File->getById($id);

    # check file rights
    if (!$this->_check_rights($file)) {
      $this->_error403();
      return;
    }

    # file does not exist, or is no valid arff
    if (!$file || strtolower($file->extension) != 'arff') {
      # TODO: think of more meaningfull error
      $this->_error404();
      return;
    }

    // in case of externally linked file, handle alternativelly
    $location = DATA_PATH . $file->filepath;
    if ($file->type == 'url') {
      $location = $file->filepath;
    }

    $handle = fopen($location, 'r');
    $position = -1;
    for ($i = 0; ($line = fgets($handle)) !== false; ++$i) {
      // process the line read.
      if (trim(strtolower($line)) == '@data') {
        $position = ftell($handle);
        break;
      }
    }

    if ($position < 0) { # apparently we didn't find '@data'
      # TODO: more meaningfull error
      $this->_error404();
      return;
    }

    $dataset = $this->Dataset->getWhereSingle('file_id = ' . $id);

    // obtain header
    $features = $this->Data_feature->getColumnWhere('name', 'did = "' . $dataset->did . '"', 'index ASC');
    if ($features < 2) {
      # TODO: more meaningfull error
      $this->_error404();
      return;
    }
    
    $this->_header_download($file->filename_original, null, 'csv', 'text/plain');
    
    echo '"' . implode('","', $features) . "\"\n";
    for ($i = 0; ($line = fgets($handle)) !== false; ++$i) {
      if (trim($line[0]) == '%') {
        continue;
      } else {
        echo $line;
      }
    }
  }

  private function _check_rights($file) {
    if($file->access_policy == 'public') {
      return true;
    }

    if($this->ion_auth->is_admin($this->user_id)) {
      return true;
    }

    elseif($file->access_policy == 'private') {
      if($this->user_id == $file->creator) {
        return true;
      } else {
        $this->_error403();
      }
    }

    elseif($file->access_policy == 'deleted') {
      $this->_error404();
    }

    elseif($file->access_policy == 'none') {
      $this->_error403();
    }
  }

  private function _error404() {
    http_response_code(404);
    $this->load->view('404');
  }

  private function _error403() {
    http_response_code(403);
    $this->load->view('403');
  }

  private function _header_download($filename, $filesize, $extension, $mime_type) {
    // formats the download header based on information from the file record
    // filename and filesize should come from the file record. extension and 
    // mimetype can be overridden
    header('Content-Description: File Transfer');
    header('Content-Type: ' . ($extension == 'arff' ? 'text/plain' : $mime_type));
    if ($filesize != null) {
       header('Content-Length: ' . $filesize);
    }
    
    // need to rename file, as extension is potentially overwritten
    $filename = pathinfo($filename, PATHINFO_FILENAME) . '.' . $extension;
    header('Content-Disposition: attachment; filename='.$filename);
    header('Content-Transfer-Encoding: binary');
    header('Expires: 0');
    header('Cache-Control: must-revalidate');
    header('Pragma: public');
    header('Connection: keep-alive');
    header('Keep-Alive: timeout=300, max=500');
  }
  
  private function _email_filesize_error($file) {
    $to = EMAIL_API_LOG;
    $subject = 'OpenML File Error. Id: ' . $file->id;
    $content = 'File record with id' . $file->id . ' (' . $file->type . ') states ' . $file->filesize . ' but PHP filesize states: ' . filesize(DATA_PATH . $file->filepath);
    sendEmail($to, $subject, $content,'text');
  }
}
?>
