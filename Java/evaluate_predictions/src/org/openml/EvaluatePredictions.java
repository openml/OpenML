package org.openml;

import java.io.BufferedReader;

import org.openml.io.Input;
import org.openml.io.Output;

import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;

public class EvaluatePredictions {
	
	private final int ATT_PREDICTION_ROWID;
	private final int[] ATT_PREDICTION_CONFIDENCE;
	
	private final Instances dataset;
	private final Instances splits;
	private final Instances predictions;
	
	private final PredictionCounter predictionCounter;
	private final String[] classes;
	
	public static void main( String[] args ) throws Exception {
		new EvaluatePredictions( 
				"http://expdb.cs.kuleuven.be/expdb/data/uci/nominal/iris.arff", 
				"http://expdb.cs.kuleuven.be/expdb/data/splits/iris_splits_CV_10_2.arff",
				"http://expdb.cs.kuleuven.be/expdb/data/splits/iris_splits_CV_10_2.arff",
				"class",
				"classification" );
		/*if( args.length != 5 ) {
			System.out.println( Output.styleToJson("error", "Wrong number of arguments", true) );
		} else {
			new EvaluatePredictions( args[0], args[1], args[2], args[3], args[4] );
		}*/
	}
	
	public EvaluatePredictions( String datasetPath, String splitsPath, String predictionsPath, String classAttribute, String task ) throws Exception {
		
		dataset 	= new Instances( new BufferedReader( Input.getURL( datasetPath ) ) );
		splits 		= new Instances( new BufferedReader( Input.getURL( splitsPath ) ) );
		predictions = new Instances( new BufferedReader( Input.getURL( predictionsPath ) ) ); 
		
		predictionCounter = new PredictionCounter(splits);
		
		ATT_PREDICTION_ROWID = predictions.attribute("prediction").index();
		
		for( int i = 0; i < dataset.numAttributes(); i++ ) {
			if( dataset.attribute( i ).name().equals( classAttribute ) )
				dataset.setClass( dataset.attribute( i ) );
		}
		if( dataset.classIndex() < 0 ) { 
			System.out.println( Output.styleToJson("error", "Class attribute not found", true) );
			return; 
		}
		setClasses( dataset );
		
		Evaluation e = new Evaluation( dataset );
		
		for( int i = 0; i < predictions.numInstances(); i++ ) {
			Instance prediction = predictions.instance( i );
			e.evaluateModelOnce( 
				confidences( dataset, prediction ), 
				dataset.instance( (int) prediction.value( ATT_PREDICTION_ROWID ) ) );
		}
		
		output( e, task );
	}
	
	private double[] confidences( Instances dataset, Instance prediction ) {
		double[] confidences = new double[dataset.numClasses()];
		for( int i = 0; i < dataset.numClasses(); i++ ) {
			confidences[i] = prediction.value( ATT_PREDICTION_CONFIDENCE[i] );
		}
		return confidences;
	}
	
	private void output( Evaluation e, String task ) throws Exception {
		if( task.equals( "classification" ) ) {
			System.out.println( "{\n\"metrices\": [\n" + Output.globalMetrics( e ) + Output.classificationMetrics( e ) + "]\n}" );
		} else if( task.equals( "regression" ) ) {
			System.out.println( "{\n\"metrices\": [\n" + Output.globalMetrics( e ) + Output.regressionMetrics( e ) + "]\n}" );
		} else {
			System.out.println( Output.styleToJson("error", "Task not defined", true) );
		}
	}
	
	private void setClasses( Instances dataset ) {
		
	}
	
}
