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

  $user_data = array(
    'first_name' => $this->input->post('first_name',TRUE),
    'last_name'  => $this->input->post('last_name',TRUE),
    'company'    => $this->input->post('company',TRUE),
    'country'    => $this->input->post('country',TRUE),
    'bio'    	   => $this->input->post('bio',TRUE),
    'external_source' => null,
    'external_id' => null,
    'session_hash' => md5(rand())
  );
  
  $user_id = $this->ion_auth->register($username, $password, $email, $user_data);
  
  if (check_uploaded_file($_FILES['image'])) {
    $this->Users->update_image($user_id, $_FILES['image']['tmp_name']);
  }
  
  
  if ($user_id)
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
