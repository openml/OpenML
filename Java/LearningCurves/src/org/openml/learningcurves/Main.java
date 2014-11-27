package org.openml.learningcurves;

import java.util.List;
import java.util.Map;

import org.openml.learningcurves.data.DataLoader;
import org.openml.learningcurves.data.DataUtils;
import org.openml.learningcurves.data.CurvesDistance;
import org.openml.learningcurves.data.Evaluation;


public class Main {
	public static final int SAMPLE_IDX = 6;
	
	public static void main( String[] args ) throws Exception {
		DataLoader dl = new DataLoader("/scratch/rijnjnvan/curves/meta_curves.csv");
		DataUtils du = new DataUtils(dl);
		
		Map<Integer, Map<Integer, Map<Integer, Evaluation>>> taskOriented = dl.getTaskOriented();
		Map<Integer, Map<Integer, Map<Integer, Evaluation>>> setupOriented = dl.getSetupOriented();
		CurvesDistance cd = new CurvesDistance(setupOriented);
		
		int correct = 0;
		int total = 0;
		
		for( Integer task_id : taskOriented.keySet() ) {
			for( Integer Ap : setupOriented.keySet() ) {
				for( Integer Aq : setupOriented.keySet() ) {
					if( Ap == Aq ) continue;
					
					List<Integer> nearestTasks = cd.nearest(task_id, Ap, Aq, SAMPLE_IDX, 1);
					int totalSamples = dl.taskSamples(task_id);
					int votesP = 0;
					int votesQ = 0;
					double scoreP = taskOriented.get( task_id ).get( Ap ).get( totalSamples ).getAccuracy();
					double scoreQ = taskOriented.get( task_id ).get( Aq ).get( totalSamples ).getAccuracy();
					
					for( Integer nearestTask : nearestTasks ) {
						double estimationP = du.coefficient(task_id, nearestTask, Ap, SAMPLE_IDX );
						double estimationQ = du.coefficient(task_id, nearestTask, Aq, SAMPLE_IDX );
						
						if( estimationP > estimationQ ) { 
							votesP++; 
						} else {
							votesQ++;
						}
					}
					
					total += 1;
					if( (votesP > votesQ && scoreP > scoreQ) || (votesP < votesQ && scoreP < scoreQ) ) {
						correct += 1;
					}
				}
			}
		}
		System.out.println( "Total: " + total + "; correct: " + correct ); 
		
	}
	
	
	/*private static void print( Map<Integer, Map<Integer, Map<Integer, Evaluation>>> taskOriented ) {
		for( Integer task : taskOriented.keySet() ) {
			System.out.println( "Task " + task );
			for( Integer setup : taskOriented.get(task).keySet() ) {
				System.out.println( "- Setup " + setup );
				for( Integer sample : taskOriented.get(task).get(setup).keySet() ) {
					System.out.println( "--- Sample " + sample + ": " + taskOriented.get(task).get(setup).get(sample) );
				}
			}
		}
	}*/
}
