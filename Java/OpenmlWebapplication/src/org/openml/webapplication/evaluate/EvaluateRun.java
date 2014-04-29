package org.openml.webapplication.evaluate;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.EvaluationScore;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.RunEvaluation;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.Task.Input.Data_set;
import org.openml.apiconnector.xml.Task.Input.Estimation_procedure;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

public class EvaluateRun {
	
	private final ApiConnector apiconnector;
	private final Map<String,Integer> file_ids;
	private final Task task;
	private final DataSetDescription dataset;
	private final XStream xstream;
	
	public static void main( String[] args ) {
		Config c = new Config("username = janvanrijn@gmail.com; password = Feyenoord2002; server = http://localhost/");
		try {
			new EvaluateRun(72, 1000, c);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public EvaluateRun( int run_id, int stream_interval_size, Config config ) throws Exception {
		apiconnector = new ApiConnector( config.getServer() );
		xstream = XstreamXmlMapping.getInstance();
		file_ids = new HashMap<String, Integer>();
		
		PredictionEvaluator predictionEvaluator;
		RunEvaluation runevaluation = new RunEvaluation( run_id );
		
		JSONArray runJson = (JSONArray) apiconnector.openmlFreeQuery( "SELECT `task_id` FROM `run` WHERE `rid` = " + run_id ).get("data");
		JSONArray filesJson =  (JSONArray) apiconnector.openmlFreeQuery( "SELECT `field`,`file_id` FROM `runfile` WHERE `source` = " + run_id ).get("data");
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
		
		// TODO: Download run XML, and check user defined measures, for consistency and additional information. 
		Run run_description = (Run) xstream.fromXML( apiconnector.getStringFromUrl( apiconnector.getOpenmlFileUrl( file_ids.get( "description" ) ).toString() ) );
		
		for( EvaluationScore e : run_description.getOutputEvaluation() ) {
			System.out.println( e.getFunction() + " - " + e.getImplementation() + " - " + e.getValue() );
		}
		
		try {
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
			
		} catch( Exception e ) {
			e.printStackTrace();
			runevaluation.setError( e.getMessage() );
		}
		
		// TODO: This can be done so much faster ... 
		String errorMessage = "";
		boolean errorFound = false;
		for( EvaluationScore recorded : run_description.getOutputEvaluation() ) {
			boolean foundSame = false;
			for( EvaluationScore calculated : runevaluation.getEvaluation_scores() ) {
				if( recorded.isSame( calculated ) ) {
					foundSame = true;
					if( recorded.sameValue( calculated ) == false ) {
						System.err.println("Inconsistent A: " + recorded + recorded.getValue() );
						System.err.println("Inconsistent B: " + calculated + calculated.getValue() + "\n===" );
						errorMessage += "Inconsistent Evaluation score: " + recorded;
						errorFound = true;
					} 
				}
			}
			if( foundSame == false ) {
				System.err.println("Added user defined Measure: " + recorded );
				runevaluation.addEvaluationMeasure( recorded );
			}
		}
		if( errorFound ) runevaluation.setError( errorMessage );
		 
		//System.out.println( XstreamXmlMapping.getInstance().toXML(runevaluation) );
	}
}
