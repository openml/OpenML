
package com.rapidminer.openml.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;

import com.rapidminer.Process;
import com.rapidminer.RapidMiner;
import com.rapidminer.RepositoryProcessLocation;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.DataRow;
import com.rapidminer.example.table.LongArrayDataRow;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.actions.OpenAction;
import com.rapidminer.gui.actions.SaveAction;
import com.rapidminer.openml.task.jaxb.beans.Bootstrapping;
import com.rapidminer.openml.task.jaxb.beans.CrossValidation;
import com.rapidminer.openml.task.jaxb.beans.DataSetDescription;
import com.rapidminer.openml.task.jaxb.beans.Fold;
import com.rapidminer.openml.task.jaxb.beans.HoldOut;
import com.rapidminer.openml.task.jaxb.beans.Repeat;
import com.rapidminer.openml.task.jaxb.beans.SubSampling;
import com.rapidminer.openml.task.jaxb.beans.Task;
import com.rapidminer.openml.task.jaxb.beans.TaskType;
import com.rapidminer.repository.MalformedRepositoryLocationException;
import com.rapidminer.repository.RepositoryException;
import com.rapidminer.repository.RepositoryLocation;
import com.rapidminer.repository.RepositoryManager;
import com.rapidminer.tools.Ontology;
import com.rapidminer.tools.ParameterService;
import com.rapidminer.tools.ProgressListener;
import com.rapidminer.tools.WebServiceTools;
import com.rapidminer.tools.XMLException;

public class OpenMLTaskManager {

	private static final String TASK_URL = "http://expdb.cs.kuleuven.be/expdb/api/?f=openml.task.search&task_id=";
	private static final String DATASET_URL = "http://expdb.cs.kuleuven.be/expdb/api/?f=openml.data.description&data_id=";

	private OpenMLTaskManager() {
		// TODO Auto-generated constructor stub
	}

	public static Task fetchTask(String taskId) throws JAXBException, IOException {

		URL taskUrl = new URL(TASK_URL + taskId);
		BufferedReader in = new BufferedReader(new InputStreamReader(WebServiceTools.openStreamFromURL(taskUrl)));

		JAXBContext jaxbContext = JAXBContext.newInstance(Task.class);

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		JAXBElement<Task> root = jaxbUnmarshaller.unmarshal(new StreamSource(in), Task.class);
		Task task = root.getValue();
		System.out.println(task.getTaskId());

		return task;

	}

	public static void fetchDataForTask(Task task) throws IOException, JAXBException {

		String openMLDir = ParameterService.getParameterValue("OpenML Directory");

		Integer dataSetId = task.getPrediction().getDataSet().getDataSetId();
		URL dataSetUrl = new URL(DATASET_URL + dataSetId);
		BufferedReader in = new BufferedReader(new InputStreamReader(WebServiceTools.openStreamFromURL(dataSetUrl)));

		JAXBContext jaxbContext = JAXBContext.newInstance(DataSetDescription.class);

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		JAXBElement<DataSetDescription> root = jaxbUnmarshaller.unmarshal(new StreamSource(in), DataSetDescription.class);
		DataSetDescription dataset = root.getValue();

		String process = null;

		String rowIdAttribute = dataset.getRowIdAttribute();

		if (rowIdAttribute == null || rowIdAttribute.length() == 0) {

			process = readProcessXML("/com/rapidminer/resources/util/rmprocess/GenerateId.xml");

		} else {
			process = readProcessXML("/com/rapidminer/resources/util/rmprocess/ImportDatawithID.xml");
			process = process.replace("$ROWID$", rowIdAttribute);
		}

		process = process.replace("$ARFF_LINK$", dataset.getUrl());
		process = process.replace("$REPO_PATH$", openMLDir + "Data/" + dataSetId);

		try {
			RapidMiner.setExecutionMode(com.rapidminer.RapidMiner.ExecutionMode.COMMAND_LINE);
			RapidMiner.init();

			Process importProcess = new Process(process);

			// perform process
			importProcess.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String readProcessXML(String path) throws IOException {
		InputStream inputStream;
		String process;
		inputStream = OpenMLTaskManager.class.getResourceAsStream(path);
		StringWriter writer = new StringWriter();
		IOUtils.copy(inputStream, writer, "UTF-8");
		process = writer.toString();
		return process;
	}

	public static void fetchMetadataForTask(Task task) throws MalformedRepositoryLocationException, RepositoryException {

		String openMLDir = ParameterService.getParameterValue("OpenML Directory");

		TaskType taskType = task.getTaskType();
		if (taskType.equals(TaskType.PREDICTION)) {

			CrossValidation crossValidation = task.getPrediction().getEvaluationMethod().getCrossValidation();
			HoldOut holdOut = task.getPrediction().getEvaluationMethod().getHoldOut();
			Bootstrapping bootstrapping = task.getPrediction().getEvaluationMethod().getBootStraping();
			SubSampling subSampling = task.getPrediction().getEvaluationMethod().getSubSampling();

			if (crossValidation != null) {
				Integer numberOfFolds = crossValidation.getNumberOfFolds();
				Integer numberOfRepeats = crossValidation.getNumberOfRepeats();
				List<Repeat> repeats = crossValidation.getRepeat();

				for (int i = 0; i < numberOfRepeats; i++) {
					Repeat repeat = repeats.get(i);
					List<Fold> folds = repeat.getFold();

					Attribute id = AttributeFactory.createAttribute("rowid", Ontology.INTEGER);
					Attribute rep = AttributeFactory.createAttribute("repeat", Ontology.INTEGER);
					Attribute fold = AttributeFactory.createAttribute("fold", Ontology.INTEGER);
					Attribute[] attributes = new Attribute[] { rep, fold, id };

					for (int j = 0; j < numberOfFolds; j++) {

						MemoryExampleTable trainTable = new MemoryExampleTable(attributes);
						MemoryExampleTable testTable = new MemoryExampleTable(attributes);

						List<Long> trainObservations = folds.get(j).getFoldTrain().getObservations();
						List<Long> testObservations = folds.get(j).getFoldTest().getObservations();

						for (Long rowId : trainObservations) {
							DataRow dataRow = new LongArrayDataRow(new long[] { (i + 1), (j + 1), (rowId + 1) });
							trainTable.addDataRow(dataRow);

						}

						for (Long rowId : testObservations) {
							DataRow dataRow = new LongArrayDataRow(new long[] { (i + 1), (j + 1), (rowId + 1) });
							testTable.addDataRow(dataRow);

						}

						ExampleSet trainExampleSet = trainTable.createExampleSet();
						ExampleSet testExampleSet = testTable.createExampleSet();

						trainExampleSet.getAttributes().setSpecialAttribute(id, "id");
						testExampleSet.getAttributes().setSpecialAttribute(id, "id");

						trainExampleSet.getAttributes().setSpecialAttribute(rep, "repeat");
						testExampleSet.getAttributes().setSpecialAttribute(rep, "repeat");

						trainExampleSet.getAttributes().setSpecialAttribute(fold, "fold");
						testExampleSet.getAttributes().setSpecialAttribute(fold, "fold");

						RepositoryLocation trainLocation = new RepositoryLocation(openMLDir + "Tasks/" + task.getTaskId() + "/train/fold_" + (j + 1) + "_repeat_" + (i + 1));
						RepositoryLocation testLocation = new RepositoryLocation(openMLDir + "Tasks/" + task.getTaskId() + "/test/fold_" + (j + 1) + "_repeat_" + (i + 1));

						RepositoryManager.getInstance(null).store(trainExampleSet, trainLocation, null);
						RepositoryManager.getInstance(null).store(testExampleSet, testLocation, null);

					}

				}
			} else if (holdOut != null) {

			} else if (bootstrapping != null) {

			} else if (subSampling != null) {

			}
		}
	}

	public static void prepareProcessforTask(Task task) throws IOException, XMLException, MalformedRepositoryLocationException {

		String openMLDir = ParameterService.getParameterValue("OpenML Directory");

		TaskType taskType = task.getTaskType();
		if (taskType.equals(TaskType.PREDICTION)) {

			CrossValidation crossValidation = task.getPrediction().getEvaluationMethod().getCrossValidation();
			HoldOut holdOut = task.getPrediction().getEvaluationMethod().getHoldOut();
			Bootstrapping bootstrapping = task.getPrediction().getEvaluationMethod().getBootStraping();
			SubSampling subSampling = task.getPrediction().getEvaluationMethod().getSubSampling();

			if (crossValidation != null) {

				String process = readProcessXML("/com/rapidminer/resources/util/rmprocess/OpenMLPredictionCVTask.xml");
				Integer dataSetId = task.getPrediction().getDataSet().getDataSetId();
				String targetFeature = task.getPrediction().getDataSet().getTargetFeature();
				process = process.replace("$DATA_PATH$", openMLDir + "Data/" + dataSetId);
				process = process.replace("$TRAIN_PATH$", openMLDir + "Tasks/" + task.getTaskId() + "/train/");
				process = process.replace("$TARGET_FEATURE$", targetFeature);
				process = process.replace("$RESULT_PATH$", openMLDir + "Tasks/" + task.getTaskId() + "/results/predictions");

				Process openMLCVTaskProcess = new Process(process);
				RepositoryLocation location = new RepositoryLocation(openMLDir + "Tasks/" + task.getTaskId() + "/execute_task_" + task.getTaskId());
				RepositoryProcessLocation processLocation = new RepositoryProcessLocation(location);
				openMLCVTaskProcess.setProcessLocation(processLocation);
				SaveAction.save(openMLCVTaskProcess);
				OpenAction.open(processLocation, true);

			} else if (holdOut != null) {

			} else if (bootstrapping != null) {

			} else if (subSampling != null) {

			}
		}
	}
}
