package org.openml.weka.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import weka.classifiers.Classifier;
import weka.classifiers.meta.MultiSearch;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Utils;

public class OptimizationTrace {

	private static final String SETUP_STRING_ATT = "setup_string";
	private static final String PARAMETER_PREFIX = "parameter_";
	
	private static Instances getHeader(int taskId,List<Entry<String,String>> parameters) {
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
		for (Entry<String,String> parameter : parameters) {
			attInfo.add(new Attribute(PARAMETER_PREFIX + parameter.getKey(),stringValues));
		}
		
		Instances dataset = new Instances("openml_task_" + taskId + "_optimization_trace", attInfo, 0);
		return dataset;
	}
	
	public static Instances addTraceToDataset(Instances dataset, List<Quadlet<String,Double,List<Entry<String,String>>,Boolean>> trace, int taskId, int repeat, int fold) {
		if (dataset == null) {
			dataset = getHeader(taskId, trace.get(0).getParameters());
		}
		
		for (int i = 0; i < trace.size(); ++i) {
			double[] values = new double[dataset.numAttributes()];
			values[0] = repeat;
			values[1] = fold;
			values[2] = i;
			values[4] = trace.get(i).getEvaluation();
			values[5] = trace.get(i).isSelected() ? 1.0 : 0.0;
			
			DenseInstance instance = new DenseInstance(1.0, values);
			instance.setDataset(dataset);
			instance.setValue(3, trace.get(i).getClassifier());
			for (Entry<String,String> parameter : trace.get(i).getParameters()) {
				instance.setValue(dataset.attribute(PARAMETER_PREFIX + parameter.getKey()), parameter.getValue());
			}
			dataset.add(instance);
		}
		
		return dataset;
	}
	
	public static List<Quadlet<String,Double,List<Entry<String,String>>,Boolean>> extractTrace(Classifier classifier) throws Exception {
		try {
			if (!(classifier instanceof MultiSearch)) {
				throw new Exception("Classifier not instance of 'weka.classifiers.meta.MultiSearch'");
			}
			MultiSearch multiSearch = (MultiSearch) classifier;
			List<Quadlet<String,Double,List<Entry<String,String>>,Boolean>> result = new ArrayList<OptimizationTrace.Quadlet<String,Double,List<Entry<String,String>>,Boolean>>();
			
			String selectedSetupString = Utils.toCommandLine(multiSearch.getBestClassifier());
			for (int i = 0; i < multiSearch.getTraceSize(); ++i) {
				String classifName = multiSearch.getTraceClassifierAsCli(i);
				double classifEval = multiSearch.getTraceValue(i);
				List<Entry<String,String>> parameterSettings = multiSearch.getTraceParamaterSettings(i);
				result.add(new Quadlet<String, Double,List<Entry<String,String>>, Boolean>(classifName, classifEval, parameterSettings, classifName.equals(selectedSetupString)));
			}
			
			return result;
		} catch(NoClassDefFoundError e) {
			throw new Exception("Could not find MultiSearch package. Ignoring trace options. ");
		}
	}
	
	public static class Quadlet<T, U, V, W>
	{
	   private T a;
	   private U b;
	   private V c;
	   private W d;

	   Quadlet(T a, U b, V c, W d)
	   {
	    this.a = a;
	    this.b = b;
	    this.c = c;
	    this.d = d;
	   }

	   T getClassifier(){ return a;}
	   U getEvaluation(){ return b;}
	   V getParameters(){ return c;}
	   W isSelected(){ return d;}
	}
}
