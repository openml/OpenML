package org.openml.learningcurves.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openml.learningcurves.data.DataLoader;
import org.openml.learningcurves.data.DataUtils;
import org.openml.learningcurves.data.Evaluation;
import org.openml.learningcurves.data.PairedRanking;
import org.openml.learningcurves.utils.OrderedMap;

public class BestAlgorithmOriginal implements CurvesExperimentFull {
	
	private static final String EXPERIMENT_NAME = "Best algorithm (Original)";
	
	public final int SAMPLE_IDX;
	public final int NEAREST_TASKS;

	private final DataLoader dl;
	private final DataUtils du;
	
	private final Map<Integer, Map<Integer, Map<Integer, Evaluation>>> taskOriented;
	private final Map<Integer, Map<Integer, Map<Integer, Evaluation>>> setupOriented;
	
	private final Map<Integer, List<Double>> losscurves;
	private int tasksCorrect = 0;
	private int tasksTotal = 0;
	
	public BestAlgorithmOriginal( DataLoader dl, int sampleIdx, int nearestTasks ) {
		this.dl = dl;
		this.du = new DataUtils(dl);
		
		SAMPLE_IDX = sampleIdx;
		NEAREST_TASKS = nearestTasks;
		
		// book keeping
		this.taskOriented = dl.getTaskOriented();
		this.setupOriented = dl.getSetupOriented();
		
		// scores
		losscurves = new HashMap<Integer, List<Double>>();
	}
	
	public void allTasks() {
		for( Integer task_id : taskOriented.keySet() ) {
			singleTask( task_id );
		}
	}
	
	public void singleTask( int task_id ) {
		PairedRanking pr = new PairedRanking( dl.getTaskSetupFoldResults().get( task_id ) );
		Map<Integer, List<Integer>> partialOrdering = pr.partialOrdering();
		OrderedMap accuracyOrdering = pr.accuracyOrdering();
		
		losscurves.put( task_id, new ArrayList<Double>() );
		List<Integer> eliminatedAlgorithms = new ArrayList<>();
		
		int currentBest = findBestAlgorithm(task_id, eliminatedAlgorithms);
		eliminatedAlgorithms.add( currentBest );
		double currentLoss = accuracyOrdering.getValueByRank( accuracyOrdering.size() - 1 ) - accuracyOrdering.getValueByKey( currentBest );
		losscurves.get( task_id ).add( currentLoss );
		
		while( currentLoss > 0 ) {
			int currentAttempt = findBestAlgorithm(task_id, eliminatedAlgorithms);
			eliminatedAlgorithms.add( currentAttempt );
			
			double updatedLoss = accuracyOrdering.getValueByRank( accuracyOrdering.size() - 1 ) - accuracyOrdering.getValueByKey( currentAttempt );
			if( updatedLoss < currentLoss ) {
				currentLoss = updatedLoss;
			}
			
			losscurves.get( task_id ).add( currentLoss );
		}
		
		tasksTotal += 1;
		if( partialOrdering.get( 0 ).contains( currentBest ) ) {
			tasksCorrect += 1;
		}
		
	}
	
	public String result() {
		StringBuilder sb = new StringBuilder();
		sb.append( EXPERIMENT_NAME + "\n" );
		sb.append( SAMPLE_IDX + " samples, " + NEAREST_TASKS + " nearest tasks\n" );
		sb.append( "Total: " + tasksTotal + "; correct: " + tasksCorrect );
		return sb.toString();
	}
	
	public List<Double> lossCurve() {
		List<Double> averageLossCurve = new ArrayList<Double>();
		
		for( Integer task_id : losscurves.keySet() ) {
			for( int i = 0; i < losscurves.get(task_id).size(); ++i  ) {
				if( averageLossCurve.size() <= i ) {
					averageLossCurve.add( losscurves.get(task_id).get(i) );
				} else {
					averageLossCurve.set( i, averageLossCurve.get(i) + losscurves.get(task_id).get(i) );
				}
			}
		}
		
		for( int i = 0; i < averageLossCurve.size(); ++i ) {
			averageLossCurve.set( i, averageLossCurve.get(i) / losscurves.keySet().size() );
		}
		
		return averageLossCurve;
	}
	
	private int findBestAlgorithm( int task_id, List<Integer> eliminatedAlgorithms ) {
		int currentBest = -1;
		for( Integer competitor : setupOriented.keySet() ) {
			// ignore eliminated algorithms
			if( eliminatedAlgorithms.contains( competitor ) ) { continue; }
			
			// if needed, choose a first "current best"
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
				int taskSample = dl.taskSamples(nearestTask) - 1;
				double accuracyBest = taskOriented.get( nearestTask ).get( currentBest ).get( taskSample ).getAccuracy();
				double accuracyComp = taskOriented.get( nearestTask ).get( competitor  ).get( taskSample ).getAccuracy();
				
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
		return currentBest;
	}
}
