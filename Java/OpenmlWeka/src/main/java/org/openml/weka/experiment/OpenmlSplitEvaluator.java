package org.openml.weka.experiment;

import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Prediction;
import weka.core.AdditionalMeasureProducer;
import weka.core.OptionHandler;
import weka.core.RevisionHandler;
import weka.experiment.SplitEvaluator;

public interface OpenmlSplitEvaluator extends SplitEvaluator, OptionHandler, AdditionalMeasureProducer, RevisionHandler {
	
	public ArrayList<Prediction> recentPredictions() throws Exception;
	
	public Classifier getClassifier();
	
	public void setClassifier( Classifier classifier );
}
