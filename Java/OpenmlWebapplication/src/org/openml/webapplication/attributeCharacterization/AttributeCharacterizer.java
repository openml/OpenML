package org.openml.webapplication.attributeCharacterization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.openml.webapplication.fantail.dc.Characterizer;
import java.util.Arrays;
import org.apache.commons.math3.special.Gamma;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Utils;

public class AttributeCharacterizer extends Characterizer {

	int index;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public AttributeCharacterizer(int index) {
		this.index = index;
	}

	public AttributeCharacterizer() {
		this.index = -1;
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
		if (index < 0 || index > dataset.numAttributes())
			throw new RuntimeException("Invalid index (" + index + ") in AttributeCharacterizer for dataset " + dataset.relationName());

		Map<String, Double> qualities = new HashMap<String, Double>();

		// list of non missing values and counts of values
		ArrayList<Double> attValuesList = new ArrayList<Double>();
		ArrayList<Double> classValuesList = new ArrayList<Double>();
		HashMap<Double, Integer> ValuesCounts = new HashMap<Double, Integer>();

		// ValuesCount MissingValuesCount NonMissingValuesCount Distinct
		Double ValuesCount = 0.0;
		Double MissingValuesCount = 0.0;
		Double NonMissingValuesCount = 0.0;
		Double Distinct = 0.0;
		for (int i = 0; i < dataset.numInstances(); i++) {
			ValuesCount++;

			if (dataset.get(i).isMissing(index)) {
				MissingValuesCount++;
			} else {
				double value = dataset.get(i).value(index);
				NonMissingValuesCount++;

				attValuesList.add(value);
				classValuesList.add(dataset.get(i).classValue());

				if (ValuesCounts.containsKey(value)) {
					ValuesCounts.replace(value, ValuesCounts.get(value) + 1);
				} else {
					ValuesCounts.put(value, 1);
				}
			}
		}
		Distinct += ValuesCounts.size();

		// AverageClassCount MostFequentClassCount LeastFequentClassCount MedianClassCount Mode
		DescriptiveStatistics ValuesCountsStats = new DescriptiveStatistics();
		HashMap<Integer, Integer> ValuesCountsCounts = new HashMap<Integer, Integer>();
		for (Integer count : ValuesCounts.values()) {
			ValuesCountsStats.addValue(count);
			if (ValuesCountsCounts.containsKey(count)) {
				ValuesCountsCounts.replace(count, ValuesCountsCounts.get(count) + 1);
			} else {
				ValuesCountsCounts.put(count, 1);
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
		try {
			for (Integer count : ValuesCounts.values()) {
				double valueProb = count / ValuesCount;
				Entropy -= valueProb * (Utils.log2(valueProb));
			}
			if (!Double.isFinite(Entropy))
				throw new Exception();
		} catch (Exception e) {
			Entropy = null;
		}

		// PearsonCorrellationCoefficient SpearmanCorrelationCoefficient Covariance
		double[] attValuesTab = new double[attValuesList.size()];
		double[] classValuesTab = new double[classValuesList.size()];
		for (int i = 0; i < attValuesList.size(); i++) {
			attValuesTab[i] = attValuesList.get(i);
			classValuesTab[i] = classValuesList.get(i);
		}
		SpearmansCorrelation spearmans = new SpearmansCorrelation();
		PearsonsCorrelation pearsons = new PearsonsCorrelation();
		Covariance covariance = new Covariance();
		Double SpearmanCorrelationCoefficient;
		Double PearsonCorrellationCoefficient;
		Double CovarianceWithTarget;
		try {
			SpearmanCorrelationCoefficient = spearmans.correlation(attValuesTab, classValuesTab);
			if (!Double.isFinite(SpearmanCorrelationCoefficient) || SpearmanCorrelationCoefficient<-1 || SpearmanCorrelationCoefficient>1)
				throw new Exception();
		} catch (Exception e) {
			SpearmanCorrelationCoefficient = null;
		}
		try {
			PearsonCorrellationCoefficient = pearsons.correlation(attValuesTab, classValuesTab);
			if (!Double.isFinite(PearsonCorrellationCoefficient) || PearsonCorrellationCoefficient<-1 || PearsonCorrellationCoefficient>1)
				throw new Exception();
		} catch (Exception e) {
			PearsonCorrellationCoefficient = null;
		}
		try {
			CovarianceWithTarget = covariance.covariance(attValuesTab, classValuesTab);
			if (!Double.isFinite(CovarianceWithTarget))
				throw new Exception();
		} catch (Exception e) {
			CovarianceWithTarget = null;
		}

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
		// MissingValues
		qualities.put(ids[37], Math.min(MissingValuesCount, 1.0));
		// AveragePercentageOfClass
		qualities.put(ids[38], AverageClassCount / NonMissingValuesCount);
		// PercentageOfMissing
		qualities.put(ids[39], MissingValuesCount / ValuesCount);
		// PercentageOfNonMissing
		qualities.put(ids[40], NonMissingValuesCount / ValuesCount);
		// PercentageOfMostFrequentClass
		qualities.put(ids[41], MostFequentClassCount / NonMissingValuesCount);
		// PercentageOfLeastFrequentClass
		qualities.put(ids[42], LeastFequentClassCount / NonMissingValuesCount);
		// ModeClassPercentage
		qualities.put(ids[43], ModeClassCount / NonMissingValuesCount);
		// MedianClassPercentage
		qualities.put(ids[44], MedianClassCount / NonMissingValuesCount);

		// Numeric specific meta-features
		Attribute attribute = dataset.attribute(index);
		if (attribute.isNumeric()) {

			Double PositiveCount = 0.0;
			Double NegativeCount = 0.0;
			boolean IntegersOnly = true;
			DescriptiveStatistics AttributeStats = new DescriptiveStatistics();
			for (int i = 0; i < dataset.numInstances(); i++) {
				if (!dataset.get(i).isMissing(index)) {
					double value = dataset.get(i).value(index);
					AttributeStats.addValue(value);
					if (IntegersOnly && Double.isFinite(value) && (value != Math.floor(value))) {
						IntegersOnly = false;
					}
					if (value < 0.0) {
						NegativeCount++;
					} else {
						PositiveCount++;
					}
				}
			}

			KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();
			double uniformPval = ksTest.kolmogorovSmirnovTest(new UniformRealDistribution(AttributeStats.getMin(), AttributeStats.getMax()),
					dataset.attributeToDoubleArray(index));

			Double Mode = null;
			int maxCount = Integer.MIN_VALUE;
			for (Double value : ValuesCounts.keySet()) {
				int count = ValuesCounts.get(value);
				if (count > maxCount) {
					maxCount = count;
					Mode = value;
				}
			}

			// IsUniform
			qualities.put(ids[13], (uniformPval > 0.05 ? 1.0 : 0.0));
			// IntegersOnly
			qualities.put(ids[14], (IntegersOnly ? 1.0 : 0.0));
			// Min
			qualities.put(ids[15], AttributeStats.getMin());
			// Max
			qualities.put(ids[16], AttributeStats.getMax());
			// Kurtosis
			qualities.put(ids[17], AttributeStats.getKurtosis());
			// Mean
			qualities.put(ids[18], AttributeStats.getMean());
			// Skewness
			qualities.put(ids[19], AttributeStats.getSkewness());
			// StandardDeviation
			qualities.put(ids[20], AttributeStats.getStandardDeviation());
			// Variance
			qualities.put(ids[21], AttributeStats.getVariance());
			// Mode
			qualities.put(ids[22], Mode);
			// Median
			qualities.put(ids[23], AttributeStats.getPercentile(50));
			// ValueRange
			qualities.put(ids[24], AttributeStats.getMax() - AttributeStats.getMin());
			// LowerOuterFence
			qualities.put(ids[25], AttributeStats.getPercentile(25) - 3 * (AttributeStats.getPercentile(75) - AttributeStats.getPercentile(25)));
			// HigherOuterFence
			qualities.put(ids[26], AttributeStats.getPercentile(75) + 3 * (AttributeStats.getPercentile(75) - AttributeStats.getPercentile(25)));
			// LowerQuartile
			qualities.put(ids[27], AttributeStats.getPercentile(25));
			// HigherQuartile
			qualities.put(ids[28], AttributeStats.getPercentile(75));
			// HigherConfidence
			qualities.put(ids[29], AttributeStats.getMean() + 1.96 * AttributeStats.getStandardDeviation() / Math.sqrt(NonMissingValuesCount));
			// LowerConfidence
			qualities.put(ids[30], AttributeStats.getMean() - 1.96 * AttributeStats.getStandardDeviation() / Math.sqrt(NonMissingValuesCount));
			// PositiveCount
			qualities.put(ids[31], PositiveCount);
			// NegativeCount
			qualities.put(ids[32], NegativeCount);
			// PositivePercentage
			qualities.put(ids[45], PositiveCount / NonMissingValuesCount);
			// NegativePercentage
			qualities.put(ids[46], NegativeCount / NonMissingValuesCount);
			// HasPositiveValues
			qualities.put(ids[47], Math.min(PositiveCount, 1.0));
			// HasNegativeValues
			qualities.put(ids[48], Math.min(NegativeCount, 1.0));

		} else {
			// IsUniform
			qualities.put(ids[13], null);
			// IntegersOnly
			qualities.put(ids[14], null);
			// Min
			qualities.put(ids[15], null);
			// Max
			qualities.put(ids[16], null);
			// Kurtosis
			qualities.put(ids[17], null);
			// Mean
			qualities.put(ids[18], null);
			// Skewness
			qualities.put(ids[19], null);
			// StandardDeviation
			qualities.put(ids[20], null);
			// Variance
			qualities.put(ids[21], null);
			// Mode
			qualities.put(ids[22], null);
			// Median
			qualities.put(ids[23], null);
			// ValueRange
			qualities.put(ids[24], null);
			// LowerOuterFence
			qualities.put(ids[25], null);
			// HigherOuterFence
			qualities.put(ids[26], null);
			// LowerQuartile
			qualities.put(ids[27], null);
			// HigherQuartile
			qualities.put(ids[28], null);
			// HigherConfidence
			qualities.put(ids[29], null);
			// LowerConfidence
			qualities.put(ids[30], null);
			// PositiveCount
			qualities.put(ids[31], null);
			// NegativeCount
			qualities.put(ids[32], null);
			// PositivePercentage
			qualities.put(ids[45], null);
			// NegativePercentage
			qualities.put(ids[46], null);
			// HasPositiveValues
			qualities.put(ids[47], null);
			// HasNegativeValues
			qualities.put(ids[48], null);
		}

		// Nominal specific meta-features
		if (attribute.isNominal()) {

			// UniformDiscrete ChiSquareUniformDistribution
			double avg = Arrays.stream(attValuesTab).sum() / attValuesTab.length;
			double chiSquare = Arrays.stream(attValuesTab).reduce(0, (a, b) -> a + Math.pow((b - avg), 2));
			boolean UniformDiscrete = (Gamma.regularizedGammaQ((attValuesTab.length - 1.0) / 2, chiSquare / (2 * avg)) > 0.05);

			// RationOfDistinguishingCategories
			int nbValuesChangingTargetDistributionKs = 0;
			int nbValuesChangingTargetDistributionU = 0;

			for (Double value : ValuesCounts.keySet()) {
				double[] classValuesSubset = new double[ValuesCounts.get(value)];
				int subsetIndex = 0;
				for (int i = 0; i < classValuesTab.length; i++) {
					if (value.doubleValue() == attValuesTab[i]) {
						classValuesSubset[subsetIndex] = classValuesTab[i];
						subsetIndex++;
					}
				}

				KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();
				if (ksTest.kolmogorovSmirnovTest(classValuesTab, classValuesSubset) > 0.05) {
					nbValuesChangingTargetDistributionKs++;
				}

				MannWhitneyUTest UTest = new MannWhitneyUTest();
				if (UTest.mannWhitneyUTest(classValuesTab, classValuesSubset) > 0.05) {
					nbValuesChangingTargetDistributionU++;
				}
			}

			// UniformDiscrete
			qualities.put(ids[33], (UniformDiscrete ? 1.0 : 0.0));
			// ChiSquareUniformDistribution
			qualities.put(ids[34], chiSquare);
			// RationOfDistinguishingCategoriesByKolmogorovSmirnoffSlashChiSquare
			qualities.put(ids[35], nbValuesChangingTargetDistributionKs / Distinct);
			// RationOfDistinguishingCategoriesByUtest
			qualities.put(ids[36], nbValuesChangingTargetDistributionU / Distinct);

		} else {
			// UniformDiscrete
			qualities.put(ids[33], null);
			// ChiSquareUniformDistribution
			qualities.put(ids[34], null);
			// RationOfDistinguishingCategoriesByKolmogorovSmirnoffSlashChiSquare
			qualities.put(ids[35], null);
			// RationOfDistinguishingCategoriesByUtest
			qualities.put(ids[36], null);
		}

		return qualities;
	}
}
