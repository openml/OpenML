/*
 *  Webapplication - Java library that runs on OpenML servers
 *  Copyright (C) 2014 
 *  @author Jan N. van Rijn (j.n.van.rijn@liacs.leidenuniv.nl)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
package org.openml.webapplication.generatefolds;

import java.util.Arrays;

import weka.core.Instances;

public class EstimationProcedure {

	public static enum EstimationProcedureType {CROSSVALIDATION, LEAVEONEOUT, HOLDOUT, LEARNINGCURVE, HOLDOUT_UNLABELED, CUSTOMHOLDOUT, BOOTSTRAP}
	public final static String[] estimationProceduresTxt = {"crossvalidation", "leaveoneout", "holdout", "learningcurve", "holdoutunlabeled", "customholdout", "bootstrap"};
	
	private final EstimationProcedureType em;
	private final int arg1;
	private final int arg2;
	
	public EstimationProcedure( String descriptor, Instances dataset ) {
		String[] parts = descriptor.split("_");
		
		if( parts.length == 0 ) {
			throw new RuntimeException("Illigal evaluationMethod (EvaluationMethod::construct (1))"); }
		if( Arrays.asList(estimationProceduresTxt).contains(parts[0]) == false ) {
			throw new RuntimeException("Illigal evaluationMethod (EvaluationMethod::construct (2))"); }
		if( parts[0].equals(estimationProceduresTxt[1] ) ) {
			em = EstimationProcedureType.LEAVEONEOUT;
			arg1 = 1;
			arg2 = dataset.numInstances();
		} else if( parts[0].equals(estimationProceduresTxt[4] ) ) {
			em = EstimationProcedureType.HOLDOUT_UNLABELED;
			arg1 = 1;
			arg2 = dataset.numInstances();
		} else if( parts[0].equals(estimationProceduresTxt[5] ) ) {
			em = EstimationProcedureType.CUSTOMHOLDOUT;
			arg1 = 1;
			arg2 = 10; // TODO: encode this information in call! (also in api splits)
		} else if( parts[0].equals(estimationProceduresTxt[6] ) ) {
			em = EstimationProcedureType.BOOTSTRAP;
			arg1 = Integer.valueOf(parts[1]);
			arg2 = 1;
		} else {
			if( parts.length != 3 ) {
				throw new RuntimeException("Illigal evaluationMethod (EvaluationMethod::construct (3))"); }
			
			if( parts[0].equals(estimationProceduresTxt[0] ) ) {
				em = EstimationProcedureType.CROSSVALIDATION;
			} else  if( parts[0].equals(estimationProceduresTxt[2] ) ) {
				em = EstimationProcedureType.HOLDOUT;
			} else if( parts[0].equals(estimationProceduresTxt[3] ) ) { 
				em = EstimationProcedureType.LEARNINGCURVE;
			} else {
				throw new RuntimeException("Illigal evaluationMethod (EvaluationMethod::construct (4))");
			}
			arg1 = Integer.valueOf(parts[1]);
			arg2 = Integer.valueOf(parts[2]);
		}
	}
	
	public EstimationProcedureType getEvaluationMethod() {
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
			int foldsize = (int) Math.ceil(dataset.numInstances() * 1.0 / arg2);
			int trainingsetsize = foldsize * (getFolds()-1);
			int totalsize = 0;
			for( int i = 0; i < getNumberOfSamples( trainingsetsize ); ++i ) {
				totalsize += sampleSize(i, trainingsetsize ) + foldsize;
			}
			return totalsize * arg1 * arg2; 
		case LEAVEONEOUT:
			return dataset.numInstances() * dataset.numInstances(); // repeats (== data set size) * data set size
		case HOLDOUT:
			return dataset.numInstances() * arg1; // repeats * data set size (each instance is used once)
		case HOLDOUT_UNLABELED:
			return dataset.numInstances(); // only one repeat valid
		case CROSSVALIDATION:
			return dataset.numInstances() * arg1 * arg2; // repeats * folds * data set size
		case CUSTOMHOLDOUT: // by default only one repeat
			return dataset.numInstances();
		case BOOTSTRAP:
			return dataset.numInstances() * 2 * arg1; // 2 * dataset size * repeats (resampled dataset = training set, all instances in test set)
		default:
			throw new RuntimeException("Illigal evaluationMethod (EvaluationMethod::getSplitSize)");
		}
	}
	
	public String toString() {
		switch( em ) {
			case CROSSVALIDATION:
				return estimationProceduresTxt[0] + "_" + arg1 + "_" + arg2;
			case LEAVEONEOUT:
				return estimationProceduresTxt[1];
			case HOLDOUT:
				return estimationProceduresTxt[2] + "_" + arg1 + "_" + arg2;
			case LEARNINGCURVE:
				return estimationProceduresTxt[3] + "_" + arg1 + "_" + arg2;
			case HOLDOUT_UNLABELED:
				return estimationProceduresTxt[4];
			case CUSTOMHOLDOUT:
				return estimationProceduresTxt[5];
			case BOOTSTRAP:
				return estimationProceduresTxt[6] + "_" + arg1;
			default:
				throw new RuntimeException("Illigal evaluationMethod (EvaluationMethod::toString)");
		}
	}
}
