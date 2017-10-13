package org.openml.webapplication.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.Data.DataSet;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.webapplication.fantail.dc.Characterizer;
import org.openml.webapplication.fantail.dc.statistical.*;

import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class GlobalCharacterizersTests {

	public static void main(String[] args) throws Exception {

		/*OpenmlConnector connector = new OpenmlConnector("ad6244a6f01a5c9fc4985a0875b30b97");

		DataSet[] datasets = connector.dataList("study_20").getData();

		for (DataSet d : datasets) {
			try {
				DataSetDescription dataDesc = connector.dataGet(d.getDid());

				File datasetFile = dataDesc.getDataset(null);

				ArffReader arff = new ArffReader(new BufferedReader(new FileReader(datasetFile)));
				Instances dataset = arff.getData();
				dataset.setClass(dataset.attribute(dataDesc.getDefault_target_attribute()));

				System.err.println("Dataset " + dataDesc.getId() + " : " + dataDesc.getName());

				Characterizer simple = new SimpleMetaFeatures();
				@SuppressWarnings("unused")
				Map<String, Double> simples = simple.characterize(dataset);
				
				Characterizer characterizer = new AttributeEntropy();

				double start = System.nanoTime() / 1000000000;
				Map<String, Double> mfs = characterizer.characterize(dataset);
				double end = (System.nanoTime() / 1000000000 - start);
				if (end > 1)
					System.err.println(characterizer.getClass() + " took : " + end);

				int nbnull = 0;
				for (String key : mfs.keySet()) {
					if (mfs.get(key) == null)
						nbnull++;
				}

				if (nbnull > 0) {
					System.err.println("Nulls : ");
					for (String key : mfs.keySet()) {
						if (mfs.get(key) == null) {
							System.err.print(key + ", ");
						}
					}
					System.err.println("");
					throw new Exception();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		System.err.println(""); */

	}

}
