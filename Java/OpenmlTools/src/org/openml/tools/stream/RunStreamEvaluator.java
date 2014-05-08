package org.openml.tools.stream;

import weka.core.Instance;
import weka.core.Utils;

public class RunStreamEvaluator {
	
	private double own_score;
	private double baseline_score;
	private double min_score;
	private double max_score;
	private double processed;
	
	private int best = 0;
	private int worst = 0;
	private int better_than_baseline = 0;
	private int worse_than_baseline = 0;
	
	public RunStreamEvaluator( ) {
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

	public void addPrediction( String prediction, String baseline_prediction, Instance score_table ) {
		processed++;
		int predictionIndex = score_table.dataset().attribute( prediction ).index();
		int baselineIndex   = score_table.dataset().attribute( baseline_prediction ).index();
		
		if( Utils.isMissingValue( score_table.value( predictionIndex ) ) == false ) {
			own_score += score_table.value( predictionIndex );
		}
		baseline_score += score_table.value( baselineIndex );
		
		double min =  Double.MAX_VALUE;
		int minIndex = -1;
		double max = Double.MIN_VALUE;
		int maxIndex = -1;
		
		for( int i = 0; i < score_table.dataset().numAttributes(); ++i ) {
			if( score_table.value( i ) < min ) {
				min = score_table.value( i );
				minIndex = i;
			}
			if( score_table.value( i ) > max ) {
				max = score_table.value( i );
				maxIndex = i;
			}
		}
		min_score += min;
		max_score += max;
		if( predictionIndex == minIndex ) worst++;
		if( predictionIndex == maxIndex ) best++;
		
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
	
	@Override 
	public String toString() {
		return summary();
	}
}
