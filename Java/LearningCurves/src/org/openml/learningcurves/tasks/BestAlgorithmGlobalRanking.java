package org.openml.learningcurves.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openml.learningcurves.data.DataLoader;
import org.openml.learningcurves.data.Evaluation;
import org.openml.learningcurves.data.PairedRanking;
import org.openml.learningcurves.utils.OrderedMap;

public class BestAlgorithmGlobalRanking implements CurvesExperiment {
	
	private static final String EXPERIMENT_NAME = "Best algorithm (Global Ranking)";

	private final DataLoader dl;
	
	private final Map<Integer, Map<Integer, Map<Integer, Evaluation>>> taskOriented;
	
	private final Map<Integer, List<Double>> losscurves;
	private int tasksCorrect = 0;
	private int tasksTotal = 0;
	
	public BestAlgorithmGlobalRanking( DataLoader dl ) {
		this.dl = dl;
		
		// book keeping
		this.taskOriented = dl.getTaskOriented();
		
		// scores
		losscurves = new HashMap<Integer, List<Double>>();
	}
	
	public void allTasks() {
		for( Integer task_id : taskOriented.keySet() ) {
			singleTask( task_id );
		}
	}
	
	public void singleTask( int task_id ) {
		
		// setup, rank
		Map<Integer, Integer> accumulatedRank = new HashMap<>();
		
		// first calculate accumulated ranking
		for( int otherTask : taskOriented.keySet() ) {
			if( otherTask == task_id ) { continue; }
			
			OrderedMap orderedmap = new OrderedMap();
			for( int setup_id : taskOriented.get( otherTask ).keySet() ) {
				orderedmap.put( setup_id, taskOriented.get( otherTask ).get( setup_id ).get( dl.taskSamples( otherTask ) ).getAccuracy() );
			}
			
			for( int setup_id : taskOriented.get( otherTask ).keySet() ) {
				int currentRank = orderedmap.getRankByKey( setup_id );
				
				if( accumulatedRank.containsKey( setup_id ) == false ) {
					accumulatedRank.put( setup_id, orderedmap.size() - currentRank );
				} else {
					accumulatedRank.put( setup_id, accumulatedRank.get( setup_id ) + orderedmap.size() - currentRank );
				}
			}
		}
		OrderedMap globalRanking = new OrderedMap();
		for( Integer setup : accumulatedRank.keySet() ) {
			globalRanking.put( setup, accumulatedRank.get( setup ) );
		}
		
		
		PairedRanking pr = new PairedRanking( dl.getTaskSetupFoldResults().get( task_id ) );
		Map<Integer, List<Integer>> partialOrdering = pr.partialOrdering();
		OrderedMap accuracyOrdering = pr.accuracyOrdering();
		
		losscurves.put( task_id, new ArrayList<Double>() );
		
		int currentBest = globalRanking.getKeyByRank( 0 );
		double currentLoss = accuracyOrdering.getValueByRank( accuracyOrdering.size() - 1 ) - accuracyOrdering.getValueByKey( currentBest );
		losscurves.get( task_id ).add( currentLoss );

		/*
		System.out.println( "Task " + task_id );
		System.out.println( "global ranking: " + globalRanking );
		System.out.println( "predicted: " + currentBest );
		System.out.println( "ground truth: " + pr.accuracyOrdering() );
		*/
		globalRanking.remove( currentBest );
		while( currentLoss > 0 && globalRanking.size() > 0 ) {
			int currentAttempt = globalRanking.getKeyByRank( 0 );
			
			double updatedLoss = accuracyOrdering.getValueByRank( accuracyOrdering.size() - 1 ) - accuracyOrdering.getValueByKey( currentAttempt );
			if( updatedLoss < currentLoss ) {
				currentLoss = updatedLoss;
			}
			globalRanking.remove( currentAttempt );
			
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
}
