package org.openml.tools.dataset;

import java.io.File;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.io.ApiSessionHash;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.UploadDataSet;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

public class UploadDataset {

	private static final XStream xstream = XstreamXmlMapping.getInstance();
	
	public static void main( String[] args ) throws Exception {
		new UploadDataset(
				"BNG_Sonar", 
				"BNG_Sonar", 
				"arff", 
				"class", 
				"janvanrijn@gmail.com", 
				"", // TODO fill in
				"/Users/jan/Desktop/BNG_Sonar.arff");
	}
	
	public UploadDataset( String name, String description, String format, String target, String username, String password, String filepath ) throws Exception {
		ApiSessionHash ash = new ApiSessionHash();
		ash.set( username, password );
		DataSetDescription dsd = new DataSetDescription( name, description, format, target );
		String description_xml = xstream.toXML( dsd );
		System.out.println( description_xml );
		
		File dataset = new File( filepath );
		File desc = Conversion.stringToTempFile( description_xml, "description", "xml");
		
		UploadDataSet ud = ApiConnector.openmlDataUpload(desc, dataset, ash.getSessionHash() );
		System.out.println( xstream.toXML( ud ) );
	}
}