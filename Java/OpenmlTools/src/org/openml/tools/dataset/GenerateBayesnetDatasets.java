package org.openml.tools.dataset;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import moa.DoTask;

import org.apache.commons.lang3.StringUtils;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.io.ApiSessionHash;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.settings.Constants;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.UploadDataSet;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import weka.core.Instances;

import com.thoughtworks.xstream.XStream;

public class GenerateBayesnetDatasets {
	
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	private static final ApiSessionHash ash = new ApiSessionHash();
	private static final int MIN_ATTRIBUTES = 10;
	private static final int MAX_ATTRIBUTES = 100;
	private static final boolean SEND_RESULT = false;
	
	private static final File outputDirectory = new File( "/Users/jan/Desktop/BayesNetTest/" );
	
	private static final String[] searchStrings = {
		"weka.classifiers.bayes.net.search.local.K2 -P 15 -S BAYES",
//		"weka.classifiers.bayes.net.search.local.GeneticSearch -L 10 -A 20 -U 10 -R 1 -M -C -S BAYES",
//		"weka.classifiers.bayes.net.search.local.SimulatedAnnealing -A 10.0 -U 10000 -D 0.999 -R 1 -S BAYES"
	};
	
	public static void main( String[] args ) throws Exception {
		new GenerateBayesnetDatasets();
	}
	
	public GenerateBayesnetDatasets() throws Exception {
		ArrayList<Long> elapsedTimes = new ArrayList<Long>();
		Config c = new Config();
		ash.set(c.getUsername(), c.getPassword());
		
		for( int i = 1; i <= 62; ++i ) {
			Conversion.log("INFO", "Download Dataset", "Downloading dataset " + i);
			DataSetDescription dsd = ApiConnector.openmlDataDescription( i );
			
			Instances dataset = new Instances( new FileReader( dsd.getDataset() ) );
			if( dataset.numAttributes() < MIN_ATTRIBUTES || dataset.numAttributes() > MAX_ATTRIBUTES ) {
				Conversion.log("INFO", "Dataset evaluation", "Dataset " + i + " " + dsd.getName() + " not used. Got " + dataset.numAttributes() + " attributes. (Interested in Range ["+MIN_ATTRIBUTES+","+MAX_ATTRIBUTES+"])" );
				continue;
			} 
			
			for( int j = 0; j < searchStrings.length; ++j ) {
				long startTime = System.currentTimeMillis();
				File generatedDataset = generateDataset( dsd, j );
				long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
				elapsedTimes.add( elapsedTime );
				if( SEND_RESULT ) {
					uploadDataset( dsd, generatedDataset );
				}
			}
			
			Conversion.log("INFO", "Generating Dataset", "Generating dataset ");
		}
		System.out.println( "Times Elapsed: " + elapsedTimes );
	}
	
	private File generateDataset( DataSetDescription dsd, int optionNumber ) throws Exception {
		String datasetname = dsd.getName() + "_" + optionNumber + "_" + searchAlgorithmToString( searchStrings[optionNumber] );
		String outputFilename = outputDirectory.getAbsolutePath() + "/" + datasetname;
		
		String[] taskArgs = new String[7];
		taskArgs[0] = "WriteStreamToARFFFile";
		taskArgs[1] = "-f";
		taskArgs[2] = outputFilename;
		taskArgs[3] = "-m";
		taskArgs[4] = "50000";
		taskArgs[5] = "-s";
		taskArgs[6] = "(generators.BayesianNetworkGenerator -f (" + dsd.getDataset().getAbsolutePath() + ") -a (" + searchStrings[optionNumber] + ") -p (" + outputFilename + ".xml))";
		
		Conversion.log("INFO", "Build dataset", "CMD: " + StringUtils.join( taskArgs, ' ' ) );
		DoTask.main( taskArgs );
		Conversion.log("INFO", "Build dataset", "DONE" );
		
		return new File( outputFilename );
	}
	
	private void uploadDataset( DataSetDescription dsd, File generatedDataset ) throws Exception {
		String datasetname = generatedDataset.getName();
		String description = "";
		
		DataSetDescription outputDatasetDescription = new DataSetDescription( datasetname, description, Constants.DATASET_FORMAT, dsd.getDefault_target_attribute() );
		String outputDatasetString = xstream.toXML( outputDatasetDescription );
		
		File outputDataset = Conversion.stringToTempFile( outputDatasetString, datasetname, Constants.DATASET_FORMAT );
		UploadDataSet ud = ApiConnector.openmlDataUpload( outputDataset, generatedDataset, ash.getSessionHash() );
		
		System.out.println( xstream.toXML( ApiConnector.openmlDataDescription( ud.getId() ) ) );
	}
	
	private String searchAlgorithmToString( String searchAlgorithmToString ) {
		String[] parts = searchAlgorithmToString.split(" ");
		return parts[0].substring( parts[0].lastIndexOf('.') + 1 );
	}
}
