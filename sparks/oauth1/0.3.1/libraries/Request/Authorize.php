<?php

class OAuth_Request_Authorize extends OAuth_Request {

	protected $name = 'request';

	// http://oauth.net/core/1.0/#rfc.section.6.2.1
	protected $required = array(
		'oauth_token' => TRUE,
	);

	public function execute(array $options = NULL)
	{
		return redirect($this->as_url());
	}

} // End Request_Authorize
