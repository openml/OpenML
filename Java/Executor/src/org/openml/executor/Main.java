package org.openml.executor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.Job;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.UploadRun;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.openml.executor.folds.Subsamples;

import com.thoughtworks.xstream.XStream;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Utils;

public class Main {

	private static final XStream xstream = XstreamXmlMapping.getInstance();
	private static OpenmlConnector connector;
	
	private final Subsamples subsamples;
	
	// TODO: make this dependent on the algorithm. 
	private final static String FLOW_IDENTIFIER = "RandomRules4.4 D1";
	private final static int FLOW_ID = 707; 
	
	public static void main(String[] args) throws Exception {
		final Config config = new Config();
		connector = new OpenmlConnector(config.getServer(), config.getUsername(), config.getPassword());
		int N = 1;
		
		String strTid = Utils.getOption('T', args);
		String strN = Utils.getOption('N', args);
		
		
		
		if( strTid != "" ) {
			int taskId = Integer.parseInt(strTid);
			try {
				new Main( taskId, "executables/", "rr" );
			} catch( Exception e ) {
				Conversion.log( "Error", "ExecuteTask", "Error executing Task " + taskId + ": " + e.getMessage() );
				e.printStackTrace();
			}
		} else {
			Conversion.log("OK", "Init", "No task_id provided (-T). Will attempt to download a scheduled classification job. ");

			if( strN != "" ) {
				N = Integer.parseInt(strN);
			}
			for( int i = 0; i < N; ++i ) {
				String workbench = ("Executor_" + FLOW_IDENTIFIER).replace( " ", "%20" );
				Job job = connector.openmlJobGet( workbench, 1 + "" );
				try {
					new Main( job.getTask_id(), "executables/", "rr" );
				} catch( Exception e ) {
					Conversion.log( "Error", "ExecuteTask", "Error executing Task " + job.getTask_id() + ": " + e.getMessage() );
					e.printStackTrace();
				}
			}
		}
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
		Run description = new Run(task_id, null, FLOW_ID, executable, null, null);
		
		Conversion.log( "OK", "UploadRun", "Will start to upload run" );
		UploadRun ur = connector.openmlRunUpload( Conversion.stringToTempFile( xstream.toXML(description), "description_executor", "xml"), outputFiles);
		Conversion.log( "OK", "Done", "Process finished, run id = " + ur.getRun_id() );
	}
	
	public Instances runAlgorithm( String executable, String pathTrainingSet, String pathTestSet ) throws IOException, InterruptedException {
		Process p = Runtime.getRuntime().exec( executable + " " + pathTrainingSet + " " + pathTestSet );
	    p.waitFor();
	    
	    // first read the input stream to a string. debugging purposes
	    StringWriter writer = new StringWriter();
	    IOUtils.copy(p.getInputStream(), writer, "UTF-8");
	    String output = writer.toString();
	    
	    // now write string to tmp file. 
	    File algorithmOutput = Conversion.stringToTempFile( output, "algorithm-output", "arff");
	    
	    // now load the instances object
	    Instances predictions = new Instances( new FileReader( algorithmOutput ) );
	    		
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
				Attribute confidenceAtt = new Attribute("confidence." + classValues[i]);
				predictions.insertAttributeAt(confidenceAtt, predictions.numAttributes() );
				for( int j = 0; j < predictions.size(); ++j ) {
					predictions.get(j).setValue(predictions.numAttributes() - 1, 0);
				}
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
