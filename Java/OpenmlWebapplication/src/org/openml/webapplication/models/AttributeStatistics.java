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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

import weka.core.Attribute;
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
	
	private final boolean trackClassDistribution;
	private final int[][] classDistribution;
	private Attribute attribute;
	
	// numClasses / numValues can be used for tracking classDistribution
	public AttributeStatistics(Attribute att, int numClasses) { 
		attribute = att;
		totalSum = new BigDecimal(0);
		totalSumSquared = new BigDecimal(0);
		totalObservations = 0;
		missingCount = 0;
		minimum = Double.MAX_VALUE;
		maximum = Double.MIN_VALUE;
		
		if (numClasses > 0 && att.isNominal()) {
			trackClassDistribution = true;
			classDistribution = new int[att.numValues()][numClasses];
		} else {
			trackClassDistribution = false;
			classDistribution = new int[0][0];
		}
	}
	
	public void addValue(double value, double classValue) {
		if (Utils.isMissingValue(value)) { 
			missingCount += 1;
			return;
		}
		
		if (trackClassDistribution) {
			classDistribution[(int) value][(int) classValue] += 1;
		}
		
		totalObservations += 1;
		totalSum = totalSum.add(new BigDecimal(value));
		totalSumSquared = totalSumSquared.add(new BigDecimal(value * value));
		
		if (value < minimum) minimum = value;
		if (value > maximum) maximum = value;
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
		if(totalObservations == 0) return 0; 
		return totalSum.divide(new BigDecimal(totalObservations), PRECISION, ROUNDING_MODE ).doubleValue();
	}
	
	public double getStandardDeviation() {
		if(totalObservations == 0) return 0; 
		BigDecimal obs = new BigDecimal(totalObservations);
		return Math.sqrt(totalSumSquared.multiply(obs).subtract(totalSum.multiply(totalSum)).divide(obs.multiply(obs.subtract(new BigDecimal(1))), PRECISION, ROUNDING_MODE).doubleValue());
	}
	
	public String getClassDistribution() {
		if (trackClassDistribution == false) {
			return "[]";
		} else {
			StringBuilder sb = new StringBuilder();
			String headline = "";
			for (int i = 0; i < attribute.numValues(); ++i) {
				headline += ",\""+attribute.value(i)+"\"";
			}
			sb.append("[[" + headline.substring(1) + "],[");
			for (int i = 0; i < classDistribution.length; ++i) {
				if (i > 0) sb.append(",");
				sb.append(Arrays.toString(classDistribution[i]));
			}
			sb.append("]]");
			return sb.toString();
		}
	}
}
