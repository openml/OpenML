
package com.rapidminer.openml.task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.logging.Level;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.openml.util.MissingValueException;
import org.openml.util.OpenMLUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.rapidminer.Process;
import com.rapidminer.RapidMiner;
import com.rapidminer.RepositoryProcessLocation;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.gui.actions.OpenAction;
import com.rapidminer.gui.actions.SaveAction;
import com.rapidminer.openml.gui.openMLTab;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.IOObject;
import com.rapidminer.repository.IOObjectEntry;
import com.rapidminer.repository.MalformedRepositoryLocationException;
import com.rapidminer.repository.RepositoryException;
import com.rapidminer.repository.RepositoryLocation;
import com.rapidminer.repository.RepositoryManager;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.ParameterService;
import com.rapidminer.tools.XMLException;
import com.rapidminer.tools.container.Pair;

public class OpenMLTaskManager {

	private static String TASK_URL = openMLTab.readFromBundle("openml.task.url");
	private static String DATASET_URL = openMLTab.readFromBundle("openml.data.url");
	
	private static boolean isLocal = false;

	private OpenMLTaskManager() {}

	public static SuperVisedClassificationTask fetchClassificationTask(String taskId) throws SAXException, IOException, ParserConfigurationException, MissingValueException {

		String openMLDir = ParameterService.getParameterValue(openMLTab.readFromBundle("gui.pref.open_ml_repo_loc"));
		/*
		 * Check if a cached task is available in the openML repo location, if not read the url prepare tasks
		 */
		RepositoryLocation loc;
		try {
			loc = new RepositoryLocation(openMLDir + "Tasks/" + taskId);
			IOObjectEntry entry = (IOObjectEntry) loc.locateEntry();
			if (entry != null) {
				SuperVisedClassificationTask taskIOObject = (SuperVisedClassificationTask) entry.retrieveData(null);
				LogService.getRoot().info("Fetching task from the repository cache location: " + loc.getAbsoluteLocation());
				return taskIOObject;
			}
		} catch (MalformedRepositoryLocationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (isLocal) {
			try {
				TASK_URL = new File("/home/venkatesh/OpenML/XML/Examples/task.xml").toURI().toURL().toString();
				DATASET_URL = new File("/home/venkatesh/OpenML/XML/Examples/dataset.xml").toURI().toURL().toString();				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

		URL taskUrl = isLocal ? new URL(TASK_URL) : new URL(TASK_URL + taskId);
		String taskXML = OpenMLUtil.readStringfromURL(taskUrl);
		Document taskDocument = OpenMLUtil.getDocumentfromXml(taskXML);
		OpenMLData data = fetchDataForTask(taskDocument);
		IOContainer taskMetaData = fetchMetadataForTask(taskDocument);
		ExampleSet trainConfig = null;
		ExampleSet testConfig = null;
		if (taskMetaData != null) {

			if (taskMetaData.getElementAt(0) instanceof ExampleSet) {
				trainConfig = (ExampleSet) taskMetaData.getElementAt(0);
			}

			if (taskMetaData.getElementAt(1) instanceof ExampleSet) {
				testConfig = (ExampleSet) taskMetaData.getElementAt(1);
			}
		}
		String taskType = OpenMLUtil.getTaskType(taskDocument);
		if (taskType.equals("Supervised Classification")) {
			SuperVisedClassificationTask superVisedClassificationTask = new SuperVisedClassificationTask(OpenMLUtil.getTaskId(taskDocument), taskXML, data, trainConfig, testConfig);
			try {
				storeInRepo(superVisedClassificationTask, openMLDir + "Tasks/" + taskId);
				LogService.getRoot().info("cached task in the repository location: " +  openMLDir + "Tasks/" + taskId);
			} catch (MalformedRepositoryLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return superVisedClassificationTask;
		}
		return null;

	}

	public static OpenMLData fetchDataForTask(Document task) throws IOException, MissingValueException, ParserConfigurationException, SAXException {

		String openMLDir = ParameterService.getParameterValue(openMLTab.readFromBundle("gui.pref.open_ml_repo_loc"));
		Integer dataSetId = OpenMLUtil.getDataSetId(task);

		RepositoryLocation loc = null;
		try {
			loc = new RepositoryLocation(openMLDir + "Data/" + dataSetId);
			IOObjectEntry entry = (IOObjectEntry) loc.locateEntry();
			if (entry != null) {
				OpenMLData dataIOObject = (OpenMLData) entry.retrieveData(null);
				LogService.getRoot().info("Fetching data from the repository cache location: " + loc.getAbsoluteLocation());
				return dataIOObject;
			}
		} catch (Exception e) {
			LogService.getRoot().severe("Unable to cache task in the repository location:" + loc.getAbsoluteLocation());
		}

		URL dataSetUrl = new URL(DATASET_URL + dataSetId);
		String dataDescXML = OpenMLUtil.readStringfromURL(dataSetUrl);
		Document dataSet = OpenMLUtil.getDocumentfromXml(dataDescXML);

		String rowIdAttribute = null;
		boolean idExists = false;
		try {
			rowIdAttribute = OpenMLUtil.getRowIdAttribute(dataSet);
			idExists = true;
		} catch (MissingValueException e1) {
			idExists = false;
		}

		String process;
		if (!idExists) {

			process = readXMLFromResource("/com/rapidminer/resources/util/rmprocess/GenerateId.xml");

		} else {
			process = readXMLFromResource("/com/rapidminer/resources/util/rmprocess/ImportDatawithID.xml");
			idExists = true;
		}

		try {
			RapidMiner.setExecutionMode(com.rapidminer.RapidMiner.ExecutionMode.COMMAND_LINE);
//			RapidMiner.init();

			Process importProcess = new Process(process);
			importProcess.getContext().addMacro(new Pair<String, String>("ARFF_LINK", OpenMLUtil.getDataSetURL(dataSet).toString()));
//			importProcess.getContext().addMacro(new Pair<String, String>("REPO_PATH", openMLDir + "Data/" + dataSetId));
			if (idExists) {
				importProcess.getContext().addMacro(new Pair<String, String>("ROWID", openMLDir + rowIdAttribute));
			}
			IOContainer ioResult = importProcess.run();

			if (ioResult.getElementAt(0) instanceof ExampleSet) {
				ExampleSet resultSet = (ExampleSet) ioResult.getElementAt(0);
				OpenMLData openMLData = new OpenMLData(dataSetId, dataDescXML, resultSet);
				storeInRepo(openMLData, openMLDir + "Data/" + dataSetId);
				LogService.getRoot().info("cached data in the repository location: " +  openMLDir + "Data/" + dataSetId);
				return openMLData;
			}

		} catch (Exception e) {
			LogService.getRoot().log(Level.SEVERE, openMLTab.readFromBundle("openml.fetch_data_fail"));
		}
		return null;
	}

	public static String readXMLFromResource(String path) throws IOException {
		InputStream inputStream;
		String process;
		inputStream = OpenMLTaskManager.class.getResourceAsStream(path);
		StringWriter writer = new StringWriter();
		IOUtils.copy(inputStream, writer, "UTF-8");
		process = writer.toString();
		return process;
	}

	private static IOContainer fetchMetadataForTask(Document task) throws MissingValueException, IOException, ParserConfigurationException, SAXException {

		String openMLDir = ParameterService.getParameterValue(openMLTab.readFromBundle("gui.pref.open_ml_repo_loc"));

		String taskType = OpenMLUtil.getTaskType(task);
		if (taskType.equals("Supervised Classification")) {

			Integer taskId = OpenMLUtil.getTaskId(task);
			URL dataSplitUrl = new URL(OpenMLUtil.getDataSplitURL(task));

			String process = readXMLFromResource("/com/rapidminer/resources/util/rmprocess/ImportDataSplits.xml");

			try {
				RapidMiner.setExecutionMode(com.rapidminer.RapidMiner.ExecutionMode.COMMAND_LINE);
//				RapidMiner.init();

				Process importProcess = new Process(process);
				importProcess.getContext().addMacro(new Pair<String, String>("ARFF_LINK", dataSplitUrl.toString()));
				IOContainer ioResult = importProcess.run();
				return ioResult;
			} catch (Exception e) {
				LogService.getRoot().log(Level.SEVERE, openMLTab.readFromBundle("openml.fetch_meta_data_fail"));
			}

		}
		return null;

	}

	private static void storeInRepo(IOObject ioObject, String configPath) throws MalformedRepositoryLocationException, RepositoryException {
		RepositoryLocation repoLocation = new RepositoryLocation(configPath);
		RepositoryManager.getInstance(null).store(ioObject, repoLocation, null);
	}

	public static void prepareProcessforTask(Document task) throws IOException, XMLException, MalformedRepositoryLocationException, MissingValueException {

		String openMLDir = ParameterService.getParameterValue(openMLTab.readFromBundle("gui.pref.open_ml_repo_loc"));
		String openMLExpDir = ParameterService.getParameterValue(openMLTab.readFromBundle("gui.pref.open_ml_fs_dir"));

		String taskType = OpenMLUtil.getTaskType(task);
		if (taskType.equals("Supervised Classification")) {

			String process = readXMLFromResource("/com/rapidminer/resources/util/rmprocess/OpenMLSupervisedClassification.xml");

			Integer taskId = OpenMLUtil.getTaskId(task);
			Integer dataSetId = OpenMLUtil.getDataSetId(task);
			String targetFeature = OpenMLUtil.getTargetFeature(task);
			Process openMLCVTaskProcess = new Process(process);
			openMLCVTaskProcess.getContext().addMacro(new Pair<String, String>("DATA_PATH", "../../Data/" + dataSetId));
			openMLCVTaskProcess.getContext().addMacro(new Pair<String, String>("REPO_TRAIN_CONFIG", "train/trainconfig"));
			openMLCVTaskProcess.getContext().addMacro(new Pair<String, String>("REPO_TEST_CONFIG", "test/testconfig"));
			openMLCVTaskProcess.getContext().addMacro(new Pair<String, String>("TARGET_FEATURE", targetFeature));
			openMLCVTaskProcess.getContext().addMacro(new Pair<String, String>("RESULTS_PATH", "results/predictions"));
			openMLCVTaskProcess.getContext().addMacro(new Pair<String, String>("RESULTS_EXPORT_PATH", openMLExpDir + "/tasks/" + taskId + "/results/predictions.arff"));
			saveProcessforTask(task, openMLDir, openMLCVTaskProcess);

		}
	}

	private static void saveProcessforTask(Document task, String openMLDir, Process openMLCVTaskProcess) throws MalformedRepositoryLocationException, MissingValueException {
		Integer taskId = OpenMLUtil.getTaskId(task);
		RepositoryLocation location = new RepositoryLocation(openMLDir + "Tasks/" + taskId + "/execute_task_" + taskId);
		RepositoryProcessLocation processLocation = new RepositoryProcessLocation(location);
		openMLCVTaskProcess.setProcessLocation(processLocation);
		SaveAction.save(openMLCVTaskProcess);
		OpenAction.open(processLocation, true);
	}

	public static void uploadImplementation(Document task) throws IOException, MalformedRepositoryLocationException, MissingValueException {
		String openMLDir = ParameterService.getParameterValue(openMLTab.readFromBundle("gui.pref.open_ml_repo_loc"));
		String openMLExpDir = ParameterService.getParameterValue(openMLTab.readFromBundle("gui.pref.open_ml_fs_dir"));

		String URL = openMLTab.readFromBundle("openml.implementation.url");
		Integer taskId = OpenMLUtil.getTaskId(task);

		RepositoryProcessLocation location = new RepositoryProcessLocation(new RepositoryLocation(openMLDir + "Tasks/" + taskId + "/execute_task_" + taskId));
		String process = location.getRawXML();
		String impl = readXMLFromResource("/com/rapidminer/resources/util/rmprocess/implementation.xml");

		File processFile = new File(openMLExpDir + "/tasks/" + taskId + "/execute_task_" + taskId + ".xml");
		File implFile = new File(openMLExpDir + "/tasks/" + taskId + "/implementation.xml");

		FileWriter processWriter = new FileWriter(processFile);
		FileWriter implWriter = new FileWriter(implFile);

		processWriter.write(process);
		implWriter.write(impl);
		processWriter.flush();
		implWriter.flush();
		processWriter.close();
		implWriter.close();

		FileBody bin = new FileBody(implFile);
		FileBody bin2 = new FileBody(processFile);

		MultipartEntity reqEntity = new MultipartEntity();
		reqEntity.addPart("description", bin);
		reqEntity.addPart("source", bin2);

		String responseMessage = httpPost(URL, reqEntity);

		System.out.println(responseMessage);
	}

	private static void uploadResults(Document task, String implementationId, String WorkflowId) throws IOException, MissingValueException {

		BufferedWriter newPredictionsWriter = null;
		BufferedReader oldPredictionsReader = null;
		Integer taskId = OpenMLUtil.getTaskId(task);

		try {
			String openMLExpDir = ParameterService.getParameterValue(openMLTab.readFromBundle("gui.pref.open_ml_fs_dir"));
			String URL = openMLTab.readFromBundle("openml.runupload.url");
			URL = MessageFormat.format(URL, taskId, WorkflowId, implementationId);
			File oldPredictionsFile = new File(openMLExpDir + "/tasks/" + taskId + "/results/predictions.arff");
			File newPredictionsFile = new File(openMLExpDir + "/tasks/" + taskId + "/results/new_predictions.arff");
			newPredictionsWriter = new BufferedWriter(new FileWriter(newPredictionsFile));
			oldPredictionsReader = new BufferedReader(new FileReader(oldPredictionsFile));
			newPredictionsWriter.write("%task-id:" + taskId + "\n");
			newPredictionsWriter.write("%parameters:?\n");
			String line;
			boolean isRelationReplaced = false;
			while ((line = oldPredictionsReader.readLine()) != null) {
				if (!isRelationReplaced && line.contains("@relation")) {
					newPredictionsWriter.write("@relation " + "openml_task_" + taskId + "_predictions");
					isRelationReplaced = true;
				}
				newPredictionsWriter.write(line + "\n");
			}
			newPredictionsWriter.flush();
			newPredictionsWriter.close();
			oldPredictionsReader.close();

			FileBody bin = new FileBody(newPredictionsFile);
			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("predictions", bin);

			String responseMessage = httpPost(URL, reqEntity);

			System.out.println(responseMessage);

		} catch (IOException e) {
			throw e;
		} finally {
			newPredictionsWriter.flush();
			newPredictionsWriter.close();
			oldPredictionsReader.close();
		}

	}

	private static String httpPost(String URL, MultipartEntity reqEntity) throws ClientProtocolException, IOException {

		HttpClient httpclient = new DefaultHttpClient();
		StringBuffer responseMessage = new StringBuffer();

		try {
			HttpPost httppost = new HttpPost(URL);
			httppost.setEntity(reqEntity);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				BufferedReader br = new BufferedReader(new InputStreamReader(resEntity.getContent()));
				while (br.ready())
					responseMessage.append(br.readLine() + "\n");
			}
			EntityUtils.consume(resEntity);
		} finally {
			try {
				httpclient.getConnectionManager().shutdown();
			} catch (Exception ignore) {}
		}

		return responseMessage.toString();

	}
}
