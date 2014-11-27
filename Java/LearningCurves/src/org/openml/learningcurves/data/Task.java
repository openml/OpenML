package org.openml.learningcurves.data;

public class Task {

	private final int task_id;
	private final String dataset;
	private int num_samples;
	
	public Task(int task_id, String dataset, int num_samples) {
		this.task_id = task_id;
		this.dataset = dataset;
		this.num_samples = num_samples;
	}

	public int getTask_id() {
		return task_id;
	}

	public String getDataset() {
		return dataset;
	}

	public int getNum_samples() {
		return num_samples;
	}
	
	public void setNum_samples( int num_samples ) {
		this.num_samples = num_samples;
	}
	
	@Override
    public int hashCode() {
		return task_id;
	}
	
	@Override
    public boolean equals(Object obj) {
		if( obj instanceof Task ) {
			Task t = (Task) obj;
			return t.getTask_id() == getTask_id();
		} else return false;
	}
	
	@Override
	public String toString() {
		return "" + getTask_id();
	}
}
