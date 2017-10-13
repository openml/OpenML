package org.openml.webapplication.fantail.dc.statistical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.openml.webapplication.fantail.dc.Characterizer;

import weka.core.AttributeStats;
import weka.core.Instances;

public class Cardinality extends Characterizer {
	
	protected final String[] ids = new String[] { 
		"MeanCardinalityOfNumericAttributes", "StdevCardinalityOfNumericAttributes", 
		"MinCardinalityOfNumericAttributes", "MaxCardinalityOfNumericAttributes", 
		"MeanCardinalityOfNominalAttributes", "StdevCardinalityOfNominalAttributes", 
		"MinCardinalityOfNominalAttributes", "MaxCardinalityOfNominalAttributes", 
		"CardinalityAtTwo", "CardinalityAtThree", "CardinalityAtFour"
	};
	
	@Override
	public String[] getIDs() {
		return ids;
	}

	@Override
	public Map<String, Double> characterize(Instances instances) {
		Map<String, Double> results = new TreeMap<>();
		
		List<Integer> all = new ArrayList<>();
		DescriptiveStatistics nom = new DescriptiveStatistics();
		DescriptiveStatistics num = new DescriptiveStatistics();
		boolean hasNum = false;
		boolean hasNom = false;
		
		for (int idx = 0; idx < instances.numAttributes(); idx++) {
			AttributeStats stats = instances.attributeStats(idx);
			all.add(stats.distinctCount);
			
			if (instances.attribute(idx).isNumeric()) {
				num.addValue(stats.distinctCount);
				hasNum = true;
			} else {
				nom.addValue(stats.distinctCount);
				hasNom = true;
			}
		}
		
		Collections.sort(all, Collections.reverseOrder());
		
		if (hasNum) {
			results.put("MeanCardinalityOfNumericAttributes", num.getMean());
			results.put("StdevCardinalityOfNumericAttributes", num.getStandardDeviation());
			results.put("MinCardinalityOfNumericAttributes", num.getMin());
			results.put("MaxCardinalityOfNumericAttributes", num.getMax());
		} else {
			results.put("MeanCardinalityOfNumericAttributes", null); 
			results.put("StdevCardinalityOfNumericAttributes", null);
			results.put("MinCardinalityOfNumericAttributes", null);
			results.put("MaxCardinalityOfNumericAttributes", null);
		}
		if (hasNom) {
			results.put("MeanCardinalityOfNominalAttributes", nom.getMean());
			results.put("StdevCardinalityOfNominalAttributes", nom.getStandardDeviation());
			results.put("MinCardinalityOfNominalAttributes", nom.getMin());
			results.put("MaxCardinalityOfNominalAttributes", nom.getMax());
		} else {
			results.put("MeanCardinalityOfNominalAttributes", null);
			results.put("StdevCardinalityOfNominalAttributes", null);
			results.put("MinCardinalityOfNominalAttributes", null);
			results.put("MaxCardinalityOfNominalAttributes", null);
		}
		
		Double cardinalityAtTwo = null;
		Double cardinalityAtThree = null;
		Double cardinalityAtFour = null;
		
		if (instances.numAttributes() >= 2) {
			cardinalityAtTwo = (double) all.get(0) * all.get(1);
		}
		if (instances.numAttributes() >= 3) {
			cardinalityAtThree = (double) all.get(0) * all.get(1) * all.get(2);
		}

		if (instances.numAttributes() >= 4) {
			cardinalityAtFour = (double) all.get(0) * all.get(1) * all.get(2) * all.get(3);
		}
		
		results.put("CardinalityAtTwo", cardinalityAtTwo);
		results.put("CardinalityAtThree", cardinalityAtThree);
		results.put("CardinalityAtFour", cardinalityAtFour);
		
		return results;
	}
	
}
