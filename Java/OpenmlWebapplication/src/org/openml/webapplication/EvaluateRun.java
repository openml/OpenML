package org.openml.webapplication;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.OpenmlConnector;
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
import org.openml.webapplication.evaluate.EvaluateSurvivalAnalysisPredictions;
import org.openml.webapplication.evaluate.PredictionEvaluator;
import org.openml.webapplication.generatefolds.EstimationProcedure;

import com.thoughtworks.xstream.XStream;

public class EvaluateRun {
	private final XStream xstream;
	private final OpenmlConnector apiconnector;
	private static final int INTERVAL_SIZE = 1000;
	
	public EvaluateRun( OpenmlConnector ac ) throws Exception {
		this( ac, null, false );
	}
	
	public EvaluateRun( OpenmlConnector ac, Integer run_id, boolean random ) throws Exception {
		apiconnector = ac;
		xstream = XstreamXmlMapping.getInstance();
		
		if( run_id != null ) {
			evaluate( run_id, INTERVAL_SIZE );
		} else {
			run_id = getRunId(random);
			while( run_id != null ) {
				Conversion.log("INFO","Evaluate Run","Downloading task " + run_id );
				evaluate( run_id, INTERVAL_SIZE );
				run_id = getRunId(random);
			}
			Conversion.log( "OK", "Process Run", "No more runs to perform. " );
		}
	}
	
	public Integer getRunId(boolean random) throws Exception {
		String sql = 
			"SELECT `rid`,`start_time`,`processed`,`error` " + 
			"FROM `run` WHERE `processed` IS NULL AND `error` IS NULL " + 
			"ORDER BY `start_time` ASC"; 
		
		JSONArray runJson = (JSONArray) apiconnector.freeQuery( sql ).get("data");
		
		int randomint = 0;
		
		if (random) {
			Random r = new Random();
			randomint = Math.abs(r.nextInt());
		}
		if( runJson.length() > 0 ) {
			int run_id = ((JSONArray) runJson.get( randomint % runJson.length() )).getInt( 0 );
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
		
		JSONArray runJson = (JSONArray) apiconnector.freeQuery( "SELECT `task_id` FROM `run` WHERE `rid` = " + run_id ).get("data");
		JSONArray filesJson =  (JSONArray) apiconnector.freeQuery( "SELECT `field`,`file_id` FROM `runfile` WHERE `source` = " + run_id ).get("data");

		try {
			int task_id = ((JSONArray) runJson.get( 0 )).getInt( 0 );
			task = apiconnector.taskGet(task_id);
			Data_set source_data = TaskInformation.getSourceData(task);
			Estimation_procedure estimationprocedure = TaskInformation.getEstimationProcedure( task );
			Integer dataset_id = source_data.getLabeled_data_set_id() != null ? source_data.getLabeled_data_set_id() : source_data.getData_set_id();
			dataset = apiconnector.dataGet( dataset_id );
			
			for( int i = 0; i < filesJson.length(); ++i ) {
				String field = ((JSONArray) filesJson.get( i )).getString( 0 );
				int file_index = ((JSONArray) filesJson.get( i )).getInt( 1 );
				
				file_ids.put(field, file_index);
			}
			
			if( file_ids.get( "description" ) == null ) {
				runevaluation.setError("Run description file not present. ");
				File evaluationFile = Conversion.stringToTempFile( xstream.toXML( runevaluation ), "run_" + run_id + "evaluations", "xml" );
				
				RunEvaluate re = apiconnector.runEvaluate( evaluationFile );
				Conversion.log( "Error", "Process Run", "Run processed, but with error: " + re.getRun_id() );
				return;
			}
			
			String description = OpenmlConnector.getStringFromUrl( apiconnector.getOpenmlFileUrl( file_ids.get( "description" ), "Run_" + run_id + "_description.xml").toString() );
			Run run_description = (Run) xstream.fromXML( description );
			
			Conversion.log( "OK", "Process Run", "Start prediction evaluator. " );
			// TODO! no string comparisons, do something better
			String filename = "Task_" + task_id + "_predictions.arff";
			if( task.getTask_type().equals("Supervised Data Stream Classification") ) {
				predictionEvaluator = new EvaluateStreamPredictions(
					apiconnector.getOpenmlFileUrl(dataset.getFile_id(), dataset.getName()), 
					apiconnector.getOpenmlFileUrl( file_ids.get( "predictions" ), filename), 
					source_data.getTarget_feature(),
					stream_interval_size );
			} else if( task.getTask_type().equals("Survival Analysis") ) {
				predictionEvaluator = new EvaluateSurvivalAnalysisPredictions( 
						task, 
						apiconnector.getOpenmlFileUrl(dataset.getFile_id(), dataset.getName()), 
						new URL(estimationprocedure.getData_splits_url()), 
						apiconnector.getOpenmlFileUrl( file_ids.get( "predictions" ), filename));
			} else {
				predictionEvaluator = new EvaluateBatchPredictions( 
					task,
					apiconnector.getOpenmlFileUrl(dataset.getFile_id(), dataset.getName()), 
					new URL(estimationprocedure.getData_splits_url()), 
					apiconnector.getOpenmlFileUrl( file_ids.get( "predictions" ), filename), 
					estimationprocedure.getType().equals(EstimationProcedure.estimationProceduresTxt[5] ) );
			}
			runevaluation.addEvaluationMeasures( predictionEvaluator.getEvaluationScores() );
			
			if(run_description.getOutputEvaluation() != null) {
				Conversion.log( "OK", "Process Run", "Start consistency check with user defined measures. (x " + run_description.getOutputEvaluation().length + ")" );
				
				// TODO: This can be done so much faster ... 
				String errorMessage = "";
				boolean errorFound = false;
				
				for( EvaluationScore recorded : run_description.getOutputEvaluation() ) {
					boolean foundSame = false;
					
					// important check: because of legacy (implementation_id), the flow id might be missing
					if (recorded.getFlow() != null && recorded.getFunction() != null) { 
						System.out.println(recorded);
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
							// give the record the correct sample size
							if( recorded.getSample() != null && recorded.getSample_size() == null ) {
								recorded.setSample_size( predictionEvaluator.getPredictionCounter().getShadowTypeSize(
										recorded.getRepeat(), recorded.getFold(), recorded.getSample()));
							}
							runevaluation.addEvaluationMeasure( recorded );
						}
					}
				}
				if( errorFound ) runevaluation.setError( errorMessage );
			} else {
				Conversion.log( "OK", "Process Run", "No local evaluation measures to compare to. " );
			}
		} catch( Exception e ) {
			e.printStackTrace();
			Conversion.log( "Warning", "Process Run", "Unexpected error, will proceed with upload process: " + e.getMessage() );
			runevaluation.setError( e.getMessage() );
		}

		Conversion.log( "OK", "Process Run", "Start uploading results ... " );
		try {
			String runEvaluation = xstream.toXML( runevaluation );
			File evaluationFile = Conversion.stringToTempFile( runEvaluation, "run_" + run_id + "evaluations", "xml" );
			
			RunEvaluate re = apiconnector.runEvaluate( evaluationFile );
			Conversion.log( "OK", "Process Run", "Run processed: " + re.getRun_id() );
		} catch( Exception  e ) {
			Conversion.log( "ERROR", "Process Run", "An error occured during API call: " + e.getMessage() );
		}
	}
}