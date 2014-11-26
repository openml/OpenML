package org.openml.learningcurves.data;

import java.util.HashMap;
import java.util.Map;

import org.openml.apiconnector.algorithms.Conversion;

public class Distance {
	// for each setup, the distance between two data sets
	private final Map<Integer, Map<Integer, Map<Integer, double[]>>> distances;
	
	public Distance( Map<Integer, Map<Integer, Map<Integer, Evaluation>>> setup_oriented ) {
		Conversion.log("OK","Distance","Starting to create Task Distance Matrix");
		distances = new HashMap<Integer, Map<Integer,Map<Integer,double[]>>>();
		for( Integer setup_id : setup_oriented.keySet() ) {
			distances.put(setup_id, new HashMap<Integer, Map<Integer, double[]>>() );
			
			for( Integer task_idA : setup_oriented.get(setup_id).keySet() ) {
				int task_idASamples = setup_oriented.get(setup_id).get(task_idA).size();
				distances.get(setup_id).put(task_idA, new HashMap<Integer, double[]>() );
				
				for( Integer task_idB : setup_oriented.get(setup_id).keySet() ) {
					int task_idBSamples = setup_oriented.get(setup_id).get(task_idB).size();
					int samples = Math.min(task_idASamples, task_idBSamples);
					
					double[] current = new double[samples];
					for( int i = 1; i < samples; ++i ) {
						current[i] = Math.abs( 
							setup_oriented.get(setup_id).get(task_idA).get(i).getAccuracy() - 
							setup_oriented.get(setup_id).get(task_idB).get(i).getAccuracy() 
						);
						
						if( i > 0 ) {
							current[i] += current[i-1];
						}
					}
					
					distances.get(setup_id).get(task_idA).put(task_idB, current);
				}
			}
		}
		Conversion.log("OK","Distance","Done creating Task Distance Matrix");
	}
	
	public double distance( int taskA, int taskB, int setupP, int setupQ, int samplesP, int samplesQ ) throws Exception {
		if( distances.containsKey(setupP) == false ) throw new Exception("Setup not present: " + setupP);
		if( distances.containsKey(setupQ) == false ) throw new Exception("Setup not present: " + setupQ);
		if( distances.get(setupP).containsKey(taskA) == false ) throw new Exception("Setup does not contain task: " + setupP + ", " + taskA );
		if( distances.get(setupP).containsKey(taskB) == false ) throw new Exception("Setup does not contain task: " + setupP + ", " + taskB );
		if( distances.get(setupQ).containsKey(taskA) == false ) throw new Exception("Setup does not contain task: " + setupQ + ", " + taskA );
		if( distances.get(setupQ).containsKey(taskB) == false ) throw new Exception("Setup does not contain task: " + setupQ + ", " + taskB );
		
		double distance = 0;
		distance += distances.get(setupP).get(taskA).get(taskB)[samplesP];
		distance += distances.get(setupQ).get(taskA).get(taskB)[samplesQ];
		return distance;
	}
}
