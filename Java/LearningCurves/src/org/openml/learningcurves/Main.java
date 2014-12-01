package org.openml.learningcurves;

import java.util.ArrayList;
import java.util.HashMap;
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
		Map<Integer, Map<Integer, Integer>> setupBasedCorrect = new HashMap<Integer, Map<Integer,Integer>>();
		Map<Integer, Map<Integer, Integer>> taskBasedCorrect = new HashMap<Integer, Map<Integer,Integer>>();
		CurvesDistance cd = new CurvesDistance(setupOriented);
		
		int correct = 0;
		int total = 0;
		int majority_class = 0;
		
		for( Integer Ap : setupOriented.keySet() ) {
			for( Integer Aq : setupOriented.keySet() ) {
				if( Ap == Aq ) continue;
				
				if (setupBasedCorrect.containsKey(Ap) == false) {
					setupBasedCorrect.put(Ap, new HashMap<Integer, Integer>());
				}
				if (setupBasedCorrect.get(Ap).containsKey(Aq) == false) {
					setupBasedCorrect.get(Ap).put(Aq, 0);
				}
				
				if (setupBasedCorrect.containsKey(Aq) == false) {
					setupBasedCorrect.put(Aq, new HashMap<Integer, Integer>());
				}
				if (setupBasedCorrect.get(Aq).containsKey(Ap) == false) {
					setupBasedCorrect.get(Aq).put(Ap, 0);
				}
				
				

				int setup_total = 0;
				int setup_correct = 0;
				int setup_winP = 0;
				
				for( Integer task_id : taskOriented.keySet() ) {
					setup_total += 1;
					
					
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
						setup_correct += 1;
						setupBasedCorrect.get(Ap).put( Aq, setupBasedCorrect.get(Ap).get(Aq) + 1 );
						setupBasedCorrect.get(Aq).put( Ap, setupBasedCorrect.get(Aq).get(Ap) + 1 );
					}
					
					if( scoreP > scoreQ ) {
						setup_winP += 1;
					}
				}
				int setup_majority_class = Math.max( setup_winP, setup_total - setup_winP );
				majority_class += setup_majority_class;
				//System.out.println( Ap + " Vs. " + Aq + ": " + setup_correct  + ", majority class: " + setup_majority_class );
			}
		}
		System.out.println( "Total: " + total + "; correct: " + correct + "; majority class: " + majority_class ); 
		
		List<Integer> setup_ids = new ArrayList<>(setupBasedCorrect.keySet());
		for( Integer p : setup_ids ) {
			for( Integer q : setup_ids ) {
				System.out.print( setupBasedCorrect.get(p).get(q) + " - " );
			}
			System.out.println("");
		}
		
		
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
