/*
 *  Webapplication - Java library that runs on OpenML servers
 *  Copyright (C) 2014 
 *  @author Jan N. van Rijn (j.n.van.rijn@liacs.leidenuniv.nl)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
package org.openml.webapplication.features;

import com.thoughtworks.xstream.XStream;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Constants;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.DataQuality.Quality;
import org.openml.apiconnector.xml.DataQualityUpload;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.DataUnprocessed;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.openml.webapplication.fantail.dc.Characterizer;
import org.openml.webapplication.settings.Settings;

import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToNominal;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FantailConnector {
	private final Integer window_size = null; // TODO: make it work again
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	private OpenmlConnector apiconnector;
	private GlobalMetafeatures globalMetafeatures;

	public FantailConnector(OpenmlConnector ac, Integer dataset_id, String mode, String priorityTag, Integer interval_size) throws Exception {
		apiconnector = ac;
		globalMetafeatures = new GlobalMetafeatures(window_size);

		if (dataset_id != null) {
			Conversion.log("OK", "Process Dataset", "Processing dataset " + dataset_id + " on special request. ");
			computeMetafeatures(dataset_id);

		} else {
			DataUnprocessed du = apiconnector.dataqualitiesUnprocessed(Settings.EVALUATION_ENGINE_ID, mode, false, globalMetafeatures.getExpectedIds());
			while (du != null) {
				Conversion.log("OK", "Process Dataset", "Processing dataset " + dataset_id + " as obtained from database. ");
				computeMetafeatures(du.getDatasets()[0].getDid());
				du = apiconnector.dataqualitiesUnprocessed(Settings.EVALUATION_ENGINE_ID, mode, false, globalMetafeatures.getExpectedIds());
			}
			Conversion.log("OK", "Process Dataset", "No more datasets to process. ");
		}
	}

	private void computeMetafeatures(int datasetId) throws Exception {
		Conversion.log("OK", "Download", "Start downloading dataset: " + datasetId);
		DataSetDescription dsd = apiconnector.dataGet(datasetId);
		Instances dataset = new Instances(new FileReader(dsd.getDataset(apiconnector)));
		List<String> qualitiesAvailable = Arrays.asList(apiconnector.dataQualities(datasetId).getQualityNames());
		extractFeatures(dsd, dataset, qualitiesAvailable);
	}

	private void extractFeatures(DataSetDescription dsd, Instances dataset, List<String> qualitiesAvailable) throws Exception {
		Conversion.log("OK", "Extract Features", "Start extracting features for dataset: " + dsd.getId());

		dataset.setClass(dataset.attribute(dsd.getDefault_target_attribute()));

		// keeping the full dataset for attribute identification purposes
		Instances fullDataset = new Instances(dataset);

		if (dsd.getRow_id_attribute() != null) {
			if (dataset.attribute(dsd.getRow_id_attribute()) != null) {
				dataset.deleteAttributeAt(dataset.attribute(dsd.getRow_id_attribute()).index());
			}
		}
		if (dsd.getIgnore_attribute() != null) {
			for (String att : dsd.getIgnore_attribute()) {
				if (dataset.attribute(att) != null) {
					dataset.deleteAttributeAt(dataset.attribute(att).index());
				}
			}
		}

		// first run stream characterizers
		/*for (StreamCharacterizer sc : globalMetafeatures.getStreamCharacterizers()) {

			if (qualitiesAvailable.containsAll(Arrays.asList(sc.getIDs())) == false) {
				Conversion.log("OK", "Extract Features", "Running Stream Characterizers (full data)");
				// This just precomputes everything, result will be used later depending on the windows size
				sc.characterize(dataset);
			} else {
				Conversion.log("OK", "Extract Features", "Skipping Stream Characterizers (full data) - already in database");
			}
		}*/

		List<Quality> qualities = new ArrayList<>();
		if (window_size != null) {
			Conversion.log("OK", "Extract Features", "Running Batch Characterizers (partial data)");

			for (int i = 0; i < dataset.numInstances(); i += window_size) {
				if (apiconnector.getVerboselevel() >= Constants.VERBOSE_LEVEL_ARFF) {
					Conversion.log("OK", "FantailConnector",
							"Starting window [" + i + "," + (i + window_size) + "> (did = " + dsd.getId() + ",total size = " + dataset.numInstances() + ")");
				}
				qualities.addAll(datasetCharacteristics(dataset, i, window_size, null, fullDataset, dsd));

				/*for (StreamCharacterizer sc : globalMetafeatures.getStreamCharacterizers()) {
					// preventing nullpointer exception (if stream characterizer was already run)
					if (qualitiesAvailable.containsAll(Arrays.asList(sc.getIDs())) == false) {
						qualities.addAll(hashMaptoList(sc.interval(i), i, window_size));
					}
				}*/
			}

		} else {
			Conversion.log("OK", "Extract Features", "Running Batch Characterizers (full data, might take a while)");
			qualities.addAll(datasetCharacteristics(dataset, null, null, qualitiesAvailable, fullDataset, dsd));
			/*for (StreamCharacterizer sc : globalMetafeatures.getStreamCharacterizers()) {
				Map<String, Double> streamqualities = sc.global();
				if (streamqualities != null) {
					qualities.addAll(hashMaptoList(streamqualities, null, null));
				}
			}*/
		}
		Conversion.log("OK", "Extract Features", "Done generating features, start wrapping up");
		if (qualities.size() > 0) {
			DataQuality dq = new DataQuality(dsd.getId(), Settings.EVALUATION_ENGINE_ID, qualities.toArray(new Quality[qualities.size()]));
			String strQualities = xstream.toXML(dq);
			DataQualityUpload dqu = apiconnector.dataQualitiesUpload(Conversion.stringToTempFile(strQualities, "qualities_did_" + dsd.getId(), "xml"));
			Conversion.log("OK", "Extract Features", "DONE: " + dqu.getDid());
		} else {
			Conversion.log("OK", "Extract Features", "DONE: Nothing to upload");
		}
	}

	private List<Quality> datasetCharacteristics(Instances dataset, Integer start, Integer interval_size, List<String> qualitiesAvailable,
			Instances fullDataset, DataSetDescription dsd) throws Exception {
		List<Quality> result = new ArrayList<>();
		Instances intervalData;

		// Be careful changing this!
		if (interval_size != null) {
			intervalData = new Instances(dataset, start, Math.min(interval_size, dataset.numInstances() - start));
			intervalData = applyFilter(intervalData, new StringToNominal(), "-R first-last");
			intervalData.setClassIndex(dataset.classIndex());
		} else {
			intervalData = dataset;
			// todo: use StringToNominal filter? might be too expensive
		}

		for (Characterizer dc : globalMetafeatures.getCharacterizers()) {
			if (qualitiesAvailable != null && qualitiesAvailable.containsAll(Arrays.asList(dc.getIDs())) == false) {
				Conversion.log("OK", "Extract Batch Features", dc.getClass().getName() + ": " + Arrays.toString(dc.getIDs()));
				Map<String, Double> qualities = dc.characterize(intervalData);
				result.addAll(hashMaptoList(qualities, start, interval_size));
			} else {
				Conversion.log("OK", "Extract Batch Features", dc.getClass().getName() + " - already in database");
			}
		}
		
		// parallel computation of attribute meta-features
		//AttributeMetafeatures attributeMetafeatures = new AttributeMetafeatures(dataset.numAttributes(), fullDataset, dsd);
		//int threads = Runtime.getRuntime().availableProcessors();
		//attributeMetafeatures.computeAndAppendAttributeMetafeatures(fullDataset, start, interval_size, threads, result);
		return result;
	}

	public static List<Quality> hashMaptoList(Map<String, Double> map, Integer start, Integer size) {
		List<Quality> result = new ArrayList<>();
		for (String quality : map.keySet()) {
			Integer end = start != null ? start + size : null;
			result.add(new Quality(quality, map.get(quality) + "", start, end, null));
		}
		return result;
	}

	private static Instances applyFilter(Instances dataset, Filter filter, String options) throws Exception {
		filter.setOptions(Utils.splitOptions(options));
		filter.setInputFormat(dataset);
		return Filter.useFilter(dataset, filter);
	}
}
