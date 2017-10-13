package org.openml.webapplication.evaluate;

import org.openml.apiconnector.xml.EvaluationScore;
import org.openml.webapplication.predictionCounter.PredictionCounter;

public interface PredictionEvaluator {

	public EvaluationScore[] getEvaluationScores();
	public PredictionCounter getPredictionCounter();
}
