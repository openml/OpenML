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

import org.apache.commons.math3.stat.StatUtils;
import org.openml.webapplication.fantail.dc.Characterizer;
import org.openml.webapplication.fantail.dc.DCUntils;

import weka.core.Instances;

public class AttributeEntropy extends Characterizer {

	protected static final String[] ids = new String[] { 
		"ClassEntropy",
		"MeanAttributeEntropy", 
		"MeanMutualInformation",
		"EquivalentNumberOfAtts", 
		"NoiseToSignalRatio",
		
		"MinAttributeEntropy",
		"MinMutualInformation",

		"MaxAttributeEntropy",
		"MaxMutualInformation",
		
		"Quartile1AttributeEntropy",
		"Quartile1MutualInformation",
		
		"Quartile2AttributeEntropy",
		"Quartile2MutualInformation",
		
		"Quartile3AttributeEntropy",
		"Quartile3MutualInformation",
	};

	@Override
	public String[] getIDs() {
		return ids;
	}

	@Override
	public Map<String, Double> characterize(Instances data) {
		int nominal_count = 0;
		for (int i = 0; i < data.numAttributes(); ++i) {
			if (data.attribute(i).isNominal() && data.classIndex() != i) {
				nominal_count += 1;
			}
		}
		
		Map<String, Double> qualities = new HashMap<String, Double>();
		if (data.classAttribute().isNominal()) {
			double classEntropy = DCUntils.computeClassEntropy(data);
			double[] attEntropy = DCUntils.computeAttributeEntropy(data);
			double[] mutualInformation = DCUntils.computeMutualInformation(data);
			
			double meanMI = StatUtils.mean(mutualInformation);
			double meanAttEntropy = nominal_count > 0 ? StatUtils.mean(attEntropy) : -1;
			
			double noiseSignalRatio;
			double ena = 0;
	
			if (meanMI <= 0) {
				ena = -1;
				noiseSignalRatio = -1;
			} else {
				ena = classEntropy / meanMI;
				noiseSignalRatio = (meanAttEntropy - meanMI) / meanMI;
			}
	
			qualities.put(ids[0], classEntropy);
			qualities.put(ids[1], meanAttEntropy);
			qualities.put(ids[2], meanMI);
			qualities.put(ids[3], ena);
			qualities.put(ids[4], noiseSignalRatio);
			
			qualities.put(ids[5], StatUtils.min(attEntropy));
			qualities.put(ids[6], StatUtils.min(mutualInformation));
			
			qualities.put(ids[7], StatUtils.max(attEntropy));
			qualities.put(ids[8], StatUtils.max(mutualInformation));
			
			qualities.put(ids[9], StatUtils.percentile(attEntropy,25));
			qualities.put(ids[10], StatUtils.percentile(mutualInformation,25));
			
			qualities.put(ids[11], StatUtils.percentile(attEntropy,50));
			qualities.put(ids[12], StatUtils.percentile(mutualInformation,50));
			
			qualities.put(ids[13], StatUtils.percentile(attEntropy,75));
			qualities.put(ids[14], StatUtils.percentile(mutualInformation,75));
		}
		return qualities;
	}
}
