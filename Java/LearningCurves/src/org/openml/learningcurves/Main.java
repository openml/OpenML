package org.openml.learningcurves;

import java.io.IOException;

import org.openml.learningcurves.data.DataLoader;
import org.openml.learningcurves.tasks.BestAlgorithmOriginal;
import org.openml.learningcurves.tasks.CurvesExperiment;


public class Main {
	
	public static void main( String[] args ) throws IOException {
		DataLoader dl = new DataLoader("data/meta_curves.csv");
		
		CurvesExperiment bao = new BestAlgorithmOriginal(dl, 6, 5);
		bao.allTasks();
		System.out.println( bao.result() );
	}
}
