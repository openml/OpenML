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
	
	/**
	 * Constructor creating a MetricScore that only contains the score field.
	 * Used for measures that have no class specific values, like predictive_accurancy.
	 * 
	 * @param score
	 */
	public MetricScore( Double score ) {
		this.score = score;
	}

	/**
	 * Constructor creating a MetricScore that only contains the array field. Used for
	 * measures that contain class specific values, but no global flat value.
	 * 
	 * @param array - 
	 */
	public MetricScore( Double[] array ) {
		this.score = MathHelper.mean(array);
		this.array = array;
	}

	/**
	 * Constructor creating a MetricScore with both a score and array data. Used for
	 * measures that contain both a global value and class specific value, like auroc
	 * precision, recall. 
	 * 
	 * @param score 
	 * @param array
	 */
	public MetricScore( Double score, Double[] array ) {
		this.score = score;
		this.array = array;
	}

	/**
	 * Fills the metric score with a confussion matrix. 
	 * 
	 * @param confusion_matrix
	 */
	public MetricScore( double[][] confusion_matrix ) {
		this.confusion_matrix = confusion_matrix;
	}

	/**
	 * @return the score field
	 */
	public Double getScore() {
		return score;
	}

	/**
	 * @return true if this score contains array data (confusion matrix or class specific values);
	 * false otherwise.
	 */
	public boolean hasArray() {
		return array != null || confusion_matrix != null;
	}
	
	/**
	 * @param decimalFormat - An object specifying how to convert doubles to strings.
	 * @return The array in string format. 
	 */
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
