package org.openml.learningcurves;

import java.io.IOException;

import org.openml.learningcurves.data.DataLoader;
import org.openml.learningcurves.tasks.BestAlgorithmGlobalRanking;
import org.openml.learningcurves.tasks.BestAlgorithmOriginal;
import org.openml.learningcurves.tasks.CurvesExperiment;


public class Main {
	
	public static void main( String[] args ) throws IOException {
		DataLoader dl = new DataLoader("data/meta_curves.csv");
		
		BestAlgorithmOriginal bao = new BestAlgorithmOriginal(dl, 2, 5);
		bao.allTasks();
		System.out.println( bao.result() );
		
		System.out.println( bao.lossCurve() );
		
		BestAlgorithmGlobalRanking bag = new BestAlgorithmGlobalRanking(dl );
		bag.allTasks();
		System.out.println( bag.result() );
		
		System.out.println( bag.lossCurve() );
	}
}
