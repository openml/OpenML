package org.openml.learningcurves.tasks;

import java.util.Map;

import org.openml.learningcurves.data.DataLoader;
import org.openml.learningcurves.data.Evaluation;

public class PairwiseMajorityClass implements CurvesExperiment {

	private static final String EXPERIMENT_NAME = "Pairwise Predictions (Majority Class)";

	private final DataLoader dl;
	
	private final Map<Integer, Map<Integer, Map<Integer, Evaluation>>> taskOriented;
	private final Map<Integer, Map<Integer, Map<Integer, Evaluation>>> setupOriented;
	
	private int pairwiseCorrect = 0;
	private int pairwiseTotal = 0;
	
	public PairwiseMajorityClass( DataLoader dl ) {
		this.dl = dl;
		
		// book keeping
		this.taskOriented = dl.getTaskOriented();
		this.setupOriented = dl.getSetupOriented();
	}
	
	public void allTasks() {
		for( Integer task_id : taskOriented.keySet() ) {
			singleTask( task_id );
		}
	}
	
	public void singleTask( int task_id ) {
		for( Integer Ap : setupOriented.keySet() ) {
			for( Integer Aq : setupOriented.keySet() ) {
				if( Ap == Aq ) { continue; }
				
				double scoreP = taskOriented.get( task_id ).get( Ap ).get( dl.taskSamples(task_id) ).getAccuracy();
				double scoreQ = taskOriented.get( task_id ).get( Aq ).get( dl.taskSamples(task_id) ).getAccuracy();
				
				int votesP = 0;
				int votesQ = 0;
				
				for( Integer evaluate_task_id : taskOriented.keySet() ) {
					if( evaluate_task_id == task_id ) continue;
					double currentScoreP = taskOriented.get( evaluate_task_id ).get( Ap ).get( dl.taskSamples(evaluate_task_id) ).getAccuracy();
					double currentScoreQ = taskOriented.get( evaluate_task_id ).get( Aq ).get( dl.taskSamples(evaluate_task_id) ).getAccuracy();
					
					if( currentScoreP >= currentScoreQ ) {
						votesP += 1;
					} else {
						votesQ += 1;
					}
				}
				
				// update scores based on the votes
				pairwiseTotal += 1;
				if( (votesP > votesQ && scoreP > scoreQ) || (votesP < votesQ && scoreP < scoreQ) ) {
					pairwiseCorrect += 1;
				}
			}
		}
	}
	
	public String result() {
		StringBuilder sb = new StringBuilder();
		sb.append( EXPERIMENT_NAME + "\n" );
		sb.append( "Total: " + pairwiseTotal + "; correct: " + pairwiseCorrect );
		return sb.toString();
	}
}
