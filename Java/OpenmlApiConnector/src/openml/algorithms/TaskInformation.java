package openml.algorithms;

import java.util.ArrayList;

import openml.xml.Task;
import openml.xml.Task.Input.Data_set;
import openml.xml.Task.Input.Estimation_procedure;
import openml.xml.Task.Output.Predictions;
import weka.core.Attribute;
import weka.core.Instances;

public class TaskInformation {

	public static int getNumberOfRepeats( Task t ) throws Exception {
		Estimation_procedure ep = getEstimationProcedure(t);
		for(int i = 0; i < ep.getParameters().length; ++i) {
			if(ep.getParameters()[i].getName().equals("number_repeats") ) {
				return Integer.parseInt(ep.getParameters()[i].getValue());
			}
		}
		throw new Exception("Tasks estimation procedure does not contain \"number_repeats\"");
	}

	public static int getNumberOfSamples( Task t ) throws Exception {
		Estimation_procedure ep = getEstimationProcedure(t);
		for(int i = 0; i < ep.getParameters().length; ++i) {
			if(ep.getParameters()[i].getName().equals("number_samples") ) {
				return Integer.parseInt(ep.getParameters()[i].getValue());
			}
		}
		throw new Exception("Tasks estimation procedure does not contain \"number_samples\"");
	}

	public static int getNumberOfFolds( Task t ) throws Exception {
		Estimation_procedure ep = getEstimationProcedure(t);
		for(int i = 0; i < ep.getParameters().length; ++i) {
			if(ep.getParameters()[i].getName().equals("number_folds") ) {
				return Integer.parseInt(ep.getParameters()[i].getValue());
			}
		}
		throw new Exception("Tasks estimation procedure does not contain \"number_folds\"");
	}
	
	public static int getNumberOfExpectedResults( Task t ) throws Exception {
		Instances splits = getEstimationProcedure(t).getData_splits();
		int count = 0;
		for( int i = 0; i < splits.numInstances(); ++i ) {
			if( InstancesHelper.nominalColumnEqualsValue( splits, i, "type", "TEST" ) ) {
				count++;
			}
		}
		return count;
	}
	
	public static Estimation_procedure getEstimationProcedure( Task t ) throws Exception {
		for( int i = 0; i < t.getInputs().length; ++i ) {
			if(t.getInputs()[i].getName().equals("estimation_procedure") ) {
				return t.getInputs()[i].getEstimation_procedure();
			}
		}
		throw new Exception("Task does not define an estimation procedure. ");
	}
	
	public static Data_set getSourceData( Task t ) throws Exception {
		for( int i = 0; i < t.getInputs().length; ++i ) {
			if(t.getInputs()[i].getName().equals("source_data") ) {
				return t.getInputs()[i].getData_set();
			}
		}
		throw new Exception("Task does not define an estimation procedure. ");
	}
	
	public static Predictions getPredictions( Task t ) throws Exception {
		for( int i = 0; i < t.getOutputs().length; ++i ) {
			if(t.getOutputs()[i].getName().equals("predictions") ) {
				return t.getOutputs()[i].getPredictions();
			}
		}
		throw new Exception("Task does not define an predictions. ");
	}
	
	public static String[] getClassNames( Task t ) throws Exception {
		ArrayList<String> res = new ArrayList<String>();
		Instances dataset = getSourceData(t).getDataSetDescription().getDataset();
		Attribute targetFeature = dataset.attribute(getSourceData(t).getTarget_feature());
		for( int i = 0; i < targetFeature.numValues(); ++i ) {
			res.add(targetFeature.value(i));
		}
		String[] resArray = new String[res.size()];
		for(int i = 0; i < res.size(); ++i) {
			resArray[i] = res.get(i);
		}
		return resArray;
	}
}
