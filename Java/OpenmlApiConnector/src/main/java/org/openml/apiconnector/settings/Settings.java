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

public class Settings {
	
	/**
	 * When set to true, API will output all downloaded and uploaded content. 
	 */
	public static final boolean API_VERBOSE = false;
	/**
	 * The webserver to which request are done. ends with tailing slash. 
	 * (Api suffix will be added by ApiConnector)
	 */
	public static final String BASE_URL = "http://openml.liacs.nl/";
	/**
	 * Whether caching is allowed. Keep value to true.
	 */
	public static final boolean CACHE_ALLOWED = true;
	/**
	 * The directory where cache files are saved. 
	 */
	public static final String CACHE_DIRECTORY = "data/.openml/cache/";
	
}
