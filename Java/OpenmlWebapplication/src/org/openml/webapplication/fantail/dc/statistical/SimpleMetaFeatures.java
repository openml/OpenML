package org.openml.webapplication.fantail.dc.statistical;

import java.util.HashMap;
import java.util.Map;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.webapplication.fantail.dc.Characterizer;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class SimpleMetaFeatures extends Characterizer {

	public static final String[] ids = new String[] { 
		"NumberOfInstances", 
		"NumberOfFeatures", 
		"NumberOfInstancesWithMissingValues", 
		"NumberOfMissingValues",
		"PercentageOfInstancesWithMissingValues",
		"PercentageOfMissingValues",
		"NumberOfNumericFeatures",
		"NumberOfSymbolicFeatures",
		"NumberOfBinaryFeatures",
		"PercentageOfNumericFeatures",
		"PercentageOfSymbolicFeatures",
		"PercentageOfBinaryFeatures",
		"Dimensionality",
		"NumberOfClasses",
		"DefaultAccuracy",
		"MajorityClassSize",
		"MinorityClassSize",
		"MajorityClassPercentage",
		"MinorityClassPercentage",
		"AutoCorrelation"
	};
	
	@Override
	public String[] getIDs() {
		return ids;
	}
	
	public Map<String, Double> characterize(Instances dataset) {
		final Map<String, Double> resultQualities = new HashMap<String,Double>();
		boolean nominalTarget = dataset.classAttribute().isNominal();
		int[] classDistribution = new int[dataset.classAttribute().numValues()];
		
		int NumberOfMissingValues = 0;
		int NumberOfInstancesWithMissingValues = 0;
		int NumberOfNumericFeatures = 0;
		int NumberOfBinaryFeatures = 0;
		int NumberOfClasses = dataset.classAttribute().numValues();
		double Dimensionality = (double) dataset.numAttributes() / (double) dataset.numInstances();
		int MajorityClassSize = -1;
		int MinorityClassSize = Integer.MAX_VALUE;
		double TimeBasedChanges = 0;
		
		// update attribute based quality counters
		for( int i = 0; i < dataset.numAttributes(); ++i ) {
			Attribute att = dataset.attribute( i );
			if(att.isNumeric())	NumberOfNumericFeatures++;
			if(att.isNominal() && att.numValues() == 2) NumberOfBinaryFeatures++;
		}
		
		// we go through all the instances in only one loop. 

		for (int i = 0; i < dataset.numInstances(); ++i) {
			Instance currentInstance = dataset.get(i);
			// increment total number of missing values counter
			for (int j = 0; j < dataset.numAttributes(); ++j) {
				if (currentInstance.isMissing(j)) ++NumberOfMissingValues;
			}
			
			// increment instances / missing value(s) counter
			if (currentInstance.hasMissingValue()) NumberOfInstancesWithMissingValues++;
			
			// increment class values counts
			if (currentInstance.classAttribute().isNominal()) {
				classDistribution[(int) currentInstance.classValue()] ++;
			}
			
			if (i > 0) {
				Instance previousInstance = dataset.get(i - 1);
				if (nominalTarget == false) {
					TimeBasedChanges += previousInstance.classValue() - currentInstance.classValue();
				} else {
					TimeBasedChanges += previousInstance.classValue() == currentInstance.classValue() ? 0 : 1;
				}
			}
		}
		
		// update statistics on class attribute
		for (int nominalSize : classDistribution) { // check will only be performed with nominal target
			if (nominalSize > MajorityClassSize) { MajorityClassSize = nominalSize; }
			if (nominalSize < MinorityClassSize) { MinorityClassSize = nominalSize; }
		}
		
		int NumberOfSymbolicFeatures = (dataset.numAttributes() - NumberOfNumericFeatures);
		double PercentageOfSymbolicFeatures = Conversion.percentage(NumberOfSymbolicFeatures, dataset.numAttributes());
		double PercentageOfBinaryFeatures = Conversion.percentage(NumberOfBinaryFeatures, dataset.numAttributes());
		double PercentageOfNumericFeatures = Conversion.percentage(NumberOfNumericFeatures, dataset.numAttributes());
		double PercentageOfInstancesWithMissingValues = Conversion.percentage(NumberOfInstancesWithMissingValues, dataset.numInstances());
		double PercentageOfMissingValues = Conversion.percentage(NumberOfMissingValues, dataset.numAttributes() * dataset.numInstances());
		double AutoCorrelation = (dataset.numInstances() - 1 - TimeBasedChanges) / (dataset.numInstances() - 1);
		
		resultQualities.put("NumberOfInstances", (double) dataset.numInstances());
		resultQualities.put("NumberOfFeatures", (double) dataset.numAttributes());
		resultQualities.put("NumberOfInstancesWithMissingValues", (double) NumberOfInstancesWithMissingValues);
		resultQualities.put("NumberOfMissingValues", (double) NumberOfMissingValues);
		resultQualities.put("PercentageOfInstancesWithMissingValues", PercentageOfInstancesWithMissingValues);
		resultQualities.put("PercentageOfMissingValues", PercentageOfMissingValues);
		resultQualities.put("NumberOfNumericFeatures", (double) NumberOfNumericFeatures);
		resultQualities.put("NumberOfSymbolicFeatures", (double) NumberOfSymbolicFeatures);
		resultQualities.put("NumberOfBinaryFeatures", (double) NumberOfBinaryFeatures);
		resultQualities.put("PercentageOfNumericFeatures", PercentageOfNumericFeatures);
		resultQualities.put("PercentageOfSymbolicFeatures", PercentageOfSymbolicFeatures);
		resultQualities.put("PercentageOfBinaryFeatures", PercentageOfBinaryFeatures);
		resultQualities.put("Dimensionality", Dimensionality);
		resultQualities.put("AutoCorrelation", AutoCorrelation);
		
		if (nominalTarget) {
			resultQualities.put("NumberOfClasses", (double) NumberOfClasses);
			resultQualities.put("DefaultAccuracy", ((double) MajorityClassSize) / dataset.numInstances());
			resultQualities.put("MajorityClassSize", (double) MajorityClassSize);
			resultQualities.put("MinorityClassSize", (double) MinorityClassSize);
			resultQualities.put("MajorityClassPercentage", Conversion.percentage(MajorityClassSize, dataset.numInstances()));
			resultQualities.put("MinorityClassPerentage", Conversion.percentage(MinorityClassSize, dataset.numInstances()));
		} else {
			resultQualities.put("NumberOfClasses", -1.0);
			resultQualities.put("DefaultAccuracy", -1.0);
			resultQualities.put("MajorityClassSize", -1.0);
			resultQualities.put("MinorityClassSize", -1.0);
			resultQualities.put("MajorityClassPercentage", -1.0);
			resultQualities.put("MinorityClassPerentage", -1.0);
		}
		
		return resultQualities;
	}
}
