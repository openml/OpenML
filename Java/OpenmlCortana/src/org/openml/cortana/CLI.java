package org.openml.cortana;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.openml.apiconnector.algorithms.*;
import org.openml.apiconnector.io.*;
import org.openml.apiconnector.settings.*;
import org.openml.apiconnector.xml.*;
import org.openml.apiconnector.xml.DataFeature.Feature;
import org.openml.apiconnector.xml.Run.Parameter_setting;
import org.openml.apiconnector.xml.Task.Input;
import org.openml.apiconnector.xml.Task.Input.Data_set;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.openml.cortana.utils.SdFlow;
import org.openml.cortana.xml.AutoRun;
import org.openml.cortana.xml.AutoRun.Experiment.Table.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.*;

public class CLI {

	public static final int TASK_ID = 13907;
	public static final String[] TAGS = {"Cortana"};
	private static String jarlocation = "lib/cortana.3073.jar";
	
	public static void main(String[] args) throws Exception {
		Config c = new Config();
		OpenmlConnector openml = new OpenmlConnector(c.getServer(),c.getApiKey());
		XStream xstream = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("_-", "_")));
		xstream.processAnnotations(AutoRun.class);
		String current_run_name = "Cortana-Run-" + ManagementFactory.getRuntimeMXBean().getName();
		
		Task task = openml.taskGet(TASK_ID);
		
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
		
		DataSetDescription dsd = openml.dataGet(dataset_id);
		File dataset = dsd.getDataset(c.getApiKey());
		File datasetTmp = Conversion.stringToTempFile("", dsd.getName(), dsd.getFormat());
		IOUtils.copy(new FileInputStream(dataset), new FileOutputStream(datasetTmp));
		
		Feature[] features = openml.dataFeatures(dataset_id).getFeatures();
		Column[] column = new Column[features.length];
		for (int i = 0; i < features.length; ++i) {
			column[i] = new Column(features[i].getDataType(), features[i].getName(), i, "0.0", true);
		}
		
		
		Map<String,String> searchParams = new HashMap<String, String>();
		searchParams.put("search_depth", "1");
		searchParams.put("minimum_coverage", "2");
		searchParams.put("maximum_coverage_fraction", "2");
		searchParams.put("maximum_subgroups", "2");
		searchParams.put("time_limit", "" + time_limit);
		searchParams.put("search_strategy", "beam");
		searchParams.put("use_nominal_sets", "false");
		searchParams.put("search_strategy_width", "100");
		searchParams.put("numeric_operators", "<html>&#8804;, &#8805;</html>");
		searchParams.put("numeric_strategy", "best-bins");
		searchParams.put("nr_bins", "8");
		searchParams.put("nr_threads", "8");
		
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
		
		File runXMLtmp = Conversion.stringToTempFile("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<!DOCTYPE autorun SYSTEM \"autorun.dtd\">\n" + xstream.toXML(ar), current_run_name, "xml");
		String cmd = "java -jar " + jarlocation + " " + runXMLtmp.getAbsolutePath() + " 0 1";
	//	String[] cliArguments = {runXMLtmp.getAbsolutePath(), "0", "1"};
		
		executeCommand(cmd);
		
		File dir = runXMLtmp.getParentFile();
		
		File resultTxt = null;
		for (File f : dir.listFiles()) {
			if (f.getName().startsWith(current_run_name) && f.getName().endsWith(".txt")) {
				
				if (resultTxt == null) {
					resultTxt = f;
				} else {
					throw new Exception("Multiple candidates for outputfile. ");
				}
			}
		}
		if (resultTxt == null) { throw new Exception("Result txt file not found. "); }
		File subgroups = File.createTempFile("subgroups", ".csv");
		resultTxt.renameTo(subgroups);
		

		int flow_id = SdFlow.getFlowId(openml);
		Parameter_setting[] params = new Parameter_setting[searchParams.size()];
		int i = 0;
		for (String key : searchParams.keySet()) {
			params[i++] = new Parameter_setting(flow_id, key, searchParams.get(key));
		}
		
		Run r = new Run(TASK_ID, null, flow_id, null, params, TAGS);
		File runfile = Conversion.stringToTempFile(XstreamXmlMapping.getInstance().toXML(r), "run", "xml");
		
		System.out.println(XstreamXmlMapping.getInstance().toXML(r));
		
		Map<String,File> uploadFiles = new HashMap<String, File>();
		uploadFiles.put("subgroups", subgroups);
		
		UploadRun ur = openml.runUpload(runfile, uploadFiles);
		
		System.out.println(ur.getRun_id());
	}
	
	private static boolean executeCommand(String cmd) {
		try {
			@SuppressWarnings("unused")
			String line;
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader bri = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			BufferedReader bre = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			while ((line = bri.readLine()) != null) {
				//System.out.println(line);
			}
			bri.close();
			while ((line = bre.readLine()) != null) {
				//System.out.println(line);
			}
			bre.close();
			p.waitFor();
			return true;
		} catch (Exception err) {
			err.printStackTrace();
			return false;
		}
	}
}
