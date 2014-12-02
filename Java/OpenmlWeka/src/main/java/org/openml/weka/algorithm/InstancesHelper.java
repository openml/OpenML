package org.openml.weka.algorithm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
	
	public static File instancesToTempFile( Instances dataset, String filename, String format ) throws IOException {
		File file = File.createTempFile(filename, '.' + format );
		BufferedWriter br = new BufferedWriter(new FileWriter(file));
		br.write(dataset.toString());
		br.close();
		file.deleteOnExit();
		return file;
	}
	
}
