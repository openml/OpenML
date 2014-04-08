package org.openml.tools.dataset;

import java.io.File;

import com.thoughtworks.xstream.XStream;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.io.ApiSessionHash;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

public class TransferDatasets {

	private static final String TO = "http://localhost/";
	private static final String FROM = "http://www.openml.org/";
	
	public static void main( String[] args ) throws Exception {
		new TransferDatasets();
	}
	
	public TransferDatasets() throws Exception {
		Config c = new Config();
		ApiConnector.API_URL = TO;
		XStream xstream = XstreamXmlMapping.getInstance();
		ApiSessionHash ash = new ApiSessionHash();
		if( ash.set(c.getUsername(), c.getPassword() ) == false ) {
			throw new Exception("Username/password incorrect");
		}
		
		for( int i = 1; i <= 62; ++i ) {
			try {
				ApiConnector.API_URL = FROM;
				DataSetDescription dsd = ApiConnector.openmlDataDescription( i );
				ApiConnector.API_URL = TO;
				File dataset = dsd.getDataset();
				dsd.unsetUrl();
				String descriptionXML = xstream.toXML(dsd);
				
				File description = Conversion.stringToTempFile( descriptionXML, dsd.getName() + "_description", "xml" );
				System.out.println( xstream.toXML(dsd)  );
				ApiConnector.openmlDataUpload( description, dataset, ash.getSessionHash() );
				System.out.println("Succes at #" + i);
			} catch( Exception e ) {
				e.printStackTrace();
				System.out.println( "Error at #" + i + ": " + e.getMessage() );
			}
		}
	}
}
