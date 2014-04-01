package org.openml.webapplication.models;

import java.math.BigDecimal;
import java.math.RoundingMode;

import weka.core.Utils;

public class AttributeStatistics {
	
	private static final int PRECISION = 16;
	private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
	
	// variables maintained when inserted
	private int missingCount;
	private int totalObservations;
	private BigDecimal totalSum;
	private BigDecimal totalSumSquared; 
	private double minimum;
	private double maximum;
	
	public AttributeStatistics() {
		totalSum = new BigDecimal( 0 );
		totalSumSquared = new BigDecimal( 0 );
		totalObservations = 0;
		missingCount = 0;
		minimum = Double.MAX_VALUE;
		maximum = Double.MIN_VALUE;
	}
	
	public void addValue( Double value ) {
		if( value.isNaN() ) {
			return;
		}
		
		if( Utils.isMissingValue(value) ) { 
			missingCount += 1;
			return;
		}
		
		totalObservations += 1;
		totalSum = totalSum.add( new BigDecimal( value ) );
		totalSumSquared = totalSumSquared.add( new BigDecimal( value * value ) );
		
		if( value < minimum ) minimum = value;
		if( value > maximum ) maximum = value;
	}

	public int getTotalObservations() {
		return totalObservations;
	}
	
	public int getMissingCount() {
		return missingCount;
	}

	public double getMinimum() {
		return minimum;
	}

	public double getMaximum() {
		return maximum;
	}
	
	public double getMean() {
		return totalSum.divide( new BigDecimal( totalObservations ), PRECISION, ROUNDING_MODE ).doubleValue();
	}
	
	public double getStandardDeviation() {
		BigDecimal obs = new BigDecimal( totalObservations );
		return Math.sqrt( totalSumSquared.multiply( obs ).subtract( totalSum.multiply( totalSum ) ).divide( obs.multiply( obs.subtract( new BigDecimal( 1 ) ) ), PRECISION, ROUNDING_MODE ).doubleValue() );
	}
	
}
