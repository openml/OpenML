package org.openml.webapplication.generatefolds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Map;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.DateParser;
import org.openml.apiconnector.algorithms.Input;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.Task.Input.Data_set;
import org.openml.apiconnector.xml.Task.Input.Stream_schedule;
import org.openml.webapplication.io.Output;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;


public class ChallengeSets {

	private final Instances dataset;
	private final Integer trainAvailable;
	private final Integer testAvailable;
	private final Attribute targetAttribute;
	
	public ChallengeSets(OpenmlConnector apiconnector, Integer task_id) throws Exception {
		Task current = apiconnector.taskGet(task_id);
		Data_set ds = TaskInformation.getSourceData(current);
		int dataset_id = ds.getData_set_id();
		DataSetDescription dsd = apiconnector.dataGet(dataset_id);
		Map<String, String> dq = apiconnector.dataQualities(dsd.getId()).getQualitiesMap();
		Integer numInstances = Integer.parseInt(dq.get("NumberOfInstances"));
		
		Stream_schedule stream_schedule = TaskInformation.getStreamSchedule(current);
		
		long secondsInProgres = DateParser.secondsSince(stream_schedule.getStart_time());
		long batchesAvailable = secondsInProgres / stream_schedule.getBatch_time();
		long trainAvailableEst = batchesAvailable * stream_schedule.getBatch_size() + stream_schedule.getInitial_batch_size();
		trainAvailable = Math.min(numInstances, (int) trainAvailableEst);
		testAvailable = trainAvailable + stream_schedule.getBatch_size();
				
		URL dataseturl = apiconnector.getOpenmlFileUrl(dsd.getFile_id(), dsd.getName() + "." + dsd.getFormat());
		dataset = new Instances(new BufferedReader(Input.getURL(dataseturl)));
		targetAttribute = dataset.attribute(ds.getTarget_feature());
	}
	
	public void train(Integer offset) throws IOException {
		if (offset == null) {
			offset = 0;
		}
		int size = trainAvailable - offset;
		if (size <= 0) {
			throw new RuntimeException("No train instances found within search criteria. ");
		}
		
		Conversion.log("OK", "Challenge Train Set", "Range <" + offset + "-" + trainAvailable + "]");
		Instances trainingSet = new Instances(dataset, size);
		for (int i = offset; i < trainAvailable; ++i) {
			trainingSet.add((Instance) dataset.get(i).copy());
		}
		Output.instanes2file(trainingSet, new OutputStreamWriter(System.out), null);
	}
	
	public void test(Integer offset) throws IOException {
		if (offset == null) {
			offset = trainAvailable;
		} 
		
		int size = testAvailable - offset;
		if (size <= 0) {
			throw new RuntimeException("No test instances found within search criteria. ");
		}
		
		Conversion.log("OK", "Challenge Test Set", "Range <" + offset + "-" + testAvailable + "]");
		
		Instances testSet = new Instances(dataset, size);
		for (int i = offset; i < testAvailable; ++i) {
			Instance current = (Instance) dataset.get(i).copy();
			current.setValue(targetAttribute, Double.NaN);
			testSet.add(current);
		}
		Output.instanes2file(testSet, new OutputStreamWriter(System.out), null);
	}
}
