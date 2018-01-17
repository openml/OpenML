<?php

class OAuth_Token_Request extends OAuth_Token {

	protected $name = 'request';

	/**
	 * @var  string  request token verifier
	 */
	protected $verifier;

	/**
	 * Change the token verifier.
	 *
	 *     $token->verifier($key);
	 *
	 * @param   string   new verifier
	 * @return  $this
	 */
	public function verifier($verifier)
	{
		$this->verifier = $verifier;

		return $this;
	}

} // End Token_Request
