package org.openml.webapplication;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataFeature;
import org.openml.apiconnector.xml.DataFeature.Feature;
import org.openml.apiconnector.xml.DataFeatureUpload;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.DataQuality.Quality;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.openml.webapplication.features.ExtractFeatures;

import com.thoughtworks.xstream.XStream;

public class ProcessDataset {

	private final OpenmlConnector apiconnector;
	private final XStream xstream;
	
	public ProcessDataset( OpenmlConnector ac ) throws Exception {
		this( ac, null );
	}
	
	public ProcessDataset( OpenmlConnector connector, Integer dataset_id ) throws Exception {
		apiconnector = connector;
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
	
	public Integer getDatasetId() throws Exception {
		String sql = 
			"SELECT `did` FROM `dataset` WHERE `processed` IS NULL AND `error` = 'false' " + 
			"ORDER BY `upload_date` ASC"; 
		
		JSONArray runJson = (JSONArray) apiconnector.freeQuery( sql ).get("data");
		
		if( runJson.length() > 0 ) {
			int dataset_id = ((JSONArray) runJson.get( 0 )).getInt( 0 );
			return dataset_id;
		} else {
			return null;
		}
	}
	
	public JSONArray getRecord( int did ) throws Exception {
		String sql = "SELECT `did`,`file_id`,`name`,`default_target_attribute` FROM `dataset` WHERE `did` = " + did;
		JSONArray runJson = (JSONArray) apiconnector.freeQuery( sql ).get("data");
		
		if( runJson.length() > 0 ) {
			return (JSONArray) runJson.get( 0 );
		} else {
			return null;
		}
	}
	
	public void process( Integer did ) throws Exception {
		JSONArray record = getRecord(did);
		// URL featureUrl = apiconnector.getOpenmlFileUrl(record.getInt(1), record.getString(2));
		URL featureUrl = apiconnector.getOpenmlFileUrl(record.getInt(1), "");
		
		// feature string should be reconverted to null, if it was NULL in mysql
		String defaultTarget = record.getString(3).equals("") ? null : record.getString(3);
		
		try {
			ExtractFeatures extractFeatures = new ExtractFeatures(featureUrl, defaultTarget);
	
			// IMPORTANT: getQualities should be called BEFORE getFeatures
			Conversion.log( "OK", "Process Dataset", "Processing dataset " + did + " - obtaining basic qualities. " );
			List<Quality> qualities = extractFeatures.getQualities();
			Conversion.log( "OK", "Process Dataset", "Processing dataset " + did + " - obtaining features. " );
			List<Feature> features = extractFeatures.getFeatures();
			DataFeature datafeature = new DataFeature(did, features.toArray(new Feature[features.size()]) );
			File dataFeatureFile = Conversion.stringToTempFile( xstream.toXML( datafeature ), "features-did" + did, "xml");
			apiconnector.dataFeaturesUpload( dataFeatureFile );
			
			DataQuality dataquality = new DataQuality(did, qualities.toArray(new Quality[qualities.size()]) );
			File dataQualityFile = Conversion.stringToTempFile( xstream.toXML( dataquality ), "qualities-did" + did, "xml");
			apiconnector.dataQualitiesUpload( dataQualityFile );
			
			Conversion.log( "OK", "Process Dataset", "Dataset " + did + " - Processed successfully. " );
		} catch(Exception e ) {
			DataFeature datafeature = new DataFeature(did, e.getMessage() );
			File dataFeatureFile = Conversion.stringToTempFile( xstream.toXML( datafeature ), "features-error-did" + did, "xml");
			DataFeatureUpload dfu = apiconnector.dataFeaturesUpload( dataFeatureFile );
			Conversion.log( "Error", "Process Dataset", "Dataset " + dfu.getDid() + " - Error: " + e.getMessage() );
		} catch (OutOfMemoryError oome) {
		    // move on
			DataFeature datafeature = new DataFeature(did, oome.getMessage() );
			File dataFeatureFile = Conversion.stringToTempFile( xstream.toXML( datafeature ), "features-error-did" + did, "xml");
			DataFeatureUpload dfu = apiconnector.dataFeaturesUpload( dataFeatureFile );
			Conversion.log( "Error", "Process Dataset", "Dataset " + dfu.getDid() + " - Error: " + oome.getMessage() );
		}
	}
}
