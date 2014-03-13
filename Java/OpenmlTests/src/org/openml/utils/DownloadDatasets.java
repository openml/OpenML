package org.openml.utils;


import java.io.File;

import com.thoughtworks.xstream.XStream;

import openml.algorithms.Conversion;
import openml.io.ApiConnector;
import openml.io.ApiSessionHash;
import openml.xml.DataSetDescription;
import openml.xstream.XstreamXmlMapping;

public class DownloadDatasets {

	public static void main( String[] args ) throws Exception {
		new DownloadDatasets();
	}
	
	public DownloadDatasets() throws Exception {
		XStream xstream = XstreamXmlMapping.getInstance();
		ApiSessionHash ash = new ApiSessionHash();
		ash.set("janvanrijn@gmail.com", "Feyenoord2002");
		
		for( int i = 1; i <= 62; ++i ) {
			try {
				ApiConnector.API_URL = "http://www.openml.org/";
				DataSetDescription dsd = ApiConnector.openmlDataDescription( i );
				ApiConnector.API_URL = "http://localhost/";
				File dataset = Conversion.instancesToTempFile( dsd.getDataset(), dsd.getName(), "arff" );
				dsd.unsetUrl();
				String descriptionXML = xstream.toXML(dsd);
				
				File description = Conversion.stringToTempFile( descriptionXML, dsd.getName() + "_description", "xml" );
				
				ApiConnector.openmlDataUpload( description, dataset, ash.getSessionHash() );
				System.out.println("Succes at #" + i);
			} catch( Exception e ) {
				e.printStackTrace();
				System.out.println( "Error at #" + i + ": " + e.getMessage() );
			}
		}
	}
}
