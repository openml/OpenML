package org.openml;

import java.io.BufferedReader;

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
	
	public static void main( String[] args ) {
		
		try {
			if( args.length != 4 ) {
				System.out.println( Output.styleToJsonError("Wrong number of arguments for Java program") );
			} else {
				new EvaluatePredictions( args[0], args[1], args[2], args[3] );
			}
		} catch( Exception e ) {
			System.out.println( Output.styleToJsonError(e.getMessage()) );
		}
	}
	
	public EvaluatePredictions( String datasetPath, String splitsPath, String predictionsPath, String classAttribute ) throws Exception {
		// set all arff files needed for this operation. 
		dataset 	= new Instances( new BufferedReader( Input.getURL( datasetPath ) ) );
		splits 		= new Instances( new BufferedReader( Input.getURL( splitsPath ) ) );
		predictions = new Instances( new BufferedReader( Input.getURL( predictionsPath ) ) ); 
		
		// initiate a class that will help us with checking the prediction count. 
		predictionCounter = new PredictionCounter(splits);
		
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
		Evaluation e = new Evaluation( dataset );
		
		for( int i = 0; i < predictions.numInstances(); i++ ) {
			Instance prediction = predictions.instance( i );
			int repeat = (int) prediction.value( ATT_PREDICTION_REPEAT );
			int fold = (int) prediction.value( ATT_PREDICTION_FOLD );
			int rowid = (int) prediction.value( ATT_PREDICTION_ROWID );
			
			predictionCounter.addPrediction(repeat, fold, rowid);
			
			e.evaluateModelOnce( 
				confidences( dataset, prediction ), 
				dataset.instance( rowid ) );
		}
		
		if( predictionCounter.check() ) {
			output( e, task );
		} else {
			throw new RuntimeException( "Prediction count does not match" );
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
		if( task == Task.CLASSIFICATION ) {
			System.out.println( "{\n\"metrices\": [\n" + Output.globalMetrics( e, classes.length ) + Output.classificationMetrics( e, classes.length ) + "]\n}" );
		} else if( task == Task.REGRESSION ) {
			System.out.println( "{\n\"metrices\": [\n" + Output.globalMetrics( e, classes.length ) + Output.regressionMetrics( e, classes.length ) + "]\n}" );
		} else {
			throw new RuntimeException( "Task not defined" );
		}
	}
	
}