package org.openml.webapplication.fantail.dc.statistical;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.openml.webapplication.fantail.dc.Characterizer;

import weka.core.Instances;

public class AttributeEntropy extends Characterizer {

	protected static final String[] ids = new String[] { "ClassEntropy", "MeanAttributeEntropy", "MeanMutualInformation", "EquivalentNumberOfAtts",
			"MeanNoiseToSignalRatio", "MinAttributeEntropy", "MinMutualInformation", "MaxAttributeEntropy", "MaxMutualInformation", "Quartile1AttributeEntropy",
			"Quartile1MutualInformation", "Quartile2AttributeEntropy", "Quartile2MutualInformation", "Quartile3AttributeEntropy", "Quartile3MutualInformation",
			"MeanJointEntropy", "MinJointEntropy", "MaxJointEntropy", "Quartile1JointEntropy", "Quartile2JointEntropy", "Quartile3JointEntropy" };

	@Override
	public String[] getIDs() {
		return ids;
	}

	/**
	 * @param weka.core.Instances
	 *            dataset : the dataset on wich to compute the meta-features
	 * @return Map<String, Double> qualities : map of meta-features (name->value)
	 * @throws Exception
	 */
	public Map<String, Double> characterize(Instances dataset) throws Exception {
		Map<String, Double> qualities = new HashMap<String, Double>();

		// counts of class values
		HashMap<Double, Integer> classValuesCounts = new HashMap<Double, Integer>();
		double instancesWithClass = 0.0;
		for (int i = 0; i < dataset.numInstances(); i++) {
			if (!dataset.get(i).classIsMissing()) {
				instancesWithClass++;
				double classValue = dataset.get(i).classValue();

				if (classValuesCounts.containsKey(classValue)) {
					classValuesCounts.replace(classValue, classValuesCounts.get(classValue) + 1);
				} else {
					classValuesCounts.put(classValue, 1);
				}
			}
		}

		Double ClassEntropy = 0.0;
		for (double count : classValuesCounts.values()) {
			double valueProb = count / instancesWithClass;
			ClassEntropy -= valueProb * (Math.log(valueProb) / Math.log(2));
		}

		DescriptiveStatistics EntropyStats = new DescriptiveStatistics();
		DescriptiveStatistics JointEntropyStats = new DescriptiveStatistics();
		DescriptiveStatistics MutualInformationStats = new DescriptiveStatistics();

		for (int attribute = 0; attribute < dataset.numAttributes(); attribute++) {
			// counts of attribute values
			HashMap<Double, Integer> attValuesCounts = new HashMap<Double, Integer>();
			HashMap<Double, HashMap<Double, Integer>> attClassValuesCounts = new HashMap<Double, HashMap<Double, Integer>>();
			double instancesWithAtt = 0;
			double instancesWithAttClass = 0;
			for (int i = 0; i < dataset.numInstances(); i++) {

				if (!dataset.get(i).isMissing(attribute)) {
					instancesWithAtt++;
					double attValue = dataset.get(i).value(attribute);

					if (attValuesCounts.containsKey(attValue)) {
						attValuesCounts.replace(attValue, attValuesCounts.get(attValue) + 1);
					} else {
						attValuesCounts.put(attValue, 1);
					}

					if (!dataset.get(i).classIsMissing()) {
						instancesWithAttClass++;
						double classValue = dataset.get(i).classValue();

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
			}

			double Entropy = 0.0;
			double JointEntropy = 0.0;
			double MutualInformation = 0.0;

			if (instancesWithAtt != 0 && instancesWithAttClass != 0) {
				for (double count : attValuesCounts.values()) {
					double valueProb = count / instancesWithAtt;
					Entropy -= valueProb * (Math.log(valueProb) / Math.log(2));
				}

				for (HashMap<Double, Integer> counts : attClassValuesCounts.values()) {
					for (double count : counts.values()) {
						double valueProb = count / instancesWithAttClass;
						JointEntropy -= valueProb * (Math.log(valueProb) / Math.log(2));
					}
				}

				MutualInformation = ClassEntropy + Entropy - JointEntropy;

				EntropyStats.addValue(Entropy);
				JointEntropyStats.addValue(JointEntropy);
				MutualInformationStats.addValue(MutualInformation);
			}
		}

		qualities.put("ClassEntropy", ClassEntropy);
		qualities.put("MeanAttributeEntropy", EntropyStats.getMean());
		qualities.put("MinAttributeEntropy", EntropyStats.getMin());
		qualities.put("MaxAttributeEntropy", EntropyStats.getMax());
		qualities.put("Quartile1AttributeEntropy", EntropyStats.getPercentile(25));
		qualities.put("Quartile2AttributeEntropy", EntropyStats.getPercentile(50));
		qualities.put("Quartile3AttributeEntropy", EntropyStats.getPercentile(75));
		qualities.put("MeanJointEntropy", JointEntropyStats.getMean());
		qualities.put("MinJointEntropy", JointEntropyStats.getMin());
		qualities.put("MaxJointEntropy", JointEntropyStats.getMax());
		qualities.put("Quartile1JointEntropy", JointEntropyStats.getPercentile(25));
		qualities.put("Quartile2JointEntropy", JointEntropyStats.getPercentile(50));
		qualities.put("Quartile3JointEntropy", JointEntropyStats.getPercentile(75));
		qualities.put("MeanMutualInformation", MutualInformationStats.getMean());
		qualities.put("MinMutualInformation", MutualInformationStats.getMin());
		qualities.put("MaxMutualInformation", MutualInformationStats.getMax());
		qualities.put("Quartile1MutualInformation", MutualInformationStats.getPercentile(25));
		qualities.put("Quartile2MutualInformation", MutualInformationStats.getPercentile(50));
		qualities.put("Quartile3MutualInformation", MutualInformationStats.getPercentile(75));

		Double MeanMutualInformation = MutualInformationStats.getMean();
		Double MeanAttributeEntropy = EntropyStats.getMean();
		Double EquivalentNumberOfAtts = (MeanMutualInformation == 0 ? null : ClassEntropy / MeanMutualInformation);
		Double NoiseToSignalRatio = (MeanMutualInformation == 0 ? null : (MeanAttributeEntropy - MeanMutualInformation) / MeanMutualInformation);

		qualities.put("EquivalentNumberOfAtts", EquivalentNumberOfAtts);
		qualities.put("MeanNoiseToSignalRatio", NoiseToSignalRatio);

		// ensure finite double
		for (String key : qualities.keySet()) {
			if (qualities.get(key) != null && !Double.isFinite(qualities.get(key)))
				qualities.replace(key, null);
		}

		return qualities;

	}

}
