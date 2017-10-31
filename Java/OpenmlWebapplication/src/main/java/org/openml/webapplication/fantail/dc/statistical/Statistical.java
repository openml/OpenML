package org.openml.webapplication.fantail.dc.statistical;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.openml.webapplication.fantail.dc.Characterizer;

import weka.core.Instances;

public class Statistical extends Characterizer {

	protected final String[] ids = new String[] { "MeanMeansOfNumericAtts", "MeanStdDevOfNumericAtts", "MeanKurtosisOfNumericAtts", "MeanSkewnessOfNumericAtts",
			"MinMeansOfNumericAtts", "MinStdDevOfNumericAtts", "MinKurtosisOfNumericAtts", "MinSkewnessOfNumericAtts", "MaxMeansOfNumericAtts",
			"MaxStdDevOfNumericAtts", "MaxKurtosisOfNumericAtts", "MaxSkewnessOfNumericAtts", "Quartile1MeansOfNumericAtts", "Quartile1StdDevOfNumericAtts",
			"Quartile1KurtosisOfNumericAtts", "Quartile1SkewnessOfNumericAtts", "Quartile2MeansOfNumericAtts", "Quartile2StdDevOfNumericAtts",
			"Quartile2KurtosisOfNumericAtts", "Quartile2SkewnessOfNumericAtts", "Quartile3MeansOfNumericAtts", "Quartile3StdDevOfNumericAtts",
			"Quartile3KurtosisOfNumericAtts", "Quartile3SkewnessOfNumericAtts" };

	@Override
	public String[] getIDs() {
		return ids;
	}

	/**
	 * @param weka.core.Instances
	 *            dataset : the dataset on wich to compute the meta-features
	 * @return Map<String, Double> qualities : map of meta-features (name->value), every quality from getIDs() is supposed to be present and value has to be
	 *         either a finite Double or null. null means that the meta-feature makes no sense on this attribute, or that it failed computation.
	 */
	public Map<String, Double> characterize(Instances dataset) {

		Map<String, Double> qualities = new HashMap<String, Double>();

		DescriptiveStatistics meanStats = new DescriptiveStatistics();
		DescriptiveStatistics stdDevStats = new DescriptiveStatistics();
		DescriptiveStatistics kurtosisStats = new DescriptiveStatistics();
		DescriptiveStatistics skewnessStats = new DescriptiveStatistics();

		for (int attribute = 0; attribute < dataset.numAttributes(); attribute++) {
			if (dataset.attribute(attribute).isNumeric()) {
				DescriptiveStatistics AttributeStats = new DescriptiveStatistics();
				for (int instance = 0; instance < dataset.numInstances(); instance++) {
					if (!dataset.get(instance).isMissing(attribute)) {
						double value = dataset.get(instance).value(attribute);
						AttributeStats.addValue(value);
					}
				}
				if (Double.isFinite(AttributeStats.getMean()))
					meanStats.addValue(AttributeStats.getMean());
				if (Double.isFinite(AttributeStats.getStandardDeviation()))
					stdDevStats.addValue(AttributeStats.getStandardDeviation());
				if (Double.isFinite(AttributeStats.getKurtosis()))
					kurtosisStats.addValue(AttributeStats.getKurtosis());
				if (Double.isFinite(AttributeStats.getSkewness()))
					skewnessStats.addValue(AttributeStats.getSkewness());
			}
		}

		qualities.put("MeanMeansOfNumericAtts", meanStats.getMean());
		qualities.put("MinMeansOfNumericAtts", meanStats.getMin());
		qualities.put("MaxMeansOfNumericAtts", meanStats.getMax());
		qualities.put("Quartile1MeansOfNumericAtts", meanStats.getPercentile(25));
		qualities.put("Quartile2MeansOfNumericAtts", meanStats.getPercentile(50));
		qualities.put("Quartile3MeansOfNumericAtts", meanStats.getPercentile(75));

		qualities.put("MeanStdDevOfNumericAtts", stdDevStats.getMean());
		qualities.put("MinStdDevOfNumericAtts", stdDevStats.getMin());
		qualities.put("MaxStdDevOfNumericAtts", stdDevStats.getMax());
		qualities.put("Quartile1StdDevOfNumericAtts", stdDevStats.getPercentile(25));
		qualities.put("Quartile2StdDevOfNumericAtts", stdDevStats.getPercentile(50));
		qualities.put("Quartile3StdDevOfNumericAtts", stdDevStats.getPercentile(75));

		qualities.put("MeanKurtosisOfNumericAtts", kurtosisStats.getMean());
		qualities.put("MinKurtosisOfNumericAtts", kurtosisStats.getMin());
		qualities.put("MaxKurtosisOfNumericAtts", kurtosisStats.getMax());
		qualities.put("Quartile1KurtosisOfNumericAtts", kurtosisStats.getPercentile(25));
		qualities.put("Quartile2KurtosisOfNumericAtts", kurtosisStats.getPercentile(50));
		qualities.put("Quartile3KurtosisOfNumericAtts", kurtosisStats.getPercentile(75));

		qualities.put("MeanSkewnessOfNumericAtts", skewnessStats.getMean());
		qualities.put("MinSkewnessOfNumericAtts", skewnessStats.getMin());
		qualities.put("MaxSkewnessOfNumericAtts", skewnessStats.getMax());
		qualities.put("Quartile1SkewnessOfNumericAtts", skewnessStats.getPercentile(25));
		qualities.put("Quartile2SkewnessOfNumericAtts", skewnessStats.getPercentile(50));
		qualities.put("Quartile3SkewnessOfNumericAtts", skewnessStats.getPercentile(75));

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
