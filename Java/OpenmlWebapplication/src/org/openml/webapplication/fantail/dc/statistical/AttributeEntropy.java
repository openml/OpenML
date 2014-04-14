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
import org.openml.webapplication.fantail.dc.DCUntils;

import weka.core.Instances;

public class AttributeEntropy extends Characterizer {

	protected final String[] ids = new String[] { "ClassEntropy",
			"MeanAttributeEntropy", "MeanMutualInformation",
			"EquivalentNumberOfAtts", "NoiseToSignalRatio" };

	@Override
	public String[] getIDs() {
		return ids;
	}

	@Override
	public Map<String, Double> characterize(Instances data) {

		double classEntropy = DCUntils.computeClassEntropy(data);
		double meanAttEntropy = DCUntils.computeMeanAttributeEntropy(data);
		double meanMI = DCUntils.computeMutualInformation(data);
		double noiseSignalRatio = (meanAttEntropy - meanMI) / meanMI;
		double ena = 0;

		if (meanMI <= 0 || Double.isNaN(meanMI)) {
			ena = -1;
			noiseSignalRatio = -1;
		} else {
			ena = classEntropy / meanMI;
		}

		if (Double.isNaN(classEntropy)) {
			classEntropy = -1;
		}
		if (Double.isNaN(meanAttEntropy)) {
			meanAttEntropy = -1;
		}
		if (Double.isNaN(meanMI)) {
			meanMI = -1;
		}
		if (Double.isNaN(ena)) {
			ena = -1;
		}
		if (Double.isNaN(noiseSignalRatio)) {
			noiseSignalRatio = -1;
		}

		Map<String, Double> qualities = new HashMap<String, Double>();
		qualities.put(ids[0], classEntropy);
		qualities.put(ids[1], meanAttEntropy);
		qualities.put(ids[2], meanMI);
		qualities.put(ids[3], ena);
		qualities.put(ids[4], noiseSignalRatio);
		return qualities;
	}
}
