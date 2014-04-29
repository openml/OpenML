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
import java.util.HashMap;
import java.util.Map;

import org.openml.apiconnector.algorithms.MathHelper;
import org.openml.apiconnector.models.Metric;
import org.openml.apiconnector.models.MetricScore;
import org.openml.apiconnector.xml.EvaluationScore;
import org.openml.webapplication.algorithm.InstancesHelper;
import org.openml.webapplication.io.Output;

import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class EvaluateStreamPredictions implements PredictionEvaluator {

	private final Instances  datasetStructure;
	private final Instances  predictionsStructure;
	private final ArffLoader datasetLoader;
	private final ArffLoader predictionsLoader;

	private final String[] classes;
	private final int nrOfClasses;
	private final int ATT_PREDICTION_ROWID;
	private final int[] ATT_PREDICTION_CONFIDENCE;
	
	private Map<Metric, MetricScore> globalMeasures;
	private final Map<Integer, Map<Metric, MetricScore>> intervalMeasures;
	private final int interval_size;
	
	public EvaluateStreamPredictions( String datasetUrl, String predictionsUrl, String classAttribute, int interval_size ) throws Exception {
		datasetLoader = new ArffLoader();
		datasetLoader.setURL(datasetUrl);
		datasetStructure = new Instances( datasetLoader.getStructure() );

		predictionsLoader = new ArffLoader();
		predictionsLoader.setURL(predictionsUrl);
		predictionsStructure = new Instances( predictionsLoader.getStructure() );
		
		intervalMeasures = new HashMap<Integer, Map<Metric,MetricScore>>();
		this.interval_size = interval_size;
		
		// Set class attribute to dataset ...
		if( datasetStructure.attribute( classAttribute ) != null ) {
			datasetStructure.setClass( datasetStructure.attribute( classAttribute ) );
		} else {
			throw new RuntimeException( "Class attribute ("+classAttribute+") not found" );
		}
		
		// register row indexes. 
		ATT_PREDICTION_ROWID = InstancesHelper.getRowIndex( "row_id", predictionsStructure );
		
		// do the same for the confidence fields. This number is dependent on the number 
		// of classes in the data set, hence the for-loop. 
		nrOfClasses = datasetStructure.classAttribute().numValues(); // returns 0 if numeric, that's good.
		classes = new String[nrOfClasses];
		ATT_PREDICTION_CONFIDENCE = new int[nrOfClasses];
		for( int i = 0; i < classes.length; i++ ) {
			classes[i] = datasetStructure.classAttribute().value( i );
			String attribute = "confidence." + classes[i];
			if( predictionsStructure.attribute(attribute) != null )
				ATT_PREDICTION_CONFIDENCE[i] = predictionsStructure.attribute( attribute ).index();
			else
				throw new Exception( "Attribute " + attribute + " not found among predictions. " );
		}
		
		doEvaluation( );
	}
	
	private void doEvaluation( ) throws Exception {
		// set global evaluation
		Evaluation globalEvaluator = new Evaluation( datasetStructure );
		Evaluation localEvaluator = new Evaluation( datasetStructure );
		
		// we go through all the instances in one loop. 
		Instance currentInstance;
		for( int iInstanceNr = 0; ( ( currentInstance = datasetLoader.getNextInstance( datasetStructure ) ) != null ); ++iInstanceNr ) {
			Instance currentPrediction = predictionsLoader.getNextInstance( predictionsStructure );
			if( currentPrediction == null ) throw new Exception( "Could not find prediction for instance #" + iInstanceNr );
			
			int rowid = (int) currentPrediction.value( ATT_PREDICTION_ROWID );
			
			if( rowid != iInstanceNr ) {
				throw new Exception(
					"Predictions need to be done in the same order as the dataset. "+
					"Could not find prediction for instance #" + iInstanceNr + 
					". Found prediction for instance #" + rowid + " instead." );
			}
			
			double[] confidences = InstancesHelper.predictionToConfidences( datasetStructure, currentPrediction, ATT_PREDICTION_CONFIDENCE ); // TODO: catch error when no prob distribution is provided
			// TODO: we might want to throw an error if the sum of confidences is not 1.0. Not now though. 
			
			globalEvaluator.evaluateModelOnce( confidences, currentInstance );
			localEvaluator.evaluateModelOnce( confidences, currentInstance );
			
			if( (iInstanceNr + 1) % interval_size == 0 ) {
				intervalMeasures.put( iInstanceNr + 1 - interval_size, Output.evaluatorToMap(globalEvaluator, nrOfClasses, TaskType.TESTTHENTRAIN) );
				localEvaluator = new Evaluation( datasetStructure );
			}
		}
		
		globalMeasures = Output.evaluatorToMap(globalEvaluator, nrOfClasses, TaskType.TESTTHENTRAIN);
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
							m.label, 
							score.getScore() == null ? null : dm.format( score.getScore() ), 
							null, 
							score.getArrayAsString( dm ) ) );
		}
		for( Integer i : intervalMeasures.keySet() ) {
			for( Metric m : intervalMeasures.get( i ).keySet() ) {
				MetricScore score = intervalMeasures.get( i ).get( m );
				DecimalFormat dm = MathHelper.defaultDecimalFormat;
				evaluationMeasures.add( 
						new EvaluationScore( 
								m.implementation, 
								m.name, 
								m.label, 
								score.getScore() == null ? null : dm.format( score.getScore() ), 
								score.getArrayAsString( dm ),
								i,
								i + interval_size, true ) );
			}
		}
		
		return evaluationMeasures.toArray( new EvaluationScore[evaluationMeasures.size()] );
	}
}
