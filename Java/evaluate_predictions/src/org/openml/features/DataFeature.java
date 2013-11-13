package org.openml.features;

import java.util.HashMap;
import java.util.Map;

public class DataFeature {
	private final int index;
	private final String name;
	private final Map<String, String> stringEntries;
	private final Map<String, Integer> intEntries;
	private final Map<String, Double> doubleEntries;
	
	public DataFeature( int index, String name ) {
		this.index = index;
		this.name = name;
		stringEntries = new HashMap<String, String>();
		intEntries = new HashMap<String, Integer>();
		doubleEntries = new HashMap<String, Double>();
	}
	
	public void put( String feature, String value ) {
		stringEntries.put( feature, value );
	}
	
	public void put( String feature, Integer value ) {
		intEntries.put( feature, value );
	}
	
	public void put( String feature, Double value ) {
		doubleEntries.put( feature, value );
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( ",\"index\":\"" + index + "\"" );
		sb.append( ",\"name\":\"" + name + "\"" );
		for( String key : stringEntries.keySet() )
			sb.append( ",\"" + key + "\":\"" + stringEntries.get(key) + "\"" );
		for( String key : intEntries.keySet() )
			sb.append( ",\"" + key + "\":" + intEntries.get(key) );
		for( String key : doubleEntries.keySet() )
			sb.append( ",\"" + key + "\":" + doubleEntries.get(key) );
		return "{" + sb.toString().substring(1) + "}";
	}
	
}
