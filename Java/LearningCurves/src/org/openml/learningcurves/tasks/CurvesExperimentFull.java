package org.openml.learningcurves.tasks;

import java.util.List;

public interface CurvesExperimentFull extends CurvesExperiment {
	
	public List<Double> lossCurve();
	
}
