package org.openml.learningcurves.tasks;

public interface CurvesExperiment {
	
	public void allTasks();
	public void singleTask( int task_id );
	public String result();
	
}
