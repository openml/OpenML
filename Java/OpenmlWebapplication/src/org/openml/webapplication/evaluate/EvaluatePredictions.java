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

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Map;

import org.openml.apiconnector.models.Metric;
import org.openml.apiconnector.models.MetricCollector;
import org.openml.apiconnector.models.MetricScore;
import org.apache.commons.lang3.StringUtils;
import org.openml.webapplication.algorithm.InstancesHelper;
import org.openml.webapplication.io.Input;
import org.openml.webapplication.io.Output;
import org.openml.webapplication.models.JsonItem;
import org.openml.webapplication.predictionCounter.FoldsPredictionCounter;
import org.openml.webapplication.predictionCounter.PredictionCounter;

import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;

public class EvaluatePredictions {
	
	private final int nrOfClasses;
	
	private final int ATT_PREDICTION_ROWID;
	private final int ATT_PREDICTION_FOLD;
	private final int ATT_PREDICTION_REPEAT;
	private final int ATT_PREDICTION_PREDICTION;
	private final int ATT_PREDICTION_SAMPLE;
	private final int[] ATT_PREDICTION_CONFIDENCE;
	
	private final Instances dataset;
	private final Instances splits;
	private final Instances predictions;
	
	private final PredictionCounter predictionCounter;
	private final String[] classes;
	private final Task task;
	private final Evaluation[][][] sampleEvaluation;
	
	public EvaluatePredictions( String datasetPath, String splitsPath, String predictionsPath, String classAttribute ) throws Exception {
		// set all arff files needed for this operation. 
		dataset 	= new Instances( new BufferedReader( Input.getURL( datasetPath ) ) );
		predictions = new Instances( new BufferedReader( Input.getURL( predictionsPath ) ) ); 
		splits 		= new Instances( new BufferedReader( Input.getURL( splitsPath ) ) );
		
		// Set class attribute to dataset ...
		if( dataset.attribute( classAttribute ) != null ) {
			dataset.setClass( dataset.attribute( classAttribute ) );
		} else {
			throw new RuntimeException( "Class attribute ("+classAttribute+") not found" );
		}
		
		// ... and specify which task we are doing. classification or regression. 
		if( dataset.classAttribute().isNominal() ) {
			if( predictions.attribute("sample") == null ) {
				task = Task.CLASSIFICATION;
			} else {
				task = Task.LEARNINGCURVE;
			}
		} else {
			task = Task.REGRESSION;
		}
		
		// initiate a class that will help us with checking the prediction count. 
		predictionCounter = new FoldsPredictionCounter(splits);
		sampleEvaluation  = new Evaluation[predictionCounter.getRepeats()][predictionCounter.getFolds()][predictionCounter.getSamples()];
		
		// *** A sample is considered to be a subset of a fold. In a normal n-times n-fold crossvalidation
		//     setting, each fold consists of 1 sample. In a leaning curve example, each fold could consist
		//     of more samples. 
		
		// register row indexes. 
		ATT_PREDICTION_ROWID = InstancesHelper.getRowIndex( "row_id", predictions );
		ATT_PREDICTION_REPEAT = InstancesHelper.getRowIndex( new String[] {"repeat", "repeat_nr"}, predictions ) ;
		ATT_PREDICTION_FOLD =  InstancesHelper.getRowIndex( new String[] {"fold", "fold_nr"}, predictions ) ;
		ATT_PREDICTION_PREDICTION = InstancesHelper.getRowIndex( new String[] {"prediction"}, predictions ) ;
		if( task == Task.LEARNINGCURVE ) {
			ATT_PREDICTION_SAMPLE =  InstancesHelper.getRowIndex( new String[] {"sample", "sample_nr"}, predictions ) ;
		} else {
			ATT_PREDICTION_SAMPLE = -1;
		}
		// do the same for the confidence fields. This number is dependent on the number 
		// of classes in the data set, hence the for-loop. 
		nrOfClasses = dataset.classAttribute().numValues(); // returns 0 if numeric, that's good.
		classes = new String[nrOfClasses];
		ATT_PREDICTION_CONFIDENCE = new int[nrOfClasses];
		for( int i = 0; i < classes.length; i++ ) {
			classes[i] = dataset.classAttribute().value( i );
			String attribute = "confidence." + classes[i];
			if( predictions.attribute(attribute) != null )
				ATT_PREDICTION_CONFIDENCE[i] = predictions.attribute( attribute ).index();
			else
				throw new Exception( "Attribute " + attribute + " not found among predictions. " );
		}
		
		// and do the actual evaluation. 
		doEvaluation();
	}
	
	private void doEvaluation() throws Exception {
		// set global evaluation
		Evaluation e = new Evaluation( dataset );
		
		
		// set local evaluations
		for( int i = 0; i < sampleEvaluation.length; ++i ) {
			for( int j = 0; j < sampleEvaluation[i].length; ++j ) {
				for( int k = 0; k < sampleEvaluation[i][j].length; ++k ) {
					sampleEvaluation[i][j][k] = new Evaluation(dataset);
				}
			}
		}
		
		for( int i = 0; i < predictions.numInstances(); i++ ) {
			Instance prediction = predictions.instance( i );
			int repeat = ATT_PREDICTION_REPEAT < 0 ? 0 : (int) prediction.value( ATT_PREDICTION_REPEAT );
			int fold = ATT_PREDICTION_FOLD < 0 ? 0 : (int) prediction.value( ATT_PREDICTION_FOLD );
			int sample = ATT_PREDICTION_SAMPLE < 0 ? 0 : (int) prediction.value( ATT_PREDICTION_SAMPLE );
			int rowid = (int) prediction.value( ATT_PREDICTION_ROWID );
			
			predictionCounter.addPrediction(repeat, fold, sample, rowid);
			if( dataset.numInstances() <= rowid ) {
				throw new RuntimeException( "Making a prediction for row_id" + rowid + " (0-based) while dataset has only " + dataset.numInstances() + " instances. " );
			}
			
			if(task == Task.REGRESSION) {
				e.evaluateModelOnce(
					prediction.value( ATT_PREDICTION_PREDICTION ), 
					dataset.instance( rowid ) );
				sampleEvaluation[repeat][fold][sample].evaluateModelOnce(
					prediction.value( ATT_PREDICTION_PREDICTION ), 
					dataset.instance( rowid ) );
			} else {
				e.evaluateModelOnce(
					InstancesHelper.predictionToConfidences( dataset, prediction, ATT_PREDICTION_CONFIDENCE ), // TODO: catch error when no prob distribution is provided
					dataset.instance( rowid ) );
				sampleEvaluation[repeat][fold][sample].evaluateModelOnce(
						InstancesHelper.predictionToConfidences( dataset, prediction, ATT_PREDICTION_CONFIDENCE ) , // TODO: catch error when no prob distribution is provided
					dataset.instance( rowid ) );
			}
		}
		
		if( predictionCounter.check() ) {
			output( e, task );
		} else {
			throw new RuntimeException( "Prediction count does not match: " + predictionCounter.getErrorMessage() );
		}
	}
	
	protected void output( Evaluation e, Task task ) throws Exception {
		if( task == Task.CLASSIFICATION || task == Task.REGRESSION || task == Task.LEARNINGCURVE || task == Task.TESTTHENTRAIN ) { // any task ...
			Map<Metric, MetricScore> metrics = Output.evaluatorToMap(e, nrOfClasses, task);
			MetricCollector population = new MetricCollector();
			
			String globalMetrics = "";
			String foldMetricsLabel = "fold_metrices";
			if( task == Task.LEARNINGCURVE ) {
				foldMetricsLabel = "sample_metrices";
			}
			String foldMetrics = "";
			
			String[] metricsPerRepeat = new String[predictionCounter.getRepeats()];
			for(int i = 0; i < metricsPerRepeat.length; ++i ) {
				String[] metricsPerFold = new String[predictionCounter.getFolds()];
				for( int j = 0; j < metricsPerFold.length; j++ ) {
					String[] metricsPerSample = new String[predictionCounter.getSamples()];
					for( int k = 0; k < metricsPerSample.length; ++k ) {
						Map<Metric, MetricScore> localMetrics = Output.evaluatorToMap(sampleEvaluation[i][j][k], nrOfClasses, task);
						population.add(localMetrics);
						
						ArrayList<JsonItem> additionalItems = new ArrayList<JsonItem>();
						additionalItems.add( new JsonItem("sample_size", predictionCounter.getShadowTypeSize(i, j, k) * 1.0 ) );
						
						metricsPerSample[k] = Output.printMetrics(localMetrics, task == Task.LEARNINGCURVE ? additionalItems : null );
					}
					
					if( task == Task.LEARNINGCURVE ) {
						metricsPerFold[j] = "[" + StringUtils.join( metricsPerSample, "],\n[") + "]";
					} else { 
						// In any other case, we do not consider samples per fold. 
						// Hence we will not add an additional dimension to the array.
						metricsPerFold[j] = metricsPerSample[0];
					}
				}
				metricsPerRepeat[i] = "[" + StringUtils.join( metricsPerFold, "],\n[") + "]";
			}
			
			foldMetrics = "[" + StringUtils.join( metricsPerRepeat, "],\n\n[") + "]";
			globalMetrics = Output.printMetrics(metrics, population, null);
			
			System.out.println(
				"{\n" + 
					"\"global_metrices\":[\n" +
						globalMetrics +
					"],\n" +
					"\""+foldMetricsLabel+"\":[\n" +
						foldMetrics + 
					"]" +
				"}"
			);
		} else {
			throw new RuntimeException( "Task not defined" );
		}
	}
	
	
}