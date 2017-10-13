package org.openml.tools.tasks;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.ApiException;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.DataQuality.Quality;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Study;
import org.openml.apiconnector.xml.TaskInputs;
import org.openml.apiconnector.xml.UploadTask;
import org.openml.apiconnector.xml.TaskInputs.Input;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

public class DidToTaskId {

	private static Set<Integer> taskIds = new HashSet<Integer>();
	
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	
	public static void main(String[] args) throws Exception {
		Config c = new Config();
		OpenmlConnector openmlConnector = new OpenmlConnector( c.getApiKey() );
		
		Study s = openmlConnector.studyGet(14, "data");
		
		System.out.println("searching for: " + s.getDataset().length);
		for (Integer dataset_id : s.getDataset()) {
			DataSetDescription dsd = openmlConnector.dataGet(dataset_id);
			DataQuality dq = openmlConnector.dataQualities(dsd.getId());
			int numInstances = -1;
			
			for (Quality q : dq.getQualities()) {
				if (q.getName().equals("NumberOfInstances")) {
					numInstances = (int) Double.parseDouble(q.getValue());
				}
			}
			
			if (numInstances == -1) continue;
			
			Input estimation_procedure = new Input("estimation_procedure", "6");
			Input data_set = new Input("source_data", dataset_id + "");
			Input target_feature = new Input("target_feature", dsd.getDefault_target_attribute());
			Input evaluation_measure = new Input("evaluation_measures", "predictive_accuracy");
			Input[] inputs = {estimation_procedure, data_set, target_feature, evaluation_measure};
			
			TaskInputs task = new TaskInputs(null, 1, inputs, null); // no task_id (will be determined by server), task type id = 2, and give inputs
			File taskFile = Conversion.stringToTempFile(xstream.toXML(task), "task", "xml");
			
			try {
				UploadTask ut = openmlConnector.taskUpload( taskFile );
				taskIds.add(ut.getId());
			} catch(ApiException e) {
				System.out.println(e.getMessage());
				String id = e.getMessage().substring(e.getMessage().indexOf('[') + 1, e.getMessage().indexOf(']'));
				taskIds.add(Integer.parseInt(id));
				
			}
		}
		System.out.println(taskIds.size() + ": " + taskIds);
	}
}
