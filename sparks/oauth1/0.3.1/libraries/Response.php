<?php

class OAuth_Response {

	/**
	 * @var   array   response parameters
	 */
	protected $params = array();

	public function __construct($body = NULL)
	{
		if ($body)
		{
			$this->params = OAuth1::parse_params($body);
		}
	}

	/**
	 * Return the value of any protected class variable.
	 *
	 *     // Get the response parameters
	 *     $params = $response->params;
	 *
	 * @param   string  variable name
	 * @return  mixed
	 */
	public function __get($key)
	{
		return $this->$key;
	}

	public function param($name, $default = NULL)
	{
		return isset($this->params[$name]) ? $this->params[$name] : $default;
	}

} // End Response
