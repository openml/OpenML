package org.openml.learningcurves.experiments;

import java.io.IOException;

import org.openml.learningcurves.data.DataLoader;
import org.openml.learningcurves.tasks.CurvesExperiment;
import org.openml.learningcurves.tasks.PairwiseMajorityClass;
import org.openml.learningcurves.tasks.PairwiseOriginal;
import org.openml.learningcurves.tasks.PairwiseRegressionBased;

public class PairwiseStrategiesVariedNeighbours {
	public static final int SAMPLE_IDX = 6;
	
	private final DataLoader dl;
	
	public PairwiseStrategiesVariedNeighbours() throws IOException {
		// load data
		dl = new DataLoader("data/meta_curves.csv");
		
		CurvesExperiment baseline = new PairwiseMajorityClass( dl );
		baseline.allTasks();
		System.out.println( baseline.result() );
		
		for( int i = 1; i < 15; i+=2) {
			CurvesExperiment experiment = new PairwiseOriginal( dl, SAMPLE_IDX, i );
			experiment.allTasks();
			System.out.println( experiment.result() );
			
			CurvesExperiment regressionbased = new PairwiseRegressionBased(dl, SAMPLE_IDX, i);
			regressionbased.allTasks();
			System.out.println( regressionbased.result() );
		}
	}

}
