package org.openml.models;

public class ConfusionMatrix {
	
	private int[][] matrix;
	
	public ConfusionMatrix ( int classes ) {
		matrix = new int[classes][classes];
	}
	
	public void add( double actual, double predicted ) {
		if( actual > matrix.length || predicted > matrix.length )
			throw new RuntimeException("Prediction falls out of confussion matrix. ");
		
		matrix[(int)actual][(int)predicted] += 1;
	}
	
	public int size() {
		return matrix.length;
	}
	
	public int get( int actual, int predicted ) {
		return matrix[actual][predicted];
	}
}
