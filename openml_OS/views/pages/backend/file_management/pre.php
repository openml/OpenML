<?php
$this->directories = directory_map(APPPATH.'views/pages/backend', 1);

$this->page_limit = 100000;
$this->page_nr = 0;
if ($this->input->get('page')) {
	$this->page_nr = safe($this->input->get('page'));
}

$all_records = $this->File->getWhere('type != "url"', null, $this->page_limit, $this->page_nr * $this->page_limit);
$all_files = array();

$this->missing_columns = array('id', 'creator', 'creation_date', 'filesize', 'filename_original', 'type', 'access_policy');
$this->missing_items = array();
$this->missing_name = 'Missing files';

$this->size_columns = array('id', 'creator', 'creation_date', 'filesize', 'real', 'filename_original', 'type');
$this->size_items = array();
$this->size_name = 'Filesize mismatch';
$this->size_api_delete_function = null;

for ($i = 0; $i < count($all_records); ++$i) {


  if (file_exists( DATA_PATH . $all_records[$i]->filepath ) == false) {
    // mark records with missing files on FS
    $this->missing_items[] = $all_records[$i];
  } else {
    $real_filesize = filesize( DATA_PATH . $all_records[$i]->filepath );
    if ($real_filesize != $all_records[$i]->filesize) {
      $all_records[$i]->real = $real_filesize;
      $this->size_items[] = $all_records[$i];
    }
  }
}

$this->missing_api_delete_function = null; 

?>
