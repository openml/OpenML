package org.openml.weka.algorithm;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.meta.MultiSearch;
import weka.core.Attribute;
import weka.core.DenseInstance;
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
	
	public static Instances addTraceToDataset(Instances dataset, List<Triplet<String,Double,Boolean>> trace, int taskId, int repeat, int fold) {
		if (dataset == null) {
			dataset = getHeader(taskId);
		}
		
		for (int i = 0; i < trace.size(); ++i) {
			double[] values = {repeat,fold,i,0.0,trace.get(i).getEvaluation(),trace.get(i).isSelected() ? 1.0 : 0.0};
			DenseInstance instance = new DenseInstance(1.0, values);
			instance.setDataset(dataset);
			instance.setValue(3, trace.get(i).getClassifier());
			dataset.add(instance);
		}
		
		return dataset;
	}
	
	public static List<Triplet<String,Double,Boolean>> extractTrace(Classifier classifier) throws Exception {
		if (!(classifier instanceof MultiSearch)) {
			throw new Exception("Classifier not instance of 'weka.classifiers.meta.MultiSearch'");
		}
		MultiSearch multiSearch = (MultiSearch) classifier;
		List<Triplet<String,Double,Boolean>> result = new ArrayList<OptimizationTrace.Triplet<String,Double,Boolean>>();
		
		String selectedSetupString = Utils.toCommandLine(multiSearch.getBestClassifier());
		for (int i = 0; i < multiSearch.getTraceSize(); ++i) {
			String classifName = multiSearch.getTraceClassifierAsCli(i);
			double classifEval = multiSearch.getTraceValue(i);
			
			result.add(new Triplet<String, Double, Boolean>(classifName, classifEval, classifName.equals(selectedSetupString)));
		}
		
		return result;
	}
	
	public static class Triplet<T, U, V>
	{
	   private T a;
	   private U b;
	   private V c;

	   Triplet(T a, U b, V c)
	   {
	    this.a = a;
	    this.b = b;
	    this.c = c;
	   }

	   T getClassifier(){ return a;}
	   U getEvaluation(){ return b;}
	   V isSelected(){ return c;}
	}
}
