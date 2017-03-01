package org.openml.webapplication.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;

import org.openml.webapplication.attributeCharacterization.AttributeCharacterizer;

import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class AttributeCharacterizerTests {

	public static void main(String[] args) throws Exception {

		// OpenmlConnector connector = new OpenmlConnector("http://capa.win.tue.nl", "ad6244a6f01a5c9fc4985a0875b30b97");
		// DataSetDescription dataDesc = connector.dataGet(1);
		// File datasetFile = dataDesc.getDataset(null);
		ArffReader arff = new ArffReader(new BufferedReader(new FileReader("C:\\Users\\admin\\Desktop\\1.arff")));
		Instances dataset = arff.getData();
		dataset.setClass(dataset.attribute("class"));

		ArrayList<AttributeCharacterizer> attributeCharacterizers = new ArrayList<AttributeCharacterizer>();

		//for (int i = 0; i < dataset.numAttributes(); i++) {
		int i =1;
		System.err.println("attribute " + i);
			AttributeCharacterizer characterizer = new AttributeCharacterizer(i);
			attributeCharacterizers.add(characterizer);
			Map<String, Double> mfs = characterizer.characterize(dataset);

			for (String key : mfs.keySet()) {
				System.err.println(key + " : " + mfs.get(key));
			}
		//}
	}

}
