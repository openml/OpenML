package org.openml.junit;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;
import org.openml.evaluate.PredictionCounter;
import org.openml.generatefolds.EvaluationMethod;
import org.openml.generatefolds.GenerateFolds;
import org.openml.io.Input;
import org.openml.junit.objects.Dataset;

import weka.core.Instances;

public class GenerateFoldsTest {

	private static final Dataset[] datasets = {
		new Dataset("http://expdb.cs.kuleuven.be/expdb/data/uci/nominal/iris.arff", "class"),
		new Dataset("http://expdb.cs.kuleuven.be/expdb/data/uci/nominal/anneal.arff", "class"),
		new Dataset("http://expdb.cs.kuleuven.be/expdb/data/uci/nominal/sonar.arff", "Class"),
		new Dataset("http://expdb.cs.kuleuven.be/expdb/data/uci/nominal/tic-tac-toe.arff", "Class"),
		new Dataset("http://expdb.cs.kuleuven.be/expdb/data/uci/nominal/letter.arff", "class")
	};
	
	public static final String[] evaluations = {
		"crossvalidation_1_10",
		"crossvalidation_10_10",
		"leaveoneout",
		"holdout_1_33",
		"holdout_1_10",
		"holdout_10_10",
	};
	
	
	@Test
	public void test() throws Exception {
		for( Dataset d : datasets ) {
			for( String em : evaluations ) {
				testDatasetEvaluationCombination(d,em);
			}
		}
	}
	
	private void testDatasetEvaluationCombination( Dataset d, String em ) throws Exception {
		EvaluationMethod evaluationMethod = new EvaluationMethod(em,d.getInstances());
		Integer expectedSize = evaluationMethod.getSplitsSize(d.getInstances());
		if(expectedSize > GenerateFolds.MAX_SPLITS_SIZE) return;
		
		String outputFilename = "output/"+d.getName()+"_"+em+".arff";
		System.out.print("testing: " + outputFilename + ", instances: " + expectedSize);
		GenerateFolds gf = new GenerateFolds(d.getUrl(), em, d.getTargetfeature(), d.getRowid(), 0);
		gf.toFile(outputFilename);
		
		Instances splits = new Instances( Input.getFile(outputFilename) );
		
		// SANITY CHECK: correct number of instances in splitsset. 
		// Very weak test, since this number is also used in the generation
		assertTrue( splits.numInstances() == expectedSize );
		
		ArrayList<Integer> rowids_dataset = new ArrayList<>();
		for( int i = 0; i < d.getInstances().numInstances(); ++i ) {
			if( d.getRowid().equals( "" ) ) rowids_dataset.add(i);
		}
		Collections.sort( rowids_dataset );
		
		switch(evaluationMethod.getEvaluationMethod()) {
		case CROSSVALIDATION:
			testDatasetEvaluationCombinationCV( splits, evaluationMethod, rowids_dataset);
			break;
		case HOLDOUT:
			testDatasetEvaluationCombinationHoldout(splits, evaluationMethod, rowids_dataset);
			break;
		case LEAVEONEOUT:
		default:
			testDatasetEvaluationCombinationCV( splits, evaluationMethod, rowids_dataset);
			break;
		}
	}
	
	private void testDatasetEvaluationCombinationCV( Instances splits, EvaluationMethod em, ArrayList<Integer> rowids) {

		PredictionCounter training = new PredictionCounter(splits,"TRAIN");
		PredictionCounter test = new PredictionCounter(splits,"TEST");
		System.out.println( ", test instances: " + test.getExpectedTotal() );
		
		// test if each repeat is complete:
		for( int i = 0; i < em.getRepeats(); ++i ) {
			ArrayList<Integer> rowids_repeat = new ArrayList<>();
			for( int j = 0; j < em.getFolds(); ++j ) {
				ArrayList<Integer> rowids_fold = new ArrayList<>();
				rowids_fold.addAll(test.getExpectedRowids(i, j));
				rowids_fold.addAll(training.getExpectedRowids(i, j));
				Collections.sort( rowids_fold );
				// SANITY CHECK: in each fold, each instance should be used exactly once
				assertEquals( rowids_fold, rowids );
				
				rowids_repeat.addAll(test.getExpectedRowids(i, j));
			}
			// SANITY CHECK: in each repeat, each instance should be tested exactly once. 
			Collections.sort( rowids_repeat );
			assertEquals( rowids_repeat, rowids );
		}
		
		// test if each testset is different:
		for( int i = 0; i < em.getRepeats() * em.getFolds(); ++i ) {
			ArrayList<Integer> current = test.getExpectedRowids(i / em.getFolds(), i % em.getFolds());
			for( int j = 0; j < i; ++j ) {
				ArrayList<Integer> previous = test.getExpectedRowids(j / em.getFolds(), j % em.getFolds());
				assertFalse( current.equals(previous) );
			}
		}
	}
	
	private void testDatasetEvaluationCombinationHoldout( Instances splits, EvaluationMethod em, ArrayList<Integer> rowids) {

		PredictionCounter training = new PredictionCounter(splits,"TRAIN");
		PredictionCounter test = new PredictionCounter(splits,"TEST");
		System.out.println( ", test instances: " + test.getExpectedTotal() );
		
		// test if each repeat is complete:
		for( int i = 0; i < em.getRepeats(); ++i ) {
			ArrayList<Integer> rowids_repeat = new ArrayList<>();
			rowids_repeat.addAll(test.getExpectedRowids(i, 0));
			rowids_repeat.addAll(training.getExpectedRowids(i, 0));
			
			// SANITY CHECK: in each repeat, each instance should be tested exactly once. 
			Collections.sort( rowids_repeat );
			assertEquals( rowids_repeat, rowids );
		}
		
		// test if each testset is different:
		for( int i = 0; i < em.getRepeats(); ++i ) {
			ArrayList<Integer> current = test.getExpectedRowids( i, 0);
			for( int j = 0; j < i; ++j ) {
				ArrayList<Integer> previous = test.getExpectedRowids( j, 0);
				assertFalse( current.equals(previous) );
			}
		}
	}
}
