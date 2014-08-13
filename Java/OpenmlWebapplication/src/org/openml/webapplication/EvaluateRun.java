package org.openml.webapplication;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.io.ApiSessionHash;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.EvaluationScore;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.RunEvaluate;
import org.openml.apiconnector.xml.RunEvaluation;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.Task.Input.Data_set;
import org.openml.apiconnector.xml.Task.Input.Estimation_procedure;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.openml.webapplication.evaluate.EvaluateBatchPredictions;
import org.openml.webapplication.evaluate.EvaluateStreamPredictions;
import org.openml.webapplication.evaluate.PredictionEvaluator;

import com.thoughtworks.xstream.XStream;

public class EvaluateRun {
	
	private final ApiConnector apiconnector;
	private final XStream xstream;
	private final ApiSessionHash ash;
	private static final int INTERVAL_SIZE = 1000;
	
	public EvaluateRun( Config config ) throws Exception {
		this( config, null );
	}
	
	public EvaluateRun( Config config, Integer run_id ) throws Exception {
		apiconnector = new ApiConnector( config.getServer() );
		ash = new ApiSessionHash( apiconnector );
		ash.set( config.getUsername(), config.getPassword() );
		xstream = XstreamXmlMapping.getInstance();
		
		if( run_id != null ) {
			evaluate( run_id, INTERVAL_SIZE );
		} else {
			run_id = getRunId();
			while( run_id != null ) {
				Conversion.log("INFO","Evaluate Run","Downloading task " + run_id );
				evaluate( run_id, INTERVAL_SIZE );
				run_id = getRunId();
			}
			Conversion.log( "OK", "Process Run", "No more runs to perform. " );
		}
	}
	
	public Integer getRunId() throws JSONException, IOException {
		String sql = 
			"SELECT `rid`,`start_time`,`processed`,`error` " + 
			"FROM `run` WHERE `processed` IS NULL AND `error` IS NULL " + 
			"ORDER BY `start_time` ASC"; 
		
		JSONArray runJson = (JSONArray) apiconnector.openmlFreeQuery( sql ).get("data");
		
		if( runJson.length() > 0 ) {
			int run_id = ((JSONArray) runJson.get( 0 )).getInt( 0 );
			return run_id;
		} else {
			return null;
		}
	}
	
	public void evaluate( int run_id, int stream_interval_size ) throws Exception {
		Conversion.log( "OK", "Process Run", "Start processing run: " + run_id );
		final Map<String,Integer> file_ids = new HashMap<String, Integer>();
		final Task task;
		final DataSetDescription dataset;
		
		PredictionEvaluator predictionEvaluator;
		RunEvaluation runevaluation = new RunEvaluation( run_id );
		
		JSONArray runJson = (JSONArray) apiconnector.openmlFreeQuery( "SELECT `task_id` FROM `run` WHERE `rid` = " + run_id ).get("data");
		JSONArray filesJson =  (JSONArray) apiconnector.openmlFreeQuery( "SELECT `field`,`file_id` FROM `runfile` WHERE `source` = " + run_id ).get("data");

		try {
			int task_id = ((JSONArray) runJson.get( 0 )).getInt( 0 );
			task = apiconnector.openmlTaskSearch(task_id);
			Data_set source_data = TaskInformation.getSourceData(task);
			Estimation_procedure estimationprocedure = TaskInformation.getEstimationProcedure( task );
			dataset = apiconnector.openmlDataDescription( source_data.getData_set_id() );
			
			for( int i = 0; i < filesJson.length(); ++i ) {
				String field = ((JSONArray) filesJson.get( i )).getString( 0 );
				int file_index = ((JSONArray) filesJson.get( i )).getInt( 1 );
				
				file_ids.put(field, file_index);
			}
			
			if( file_ids.get( "description" ) == null ) {
				runevaluation.setError("Run description file not present. ");
				File evaluationFile = Conversion.stringToTempFile( xstream.toXML( runevaluation ), "run_" + run_id + "evaluations", "xml" );
				
				RunEvaluate re = apiconnector.openmlRunEvaluate( evaluationFile, ash.getSessionHash() );
				Conversion.log( "Error", "Process Run", "Run processed, but with error: " + re.getRun_id() );
				return;
			}
			
			Run run_description = (Run) xstream.fromXML( ApiConnector.getStringFromUrl( apiconnector.getOpenmlFileUrl( file_ids.get( "description" ) ).toString() ) );
			
			Conversion.log( "OK", "Process Run", "Start prediction evaluator. " );
			// TODO! no string comparisons, do something better
			if( task.getTask_type().equals("Supervised Data Stream Classification") ) {
				predictionEvaluator = new EvaluateStreamPredictions(
					dataset.getUrl(), 
					apiconnector.getOpenmlFileUrl( file_ids.get( "predictions" ) ).toString(), 
					source_data.getTarget_feature(),
					stream_interval_size );
			} else {
				predictionEvaluator = new EvaluateBatchPredictions( 
					dataset.getUrl(), 
					estimationprocedure.getData_splits_url(), 
					apiconnector.getOpenmlFileUrl( file_ids.get( "predictions" ) ).toString(), 
					source_data.getTarget_feature() );
			}
			runevaluation.addEvaluationMeasures( predictionEvaluator.getEvaluationScores() );
			Conversion.log( "OK", "Process Run", "Start consistency check with user defined measures. (x " + run_description.getOutputEvaluation().length + ")" );
			
			// TODO: This can be done so much faster ... 
			String errorMessage = "";
			boolean errorFound = false;
			for( EvaluationScore recorded : run_description.getOutputEvaluation() ) {
				boolean foundSame = false;
				for( EvaluationScore calculated : runevaluation.getEvaluation_scores() ) {
					if( recorded.isSame( calculated ) ) {
						foundSame = true;
						if( recorded.sameValue( calculated ) == false ) {
							String offByStr = "";
							try {
								double diff = Math.abs( Double.parseDouble( recorded.getValue() ) - Double.parseDouble( calculated.getValue() ) );
								offByStr = " (off by " + diff + ")";
							} catch( NumberFormatException nfe ) { }
							
							errorMessage += "Inconsistent Evaluation score: " + recorded + offByStr;
							errorFound = true;
						} 
					}
				}
				if( foundSame == false ) {
					runevaluation.addEvaluationMeasure( recorded );
				}
			}
			if( errorFound ) runevaluation.setError( errorMessage );
			
		} catch( Exception e ) {
			e.printStackTrace();
			Conversion.log( "Warning", "Process Run", "Unexpected error, will proceed with upload process: " + e.getMessage() );
			runevaluation.setError( e.getMessage() );
		}

		Conversion.log( "OK", "Process Run", "Start uploading results ... " );
		try {
			File evaluationFile = Conversion.stringToTempFile( xstream.toXML( runevaluation ), "run_" + run_id + "evaluations", "xml" );
			
			RunEvaluate re = apiconnector.openmlRunEvaluate( evaluationFile, ash.getSessionHash() );
			Conversion.log( "OK", "Process Run", "Run processed: " + re.getRun_id() );
		} catch( Exception  e ) {
			Conversion.log( "ERROR", "Process Run", "An error occured during API call: " + e.getMessage() );
		}
	}
}