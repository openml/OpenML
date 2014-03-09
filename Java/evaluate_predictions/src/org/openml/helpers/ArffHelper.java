package org.openml.helpers;

import weka.core.Instances;

public class ArffHelper {

	
	public static int getRowIndex( String[] names, Instances instances ) {
		for( String name : names ) {
			int probe = getRowIndex(name, instances);
			if( probe >= 0 ) return probe;
		}
		return -1;
	}
	
	public static int getRowIndex( String name, Instances instances ) {
		return (instances.attribute( name ) != null) ? instances.attribute( name ).index() : -1;
	}
}
