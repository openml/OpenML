package org.openml.tools.dataset;

import java.io.File;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.io.ApiSessionHash;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.UploadDataSet;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import com.thoughtworks.xstream.XStream;

public class UploadDataset {

	private static final XStream xstream = XstreamXmlMapping.getInstance();
	// CONFIG
	private static final File f = new File("/Users/jan/Desktop/data_test/20_newsgroups.drift.arff");
	private static final String className = "class";
	
	public static void main( String[] args ) throws Exception {
		Config c = new Config();
		
		ApiSessionHash ash = new ApiSessionHash();
		ash.set( c.getUsername(), c.getPassword() );
		
		new UploadDataset(
				f.getName(), 
				"Automated file upload of " + f.getName(), 
				f.getName().substring( f.getName().lastIndexOf('.') + 1 ), 
				className, 
				f, 
				ash );
	}
	
	public UploadDataset( String name, String description, String format, String target, File dataset, ApiSessionHash ash ) throws Exception {
		DataSetDescription dsd = new DataSetDescription( name, description, format, target );
		String description_xml = xstream.toXML( dsd );
		System.out.println( description_xml );
		
		File desc = Conversion.stringToTempFile( description_xml, "description", "xml");
		
		UploadDataSet ud = ApiConnector.openmlDataUpload(desc, dataset, ash.getSessionHash() );
		System.out.println( xstream.toXML( ud ) );
	}
}