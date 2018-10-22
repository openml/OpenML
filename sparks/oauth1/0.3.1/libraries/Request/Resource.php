<?php

class OAuth_Request_Resource extends OAuth_Request {

	protected $name = 'resource';

	// http://oauth.net/core/1.0/#rfc.section.7
	protected $required = array(
		'oauth_consumer_key'     => TRUE,
		'oauth_token'            => TRUE,
		'oauth_signature_method' => TRUE,
		'oauth_signature'        => TRUE,
		'oauth_timestamp'        => TRUE,
		'oauth_nonce'            => TRUE,
		'oauth_version'          => TRUE,
	);

} // End Request_Resource
