package org.openml.tools.dataset;

import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import moa.DoTask;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.DateParser;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.settings.Constants;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.UploadDataSet;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import weka.core.Instances;

import com.thoughtworks.xstream.XStream;

public class GenerateBayesnetDatasets {
	
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	private static final int MIN_ATTRIBUTES = 8;
	private static final int MAX_ATTRIBUTES = 100;
	private static final int MIN_NUM_ORIG_INSTANCES = 128;
	private static final int TARGET_NUM_INSTANCES = 1000000;
	private static final boolean SEND_RESULT = true;
	
	private static final Integer[] driftFrequencies = { 1000, 5000, 10000 };
	private static final Integer[] driftImpacts = { 1, 5, 10 };
	
	private final OpenmlConnector apiconnector;
	
	private static final File outputDirectory = new File( "data/" );
	
	private static final String searchAlgorithm = "weka.classifiers.bayes.net.search.local.K2 -P 15 -S BAYES";
	
	public static void main( String[] args ) throws Exception {
		new GenerateBayesnetDatasets();
	}
	
	public GenerateBayesnetDatasets() throws Exception {
		Config config = new Config();
		this.apiconnector = new OpenmlConnector( config.getApiKey() );
		
		int eligableDatasets = 0;
		List<Integer> errors = new ArrayList<Integer>();
		
		for( int iDatasets = 10; iDatasets <= 10; iDatasets++ ) {
			try {
				Conversion.log("INFO", "Download Dataset", "Downloading dataset " + iDatasets);
				DataSetDescription dsd = apiconnector.dataGet( iDatasets );
				
				Instances dataset = new Instances( new FileReader( dsd.getDataset( apiconnector.getApiKey() ) ) );
				if( dataset.numAttributes() < MIN_ATTRIBUTES || dataset.numAttributes() > MAX_ATTRIBUTES ) {
					Conversion.log("INFO", "Dataset evaluation", "Dataset " + iDatasets + " " + dsd.getName() + " not used. Got " + dataset.numAttributes() + " attributes. (Interested in Range ["+MIN_ATTRIBUTES+","+MAX_ATTRIBUTES+"])" );
					continue;
				}
				
				BigInteger maxDifferentOptions = new BigInteger("1");
				for( int iAttributes = 0; iAttributes < dataset.numAttributes(); ++iAttributes ) {
					if( dataset.attribute( iAttributes ).isNominal() == false ) {
						maxDifferentOptions = maxDifferentOptions.multiply( new BigInteger("3") );
					} else {
						maxDifferentOptions = maxDifferentOptions.multiply( new BigInteger( dataset.attribute( iAttributes ).numValues() + "" ) );
					}
				}
	
				
				int numInstancesBNG = maxDifferentOptions.min( new BigInteger( "" + TARGET_NUM_INSTANCES ) ).intValue();
				int numInstancesOrig = dataset.numInstances();
				
				if( numInstancesOrig < TARGET_NUM_INSTANCES / 10 && numInstancesOrig > MIN_NUM_ORIG_INSTANCES ) {
					for( int driftFrequency : driftFrequencies ) {
						for( int driftImpact : driftImpacts ) {
							long startTime = System.currentTimeMillis();
							// only create when num instances is low. Otherwise use real dataset
							File generatedDataset = generateDataset( dsd, numInstancesBNG, driftFrequency, driftImpact );
							long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
							Conversion.log("INFO", "Generating Dataset", "Generated dataset on " + dsd.getName() + " in " + elapsedTime + " seconds ");
							
							//String summary = summarize( dataset, new Instances( new FileReader( generatedDataset ) ) );
		
							eligableDatasets++;
							if( SEND_RESULT ) {
								Conversion.log( "Generate Dataset", "OK", "Start upploading..." );
								uploadDataset( dsd, generatedDataset, driftFrequency, driftImpact, "" );
								Conversion.log( "Generate Dataset", "OK", "Done! " );
							}
						}
					}
				}
			} catch( Exception e ) {
				System.out.println( "Error at did " + iDatasets + ": " + e.getMessage() );
				errors.add(iDatasets);
			}
		}
		Conversion.log( "OK", "Done", "Generated " + eligableDatasets + " datasets; the following ids had errors: " + errors );
	}
	
	private File generateDataset( DataSetDescription dsd, long numInstances, int driftFrequency, int driftImpact ) throws Exception {
		String datasetname = "BNG(" + dsd.getName() + ","+driftFrequency+","+driftImpact+")";
		String outputFilename = outputDirectory.getAbsolutePath() + "/" + datasetname.replace(",","_").replace("(","_").replace(")","") + ".arff";
		String numericString = "-n";
		
		String[] taskArgs = new String[7];
		taskArgs[0] = "WriteStreamToARFFFile";
		taskArgs[1] = "-f";
		taskArgs[2] = outputFilename;
		taskArgs[3] = "-m";
		taskArgs[4] = "" + numInstances;
		taskArgs[5] = "-s";
		taskArgs[6] = "(generators.BayesianNetworkGenerator -f (" + dsd.getDataset( apiconnector.getApiKey() ).getAbsolutePath() + ") -a (" + searchAlgorithm + ") -p (" + outputFilename + ".xml) " + numericString + " -c " + driftFrequency + " -I " + driftImpact + ")";
		
		Conversion.log("INFO", "Build dataset", "CMD: " + StringUtils.join( taskArgs, ' ' ) );
		DoTask.main( taskArgs );
		Conversion.log("INFO", "Build dataset", "DONE" );
		
		return new File( outputFilename );
	}
	
	private void uploadDataset( DataSetDescription dsd, File generatedDataset, int driftFrequency, int driftImpact, String summary ) throws Exception {
		String datasetname = "BNG("+dsd.getName()+","+driftFrequency+","+driftImpact+")";
		String description = "The Bayesian Network Generator, with the " + datasetname + 
				" dataset ("+dsd.getUrl()+") as input. A Bayesian Network is created using Weka's BayesNet Package (" + 
				searchAlgorithm + "), which is then used to generate pseudo random instances. \n\n" + summary;
		String[] creators = {"Geoffrey Holmes", "Bernhard Pfahringer","Jan van Rijn", "Joaquin Vanschoren"};
		String[] tag = {"BNG"};
		
		DataSetDescription outputDatasetDescription = new DataSetDescription( 
				datasetname,
				null, // version
				description,
				creators,
				ArrayUtils.addAll(dsd.getCreator(), dsd.getContributor()),
				Constants.DATASET_FORMAT, 
				DateParser.defaultOrder.format( new Date() ),
				dsd.getLanguage(),
				"public domain",
				dsd.getRow_id_attribute(), // row id attribute
				dsd.getDefault_target_attribute(), 
				dsd.getIgnore_attribute(),
				tag,
				null // md5 hash
		);
		String outputDatasetString = xstream.toXML( outputDatasetDescription );
		System.out.println( outputDatasetString );
		
		File outputDataset = Conversion.stringToTempFile( outputDatasetString, datasetname, Constants.DATASET_FORMAT );
		UploadDataSet ud = apiconnector.dataUpload( outputDataset, generatedDataset );
		
		System.out.println( xstream.toXML( apiconnector.dataGet( ud.getId() ) ) );
	}
}
