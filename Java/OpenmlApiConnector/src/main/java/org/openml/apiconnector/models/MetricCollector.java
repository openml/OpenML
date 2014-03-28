/*
 *  OpenmlApiConnector - Java integration of the OpenML Web API
 *  Copyright (C) 2014 
 *  @author Jan N. van Rijn (j.n.van.rijn@liacs.leidenuniv.nl)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
package org.openml.apiconnector.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MetricCollector {

	private Map<Metric, ArrayList<MetricScore>> metrics;
	
	/**
	 * Default Constructor. Bundles the scores of evaluation measures in one class.
	 * All the scores of the same measure are stored in an arraylist, so that we can
	 * calculate the standard deviation of it. 
	 */
	public MetricCollector() {
		metrics = new HashMap<Metric, ArrayList<MetricScore>>();
	}
	
	/**
	 * @param metricMap - A map containing metrics and their respective scores.
	 */
	public void add( Map<Metric, MetricScore> metricMap ) {
		for( Metric m : metricMap.keySet() ) {
			if( metrics.containsKey(m) == false ) {
				
				metrics.put(m, new ArrayList<MetricScore>());
			}
			metrics.get(m).add( metricMap.get(m) );
		}
	}
	
	/**
	 * @param m - The measure of which the scores should be returned
	 * @return An ArrayList containing all the scores of measure m
	 */
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
