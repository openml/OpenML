package org.openml.learningcurves.tasks;

import java.util.List;
import java.util.Map;

import org.openml.learningcurves.data.DataLoader;
import org.openml.learningcurves.data.DataUtils;
import org.openml.learningcurves.data.Evaluation;

public class BestAlgorithmOriginal implements CurvesExperiment {
	
	private static final String EXPERIMENT_NAME = "Best algorithm (Original)";
	
	public final int SAMPLE_IDX;
	public final int NEAREST_TASKS;

	private final DataLoader dl;
	private final DataUtils du;
	
	private final Map<Integer, Map<Integer, Map<Integer, Evaluation>>> taskOriented;
	private final Map<Integer, Map<Integer, Map<Integer, Evaluation>>> setupOriented;
	
	public BestAlgorithmOriginal( DataLoader dl, int sampleIdx, int nearestTasks ) {
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
		System.out.print( "Task " + task_id + ": " );
		int currentBest = -1;
		
		for( Integer competitor : setupOriented.keySet() ) {
			if( currentBest == -1 ) {
				currentBest = competitor;
				continue;
			}
			
			// score book keeping
			int votesBest = 0;
			int votesComp = 0;
			
			// identify nearest tasks
			List<Integer> nearestTasks = dl.getCd().nearest(task_id, currentBest, competitor, SAMPLE_IDX, NEAREST_TASKS);
			
			for( Integer nearestTask : nearestTasks ) {
				// obtain the scores on the nearest task
				double accuracyBest = taskOriented.get( nearestTask ).get( currentBest ).get( dl.taskSamples(nearestTask) ).getAccuracy();
				double accuracyComp = taskOriented.get( nearestTask ).get( competitor  ).get( dl.taskSamples(nearestTask) ).getAccuracy();
				
				// adapt learning curves to the original curve
				double coefficientBest = du.coefficient(task_id, nearestTask, currentBest, SAMPLE_IDX );
				double coefficientComp = du.coefficient(task_id, nearestTask, competitor , SAMPLE_IDX );
				
				// new score
				double estimationBest = coefficientBest * accuracyBest;
				double estimationComp = coefficientComp * accuracyComp;
				
				if( estimationBest >= estimationComp ) { 
					votesBest++; 
				} else {
					votesComp++;
				}
			}
			
			if( votesComp > votesBest ) {
				currentBest = competitor;
			}
		}
		System.out.println( "predicted setup " + currentBest );
	}
	
	
	public String result() {
		return EXPERIMENT_NAME + "\nunimplemented";
	}
}
