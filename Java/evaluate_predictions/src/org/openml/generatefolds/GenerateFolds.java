package org.openml.generatefolds;

import java.io.BufferedReader;
import java.util.Random;

import org.openml.io.Input;

import weka.core.Attribute;
import weka.core.Instances;

public class GenerateFolds {
	public final static String[] evaluation_methods = {"cv","leave_one_out","holdout"};

	private final Instances dataset;
	private final Instances splits;
	private final String splits_name;
	private final Integer splits_size;
	
	private EvaluationMethod evaluationMethod;
	private Integer evaluationMethod_p1;
	private Integer evaluationMethod_p2;
	
	private final ArffMapping am;
	private final Random rand;
	
	public GenerateFolds( String datasetPath, String splitsPath, String evaluation, String targetFeature, String rowid, int seed ) throws Exception {
		am = new ArffMapping();
		rand = new Random(seed);
		
		splits_name = Input.filename( datasetPath ) + "_splits";
		splits_size = initialize_evaluation( evaluation );
		dataset = new Instances( new BufferedReader( Input.getURL( datasetPath ) ) );
		setTargetAttribute( dataset, targetFeature );
		
		if(rowid.equals("")) {
			rowid = "rowid";
			addRowId(dataset,rowid);
		}
		
		splits = new Instances(splits_name,am.getArffHeader(),splits_size);
		
		switch(evaluationMethod) {
			case HOLDOUT:
				for( int r = 0; r < evaluationMethod_p1; ++r) {
					dataset.randomize(rand);
					int testSetSize = Math.round(dataset.numInstances()*evaluationMethod_p1);
					for( int i = 0; i < dataset.numInstances(); ++i ) {
						splits.add(am.createInstance(i < testSetSize,dataset.instance(i).value(0),r,0));
					}
				}
				break;
			case CROSSVALIDATION:
				for( int r = 0; r < evaluationMethod_p1; ++r) {
					dataset.randomize(rand);
					if (dataset.classAttribute().isNominal())
						dataset.stratify(evaluationMethod_p2);
					
					for( int f = 0; f < evaluationMethod_p2; ++f ) {
						Instances train = dataset.trainCV(evaluationMethod_p2, f);
						Instances test = dataset.testCV(evaluationMethod_p2, f);
						
						for( int i = 0; i < train.numInstances(); ++i ) 
							splits.add(am.createInstance(true,train.instance(i).value(0),r,f));
						for( int i = 0; i < test.numInstances(); ++i ) 
							splits.add(am.createInstance(false,test.instance(i).value(0),r,f));
					}
				}
				break;
			case LEAVEONEOUT:
				for( int r = 0; r < dataset.numInstances(); ++r ) {
					for( int f = 0; f < dataset.numInstances(); ++f ) {
						splits.add(am.createInstance(r==f,dataset.instance(f).value(0),r,f));
					}
				}
				break;
		}
		
		System.out.println(splits);
		
	}
	
	// @pre: dataset is initialized
	// @return: the number of instances in split file
	private int initialize_evaluation( String e ) {
		String[] evaluation = e.split("_");
		if( evaluation[0].equals(evaluation_methods[0]) ) {
			if(evaluation.length < 3)
				throw new RuntimeException("Evaluation method not complete, should be in the form cv_{repeats}_{folds}.");
			evaluationMethod = EvaluationMethod.CROSSVALIDATION;
			evaluationMethod_p1 = Integer.valueOf(evaluation[1]);
			evaluationMethod_p2 = Integer.valueOf(evaluation[2]);
			return dataset.numInstances() * evaluationMethod_p1 * evaluationMethod_p2; // repeats * folds * data set size
		} else if( evaluation[0].equals(evaluation_methods[1]) ) {
			evaluationMethod = EvaluationMethod.LEAVEONEOUT;
			evaluationMethod_p1 = -1;
			evaluationMethod_p2 = -1;
			return dataset.numInstances() * dataset.numInstances(); // repeats (== data set size) * data set size
		} else if( evaluation[0].equals(evaluation_methods[2]) ) {
			if(evaluation.length < 3)
				throw new RuntimeException("Evaluation method not complete, should be in the form holdout_{repeats}_{percentage}.");
			evaluationMethod = EvaluationMethod.HOLDOUT;
			evaluationMethod_p1 = Integer.valueOf(evaluation[1]);
			evaluationMethod_p2 = Integer.valueOf(evaluation[2]);
			return dataset.numInstances() * evaluationMethod_p1; // repeats * data set size (each instance is used once)
		} else {
			throw new RuntimeException("Evaluation method not in {"+evaluation_methods.toString()+"}.");
		}
	}
	
	private static Instances addRowId( Instances instances, String name ) {
		instances.insertAttributeAt(new Attribute(name), 0);
		for( int i = 0; i < instances.numInstances(); ++i )
			instances.instance(i).setValue(0, i);
		return instances;
	}
	
	public static void setTargetAttribute( Instances instances, String classAttribute ) throws Exception {
		for(int i = 0; i < instances.numAttributes(); ++i ) {
			if(instances.attribute(i).name().equals(classAttribute)) {
				instances.setClassIndex(i);
				return;
			}
		}
		throw new Exception("classAttribute " + classAttribute + " non-existant on dataset. ");
	}
}
