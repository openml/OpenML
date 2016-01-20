package org.openml.weka.algorithm;

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
	
}
