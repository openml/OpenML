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
