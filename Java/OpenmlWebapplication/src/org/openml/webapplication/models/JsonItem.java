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
package org.openml.webapplication.models;

import org.openml.webapplication.io.Output;

public class JsonItem {
	private String key;
	private String value;
	private boolean quotes;
	
	public JsonItem( String key, String value ) {
		this.key = key;
		this.value = value;
		this.quotes = true;
	}
	
	public JsonItem( String key, String value, boolean quotes ) {
		this.key = key;
		this.value = value;
		this.quotes = quotes;
	}
	
	public JsonItem( String key, Double value ) {
		this.key = key;
		this.value = Output.getDecimalFormat().format(value);
		this.quotes = false;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public boolean useQuotes() {
		return quotes;
	}
}
