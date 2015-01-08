package org.openml.learningcurves.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.inference.TTest;

public class PairedRanking {
	
	private static final double ALPHA = 0.05;
	private static final TTest ttester = new TTest();
	
	private final List<Integer> setups;
	private final Map<Integer, Map<Integer, Boolean>> significantlyBetter;
	private final Map<Integer, List<Double>> fold_results;
	
	public PairedRanking( Map<Integer, List<Double>> fold_results ) {
		this.fold_results = fold_results;
		significantlyBetter = new TreeMap<Integer, Map<Integer, Boolean>>();
		setups = new ArrayList<>( fold_results.keySet() );
		
		for( int setupTest: fold_results.keySet() ) {
			significantlyBetter.put( setupTest, new TreeMap<Integer, Boolean>() );
			for( int setupCompetition : fold_results.keySet() ) {
				if( setupTest == setupCompetition ) continue;
				
				// TODO: times two????
				boolean significant = mean( fold_results.get( setupTest ) ) > mean( fold_results.get( setupCompetition ) ) && ttester.tTest( doubleListToPrimitive( fold_results.get( setupTest ) ), doubleListToPrimitive( fold_results.get( setupCompetition ) ), ALPHA );
				significantlyBetter.get( setupTest ).put( setupCompetition, significant );
			}
		}
	}
	
	public Map<Integer, List<Integer>> partialOrdering() {
		Map<Integer, List<Integer>> ordering = new TreeMap<>();
		
		for( int currentSetup : setups ) {
			int currentLoses = 0;
			for( int competitors : significantlyBetter.keySet() ) {
				if( significantlyBetter.get( competitors ).containsKey( currentSetup ) ) {
					if( significantlyBetter.get( competitors ).get( currentSetup ) == true ) {
						currentLoses += 1;
					}
				}
			}
			
			if( ordering.containsKey( currentLoses ) == false ) { 
				ordering.put( currentLoses, new ArrayList<Integer>() ); 
			}
			
			ordering.get( currentLoses ).add( currentSetup );
		}
		
		return ordering;
	}
	
	public Map<Integer, Double> accuracyOrdering() {
		Map<Integer,Double> map = new HashMap<Integer,Double>();
        ValueComparator bvc =  new ValueComparator(map);
        TreeMap<Integer,Double> sorted_map = new TreeMap<Integer,Double>(bvc);
        
        for( int setup : fold_results.keySet() ) {
        	map.put(setup, mean(fold_results.get( setup ) ) );
        }
        
        sorted_map.putAll(map);
        
        return sorted_map;
	}
	
	public static double[] doubleListToPrimitive( List<Double> doubleArray) {
		double[] result = new double[doubleArray.size()];
		for( int i = 0; i < doubleArray.size(); ++i  ) {
			result[i] = doubleArray.get(i);
		}
		
		return result;
		
	}
	
	public double mean( List<Double> array ) {
		double total = 0;
		for( int i = 0; i < array.size(); ++i ) {
			total += array.get(i);
		}
		return total / array.size();
	}
	
	class ValueComparator implements Comparator<Integer> {

	    Map<Integer, Double> base;
	    public ValueComparator(Map<Integer, Double> base) {
	        this.base = base;
	    }

	    // Note: this comparator imposes orderings that are inconsistent with equals.    
	    public int compare(Integer a, Integer b) {
	        if (base.get(a) >= base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}
}
