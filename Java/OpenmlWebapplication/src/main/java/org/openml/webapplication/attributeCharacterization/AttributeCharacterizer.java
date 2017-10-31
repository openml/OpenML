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

	public static final String[] ids = new String[] { "ValuesCount", "NonMissingValuesCount", "MissingValuesCount", "Distinct", "AverageClassCount",
			"MostFequentClassCount", "LeastFequentClassCount", "ModeClassCount", "MedianClassCount", "PearsonCorrellationCoefficient",
			"SpearmanCorrelationCoefficient", "CovarianceWithTarget",

			"Entropy", "JointEntropy", "MutualInformation", "EquivalentNumberOfAtts", "NoiseToSignalRatio",

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

	/**
	 * @param weka.core.Instances
	 *            dataset : the dataset on wich to compute the (index)th attribute meta-features
	 * @return Map<String, Double> qualities : map of meta-features (name->value), every quality from getIDs() is supposed to be present and value has to be
	 *         either a finite Double or null. null means that the meta-feature makes no sense on this attribute, or that it failed computation.
	 */
	public Map<String, Double> characterize(Instances dataset) {
		if (index < 0 || index > dataset.numAttributes())
			throw new RuntimeException("Invalid index (" + index + ") in AttributeCharacterizer for dataset " + dataset.relationName());

		Map<String, Double> qualities = new HashMap<String, Double>();
		
			// ****************************************************************************************
			// counts of values
			HashMap<Double, Integer> ValuesCounts = new HashMap<Double, Integer>();

			Double ValuesCount = null;
			Double MissingValuesCount = null;
			Double NonMissingValuesCount = null;
			Double Distinct = null;
			Double AverageClassCount = null;
			Double MostFequentClassCount = null;
			Double LeastFequentClassCount = null;
			Double MedianClassCount = null;
			Double ModeClassCount = null;
			Double MissingValues = null;
			Double AveragePercentageOfClass = null;
			Double PercentageOfMissing = null;
			Double PercentageOfNonMissing = null;
			Double PercentageOfMostFrequentClass = null;
			Double PercentageOfLeastFrequentClass = null;
			Double ModeClassPercentage = null;
			Double MedianClassPercentage = null;

			ValuesCount = 0.0;
			MissingValuesCount = 0.0;
			NonMissingValuesCount = 0.0;
			for (int i = 0; i < dataset.numInstances(); i++) {
				ValuesCount++;

				if (dataset.get(i).isMissing(index)) {
					MissingValuesCount++;
				} else {
					double value = dataset.get(i).value(index);
					NonMissingValuesCount++;

					if (ValuesCounts.containsKey(value)) {
						ValuesCounts.replace(value, ValuesCounts.get(value) + 1);
					} else {
						ValuesCounts.put(value, 1);
					}
				}
			}
			Distinct = (double) ValuesCounts.size();

			// ClassCounts
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
			AverageClassCount = ValuesCountsStats.getMean();
			MostFequentClassCount = ValuesCountsStats.getMax();
			LeastFequentClassCount = ValuesCountsStats.getMin();
			MedianClassCount = ValuesCountsStats.getPercentile(50);

			// ModeClassCount
			DescriptiveStatistics ValuesCountsCountsStats = new DescriptiveStatistics();
			for (Integer value : ValuesCountsCounts.values()) {
				ValuesCountsCountsStats.addValue(value);
			}
			ModeClassCount = ValuesCountsCountsStats.getMax();

			// Percentages
			MissingValues = Math.min(MissingValuesCount, 1.0);
			AveragePercentageOfClass = (NonMissingValuesCount > 0 ? AverageClassCount / NonMissingValuesCount : 0.0);
			PercentageOfMissing = (ValuesCount > 0 ? MissingValuesCount / ValuesCount : 1.0);
			PercentageOfNonMissing = (ValuesCount > 0 ? NonMissingValuesCount / ValuesCount : 0.0);
			PercentageOfMostFrequentClass = (NonMissingValuesCount > 0 ? MostFequentClassCount / NonMissingValuesCount : 0.0);
			PercentageOfLeastFrequentClass = (NonMissingValuesCount > 0 ? LeastFequentClassCount / NonMissingValuesCount : 0.0);
			ModeClassPercentage = (NonMissingValuesCount > 0 ? ModeClassCount / NonMissingValuesCount : 0.0);
			MedianClassPercentage = (NonMissingValuesCount > 0 ? MedianClassCount / NonMissingValuesCount : 0.0);

			qualities.put("ValuesCount", ValuesCount);
			qualities.put("NonMissingValuesCount", NonMissingValuesCount);
			qualities.put("MissingValuesCount", MissingValuesCount);
			qualities.put("Distinct", Distinct);
			qualities.put("AverageClassCount", AverageClassCount);
			qualities.put("MostFequentClassCount", MostFequentClassCount);
			qualities.put("LeastFequentClassCount", LeastFequentClassCount);
			qualities.put("ModeClassCount", ModeClassCount);
			qualities.put("MedianClassCount", MedianClassCount);
			qualities.put("MissingValues", MissingValues);
			qualities.put("AveragePercentageOfClass", AveragePercentageOfClass);
			qualities.put("PercentageOfMissing", PercentageOfMissing);
			qualities.put("PercentageOfNonMissing", PercentageOfNonMissing);
			qualities.put("PercentageOfMostFrequentClass", PercentageOfMostFrequentClass);
			qualities.put("PercentageOfLeastFrequentClass", PercentageOfLeastFrequentClass);
			qualities.put("ModeClassPercentage", ModeClassPercentage);
			qualities.put("MedianClassPercentage", MedianClassPercentage);

			// ****************************************************************************************
			// counts of class values for entropy and correlations
			HashMap<Double, Integer> classValuesCounts = new HashMap<Double, Integer>();
			HashMap<Double, HashMap<Double, Integer>> attClassValuesCounts = new HashMap<Double, HashMap<Double, Integer>>();
			ArrayList<Double> attValuesList = new ArrayList<Double>();
			ArrayList<Double> classValuesList = new ArrayList<Double>();
			double fullPairsCount = 0;
			for (int i = 0; i < dataset.numInstances(); i++) {
				if (!dataset.get(i).isMissing(index) && !dataset.get(i).classIsMissing()) {
					fullPairsCount++;
					double attValue = dataset.get(i).value(index);
					double classValue = dataset.get(i).classValue();

					attValuesList.add(dataset.get(i).value(index));
					classValuesList.add(dataset.get(i).classValue());

					if (classValuesCounts.containsKey(classValue)) {
						classValuesCounts.replace(classValue, classValuesCounts.get(classValue) + 1);
					} else {
						classValuesCounts.put(classValue, 1);
					}

					if (attClassValuesCounts.containsKey(attValue)) {
						HashMap<Double, Integer> attValSpecClassCounts = attClassValuesCounts.get(attValue);
						if (attValSpecClassCounts.containsKey(classValue)) {
							attValSpecClassCounts.replace(classValue, attValSpecClassCounts.get(classValue) + 1);
						} else {
							attValSpecClassCounts.put(classValue, 1);
						}
					} else {
						HashMap<Double, Integer> attValSpecClassCounts = new HashMap<Double, Integer>();
						attValSpecClassCounts.put(classValue, 1);
						attClassValuesCounts.put(attValue, attValSpecClassCounts);
					}
				}
			}

			// ****************************************************************************************
			// Entropy
			Double Entropy = 0.0;
			Double ClassEntropy = 0.0;
			Double JointEntropy = 0.0;
			Double MutualInformation = 0.0;
			Double EquivalentNumberOfAtts = 0.0;
			Double NoiseToSignalRatio = 0.0;

			for (double count : ValuesCounts.values()) {
				double valueProb = count / ValuesCount;
				Entropy -= valueProb * (Math.log(valueProb) / Math.log(2));
			}

			for (double count : classValuesCounts.values()) {
				double valueProb = count / fullPairsCount;
				ClassEntropy -= valueProb * (Math.log(valueProb) / Math.log(2));
			}

			for (HashMap<Double, Integer> counts : attClassValuesCounts.values()) {
				for (double count : counts.values()) {
					double valueProb = count / fullPairsCount;
					JointEntropy -= valueProb * (Math.log(valueProb) / Math.log(2));
				}
			}

			MutualInformation = ClassEntropy + Entropy - JointEntropy;
			EquivalentNumberOfAtts = (MutualInformation == 0 ? null : ClassEntropy / MutualInformation);
			NoiseToSignalRatio = (MutualInformation == 0 ? null : (Entropy - MutualInformation) / MutualInformation);

			qualities.put("Entropy", Entropy);
			qualities.put("JointEntropy", JointEntropy);
			qualities.put("MutualInformation", MutualInformation);
			qualities.put("EquivalentNumberOfAtts", EquivalentNumberOfAtts);
			qualities.put("NoiseToSignalRatio", NoiseToSignalRatio);

			// ****************************************************************************************
			// PearsonCorrellationCoefficient SpearmanCorrelationCoefficient Covariance
			Double SpearmanCorrelationCoefficient = null;
			Double PearsonCorrellationCoefficient = null;
			Double CovarianceWithTarget = null;
			double[] attValuesTab = new double[attValuesList.size()];
			double[] classValuesTab = new double[classValuesList.size()];
			for (int i = 0; i < attValuesList.size(); i++) {
				attValuesTab[i] = attValuesList.get(i);
				classValuesTab[i] = classValuesList.get(i);
			}

			if (index != dataset.classIndex()) {
				SpearmansCorrelation spearmans = new SpearmansCorrelation();
				PearsonsCorrelation pearsons = new PearsonsCorrelation();
				Covariance covariance = new Covariance();

				try {
					SpearmanCorrelationCoefficient = spearmans.correlation(attValuesTab, classValuesTab);
					if (SpearmanCorrelationCoefficient < -1 || SpearmanCorrelationCoefficient > 1)
						throw new Exception();
				} catch (Exception e) {
					SpearmanCorrelationCoefficient = null;
				}
				try {
					PearsonCorrellationCoefficient = pearsons.correlation(attValuesTab, classValuesTab);
					if (PearsonCorrellationCoefficient < -1 || PearsonCorrellationCoefficient > 1)
						throw new Exception();
				} catch (Exception e) {
					PearsonCorrellationCoefficient = null;
				}
				try {
					CovarianceWithTarget = covariance.covariance(attValuesTab, classValuesTab);
				} catch (Exception e) {
					CovarianceWithTarget = null;
				}
			}
			qualities.put("PearsonCorrellationCoefficient", PearsonCorrellationCoefficient);
			qualities.put("SpearmanCorrelationCoefficient", SpearmanCorrelationCoefficient);
			qualities.put("CovarianceWithTarget", CovarianceWithTarget);

			// ****************************************************************************************
			// ******************** Numeric specific meta-features
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

				Double IsUniform = null;
				double uniformPval;
				try {
					if (Distinct < 2) {
						IsUniform = 1.0;
					} else {
						KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();
						uniformPval = ksTest.kolmogorovSmirnovTest(new UniformRealDistribution(AttributeStats.getMin(), AttributeStats.getMax()), attValuesTab);
						IsUniform = (uniformPval > 0.05 ? 1.0 : 0.0);
					}
				} catch (Exception e) {
					IsUniform = null;
				}

				Double Mode = null;
				int maxCount = Integer.MIN_VALUE;
				try {
					for (Double value : ValuesCounts.keySet()) {
						int count = ValuesCounts.get(value);
						if (count > maxCount) {
							maxCount = count;
							Mode = value;
						}
					}
				} catch (Exception e) {
					Mode = null;
				}

				qualities.put("IsUniform", IsUniform);
				qualities.put("IntegersOnly", (IntegersOnly ? 1.0 : 0.0));
				qualities.put("Min", AttributeStats.getMin());
				qualities.put("Max", AttributeStats.getMax());
				qualities.put("Kurtosis", AttributeStats.getKurtosis());
				qualities.put("Mean", AttributeStats.getMean());
				qualities.put("Skewness", AttributeStats.getSkewness());
				qualities.put("StandardDeviation", AttributeStats.getStandardDeviation());
				qualities.put("Variance", AttributeStats.getVariance());
				qualities.put("Mode", Mode);
				qualities.put("Median", AttributeStats.getPercentile(50));
				qualities.put("ValueRange", AttributeStats.getMax() - AttributeStats.getMin());
				qualities.put("LowerOuterFence", AttributeStats.getPercentile(25) - 3 * (AttributeStats.getPercentile(75) - AttributeStats.getPercentile(25)));
				qualities.put("HigherOuterFence", AttributeStats.getPercentile(75) + 3 * (AttributeStats.getPercentile(75) - AttributeStats.getPercentile(25)));
				qualities.put("LowerQuartile", AttributeStats.getPercentile(25));
				qualities.put("HigherQuartile", AttributeStats.getPercentile(75));
				qualities.put("HigherConfidence", (NonMissingValuesCount > 0
						? AttributeStats.getMean() + 1.96 * AttributeStats.getStandardDeviation() / Math.sqrt(NonMissingValuesCount) : null));
				qualities.put("LowerConfidence", (NonMissingValuesCount > 0
						? AttributeStats.getMean() - 1.96 * AttributeStats.getStandardDeviation() / Math.sqrt(NonMissingValuesCount) : null));
				qualities.put("PositiveCount", PositiveCount);
				qualities.put("NegativeCount", NegativeCount);
				qualities.put("PositivePercentage", (NonMissingValuesCount > 0 ? PositiveCount / NonMissingValuesCount : 0.0));
				qualities.put("NegativePercentage", (NonMissingValuesCount > 0 ? NegativeCount / NonMissingValuesCount : 0.0));
				qualities.put("HasPositiveValues", Math.min(PositiveCount, 1.0));
				qualities.put("HasNegativeValues", Math.min(NegativeCount, 1.0));
			}

			// ****************************************************************************************
			// ************* Nominal specific meta-features
			if (attribute.isNominal()) {

				// UniformDiscrete ChiSquareUniformDistribution
				Double UniformDiscrete = null;
				Double ChiSquareUniformDistribution = null;

					double avg = Arrays.stream(attValuesTab).sum() / attValuesTab.length;
					ChiSquareUniformDistribution = Arrays.stream(attValuesTab).reduce(0, (a, b) -> a + Math.pow((b - avg), 2));
					UniformDiscrete = ((Gamma.regularizedGammaQ((attValuesTab.length - 1.0) / 2, ChiSquareUniformDistribution / (2 * avg)) > 0.05) ? 1.0 : 0.0);

				qualities.put("UniformDiscrete", UniformDiscrete);
				qualities.put("ChiSquareUniformDistribution", ChiSquareUniformDistribution);

				// RationOfDistinguishingCategories
				Double RationOfDistinguishingCategoriesByKolmogorovSmirnoffSlashChiSquare = null;
				Double RationOfDistinguishingCategoriesByUtest = null;
				if (index != dataset.classIndex()) {

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

							// use if computation of ksTest seems to hang indefinitely
							// try {
							// ExecutorService service = Executors.newFixedThreadPool(1);
							// Future<Double> future;
							// Callable<Double> callable = () -> {
							// KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();
							// return ksTest.kolmogorovSmirnovStatistic(classValuesTab, classValuesSubset);
							// };
							// future = service.submit(callable);
							// service.shutdown();
							//
							// double p = future.get(2, TimeUnit.SECONDS);
							//
							// if (!service.awaitTermination(2, TimeUnit.SECONDS)) {
							// service.shutdownNow();
							// }
							// if (p > 0.05) {
							// nbValuesChangingTargetDistributionKs++;
							// }
							// } catch (Exception e) {
							// }

							KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();
							if (ksTest.kolmogorovSmirnovStatistic(classValuesTab, classValuesSubset) > 0.05) {
								nbValuesChangingTargetDistributionKs++;
							}

							MannWhitneyUTest UTest = new MannWhitneyUTest();
							if (UTest.mannWhitneyUTest(classValuesTab, classValuesSubset) > 0.05) {
								nbValuesChangingTargetDistributionU++;
							}
						}

						RationOfDistinguishingCategoriesByKolmogorovSmirnoffSlashChiSquare = nbValuesChangingTargetDistributionKs / Distinct;
						RationOfDistinguishingCategoriesByUtest = nbValuesChangingTargetDistributionU / Distinct;

				}
				qualities.put("RationOfDistinguishingCategoriesByKolmogorovSmirnoffSlashChiSquare",
						RationOfDistinguishingCategoriesByKolmogorovSmirnoffSlashChiSquare);
				qualities.put("RationOfDistinguishingCategoriesByUtest", RationOfDistinguishingCategoriesByUtest);
			}

		// enforce finite double or null for all existing qualities
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
