<?php

class OAuth_Provider_Google extends OAuth_Provider {

	public $name = 'google';
	
	/**
	 * @var  string  scope separator, most use "," but some like Google are spaces
	 */
	public $scope_seperator = ' ';

	public function url_request_token()
	{
		return 'https://www.google.com/accounts/OAuthGetRequestToken';
	}

	public function url_authorize()
	{
		return 'https://www.google.com/accounts/OAuthAuthorizeToken';
	}

	public function url_access_token()
	{
		return 'https://www.google.com/accounts/OAuthGetAccessToken';
	}
	
	public function __construct(array $options = array())
	{
		// Now make sure we have the default scope to get user data
		$options['scope'] = \Arr::merge(
			
			// We need this default feed to get the authenticated users basic information
			// array('https://www.googleapis.com/auth/plus.me'),
			array('https://www.google.com/m8/feeds'),
			
			// And take either a string and array it, or empty array to merge into
			(array) \Arr::get($options, 'scope', array())
		);
		
		parent::__construct($options);
	}
	
	public function get_user_info(OAuth_Consumer $consumer, OAuth_Token $token)
	{
		// Create a new GET request with the required parameters
		$request = Request::forge('resource', 'GET', 'https://www.google.com/m8/feeds/contacts/default/full?max-results=1&alt=json', array(
			'oauth_consumer_key' => $consumer->key,
			'oauth_token' => $token->access_token,
		));

		// Sign the request using the consumer and token
		$request->sign($this->signature, $consumer, $token);

		$response = json_decode($request->execute(), true);
		
		// Fetch data parts
		$email = \Arr::get($response, 'feed.id.$t');
		$name = \Arr::get($response, 'feed.author.0.name.$t');
		$name == '(unknown)' and $name = $email;
		
		return array(
			'uid' => $email,
			'nickname' => \Inflector::friendly_title($name),
			'name' => $name,
			'email' => $email,
			'location' => null,
			'image' => null,
			'description' => null,
			'urls' => array(),
		);
	}

} // End Provider_Gmail
