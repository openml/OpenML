package openml.algorithms;


public class MathHelper {

	public static double standard_deviation( Double[] population, boolean sample ) {
		double variance = 0;
		double mean = sum(population) / population.length;
		
		for( double entry : population ) {
			variance += java.lang.Math.pow(entry - mean, 2);
		}
		variance /= sample ? population.length - 1 : population.length;
		
		return java.lang.Math.sqrt(variance);
	}
	
	public static double sum( Double[] array ) {
		double total = 0;
		for( double add : array ) total += add;
		return total;
	}
	
	public static double mean( Double[] array ) {
		return sum(array) / array.length;
	}
}
