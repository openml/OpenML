package org.openml.weka.experiment;

import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Prediction;
import weka.experiment.RegressionSplitEvaluator;

public class OpenmlRegressionSplitEvaluator extends RegressionSplitEvaluator implements OpenmlSplitEvaluator {
	
	private static final long serialVersionUID = 207500686037430871L;
	
	public ArrayList<Prediction> recentPredictions() throws Exception {
		if( m_Evaluation != null ) {
			return m_Evaluation.predictions();
		}
		throw new Exception("No predictions set by SplitEvaluator. ");
	}
	
	public Classifier getClassifier() {
		return m_Classifier;
	}
}
