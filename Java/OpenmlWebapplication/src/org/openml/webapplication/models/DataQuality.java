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

public class DataQuality {

	private final String name;
	private final String label;
	private final String value;
	
	public DataQuality( String name, String label, String value ) {
		this.name = name;
		this.label = label;
		this.value = value;
	}
	
	public DataQuality( String name, String value ) {
		this( name, null, value );
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"name\":\""+name+"\",");
		if(label != null) {
			sb.append("\"label\":\""+label+"\",");
		}
		sb.append("\"value\":"+value+"}");
		return sb.toString();
	}
}
