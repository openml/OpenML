package org.openml.learningcurves.data;

import java.util.Map;

public class DataUtils {

	// task oriented data: task_id, setup_id, sample, Evaluation
	private final Map<Integer, Map<Integer, Map<Integer, Evaluation>>> task_oriented;
	// setup oriented data: setup_id, task_id, sample, Evaluation
	//private final Map<Integer, Map<Integer, Map<Integer, Evaluation>>> setup_oriented;
	
	public DataUtils( DataLoader dl ) {
		task_oriented = dl.getTaskOriented();
		//setup_oriented = dl.getSetupOriented();
	}
	
	public double coefficient( int taskI, int taskR, int setupP, int numSamples ) {
		double nominator = 0;
		double denominator = 0;
		
		for( int i = 0; i < numSamples; ++i ) {
			nominator += 
					task_oriented.get( taskI ).get( setupP ).get( i ).getAccuracy() * 
					task_oriented.get( taskR ).get( setupP ).get( i ).getAccuracy() * 
					Math.pow(sample_size(i), 2);
			denominator += 
					Math.pow( task_oriented.get( taskR ).get( setupP ).get( i ).getAccuracy(), 2) * 
					Math.pow(sample_size(i), 2);
		}
		
		return nominator / denominator;
	}
	
	public static int sample_size( int index ) {
		return (int) Math.floor( Math.pow(2, 6 + (0.5 * index)) );
	}
}
