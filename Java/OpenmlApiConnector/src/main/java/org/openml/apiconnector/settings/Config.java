/*
 *  OpenmlApiConnector - Java integration of the OpenML Web API
 *  Copyright (C) 2014 
 *  @author Jan N. van Rijn (j.n.van.rijn@liacs.leidenuniv.nl)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
package org.openml.apiconnector.settings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * A Class that loads a config file with username/password and server information.
 * Highly recommended to use config file and this class when executing experiments
 * on a server. 
 * 
 * @author J. N. van Rijn <j.n.van.rijn@liacs.leidenuniv.nl>
 */
public class Config {

	private boolean loaded = false;
	private HashMap<String, String> config;
	
	/**
	 * @throws IOException - Could not load config file
	 */
	public Config() throws IOException {
		load("openml.conf");
	}
	
	/**
	 * @param f The location (absolute or relative) where the config
	 * file can be found. 
	 * @throws IOException - Could not load config file
	 */
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
	
	/**
	 * @return The username specified in the config file
	 */
	public String getUsername() {
		return get("username");
	}
	
	/**
	 * @return The password specified in the config file
	 */
	public String getPassword() {
		return get("password");
	}
	
	/**
	 * @param key - Item name to be loaded from the config file. 
	 * @return Field "key", if specified in the config file. null otherwise
	 */
	public String get( String key ) {
		if( loaded ) {
			if( config.containsKey( key ) ) {
				return config.get( key );
			}
		}
		return null;
	}
}
