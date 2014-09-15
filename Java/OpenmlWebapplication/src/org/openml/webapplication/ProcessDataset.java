package org.openml.webapplication;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.io.ApiSessionHash;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.DataFeature;
import org.openml.apiconnector.xml.DataFeature.Feature;
import org.openml.apiconnector.xml.DataFeatureUpload;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.DataQuality.Quality;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.openml.webapplication.features.ExtractFeatures;

import com.thoughtworks.xstream.XStream;

public class ProcessDataset {

	private final ApiConnector apiconnector;
	private final XStream xstream;
	private final ApiSessionHash ash;
	
	public ProcessDataset( Config config ) throws Exception {
		this( config, null );
	}
	
	public ProcessDataset( Config config, Integer dataset_id ) throws Exception {

		apiconnector = new ApiConnector( config.getServer() );
		ash = new ApiSessionHash( apiconnector );
		ash.set( config.getUsername(), config.getPassword() );
		xstream = XstreamXmlMapping.getInstance();
		
		if( dataset_id != null ) {
			Conversion.log( "OK", "Process Dataset", "Processing dataset " + dataset_id + " on special request. " );
			process( dataset_id );
		} else {
			dataset_id = getDatasetId();
			while( dataset_id != null ) {
				Conversion.log( "OK", "Process Dataset", "Processing dataset " + dataset_id + " as obtained from database. " );
				process( dataset_id );
				dataset_id = getDatasetId();
			}
			Conversion.log( "OK", "Process Dataset", "No more datasets to process. " );
		}
	}
	
	public Integer getDatasetId() throws JSONException, IOException {
		String sql = 
			"SELECT `did`,`url`,`default_target_attribute`,`upload_date` " + 
			"FROM `dataset` WHERE `processed` IS NULL AND `error` = 'false'" + 
			"ORDER BY `upload_date` ASC"; 
		
		JSONArray runJson = (JSONArray) apiconnector.openmlFreeQuery( sql ).get("data");
		
		if( runJson.length() > 0 ) {
			int dataset_id = ((JSONArray) runJson.get( 0 )).getInt( 0 );
			return dataset_id;
		} else {
			return null;
		}
	}
	
	public JSONArray getRecord( int did ) throws JSONException, IOException {
		String sql = 
			"SELECT `did`,`url`,`default_target_attribute`,`upload_date` " + 
			"FROM `dataset` WHERE `did` = " + did;
		JSONArray runJson = (JSONArray) apiconnector.openmlFreeQuery( sql ).get("data");
		
		if( runJson.length() > 0 ) {
			return (JSONArray) runJson.get( 0 );
		} else {
			return null;
		}
	}
	
	public void process( Integer did ) throws Exception {
		JSONArray record = getRecord(did);
		String didStr = record.getString( 1 );
		// feature string should be reconverted to null, if it was NULL in mysql
		String featureStr = record.getString( 2 ).equals("") ? null : record.getString( 2 );
		ExtractFeatures extractFeatures = new ExtractFeatures(didStr, featureStr);

		// IMPORTANT: getQualities should be called BEFORE getFeatures
		Conversion.log( "OK", "Process Dataset", "Processing dataset " + did + " - obtaining basic qualities. " );
		List<Quality> qualities = extractFeatures.getQualities();
		Conversion.log( "OK", "Process Dataset", "Processing dataset " + did + " - obtaining features. " );
		List<Feature> features = extractFeatures.getFeatures();
		try {
			DataFeature datafeature = new DataFeature(did, features.toArray(new Feature[features.size()]) );
			File dataFeatureFile = Conversion.stringToTempFile( xstream.toXML( datafeature ), "features-did" + did, "xml");
			apiconnector.openmlDataFeatureUpload( dataFeatureFile, ash.getSessionHash() );
			
			DataQuality dataquality = new DataQuality(did, qualities.toArray(new Quality[qualities.size()]) );
			File dataQualityFile = Conversion.stringToTempFile( xstream.toXML( dataquality ), "qualities-did" + did, "xml");
			apiconnector.openmlDataQualityUpload( dataQualityFile, ash.getSessionHash() );
			
			Conversion.log( "OK", "Process Dataset", "Dataset " + did + " - Processed successfully. " );
		} catch(Exception e ) {
			DataFeature datafeature = new DataFeature(did, e.getMessage() );
			File dataFeatureFile = Conversion.stringToTempFile( xstream.toXML( datafeature ), "features-error-did" + did, "xml");
			DataFeatureUpload dfu = apiconnector.openmlDataFeatureUpload( dataFeatureFile, ash.getSessionHash() );
			Conversion.log( "Error", "Process Dataset", "Dataset " + dfu.getDid() + " - Error: " + e.getMessage() );
		}
	}
}
