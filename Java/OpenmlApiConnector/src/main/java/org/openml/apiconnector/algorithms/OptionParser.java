package org.openml.apiconnector.algorithms;

public class OptionParser {

	public static String[] removeFirstElement( String[] old ) {
		int n = old.length-1;
		String[] newArray = new String[n];
		System.arraycopy( old, 1, newArray, 0, n);
		return newArray;
	}
	
	
}
