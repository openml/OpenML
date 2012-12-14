package crossvalidation;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public abstract class Task {
	
	int taskId;
	
	String taskType;

	public Task(){}
	
	public Task(int taskId, String taskType){
		this.taskId=taskId;
		this.taskType=taskType;
	}
	
	public int getTaskId(){
		return this.taskId;
	}
	
	public String getTaskType(){
		return this.taskType;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	
	
}
