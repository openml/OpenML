package org.openml.learningcurves.experiments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openml.learningcurves.data.CurvesDistance;
import org.openml.learningcurves.data.DataLoader;
import org.openml.learningcurves.data.DataUtils;
import org.openml.learningcurves.data.Evaluation;

public class PairwiseRegressionBased implements CurvesExperiment {
	private static final String EXPERIMENT_NAME = "Pairwise Predictions (Regression based)";
	
	public final int SAMPLE_IDX;
	public final int NEAREST_TASKS;

	private final DataLoader dl;
	private final DataUtils du;
	private final CurvesDistance cd;
	
	private final Map<Integer, Map<Integer, Map<Integer, Evaluation>>> taskOriented;
	private final Map<Integer, Map<Integer, Map<Integer, Evaluation>>> setupOriented;
	
	private int pairwiseCorrect = 0;
	private int pairwiseTotal = 0;
	
	public PairwiseRegressionBased( DataLoader dl, int sampleIdx, int nearestTasks ) {
		this.dl = dl;
		this.du = new DataUtils(dl);
		
		SAMPLE_IDX = sampleIdx;
		NEAREST_TASKS = nearestTasks;
		
		// book keeping
		this.taskOriented = dl.getTaskOriented();
		this.setupOriented = dl.getSetupOriented();
		this.cd = new CurvesDistance(setupOriented);
	}
	
	public void allTasks() {
		for( Integer task_id : taskOriented.keySet() ) {
			singleTask( task_id );
		}
	}
	
	public void singleTask( int task_id ) {
		Map<Integer, Double> predictedScores = new HashMap<Integer, Double>();
		
		// prepare all the estimated regression scores
		for( int setupId : setupOriented.keySet() ) {
			// identify nearest tasks
			List<Integer> nearestTasks = cd.nearest(task_id, setupId, SAMPLE_IDX, NEAREST_TASKS);
			
			for( Integer nearestTask : nearestTasks ) {
				// accuracy on retrieved task
				double accuracy = taskOriented.get( nearestTask ).get( setupId ).get( dl.taskSamples(nearestTask) ).getAccuracy();
				
				// adapt learning curve to the original curve
				double coefficient = du.coefficient(task_id, nearestTask, setupId, SAMPLE_IDX );
				
				// new score
				double estimation = coefficient * accuracy;
				
				predictedScores.put(setupId, estimation);
			}
		}
		
		// now establish performance on the pairwise tasks
		for( int setupP : setupOriented.keySet() ) {
			for( int setupQ : setupOriented.keySet() ) {
				if( setupP == setupQ ) { continue; }
				
				double scoreP = taskOriented.get( task_id ).get( setupP ).get( dl.taskSamples(task_id) ).getAccuracy();
				double scoreQ = taskOriented.get( task_id ).get( setupQ ).get( dl.taskSamples(task_id) ).getAccuracy();
				
				pairwiseTotal += 1;
				if( (scoreP > scoreQ) == (predictedScores.get(setupP) > predictedScores.get(setupQ) ) ) {
					pairwiseCorrect += 1;
				}
			}
		}
	}
	
	public String result() {
		return EXPERIMENT_NAME + "\n" + "Total: " + pairwiseTotal + "; correct: " + pairwiseCorrect;
	}
}
