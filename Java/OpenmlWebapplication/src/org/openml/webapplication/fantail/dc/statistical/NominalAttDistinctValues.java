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
import java.util.Map;

import org.openml.webapplication.fantail.dc.Characterizer;

import weka.core.Instances;

public class NominalAttDistinctValues extends Characterizer {

	protected final String[] ids = new String[] {
			"MaxNominalAttDistinctValues", "MinNominalAttDistinctValues",
			"MeanNominalAttDistinctValues", "StdvNominalAttDistinctValues" };

	@Override
	public String[] getIDs() {
		return ids;
	}

	@Override
	public Map<String, Double> characterize(Instances data) {
		int attrib_count = data.numAttributes() - 1;
		int nominal_count = 0;
		// int numeric_count = 0;

		for (int i = 0; i < attrib_count; i++) {
			if (data.attribute(i).isNominal()) {
				nominal_count++;
			} // else {
				// numeric_count++;
				// }
		}

		if (nominal_count == 0) {
			Map<String, Double> qualities = new HashMap<String, Double>();
			qualities.put(ids[0], -1.0);
			qualities.put(ids[1], -1.0);
			qualities.put(ids[2], -1.0);
			qualities.put(ids[3], -1.0);
			return qualities;
		}

		ArrayList<Double> distinctValuesCounts = new ArrayList<Double>();

		for (int i = 0; i < attrib_count; i++) {
			if (data.attribute(i).isNominal()) {
				distinctValuesCounts.add(1.0 * data.numDistinctValues(i));

			}
		}

		double[] values = new double[distinctValuesCounts.size()];
		for (int i = 0; i < distinctValuesCounts.size(); i++) {
			values[i] = distinctValuesCounts.get(i);
		}

		double min = values[weka.core.Utils.minIndex(values)];
		double max = values[weka.core.Utils.maxIndex(values)];
		double mean = weka.core.Utils.mean(values);
		double variance = weka.core.Utils.variance(values);
		double stdv = Math.sqrt(variance);

		Map<String, Double> qualities = new HashMap<String, Double>();
		qualities.put(ids[0], max);
		qualities.put(ids[1], min);
		qualities.put(ids[2], mean);
		qualities.put(ids[3], stdv);
		return qualities;
	}
}
