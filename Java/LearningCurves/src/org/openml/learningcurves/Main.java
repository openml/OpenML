package org.openml.learningcurves;

import java.util.Map;

import org.openml.learningcurves.data.DataLoader;
import org.openml.learningcurves.data.Distance;
import org.openml.learningcurves.data.Evaluation;


public class Main {

	public static void main( String[] args ) throws Exception {
		DataLoader dl = new DataLoader("data/meta_curves.csv");
		Map<Integer, Map<Integer, Map<Integer, Evaluation>>> taskOriented = dl.getTaskOriented();
		Map<Integer, Map<Integer, Map<Integer, Evaluation>>> setupOriented = dl.getSetupOriented();
		Distance d = new Distance(setupOriented);

		print(taskOriented);
		print(setupOriented);
		
		System.out.println( d.distance(1701, 1764, 594, 597, 5, 5) );
	}
	
	
	private static void print( Map<Integer, Map<Integer, Map<Integer, Evaluation>>> taskOriented ) {
		for( Integer task : taskOriented.keySet() ) {
			System.out.println( "Task " + task );
			for( Integer setup : taskOriented.get(task).keySet() ) {
				System.out.println( "- Setup " + setup );
				for( Integer sample : taskOriented.get(task).get(setup).keySet() ) {
					System.out.println( "--- Sample " + sample + ": " + taskOriented.get(task).get(setup).get(sample) );
				}
			}
		}
	}
}
