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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openml.apiconnector.algorithms.Input;
import org.openml.apiconnector.algorithms.MathHelper;
import org.openml.apiconnector.models.Metric;
import org.openml.apiconnector.models.MetricScore;
import org.openml.apiconnector.xml.EvaluationScore;
import org.openml.webapplication.algorithm.InstancesHelper;
import org.openml.webapplication.io.Output;
import org.openml.webapplication.predictionCounter.FoldsPredictionCounter;
import org.openml.webapplication.predictionCounter.PredictionCounter;

import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;

public class EvaluateBatchPredictions implements PredictionEvaluator {
	
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
	private final TaskType task;
	private final Evaluation[][][][] sampleEvaluation;
	private final boolean bootstrap;
	
	private EvaluationScore[] evaluationScores;
	
	public EvaluateBatchPredictions( String datasetPath, String splitsPath, String predictionsPath, String classAttribute, boolean bootstrap ) throws Exception {
		// set all arff files needed for this operation. 
		dataset 	= new Instances( new BufferedReader( Input.getURL( datasetPath ) ) );
		predictions = new Instances( new BufferedReader( Input.getURL( predictionsPath ) ) ); 
		splits 		= new Instances( new BufferedReader( Input.getURL( splitsPath ) ) );
		this.bootstrap = bootstrap;
		
		// Set class attribute to dataset ...
		if( dataset.attribute( classAttribute ) != null ) {
			dataset.setClass( dataset.attribute( classAttribute ) );
		} else {
			throw new RuntimeException( "Class attribute ("+classAttribute+") not found" );
		}
		
		// ... and specify which task we are doing. classification or regression. 
		if( dataset.classAttribute().isNominal() ) {
			if( predictions.attribute("sample") == null ) {
				task = TaskType.CLASSIFICATION;
			} else {
				task = TaskType.LEARNINGCURVE;
			}
		} else {
			task = TaskType.REGRESSION;
		}
		
		// initiate a class that will help us with checking the prediction count. 
		predictionCounter = new FoldsPredictionCounter(splits);
		sampleEvaluation  = new Evaluation[predictionCounter.getRepeats()][predictionCounter.getFolds()][predictionCounter.getSamples()][bootstrap ? 2 : 1];
		
		// *** A sample is considered to be a subset of a fold. In a normal n-times n-fold crossvalidation
		//     setting, each fold consists of 1 sample. In a leaning curve example, each fold could consist
		//     of more samples. 
		
		// register row indexes. 
		ATT_PREDICTION_ROWID = InstancesHelper.getRowIndex( "row_id", predictions );
		ATT_PREDICTION_REPEAT = InstancesHelper.getRowIndex( new String[] {"repeat", "repeat_nr"}, predictions ) ;
		ATT_PREDICTION_FOLD =  InstancesHelper.getRowIndex( new String[] {"fold", "fold_nr"}, predictions ) ;
		ATT_PREDICTION_PREDICTION = InstancesHelper.getRowIndex( new String[] {"prediction"}, predictions ) ;
		if( task == TaskType.LEARNINGCURVE ) {
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
			if( predictions.attribute(attribute) != null ) {
				ATT_PREDICTION_CONFIDENCE[i] = predictions.attribute( attribute ).index();
			} else {
				throw new Exception( "Attribute " + attribute + " not found among predictions. " );
			}
		}
		
		// and do the actual evaluation. 
		doEvaluation();
	}
	
	private void doEvaluation() throws Exception {
		// set global evaluation
		Evaluation[] e = new Evaluation[bootstrap ? 2 : 1];
		for( int i = 0; i < e.length; ++i ) {
			e[i] = new Evaluation( dataset );
		}
		
		// set local evaluations
		for( int i = 0; i < sampleEvaluation.length; ++i ) {
			for( int j = 0; j < sampleEvaluation[i].length; ++j ) {
				for( int k = 0; k < sampleEvaluation[i][j].length; ++k ) {
					for( int m = 0; m < (bootstrap ? 2 : 1); ++m ) {
						sampleEvaluation[i][j][k][m] = new Evaluation(dataset);
					}
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
			
			int bootstrap = 0;
			if(task == TaskType.REGRESSION) {
				e[bootstrap].evaluateModelOnce(
					prediction.value( ATT_PREDICTION_PREDICTION ), 
					dataset.instance( rowid ) );
				sampleEvaluation[repeat][fold][sample][bootstrap].evaluateModelOnce(
					prediction.value( ATT_PREDICTION_PREDICTION ), 
					dataset.instance( rowid ) );
			} else {
				e[bootstrap].evaluateModelOnceAndRecordPrediction(
					InstancesHelper.predictionToConfidences( dataset, prediction, ATT_PREDICTION_CONFIDENCE ), // TODO: catch error when no prob distribution is provided
					dataset.instance( rowid ) );
				sampleEvaluation[repeat][fold][sample][bootstrap].evaluateModelOnceAndRecordPrediction(
					InstancesHelper.predictionToConfidences( dataset, prediction, ATT_PREDICTION_CONFIDENCE ) , // TODO: catch error when no prob distribution is provided
					dataset.instance( rowid ) );
			}
		}
		
		if( predictionCounter.check() == false ) {
			throw new RuntimeException( "Prediction count does not match: " + predictionCounter.getErrorMessage() );
		}
		
		List<EvaluationScore> evaluationMeasuresList = new ArrayList<EvaluationScore>();
		Map<Metric, MetricScore> globalMeasures = Output.evaluatorToMap( e, nrOfClasses, task, bootstrap );
		for( Metric m : globalMeasures.keySet() ) {
			MetricScore score = globalMeasures.get( m );
			DecimalFormat dm = MathHelper.defaultDecimalFormat;
			EvaluationScore em = new EvaluationScore( 
					m.implementation, 
					m.name, 
					score.getScore() == null ? null : dm.format( score.getScore() ), 
					null, 
					score.getArrayAsString( dm ) );
			evaluationMeasuresList.add( em );
		}
		for( int i = 0; i < sampleEvaluation.length; ++i ) {
			for( int j = 0; j < sampleEvaluation[i].length; ++j ) {
				for( int k = 0; k < sampleEvaluation[i][j].length; ++k ) {
					Map<Metric, MetricScore> currentMeasures = Output.evaluatorToMap( sampleEvaluation[i][j][k] , nrOfClasses, task, bootstrap);
					for( Metric m : currentMeasures.keySet() ) {
						MetricScore score = currentMeasures.get( m );
						DecimalFormat dm = MathHelper.defaultDecimalFormat;
						EvaluationScore currentMeasure;
						if( task == TaskType.LEARNINGCURVE ) {
							currentMeasure = new EvaluationScore( 
								m.implementation, 
								m.name, 
								score.getScore() == null ? null : dm.format( score.getScore() ), 
								score.getArrayAsString( dm ),
								i, j, k, predictionCounter.getShadowTypeSize(i, j, k) );
						} else {
							currentMeasure = new EvaluationScore( 
								m.implementation, 
								m.name, 
								score.getScore() == null ? null : dm.format( score.getScore() ), 
								score.getArrayAsString( dm ),
								i, j );
						}
						evaluationMeasuresList.add( currentMeasure );
					}
				}
			}
		}
		evaluationScores = evaluationMeasuresList.toArray(new EvaluationScore[evaluationMeasuresList.size()]);
	}
	
	public EvaluationScore[] getEvaluationScores() {
		return evaluationScores;
	}

	@Override
	public PredictionCounter getPredictionCounter() {
		return predictionCounter;
	}
	
}