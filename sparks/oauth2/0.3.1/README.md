# Codeigniter OAuth 2.0

Authorize users with your application in a driver-base fashion meaning one implementation works for multiple OAuth 2 providers. This is only to authenticate onto OAuth2 providers and not to build an OAuth2 service.

Note that this Spark ONLY provides the authorization mechanism. There's an example controller below, however in a later version there will be a full controller.

## Examples

OAuth 2 is split into two sections, clients and providers. A client is an application - perhaps a basic Twitter feed aggregator - which 
authenticates with an OAuth 2 provider, which in this example would be Twitter itself. You can interact with any provider which is supported in the list below:

- Facebook
- Foursquare
- GitHub
- Google
- PayPal
- Instagram
- Soundcloud
- Windows Live
- YouTube


## TODO

- Requests should be done through a more stable system, there however isn't a Request class in CodeIgniter.
- Add unit tests and get on Travis

## Usage Example

This example will need the user to go to a certain URL, which will support multiple providers. I like to set a controller to handle it and either have one single "session" method - or have another method for callbacks if you want to separate out the code even more.

Here you'll see we have the provider passed in as a URI segment of "facebook" which can be used to find config in a database, or in a config multi-dimensional array. If you want to hard code it all then that is just fine too.

Send your user to `http://example.com/auth/session/facebook` where Auth is the name of the controller. This will also be the address of the "Callback URL" which will be required by many OAuth 2 providers such as Facebook.

```php
class Auth extends CI_Controller
{
	public function session($provider)
	{
		$this->load->helper('url_helper');
		
		$this->load->spark('oauth2/0.3.1');
	
		$provider = $this->oauth2->provider($provider, array(
			'id' => 'your-client-id',
			'secret' => 'your-client-secret',
		));

		if ( ! $this->input->get('code'))
		{
			// By sending no options it'll come back here
			$provider->authorize();
		}
		else
		{
			// Howzit?
			try
			{
				$token = $provider->access($_GET['code']);
			
				$user = $provider->get_user_info($token);
			
				// Here you should use this information to A) look for a user B) help a new user sign up with existing data.
				// If you store it all in a cookie and redirect to a registration page this is crazy-simple.
				echo "<pre>Tokens: ";
				var_dump($token);
				
				echo "\n\nUser Info: ";
				var_dump($user);
			}
		
			catch (OAuth2_Exception $e)
			{
				show_error('That didnt work: '.$e);
			}
		
		}
	}
}
```

If all goes well you should see a dump of user data and have `$token` available. If all does not go well you'll likely have a bunch of errors on your screen.

Contribute
----------

1. Check for open issues or open a new issue for a feature request or a bug
2. Fork [the repository][] on Github to start making your changes to the
    `develop` branch (or branch off of it)
3. Write a test which shows that the bug was fixed or that the feature works as expected
4. Send a pull request and bug me until I merge it

[the repository]: https://github.com/philsturgeon/codeigniter-oauth2