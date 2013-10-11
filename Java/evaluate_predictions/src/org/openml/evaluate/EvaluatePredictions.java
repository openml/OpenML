package org.openml.evaluate;

import java.io.BufferedReader;

import org.apache.commons.lang3.StringUtils;
import org.openml.io.Input;
import org.openml.io.Output;

import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;

public class EvaluatePredictions {
	
	private final int nrOfClasses;
	
	private final int ATT_PREDICTION_ROWID;
	private final int ATT_PREDICTION_FOLD;
	private final int ATT_PREDICTION_REPEAT;
	private final int[] ATT_PREDICTION_CONFIDENCE;
	
	private final Instances dataset;
	private final Instances splits;
	private final Instances predictions;
	
	private final PredictionCounter predictionCounter;
	private final String[] classes;
	private final Task task;
	private final Evaluation[][] foldEvaluation;
	
	public EvaluatePredictions( String datasetPath, String splitsPath, String predictionsPath, String classAttribute ) throws Exception {
		// set all arff files needed for this operation. 
		dataset 	= new Instances( new BufferedReader( Input.getURL( datasetPath ) ) );
		splits 		= new Instances( new BufferedReader( Input.getURL( splitsPath ) ) );
		predictions = new Instances( new BufferedReader( Input.getURL( predictionsPath ) ) ); 
		
		// initiate a class that will help us with checking the prediction count. 
		predictionCounter = new PredictionCounter(splits);
		foldEvaluation = new Evaluation[predictionCounter.getRepeats()][predictionCounter.getFolds()];
		
		// Set class attribute to dataset ...
		for( int i = 0; i < dataset.numAttributes(); i++ ) {
			if( dataset.attribute( i ).name().equals( classAttribute ) )
				dataset.setClass( dataset.attribute( i ) );
		}

		// ... and throw an error if we failed to do so ... 
		if( dataset.classIndex() < 0 ) { 
			throw new RuntimeException( "Class attribute ("+classAttribute+") not found" );
		}
		// ... and specify which task we are doing. classification or regression. 
		if( dataset.classAttribute().isNominal() ) { 
			task = Task.CLASSIFICATION;
		} else {
			task = Task.REGRESSION;
		}
		
		// first check if the fields are present.
		if(predictions.attribute("row_id") == null) 
			throw new RuntimeException("Predictions file lacks attribute row_id");
		if(predictions.attribute("fold") == null && predictions.attribute("repeat_nr") == null) 
			throw new RuntimeException("Predictions file lacks attribute fold");
		if(predictions.attribute("repeat") == null && predictions.attribute("repeat_nr") == null) 
			throw new RuntimeException("Predictions file lacks attribute repeat");
		
		// and add those fields. 
		ATT_PREDICTION_ROWID = predictions.attribute("row_id").index();
		ATT_PREDICTION_REPEAT = (predictions.attribute("repeat") != null) ? 
				predictions.attribute("repeat").index() : predictions.attribute("repeat_nr").index();
		ATT_PREDICTION_FOLD = (predictions.attribute("fold") != null) ? 
				predictions.attribute("fold").index() : predictions.attribute("fold_nr").index();
		
		// do the same for the confidence fields. This number is dependent on the number 
		// of classes in the data set, hence the for-loop. 
		nrOfClasses = dataset.classAttribute().numValues();
		classes = new String[nrOfClasses];
		ATT_PREDICTION_CONFIDENCE = new int[nrOfClasses];
		for( int i = 0; i < classes.length; i++ ) {
			classes[i] = dataset.classAttribute().value( i );
			String attribute = "confidence." + classes[i];
			if( predictions.attribute(attribute) != null )
				ATT_PREDICTION_CONFIDENCE[i] = predictions.attribute( attribute ).index();
			else
				throw new RuntimeException( "Attribute " + attribute + " not found among predictions. " );
		}
		
		// and do the actual evaluation. 
		doEvaluation();
	}
	
	private void doEvaluation() throws Exception {
		// set global evaluation
		Evaluation e = new Evaluation( dataset );
		// set local evaluations
		for( int i = 0; i < foldEvaluation.length; ++i ) 
			for( int j = 0; j < foldEvaluation[i].length; ++j )
				foldEvaluation[i][j] = new Evaluation(dataset);
		
		for( int i = 0; i < predictions.numInstances(); i++ ) {
			Instance prediction = predictions.instance( i );
			int repeat = (int) prediction.value( ATT_PREDICTION_REPEAT );
			int fold = (int) prediction.value( ATT_PREDICTION_FOLD );
			int rowid = (int) prediction.value( ATT_PREDICTION_ROWID );
			
			predictionCounter.addPrediction(repeat, fold, rowid);
			if( dataset.numInstances() <= rowid ) {
				throw new RuntimeException( "Making a prediction for row_id" + rowid + " (0-based) while dataset has only " + dataset.numInstances() + " instances. " );
			}
			e.evaluateModelOnce( 
				confidences( dataset, prediction ), 
				dataset.instance( rowid ) );
			foldEvaluation[repeat][fold].evaluateModelOnce(
				confidences( dataset, prediction ), 
				dataset.instance( rowid ) );
		}
		
		if( predictionCounter.check() ) {
			output( e, task );
		} else {
			throw new RuntimeException( "Prediction count does not match: " + predictionCounter.getErrorMessage() );
		}
	}
	
	private double[] confidences( Instances dataset, Instance prediction ) {
		double[] confidences = new double[dataset.numClasses()];
		for( int i = 0; i < dataset.numClasses(); i++ ) {
			confidences[i] = prediction.value( ATT_PREDICTION_CONFIDENCE[i] );
		}
		return confidences;
	}
	
	private void output( Evaluation e, Task task ) throws Exception {
		if( task == Task.CLASSIFICATION || task == Task.REGRESSION ) {
			String globalMetrics = Output.printMetrics(e, nrOfClasses, task);
			String foldMetrics = "";
			
			String[] metricsPerRepeat = new String[predictionCounter.getRepeats()];
			for(int i = 0; i < metricsPerRepeat.length; ++i ) {
				String[] metricsPerFold = new String[predictionCounter.getFolds()];
				for( int j = 0; j < metricsPerFold.length; j++ ) {
					metricsPerFold[j] = Output.printMetrics(foldEvaluation[i][j], nrOfClasses, task);
				}
				metricsPerRepeat[i] = "[" + StringUtils.join( metricsPerFold, ",") + "]";
			}
			foldMetrics = "[" + StringUtils.join( metricsPerRepeat, ",") + "]";
			
			System.out.println(
				"{\n" + 
					"\"global_metrices\":[\n" +
						globalMetrics +
					"],\n" +
					"\"fold_metrics\":[\n" +
						foldMetrics + 
					"]" +
				"}"
			);
		} else {
			throw new RuntimeException( "Task not defined" );
		}
	}
	
}