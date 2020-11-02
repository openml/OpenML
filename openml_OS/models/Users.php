<?php
class Users extends MY_Community_Model {
  
  function __construct() {
    parent::__construct();
    $this->table = 'users';
    $this->id_column = 'id';
    
    $this->load->model('File');
    $this->load->helper('file'); 
    
    $this->content_folder_modulo = 10000;
  }
  
  function update_image($user_id, $tmp_file_location) {
    $additional_data = array();
    resize_image_squared($tmp_file_location, $this->config->item('max_avatar_size'));
    $subdirectory = floor($user_id / $this->content_folder_modulo) * $this->content_folder_modulo;
    $to_directory = 'userdata_structured/' . $subdirectory . '/' . $user_id . '/';
    $file_id = $this->File->register_uploaded_file($_FILES['image'], $to_directory, $user_id, 'userimage');
    if($file_id) {
      $additional_data['image'] = $this->config->item('data_controller') . '/view/' . $file_id . '/' . $_FILES['image']['name'];
	    $update = $this->ion_auth->update($user_id, $additional_data);
	    return $update;
    } else {
      return false;
    }
  }
  
}
?>
