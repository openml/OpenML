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
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.openml.webapplication.attributeCharacterization.AttributeCharacterizer;
import org.openml.webapplication.fantail.dc.Characterizer;
import org.openml.webapplication.fantail.dc.StreamCharacterizer;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToNominal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FantailConnector {
	private final Integer window_size;
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	private OpenmlConnector apiconnector;
	private DatabaseUtils dbUtils;
	private GlobalMetafeatures globalMetafeatures;

	public FantailConnector(OpenmlConnector ac, Integer dataset_id, boolean random, String priorityTag, Integer interval_size) throws Exception {
		window_size = interval_size;
		apiconnector = ac;
		dbUtils = new DatabaseUtils(apiconnector);
		globalMetafeatures = new GlobalMetafeatures(window_size);

		if (dataset_id != null) {
			Conversion.log("OK", "Process Dataset", "Processing dataset " + dataset_id + " on special request. ");
			computeMetafeatures(dataset_id);

		} else {
			dataset_id = dbUtils.findDatasetIdWithoutMetafeatures(globalMetafeatures.getExpectedIds(), AttributeMetafeatures.getAttributeMetafeatures(), window_size, random,
					priorityTag);
			while (dataset_id != null) {
				Conversion.log("OK", "Process Dataset", "Processing dataset " + dataset_id + " as obtained from database. ");
				computeMetafeatures(dataset_id);
				dataset_id = dbUtils.findDatasetIdWithoutMetafeatures(globalMetafeatures.getExpectedIds(), AttributeMetafeatures.getAttributeMetafeatures(), window_size, random,
						priorityTag);
			}
			Conversion.log("OK", "Process Dataset", "No more datasets to process. ");
		}
	}

	private void computeMetafeatures(int datasetId) throws Exception {
		DataSetDescription dsd = dbUtils.GetDatasetDescription(datasetId);
		Instances dataset = dbUtils.getDataset(dsd);
		List<String> qualitiesAvailable = dbUtils.GetQualitiesAvailable(datasetId, window_size);
		extractFeatures(dsd, dataset, qualitiesAvailable);
	}

	private void extractFeatures(DataSetDescription dsd, Instances dataset, List<String> qualitiesAvailable) throws Exception {
		Conversion.log("OK", "Extract Features", "Start extracting features for dataset: " + dsd.getId());
		
		//keeping the full dataset for attribute identification purposes
		Instances fullDataset = new Instances(dataset);

		dataset.setClass(dataset.attribute(dsd.getDefault_target_attribute()));
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
		for (StreamCharacterizer sc : globalMetafeatures.getStreamCharacterizers()) {

			if (qualitiesAvailable.containsAll(Arrays.asList(sc.getIDs())) == false) {
				Conversion.log("OK", "Extract Features", "Running Stream Characterizers (full data)");
				// This just precomputes everything, result will be used later depending on the windows size
				sc.characterize(dataset);
			} else {
				Conversion.log("OK", "Extract Features", "Skipping Stream Characterizers (full data) - already in database");
			}
		}

		List<Quality> qualities = new ArrayList<DataQuality.Quality>();
		if (window_size != null) {
			Conversion.log("OK", "Extract Features", "Running Batch Characterizers (partial data)");

			for (int i = 0; i < dataset.numInstances(); i += window_size) {
				if (apiconnector.getVerboselevel() >= Constants.VERBOSE_LEVEL_ARFF) {
					Conversion.log("OK", "FantailConnector",
							"Starting window [" + i + "," + (i + window_size) + "> (did = " + dsd.getId() + ",total size = " + dataset.numInstances() + ")");
				}
				qualities.addAll(datasetCharacteristics(dataset, i, window_size, null, fullDataset, dsd));

				for (StreamCharacterizer sc : globalMetafeatures.getStreamCharacterizers()) {
					// preventing nullpointer exception (if stream characterizer was already run)
					if (qualitiesAvailable.containsAll(Arrays.asList(sc.getIDs())) == false) {
						qualities.addAll(hashMaptoList(sc.interval(i), i, window_size));
					}
				}
			}

		} else {
			Conversion.log("OK", "Extract Features", "Running Batch Characterizers (full data, might take a while)");
			qualities.addAll(datasetCharacteristics(dataset, null, null, qualitiesAvailable, fullDataset, dsd));
			for (StreamCharacterizer sc : globalMetafeatures.getStreamCharacterizers()) {
				Map<String, Double> streamqualities = sc.global();
				if (streamqualities != null) {
					qualities.addAll(hashMaptoList(streamqualities, null, null));
				}
			}
		}
		Conversion.log("OK", "Extract Features", "Done generating features, start wrapping up");
		if (qualities.size() > 0) {
			DataQuality dq = new DataQuality(dsd.getId(), qualities.toArray(new Quality[qualities.size()]));
			String strQualities = xstream.toXML(dq);

			DataQualityUpload dqu = apiconnector.dataQualitiesUpload(Conversion.stringToTempFile(strQualities, "qualities_did_" + dsd.getId(), "xml"));
			Conversion.log("OK", "Extract Features", "DONE: " + dqu.getDid());
		} else {
			Conversion.log("OK", "Extract Features", "DONE: Nothing to upload");
		}
	}

	private List<Quality> datasetCharacteristics(Instances dataset, Integer start, Integer interval_size, List<String> qualitiesAvailable,
			Instances fullDataset, DataSetDescription dsd) throws Exception {
		List<Quality> result = new ArrayList<DataQuality.Quality>();
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
		AttributeMetafeatures attributeMetafeatures = new AttributeMetafeatures(dataset.numAttributes(), fullDataset, dsd);

		// parallel computation of attribute meta-features
		int threads = Runtime.getRuntime().availableProcessors();
		ExecutorService service = Executors.newFixedThreadPool(threads);

		List<Future<List<Quality>>> futures = new ArrayList<Future<List<Quality>>>();
		for (final AttributeCharacterizer attributeCharacterizer : attributeMetafeatures.getAttributeCharacterizers()) {
			Callable<List<Quality>> callable = new Callable<List<Quality>>() {
				public List<Quality> call() throws Exception {
					List<Quality> output = new ArrayList<Quality>();
					Map<String, QualityResult> qualities = attributeMetafeatures.characterize(fullDataset, attributeCharacterizer);
					output.addAll(attributeMetafeatures.qualityResultToList(qualities, start, interval_size));
					return output;
				}
			};
			futures.add(service.submit(callable));
		}

		service.shutdown();

		for (Future<List<Quality>> future : futures) {
			result.addAll(future.get());
		}

		// old for loop
		// for (AttributeCharacterizer attributeCharacterizer : attributeMetafeatures.getAttributeCharacterizers()) {
		// Map<String,QualityResult> qualities = attributeMetafeatures.characterize(dataset, attributeCharacterizer);
		// result.addAll(attributeMetafeatures.qualityResultToList(qualities, start, interval_size));
		// }
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
