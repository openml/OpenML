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
import java.util.List;

import org.openml.apiconnector.algorithms.Input;
import org.openml.apiconnector.xml.EvaluationScore;
import org.openml.apiconnector.xml.Task;
import org.openml.webapplication.algorithm.InstancesHelper;
import org.openml.webapplication.predictionCounter.FoldsPredictionCounter;
import org.openml.webapplication.predictionCounter.PredictionCounter;

import weka.core.Instance;
import weka.core.Instances;

public class EvaluateSurvivalAnalysisPredictions implements PredictionEvaluator {
	
	private final int ATT_PREDICTION_ROWID;
	private final int ATT_PREDICTION_FOLD;
	private final int ATT_PREDICTION_REPEAT;
	
	private final Instances dataset;
	private final Instances splits;
	private final Instances predictions;
	
	private final PredictionCounter predictionCounter;
	
	private EvaluationScore[] evaluationScores;
	
	public EvaluateSurvivalAnalysisPredictions( Task task, String datasetPath, String splitsPath, String predictionsPath ) throws Exception {
		// set all arff files needed for this operation. 
		dataset 	= new Instances( new BufferedReader( Input.getURL( datasetPath ) ) );
		predictions = new Instances( new BufferedReader( Input.getURL( predictionsPath ) ) ); 
		splits 		= new Instances( new BufferedReader( Input.getURL( splitsPath ) ) );
		
		// initiate a class that will help us with checking the prediction count. 
		predictionCounter = new FoldsPredictionCounter(splits);
		
		// register row indexes. 
		ATT_PREDICTION_ROWID = InstancesHelper.getRowIndex( "row_id", predictions );
		ATT_PREDICTION_REPEAT = InstancesHelper.getRowIndex( new String[] {"repeat", "repeat_nr"}, predictions ) ;
		ATT_PREDICTION_FOLD =  InstancesHelper.getRowIndex( new String[] {"fold", "fold_nr"}, predictions ) ;
		
		// and do the actual evaluation. 
		doEvaluation();
	}
	
	private void doEvaluation() throws Exception {
		for( int i = 0; i < predictions.numInstances(); i++ ) {
			Instance prediction = predictions.instance( i );
			int repeat = ATT_PREDICTION_REPEAT < 0 ? 0 : (int) prediction.value( ATT_PREDICTION_REPEAT );
			int fold = ATT_PREDICTION_FOLD < 0 ? 0 : (int) prediction.value( ATT_PREDICTION_FOLD );
			int rowid = (int) prediction.value( ATT_PREDICTION_ROWID );
			
			predictionCounter.addPrediction(repeat, fold, 0, rowid);
			if( dataset.numInstances() <= rowid ) {
				throw new RuntimeException( "Making a prediction for row_id" + rowid + " (0-based) while dataset has only " + dataset.numInstances() + " instances. " );
			}
		}
		
		if( predictionCounter.check() == false ) {
			throw new RuntimeException( "Prediction count does not match: " + predictionCounter.getErrorMessage() );
		}
		
		List<EvaluationScore> evaluationMeasuresList = new ArrayList<EvaluationScore>();
		
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