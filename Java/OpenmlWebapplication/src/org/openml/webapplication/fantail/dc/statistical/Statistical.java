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

import java.util.HashMap;
import java.util.Map;

import org.openml.webapplication.fantail.dc.Characterizer;

import weka.core.Instance;
import weka.core.Instances;

public class Statistical extends Characterizer {

	protected final String[] ids = new String[] { "MeanMeansOfNumericAtts",
			"MeanStdDevOfNumericAtts", "MeanKurtosisOfNumericAtts",
			"MeanSkewnessOfNumericAtts" };

	@Override
	public String[] getIDs() {
		return ids;
	}

	@Override
	public Map<String, Double> characterize(Instances instances) {
		int attrib_count = instances.numAttributes() - 1, numeric_count = 0;

		double mean_sum = 0.0, stddev_sum = 0.0, kurtosis_sum = 0.0, skewness_sum = 0.0;

		for (int i = 0; i < attrib_count; i++) {
			if (instances.attribute(i).isNumeric()) {
				numeric_count++;
				final double mean = instances.meanOrMode(i);
				final double stddev = Math.sqrt(instances.variance(i));
				final double kurtosis = findKurtosis(instances, mean, stddev, i);
				final double skewness = findSkewness(instances, mean, stddev, i);
				mean_sum += mean;
				stddev_sum += stddev;
				kurtosis_sum += kurtosis;
				skewness_sum += skewness;
			}
		}

		if (0 == numeric_count) {

			Map<String, Double> qualities = new HashMap<String, Double>();
			qualities.put(ids[0], 0.0);
			qualities.put(ids[1], 0.0);
			qualities.put(ids[2], 0.0);
			qualities.put(ids[3], 0.0);
			return qualities;
		} else {

			Map<String, Double> qualities = new HashMap<String, Double>();
			qualities.put(ids[0], mean_sum / numeric_count);
			qualities.put(ids[1], stddev_sum / numeric_count);
			qualities.put(ids[2], kurtosis_sum / numeric_count);
			qualities.put(ids[3], skewness_sum / numeric_count);
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
