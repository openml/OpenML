<?php

abstract class OAuth_Signature {

	/**
	 * Create a new signature object by name.
	 *
	 *     $signature = Signature::forge('HMAC-SHA1');
	 *
	 * @param   string  signature name: HMAC-SHA1, PLAINTEXT, etc
	 * @param   array   signature options
	 * @return  Signature
	 */
	public static function forge($name, array $options = NULL)
	{
		$name = str_replace('-', '_', $name);

		// Create the class name as a base of this class
		$class = 'OAuth_Signature_'.$name;

		class_exists($class) or include 'Signature/'.ucfirst($name).'.php';

		return new $class($options);
	}

	/**
	 * @var  string  signature name: HMAC-SHA1, PLAINTEXT, etc
	 */
	protected $name;

	/**
	 * Return the value of any protected class variables.
	 *
	 *     $name = $signature->name;
	 *
	 * @param   string  variable name
	 * @return  mixed
	 */
	public function __get($key)
	{
		return $this->$key;
	}

	/**
	 * Get a signing key from a consumer and token.
	 *
	 *     $key = $signature->key($consumer, $token);
	 *
	 * [!!] This method implements the signing key of [OAuth 1.0 Spec 9](http://oauth.net/core/1.0/#rfc.section.9).
	 *
	 * @param   Consumer  consumer
	 * @param   Token     token
	 * @return  string
	 * @uses    OAuth1::urlencode
	 */
	public function key(OAuth_Consumer $consumer, OAuth_Token $token = NULL)
	{
		$key = OAuth1::urlencode($consumer->secret).'&';

		if ($token)
		{
			$key .= OAuth1::urlencode($token->secret);
		}

		return $key;
	}

	abstract public function sign(OAuth_Request $request, OAuth_Consumer $consumer, OAuth_Token $token = NULL);

	abstract public function verify($signature, OAuth_Request $request, OAuth_Consumer $consumer, OAuth_Token $token = NULL);

} // End Signature
