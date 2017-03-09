package org.openml.webapplication.fantail.dc.statistical;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.openml.webapplication.fantail.dc.Characterizer;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class AttributeEntropy extends Characterizer {

	protected static final String[] ids = new String[] { "ClassEntropy", "MeanAttributeEntropy", "MeanMutualInformation", "EquivalentNumberOfAtts",
			"NoiseToSignalRatio", "MinAttributeEntropy", "MinMutualInformation", "MaxAttributeEntropy", "MaxMutualInformation", "Quartile1AttributeEntropy",
			"Quartile1MutualInformation", "Quartile2AttributeEntropy", "Quartile2MutualInformation", "Quartile3AttributeEntropy",
			"Quartile3MutualInformation", };

	@Override
	public String[] getIDs() {
		return ids;
	}

	@Override
	public Map<String, Double> characterize(Instances data) {
		Map<String, Double> qualities = new HashMap<String, Double>();

		try {
			
			

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
	
	public static double computeClassEntropy(Instances data) {

		double[] classValueCounts = new double[data.numClasses()];
		for (int i = 0; i < data.numInstances(); i++) {
			Instance inst = data.instance(i);
			classValueCounts[(int) inst.classValue()]++;
		}
		double classEntropy = 0;
		for (int c = 0; c < data.numClasses(); c++) {
			if (classValueCounts[c] > 0) {
				double prob_c = classValueCounts[c] / data.numInstances();
				classEntropy += prob_c * (Utils.log2(prob_c));
			}
		}
		classEntropy = classEntropy * -1.0;

		return classEntropy;
	}

	public static double[] computeAttributeEntropy(Instances data) {
		List<Double> attributeEntropy = new ArrayList<Double>();
		for (int attIndex = 0; attIndex < data.numAttributes(); attIndex++) {

			if (data.attribute(attIndex).isNominal() && (data.classIndex() != attIndex)) {
				double[] attValueCounts = new double[data.numDistinctValues(attIndex)];

				for (int i = 0; i < data.numInstances(); i++) {
					Instance inst = data.instance(i);
					attValueCounts[(int) inst.value(attIndex)]++;
				}
				double attEntropy = 0;
				for (int c = 0; c < data.attribute(attIndex).numValues(); c++) {
					if (attValueCounts[c] > 0) {
						double prob_c = attValueCounts[c] / data.numInstances();
						attEntropy += prob_c * (Utils.log2(prob_c));
					}
				}
				attEntropy = attEntropy * -1.0;
				attributeEntropy.add(attEntropy);
			}
		}
		return ArrayUtils.toPrimitive(attributeEntropy.toArray(new Double[attributeEntropy.size()]));
	}

	public static double[] computeMutualInformation(Instances data) {
		List<Double> mutualInformation = new ArrayList<Double>();

		for (int attIndex = 0; attIndex < data.numAttributes(); attIndex++) {
			if (data.attribute(attIndex).isNominal() && (data.classIndex() != attIndex)) {
		//		System.out.println(data.attribute(attIndex));
				double infoGain = computeInfoGain(data, data.attribute(attIndex));
				infoGain = Math.round(infoGain * Math.pow(10, 14)) / Math.pow(10, 14);
				mutualInformation.add(infoGain);
			}
		}
		return ArrayUtils.toPrimitive(mutualInformation.toArray(new Double[mutualInformation.size()]));
	}

	private static Instances[] splitData(Instances data, Attribute att) {

		Instances[] splitData = new Instances[att.numValues()];
		for (int j = 0; j < att.numValues(); j++) {
			splitData[j] = new Instances(data, data.numInstances());
		}
		Enumeration<?> instEnum = data.enumerateInstances();
		while (instEnum.hasMoreElements()) {
			Instance inst = (Instance) instEnum.nextElement();
			splitData[(int) inst.value(att)].add(inst);
		}
		for (Instances splitData1 : splitData) {
			splitData1.compactify();
		}
		return splitData;
	}

	private static double computeInfoGain(Instances data, Attribute att) {
		double infoGain = computeEntropy(data);
		Instances[] splitData = splitData(data, att);
		for (int j = 0; j < att.numValues(); j++) {
			if (splitData[j].numInstances() > 0) {
				double entropyAfter = computeEntropy(splitData[j]);
				double percentage = (double) splitData[j].numInstances() / (double) data.numInstances();
				infoGain -=  percentage * entropyAfter;
			}
		}
		return infoGain;
	}

	private static double computeEntropy(Instances data) {

		double[] classCounts = new double[data.numClasses()];
		Enumeration<?> instEnum = data.enumerateInstances();
		while (instEnum.hasMoreElements()) {
			Instance inst = (Instance) instEnum.nextElement();
			classCounts[(int) inst.classValue()]++;
		}
		double entropy = 0;
		for (int j = 0; j < data.numClasses(); j++) {
			if (classCounts[j] > 0) {
				entropy -= classCounts[j] * Utils.log2(classCounts[j]);
			}
		}
		entropy /= (double) data.numInstances();
		return entropy + Utils.log2(data.numInstances());
	}
}
