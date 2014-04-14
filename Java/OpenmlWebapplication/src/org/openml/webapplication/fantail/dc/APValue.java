/*
 *  Webapplication - Java library that runs on OpenML servers
 *  Copyright (C) 2014 
 *  @author Jan N. van Rijn (j.n.van.rijn@liacs.leidenuniv.nl)
 *  @author Quan Sun (quan.sun.nz@gmail.com)
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
package org.openml.webapplication.fantail.dc;

import java.io.Serializable;

public class APValue implements Serializable {

	private static final long serialVersionUID = 1L;

	public APValue(String id, double accuracy, double time) {
		this.id = id;
		this.accuracy = accuracy;

		if (time > 0) {
			this.time = time;
		} else {
			this.time = 1; // Prevent division by zero
		}
	}

	public double error() {
		return 1 - accuracy;
	}

	public final String id;

	public final double accuracy, time;

	public String toString() {
		return new StringBuffer().append(id).append("\tAccuracy = ")
				.append(accuracy).append("\tTime = ").append(time).toString();
	}
}
