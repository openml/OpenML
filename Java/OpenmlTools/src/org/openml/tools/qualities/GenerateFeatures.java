package org.openml.tools.qualities;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.QueryUtils;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.io.ApiSessionHash;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.DataFeature;
import org.openml.apiconnector.xml.DataFeature.Feature;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.DataQuality.Quality;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.openml.webapplication.features.ExtractFeatures;

import com.thoughtworks.xstream.XStream;

public class GenerateFeatures {

	private static Config config = new Config("username = janvanrijn@gmail.com; password = Feyenoord2002; server = http://localhost/");

	public static void main( String[] args ) throws JSONException, IOException {
		ApiConnector api = new ApiConnector( config.getServer() );
		ApiSessionHash ash = new ApiSessionHash(api);
		ash.set( config.getUsername(), config.getPassword() );
		
		process( api, ash, 1 );
	}
	
	public static void process_all( ApiConnector api, ApiSessionHash ash ) throws JSONException, IOException {
		
		String sql = "SELECT `did` FROM `dataset` WHERE `did` NOT IN (SELECT DISTINCT `did` FROM `data_feature`) LIMIT 0,10;";
		
		int[] dataset_ids = QueryUtils.getIdsFromDatabase(api, sql);

		if( dataset_ids.length == 0 ) {
			Conversion.log("OK", "Extract Qualities", "No datasets to process. ");
		}
		
		for( int did : dataset_ids ) {
			process( api, ash, did );
		}
	}
	
	public static void process( ApiConnector api, ApiSessionHash ash, Integer did ) {
		try {
			Conversion.log("OK", "Generate Features", "Generate Features for: " + did );
			XStream xstream = XstreamXmlMapping.getInstance();
			DataSetDescription dsd = api.openmlDataDescription(did);

			Conversion.log("OK", "Generate Features", "Downloading dataset" );
			ExtractFeatures ef = new ExtractFeatures( dsd.getUrl(), dsd.getDefault_target_attribute() );
			Conversion.log("OK", "Generate Features", "Start to calculate (Feature) Qualities" );
			ArrayList<Quality> qualities= ef.getQualities();
			Conversion.log("OK", "Generate Features", "Start to extract Features" );
			ArrayList<Feature> features = ef.getFeatures();

			DataQuality dq = new DataQuality( dsd.getId(), qualities.toArray( new Quality[qualities.size()] ) );
			DataFeature df = new DataFeature( dsd.getId(), features.toArray( new Feature[features.size()] ) );
			
			System.out.println( xstream.toXML( dq ) );
			System.out.println( xstream.toXML( df ) );
			
			api.openmlDataFeatureUpload( Conversion.stringToTempFile( xstream.toXML( df ), "data_"+did+"_features", "xml"), ash.getSessionHash() );
			api.openmlDataQualityUpload( Conversion.stringToTempFile( xstream.toXML( dq ), "data_"+did+"_qualities", "xml"), ash.getSessionHash() );
		} catch( Exception e ) { 
			e.printStackTrace();
			System.err.println("Error at " + did + ": " + e.getMessage() ); 
		}
	}
}
