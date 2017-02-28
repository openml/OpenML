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
import org.openml.webapplication.fantail.dc.Characterizer;
import org.openml.webapplication.fantail.dc.StreamCharacterizer;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToNominal;

import java.util.*;

public class FantailConnector {
    public class QualityResult{
        public QualityResult(Double value, Integer index){
            this.value = value;
            this.index = index;
        }

        Double value;
        Integer index;
    }

    public class CharacterizerWrapper {
        public CharacterizerWrapper(Characterizer characterizer){
            this.characterizer = characterizer;
            this.index = null;
        }

        public CharacterizerWrapper(Characterizer characterizer, int index){
            this.characterizer = characterizer;
            this.index = index;
        }

        public Characterizer characterizer;
        public Integer index;

        public String[] getIDs() {
            return characterizer.getIDs();
        }

        public int getNumMetaFeatures() {
            return getIDs().length;
        }

        public Map<String, QualityResult> characterize(Instances instances){
            Map<String, Double> values =characterizer.characterize(instances);
            Map <String, QualityResult> result = new HashMap<>();
            values.forEach((s,v) -> result.put(s, new QualityResult(v, index)));
            return result;
        }
    }

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

		if(dataset_id != null) {
			Conversion.log("OK", "Process Dataset", "Processing dataset " + dataset_id + " on special request. ");
			computeMetafeatures(dataset_id);

		} else {

			dataset_id = dbUtils.getDatasetId(getExpectedNumberOfMetafeatures(), window_size, random, priorityTag);
			while( dataset_id != null ) {
				Conversion.log("OK", "Process Dataset", "Processing dataset " + dataset_id + " as obtained from database. ");
				computeMetafeatures(dataset_id);
				dataset_id = dbUtils.getDatasetId(getExpectedNumberOfMetafeatures(), window_size, random, priorityTag);
			}
			Conversion.log("OK", "Process Dataset", "No more datasets to process. ");
		}
	}

	private int getExpectedNumberOfMetafeatures(){
		//TODO: modify code to work with expected attribute metafeatures,
		//TODO: perhaps modify it so it really verifies that all valid qualities are computed
		return globalMetafeatures.getExpectedQualities();
	}

	private void computeMetafeatures(int datasetId) throws Exception {
	    DataSetDescription dsd = dbUtils.GetDatasetDescription(datasetId);
	    Instances dataset = dbUtils.getDataset(dsd);
	    List<String> qualitiesAvailable = dbUtils.GetQualitiesAvailable(datasetId, window_size);
        extractFeatures(dsd, dataset, qualitiesAvailable);
    }
	
	private boolean extractFeatures(DataSetDescription dsd, Instances dataset, List<String> qualitiesAvailable) throws Exception {
		Conversion.log("OK", "Extract Features", "Start extracting features for dataset: " + dsd.getId());
		
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
		for(StreamCharacterizer sc : globalMetafeatures.getStreamCharacterizers()) {

			if (qualitiesAvailable.containsAll(Arrays.asList(sc.getIDs())) == false) {
				Conversion.log("OK", "Extract Features", "Running Stream Characterizers (full data)");
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
					Conversion.log("OK", "FantailConnector", "Starting window [" + i + "," + (i + window_size) + "> (did = " + dsd.getId() + ",total size = " + dataset.numInstances() + ")");
				}
				qualities.addAll(datasetCharacteristics(dataset, i, window_size, null));
				
				for(StreamCharacterizer sc : globalMetafeatures.getStreamCharacterizers()) {
					// preventing nullpointer exception (if stream characterizer was already run)
					if (qualitiesAvailable.containsAll(Arrays.asList(sc.getIDs())) == false) {
						qualities.addAll(hashMaptoList(sc.interval(i), i, window_size));
					}
				}
			}
			
		} else {
			Conversion.log("OK", "Extract Features", "Running Batch Characterizers (full data, might take a while)");
			qualities.addAll(datasetCharacteristics(dataset, null, null, qualitiesAvailable));
			for (StreamCharacterizer sc : globalMetafeatures.getStreamCharacterizers()) {
				Map<String, Double> streamqualities = sc.global();
				if (streamqualities != null) {
					qualities.addAll(hashMaptoList(streamqualities, null, null));
				}
			}
		}
		Conversion.log("OK", "Extract Features", "Done generating features, start wrapping up");
		if(qualities.size()>0){
			DataQuality dq = new DataQuality(dsd.getId(), qualities.toArray(new Quality[qualities.size()]));
			String strQualities = xstream.toXML(dq);
			
			DataQualityUpload dqu = apiconnector.dataQualitiesUpload(Conversion.stringToTempFile(strQualities, "qualities_did_" + dsd.getId(), "xml"));
			Conversion.log("OK", "Extract Features", "DONE: " + dqu.getDid());
		}
		else{
			Conversion.log("OK", "Extract Features", "DONE: Nothing to upload");
		}
		
		return true;
	}

	private List<Quality> datasetCharacteristics(Instances fulldata, Integer start, Integer interval_size, List<String> qualitiesAvailable) throws Exception {
		List<Quality> result = new ArrayList<DataQuality.Quality>();
		Instances intervalData;
		
		// Be careful changing this!
		if (interval_size != null) {
			intervalData = new Instances(fulldata, start, Math.min(interval_size, fulldata.numInstances() - start));
			intervalData = applyFilter(intervalData, new StringToNominal(), "-R first-last");
			intervalData.setClassIndex(fulldata.classIndex());
		} else {
			intervalData = fulldata;
			// todo: use StringToNominal filter? might be too expensive
		}
		
		for(Characterizer dc : globalMetafeatures.getCharacterizers()) {
			if (qualitiesAvailable != null && qualitiesAvailable.containsAll(Arrays.asList(dc.getIDs())) == false) { 
				Conversion.log("OK","Extract Batch Features", dc.getClass().getName() + ": " + Arrays.toString(dc.getIDs()));
				Map<String,Double> qualities = dc.characterize(intervalData);
				result.addAll(hashMaptoList(qualities, start, interval_size));
			} else {
				Conversion.log("OK","Extract Batch Features", dc.getClass().getName() + " - already in database");
			}
		}
		return result;
	}
	
	public static List<Quality> qualityResultToList(Map<String, QualityResult> map, Integer start, Integer size) {
		List<Quality> result = new ArrayList<DataQuality.Quality>();
		for(String quality : map.keySet()) {
			Integer end = start != null ? start + size : null;
			QualityResult qualityResult = map.get(quality);
			result.add(new Quality(quality, qualityResult.value + "", start, end, qualityResult.index));
		}
		return result;
	}

    public static List<Quality> hashMaptoList(Map<String, Double> map, Integer start, Integer size) {
        List<Quality> result = new ArrayList<DataQuality.Quality>();
        for(String quality : map.keySet()) {
            Integer end = start != null ? start + size : null;
            result.add(new Quality(quality, map.get(quality) + "", start, end, null));
        }
        return result;
    }
	
	private static Instances applyFilter(Instances dataset, Filter filter, String options) throws Exception {
		((OptionHandler) filter).setOptions(Utils.splitOptions(options));
		filter.setInputFormat(dataset);
		return Filter.useFilter(dataset, filter);
	}
}
