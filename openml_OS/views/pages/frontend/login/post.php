<?php
if (!empty($_POST['submitlogin'])) {
	if ($this->ion_auth->login($this->input->post('identity'), $this->input->post('password'), false))
	{
		  if(strpos($this->input->post('location'), 'login') !== false){ //avoid redirecting to login page after successful login
				redirect('home');
			} else { // redirect to whereever you were on the website
			  // remove tailing slash
			  $loc = $this->input->post('location');
			  if (substr($loc,0,1) === '/') {
			    $loc = substr($loc,1);
			  }
				redirect(BASE_URL . $loc);
			}
			exit();
	}
	else
	{
		$this->session->set_flashdata('message', $this->ion_auth->errors());
		redirect('home');
		exit();
	}
}

?>
