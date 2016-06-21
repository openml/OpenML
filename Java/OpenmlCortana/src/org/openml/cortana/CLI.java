package org.openml.cortana;

import java.io.*;

import nl.liacs.subdisc.gui.*;

import org.openml.apiconnector.algorithms.*;
import org.openml.apiconnector.io.*;
import org.openml.apiconnector.settings.*;
import org.openml.apiconnector.xml.*;
import org.openml.apiconnector.xml.DataFeature.Feature;
import org.openml.apiconnector.xml.Task.Input;
import org.openml.apiconnector.xml.Task.Input.Data_set;
import org.openml.cortana.AutoRun.Experiment.Table.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.*;

public class CLI {

	public static void main(String[] args) throws Exception {
		Config c = new Config();
		OpenmlConnector openml = new OpenmlConnector( c.getApiKey());
		XStream xstream = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("_-", "_")));
		xstream.processAnnotations(AutoRun.class);
		
		Task task = openml.taskGet(52949);
		
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
		
		Feature[] features = openml.dataFeatures(dataset_id).getFeatures();
		Column[] column = new Column[features.length];
		for (int i = 0; i < features.length; ++i) {
			column[i] = new Column(features[i].getDataType(), features[i].getName(), i, "0.0", true);
		}
		
		AutoRun ar = new AutoRun(target_feature, target_value, quality_measure, 1, 2, time_limit, "beam", false, 100, "<html>&#8804;, &#8805;</html>", "best-bins", 8, 8, dsd.getName(), dataset.getName(), column);
		
		File runXMLtmp = Conversion.stringToTempFile("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<!DOCTYPE autorun SYSTEM \"autorun.dtd\">\n" + xstream.toXML(ar), "jantest", "xml");
		File runXML = new File(dataset.getParentFile() + "/" + "jantest.xml");
		runXMLtmp.renameTo(runXML);
		
		System.out.println(xstream.toXML(ar));
		
		String[] arguments = {runXML.getAbsolutePath(), "1"};
		
		SubDisc.main(arguments);
		
		
	}
}
