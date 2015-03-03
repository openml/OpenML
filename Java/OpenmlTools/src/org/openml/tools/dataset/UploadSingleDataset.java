package org.openml.tools.dataset;

import java.io.File;
import java.io.IOException;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.UploadDataSet;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

public class UploadSingleDataset {
	
	private final OpenmlConnector apiconnector;
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	
	public static void main( String[] args ) throws IOException, Exception {
		
		new UploadSingleDataset();
		
	}
	
	public UploadSingleDataset() throws IOException, Exception {
		
		Config config = new Config();
		if( config.getServer() != null ) {
			apiconnector = new OpenmlConnector( config.getServer() );
		} else { 
			apiconnector = new OpenmlConnector();
		}
		
		String[] ignore = {"animal", "class"};
		String[] tags = {};
		
		DataSetDescription dsd = new DataSetDescription( 
				"Zoo_test_jan", "2.0", "no description, it's a test", null, null, "arff", null, null,null, null, "class", ignore, tags, null );
		String description_xml = xstream.toXML( dsd );
		System.out.println(description_xml);
		UploadDataSet uds = apiconnector.dataUpload( 
			Conversion.stringToTempFile(description_xml, "zoo", "arff"), 
			new File("/vol/home/rijnjnvan/data/zoo.arff") );
		System.out.println( uds.getId() );
	}
}
