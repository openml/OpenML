package openml.settings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Config {

	private boolean loaded = false;
	private HashMap<String, String> config;
	
	public Config() throws IOException {
		load("openml.conf");
	}
	
	public void load( String f ) throws IOException {
		config = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader(f));
		while( br.ready() ) {
			String[] l = br.readLine().split("=");
			if( l.length == 2 ) {
				config.put( l[0].trim(), l[1].trim() );
			}
		}
		br.close();
		loaded = true;
	}
	
	public String getUsername() {
		return get("username");
	}
	
	public String getPassword() {
		return get("password");
	}
	
	public String get( String key ) {
		if( loaded ) {
			if( config.containsKey( key ) ) {
				return config.get( key );
			}
		}
		return null;
	}
}
