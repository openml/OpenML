package org.openml.cortana.utils;

import java.io.File;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.Flow;
import org.openml.apiconnector.xml.Flow.Parameter;
import org.openml.apiconnector.xml.FlowExists;
import org.openml.apiconnector.xml.UploadFlow;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

public class SdFlow {
	public static final String NAME = "nl.liacs.subdisc.SubgroupDiscovery";
	public static final String EXTERNAL_VERSION = "3103";
	public static final String CUSTOM_NAME = "SubgroupDiscovery";
	public static final String CLASS_NAME = "nl.liacs.subdisc.SubgroupDiscovery";
	public static final String DESCRIPTION = "Subgroup Discovery search algorithm. ";
	public static final String[] CREATOR = {"Arno_Knobbe", "Marving_Meeng"};
	public static final String[] CONTRIBUTOR = {"Matthijs_van_Leeuwen", "Wouter_Duivensteijn", "Claudio_Sa"};
	public static final String LICENSE = null;
	public static final String LANGUAGE = "English";
	public static final String DEPENDENCIES = "cortana." + EXTERNAL_VERSION;
	public static final String[] TAGS = {"Cortana"};
	
	public static final Parameter PARAMETERS[] = {
		new Parameter("search_depth", "numeric", "1", null),
		new Parameter("minimum_coverage", "numeric", "2", null),
		new Parameter("maximum_coverage_fraction", "numeric", "1.0", null),
		new Parameter("maximum_subgroups", "numeric", "0", null),
		new Parameter("maximum_time", "numeric", "1", null),
		new Parameter("search_strategy", "option", "beam", null),
		new Parameter("use_nominal_sets", "flag", "false", null),
		new Parameter("search_strategy_width", "numeric", "100", null),
		new Parameter("numeric_operators", "option", "<html>&#8804;, &#8805;</html>", null),
		new Parameter("numeric_strategy", "option", "best-bins", null),
		new Parameter("nr_bins", "numeric", "8", null),
		new Parameter("nr_threads", "numeric", "8", null),
		new Parameter("time_limit", "numeric", "1", null),
		new Parameter("alpha", "numeric", "0.5", null),
		new Parameter("beta", "numeric", "1.0", null),
		new Parameter("post_processing_do_autorun", "flag", "true", null),
		new Parameter("post_processing_count", "numeric", "20", null),
		new Parameter("beam_seed", "numeric", "", null),
		new Parameter("overall_ranking_loss", "numeric", "0.0", null),
	};
	
	private static Flow getFlow() {
		Flow flow = new Flow(NAME, CUSTOM_NAME, CLASS_NAME, EXTERNAL_VERSION, DESCRIPTION, CREATOR, CONTRIBUTOR, LICENSE, LANGUAGE, null, null, DEPENDENCIES, TAGS);
		for (Parameter p : PARAMETERS) {
			flow.addParameter(p);
		}
		
		return flow;
	}
	
	public static int getFlowId(OpenmlConnector apiconnector) throws Exception {
		Flow flow = getFlow();
		try {
			// First ask OpenML whether this implementation already exists
			FlowExists result = apiconnector.flowExists(flow.getName(), flow.getExternal_version());
			if(result.exists()) return result.getId();
		} catch( Exception e ) { /* Suppress Exception since it is totally OK. */ }
		// It does not exist. Create it. 
		String xml = XstreamXmlMapping.getInstance().toXML(flow);
		System.err.println(xml);
		File implementationFile = Conversion.stringToTempFile(xml, flow.getName(), "xml");
		UploadFlow ui = apiconnector.flowUpload(implementationFile, null, null);
		return ui.getId();
	}
}
