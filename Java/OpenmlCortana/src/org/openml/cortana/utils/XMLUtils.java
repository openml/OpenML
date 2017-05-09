package org.openml.cortana.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.SetupParameters;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.DataFeature.Feature;
import org.openml.apiconnector.xml.SetupParameters.Parameter;
import org.openml.apiconnector.xml.Task.Input;
import org.openml.apiconnector.xml.Task.Input.Data_set;
import org.openml.cortana.xml.AutoRun;
import org.openml.cortana.xml.AutoRun.Experiment.Table.Column;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

public class XMLUtils {
	private static String XML_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<!DOCTYPE autorun SYSTEM \"autorun.dtd\">\n";
	
	public static File autoRunToTmpFile(AutoRun ar, String runName) throws IOException {
		XStream xstream = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("_-", "_")));
		xstream.processAnnotations(AutoRun.class);
		File runXMLtmp = Conversion.stringToTempFile(XML_PREFIX + xstream.toXML(ar), runName, "xml");
		return runXMLtmp;
	}
	
	public static AutoRun generateAutoRunFromJson(OpenmlConnector openml, String setupString, int taskId) throws Exception {
		return generateAutoRun(openml, jsonToMap(setupString), taskId);
	}
	
	public static AutoRun generateAutoRunFromSetup(OpenmlConnector openml, int setupId, int taskId) throws Exception {
		SetupParameters sp = openml.setupParameters(setupId);
		Map<String, String> searchParams = new HashMap<String, String>();
		
		for (Parameter p : sp.getParameters()) {
			searchParams.put(p.getParameter_name(), p.getValue());
		}
		return generateAutoRun(openml, searchParams, taskId);
	}

	private static AutoRun generateAutoRun(OpenmlConnector openml, Map<String,String> searchParams, int taskId) throws Exception {
		Task task = openml.taskGet(taskId);
		
		if (task.getTask_type().equals("Subgroup Discovery") == false) {
			throw new Exception("Wrong task type. ");
		}
		
		Integer dataset_id = null;
		String target_feature = null;
		String target_value = null;
		Double time_limit = null;
		String quality_measure = null;
		
		for (Input i : task.getInputs()) {
			if (i.getName().equals("source_data")) {
				Data_set ds = i.getData_set();
				
				dataset_id = ds.getData_set_id();
				target_feature = ds.getTarget_feature();
				target_value = ds.getTarget_value();
				
			}
			
			if (i.getName().equals("time_limit")) {
				time_limit = i.getTime_limit();
			}
			
			if (i.getName().equals("quality_measure")) {
				quality_measure = i.getQuality_measure();
			}
		}
		searchParams.put("time_limit", "" + time_limit);
		
		DataSetDescription dsd = openml.dataGet(dataset_id);
		File dataset = dsd.getDataset(openml.getApiKey());
		File datasetTmp = Conversion.stringToTempFile("", dsd.getName(), dsd.getFormat());
		IOUtils.copy(new FileInputStream(dataset), new FileOutputStream(datasetTmp));
		
		Feature[] features = openml.dataFeatures(dataset_id).getFeatures();
		Column[] column = new Column[features.length];
		for (int i = 0; i < features.length; ++i) {
			column[i] = new Column(features[i].getDataType(), features[i].getName(), i, "0.0", true);
		}
		
		AutoRun ar = new AutoRun(
			target_feature, target_value, quality_measure, 
			Integer.parseInt(searchParams.get("search_depth")), 
			Integer.parseInt(searchParams.get("minimum_coverage")), 
			Double.parseDouble(searchParams.get("maximum_coverage_fraction")), 
			Integer.parseInt(searchParams.get("maximum_subgroups")), 
			Double.parseDouble(searchParams.get("time_limit")), 
			searchParams.get("search_strategy"), 
			Boolean.parseBoolean(searchParams.get("use_nominal_sets")), 
			Integer.parseInt(searchParams.get("search_strategy_width")), 
			searchParams.get("numeric_operators"), 
			searchParams.get("numeric_strategy"), 
			Integer.parseInt(searchParams.get("nr_bins")), 
			Integer.parseInt(searchParams.get("nr_threads")), 
			dsd.getName(), datasetTmp.getName(), column);
		return ar;
	}
	
	private static final Map<String, String> jsonToMap(String s) {
		Map<String, String> searchParams = new HashMap<String, String>();
		JSONObject jObject = new JSONObject(s);
        Iterator<?> keys = jObject.keys();
        searchParams = new HashMap<String, String>();
        
        while( keys.hasNext() ){
            String key = (String)keys.next();
            String value = jObject.getString(key); 
            searchParams.put(key, value);
        }
        return searchParams;
	}
}
