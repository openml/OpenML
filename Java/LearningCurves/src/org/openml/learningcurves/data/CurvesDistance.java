package org.openml.learningcurves.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openml.apiconnector.algorithms.Conversion;

public class CurvesDistance {
	// for each setup, the distance between two data set
	// setup_id, task_id, task_id, distance array (per sample) 
	private final Map<Integer, Map<Integer, Map<Integer, double[]>>> distances;
	
	private final Set<Integer> task_ids;
	
	public CurvesDistance( Map<Integer, Map<Integer, Map<Integer, Evaluation>>> setup_oriented ) {
		Conversion.log("OK","Distance","Starting to create Task Distance Matrix");
		distances = new HashMap<Integer, Map<Integer,Map<Integer,double[]>>>();
		task_ids = setup_oriented.get( setup_oriented.keySet().iterator().next() ).keySet();
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
	
	/*public double distance( int taskA, int taskB, int setupP, int setupQ, int samplesP, int samplesQ ) throws Exception {
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
	}*/
	
	public List<Integer> nearest( int task, int setupP, int setupQ, int sampleSize, int k ) {
		Map<Integer, Double> tasks = new HashMap<>();
		List<Integer> result = new ArrayList<>();
		
		for( Integer task_id : task_ids ) {
			if( task_id == task ) { continue; }
			
			double distance = distances.get(setupP).get(task).get(task_id)[sampleSize] + distances.get(setupQ).get(task).get(task_id)[sampleSize];
			tasks.put(task_id, distance);
		}
		
		for( int i = 0; i < k; ++i ) {
			double min = Double.MAX_VALUE;
			int index = -1;
			for( Integer task_id : tasks.keySet() ) {
				if( tasks.get( task_id ) < min ) {
					min = tasks.get( task_id );
					index = task_id;
				}
			}
			result.add(index);
			tasks.remove(index);
		}
		
		return result;
	}
	
	public List<Integer> nearest( int task, int setup, int sampleSize, int k ) {
		Map<Integer, Double> tasks = new HashMap<>();
		List<Integer> result = new ArrayList<>();
		
		for( Integer task_id : task_ids ) {
			if( task_id == task ) { continue; }
			
			double distance = distances.get(setup).get(task).get(task_id)[sampleSize];
			tasks.put(task_id, distance);
		}
		
		for( int i = 0; i < k; ++i ) {
			double min = Double.MAX_VALUE;
			int index = -1;
			for( Integer task_id : tasks.keySet() ) {
				if( tasks.get( task_id ) < min ) {
					min = tasks.get( task_id );
					index = task_id;
				}
			}
			result.add(index);
			tasks.remove(index);
		}
		
		return result;
	}
}
