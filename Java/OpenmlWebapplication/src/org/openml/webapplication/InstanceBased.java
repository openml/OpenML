package org.openml.webapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openml.apiconnector.algorithms.Input;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.openml.webapplication.io.Output;

import com.thoughtworks.xstream.XStream;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class InstanceBased {
	
	private final Integer task_id;
	private final Instances task_splits;
	private final Instances dataset;
	
	private final OpenmlConnector openml;
	private final List<Integer> run_ids;
	private final Map<Integer,Run> runs;
	private final Map<Integer,String> correct;
	private final Map<Integer,Map<Integer,Map<Integer,Map<Integer,Map<Integer,String>>>>> predictions;
	
	private Instances resultSet;
	private Integer differenceCounter;
	private Integer predictionCounter;
	
	
	public InstanceBased(OpenmlConnector openml, List<Integer> run_ids, Integer task_id) throws Exception {
		this.run_ids = run_ids;
		this.openml = openml;
		this.predictions = new HashMap<Integer, Map<Integer,Map<Integer,Map<Integer,Map<Integer,String>>>>>();
		this.runs = new HashMap<Integer,Run>();
		
		this.task_id = task_id; 
		Task currentTask = openml.taskGet(task_id);
		
		if (currentTask.getTask_type().equals("Supervised Classification") == false) { // TODO: no string based comp.
			throw new RuntimeException("Experimental function, only works with 'Supervised Classification' tasks for now (ttid / 1)" );
		}
		
		URL taskUrl = new URL(TaskInformation.getEstimationProcedure(currentTask).getData_splits_url());
		task_splits = new Instances(new BufferedReader(Input.getURL(taskUrl)));
		
		for (Integer run_id : run_ids) {
			Run current = this.openml.runGet(run_id);
			runs.put(run_id,current);
			Run.Data.File[] outputFiles = current.getOutputFile();
			
			boolean found = false;
			for (Run.Data.File f : outputFiles) {
				if (f.getName().equals("predictions")) {
					found = true;
					URL predictionsURL = openml.getOpenmlFileUrl(f.getFileId(), f.getName());
					Instances runPredictions = new Instances(new BufferedReader(Input.getURL(predictionsURL)));
					predictions.put(run_id,predictionsToHashMap(runPredictions));
				}
			}
			
			if (found == false) {
				throw new RuntimeException("No prediction files associated with run. Id: " + run_id );
			}
			if (task_id != current.getTask_id()) {
				throw new RuntimeException("Runs are not of the same task type: Should be: " + this.task_id + "; found " + current.getTask_id() + " (and maybe more)" );
			}
		}
		
		
		DataSetDescription dsd = openml.dataGet(TaskInformation.getSourceData(currentTask).getData_set_id());

		dataset = new Instances(new BufferedReader(Input.getURL(openml.getOpenmlFileUrl(dsd.getFile_id(), dsd.getName()))));
		correct = datasetToHashMap(dataset, TaskInformation.getSourceData(currentTask).getTarget_feature());
	}

	public int getNumTaskSplits() {
		return task_splits.numInstances();
	}
	
	public void calculateDifference() {
		if (run_ids.size() != 2) {
			throw new RuntimeException("Too many runs to compare. Should be 2. ");
		}
		
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(new Attribute("repeat"));
		attributes.add(new Attribute("fold"));
		attributes.add(new Attribute("rowid"));
		attributes.add(new Attribute("whichCorrect"));
		resultSet = new Instances("difference",attributes,task_splits.numInstances());

		differenceCounter = 0;
		predictionCounter = 0;	
		for (int i = 0; i < task_splits.numInstances(); ++i) {
			Instance current = task_splits.get(i);
			boolean test = current.stringValue(task_splits.attribute("type")).equals("TEST");
			if (!test) {
				continue;
			}
			predictionCounter++;
			
			Integer row_id = (int) current.value(task_splits.attribute("rowid"));
			Integer repeat = (int) current.value(task_splits.attribute("repeat"));
			Integer fold = (int) current.value(task_splits.attribute("fold"));
			Integer whichCorrect = -1;
			Integer sample = 0;
			try {
				sample = (int) current.value(task_splits.attribute("sample"));
			} catch(Exception e) {}
			
			String label = null;
			boolean difference = false;
			String correctLabel = correct.get(row_id);
			
			for (Integer run_id : run_ids) {
				String currentLabel = predictions.get(run_id).get(repeat).get(fold).get(sample).get(row_id);
				if (label == null) {
					label = currentLabel;
					if (currentLabel.equals(correctLabel)) {
						whichCorrect = run_id;
					}
				} else if (label.equals(currentLabel) == false) {
					difference = true;
					if (currentLabel.equals(correctLabel)) {
						whichCorrect = run_id;
					}
				}
			}
			
			if (difference) {
				double[] instance = {repeat,fold,row_id,whichCorrect};
				resultSet.add(new DenseInstance(1.0,instance));
				differenceCounter++;
			}
		}
	}
	
	public void calculateAllWrong() {
		if (run_ids.size() < 2) {
			throw new RuntimeException("Too few runs to compare. Should be at least 2. ");
		}
		
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(new Attribute("repeat"));
		attributes.add(new Attribute("fold"));
		attributes.add(new Attribute("rowid"));
		resultSet = new Instances("all-wrong",attributes,task_splits.numInstances());
		
		for (int i = 0; i < task_splits.numInstances(); ++i) {
			Instance current = task_splits.get(i);
			boolean test = current.stringValue(task_splits.attribute("type")).equals("TEST");
			if (!test) {
				continue;
			}
			
			Integer row_id = (int) current.value(task_splits.attribute("rowid"));
			Integer repeat = (int) current.value(task_splits.attribute("repeat"));
			Integer fold = (int) current.value(task_splits.attribute("fold"));
			Integer sample = 0;
			try {
				sample = (int) current.value(task_splits.attribute("sample"));
			} catch(Exception e) {}
			
			String correctLabel = correct.get(row_id);
			Integer correctPredictions = 0;
			
			for (Integer run_id : run_ids) {
				
				//System.out.println(predictions.get(run_id));
				//System.out.println(repeat + "," + fold + "," + sample + "," + row_id);
				
				if (predictions.get(run_id).get(repeat).get(fold).get(sample).get(row_id).equals(correctLabel)) {
					correctPredictions += 1;
				}
			}
			
			if (correctPredictions == 0) {
				double[] instance = {repeat,fold,row_id};
				resultSet.add(new DenseInstance(1.0,instance));
			}
		}
		
	}
	
	public void toStdout() throws IOException {
		if(differenceCounter != null && predictionCounter != null) {
			System.out.format("%% Classifier Output Difference: %d/%d \n",differenceCounter,predictionCounter);
		}
		Output.instanes2file(resultSet, new OutputStreamWriter(System.out));
	}
	
	private static Map<Integer,Map<Integer,Map<Integer,Map<Integer,String>>>> predictionsToHashMap(Instances predictions) {
		Map<Integer,Map<Integer,Map<Integer,Map<Integer,String>>>> results = new HashMap<Integer, Map<Integer,Map<Integer,Map<Integer,String>>>>();
		
		for (int i = 0; i < predictions.numInstances(); ++i) {
			Instance current = predictions.get(i);
			
			Integer repeat = (int) current.value(predictions.attribute("repeat"));
			Integer fold = (int) current.value(predictions.attribute("fold"));
			Integer sample = 0;
			try { 
				sample = (int) current.value(predictions.attribute("sample"));
			} catch(NullPointerException e) {}
			Integer row_id = (int) current.value(predictions.attribute("row_id"));
			String prediction = current.stringValue(predictions.attribute("prediction"));
			
			if (results.containsKey(repeat) == false) {
				results.put(repeat, new HashMap<Integer, Map<Integer,Map<Integer,String>>>());
			}
			if (results.get(repeat).containsKey(fold) == false) {
				results.get(repeat).put(fold, new HashMap<Integer,Map<Integer,String>>());
			}
			if (results.get(repeat).get(fold).containsKey(sample) == false) {
				results.get(repeat).get(fold).put(sample, new HashMap<Integer,String>());
			}
			results.get(repeat).get(fold).get(sample).put(row_id, prediction);
		}
		
		return results;
	}
	
	private static Map<Integer,String> datasetToHashMap(Instances dataset, String target_attribute) {
		Map<Integer,String> correct = new HashMap<Integer, String>();
		
		for (int i = 0; i < dataset.numInstances(); ++i) {
			Instance current = dataset.get(i);
			
			correct.put(i, current.stringValue(dataset.attribute(target_attribute)));
		}
		
		return correct;
	}
	
}
