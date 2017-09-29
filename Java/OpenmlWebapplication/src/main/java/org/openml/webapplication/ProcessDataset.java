package org.openml.webapplication;

import java.io.BufferedReader;
import java.io.File;
import java.net.URL;
import java.util.List;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.Input;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataFeature;
import org.openml.apiconnector.xml.DataFeature.Feature;
import org.openml.apiconnector.xml.DataFeatureUpload;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.DataQuality.Quality;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.DataUnprocessed;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.openml.webapplication.features.ExtractFeatures;
import org.openml.webapplication.settings.Settings;

import weka.core.Instances;

import com.thoughtworks.xstream.XStream;

public class ProcessDataset {

	private final OpenmlConnector apiconnector;
	private final XStream xstream;
	
	public ProcessDataset(OpenmlConnector ac, String mode) throws Exception {
		this(ac, null, mode);
	}
	
	public ProcessDataset(OpenmlConnector connector, Integer dataset_id, String mode) throws Exception {
		apiconnector = connector;
		xstream = XstreamXmlMapping.getInstance();
		if(dataset_id != null) {
			Conversion.log( "OK", "Process Dataset", "Processing dataset " + dataset_id + " on special request. ");
			process(dataset_id);
		} else {
			DataUnprocessed du = connector.dataUnprocessed(Settings.EVALUATION_ENGINE_ID, mode);
			
			while(du != null) {
				dataset_id = du.getDatasets()[0].getDid();
				Conversion.log("OK", "Process Dataset", "Processing dataset " + dataset_id + " as obtained from database. ");
				process( dataset_id );
				du = connector.dataUnprocessed(Settings.EVALUATION_ENGINE_ID, mode);
			}
			Conversion.log("OK", "Process Dataset", "No more datasets to process. ");
		}
	}
	
	public void process(Integer did) throws Exception {
		DataSetDescription dsd = apiconnector.dataGet(did);
		URL datasetURL = apiconnector.getOpenmlFileUrl(dsd.getFile_id(), dsd.getName() + "." + dsd.getFormat());
		String defaultTarget = dsd.getDefault_target_attribute();
		
		try {
			Instances dataset = new Instances(new BufferedReader(Input.getURL(datasetURL)));
			Conversion.log( "OK", "Process Dataset", "Processing dataset " + did + " - obtaining basic qualities. " );
			List<Quality> qualities = ExtractFeatures.getQualities(dataset,defaultTarget);
			Conversion.log( "OK", "Process Dataset", "Processing dataset " + did + " - obtaining features. " );
			List<Feature> features = ExtractFeatures.getFeatures(dataset,defaultTarget);
			DataFeature datafeature = new DataFeature(did, Settings.EVALUATION_ENGINE_ID, features.toArray(new Feature[features.size()]) );
			File dataFeatureFile = Conversion.stringToTempFile( xstream.toXML(datafeature), "features-did" + did, "xml");
			apiconnector.dataFeaturesUpload(dataFeatureFile);
			
			DataQuality dataquality = new DataQuality(did, Settings.EVALUATION_ENGINE_ID, qualities.toArray(new Quality[qualities.size()]) );
			File dataQualityFile = Conversion.stringToTempFile( xstream.toXML(dataquality), "qualities-did" + did, "xml");
			apiconnector.dataQualitiesUpload(dataQualityFile);
			
			Conversion.log("OK", "Process Dataset", "Dataset " + did + " - Processed successfully. ");
		} catch(Exception e) {
			e.printStackTrace();
			DataFeature datafeature = new DataFeature(did, Settings.EVALUATION_ENGINE_ID, e.getMessage());
			File dataFeatureFile = Conversion.stringToTempFile(xstream.toXML(datafeature), "features-error-did" + did, "xml");
			DataFeatureUpload dfu = apiconnector.dataFeaturesUpload( dataFeatureFile );
			Conversion.log("Error", "Process Dataset", "Dataset " + dfu.getDid() + " - Error: " + e.getMessage());
		} catch (OutOfMemoryError oome) {
		    // move on
			DataFeature datafeature = new DataFeature(did, Settings.EVALUATION_ENGINE_ID, oome.getMessage());
			File dataFeatureFile = Conversion.stringToTempFile(xstream.toXML(datafeature), "features-error-did" + did, "xml");
			DataFeatureUpload dfu = apiconnector.dataFeaturesUpload(dataFeatureFile);
			Conversion.log("Error", "Process Dataset", "Dataset " + dfu.getDid() + " - Error: " + oome.getMessage());
		}
	}
}
