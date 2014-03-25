package org.openml.apiconnector.algorithms;

public class MathFunctions {

	public static int argmax( double[] array, boolean naturalNumbers ) {
		int best = -1;
		double value = (naturalNumbers) ? 0D : Double.MIN_VALUE;
		for( int i = 0; i < array.length; ++i ) {
			if(array[i] > value) {
				value = array[i];
				best = i;
			}
		}
		return best;
	}
}
