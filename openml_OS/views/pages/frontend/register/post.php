<?php
$this->form_validation->set_rules('last_name', 'Last Name', 'required');
$this->form_validation->set_rules('email', 'Email Address', 'required|valid_email');
$this->form_validation->set_rules('password', 'Password (min 8 characters)', 'required|min_length[8]|max_length[64]|matches[password_confirm]');
$this->form_validation->set_rules('password_confirm', 'Password Confirmation', 'required');

if ($this->form_validation->run() == true)
{
  $username = $this->input->post('email',TRUE); //TRUE enables XSS filtering
  $email    = $this->input->post('email',TRUE);
  $password = $this->input->post('password',TRUE);

  $additional_data = array(
    'first_name' => $this->input->post('first_name',TRUE),
    'last_name'  => $this->input->post('last_name',TRUE),
    'affiliation'=> $this->input->post('affiliation',TRUE),
    'country'    => $this->input->post('country',TRUE),
    'bio'    	 => $this->input->post('bio',TRUE),
    'external_source' => null,
    'external_id' => null,
    'session_hash' => md5(rand())
  );

  if( check_uploaded_file( $_FILES['image'] ) ) {
    resize_image_squared($_FILES['image']['tmp_name'], $this->config->item('max_avatar_size') );
    $file_id = $this->File->register_uploaded_file($_FILES['image'], 'userdata/', -1, 'userimage');
    if($file_id) {
      $additional_data['image'] = $this->data_controller . 'view/' . $file_id . '/' . $_FILES['image']['name'];
    }
  }
  $user_id = $this->ion_auth->register($username, $password, $email, $additional_data);
  if ( $user_id )
  {
    //check to see if we are creating the user
    //redirect them back to the admin page
    $this->session->set_flashdata('message', $this->ion_auth->messages());
    redirect('frontend/page/register');
  } else {
    $this->session->set_flashdata('message', $this->ion_auth->errors());
    redirect('frontend/page/register');
  }
}
else
{
  $_POST['warningmessage'] = validation_errors();
}

?>
