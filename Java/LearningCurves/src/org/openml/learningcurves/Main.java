package org.openml.learningcurves;

import org.openml.learningcurves.data.DataLoader;
import org.openml.learningcurves.experiments.CurvesExperiment;
import org.openml.learningcurves.experiments.PairwiseMajorityClass;
import org.openml.learningcurves.experiments.PairwiseOriginal;
import org.openml.learningcurves.experiments.PairwiseRegressionBased;


public class Main {
	public static final int SAMPLE_IDX = 6;
	public static final int NEAREST_TASKS = 3;
	
	private static DataLoader dl;
	
	public static void main( String[] args ) throws Exception {
		// load data
		dl = new DataLoader("data/meta_curves.csv");
		
		CurvesExperiment baseline = new PairwiseMajorityClass( dl, SAMPLE_IDX, NEAREST_TASKS );
		baseline.allTasks();
		System.out.println( baseline.result() );
		
		CurvesExperiment experiment = new PairwiseOriginal( dl, SAMPLE_IDX, NEAREST_TASKS );
		experiment.allTasks();
		System.out.println( experiment.result() );
		
		CurvesExperiment regressionbased = new PairwiseRegressionBased(dl, SAMPLE_IDX, NEAREST_TASKS);
		regressionbased.allTasks();
		System.out.println( regressionbased.result() );
	}
}
