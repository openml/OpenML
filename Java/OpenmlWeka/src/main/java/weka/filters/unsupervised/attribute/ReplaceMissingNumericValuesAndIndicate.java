package weka.filters.unsupervised.attribute;

import java.util.ArrayList;
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

	@Override
	protected Instances process(Instances instances) throws Exception {
		instances.insertAttributeAt(getIndicatorAttribute(), instances.numAttributes() - 1);
		
		Attribute indicator = instances.attribute(ATTNAME_INDICATOR);
		
		double[] medians = new double[instances.numAttributes()];
		
		// look for median
		for (int j = 0; j < instances.numAttributes(); ++j) {
			if (instances.attribute(j).isNumeric()) {
				double[] values = new double[instances.numInstances()];
				for (int i = 0; i < instances.numInstances(); ++i) {
					values[i] = instances.get(i).value(j);
				}
				Median median = new Median();
				medians[j] = median.evaluate(values);
				Conversion.log("OK", "Impute Numeric Value", "Attribute " + instances.attribute(j) + " (median): " + medians[j]);
			}
		}
		
		for (int i = 0; i < instances.numInstances(); ++i) {
			Instance current = instances.get(i);
			current.setValue(indicator, 0.0); // 0.0 means "false"
			for (int j = 0; j < instances.numAttributes(); ++j) {
				if (instances.attribute(j).isNumeric() == false) { continue; } 
				if (Utils.isMissingValue(current.value(j))) {
					current.setValue(j, medians[j]);
					current.setValue(indicator, 1.0);
				}
			}
		}
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
