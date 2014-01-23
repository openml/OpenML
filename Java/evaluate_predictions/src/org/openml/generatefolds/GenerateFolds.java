package org.openml.generatefolds;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Random;

import org.openml.generatefolds.EvaluationMethod.EvaluationMethods;
import org.openml.io.Input;
import org.openml.io.Output;

import weka.core.Attribute;
import weka.core.Instances;

public class GenerateFolds {
	public static final int MAX_SPLITS_SIZE = 1000000;
	
	private final Instances dataset;
	private final Instances splits;
	private final String splits_name;
	private final Integer splits_size;
	
	private final EvaluationMethod evaluationMethod;
	
	private final ArffMapping am;
	private final Random rand;
	
	public GenerateFolds( String datasetPath, String evaluation, String targetFeature, String rowid, int seed ) throws Exception {
		rand = new Random(seed);
		
		dataset = new Instances( new BufferedReader( Input.getURL( datasetPath ) ) );
		evaluationMethod = new EvaluationMethod(evaluation,dataset);
		
		setTargetAttribute( dataset, targetFeature );
		
		am = new ArffMapping( evaluationMethod.getEvaluationMethod() == EvaluationMethods.LEARNINGCURVE);
		
		splits_name = Input.filename( datasetPath ) + "_splits";
		splits_size = evaluationMethod.getSplitsSize(dataset);
		
		if(splits_size > MAX_SPLITS_SIZE)
			throw new RuntimeException("Dataset to big for this type of evaluation method. ");
		
		if(rowid.equals("")) {
			rowid = "rowid";
			addRowId(dataset,rowid);
		}
		
		splits = generateInstances(splits_name);
	}
	
	public void toFile( String splitsPath ) throws IOException {
		Output.instanes2file(splits, splitsPath);
	}
	
	public void toStdout() {
		System.out.println(splits.toString());
	}
	
	private Instances generateInstances(String name) {
		Instances splits = new Instances(name,am.getArffHeader(),splits_size);
		switch(evaluationMethod.getEvaluationMethod()) {
			case HOLDOUT:
				for( int r = 0; r < evaluationMethod.getRepeats(); ++r) {
					dataset.randomize(rand);
					int testSetSize = Math.round(dataset.numInstances()*evaluationMethod.getPercentage()/100);
					
					for( int i = 0; i < dataset.numInstances(); ++i ) {
						int rowid = (int) dataset.instance(i).value(0);
						splits.add(am.createInstance(i >= testSetSize,rowid,r,0));
					}
				}
				break;
			case CROSSVALIDATION:
				for( int r = 0; r < evaluationMethod.getRepeats(); ++r) {
					dataset.randomize(rand);
					if (dataset.classAttribute().isNominal())
						dataset.stratify(evaluationMethod.getFolds());
					
					for( int f = 0; f < evaluationMethod.getFolds(); ++f ) {
						Instances train = dataset.trainCV(evaluationMethod.getFolds(), f);
						Instances test = dataset.testCV(evaluationMethod.getFolds(), f);
						
						for( int i = 0; i < train.numInstances(); ++i ) {
							int rowid = (int) train.instance(i).value(0);
							splits.add(am.createInstance(true,rowid,r,f));
						}
						for( int i = 0; i < test.numInstances(); ++i ) {
							int rowid = (int) test.instance(i).value(0);
							splits.add(am.createInstance(false,rowid,r,f));
						}
					}
				}
				break;
			case LEAVEONEOUT:
				for( int f = 0; f < dataset.numInstances(); ++f ) {
					for( int i = 0; i < dataset.numInstances(); ++i ) {
						int rowid = (int) dataset.instance(i).value(0);
						splits.add(am.createInstance(f!=i,rowid,0,f));
					}
				}
				break;
			case LEARNINGCURVE:
				for( int r = 0; r < evaluationMethod.getRepeats(); ++r ) {
					dataset.randomize(rand);
					if (dataset.classAttribute().isNominal())
						dataset.stratify(evaluationMethod.getFolds());
					
					for( int f = 0; f < evaluationMethod.getFolds(); ++f ) {
						Instances train = dataset.trainCV(evaluationMethod.getFolds(), f);
						Instances test = dataset.testCV(evaluationMethod.getFolds(), f);
						// TODO: stratify the training set
						for( int s = 0; s < evaluationMethod.getNumberOfSamples( train.numInstances() ); ++s ) {
							for( int i = 0; i < evaluationMethod.sampleSize( f, train.numInstances() ); ++i ) {
								int rowid = (int) train.instance(i).value(0);
								splits.add(am.createInstance(true,rowid,r,f,i));
							}
							for( int i = 0; i < test.numInstances(); ++i ) {
								int rowid = (int) train.instance(i).value(0);
								splits.add(am.createInstance(false,rowid,r,f,i));
							}
						}
					}
				}
				break;
		}
		return splits;
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
