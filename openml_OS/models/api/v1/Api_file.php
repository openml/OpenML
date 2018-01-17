<?php
class Api_file extends Api_model {
  
  protected $version = 'v1';
  
  function __construct() {
    parent::__construct();
    
    // load models
    $this->load->model('File');
  }

  function bootstrap($format, $segments, $request_type, $user_id) {
    $this->outputFormat = $format;
    
    $getpost = array('get','post');
    
    if (count($segments) == 0 && $request_type == 'post') {
      $this->file_upload();
      return;
    }
    
    $this->returnError( 100, $this->version );
  }
  
  
  private function file_upload() {
    if( $this->user_has_admin_rights == false ) {
      $this->returnError( 104, $this->version );
      return;
    }

    $file = isset( $_FILES['file'] ) ? $_FILES['file'] : false;
    if( ! check_uploaded_file( $file ) ) {
      $this->returnError( 491, $this->version );
      return;
    }

    $file_id = $this->File->register_uploaded_file($file, $this->data_folders['misc'], $this->user_id, 'run_uploaded_file');
    if( $file_id == false ) {
      $this->returnError( 492, $this->version );
      return;
    }

    $this->xmlContents( 'file-upload', $this->version, array( 'file_id' => $file_id, 'filename' => $file['name'] ) );
  }
}
?>
