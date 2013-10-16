package org.openml.generatefolds;

import java.util.Arrays;

import weka.core.Instances;

public class EvaluationMethod {

	public static enum EvaluationMethods {CROSSVALIDATION, LEAVEONEOUT, HOLDOUT}
	public final static String[] evaluationMethods = {"cv","leaveoneout","holdout"};
	
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
			} else {
				em = EvaluationMethods.HOLDOUT;
			}
			arg1 = Integer.valueOf(parts[1]);
			arg2 = Integer.valueOf(parts[2]);
		}
	}
	
	public EvaluationMethods getEvaluationMethod() {
		return em;
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
