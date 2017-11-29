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
package org.openml.webapplication.features;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.xml.DataFeature.Feature;
import org.openml.apiconnector.xml.DataQuality.Quality;
import org.openml.webapplication.fantail.dc.Characterizer;
import org.openml.webapplication.fantail.dc.statistical.SimpleMetaFeatures;
import org.openml.webapplication.models.AttributeStatistics;

import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Instances;

public class ExtractFeatures {
	
	public static List<Feature> getFeatures(Instances dataset, String defaultClass) {
		if (defaultClass != null) {
			if(defaultClass.contains(",")){
				dataset.setClass(dataset.attribute(defaultClass.split(",")[0]));
			} else {
				dataset.setClass(dataset.attribute(defaultClass));
			}
		} else {
			dataset.setClassIndex(dataset.numAttributes()-1);
		}
		
		final ArrayList<Feature> resultFeatures = new ArrayList<Feature>();
		
		for (int i = 0; i < dataset.numAttributes(); i++) {
			Attribute att = dataset.attribute(i);
			int numValues = dataset.classAttribute().isNominal() ? dataset.classAttribute().numValues() : 0;
			AttributeStatistics attributeStats = new AttributeStatistics(dataset.attribute(i),numValues);
		
			for (int j = 0; j < dataset.numInstances(); ++j) {
				attributeStats.addValue(dataset.get(j).value(i), dataset.get(j).classValue());
			}
			
			String data_type = null;
			
			Integer numberOfDistinctValues = null;
			Integer numberOfUniqueValues = null;
			Integer numberOfMissingValues = null;
			Integer numberOfIntegerValues = null;
			Integer numberOfRealValues = null;
			Integer numberOfNominalValues = null;
			Integer numberOfValues = null;
			
			Double maximumValue = null;
			Double minimumValue = null;
			Double meanValue = null;
			Double standardDeviation = null;
				
			AttributeStats as = dataset.attributeStats(i);
				
			numberOfDistinctValues = as.distinctCount;
			numberOfUniqueValues = as.uniqueCount;
			numberOfMissingValues = as.missingCount;
			numberOfIntegerValues = as.intCount;
			numberOfRealValues = as.realCount;
			numberOfMissingValues = as.missingCount;
			
			
			if (att.isNominal()) {
				numberOfNominalValues = att.numValues(); 
			}
			numberOfValues = attributeStats.getTotalObservations();
			
			if (att.isNumeric()) {
				maximumValue = attributeStats.getMaximum();
				minimumValue = attributeStats.getMinimum();
				meanValue = attributeStats.getMean();
				standardDeviation = 0.0;
				try {
					standardDeviation = attributeStats.getStandardDeviation();
				} catch(Exception e) {
					Conversion.log("WARNING", "StdDev", "Could not compute standard deviation of feature "+ att.name() +": "+e.getMessage());
				}
			}
			
			if(att.type() == 0) {
				data_type = "numeric";
			} else if(att.type() == 1) {
				data_type = "nominal";
			} else if(att.type() == 2) {
				data_type = "string";
			} else {
				data_type = "unknown";
			}
			
			resultFeatures.add(new Feature(att.index(), att.name(), data_type,
					att.index() == dataset.classIndex(), 
					numberOfDistinctValues,
					numberOfUniqueValues, numberOfMissingValues,
					numberOfIntegerValues, numberOfRealValues,
					numberOfNominalValues, numberOfValues,
					maximumValue, minimumValue, meanValue,
					standardDeviation, attributeStats.getClassDistribution()));
		}
		return resultFeatures;
	}
	
	public static List<Quality> getQualities(Instances dataset, String defaultClass) throws Exception {
		if (defaultClass != null) {
			if(defaultClass.contains(",")){
				dataset.setClass(dataset.attribute(defaultClass.split(",")[0]));
			} else {
				dataset.setClass(dataset.attribute(defaultClass));
			}
		} else {
			dataset.setClassIndex(dataset.numAttributes()-1);
		}
		List<Quality> result = new ArrayList<Quality>();
		Characterizer simpleQualities = new SimpleMetaFeatures();
		Map<String,Double> qualities = simpleQualities.characterize(dataset);
		for (String quality : qualities.keySet()) {
			result.add(new Quality(quality, qualities.get(quality)));
		}
		return result;
	}
}