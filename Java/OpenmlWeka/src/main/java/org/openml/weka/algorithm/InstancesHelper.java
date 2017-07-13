package org.openml.weka.algorithm;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.Task.Input.Data_set;

import weka.core.Instances;

public class InstancesHelper {

	public static void setTargetAttribute(Instances instances, String classAttribute) throws Exception {
		for (int i = 0; i < instances.numAttributes(); ++i) {
			if (instances.attribute(i).name().equals(classAttribute)) {
				instances.setClassIndex(i);
				return;
			}
		}
		throw new Exception("classAttribute " + classAttribute + " non-existant on dataset. ");
	}

	public static int getAttributeIndex(Instances instances, String attribute) throws Exception {
		for (int i = 0; i < instances.numAttributes(); ++i) {
			if (instances.attribute(i).name().equals(attribute)) {
				return i;
			}
		}
		throw new Exception("Attribute " + attribute + " non-existant on dataset. ");
	}

	public static List<String> getAttributes(Instances dataset) {
		List<String> attributesAvailable = new ArrayList<String>();
		for (int j = 0; j < dataset.numAttributes(); ++j) {
			attributesAvailable.add(dataset.attribute(j).name());
		}

		return attributesAvailable;
	}
	
	public static Instances getDatasetFromTask(OpenmlConnector apiconnector, Task task) throws Exception {
		Data_set ds = TaskInformation.getSourceData(task);

		DataSetDescription dsd = ds.getDataSetDescription(apiconnector);
		Instances instances = new Instances(new FileReader(dsd.getDataset(apiconnector.getApiKey())));

		InstancesHelper.setTargetAttribute(instances, ds.getTarget_feature());

		// remove attributes that may not be used.
		if (dsd.getIgnore_attribute() != null) {
			for (String ignoreAttr : dsd.getIgnore_attribute()) {
				String attName = ignoreAttr;
				Integer attIdx = instances.attribute(ignoreAttr).index();
				Conversion.log("OK", "Remove Attribte", "Removing attribute " + attName + " (1-based index: " + attIdx + ")");
				instances.deleteAttributeAt(attIdx);
			}
		}

		if (dsd.getRow_id_attribute() != null) {
			String attName = dsd.getRow_id_attribute();
			Integer attIdx = instances.attribute(dsd.getRow_id_attribute()).index();
			Conversion.log("OK", "Remove Attribte", "Removing attribute " + attName + " (1-based index: " + attIdx + ")");
			instances.deleteAttributeAt(attIdx);
		}
		
		return instances;
	}

}
