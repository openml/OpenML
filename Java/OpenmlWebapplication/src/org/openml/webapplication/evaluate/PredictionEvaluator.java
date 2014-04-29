package org.openml.webapplication.evaluate;

import org.openml.apiconnector.xml.EvaluationScore;

public interface PredictionEvaluator {

	public EvaluationScore[] getEvaluationScores();
}
