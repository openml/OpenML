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

import java.text.DecimalFormat;

import org.openml.apiconnector.algorithms.MathHelper;

public class MetricScore {
	
	private Double score = null;
	private Double[] array = null;
	private double[][] confusion_matrix = null;
	
	public MetricScore( Double score ) {
		this.score = score;
	}

	public MetricScore( Double[] array ) {
		this.score = MathHelper.mean(array);
		this.array = array;
	}

	public MetricScore( Double score, Double[] array ) {
		this.score = score;
		this.array = array;
	}

	public MetricScore( double[][] confusion_matrix ) {
		this.confusion_matrix = confusion_matrix;
	}

	public Double getScore() {
		return score;
	}

	public boolean hasArray() {
		return array != null || confusion_matrix != null;
	}
	
	public String getArrayAsString( DecimalFormat decimalFormat ) {
		StringBuilder sb = new StringBuilder();
		if( array != null ) {
			for( Double d : array ) {
				sb.append( "," + decimalFormat.format( d ) );
			}
			return "[" + sb.toString().substring( 1 ) + "]";
		} else if( confusion_matrix != null ) {
			
			for( double[] perClass : confusion_matrix ) {
				StringBuilder sbperClass = new StringBuilder();
				
				for( double i : perClass ) {
					sbperClass.append("," + ((int) i) );
				}
				
				sb.append( ",[" + sbperClass.toString().substring( 1 ) + "]" );
			}
			return "[" + sb.toString().substring( 1 ) + "]";
		} else {
			return null;
		}
	}
}
