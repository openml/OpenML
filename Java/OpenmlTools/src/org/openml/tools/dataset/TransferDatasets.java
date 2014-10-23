package org.openml.tools.dataset;

import java.io.File;

import com.thoughtworks.xstream.XStream;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.UploadDataSet;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

public class TransferDatasets {

	private static final String FROM = "http://www.openml.org/";
	
	private final OpenmlConnector apiFrom;
	private final OpenmlConnector apiTo;
	
	public static void main( String[] args ) throws Exception {
		new TransferDatasets();
	}
	
	public TransferDatasets() throws Exception {
		Config config = new Config();
		if( config.getServer() != null ) {
			apiTo = new OpenmlConnector( config.getServer() );
		} else { 
			apiTo = new OpenmlConnector();
		}
		apiFrom = new OpenmlConnector( FROM );
		XStream xstream = XstreamXmlMapping.getInstance();
		if( apiTo.setCredentials(config.getUsername(), config.getPassword() ) == false ) {
			throw new Exception("Username/password incorrect");
		}
		
		for( int i = 1; i <= 62; ++i ) {
			try {
				DataSetDescription dsd = apiFrom.openmlDataDescription( i );
				File dataset = dsd.getDataset( null );
				dsd.unsetUrl();
				dsd.unsetId();
				String descriptionXML = xstream.toXML(dsd);
				
				File description = Conversion.stringToTempFile( descriptionXML, dsd.getName() + "_description", "xml" );
				//System.out.println( xstream.toXML(dsd)  );
				UploadDataSet uds = apiTo.openmlDataUpload( description, dataset );
				System.out.println("Succes at #" + i + " id: " + uds.getId() );
			} catch( Exception e ) {
				e.printStackTrace();
				System.out.println( "Error at #" + i + ": " + e.getMessage() );
			}
		}
	}
}
