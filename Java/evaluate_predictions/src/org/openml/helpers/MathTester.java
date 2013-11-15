package org.openml.helpers;

import static org.junit.Assert.*;

import org.junit.Test;

public class MathTester {

	@Test
	public void testCase1() {
		Double[] input = {1.0,1.0,1.0,1.0,1.0,1.0};
		Double sum = 6.0;
		Double stdev_population = 0.0;
		Double stdev_sample = 0.0;
		
		assertTrue( equals( MathHelper.sum(input), sum, 0.0000001 ) );
		assertTrue( equals( MathHelper.standard_deviation(input, false), stdev_population, 0.0000001 ) );
		assertTrue( equals( MathHelper.standard_deviation(input, true), stdev_sample, 0.0000001 ) );
	}

	@Test
	public void testCase2() {
		Double[] input = {1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,1.0};
		Double sum = 46.0;
		Double stdev_population = 2.72763633939717;
		Double stdev_sample = 2.87518115371304;
		
		assertTrue( equals( MathHelper.sum(input), sum, 0.0000001 ) );
		assertTrue( equals( MathHelper.standard_deviation(input, false), stdev_population, 0.0000001 ) );
		assertTrue( equals( MathHelper.standard_deviation(input, true), stdev_sample, 0.0000001 ) );
	}

	@Test
	public void testCase3() {
		Double[] input = {90.0,90.0,90.0,90.0,90.0,90.0,90.0,90.0,89.0,89.0};
		Double sum = 898.0;
		Double stdev_population = 0.4;
		Double stdev_sample = 0.421637021355784;
		
		assertTrue( equals( MathHelper.sum(input), sum, 0.0000001 ) );
		assertTrue( equals( MathHelper.standard_deviation(input, false), stdev_population, 0.0000001 ) );
		assertTrue( equals( MathHelper.standard_deviation(input, true), stdev_sample, 0.0000001 ) );
	}
	
	private static boolean equals( Double a, Double b, Double episilon ) {
		if( java.lang.Math.abs( a - b ) < episilon )
			return true;
		return false;
	}

}
