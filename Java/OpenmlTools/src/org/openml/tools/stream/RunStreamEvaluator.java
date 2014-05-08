package org.openml.tools.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.core.Instance;
import weka.core.Utils;

public class RunStreamEvaluator {
	private final List<String> legalClassifiers;
	private final Map<Integer, Map<Integer, Double>> accuracy_curve;
	private final Map<Integer, Map<Integer, Double>> baseline_curve;
	private final Map<Integer, Map<Integer, Double>> maxscore_curve;
	
	private double own_score;
	private double baseline_score;
	private double min_score;
	private double max_score;
	private double processed;
	
	private int best = 0;
	private int worst = 0;
	private int better_than_baseline = 0;
	private int worse_than_baseline = 0;
	
	public RunStreamEvaluator( String[] legalClassifiers ) {
		this.legalClassifiers = Arrays.asList( legalClassifiers );
		accuracy_curve = new HashMap<Integer, Map<Integer,Double>>();
		baseline_curve = new HashMap<Integer, Map<Integer,Double>>();
		maxscore_curve = new HashMap<Integer, Map<Integer,Double>>();
		
		own_score = 0.0;
		baseline_score = 0.0;
		min_score = 0.0;
		max_score = 0.0;
		processed = 0.0;
	}
	
	public double getOwn_score() {
		return own_score;
	}

	public double getBaseline_score() {
		return baseline_score;
	}

	public double getMin_score() {
		return min_score;
	}

	public double getMax_score() {
		return max_score;
	}

	public double getProcessed() {
		return processed;
	}

	public int getBest() {
		return best;
	}

	public int getWorst() {
		return worst;
	}

	public int getBetter_than_baseline() {
		return better_than_baseline;
	}

	public int getWorse_than_baseline() {
		return worse_than_baseline;
	}

	public void addPrediction( Integer task, Integer interval_start, String prediction, String baseline_prediction, Instance score_table ) {
		if( accuracy_curve.containsKey( task ) == false ) {	accuracy_curve.put( task, new HashMap<Integer, Double>() );	}
		if( baseline_curve.containsKey( task ) == false ) {	baseline_curve.put( task, new HashMap<Integer, Double>() );	}
		if( maxscore_curve.containsKey( task ) == false ) {	maxscore_curve.put( task, new HashMap<Integer, Double>() );	}
		
		processed++;
		int predictionIndex = score_table.dataset().attribute( prediction ).index();
		int baselineIndex   = score_table.dataset().attribute( baseline_prediction ).index();
		
		if( Utils.isMissingValue( score_table.value( predictionIndex ) ) == false ) {
			own_score += score_table.value( predictionIndex );
			accuracy_curve.get( task ).put( interval_start, score_table.value( predictionIndex ) );
		} else {
			accuracy_curve.get( task ).put( interval_start, 0.0 );
		}
		if( Utils.isMissingValue( score_table.value( baselineIndex ) ) == false ) {
			baseline_score += score_table.value( baselineIndex );
			baseline_curve.get( task ).put( interval_start, score_table.value( baselineIndex ) );
		}else {
			baseline_curve.get( task ).put( interval_start, 0.0 );
		}
		
		double max = Double.MIN_VALUE;
		int maxIndex = -1;
		String maxClassifier = null;
		
		for( int i = 0; i < score_table.dataset().numAttributes(); ++i ) {
			if( legalClassifiers.contains( score_table.dataset().attribute( i ).name() ) ) {
				if( score_table.value( i ) > max ) {
					max = score_table.value( i );
					maxIndex = i;
					maxClassifier = score_table.dataset().attribute( i ).name();
				}
			}
		}
		max_score += max;
		if( prediction.equals( maxClassifier ) ) {
			
			best++;
		} 
		
		if( Utils.isMissingValue( score_table.value( maxIndex ) ) == false ) {
			maxscore_curve.get( task ).put( interval_start, score_table.value( maxIndex ) );
		} else {
			maxscore_curve.get( task ).put( interval_start, 0.0 );
		}
		
		if( score_table.value( predictionIndex ) > score_table.value( baselineIndex ) ) {
			better_than_baseline ++;
		}
		if( score_table.value( predictionIndex ) < score_table.value( baselineIndex ) ) {
			worse_than_baseline ++;
		}
	}
	
	public String summary() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("=== CUSTOM STREAM EVALUATOR ===\n");
		sb.append("Instances: "+processed+"\n");
		sb.append("Score: "+ (own_score / processed) +"\n");
		sb.append("Base score: "+ (baseline_score / processed) +"\n");
		sb.append("Max score: "+ (max_score / processed) +"\n");
		sb.append("Min score: "+ (min_score / processed) +"\n");
		sb.append("Beaten baseline: "+ better_than_baseline +" x\n");
		sb.append("Defeated by baseline: "+ worse_than_baseline +" x\n");
		sb.append("Best value predicted: "+ best +" x\n");
		sb.append("Worst value predicted: "+ worst +" x\n");
		
		return sb.toString();
	}
	
	public String getSql( int task_id ) {
		StringBuilder sb = new StringBuilder();

		sb.append( "(" );
		sb.append( task_id + "," );
		sb.append( getCurvesForTask( task_id ) );
		sb.append( "),\n" );
		
		return sb.toString();
	}
	
	public String getCurvesForTask( int task_id ) {
		StringBuilder sb = new StringBuilder();
		ArrayList<Integer> intervals = new ArrayList<Integer>();
		intervals.addAll( accuracy_curve.get(task_id ).keySet() );
		Collections.sort( intervals );
		
		double accuracy = 0.0;
		double baseline = 0.0;
		double maxscore = 0.0;
		
		for( int i = 0; i < intervals.size(); ++i ) {
			int interval_start = intervals.get( i );
			accuracy += accuracy_curve.get( task_id ).get( interval_start );
			baseline += baseline_curve.get( task_id ).get( interval_start );
			maxscore += maxscore_curve.get( task_id ).get( interval_start );

			sb.append( interval_start + "," );
			sb.append( (accuracy / (i+1)) + "," );
			sb.append( (baseline / (i+1)) + "," );
			sb.append( (maxscore / (i+1)) + "\n" );
		}
		
		return sb.toString();
	}
	
	@Override 
	public String toString() {
		return summary();
	}
}
