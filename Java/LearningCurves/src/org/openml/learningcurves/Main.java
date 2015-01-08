package org.openml.learningcurves;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openml.learningcurves.data.DataLoader;
import org.openml.learningcurves.data.DataUtils;
import org.openml.learningcurves.data.CurvesDistance;
import org.openml.learningcurves.data.Evaluation;


public class Main {
	public static final int SAMPLE_IDX = 6;
	public static final int NEAREST_TASKS = 3;
	
	public static void main( String[] args ) throws Exception {
		DataLoader dl = new DataLoader("data/meta_curves.csv");
		DataUtils du = new DataUtils(dl);
		
		Map<Integer, Map<Integer, Map<Integer, Evaluation>>> taskOriented = dl.getTaskOriented();
		Map<Integer, Map<Integer, Map<Integer, Evaluation>>> setupOriented = dl.getSetupOriented();
		Map<Integer, Integer> setupBasedCorrect = new HashMap<Integer, Integer>();
		Map<Integer, Integer> taskBasedCorrect = new HashMap<Integer, Integer>();
		CurvesDistance cd = new CurvesDistance(setupOriented);
		
		int correct = 0;
		int total = 0;
		int majority_score = 0;

		
		for( Integer task_id : taskOriented.keySet() ) {
			System.out.println( "Evaluating task: " + task_id );
			
			if( taskBasedCorrect.containsKey( task_id ) == false ) {
				taskBasedCorrect.put( task_id, 0 );
			}
			
			PairedRanking pr = new PairedRanking( dl.getTaskSetupFoldResults().get( task_id ) );
			//System.out.println( "Partial ordering: " + pr.partialOrdering() );
			//System.out.println( "Real ordering   : " + pr.accuracyOrdering() );
			
			int currentBestAlgorithm = -1;
			
			// pick each algorithm
			for( Integer Ap : setupOriented.keySet() ) {
				if( currentBestAlgorithm == -1 ) { currentBestAlgorithm = Ap; }
				
				// pairwise compare it to all other algorithms
				for( Integer Aq : setupOriented.keySet() ) {
					if( Ap == Aq ) continue;
					
					// obtain ground truth
					double scoreP = taskOriented.get( task_id ).get( Ap ).get( dl.taskSamples(task_id) ).getAccuracy();
					double scoreQ = taskOriented.get( task_id ).get( Aq ).get( dl.taskSamples(task_id) ).getAccuracy();
					
					// book keeping for evaluation results
					if( setupBasedCorrect.containsKey( Ap ) == false ) {
						setupBasedCorrect.put( Ap, 0 );
					}
					if( setupBasedCorrect.containsKey( Aq ) == false ) {
						setupBasedCorrect.put( Aq, 0 );
					}
					
					// score book keeping
					int votesP = 0;
					int votesQ = 0;
					
					// identify nearest tasks
					List<Integer> nearestTasks = cd.nearest(task_id, Ap, Aq, SAMPLE_IDX, NEAREST_TASKS);
					
					for( Integer nearestTask : nearestTasks ) {
						// obtain the scores on the nearest task
						double accuracyP = taskOriented.get( nearestTask ).get( Ap ).get( dl.taskSamples(nearestTask) ).getAccuracy();
						double accuracyQ = taskOriented.get( nearestTask ).get( Aq ).get( dl.taskSamples(nearestTask) ).getAccuracy();
						
						// adapt learning curves to the original curve
						double coefficientP = du.coefficient(task_id, nearestTask, Ap, SAMPLE_IDX );
						double coefficientQ = du.coefficient(task_id, nearestTask, Aq, SAMPLE_IDX );
						
						// new score
						double estimationP = coefficientP * accuracyP;
						double estimationQ = coefficientQ * accuracyQ;
						
						if( estimationP >= estimationQ ) { 
							votesP++; 
						} else {
							votesQ++;
						}
					}
					
					// update scores based on the votes
					total += 1;
					if( (votesP > votesQ && scoreP > scoreQ) || (votesP < votesQ && scoreP < scoreQ) ) {
						correct += 1;
						
						setupBasedCorrect.put( Ap, setupBasedCorrect.get(Ap) + 1 );
						setupBasedCorrect.put( Aq, setupBasedCorrect.get(Aq) + 1 );
						taskBasedCorrect.put( task_id, taskBasedCorrect.get(task_id) + 1 );
					}
					
					// update current best algorithm
					if( Ap == currentBestAlgorithm && votesQ > votesP ) {
						currentBestAlgorithm = Aq;
					} else if( Aq == currentBestAlgorithm && votesP > votesQ ) {
						currentBestAlgorithm = Ap;
					}
					
					// now check what an algorithm predicting majority class would output
					if( majorityClass( task_id, Ap, Aq, taskOriented, dl.taskSamples(task_id) ) == (scoreP >= scoreQ) )
					{ 
						majority_score += 1;
					}
				}
			}
			System.out.println("Predicted algorithm: " + currentBestAlgorithm );
		}
		System.out.println( "Total: " + total + "; correct: " + correct + "; majority class score: " + majority_score ); 
	}
	
	public static boolean majorityClass( int task_id, int setupTest, int setupCompatitor, Map<Integer, Map<Integer, Map<Integer, Evaluation>>> taskOriented, int task_samples ) {
		int winTest = 0;
		double evaluations = taskOriented.keySet().size() - 1;
		for( Integer evaluate_task_id : taskOriented.keySet() ) {
			if( evaluate_task_id == task_id ) continue;
			double scoreTest = taskOriented.get( evaluate_task_id ).get( setupTest ).get( task_samples ).getAccuracy();
			double scoreComp = taskOriented.get( evaluate_task_id ).get( setupCompatitor ).get( task_samples ).getAccuracy();
			
			if( scoreTest > scoreComp ) {
				winTest += 1;
			}
		}
		
		return winTest >= evaluations / 2;
	}
}
