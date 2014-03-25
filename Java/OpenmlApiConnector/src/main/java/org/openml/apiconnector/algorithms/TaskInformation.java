package org.openml.apiconnector.algorithms;

import java.io.BufferedReader;
import java.io.FileReader;

import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.Task.Input.Data_set;
import org.openml.apiconnector.xml.Task.Input.Estimation_procedure;
import org.openml.apiconnector.xml.Task.Output.Predictions;

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
	
	/*public static int getNumberOfExpectedResults( Task t ) throws Exception {
		Instances splits = getEstimationProcedure(t).getData_splits();
		int count = 0;
		for( int i = 0; i < splits.numInstances(); ++i ) {
			if( InstancesHelper.nominalColumnEqualsValue( splits, i, "type", "TEST" ) ) {
				count++;
			}
		}
		return count;
	}*/
	
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
		DataSetDescription dsd = getSourceData(t).getDataSetDescription();
		String targetFeature = getSourceData(t).getTarget_feature();
		BufferedReader br = new BufferedReader( new FileReader( ArffHelper.downloadAndCache("dataset", dsd.getCacheFileName(), dsd.getUrl(), dsd.getMd5_checksum() ) ) );
		
		String line;
		
		while( (line = br.readLine()) != null) {
			if( ArffHelper.isDataDeclaration(line) ) throw new Exception("Attribute not found.");
			if( ArffHelper.isAttributeDeclaration(line) ) {
				try {
					if( ArffHelper.getAttributeName( line ).equals( targetFeature ) ) {
						br.close();
						return ArffHelper.getNominalValues( line );
					}
				} catch( Exception e ) {/*Not going to happen*/}
			}
		}
		br.close();
		throw new Exception("Attribute not found.");
	}
}
