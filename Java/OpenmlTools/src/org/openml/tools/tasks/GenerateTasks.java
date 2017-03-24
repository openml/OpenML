package org.openml.tools.tasks;

import java.util.ArrayList;
import java.util.List;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.ApiException;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Task_new;
import org.openml.apiconnector.xml.Task_new.Input;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

public class GenerateTasks {
	
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	
	public static void main(String[] args) throws Exception {
		OpenmlConnector connector = new OpenmlConnector("https://test.openml.org/", "8baa83ecddfe44b561fd3d92442e3319");
		connector.setVerboseLevel(1);
		List<Integer> dataset_ids = new ArrayList<Integer>();
		for (int i = 1; i <= 100; ++i) {
			dataset_ids.add(i);
		}

		Integer[] tt1_ep = {1, 2, 3, 4, 5, 6};
		generateTaskType(1, dataset_ids, tt1_ep, connector);
		
		Integer[] tt3_ep = {13, 14};
		generateTaskType(3, dataset_ids, tt3_ep, connector);
		
		Integer[] tt4_ep = {15};
		generateTaskType(4, dataset_ids, tt4_ep, connector);
	}
	
	public static void generateTaskType(Integer ttid, List<Integer> dataset_ids, 
			Integer[] estimation_procedures, 
			OpenmlConnector connector) throws Exception {
		
		for (int did : dataset_ids) {
			DataSetDescription dsd = connector.dataGet(did);
			for (int ep : estimation_procedures) {
				Input source_data = new Input("source_data", "" + did);
				Input estimation_procedure = new Input("estimation_procedure", "" + ep);
				Input target_feature = new Input("target_feature", dsd.getDefault_target_attribute());
				Input[] inputs = {source_data, estimation_procedure, target_feature};
				
				Task_new task = new Task_new(null, ttid, inputs, null);
				String taskAsString = xstream.toXML(task);
				try {
					connector.taskUpload(Conversion.stringToTempFile(taskAsString, "task", "xml"));
				} catch(ApiException e) {
					if (e.getCode() != 533) {
						throw e;
					}
				}
			}
		}
	}
}
