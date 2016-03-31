package org.openml.weka.algorithm;

import java.util.ArrayList;
import java.util.List;

import weka.core.Instances;

public class InstancesHelper {
	
	public static void setTargetAttribute( Instances instances, String classAttribute ) throws Exception {
		for(int i = 0; i < instances.numAttributes(); ++i ) {
			if(instances.attribute(i).name().equals(classAttribute)) {
				instances.setClassIndex(i);
				return;
			}
		}
		throw new Exception("classAttribute " + classAttribute + " non-existant on dataset. ");
	}
	
	public static int getAttributeIndex( Instances instances, String attribute ) throws Exception {
		for(int i = 0; i < instances.numAttributes(); ++i ) {
			if(instances.attribute(i).name().equals(attribute)) {
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
	
}
