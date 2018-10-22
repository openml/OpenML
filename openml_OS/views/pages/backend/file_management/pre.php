<?php
$this->directories = directory_map(APPPATH.'views/pages/backend', 1);

$sql = 'SELECT * FROM file; ';

$all_records = $this->File->get();
$all_files = array();

$this->missing_columns = array( 'id', 'creator', 'creation_date', 'filesize', 'filename_original', 'type', 'access_policy' );
$this->missing_items = array();
$this->missing_name = 'Missing files';

$this->size_columns = array( 'id', 'creator', 'creation_date', 'filesize', 'real', 'filename_original', 'type' );
$this->size_items = array();
$this->size_name = 'Filesize mismatch';
$this->size_api_delete_function = null;

for( $i = 0; $i < count($all_records); ++$i ) {
  // maintain a list of all files that are allowed to exists
  //$all_files[$all_records[$i]->filepath] = true;


  if( file_exists( DATA_PATH . $all_records[$i]->filepath ) == false ) {
    // mark records with missing files on FS
    $this->missing_items[] = $all_records[$i];
  } else {
    $real_filesize = filesize( DATA_PATH . $all_records[$i]->filepath );
    if( $real_filesize != $all_records[$i]->filesize ) {
      $all_records[$i]->real = $real_filesize;
      $this->size_items[] = $all_records[$i];
    }
  }
}

$this->missing_api_delete_function = null; /* array(
  'function'        => 'openml.user.delete',
  'key'             => 'user_id',
  'filter'          => 'may_delete',
  'id_field'        => 'id',
  'identify_field'  => 'name' );*/

?>
