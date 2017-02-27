package org.openml.webapplication.attributeCharacterization;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.openml.webapplication.fantail.dc.Characterizer;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Utils;

public class AttributeCharacterizer extends Characterizer {

	int index;

	public AttributeCharacterizer(int index) {
		this.index = index;
	}

	public static final String[] ids = new String[] { "ValuesCount", "NonMissingValuesCount", "MissingValuesCount", "Distinct", "AverageClassCount", "Entropy",
			"MostFequentClassCount", "LeastFequentClassCount", "ModeClassCount", "MedianClassCount", "PearsonCorrellationCoefficient",
			"SpearmanCorrelationCoefficient", "CovarianceWithTarget",

			"IsUniform", "IntegersOnly", "Min", "Max", "Kurtosis", "Mean", "Skewness", "StandardDeviation", "Variance", "Mode", "Median", "ValueRange",
			"LowerOuterFence", "HigherOuterFence", "LowerQuartile", "HigherQuartile", "HigherConfidence", "LowerConfidence", "PositiveCount", "NegativeCount",

			"UniformDiscrete", "ChiSquareUniformDistribution", "RationOfDistinguishingCategoriesByKolmogorovSmirnoffSlashChiSquare",
			"RationOfDistinguishingCategoriesByUtest",

			"MissingValues", "AveragePercentageOfClass", "PercentageOfMissing", "PercentageOfNonMissing", "PercentageOfMostFrequentClass",
			"PercentageOfLeastFrequentClass", "ModeClassPercentage", "MedianClassPercentage",

			"PositivePercentage", "NegativePercentage", "HasPositiveValues", "HasNegativeValues" };

	@Override
	public String[] getIDs() {
		return ids;
	}

	public Map<String, Double> characterize(Instances dataset) {
		Map<String, Double> qualities = new HashMap<String, Double>();

		// ValuesCount MissingValuesCount NonMissingValuesCount Distinct
		HashMap<Double, Integer> ValuesCounts = new HashMap<Double, Integer>();
		Double ValuesCount = 0.0;
		Double MissingValuesCount = 0.0;
		Double NonMissingValuesCount = 0.0;
		Double Distinct = 0.0;
		for (int i = 0; i < dataset.numInstances(); i++) {
			double value = dataset.get(i).value(index);
			ValuesCount++;

			if (dataset.get(i).isMissing(index)) {
				MissingValuesCount++;
			} else {
				NonMissingValuesCount++;
			}

			if (ValuesCounts.containsKey(value)) {
				ValuesCounts.replace(value, ValuesCounts.get(value) + 1);
			} else {
				ValuesCounts.put(value, 1);
			}
		}
		Distinct += ValuesCounts.size();

		// AverageClassCount MostFequentClassCount LeastFequentClassCount MedianClassCount
		DescriptiveStatistics ValuesCountsStats = new DescriptiveStatistics();
		HashMap<Integer, Integer> ValuesCountsCounts = new HashMap<Integer, Integer>();
		for (Integer value : ValuesCounts.values()) {
			ValuesCountsStats.addValue(value);
			if (ValuesCountsCounts.containsKey(value)) {
				ValuesCountsCounts.replace(value, ValuesCountsCounts.get(value) + 1);
			} else {
				ValuesCountsCounts.put(value, 1);
			}
		}
		Double AverageClassCount = ValuesCountsStats.getMean();
		Double MostFequentClassCount = ValuesCountsStats.getMax();
		Double LeastFequentClassCount = ValuesCountsStats.getMin();
		Double MedianClassCount = ValuesCountsStats.getPercentile(50);

		// ModeClassCount
		DescriptiveStatistics ValuesCountsCountsStats = new DescriptiveStatistics();
		for (Integer value : ValuesCountsCounts.values()) {
			ValuesCountsCountsStats.addValue(value);
		}
		Double ModeClassCount = ValuesCountsCountsStats.getMax();

		// Entropy
		Double Entropy = 0.0;
		for (Integer count : ValuesCounts.values()) {
			double valueProb = count / ValuesCount;
			Entropy -= valueProb * (Utils.log2(valueProb));
		}

		// PearsonCorrellationCoefficient SpearmanCorrelationCoefficient Covariance
		SpearmansCorrelation spearmans = new SpearmansCorrelation();
		PearsonsCorrelation pearsons = new PearsonsCorrelation();
		Covariance covariance = new Covariance();
		Double SpearmanCorrelationCoefficient = spearmans.correlation(dataset.attributeToDoubleArray(index), dataset.attributeToDoubleArray(dataset.classIndex()));
		Double PearsonCorrellationCoefficient = pearsons.correlation(dataset.attributeToDoubleArray(index), dataset.attributeToDoubleArray(dataset.classIndex()));
		Double CovarianceWithTarget = covariance.covariance(dataset.attributeToDoubleArray(index), dataset.attributeToDoubleArray(dataset.classIndex()));
		

		// TODO remove dummies
		Double Dummy = 0.0;

		// ValuesCount
		qualities.put(ids[0], ValuesCount);
		// NonMissingValuesCount
		qualities.put(ids[1], NonMissingValuesCount);
		// MissingValuesCount
		qualities.put(ids[2], MissingValuesCount);
		// Distinct
		qualities.put(ids[3], Distinct);
		// AverageClassCount
		qualities.put(ids[4], AverageClassCount);
		// Entropy
		qualities.put(ids[5], Entropy);
		// MostFequentClassCount
		qualities.put(ids[6], MostFequentClassCount);
		// LeastFequentClassCount
		qualities.put(ids[7], LeastFequentClassCount);
		// ModeClassCount
		qualities.put(ids[8], ModeClassCount);
		// MedianClassCount
		qualities.put(ids[9], MedianClassCount);
		// PearsonCorrellationCoefficient
		qualities.put(ids[10], PearsonCorrellationCoefficient);
		// SpearmanCorrelationCoefficient
		qualities.put(ids[11], SpearmanCorrelationCoefficient);
		// CovarianceWithTarget
		qualities.put(ids[12], CovarianceWithTarget);

		Attribute attribute = dataset.attribute(index);
		if (attribute.isNumeric()) {

			
			// IsUniform
			qualities.put(ids[13], ValuesCount);
			// IntegersOnly
			qualities.put(ids[14], ValuesCount);
			// Min
			qualities.put(ids[15], ValuesCount);
			// Max
			qualities.put(ids[16], ValuesCount);
			// Kurtosis
			qualities.put(ids[17], ValuesCount);
			// Mean
			qualities.put(ids[18], ValuesCount);
			// Skewness
			qualities.put(ids[19], ValuesCount);
			// StandardDeviation
			qualities.put(ids[20], ValuesCount);
			// Variance
			qualities.put(ids[21], ValuesCount);
			// Mode
			qualities.put(ids[22], ValuesCount);
			// Median
			qualities.put(ids[23], ValuesCount);
			// ValueRange
			qualities.put(ids[24], ValuesCount);
			// LowerOuterFence
			qualities.put(ids[25], ValuesCount);
			// HigherOuterFence
			qualities.put(ids[26], ValuesCount);
			// LowerQuartile
			qualities.put(ids[27], ValuesCount);
			// HigherQuartile
			qualities.put(ids[28], ValuesCount);
			// HigherConfidence
			qualities.put(ids[29], ValuesCount);
			// LowerConfidence
			qualities.put(ids[30], ValuesCount);
			// PositiveCount
			qualities.put(ids[31], ValuesCount);
			// NegativeCount
			qualities.put(ids[32], ValuesCount);	

		} else {

		}

		if (attribute.isNominal()) {
			// UniformDiscrete
			qualities.put(ids[33], ValuesCount);
			// ChiSquareUniformDistribution
			qualities.put(ids[34], ValuesCount);
			// RationOfDistinguishingCategoriesByKolmogorovSmirnoffSlashChiSquare
			qualities.put(ids[35], ValuesCount);
			// RationOfDistinguishingCategoriesByUtest
			qualities.put(ids[36], ValuesCount);
			
		} else {

		}

		
		// MissingValues
		qualities.put(ids[37], Math.min(MissingValuesCount,1.0));
		// AveragePercentageOfClass
		qualities.put(ids[38], AverageClassCount/ValuesCount);
		// PercentageOfMissing
		qualities.put(ids[39], MissingValuesCount/ValuesCount);
		// PercentageOfNonMissing
		qualities.put(ids[40], NonMissingValuesCount/ValuesCount);
		// PercentageOfMostFrequentClass
		qualities.put(ids[41], MostFequentClassCount/ValuesCount);
		// PercentageOfLeastFrequentClass
		qualities.put(ids[42], LeastFequentClassCount/ValuesCount);
		// ModeClassPercentage
		qualities.put(ids[43], ModeClassCount/ValuesCount);
		// MedianClassPercentage
		qualities.put(ids[44], MedianClassCount/ValuesCount);	

		// PositivePercentage
		qualities.put(ids[45], ValuesCount);
		// NegativePercentage
		qualities.put(ids[46], ValuesCount);
		// HasPositiveValues
		qualities.put(ids[47], ValuesCount);
		// HasNegativeValues
		qualities.put(ids[48], ValuesCount);

		return qualities;
	}
}
