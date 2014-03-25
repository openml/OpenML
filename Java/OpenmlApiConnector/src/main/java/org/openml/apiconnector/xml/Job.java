package org.openml.apiconnector.xml;

import org.openml.apiconnector.settings.Constants;

public class Job {

	private final String oml = Constants.OPENML_XMLNS;
	private int task_id;
	private String learner;
	
	public int getTask_id() {
		return task_id;
	}
	
	public String getLearner() {
		return learner;
	}
	
	public String getOml() {
		return oml;
	}
}
