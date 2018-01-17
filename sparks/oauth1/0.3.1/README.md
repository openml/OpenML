## CodeIgniter OAuth

An implementation of the [OAuth](http://oauth.net/) protocol with drivers to work with different providers such as Twitter, Google, etc.

This is based on the wonderful [Kohana OAuth](https://github.com/kohana/oauth) package but has been adapted to work with a wider range of providers.

Note that this Spark ONLY provides the authorization mechanism. You will need to implement the example code below so you can save this information to make API requests on the users behalf.

### Providers

- Dropbox
- Flickr
- Google
- LinkedIn
- Tumblr
- Twitter

### Installation

#### Install with Sparks

```console
$ php tools/spark install -v0.3.1 oauth
```

### Usage Example

This example will need the user to go to a certain URL, which will support multiple providers. I like to set a controller to handle it and either have one single "session" method - or have another method for callbacks if you want to separate out the code even more.

Here you'll see we have the provider passed in as a URI segment of "twitter" which can be used to find config in a database, or in a config multi-dimensional array. If you want to hard code it all then that is just fine too.

Send your user to `http://example.com/auth/oauth/twitter` where Auth is the name of the controller. This will also be the address of the "Callback URL" which will be required by many OAuth providers.

```php
class Auth extends CI_Controller
{
	public function oauth($provider)
	{
		$this->load->helper('url');
		
		$this->load->spark('oauth/0.3.1');
	
		// Create an consumer from the config
		$consumer = $this->oauth->consumer(array(
			'key' => $config['key'],
			'secret' => $config['secret'],
		));

		// Load the provider
		$provider = $this->oauth->provider($provider);
		
		// Create the URL to return the user to
		$callback = site_url('auth/oauth/'.$provider->name);

		if ( ! $this->input->get_post('oauth_token'))
		{
			// Add the callback URL to the consumer
			$consumer->callback($callback);	

			// Get a request token for the consumer
			$token = $provider->request_token($consumer);

			// Store the token
			$this->session->set_userdata('oauth_token', base64_encode(serialize($token)));

			// Get the URL to the twitter login page
			$url = $provider->authorize($token, array(
				'oauth_callback' => $callback,
			));

			// Send the user off to login
			redirect($url);
		}
		else
		{
			if ($this->session->userdata('oauth_token'))
			{
				// Get the token from storage
				$token = unserialize(base64_decode($this->session->userdata('oauth_token')));
			}

			if ( ! empty($token) AND $token->access_token !== $this->input->get_post('oauth_token'))
			{	
				// Delete the token, it is not valid
				$this->session->unset_userdata('oauth_token');

				// Send the user back to the beginning
				exit('invalid token after coming back to site');
			}

			// Get the verifier
			$verifier = $this->input->get_post('oauth_verifier');

			// Store the verifier in the token
			$token->verifier($verifier);

			// Exchange the request token for an access token
			$token = $provider->access_token($consumer, $token);
		
			// We got the token, let's get some user data
			$user = $provider->get_user_info($consumer, $token);
		
			// Here you should use this information to A) look for a user B) help a new user sign up with existing data.
			// If you store it all in a cookie and redirect to a registration page this is crazy-simple.
			echo "<pre>Tokens: ";
			var_dump($token).PHP_EOL.PHP_EOL;
			
			echo "User Info: ";
			var_dump($user);
		}
	}
}
```

If all goes well you should see a dump of user data and have `$token` available. If all does not go well you'll likely have a bunch of errors on your screen.