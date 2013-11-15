package org.openml.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MetricCollector {

	private Map<Metric, ArrayList<Double>> metrics;
	
	public MetricCollector() {
		metrics = new HashMap<Metric, ArrayList<Double>>();
	}
	
	public void add( Map<Metric, Double> metricMap ) {
		for( Metric m : metricMap.keySet() ) {
			if( metrics.containsKey(m) == false ) {
				
				metrics.put(m, new ArrayList<Double>());
			}
			metrics.get(m).add( metricMap.get(m) );
		}
	}
	
	public ArrayList<Double> get( Metric m ) {
		return metrics.get( m );
	}
	
	@Override
	public String toString() {
		return metrics.toString();
	}
}
