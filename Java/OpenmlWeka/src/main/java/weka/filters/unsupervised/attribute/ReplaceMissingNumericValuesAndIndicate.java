package weka.filters.unsupervised.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.openml.apiconnector.algorithms.Conversion;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.SimpleBatchFilter;

public class ReplaceMissingNumericValuesAndIndicate extends SimpleBatchFilter implements OptionHandler {
	
	private static final long serialVersionUID = -6903848882440211704L;
	
	private static final String ATTNAME_INDICATOR = "replacedMissingNumericValues";
	
	private double[] medians;
	private int[] imputations;
	
	
	@Override
	public String globalInfo() {
		return "Replaces missing values and adds an indicator column to" + 
			   "indicate instances that had missing values.";
	}

	@Override
	protected Instances determineOutputFormat(Instances inputFormat)
			throws Exception {
		inputFormat.insertAttributeAt(getIndicatorAttribute(), inputFormat.numAttributes() - 1);
		return inputFormat;
	}
	
	protected void searchMedian(Instances instances) {
		medians = new double[instances.numAttributes()];
		imputations = new int[instances.numAttributes()];
		
		for (int j = 0; j < instances.numAttributes(); ++j) {
			int numPresentValues = 0;
			if (instances.attribute(j).isNumeric()) {
				double[] values = new double[instances.numInstances()];
				for (int i = 0; i < instances.numInstances(); ++i) {
					Instance current = instances.get(i);
					if (Utils.isMissingValue(current.value(j)) == false) {
						values[numPresentValues] = current.value(j);
						numPresentValues += 1;
					}
				}
				if (numPresentValues > 0) {
					double[] goodValues = Arrays.copyOf(values, numPresentValues);
					Median median = new Median();
					medians[j] = median.evaluate(goodValues);
				}
			}
		}
		
		for (int j = 0; j < instances.numAttributes(); ++j) {
			if (instances.attribute(j).isNumeric()) {
				Conversion.log("OK", "Impute Numeric", "Attribute " + instances.attribute(j) + " - Median: " + medians[j]);
			}
		}
	}
	
	protected void imputeMedian(Instances instances) {
		Attribute indicator = instances.attribute(ATTNAME_INDICATOR);
		
		for (int i = 0; i < instances.numInstances(); ++i) {
			Instance current = instances.get(i);
			current.setValue(indicator, 0.0); // 0.0 means "false"
			for (int j = 0; j < instances.numAttributes(); ++j) {
				if (instances.attribute(j).isNumeric() == false) { continue; } 
				if (Utils.isMissingValue(current.value(j))) {
					current.setValue(j, medians[j]);
					current.setValue(indicator, 1.0);
					imputations[j] += 1;
				}
			}
		}
	}

	@Override
	protected Instances process(Instances instances) throws Exception {
		instances.insertAttributeAt(getIndicatorAttribute(), instances.numAttributes() - 1);
		
		if (m_FirstBatchDone == false) {
			searchMedian(instances);
		}
		imputeMedian(instances);
		
		return instances;
	}
	
	private static Attribute getIndicatorAttribute() {
		List<String> values = new ArrayList<String>();
		values.add("false");
		values.add("true");
		Attribute indicator = new Attribute(ATTNAME_INDICATOR, values);
		return indicator;
	}
}
