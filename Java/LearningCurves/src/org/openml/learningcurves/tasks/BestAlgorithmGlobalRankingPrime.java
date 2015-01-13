package org.openml.learningcurves.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openml.learningcurves.data.DataLoader;
import org.openml.learningcurves.data.PairedRanking;
import org.openml.learningcurves.utils.OrderedMap;

public class BestAlgorithmGlobalRankingPrime extends BestAlgorithmGlobalRanking {
	
	public BestAlgorithmGlobalRankingPrime(DataLoader dl) {
		super(dl);
	}

	protected static final String EXPERIMENT_NAME = "Best algorithm (Global Ranking Prime)";
	
	public void singleTask( int task_id ) {
		
		// setup, rank
		Map<Integer, Integer> accumulatedRank = new HashMap<>();
		
		// first calculate accumulated ranking
		for( int otherTask : taskOriented.keySet() ) {
			if( otherTask == task_id ) { continue; }
			
			OrderedMap orderedmap = new OrderedMap();

			int sampleNr = Math.min( dl.taskSamples(task_id), dl.taskSamples( otherTask ) - 1 );
			
			
			for( int setup_id : setupOriented.keySet() ) {
				orderedmap.put( setup_id, taskOriented.get( otherTask ).get( setup_id ).get( sampleNr ).getAccuracy() );
			}
			
			for( int setup_id : setupOriented.keySet() ) {
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
			globalRanking.put( setup, accumulatedRank.get( setup ) / ((taskOriented.keySet().size() - 1) * 1.0) );
		}
		
		globalRankings.put( task_id, globalRanking );
		
		
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
		for( int i = 1; currentLoss > 0 && globalRanking.size() > 0; ++i ) {
			int currentAttempt = globalRanking.getKeyByRank( i );
			
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
		sb.append( "Total: " + tasksTotal + "; correct: " + tasksCorrect );
		return sb.toString();
	}
}
