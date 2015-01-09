package org.openml.learningcurves.utils;

import java.util.ArrayList;
import java.util.List;

public class OrderedMap {

	private final List<Integer> keys;
	private final List<Double> values;
	
	public OrderedMap() {
		keys = new ArrayList<>();
		values = new ArrayList<>();
	}
	
	public void put( int key, double value ) {
		if( values.size() == 0 ) {
			values.add(0, value);
			keys.add(0, key);
			
		} else {
			boolean added = false;
			for( int i = 0; i < values.size(); ++i ) {
				if( values.get(i) < value ) { continue; }
				added = true;
				values.add(i, value);
				keys.add(i, key);
				break;
			}
			
			// add if not yet added. 
			if(added == false ) {
				values.add(value);
				keys.add(key);
			}
		
		}
	}
	
	public Integer getRankByKey( int key ) {
		for( int i = 0; i < keys.size(); ++i ) {
			if( keys.get( i ) == key ) {
				return i;
			}
		}
		return null;
	}
	
	public Integer getKeyByRank( int rank ) {
		return keys.get( rank );
	}
	
	public Double getValueByKey( int key ) {
		for( int i = 0; i < keys.size(); ++i ) {
			if( keys.get( i ) == key ) {
				return values.get( i );
			}
		}
		return null;
	}
	
	public Double getValueByRank( int rank ) {
		return values.get( rank );
	}
	
	public boolean remove( int key ) {
		for( int i = 0; i < keys.size(); ++i ) {
			if( keys.get( i ) == key ) {
				keys.remove( i );
				values.remove( i );
				return true;
			}
		}
		return false;
	}
	
	public Integer size() {
		return keys.size();
	}
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for( int i = 0; i < keys.size(); ++i ) {
			sb.append( ", " + keys.get(i) + "=" + values.get(i) );
		}
		return "[" + sb.toString().substring( 2 ) + "]";
	}
}
