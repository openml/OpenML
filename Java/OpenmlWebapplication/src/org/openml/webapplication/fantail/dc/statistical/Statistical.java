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
package org.openml.webapplication.fantail.dc.statistical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.openml.webapplication.fantail.dc.Characterizer;

import weka.core.Instance;
import weka.core.Instances;

public class Statistical extends Characterizer {
	
	private List<Double> meanList = new ArrayList<Double>();
	private List<Double> stdDevList = new ArrayList<Double>();
	private List<Double> kurtosisList = new ArrayList<Double>();
	private List<Double> skewnessList = new ArrayList<Double>();
	
	protected final String[] ids = new String[] { 
		"MeanMeansOfNumericAtts",
		"MeanStdDevOfNumericAtts", 
		"MeanKurtosisOfNumericAtts",
		"MeanSkewnessOfNumericAtts",
		
		"MinMeansOfNumericAtts",
		"MinStdDevOfNumericAtts", 
		"MinKurtosisOfNumericAtts",
		"MinSkewnessOfNumericAtts",
		
		"MaxMeansOfNumericAtts",
		"MaxStdDevOfNumericAtts", 
		"MaxKurtosisOfNumericAtts",
		"MaxSkewnessOfNumericAtts",
		
		"Quartile1MeansOfNumericAtts",
		"Quartile1StdDevOfNumericAtts", 
		"Quartile1KurtosisOfNumericAtts",
		"Quartile1SkewnessOfNumericAtts",
		
		"Quartile2MeansOfNumericAtts",
		"Quartile2StdDevOfNumericAtts", 
		"Quartile2KurtosisOfNumericAtts",
		"Quartile2SkewnessOfNumericAtts",
		
		"Quartile3MeansOfNumericAtts",
		"Quartile3StdDevOfNumericAtts", 
		"Quartile3KurtosisOfNumericAtts",
		"Quartile3SkewnessOfNumericAtts" 
	};

	@Override
	public String[] getIDs() {
		return ids;
	}

	@Override
	public Map<String, Double> characterize(Instances instances) {
		int attrib_count = instances.numAttributes() - 1, numeric_count = 0;

		for (int i = 0; i < attrib_count; i++) {
			if (instances.attribute(i).isNumeric()) {
				numeric_count++;
				final double mean = instances.meanOrMode(i);
				final double stddev = Math.sqrt(instances.variance(i));
				final double kurtosis = findKurtosis(instances, mean, stddev, i);
				final double skewness = findSkewness(instances, mean, stddev, i);

				meanList.add(mean);
				stdDevList.add(stddev);
				kurtosisList.add(kurtosis);
				skewnessList.add(skewness);
			}
		}

		if (0 == numeric_count) {

			Map<String, Double> qualities = new HashMap<String, Double>();
			qualities.put(ids[0], 0.0);
			qualities.put(ids[1], 0.0);
			qualities.put(ids[2], 0.0);
			qualities.put(ids[3], 0.0);
			
			qualities.put(ids[4], 0.0);
			qualities.put(ids[5], 0.0);
			qualities.put(ids[6], 0.0);
			qualities.put(ids[7], 0.0);
			
			qualities.put(ids[8], 0.0);
			qualities.put(ids[9], 0.0);
			qualities.put(ids[10], 0.0);
			qualities.put(ids[11], 0.0);
			
			qualities.put(ids[12], 0.0);
			qualities.put(ids[13], 0.0);
			qualities.put(ids[14], 0.0);
			qualities.put(ids[15], 0.0);
			
			qualities.put(ids[16], 0.0);
			qualities.put(ids[17], 0.0);
			qualities.put(ids[18], 0.0);
			qualities.put(ids[19], 0.0);
			
			qualities.put(ids[20], 0.0);
			qualities.put(ids[21], 0.0);
			qualities.put(ids[22], 0.0);
			qualities.put(ids[23], 0.0);
			return qualities;
		} else {
			double[] meansArray = ArrayUtils.toPrimitive(meanList.toArray(new Double[numeric_count]));
			double[] stdDevsArray = ArrayUtils.toPrimitive(stdDevList.toArray(new Double[numeric_count]));
			double[] kurtosisArray = ArrayUtils.toPrimitive(kurtosisList.toArray(new Double[numeric_count]));
			double[] skewnessArray = ArrayUtils.toPrimitive(skewnessList.toArray(new Double[numeric_count]));
			
			Map<String, Double> qualities = new HashMap<String, Double>();
			qualities.put(ids[0], StatUtils.mean(meansArray));
			qualities.put(ids[1], StatUtils.mean(stdDevsArray));
			qualities.put(ids[2], StatUtils.mean(kurtosisArray));
			qualities.put(ids[3], StatUtils.mean(skewnessArray));
			
			qualities.put(ids[4], StatUtils.min(meansArray));
			qualities.put(ids[5], StatUtils.min(stdDevsArray));
			qualities.put(ids[6], StatUtils.min(kurtosisArray));
			qualities.put(ids[7], StatUtils.min(skewnessArray));
			
			qualities.put(ids[8], StatUtils.max(meansArray));
			qualities.put(ids[9], StatUtils.max(stdDevsArray));
			qualities.put(ids[10], StatUtils.max(kurtosisArray));
			qualities.put(ids[11], StatUtils.max(skewnessArray));
			
			qualities.put(ids[12], StatUtils.percentile(meansArray,25));
			qualities.put(ids[13], StatUtils.percentile(stdDevsArray,25));
			qualities.put(ids[14], StatUtils.percentile(kurtosisArray,25));
			qualities.put(ids[15], StatUtils.percentile(skewnessArray,25));
			
			qualities.put(ids[16], StatUtils.percentile(meansArray,50));
			qualities.put(ids[17], StatUtils.percentile(stdDevsArray,50));
			qualities.put(ids[18], StatUtils.percentile(kurtosisArray,50));
			qualities.put(ids[19], StatUtils.percentile(skewnessArray,50));
			
			qualities.put(ids[20], StatUtils.percentile(meansArray,75));
			qualities.put(ids[21], StatUtils.percentile(stdDevsArray,75));
			qualities.put(ids[22], StatUtils.percentile(kurtosisArray,75));
			qualities.put(ids[23], StatUtils.percentile(skewnessArray,75));
			return qualities;
		}
	}

	private static double findKurtosis(Instances instances, double mean,
			double stddev, int attrib) {
		final double S4 = Math.pow(stddev, 4), YBAR = mean;
		double sum = 0.0;
		final int COUNT = instances.numInstances();
		int n = 0;

		if (S4 == 0) {
			return 0;
		}

		for (int i = 0; i < COUNT; i++) {
			Instance instance = instances.instance(i);
			if (!instance.isMissing(attrib)) {
				n++;
				sum += Math.pow(instance.value(attrib) - YBAR, 4);
			}
		}

		return (sum / ((n - 1) * S4)) - 3;
	}

	private static double findSkewness(Instances instances, double mean,
			double stddev, int attrib) {
		final double S3 = Math.pow(stddev, 3), YBAR = mean;
		double sum = 0.0;
		final int COUNT = instances.numInstances();
		int n = 0;

		if (S3 == 0) {
			return 0;
		}

		for (int i = 0; i < COUNT; i++) {
			Instance instance = instances.instance(i);
			if (!instance.isMissing(attrib)) {
				n++;
				sum += Math.pow(instance.value(attrib) - YBAR, 3);
			}
		}

		return (sum / ((n - 1) * S3));
	}
}
