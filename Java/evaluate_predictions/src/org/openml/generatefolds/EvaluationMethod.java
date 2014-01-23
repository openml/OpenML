package org.openml.generatefolds;

import java.util.Arrays;

import weka.core.Instances;

public class EvaluationMethod {

	public static enum EvaluationMethods {CROSSVALIDATION, LEAVEONEOUT, HOLDOUT, LEARNINGCURVE}
	public final static String[] evaluationMethods = {"crossvalidation","leaveoneout","holdout","learningcurve"};
	
	private final EvaluationMethods em;
	private final int arg1;
	private final int arg2;
	
	public EvaluationMethod( String descriptor, Instances dataset ) {
		String[] parts = descriptor.split("_");
		if( parts.length == 0 ) {
			throw new RuntimeException("Illigal evaluationMethod (NULL)"); }
		if( Arrays.asList(evaluationMethods).contains(parts[0]) == false ) {
			throw new RuntimeException("Illigal evaluationMethod"); }
		if( parts[0].equals(evaluationMethods[1] ) ) {
			em = EvaluationMethods.LEAVEONEOUT;
			arg1 = 1;
			arg2 = dataset.numInstances();
		} else {
			if( parts.length != 3 ) {
				throw new RuntimeException("Illigal evaluationMethod"); }
			
			if( parts[0].equals(evaluationMethods[0] ) ) {
				em = EvaluationMethods.CROSSVALIDATION;
			} else  if( parts[0].equals(evaluationMethods[2] ) ) {
				em = EvaluationMethods.HOLDOUT;
			} else {// if( parts[0].equals(evaluationMethods[3] ) ) { 
				em = EvaluationMethods.LEARNINGCURVE;
			}
			arg1 = Integer.valueOf(parts[1]);
			arg2 = Integer.valueOf(parts[2]);
		}
	}
	
	public EvaluationMethods getEvaluationMethod() {
		return em;
	}
	
	public int sampleSize( int number, int trainingsetSize ) {
		return (int) Math.min(trainingsetSize, Math.round( Math.pow( 2, 6 + ( number * 0.5 ) ) ) );
	}
	
	public int getNumberOfSamples( int trainingsetSize ) {
		int i = 0;
		for( ; sampleSize( i, trainingsetSize ) < trainingsetSize; ++i ) { }
		return i + 1; // + 1 for considering the "full" training set
	}
	
	public int getRepeats() {
		return arg1;
	}
	
	public int getFolds() {
		return arg2;
	}
	
	public int getPercentage() {
		return arg2;
	}
	
	public int getSplitsSize( Instances dataset ) {
		switch(em) {
		case LEARNINGCURVE:
			// TODO: might be good to find a neat closed formula. 
			int foldsize = (int) Math.ceil(dataset.numInstances() / arg2);
			int trainingsetsize = foldsize * (getFolds()-1);
			int totalsize = 0;
			for( int i = 0; i < arg2; ++i ) {
				totalsize += sampleSize(i, trainingsetsize ) + foldsize;
			}
			return totalsize * arg1; 
		case LEAVEONEOUT:
			return dataset.numInstances() * dataset.numInstances(); // repeats (== data set size) * data set size
		case HOLDOUT:
			return dataset.numInstances() * arg1; // repeats * data set size (each instance is used once)
		case CROSSVALIDATION:
		default:
			return dataset.numInstances() * arg1 * arg2; // repeats * folds * data set size
		}
	}
	
	public String toString() {
		switch( em ) {
			case LEARNINGCURVE:
				return evaluationMethods[3] + "_" + arg1 + "_" + arg2;
			case HOLDOUT:
				return evaluationMethods[2] + "_" + arg1 + "_" + arg2;
			case LEAVEONEOUT:
				return evaluationMethods[1];
			case CROSSVALIDATION:
			default:
				return evaluationMethods[0] + "_" + arg1 + "_" + arg2;
		}
	}
}
