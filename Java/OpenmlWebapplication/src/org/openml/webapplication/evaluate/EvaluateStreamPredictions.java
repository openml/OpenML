package org.openml.webapplication.evaluate;

import java.util.Map;

import org.openml.apiconnector.models.Metric;
import org.openml.apiconnector.models.MetricScore;
import org.openml.webapplication.algorithm.InstancesHelper;
import org.openml.webapplication.io.Output;

import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class EvaluateStreamPredictions {

	private final Instances  datasetStructure;
	private final Instances  predictionsStructure;
	private final ArffLoader datasetLoader;
	private final ArffLoader predictionsLoader;

	private final String[] classes;
	private final int nrOfClasses;
	private final int ATT_PREDICTION_ROWID;
	private final int[] ATT_PREDICTION_CONFIDENCE;
	
	public EvaluateStreamPredictions( String datasetUrl, String predictionsUrl, String classAttribute ) throws Exception {
		datasetLoader = new ArffLoader();
		datasetLoader.setURL(datasetUrl);
		datasetStructure = new Instances( datasetLoader.getStructure() );

		predictionsLoader = new ArffLoader();
		predictionsLoader.setURL(predictionsUrl);
		predictionsStructure = new Instances( predictionsLoader.getStructure() );
		
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
		
		doEvaluation();
	}
	
	private void doEvaluation() throws Exception {
		// set global evaluation
		Evaluation e = new Evaluation( datasetStructure );
		

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
			
			e.evaluateModelOnce(
				InstancesHelper.predictionToConfidences( datasetStructure, currentPrediction, ATT_PREDICTION_CONFIDENCE ), // TODO: catch error when no prob distribution is provided
				currentInstance );
		}
		
		Map<Metric, MetricScore> metrics = Output.evaluatorToMap(e, nrOfClasses, Task.TESTTHENTRAIN);
		String globalMetrics = Output.printMetrics(metrics);
		
		System.out.println("{\n\"global_metrices\":[\n" + globalMetrics + "]\n}" );
		
	}
}
