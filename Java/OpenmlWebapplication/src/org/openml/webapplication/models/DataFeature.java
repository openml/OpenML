/*
 *  Webapplication - Java library that runs on OpenML servers
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
package org.openml.webapplication.models;

import java.util.HashMap;
import java.util.Map;

public class DataFeature {
	private final int index;
	private final String name;
	private final Map<String, String> stringEntries;
	private final Map<String, Integer> intEntries;
	private final Map<String, Double> doubleEntries;
	
	public DataFeature( int index, String name ) {
		this.index = index;
		this.name = name;
		stringEntries = new HashMap<String, String>();
		intEntries = new HashMap<String, Integer>();
		doubleEntries = new HashMap<String, Double>();
	}
	
	public void put( String feature, String value ) {
		stringEntries.put( feature, value );
	}
	
	public void put( String feature, Integer value ) {
		intEntries.put( feature, value );
	}
	
	public void put( String feature, Double value ) {
		doubleEntries.put( feature, value );
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( ",\"index\":\"" + index + "\"" );
		sb.append( ",\"name\":\"" + name + "\"" );
		for( String key : stringEntries.keySet() )
			sb.append( ",\"" + key + "\":\"" + stringEntries.get(key) + "\"" );
		for( String key : intEntries.keySet() )
			sb.append( ",\"" + key + "\":" + intEntries.get(key) );
		for( String key : doubleEntries.keySet() )
			sb.append( ",\"" + key + "\":" + doubleEntries.get(key) );
		return "{" + sb.toString().substring(1) + "}";
	}
	
}
