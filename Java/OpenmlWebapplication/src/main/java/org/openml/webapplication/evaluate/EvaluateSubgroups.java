package org.openml.webapplication.evaluate;

import org.openml.apiconnector.xml.EvaluationScore;
import org.openml.webapplication.predictionCounter.PredictionCounter;

public class EvaluateSubgroups implements PredictionEvaluator {
	
	private final EvaluationScore[] evaluationScores;
	
	public EvaluateSubgroups() {
		// this prediction evaluator works by returning no evaluations.
		// these should all have been included in run.xml already, 
		// and will be stored as user calculated results. 
		evaluationScores = new EvaluationScore[0];
	}
	
	@Override
	public EvaluationScore[] getEvaluationScores() {
		return evaluationScores;
	}

	@Override
	public PredictionCounter getPredictionCounter() {
		return null;
	}

}
