package org.openml.learningcurves.experiments;

import java.io.IOException;

import org.openml.learningcurves.data.DataLoader;
import org.openml.learningcurves.tasks.CurvesExperiment;
import org.openml.learningcurves.tasks.PairwiseMajorityClass;
import org.openml.learningcurves.tasks.PairwiseOriginal;
import org.openml.learningcurves.tasks.PairwiseRegressionBased;

public class PairwiseStrategies {
	
	private final DataLoader dl;
	
	private final int MAX_SAMPLES = 6;
	private final int MAX_NEIGHBOURS = 21;
	
	public PairwiseStrategies() throws IOException {
		// load data
		dl = new DataLoader("data/meta_curves.csv");
		
		CurvesExperiment baseline = new PairwiseMajorityClass( dl );
		baseline.allTasks();
		System.out.println( baseline.result() );
		
		for( int iSampleidx = 1; iSampleidx <= MAX_SAMPLES; ++iSampleidx ) {
			for( int iNeighbours = 1; iNeighbours <= MAX_NEIGHBOURS; iNeighbours+=2) {
				CurvesExperiment experiment = new PairwiseOriginal(dl, iSampleidx, iNeighbours);
				experiment.allTasks();
				System.out.println( experiment.result() );
			}
		}
		
		for( int iSampleidx = 1; iSampleidx <= MAX_SAMPLES; ++iSampleidx ) {
			for( int iNeighbours = 1; iNeighbours <= MAX_NEIGHBOURS; iNeighbours+=2) {
				CurvesExperiment regressionbased = new PairwiseRegressionBased(dl, iSampleidx, iNeighbours);
				regressionbased.allTasks();
				System.out.println( regressionbased.result() );
			}
		}
	}
}
