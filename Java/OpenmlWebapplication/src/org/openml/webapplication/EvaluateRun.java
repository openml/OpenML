package org.openml.webapplication;

import java.io.BufferedReader;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.Input;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.EvaluationScore;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.RunEvaluate;
import org.openml.apiconnector.xml.RunEvaluation;
import org.openml.apiconnector.xml.RunTrace;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.Task.Input.Data_set;
import org.openml.apiconnector.xml.Task.Input.Estimation_procedure;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.openml.webapplication.evaluate.EvaluateBatchPredictions;
import org.openml.webapplication.evaluate.EvaluateStreamPredictions;
import org.openml.webapplication.evaluate.EvaluateSubgroups;
import org.openml.webapplication.evaluate.EvaluateSurvivalAnalysisPredictions;
import org.openml.webapplication.evaluate.PredictionEvaluator;
import org.openml.webapplication.generatefolds.EstimationProcedure;

import weka.core.Instance;
import weka.core.Instances;

import com.thoughtworks.xstream.XStream;

public class EvaluateRun {
	private final XStream xstream;
	private final OpenmlConnector apiconnector;
	
	public EvaluateRun(OpenmlConnector ac) throws Exception {
		this(ac, null, false, null);
	}
	
	public EvaluateRun(OpenmlConnector ac, Integer run_id, boolean random, String ttids) throws Exception {
		apiconnector = ac;
		xstream = XstreamXmlMapping.getInstance();
		
		if( run_id != null ) {
			evaluate( run_id );
		} else {
			run_id = getRunId(random, ttids);
			while( run_id != null ) {
				Conversion.log("INFO","Evaluate Run","Downloading task " + run_id );
				evaluate( run_id );
				run_id = getRunId(random, ttids);
			}
			Conversion.log( "OK", "Process Run", "No more runs to perform. " );
		}
	}
	
	public Integer getRunId(boolean random, String ttids) throws Exception {
		String ttidsStr = ttids == null ? "" : " AND `t`.`ttid` IN ("+ttids+") ";
		String sql = 
			"SELECT `rid`,`start_time`,`processed`,`error` " + 
			"FROM `run` `r`, `task` `t` " +
			"WHERE `r`.`task_id` = `t`.`task_id` " + ttidsStr + 
			"AND `processed` IS NULL AND `error` IS NULL AND error_message IS NULL " + 
			"ORDER BY `start_time` ASC LIMIT 0, 100; "; 
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
	
	public void evaluate( int run_id) throws Exception {
		Conversion.log( "OK", "Process Run", "Start processing run: " + run_id );
		final Map<String,Integer> file_ids = new HashMap<String, Integer>();
		final Task task;
		final DataSetDescription dataset;
		
		PredictionEvaluator predictionEvaluator;
		RunEvaluation runevaluation = new RunEvaluation(run_id);
		RunTrace trace = null;
		
		JSONArray runJson = (JSONArray) apiconnector.freeQuery( "SELECT `task_id` FROM `run` WHERE `rid` = " + run_id ).get("data");
		JSONArray filesJson =  (JSONArray) apiconnector.freeQuery( "SELECT `field`,`file_id` FROM `runfile` WHERE `source` = " + run_id ).get("data");

		try {
			int task_id = ((JSONArray) runJson.get(0)).getInt(0);
			task = apiconnector.taskGet(task_id);
			Data_set source_data = TaskInformation.getSourceData(task);
			Estimation_procedure estimationprocedure = null;
			try { estimationprocedure = TaskInformation.getEstimationProcedure(task); } catch(Exception e) {}
			Integer dataset_id = source_data.getLabeled_data_set_id() != null ? source_data.getLabeled_data_set_id() : source_data.getData_set_id();
			
			for(int i = 0; i < filesJson.length(); ++i) {
				String field = ((JSONArray) filesJson.get(i)).getString(0);
				int file_index = ((JSONArray) filesJson.get(i)).getInt(1);
				
				file_ids.put(field, file_index);
			}
			
			if(file_ids.get("description") == null) {
				runevaluation.setError("Run description file not present. ");
				File evaluationFile = Conversion.stringToTempFile(xstream.toXML(runevaluation), "run_" + run_id + "evaluations", "xml");
				
				RunEvaluate re = apiconnector.runEvaluate(evaluationFile);
				Conversion.log("Error", "Process Run", "Run processed, but with error: " + re.getRun_id());
				return;
			}
			
			if(file_ids.get("predictions") == null && file_ids.get("subgroups") == null) { // TODO: this is currently true, but later on we might have tasks that do not require evaluations!
				runevaluation.setError("Required output files not present (e.g., arff predictions). ");
				File evaluationFile = Conversion.stringToTempFile(xstream.toXML(runevaluation), "run_" + run_id + "evaluations", "xml");
				
				RunEvaluate re = apiconnector.runEvaluate(evaluationFile);
				Conversion.log("Error", "Process Run", "Run processed, but with error: " + re.getRun_id());
				return;
			}
			
			if (file_ids.get("trace") != null) {
				trace = traceToXML(file_ids.get("trace"), task_id, run_id);
			}
			
			String description = OpenmlConnector.getStringFromUrl( apiconnector.getOpenmlFileUrl( file_ids.get( "description" ), "Run_" + run_id + "_description.xml").toString() );
			Run run_description = (Run) xstream.fromXML( description );
			dataset = apiconnector.dataGet(dataset_id);
			
			Conversion.log( "OK", "Process Run", "Start prediction evaluator. " );
			// TODO! no string comparisons, do something better
			String filename_prefix = "Run_" + run_id + "_";
			if( task.getTask_type().equals("Supervised Data Stream Classification") ) {
				predictionEvaluator = new EvaluateStreamPredictions(
					apiconnector.getOpenmlFileUrl(dataset.getFile_id(), dataset.getName()), 
					apiconnector.getOpenmlFileUrl( file_ids.get( "predictions" ), filename_prefix + "predictions.arff"), 
					source_data.getTarget_feature());
			} else if( task.getTask_type().equals("Survival Analysis") ) {
				predictionEvaluator = new EvaluateSurvivalAnalysisPredictions( 
						task, 
						apiconnector.getOpenmlFileUrl(dataset.getFile_id(), dataset.getName()), 
						new URL(estimationprocedure.getData_splits_url()), 
						apiconnector.getOpenmlFileUrl( file_ids.get( "predictions" ), filename_prefix + "predictions.arff"));
			} else if (task.getTask_type().equals("Subgroup Discovery")) {
				predictionEvaluator = new EvaluateSubgroups();
				
			} else {
				predictionEvaluator = new EvaluateBatchPredictions( 
					task,
					apiconnector.getOpenmlFileUrl(dataset.getFile_id(), dataset.getName()), 
					new URL(estimationprocedure.getData_splits_url()), 
					apiconnector.getOpenmlFileUrl( file_ids.get( "predictions" ), filename_prefix + "predictions.arff"), 
					estimationprocedure.getType().equals(EstimationProcedure.estimationProceduresTxt[6] ) );
			}
			runevaluation.addEvaluationMeasures( predictionEvaluator.getEvaluationScores() );
			
			if(run_description.getOutputEvaluation() != null) {
				Conversion.log( "OK", "Process Run", "Start consistency check with user defined measures. (x " + run_description.getOutputEvaluation().length + ")" );
				
				// TODO: This can be done so much faster ... 
				String warningMessage = "";
				boolean warningFound = false;
				
				for( EvaluationScore recorded : run_description.getOutputEvaluation() ) {
					boolean foundSame = false;
					
					// important check: because of legacy (implementation_id), the flow id might be missing
					if (recorded.getFlow() != null && recorded.getFunction() != null) { 
						for( EvaluationScore calculated : runevaluation.getEvaluation_scores() ) {
							if( recorded.isSame( calculated ) ) {
								foundSame = true;
								if( recorded.sameValue( calculated ) == false ) {
									String offByStr = "";
									try {
										double diff = Math.abs( Double.parseDouble( recorded.getValue() ) - Double.parseDouble( calculated.getValue() ) );
										offByStr = " (off by " + diff + ")";
									} catch( NumberFormatException nfe ) { }
									
									warningMessage += "Inconsistent Evaluation score: " + recorded + offByStr;
									warningFound = true;
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
				if( warningFound ) runevaluation.setWarning( warningMessage );
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
			//System.out.println(runEvaluation);
			File evaluationFile = Conversion.stringToTempFile( runEvaluation, "run_" + run_id + "evaluations", "xml" );
			RunEvaluate re = apiconnector.runEvaluate( evaluationFile );
			
			if (trace != null) {
				String runTrace = xstream.toXML( trace );
				//System.out.println(runTrace);
				File traceFile = Conversion.stringToTempFile( runTrace, "run_" + run_id + "trace", "xml" );
				
				apiconnector.runTraceUpload(traceFile);
			}
			
			Conversion.log( "OK", "Process Run", "Run processed: " + re.getRun_id() );
		} catch( Exception  e ) {
			Conversion.log( "ERROR", "Process Run", "An error occured during API call: " + e.getMessage() );
		}
	}
	
	private RunTrace traceToXML(int file_id, int task_id, int run_id) throws Exception {
		RunTrace trace = new RunTrace(run_id);
		URL traceURL = apiconnector.getOpenmlFileUrl(file_id, "Task_" + task_id + "_trace.arff");
		Instances traceDataset = new Instances(new BufferedReader(Input.getURL(traceURL)));
		List<Integer> parameterIndexes = new ArrayList<Integer>();
		
		if (traceDataset.attribute("repeat") == null || 
			traceDataset.attribute("fold") == null || 
			traceDataset.attribute("iteration") == null || 
			traceDataset.attribute("evaluation") == null ||
			traceDataset.attribute("selected") == null) {
			throw new Exception("trace file missing mandatory attributes. ");
		}
		
		for (int i = 0; i < traceDataset.numAttributes(); ++i) {
			if (traceDataset.attribute(i).name().startsWith("parameter_")) {
				parameterIndexes.add(i);
			}
		}
		if (parameterIndexes.size() == 0) {
			throw new Exception("trace file contains no fields with prefix 'parameter_' (i.e., parameters are not registered). ");
		}
		if (traceDataset.numAttributes() > 6 + parameterIndexes.size()) {
			throw new Exception("trace file contains illegal attributes (only allow for repeat, fold, iteration, evaluation, selected, setup_string and parameter_*). ");
		}
		
		for (int i = 0; i < traceDataset.numInstances(); ++i) {
			Instance current = traceDataset.get(i);
			Integer repeat = (int) current.value(traceDataset.attribute("repeat").index());
			Integer fold = (int) current.value(traceDataset.attribute("fold").index());
			Integer iteration = (int) current.value(traceDataset.attribute("iteration").index());
			Double evaluation = current.value(traceDataset.attribute("evaluation").index());
			Boolean selected = current.stringValue(traceDataset.attribute("selected").index()).equals("true");
			
			Map<String,String> parameters = new HashMap<String, String>();
			for (int j = 0; j < parameterIndexes.size(); ++j) {
				int attIdx = parameterIndexes.get(j);
				if (traceDataset.attribute(attIdx).isNumeric()) {
					parameters.put(traceDataset.attribute(attIdx).name(),current.value(attIdx) + "");
				} else {
					parameters.put(traceDataset.attribute(attIdx).name(),current.stringValue(attIdx));
				}
			}
			String setup_string = new JSONObject(parameters).toString();
			
			trace.addIteration(new RunTrace.Trace_iteration(repeat,fold,iteration,setup_string,evaluation,selected));
		}
		
		return trace;
	}
	
}