/*
 *  Webapplication - Java library that runs on OpenML servers
 *  Copyright (C) 2014 
 *  @author Jan N. van Rijn (j.n.van.rijn@liacs.leidenuniv.nl)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
package org.openml.webapplication.evaluate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.DateParser;
import org.openml.apiconnector.algorithms.MathHelper;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.models.Metric;
import org.openml.apiconnector.models.MetricScore;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.EvaluationScore;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.Run.Data.File;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.Task.Input.Data_set;
import org.openml.apiconnector.xml.Task.Input.Stream_schedule;
import org.openml.webapplication.algorithm.InstancesHelper;
import org.openml.webapplication.io.Output;
import org.openml.webapplication.predictionCounter.PredictionCounter;

import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class EvaluateStreamChallenge implements PredictionEvaluator {

	private final Instances  datasetStructure;
	private final ArffLoader datasetLoader;
	private final Map<Integer, File> predictionFiles;
	
	private ArffLoader predictionsLoader;
	private final String[] classes;
	private final int nrOfClasses;
	private final Stream_schedule schedule;
	private final long competitionStartTime;
	
	private int ATT_PREDICTION_ROWID;
	private int ATT_PREDICTION_PREDICTION;
	private int[] ATT_PREDICTION_CONFIDENCE;
	
	private Map<Metric, MetricScore> globalMeasures;
	
	public EvaluateStreamChallenge(OpenmlConnector connector, int run_id) throws Exception {
		Run run = connector.runGet(run_id);
		Task task = connector.taskGet(run.getTask_id());
		Data_set ds = TaskInformation.getSourceData(task);
		schedule = TaskInformation.getStreamSchedule(task);
		competitionStartTime = DateParser.unixTimestamp(schedule.getStart_time());
		DataSetDescription dsd = connector.dataGet(ds.getData_set_id());
		
		Conversion.log("OK", "EvaluateStreamChallenge", "dataset url: " + dsd.getUrl());
		datasetLoader = new ArffLoader();
		datasetLoader.setURL(dsd.getUrl().toString());
		datasetStructure = new Instances(datasetLoader.getStructure());

		predictionsLoader = new ArffLoader();
		// Set class attribute to dataset ...
		if( datasetStructure.attribute(ds.getTarget_feature()) != null ) {
			datasetStructure.setClass(datasetStructure.attribute(ds.getTarget_feature()));
		} else {
			throw new RuntimeException("Class attribute ("+ds.getTarget_feature()+") not found");
		}
		// do the same for the confidence fields. This number is dependent on the number 
		// of classes in the data set, hence the for-loop. 
		nrOfClasses = datasetStructure.classAttribute().numValues(); // returns 0 if numeric, that's good.
		classes = new String[nrOfClasses];
		
		predictionFiles = new TreeMap<Integer, File>();
		for (String field : run.getOutputFileAsMap().keySet()) {
			if (field.equals("predictions")) {
				predictionFiles.put(0, run.getOutputFileAsMap().get(field));
			} else if (field.startsWith("predictions_")) {
				int index = Integer.parseInt(field.substring("predictions_".length()));
				predictionFiles.put(index, run.getOutputFileAsMap().get(field));
			}
		}
		
		doEvaluation( );
	}
	
	private void doEvaluation( ) throws Exception {
		// set global evaluation
		Evaluation globalEvaluator = new Evaluation(datasetStructure);
		
		Set<Integer> latePredictions = new HashSet<Integer>();
		int prevRowId = -1;
		Instance currentInstance = null;
		int currentInstanceIdx = -1;
		for (Integer idx : predictionFiles.keySet()) {
			predictionsLoader = new ArffLoader();
			predictionsLoader.setURL(predictionFiles.get(idx).getUrl());
			Instances predictionsStructure = new Instances(predictionsLoader.getStructure());
			initializePredictionFile(predictionsStructure);
			
			Instance currentPrediction;
			long secondsAfterStart = (DateParser.unixTimestamp(predictionFiles.get(idx).getUploadTime()) - competitionStartTime) / 1000;
			
			for(; ((currentPrediction = predictionsLoader.getNextInstance(predictionsStructure)) != null); ) {
				// iterating over all predictions
				int row_id = (int) currentPrediction.value(ATT_PREDICTION_ROWID);
				if (row_id <= prevRowId) {
					throw new Exception("Predictions not in ascending order. Found " + row_id + " after " + prevRowId + " in file idx " + idx);
				}
				if (row_id < schedule.getInitial_batch_size()) {
					throw new Exception("Illigal prediction found, in range of initial batch size: " + row_id);
				}
				if (prevRowId == -1 && row_id != schedule.getInitial_batch_size()) {
					throw new Exception("Illigal starting point, not with initial batch size: Found " + row_id + " in file idx " + idx);
				}
				if (row_id != prevRowId + 1 && prevRowId > -1) {
					throw new Exception("Illigal prediction order, not following each other. Found " + row_id + " after " + prevRowId + " in file idx " + idx);
				}
				
				// now check time
				int batchNumber = (row_id - schedule.getInitial_batch_size()) / schedule.getBatch_size();
				long deadline = (batchNumber + 1) * schedule.getBatch_time();
				
				if (secondsAfterStart > deadline) {
					latePredictions.add(row_id);
				}
				
				// now obtain correct instance
				while (currentInstanceIdx < row_id) {
					currentInstance = datasetLoader.getNextInstance(datasetStructure);
					currentInstanceIdx += 1;
				}
				
				// compare with correct instance
				double[] confidences = InstancesHelper.predictionToConfidences( datasetStructure, currentPrediction, ATT_PREDICTION_CONFIDENCE, ATT_PREDICTION_PREDICTION ); 
				// TODO: we might want to throw an error if the sum of confidences is not 1.0. Not now though. 
				confidences = InstancesHelper.toProbDist( confidences ); // TODO: security, we might be more picky later on and requiring real prob distributions.
				try {
					globalEvaluator.evaluateModelOnceAndRecordPrediction(confidences, currentInstance);
				} catch( ArrayIndexOutOfBoundsException aiobe ) {
					throw new Exception("ArrayIndexOutOfBoundsException: This is an error that occurs when the classifier returns negative values. ");
				}
				
				prevRowId = row_id;
			}
		}
		Conversion.log("Warning", "EvaluateStreamChallenge", "Predictions that came late: " + latePredictions.size());
		
		globalMeasures = Output.evaluatorToMap(globalEvaluator, nrOfClasses, TaskType.TESTTHENTRAIN);
	}
	
	private void initializePredictionFile(Instances predictionsStructure) throws Exception {
		// register row indexes. 
		ATT_PREDICTION_ROWID = InstancesHelper.getRowIndex("row_id", predictionsStructure);
		ATT_PREDICTION_PREDICTION = InstancesHelper.getRowIndex("prediction", predictionsStructure);
		ATT_PREDICTION_CONFIDENCE = new int[nrOfClasses];
		
		for(int i = 0; i < classes.length; ++i) {
			classes[i] = datasetStructure.classAttribute().value(i);
			String attribute = "confidence." + classes[i];
			if(predictionsStructure.attribute(attribute) != null) {
				ATT_PREDICTION_CONFIDENCE[i] = predictionsStructure.attribute( attribute ).index();
			} else {
				System.out.println(predictionsStructure);
				throw new Exception("Attribute " + attribute + " not found among predictions. ");
			}
		}
	}
	
	public EvaluationScore[] getEvaluationScores() {
		ArrayList<EvaluationScore> evaluationMeasures = new ArrayList<EvaluationScore>();
		for( Metric m : globalMeasures.keySet() ) {
			MetricScore score = globalMeasures.get( m );
			DecimalFormat dm = MathHelper.defaultDecimalFormat;
			evaluationMeasures.add( 
				new EvaluationScore( 
					m.implementation, 
					m.name, 
					score.getScore() == null ? null : dm.format( score.getScore() ), 
					null, 
					score.getArrayAsString( dm ) ) );
		}
		
		return evaluationMeasures.toArray( new EvaluationScore[evaluationMeasures.size()] );
	}

	@Override
	public PredictionCounter getPredictionCounter() {
		return null; // TODO: might give an error, when used uncarefully.
	}
}
