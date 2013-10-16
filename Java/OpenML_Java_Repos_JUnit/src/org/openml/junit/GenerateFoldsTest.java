package org.openml.junit;

import static org.junit.Assert.*;

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
//		new Dataset("http://expdb.cs.kuleuven.be/expdb/data/uci/nominal/anneal.arff", "class"),
//		new Dataset("http://expdb.cs.kuleuven.be/expdb/data/uci/nominal/sonar.arff", "Class"),
//		new Dataset("http://expdb.cs.kuleuven.be/expdb/data/uci/nominal/tic-tac-toe.arff", "Class"),
//		new Dataset("http://expdb.cs.kuleuven.be/expdb/data/uci/nominal/letter.arff", "class")
	};
	
	public static final String[] evaluations = {
		"cv_1_10",
//		"cv_10_10",
//		"leaveoneout",
//		"holdout_1_33",
//		"holdout_1_10",
//		"holdout_10_10",
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
		String outputFilename = "output/"+d.getName()+"_"+em+".arff";
		System.out.println("testing: " + outputFilename + ", instances: " + expectedSize);
		GenerateFolds gf = new GenerateFolds(d.getUrl(), outputFilename, em, d.getTargetfeature(), d.getRowid(), 0);
		Instances splits = new Instances( Input.getFile(outputFilename) );
		PredictionCounter counter = new PredictionCounter(splits);
		
		// test if number of instances in dataset is OK. Very weak test,
		// since this number is also used in the generation
		assertTrue( splits.numInstances() == expectedSize );
		
		// test if each repeat is complete:
		for( int i = 0; i < evaluationMethod.getRepeats(); ++i ) {
			for( int j = 0; j < evaluationMethod.getFolds(); ++j ) {
				System.out.println(counter.getExpectedRowids(i, j));
			}
		}
	}
}
