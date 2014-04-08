package org.openml.tools.dataset;

import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Date;

import moa.DoTask;
import moa.streams.generators.BayesianNetworkGenerator;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.DateParser;
import org.openml.apiconnector.algorithms.MathHelper;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.io.ApiSessionHash;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.settings.Constants;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.UploadDataSet;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.unsupervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

import com.thoughtworks.xstream.XStream;

public class GenerateBayesnetDatasets {
	
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	private static final ApiSessionHash ash = new ApiSessionHash();
	private static final int MIN_ATTRIBUTES = 10;
	private static final int MAX_ATTRIBUTES = 100;
	private static final int TARGET_NUM_INSTANCES = 1000000;
	private static final boolean SEND_RESULT = true;
	
	private static final File outputDirectory = new File( "/Users/jan/Desktop/BayesNetTest/" );
	
	private static final String searchAlgorithm = "weka.classifiers.bayes.net.search.local.K2 -P 15 -S BAYES";
	
	public static void main( String[] args ) throws Exception {
		new GenerateBayesnetDatasets();
	}
	
	public GenerateBayesnetDatasets() throws Exception {
		Config c = new Config();
		ash.set(c.getUsername(), c.getPassword());
		
		for( int iDatasets = 16; iDatasets <= 62; ++iDatasets ) {
			Conversion.log("INFO", "Download Dataset", "Downloading dataset " + iDatasets);
			DataSetDescription dsd = ApiConnector.openmlDataDescription( iDatasets );
			
			Instances dataset = new Instances( new FileReader( dsd.getDataset() ) );
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
			
			long startTime = System.currentTimeMillis();
			File generatedDataset = generateDataset( dsd, maxDifferentOptions.min( new BigInteger( "" + TARGET_NUM_INSTANCES ) ).intValue() );
			long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
			Conversion.log("INFO", "Generating Dataset", "Generated dataset on " + dsd.getName() + " in " + elapsedTime + " seconds ");
			
			String summary = summarize( dataset, new Instances( new FileReader( generatedDataset ) ) );
			
			if( SEND_RESULT ) {
				uploadDataset( dsd, generatedDataset, summary );
			}
		}
	}
	
	private String summarize( Instances sourceData, Instances resultData ) throws Exception {
		// apply same filters on input data
		StringBuilder sb = new StringBuilder();
		sb.append( "Attribute Information:\n" );
		String discretizeOptions = BayesianNetworkGenerator.getDiscretizationOptions( sourceData );
		if( discretizeOptions != null ) { sourceData = BayesianNetworkGenerator.applyFilter( sourceData, new Discretize(), discretizeOptions ); }
		sourceData = BayesianNetworkGenerator.applyFilter(sourceData, new ReplaceMissingValues(), "" );
		
		//double sizeFactor = ( (double) sourceData.numInstances() ) / ( (double) resultData.numInstances() ); 
		for( int iAttribute = 0; iAttribute < sourceData.numAttributes(); ++iAttribute ) {
			Attribute currentAttribute = sourceData.attribute( iAttribute );
			
			AttributeStats sourceStats = sourceData.attributeStats( iAttribute );
			AttributeStats resultStats = resultData.attributeStats( iAttribute );
			
			double[] sourceDistribution = new double[sourceStats.nominalCounts.length];
			double[] resultDistribution = new double[resultStats.nominalCounts.length];
			
			
			double change = 0.0;
			String allValues = "";
			for( int iValue = 0; iValue < currentAttribute.numValues(); ++ iValue ) {
				sourceDistribution[iValue] = sourceStats.nominalCounts[iValue];
				resultDistribution[iValue] = resultStats.nominalCounts[iValue];
				allValues += ", " + currentAttribute.value( iValue );
			}
			
			Utils.normalize( sourceDistribution );
			Utils.normalize( resultDistribution );
			
			System.err.println( "[CHANGE] " + currentAttribute.name() + " - " + change  );
			sb.append( iAttribute + ". " + currentAttribute.name() + ": " + allValues.substring( 1 ) + 
					"; Distrubution: [" + doubleArrayToString( resultDistribution, ';', MathHelper.visualDecimalFormat ) + 
					"]; Distribution (Original dataset): [" + doubleArrayToString( sourceDistribution, ';', MathHelper.visualDecimalFormat ) + "]\n"  );
		}
		
		return sb.toString();
	}
	
	private File generateDataset( DataSetDescription dsd, long numInstances ) throws Exception {
		String datasetname = "BayesianNetworkGenerator_" + dsd.getName();
		String outputFilename = outputDirectory.getAbsolutePath() + "/" + datasetname + ".arff";
		
		String[] taskArgs = new String[7];
		taskArgs[0] = "WriteStreamToARFFFile";
		taskArgs[1] = "-f";
		taskArgs[2] = outputFilename;
		taskArgs[3] = "-m";
		taskArgs[4] = "" + numInstances;
		taskArgs[5] = "-s";
		taskArgs[6] = "(generators.BayesianNetworkGenerator -f (" + dsd.getDataset().getAbsolutePath() + ") -a (" + searchAlgorithm + ") -p (" + outputFilename + ".xml))";
		
		Conversion.log("INFO", "Build dataset", "CMD: " + StringUtils.join( taskArgs, ' ' ) );
		DoTask.main( taskArgs );
		Conversion.log("INFO", "Build dataset", "DONE" );
		
		return new File( outputFilename );
	}
	
	private void uploadDataset( DataSetDescription dsd, File generatedDataset, String summary ) throws Exception {
		String datasetname = generatedDataset.getName();
		String description = "The Bayesian Network Generator, with the " + datasetname + 
				" dataset ("+dsd.getUrl()+") as input. A Bayesian Network is created using Weka's BayesNet Package (" + 
				searchAlgorithm + "), which is then used to generate pseudo random instances. \n\n" + summary;
		String[] creators = {"Geoffrey Holmes", "Bernhard Pfahringer","Jan van Rijn", "Joaquin Vanschoren"};
		
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
				null, // row id attribute
				dsd.getDefault_target_attribute(), 
				null // md5 hash
		);
		String outputDatasetString = xstream.toXML( outputDatasetDescription );
		
		File outputDataset = Conversion.stringToTempFile( outputDatasetString, datasetname, Constants.DATASET_FORMAT );
		UploadDataSet ud = ApiConnector.openmlDataUpload( outputDataset, generatedDataset, ash.getSessionHash() );
		
		System.out.println( xstream.toXML( ApiConnector.openmlDataDescription( ud.getId() ) ) );
	}
	
	private static String doubleArrayToString( double[] array, char delim, DecimalFormat df ) {
		if( array.length == 0 ) return "";
		
		StringBuilder sb = new StringBuilder();
		for( double d : array ) {
			sb.append( delim + df.format( d ) );
		}
		return sb.toString().substring( 1 );
	}
}
