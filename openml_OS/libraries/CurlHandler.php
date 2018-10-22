<?php
class CurlHandler {

	private $handle;
	private $header;

	function __construct() {
		$this->handle = curl_init();
	}

	function __destruct() {
		curl_close($this->handle);
	}

	function init($url) {
		$this->handle = curl_init($url);
		curl_setopt($this->handle, CURLOPT_NOBODY, true);
		curl_setopt($this->handle, CURLOPT_RETURNTRANSFER, true);
		curl_setopt($this->handle, CURLOPT_HEADER, true);
		//curl_setopt($this->handle, CURLOPT_FOLLOWLOCATION, true);
		$this->header = curl_exec($this->handle);
	}

	function succes() {
		$httpCode = curl_getinfo($this->handle, CURLINFO_HTTP_CODE);
		return ($httpCode == 200);
	}

	function getFilesize( $url ) {
		if (preg_match('/Content-Length: (\d+)/', $data, $matches)) {
			$contentLength = (int)$matches[1];
			return $contentLength;
		} else {
			return -1;
		}
	}

  // does a single post, in a specific handle (hence, helper)
  function post_multipart_helper( $url, $data ) {
    // Get cURL resource
    $curl = curl_init();
    // Set some options - we are passing in a useragent too here
    curl_setopt_array($curl, array(
      CURLOPT_RETURNTRANSFER => true,
      CURLOPT_URL => $url,
      CURLOPT_POST => true,
      CURLOPT_HTTPHEADER => array('Content-Type: multipart/form-data'),
      CURLOPT_POSTFIELDS => $data
    ));

    $res = curl_exec($curl);
    curl_close($curl);
    return $res;
  }

	// does a single post, in a specific handle (hence, helper)
	function post_helper($url, $data) {
		// Get cURL resource
		$curl = curl_init();
		// Set some options - we are passing in a useragent too here
		curl_setopt_array($curl, array(
			CURLOPT_RETURNTRANSFER => true,
			CURLOPT_URL => $url,
			CURLOPT_POST => true,
			CURLOPT_POSTFIELDS => $data
		));

		$res = curl_exec($curl);
		curl_close($curl);
		return $res;
	}
}
?>
