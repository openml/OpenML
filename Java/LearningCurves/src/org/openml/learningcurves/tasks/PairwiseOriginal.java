package org.openml.learningcurves.tasks;

import java.util.List;
import java.util.Map;

import org.openml.learningcurves.data.DataLoader;
import org.openml.learningcurves.data.DataUtils;
import org.openml.learningcurves.data.Evaluation;

public class PairwiseOriginal implements CurvesExperiment {

	private static final String EXPERIMENT_NAME = "Pairwise Predictions (Original)";
	
	public final int SAMPLE_IDX;
	public final int NEAREST_TASKS;

	private final DataLoader dl;
	private final DataUtils du;
	
	private final Map<Integer, Map<Integer, Map<Integer, Evaluation>>> taskOriented;
	private final Map<Integer, Map<Integer, Map<Integer, Evaluation>>> setupOriented;
	
	private int pairwiseCorrect = 0;
	private int pairwiseTotal = 0;
	
	public PairwiseOriginal( DataLoader dl, int sampleIdx, int nearestTasks ) {
		this.dl = dl;
		this.du = new DataUtils(dl);
		
		SAMPLE_IDX = sampleIdx;
		NEAREST_TASKS = nearestTasks;
		
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
				
				// score book keeping
				int votesP = 0;
				int votesQ = 0;
				
				// identify nearest tasks
				List<Integer> nearestTasks = dl.getCd().nearest(task_id, Ap, Aq, SAMPLE_IDX, NEAREST_TASKS);
				
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
		sb.append( SAMPLE_IDX + " samples, " + NEAREST_TASKS + " nearest tasks\n" );
		sb.append( "Total: " + pairwiseTotal + "; correct: " + pairwiseCorrect );
		return sb.toString();
	}
}
