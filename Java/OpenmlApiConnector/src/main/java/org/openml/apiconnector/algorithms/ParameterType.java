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

public enum ParameterType {
	FLAG("flag"), KERNEL("kernel"), BASELEARNER("baselearner"), OPTION("option");

	private String text;

	ParameterType(String text) {
		this.text = text;
	}

	/**
	 * @return The name of this parameter type;
	 */
	public String getName() {
		return this.text;
	}

	/**
	 * Converts a textual description of a parameter into a ParameterType
	 * 
	 * @param text (String)
	 * @return the ParameterType
	 */
	public static ParameterType fromString(String text) {
		if (text != null) {
			for (ParameterType b : ParameterType.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
		}
		return null;
	}
}