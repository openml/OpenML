
package com.rapidminer.openml.task;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.openml.util.MissingValueException;
import org.openml.util.OpenMLUtil;
import org.xml.sax.SAXException;

import com.rapidminer.example.ExampleSet;

public class SuperVisedClassificationTask extends OpenMLTask {


	private static final long serialVersionUID = 1656939100222634416L;
	
	private ExampleSet trainConfig;
	private ExampleSet testConfig;

	public SuperVisedClassificationTask(int taskId, String taskXML, OpenMLData data, ExampleSet trainConfig, ExampleSet testConfig) throws SAXException, IOException, ParserConfigurationException {
		super(taskId, taskXML, data);
		this.trainConfig = trainConfig;
		this.testConfig = testConfig;
	}

	public ExampleSet getTrainConfig() {
		return trainConfig;
	}

	public ExampleSet getTestConfig() {
		return testConfig;
	}

	public Integer getNumberOfFolds() throws MissingValueException {
		return OpenMLUtil.getNumberOfFolds(getTaskDocument());
	}

	public Integer getNumberofRepeats() throws MissingValueException {

		return OpenMLUtil.getNumberOfRepeats(getTaskDocument());
	}

	public String getTargetFeature() throws MissingValueException {
		return OpenMLUtil.getTargetFeature(getTaskDocument());
	}

}
