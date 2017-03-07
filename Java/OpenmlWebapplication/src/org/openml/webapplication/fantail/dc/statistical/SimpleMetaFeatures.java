package org.openml.webapplication.fantail.dc.statistical;

import java.util.HashMap;
import java.util.Map;

import org.openml.webapplication.fantail.dc.Characterizer;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class SimpleMetaFeatures extends Characterizer {

	public static final String[] ids = new String[] { "NumberOfInstances", "NumberOfFeatures", "NumberOfInstancesWithMissingValues", "NumberOfMissingValues",
			
			"PercentageOfInstancesWithMissingValues", "PercentageOfMissingValues", 
			
			"NumberOfNumericFeatures", "NumberOfSymbolicFeatures",			"NumberOfBinaryFeatures", 
			
			"PercentageOfNumericFeatures", "PercentageOfSymbolicFeatures", "PercentageOfBinaryFeatures", 
			
			"Dimensionality",
			"NumberOfClasses", "DefaultAccuracy", "MajorityClassSize", "MinorityClassSize", "MajorityClassPercentage", "MinorityClassPercentage",
			"AutoCorrelation" };

	@Override
	public String[] getIDs() {
		return ids;
	}

	public Map<String, Double> characterize(Instances dataset) {
		Map<String, Double> qualities = new HashMap<String, Double>();
		try {

			Double NumberOfInstances = 0.0 + dataset.size();
			Double NumberOfFeatures = 0.0 + dataset.numAttributes();
			Double Dimensionality = (NumberOfInstances > 0 ? NumberOfFeatures / NumberOfInstances : 0);

			Double NumberOfInstancesWithMissingValues = null;
			Double NumberOfMissingValues = null;
			Double PercentageOfInstancesWithMissingValues = null;
			Double PercentageOfMissingValues = null;
			Double NumberOfNumericFeatures = null;
			Double NumberOfSymbolicFeatures = null;
			Double NumberOfBinaryFeatures = null;
			Double PercentageOfNumericFeatures = null;
			Double PercentageOfSymbolicFeatures = null;
			Double PercentageOfBinaryFeatures = null;
			Double AutoCorrelation = null;

			Double NumberOfClasses = null;
			Double DefaultAccuracy = null;
			Double MajorityClassSize = null;
			Double MinorityClassSize = null;
			Double MajorityClassPercentage = null;
			Double MinorityClassPercentage = null;

			// Features
			NumberOfNumericFeatures = 0.0;
			NumberOfSymbolicFeatures = 0.0;
			NumberOfBinaryFeatures = 0.0;
			for (int i = 0; i < dataset.numAttributes(); ++i) {
				Attribute att = dataset.attribute(i);
				if (att.isNumeric())
					NumberOfNumericFeatures++;
				if (att.isNominal()) {
					NumberOfSymbolicFeatures++;
					if (att.numValues() == 2)
						NumberOfBinaryFeatures++;
				}
			}
			PercentageOfNumericFeatures = (NumberOfFeatures > 0 ? NumberOfNumericFeatures / NumberOfFeatures : null);
			PercentageOfSymbolicFeatures = (NumberOfFeatures > 0 ? NumberOfSymbolicFeatures / NumberOfFeatures : null);
			PercentageOfBinaryFeatures = (NumberOfFeatures > 0 ? NumberOfBinaryFeatures / NumberOfFeatures : null);

			// MissingValues
			NumberOfMissingValues = 0.0;
			NumberOfInstancesWithMissingValues = 0.0;
			for (int i = 0; i < dataset.numInstances(); i++) {
				Instance currentInstance = dataset.get(i);

				for (int j = 0; j < dataset.numAttributes(); j++) {
					if (currentInstance.isMissing(j))
						NumberOfMissingValues++;
				}

				if (currentInstance.hasMissingValue())
					NumberOfInstancesWithMissingValues++;
			}
			PercentageOfInstancesWithMissingValues = (NumberOfInstances > 0 ? NumberOfInstancesWithMissingValues / NumberOfInstances : null);
			PercentageOfMissingValues = (NumberOfFeatures * NumberOfInstances > 0 ? NumberOfMissingValues / (NumberOfFeatures * NumberOfInstances) : null);

			// AutoCorrelation
			double TimeBasedChanges = 0.0;
			try {
				for (int i = 1; i < dataset.numInstances(); i++) {
					Instance currentInstance = dataset.get(i);
					Instance previousInstance = dataset.get(i - 1);
					if (dataset.classAttribute().isNumeric()) {
						TimeBasedChanges += previousInstance.classValue() - currentInstance.classValue();
					} else if (dataset.classAttribute().isNominal()) {
						TimeBasedChanges += (previousInstance.classValue() == currentInstance.classValue() ? 0 : 1);
					}
				}
				AutoCorrelation = (NumberOfInstances > 1 ? (NumberOfInstances - 1 - TimeBasedChanges) / (NumberOfInstances - 1) : null);
			} catch (Exception e) {
				AutoCorrelation = null;
			}

			// Class propoerties
			try {
				MajorityClassSize = Double.MIN_VALUE;
				MinorityClassSize = Double.MAX_VALUE;
				HashMap<Double, Integer> ValuesCounts = new HashMap<Double, Integer>();
				for (int i = 0; i < dataset.numInstances(); i++) {
					double value = dataset.get(i).classValue();
					if (ValuesCounts.containsKey(value)) {
						ValuesCounts.replace(value, ValuesCounts.get(value) + 1);
					} else {
						ValuesCounts.put(value, 1);
					}
				}
				for (Integer count : ValuesCounts.values()) {
					if (count > MajorityClassSize) {
						MajorityClassSize = (double) count;
					}
					if (count < MinorityClassSize) {
						MinorityClassSize = (double) count;
					}
				}
				NumberOfClasses = (double) ValuesCounts.size();
				MajorityClassPercentage = (NumberOfInstances > 0 ? MajorityClassSize / NumberOfInstances : null);
				MinorityClassPercentage = (NumberOfInstances > 0 ? MinorityClassSize / NumberOfInstances : null);
				DefaultAccuracy = MajorityClassPercentage;

			} catch (Exception e) {
				NumberOfClasses = null;
				DefaultAccuracy = null;
				MajorityClassSize = null;
				MinorityClassSize = null;
				MajorityClassPercentage = null;
				MinorityClassPercentage = null;
			}

			qualities.put("NumberOfInstances", NumberOfInstances);
			qualities.put("NumberOfFeatures", NumberOfFeatures);
			qualities.put("NumberOfInstancesWithMissingValues", NumberOfInstancesWithMissingValues);
			qualities.put("NumberOfMissingValues", NumberOfMissingValues);
			qualities.put("PercentageOfInstancesWithMissingValues", PercentageOfInstancesWithMissingValues);
			qualities.put("PercentageOfMissingValues", PercentageOfMissingValues);
			qualities.put("NumberOfNumericFeatures", NumberOfNumericFeatures);
			qualities.put("NumberOfSymbolicFeatures", NumberOfSymbolicFeatures);
			qualities.put("NumberOfBinaryFeatures", NumberOfBinaryFeatures);
			qualities.put("PercentageOfNumericFeatures", PercentageOfNumericFeatures);
			qualities.put("PercentageOfSymbolicFeatures", PercentageOfSymbolicFeatures);
			qualities.put("PercentageOfBinaryFeatures", PercentageOfBinaryFeatures);
			qualities.put("Dimensionality", Dimensionality);
			qualities.put("AutoCorrelation", AutoCorrelation);
			qualities.put("NumberOfClasses", NumberOfClasses);
			qualities.put("DefaultAccuracy", DefaultAccuracy);
			qualities.put("MajorityClassSize", MajorityClassSize);
			qualities.put("MinorityClassSize", MinorityClassSize);
			qualities.put("MajorityClassPercentage", MajorityClassPercentage);
			qualities.put("MinorityClassPercentage", MinorityClassPercentage);

		} catch (Exception e) {
		}

		// enforce finite double or null for all qualities
		for (String key : qualities.keySet()) {
			if (qualities.get(key) != null && !Double.isFinite(qualities.get(key)))
				qualities.replace(key, null);
		}

		for (String key : ids) {
			if (!qualities.containsKey(key))
				qualities.put(key, null);
		}

		return qualities;
	}
}
