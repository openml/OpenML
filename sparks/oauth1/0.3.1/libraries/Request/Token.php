<?php

class OAuth_Request_Token extends OAuth_Request {

	protected $name = 'request';

	// http://oauth.net/core/1.0/#rfc.section.6.3.1
	protected $required = array(
		'oauth_callback'         => TRUE,
		'oauth_consumer_key'     => TRUE,
		'oauth_signature_method' => TRUE,
		'oauth_signature'        => TRUE,
		'oauth_timestamp'        => TRUE,
		'oauth_nonce'            => TRUE,
		'oauth_version'          => TRUE,
	);


	public function execute(array $options = NULL)
	{
		return new OAuth_Response(parent::execute($options));
	}

} // End Request_Token
