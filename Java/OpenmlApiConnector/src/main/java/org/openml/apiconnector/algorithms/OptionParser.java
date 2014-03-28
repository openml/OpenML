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
package org.openml.apiconnector.algorithms;

public class OptionParser {

	/**
	 * Removes the first element of an option string. Used for generating option strings in Weka plugin. 
	 * 
	 * @param old - The original option string
	 * @return The processed option String
	 */
	public static String[] removeFirstElement( String[] old ) {
		int n = old.length-1;
		String[] newArray = new String[n];
		System.arraycopy( old, 1, newArray, 0, n);
		return newArray;
	}
	
	
}
