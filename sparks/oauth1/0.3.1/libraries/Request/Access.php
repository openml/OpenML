<?php

class OAuth_Request_Access extends OAuth_Request {

	protected $name = 'access';

	protected $required = array(
		'oauth_consumer_key'     => TRUE,
		'oauth_token'            => TRUE,
		'oauth_signature_method' => TRUE,
		'oauth_signature'        => TRUE,
		'oauth_timestamp'        => TRUE,
		'oauth_nonce'            => TRUE,
		// 'oauth_verifier'         => TRUE,
		'oauth_version'          => TRUE,
	);

	public function execute(array $options = NULL)
	{
		return new OAuth_Response(parent::execute($options));
	}

} // End Request_Access
