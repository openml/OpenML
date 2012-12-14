package crossvalidation;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("oml:task")
public class TaskContainer {
	
	@XStreamAlias("oml:task-id")
	int taskId;
	@XStreamAlias("oml:task-type")
	String taskType;
	@XStreamImplicit(itemFieldName="prediction")
	List<Prediction> predictions = new ArrayList<Prediction>();
	
	public TaskContainer(int id, String type, List<Prediction> preds){
		taskId=id;
		taskType=type;
		predictions = preds;
	}
	
	public TaskContainer(){}
	
	
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public void setPredictions(List<Prediction> prediction) {
		this.predictions = prediction;
	}
	
	public int getPrediction(){
		return this.predictions.size();
	}
	
	public List<Prediction> getPredictions(){
		return this.predictions;
	}
	
	public void addPrediction(Prediction t){
		System.out.println("boe");
		this.predictions.add(t);
	}
	
	public int getTaskId(){
		return this.taskId;
	}
	
	public String getTaskType(){
		return this.taskType;
	}
	
}
