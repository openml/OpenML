package org.openml.executor;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.UploadRun;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.openml.executor.folds.Subsamples;

import com.thoughtworks.xstream.XStream;

import weka.core.Attribute;
import weka.core.Instances;

public class Main {

	private static final OpenmlConnector connector = new OpenmlConnector("janvanrijn@gmail.com", "Feyenoord2008");
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	
	private final Subsamples subsamples;
	
	private final int implementation_id = 707; // TODO: do differently. 
	
	public static void main(String[] args) throws Exception {
		final String path = "executables/";
		final String executable = "rr";
		final int task_id = 1;
		
		new Main( task_id, path, executable );
	}
	
	public Main( int task_id, String path, String executable ) throws Exception {
		Conversion.log( "OK", "Start", "Going to execute " + executable + " on task " + task_id );
		subsamples = new Subsamples(connector, task_id);
		Instances predictionsAll = null;
		
		for( int repeat = 0; repeat < subsamples.REPEATS; ++repeat ) {
			for( int fold = 0; fold < subsamples.FOLDS; ++fold ) {
				Conversion.log( "OK", "Modelling", "Starting with repeat " + repeat + ", fold " + fold );
				Instances trainingSet = subsamples.getTrainingSet( repeat, fold );
				Instances testSet = subsamples.getTestSet( repeat, fold );
				File trainingSetFile = Conversion.stringToTempFile( trainingSet.toString(), "task_1_train_" + repeat + "_" + fold, "arff");
				File testSetFile = Conversion.stringToTempFile( testSet.toString(), "task_1_test_" + repeat + "_" + fold, "arff");
				
				Instances predictionsSample = runAlgorithm( path + executable, trainingSetFile.getAbsolutePath(), testSetFile.getAbsolutePath() );
				postProcess( predictionsSample, repeat, fold, 0 );
				predictionsAll = joinInstances( predictionsAll, predictionsSample );
			}
		}
		
		Map<String, File> outputFiles = new HashMap<String, File>();
		outputFiles.put("predictions", Conversion.stringToTempFile(predictionsAll.toString(), "predictions_executor", "arff") );
		Run description = new Run(task_id, null, implementation_id, executable, null, null);
		
		Conversion.log( "OK", "UploadRun", "Will start to upload run" );
		UploadRun ur = connector.openmlRunUpload( Conversion.stringToTempFile( xstream.toXML(description), "description_executor", "xml"), outputFiles);
		Conversion.log( "OK", "Done", "Process finished, run id = " + ur.getRun_id() );
	}
	
	public Instances runAlgorithm( String executable, String pathTrainingSet, String pathTestSet ) throws IOException, InterruptedException {
		Process p = Runtime.getRuntime().exec( executable + " " + pathTrainingSet + " " + pathTestSet );
	    p.waitFor();
	    Instances predictions = new Instances(new InputStreamReader(p.getInputStream()));
	    return predictions;
	}
	
	private void postProcess( Instances predictions, int repeat, int fold, int sample ) throws Exception {
		// clear attributes if they already exist
		String[] reserverdWords = {"repeat","fold","sample","row_id","rowid"};
		for( String word : reserverdWords ) {
			if( predictions.attribute( word ) != null ) {
				predictions.deleteAttributeAt( predictions.attribute( word ).index() );
			}
			
		}
		// now add our attributes
		predictions.insertAttributeAt(new Attribute("repeat"), 0 );
		predictions.insertAttributeAt(new Attribute("fold"), 1 );
		predictions.insertAttributeAt(new Attribute("sample"), 2 );
		predictions.insertAttributeAt(new Attribute("row_id"), 3 );
		
		// and missing confidence factors
		String[] classValues = TaskInformation.getClassNames(connector, subsamples.TASK);
		for( int i = 0; i < classValues.length; ++i ) {
			if( predictions.attribute("confidence." + classValues[i] ) == null ) {
				predictions.insertAttributeAt(new Attribute("confidence." + classValues[i]), predictions.numAttributes() );
			}
		}
		
		// get info about rowids
		ArrayList<Integer> rowids = subsamples.getTestSetRowIds(repeat, fold, sample);
		
		if( rowids.size() != predictions.size() ) {
			throw new RuntimeException("Prediction Count does not match! (" + repeat + ", " + fold + ", " + sample + ")" );
		}
		
		for( int i = 0; i < predictions.size(); ++i ) {
			predictions.get( i ).setValue( 0,  repeat);
			predictions.get( i ).setValue( 1,  fold);
			predictions.get( i ).setValue( 2,  sample);
			predictions.get( i ).setValue( 3,  rowids.get(i) );
		}
	}
	
	private Instances joinInstances( Instances a, Instances b ) {
		if( a == null ) { return b; }
		if( b == null ) { return a; }
		
		for( int i = 0; i < b.size(); ++i ) {
			a.add( b.get(i) );
		}
		
		return a;
	}
}
