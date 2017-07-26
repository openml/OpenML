package org.openml.webapplication.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.Data.DataSet;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.webapplication.attributeCharacterization.AttributeCharacterizer;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class AttributeCharacterizerTests {

	public static final List<String> numerics = (List<String>) Arrays
			.asList(new String[] { "Median", "Kurtosis", "HasPositiveValues", "HigherOuterFence", "HasNegativeValues", "LowerConfidence", "LowerQuartile",
					"NegativeCount", "ValueRange", "Min", "LowerOuterFence", "Mean", "IsUniform", "IntegersOnly", "Max", "NegativePercentage", "Variance",
					"Skewness", "StandardDeviation", "Mode", "PositivePercentage", "HigherConfidence", "PositiveCount", "HigherQuartile" });

	public static final List<String> nominals = (List<String>) Arrays.asList(new String[] { "UniformDiscrete", "ChiSquareUniformDistribution",
			"RationOfDistinguishingCategoriesByKolmogorovSmirnoffSlashChiSquare", "RationOfDistinguishingCategoriesByUtest" });

	public static void main(String[] args) throws Exception {

		/* OpenmlConnector connector = new OpenmlConnector("ad6244a6f01a5c9fc4985a0875b30b97");
		DataSet[] datasets = connector.dataList("tmp").getData();

		for (DataSet d : datasets) {
			try {
				DataSetDescription dataDesc = connector.dataGet(d.getDid());
				File datasetFile = dataDesc.getDataset(null);

				ArffReader arff = new ArffReader(new BufferedReader(new FileReader(datasetFile)));
				Instances dataset = arff.getData();
				dataset.setClass(dataset.attribute(dataDesc.getDefault_target_attribute()));

				System.err.println("Dataset " + dataDesc.getId() + " : " + dataDesc.getName());

				ArrayList<AttributeCharacterizer> attributeCharacterizers = new ArrayList<AttributeCharacterizer>();

				for (int i = 0; i < dataset.numAttributes(); i++) {
					// System.err.println("attribute " + i);
					AttributeCharacterizer characterizer = new AttributeCharacterizer(i);
					attributeCharacterizers.add(characterizer);

					double start = System.nanoTime() / 1000000000;
					Map<String, Double> mfs = characterizer.characterize(dataset);
					double end = (System.nanoTime() / 1000000000 - start);
					if (end > 1)
						System.err.println("attribute " + dataset.attribute(i).name() + " took : " + end);

					int nbnull = 0;
					for (String key : mfs.keySet()) {
						if (mfs.get(key) == null)
							nbnull++;
					}

					int expectedNull = 0;
					if (dataset.attribute(i).isNominal()) {
						expectedNull = 24;
					} else if (dataset.attribute(i).isNumeric()) {
						expectedNull = 4;
					}

					if (nbnull != expectedNull) {
						System.err.print(dataset.attribute(i).name() + " nulls : ");
						for (String key : mfs.keySet()) {
							if (mfs.get(key) == null) {
								if (dataset.attribute(i).isNominal() && !numerics.contains(key)) {
									System.err.print(key + ", ");
								} else if (dataset.attribute(i).isNumeric() && !nominals.contains(key)) {
									System.err.print(key + ", ");
								}
							}
						}
						System.err.println("");
					}
				}

			} catch (Exception e) {
			}

		}
		System.err.println(""); */

	}

}
