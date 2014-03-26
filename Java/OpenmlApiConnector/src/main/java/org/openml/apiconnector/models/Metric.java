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
package org.openml.apiconnector.models;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Metric {

	public String name;
	public String implementation;
	public String label;
	
	public Metric( String name, String implementation, String label ) {
		this.name = name;
		this.implementation = implementation;
		this.label = label;
	}
	
	@Override
	public boolean equals( Object o ) {
		if( o instanceof Metric ) {

			Metric other = (Metric) o;
			
			if(other.label == null ) {
				return this.label == null && other.name.equals(this.name) && other.implementation.equals(this.implementation);
			} else {
				return other.name.equals(this.name) && other.label.equals(this.label) && other.implementation.equals(implementation);
			}
			
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
        return new HashCodeBuilder(17, 31).append(name).append(label).append(implementation).toHashCode();
    }
	
	@Override
	public String toString() {
		if(label == null ) 
			return implementation;
		else
			return implementation + "(label:" + label + ")";
	}
}
