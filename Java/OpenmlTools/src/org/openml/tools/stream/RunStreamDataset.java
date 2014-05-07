package org.openml.tools.stream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.tools.algorithms.InstancesHelper;

import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class RunStreamDataset {
	private static final String OPENML_TASK_ID_ATT = "openml_task_id";
	private static final String OPENML_SCORE_ATT_PREFIX = "openml_classifier_";
	private static final String OPENML_CLASS_ATT = "class";
	
	private final ArrayList<Attribute> scoreAttributes;
	private final ArrayList<Attribute> trainTestAttributes;
	private final Evaluation GLOBAL_EVALUATOR;
	private final BufferedWriter LOG_WRITER;
	
	private final Map<Integer, Integer> tasksAvailable;
	private final Instances allMeasurements;
	private final Integer taskIdIndex;
	
	public static void main( String[] args ) throws Exception {
		
		new RunStreamDataset();
		
	}
	
	public RunStreamDataset() throws Exception {
		tasksAvailable = new HashMap<Integer, Integer>();
		allMeasurements = new Instances( new BufferedReader( new FileReader( new File("meta_stream.arff") ) ) );
		allMeasurements.setClass( allMeasurements.attribute( OPENML_CLASS_ATT ) );
		GLOBAL_EVALUATOR = new Evaluation( allMeasurements );
		GLOBAL_EVALUATOR.useNoPriors();
		LOG_WRITER = new BufferedWriter( new FileWriter( new File( "evaluator.log" ) ) );
		
		taskIdIndex = allMeasurements.attribute( OPENML_TASK_ID_ATT ).index();
		scoreAttributes = getScoreAttributes();
		trainTestAttributes = getTrainTestAttributes();
		
		for( int i = 0; i < allMeasurements.numInstances(); ++i ) {
			int taskId = (int) allMeasurements.instance( i ).value( taskIdIndex );
			if( tasksAvailable.containsKey( taskId ) == false ) {
				tasksAvailable.put( taskId, 1 );
			} else {
				tasksAvailable.put( taskId, tasksAvailable.get( taskId ) + 1 );
			}
		}
		
		System.out.println( tasksAvailable );
		int counter = 0;
		for( Integer i : tasksAvailable.keySet() ) {
			Conversion.log( "[OK]", "[RunStream]", "Running task " + i + " ~ "+ tasksAvailable.get( i ) + " instances ("+(++counter)+"/"+tasksAvailable.keySet().size()+")" );
			evaluateTask( i );
			Conversion.log( "[OK]", "[RunStream]", "Current score " + GLOBAL_EVALUATOR.pctCorrect() );
		}
		
		System.out.println( GLOBAL_EVALUATOR.toSummaryString() );
		LOG_WRITER.write( GLOBAL_EVALUATOR.toSummaryString() );
	}
	
	private void evaluateTask( int task_id ) throws Exception {
		Integer instancesCount = tasksAvailable.get( task_id );
		Instances testSet = new Instances( "task_" + task_id + "_test", trainTestAttributes, instancesCount );
		Instances trainSet = new Instances( "task_" + task_id + "_train", trainTestAttributes, allMeasurements.size() - instancesCount );
		Instances scoreSet = new Instances( "task_" + task_id + "_scores", scoreAttributes, instancesCount);
		
		trainSet.setClass( trainSet.attribute( OPENML_CLASS_ATT ) );
		testSet.setClass( testSet.attribute( OPENML_CLASS_ATT ) );
		// first create the three instance sets...
		for( int i = 0; i < allMeasurements.numInstances(); ++i ) {
			int instanceTaskId = (int) allMeasurements.instance( i ).value( taskIdIndex );
			if( task_id == instanceTaskId ) {
				addPlainInstance( testSet, allMeasurements.instance( i ) );
				addScoreInstance( scoreSet, allMeasurements.instance( i ) );
			} else {
				addPlainInstance( trainSet, allMeasurements.instance( i ) );
			}
		}
		
		InstancesHelper.toFile(testSet, "test");
		InstancesHelper.toFile(trainSet, "train");
		
		Classifier metaLearner = new RandomForest();
		Evaluation evaluator = new Evaluation( trainSet );
		
		metaLearner.buildClassifier( trainSet );
		
		for( int i = 0; i < testSet.numInstances(); ++i ) {
			GLOBAL_EVALUATOR.evaluateModelOnceAndRecordPrediction( metaLearner, testSet.instance( i ) );
			evaluator.evaluateModelOnceAndRecordPrediction( metaLearner, testSet.instance( i ) );
			//int prediction = (int) metaLearner.classifyInstance( testSet.instance( i ) );
			//int correct = (int) testSet.instance( i ).classValue();
			
			//String strPrediction = testSet.classAttribute().value( prediction );
			//String strCorrect = testSet.classAttribute().value( correct );
			//System.out.println("predicted: " + strPrediction + "( correct = " + strCorrect + ")" );
		}
		LOG_WRITER.write( evaluator.toSummaryString() );
	}
	
	private ArrayList<Attribute> getTrainTestAttributes() {
		ArrayList<Attribute> result = new ArrayList<Attribute>();
		for( int i = 0; i < allMeasurements.numAttributes(); ++i ) {
			String attName = allMeasurements.attribute( i ).name();
			if( attName.equals( OPENML_TASK_ID_ATT ) ) {
				// do nothing
			} else if( attName.startsWith( OPENML_SCORE_ATT_PREFIX ) ) {
				// do nothing
			} else {
				result.add( InstancesHelper.copyAttribute( allMeasurements.attribute( i ) ) );
			}
		}
		return result;
	}
	
	private ArrayList<Attribute> getScoreAttributes() {
		ArrayList<Attribute> result = new ArrayList<Attribute>();
		for( int i = 0; i < allMeasurements.numAttributes(); ++i ) {
			String attName = allMeasurements.attribute( i ).name();
			if( attName.equals( OPENML_TASK_ID_ATT ) ) {
				// do nothing
			} else if( attName.startsWith( OPENML_SCORE_ATT_PREFIX ) ) {
				result.add( new Attribute( attName.substring( OPENML_SCORE_ATT_PREFIX.length() ) ) );
			}
		}
		return result;
	}
	
	private void addPlainInstance( Instances set, Instance otherInstance ) {
		Instance newInstance = new DenseInstance( set.numAttributes() );
		newInstance.setDataset( set );
		
		for( int i = 0; i < newInstance.numAttributes(); ++i ) {
			String attName = newInstance.attribute( i ).name() + "";
			int otherInstanceAttIndex = otherInstance.dataset().attribute( attName ).index();
			newInstance.setValue( i, otherInstance.value( otherInstanceAttIndex ) );
		}
		set.add( newInstance );
	}
	
	private void addScoreInstance( Instances set, Instance otherInstance ) {
		Instance newInstance = new DenseInstance( set.numAttributes() );
		newInstance.setDataset( set );
		
		for( int i = 0; i < newInstance.numAttributes(); ++i ) {
			String attName = newInstance.attribute( i ).name();
			int otherInstanceAttIndex = otherInstance.dataset().attribute( OPENML_SCORE_ATT_PREFIX + attName ).index();
			newInstance.setValue( i, otherInstance.value( otherInstanceAttIndex ) );
		}
		set.add( newInstance );
	}
}
