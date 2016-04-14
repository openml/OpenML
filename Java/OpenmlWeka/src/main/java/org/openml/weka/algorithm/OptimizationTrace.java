package org.openml.weka.algorithm;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.meta.MultiSearch;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class OptimizationTrace {

	private static final String SETUP_STRING_ATT = "setup_string";
	
	private static Instances getHeader(int taskId) {
		ArrayList<Attribute> attInfo = new ArrayList<Attribute>();
		List<String> stringValues = null;
		List<String> trueFalse = new ArrayList<String>();
		trueFalse.add("false");
		trueFalse.add("true");
		
		attInfo.add(new Attribute("repeat"));
		attInfo.add(new Attribute("fold"));
		attInfo.add(new Attribute("iteration"));
		attInfo.add(new Attribute(SETUP_STRING_ATT,stringValues));
		attInfo.add(new Attribute("evaluation"));
		attInfo.add(new Attribute("selected",trueFalse));
		
		Instances dataset = new Instances("openml_task_" + taskId + "_optimization_trace", attInfo, 0);
		return dataset;
	}
	
	public static Instances extractTrace(Classifier classifier, int taskId, int repeat, int fold) throws Exception {
		if (!(classifier instanceof MultiSearch)) {
			throw new Exception("Classifier not instance of 'weka.classifiers.meta.MultiSearch'");
		}
		MultiSearch multiSearch = (MultiSearch) classifier;
		Instances dataset = getHeader(taskId);
		String setupString = Utils.toCommandLine(multiSearch.getBestClassifier());
		for (int i = 0; i < multiSearch.getTraceSize(); ++i) {
			String classifName = multiSearch.getTraceClassifierAsCli(i);
			double classifEval = multiSearch.getTraceValue(i);
			double[] attValues = {repeat, fold, i, 0.0, classifEval, classifName.equals(setupString) ? 1.0 : 0.0};
			Instance current = new DenseInstance(1, attValues);
			current.setValue(dataset.attribute(SETUP_STRING_ATT), classifName);
			dataset.add(current);
		}
		
		return dataset;
	}
}
