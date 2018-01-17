<?php
/**
 * OAuth LinkedIn Provider
 *
 * Documents for implementing LinkedIn OAuth can be found at
 * <http://dev.twitter.com/pages/auth>.
 *
 * [!!] This class does not implement the LinkedIn API. It is only an
 * implementation of standard OAuth with Twitter as the service provider.
 *
 */

class OAuth_Provider_Linkedin extends OAuth_Provider {

	public $name = 'linkedin';

	public function url_request_token()
	{
		return 'https://api.linkedin.com/uas/oauth/requestToken';
	}

	public function url_authorize()
	{
		return 'https://api.linkedin.com/uas/oauth/authorize';
	}

	public function url_access_token()
	{
		return 'https://api.linkedin.com/uas/oauth/accessToken';
	}
	
	public function get_user_info(OAuth_Consumer $consumer, OAuth_Token $token)
	{
		// Create a new GET request with the required parameters
		$url = 'https://api.linkedin.com/v1/people/~:(id,first-name,last-name,headline,member-url-resources,picture-url,location,public-profile-url)';
		$request = OAuth_Request::forge('resource', 'GET', $url, array(
			'oauth_consumer_key' => $consumer->key,
			'oauth_token' => $token->access_token,
		));

		// Sign the request using the consumer and token
		$request->sign($this->signature, $consumer, $token);

		$user = OAuth_Format::factory($request->execute(), 'xml')->to_array();
		
		// Create a response from the request
		return array(
			'uid' => $user['id'],
			'name' => $user['first-name'].' '.$user['last-name'],
			'nickname' => end(explode('/', $user['public-profile-url'])),
			'description' => $user['headline'],
			'location' => isset($user['location']['name']) ? $user['location']['name'] : null,
			'urls' => array(
			  'Linked In' => $user['public-profile-url'],
			),
		);
	}

} // End Provider_Dropbox
