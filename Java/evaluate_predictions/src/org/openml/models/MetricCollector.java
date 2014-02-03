package org.openml.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MetricCollector {

	private Map<Metric, ArrayList<MetricScore>> metrics;
	
	public MetricCollector() {
		metrics = new HashMap<Metric, ArrayList<MetricScore>>();
	}
	
	public void add( Map<Metric, MetricScore> metricMap ) {
		for( Metric m : metricMap.keySet() ) {
			if( metrics.containsKey(m) == false ) {
				
				metrics.put(m, new ArrayList<MetricScore>());
			}
			metrics.get(m).add( metricMap.get(m) );
		}
	}
	
	public ArrayList<Double> getScores( Metric m ) {
		ArrayList<MetricScore> metricscores = metrics.get( m );
		ArrayList<Double> scores = new ArrayList<Double>();
		for( MetricScore s : metricscores ) {
			if( s.getScore() != null )
				scores.add( s.getScore() );
		}
		return scores;
	}
	
	@Override
	public String toString() {
		return metrics.toString();
	}
}
