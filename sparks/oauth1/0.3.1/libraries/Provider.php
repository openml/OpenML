<?php
/**
 * OAuth Provider
 *
 * @package    CodeIgniter/OAuth
 * @category   Provider
 * @author     Phil Sturgeon
 * @copyright  (c) 2012 HappyNinjas Ltd
 * @license    http://philsturgeon.co.uk/code/dbad-license
 */

abstract class OAuth_Provider {

	/**
	 * @var  string  provider name
	 */
	public $name;

	/**
	 * @var  string  signature type
	 */
	protected $signature = 'HMAC-SHA1';

	/**
	 * @var  string  uid key name
	 */
	public $uid_key = 'uid';

	/**
	 * @var  array  additional request parameters to be used for remote requests
	 */
	protected $params = array();
	
	/**
	 * @var  string  scope separator, most use "," but some like Google are spaces
	 */
	public $scope_seperator = ',';

	/**
	 * Overloads default class properties from the options.
	 *
	 * Any of the provider options can be set here:
	 *
	 * Type      | Option        | Description                                    | Default Value
	 * ----------|---------------|------------------------------------------------|-----------------
	 * mixed     | signature     | Signature method name or object                | provider default
	 *
	 * @param   array   provider options
	 * @return  void
	 */
	public function __construct(array $options = NULL)
	{
		if (isset($options['signature']))
		{
			// Set the signature method name or object
			$this->signature = $options['signature'];
		}

		if ( ! is_object($this->signature))
		{
			// Convert the signature name into an object
			$this->signature = OAuth_Signature::forge($this->signature);
		}

		if ( ! $this->name)
		{
			// Attempt to guess the name from the class name
			$this->name = strtolower(substr(get_class($this), strlen('Provider_')));
		}
	}

	/**
	 * Return the value of any protected class variable.
	 *
	 *     // Get the provider signature
	 *     $signature = $provider->signature;
	 *
	 * @param   string  variable name
	 * @return  mixed
	 */
	public function __get($key)
	{
		return $this->$key;
	}

	/**
	 * Returns the request token URL for the provider.
	 *
	 *     $url = $provider->url_request_token();
	 *
	 * @return  string
	 */
	abstract public function url_request_token();

	/**
	 * Returns the authorization URL for the provider.
	 *
	 *     $url = $provider->url_authorize();
	 *
	 * @return  string
	 */
	abstract public function url_authorize();

	/**
	 * Returns the access token endpoint for the provider.
	 *
	 *     $url = $provider->url_access_token();
	 *
	 * @return  string
	 */
	abstract public function url_access_token();
	
	/**
	 * Returns basic information about the user.
	 *
	 *     $url = $provider->get_user_info();
	 *
	 * @return  string
	 */
	abstract public function get_user_info(OAuth_Consumer $consumer, OAuth_Token $token);

	/**
	 * Ask for a request token from the OAuth provider.
	 *
	 *     $token = $provider->request_token($consumer);
	 *
	 * @param   Consumer  consumer
	 * @param   array           additional request parameters
	 * @return  Token_Request
	 * @uses    Request_Token
	 */
	public function request_token(OAuth_Consumer $consumer, array $params = NULL)
	{
		// Create a new GET request for a request token with the required parameters
		$request = OAuth_Request::forge('token', 'GET', $this->url_request_token(), array(
			'oauth_consumer_key' => $consumer->key,
			'oauth_callback'     => $consumer->callback,
			'scope'     		 => is_array($consumer->scope) ? implode($this->scope_seperator, $consumer->scope) : $consumer->scope,
		));

		if ($params)
		{
			// Load user parameters
			$request->params($params);
		}

		// Sign the request using only the consumer, no token is available yet
		$request->sign($this->signature, $consumer);

		// Create a response from the request
		$response = $request->execute();

		// Store this token somewhere useful
		return OAuth_Token::forge('request', array(
			'access_token'  => $response->param('oauth_token'),
			'secret' => $response->param('oauth_token_secret'),
		));
	}

	/**
	 * Get the authorization URL for the request token.
	 *
	 *     Response::redirect($provider->authorize_url($token));
	 *
	 * @param   Token_Request  token
	 * @param   array                additional request parameters
	 * @return  string
	 */
	public function authorize(OAuth_Token_Request $token, array $params = NULL)
	{
		// Create a new GET request for a request token with the required parameters
		$request = OAuth_Request::forge('authorize', 'GET', $this->url_authorize(), array(
			'oauth_token' => $token->access_token,
		));

		if ($params)
		{
			// Load user parameters
			$request->params($params);
		}

		return $request->as_url();
	}

	/**
	 * Exchange the request token for an access token.
	 *
	 *     $token = $provider->access_token($consumer, $token);
	 *
	 * @param   Consumer       consumer
	 * @param   Token_Request  token
	 * @param   array                additional request parameters
	 * @return  Token_Access
	 */
	public function access_token(OAuth_Consumer $consumer, OAuth_Token_Request $token, array $params = NULL)
	{
		// Create a new GET request for a request token with the required parameters
		$request = OAuth_Request::forge('access', 'GET', $this->url_access_token(), array(
			'oauth_consumer_key' => $consumer->key,
			'oauth_token'        => $token->access_token,
			'oauth_verifier'     => $token->verifier,
		));

		if ($params)
		{
			// Load user parameters
			$request->params($params);
		}

		// Sign the request using only the consumer, no token is available yet
		$request->sign($this->signature, $consumer, $token);

		// Create a response from the request
		$response = $request->execute();
		
		// Store this token somewhere useful
		return OAuth_Token::forge('access', array(
			'access_token'  => $response->param('oauth_token'),
			'secret' => $response->param('oauth_token_secret'),
			'uid' => $response->param($this->uid_key) ? $response->param($this->uid_key) : get_instance()->input->get_post($this->uid_key),
		));
	}

} // End Provider
