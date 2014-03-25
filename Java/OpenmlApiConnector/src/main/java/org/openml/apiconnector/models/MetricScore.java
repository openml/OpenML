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
