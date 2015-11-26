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
import org.openml.apiconnector.xml.Task_new;
import org.openml.apiconnector.xml.UploadTask;
import org.openml.apiconnector.xml.Task_new.Input;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.openml.webapplication.generatefolds.EstimationProcedure;

import com.thoughtworks.xstream.XStream;

public class DidToTaskId {

	private static final Integer[] dids = { 1043,1037,179,1119,4135,1145,1158,4134,15,822,29,31,803,37,151,1038,1042,1039,273,1176,1053,1067,981,993,1002,1018,976,1004,1111,1112,1114,3,1223,1222,1120,1056,334,1046,24,1116,1128,1161,1142,1134,1130,1139,1166,1146,1138,1068,1069,1050,1049,470,38,44,1036,1040,50,1242 };
	private static Set<Integer> taskIds = new HashSet<Integer>();
	
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	
	public static void main(String[] args) throws Exception {
		Config c = new Config();
		OpenmlConnector openmlConnector = new OpenmlConnector( c.getApiKey() );
		
		System.out.println("searching for: " + dids.length);
		for (Integer dataset_id : dids) {
			DataSetDescription dsd = openmlConnector.dataGet(dataset_id);
			DataQuality dq = openmlConnector.dataQualities(dsd.getId());
			int numInstances = -1;
			
			for (Quality q : dq.getQualities()) {
				if (q.getName().equals("NumberOfInstances")) {
					numInstances = Integer.parseInt(q.getValue());
				}
			}
			
			if (numInstances == -1) continue;
			
			int numSamples = EstimationProcedure.getNumberOfSamples((int) Math.ceil(numInstances * .9));
			
			Input estimation_procedure = new Input("estimation_procedure", "13"); // the openml id for 10 fold CV. Contact me for other id's
			Input data_set = new Input("source_data", dataset_id + "");
			Input target_feature = new Input("target_feature", dsd.getDefault_target_attribute());
			Input number_samples = new Input("number_samples", "" + numSamples);
			Input[] inputs = {estimation_procedure, data_set, target_feature, number_samples};
			
			Task_new task = new Task_new(null, 3, inputs); // no task_id (will be determined by server), task type id = 2, and give inputs
			File taskFile = Conversion.stringToTempFile(xstream.toXML(task), "task", "xml");
			
			try {
				UploadTask ut = openmlConnector.taskUpload( taskFile );
				taskIds.add(ut.getId());
			} catch(ApiException e) {
				String id = e.getMessage().substring(e.getMessage().indexOf('[') + 1, e.getMessage().indexOf(']'));
				taskIds.add(Integer.parseInt(id));
				
			}
		}
		System.out.println(taskIds.size() + ": " + taskIds);
	}
}
