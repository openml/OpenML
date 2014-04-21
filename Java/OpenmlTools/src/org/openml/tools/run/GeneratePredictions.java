package org.openml.tools.run;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.Task;

import weka.classifiers.Classifier;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class GeneratePredictions {

	private final Instances dataset;
	private final Instances splits;
	private final Instances empty;
	private final Instances predictions;
	
	private final BufferedWriter writer;
	
	private final int ATT_SPLITS_TYPE;
	private final int ATT_SPLITS_REPEAT;
	private final int ATT_SPLITS_FOLD;
	private final int ATT_SPLITS_ROWID;
	
	private Classifier classifier;
	
	private String[] classes;
	
	private final ArrayList<Integer>[][][] sets;
	
	private final ApiConnector apiconnector;
	
	public static void main( String[] args ) throws Exception {
		new GeneratePredictions( 1 );
	}
	
	@SuppressWarnings("unchecked")
	public GeneratePredictions( Integer task_id ) throws Exception {
		Config config = new Config();
		if( config.getServer() != null ) {
			apiconnector = new ApiConnector( config.getServer() );
		} else { 
			apiconnector = new ApiConnector();
		} 
		
		Task t = apiconnector.openmlTaskSearch( task_id );
		
		String datasetPath = TaskInformation.getSourceData( t ).getDataSetDescription( apiconnector ).getUrl();
		String splitsPath = TaskInformation.getEstimationProcedure( t ).getData_splits_url();
		String classAttribute = TaskInformation.getSourceData( t ).getTarget_feature();
		Integer repeats = TaskInformation.getNumberOfRepeats( t );
		Integer folds = TaskInformation.getNumberOfFolds( t );
		
		int nrOfPredictions = 0;
		boolean regression;
		
		dataset = new Instances( new BufferedReader( getURL( datasetPath ) ) );
		splits = new Instances( new BufferedReader( getURL( splitsPath ) ) );
		empty = new Instances( dataset );
		empty.delete();
		
		writer = new BufferedWriter( new FileWriter( new File( "data/output/predictions_" + datasetPath.substring( datasetPath.lastIndexOf('/') + 1 ) ) ) );
		sets = new ArrayList[2][repeats][folds];
		
		for( int i = 0; i < 2; i++ )
			for( int j = 0; j < repeats; j++ )
				for( int k = 0; k < folds; k++ )
					sets[i][j][k] = new ArrayList<Integer>();
		
		ATT_SPLITS_REPEAT = splits.attribute( "repeat" ).index();
		ATT_SPLITS_FOLD = splits.attribute( "fold" ).index();
		ATT_SPLITS_TYPE = splits.attribute( "type" ).index();
		ATT_SPLITS_ROWID = splits.attribute( "rowid" ).index();
		
		for( int i = 0; i < dataset.numAttributes(); i++ ) {
			if( dataset.attribute( i ).name().equals( classAttribute ) ) {
				dataset.setClass( dataset.attribute( i ) );
				empty.setClass( dataset.attribute( i ) );
			}
		}
		if( dataset.classIndex() < 0 ) { 
			throw new RuntimeException("class not found.");
		}
		regression = dataset.classAttribute().isNumeric();
		System.out.println( regression ? "Regression" : "classification" );
		classes = new String[dataset.classAttribute().numValues()];
		for( int i = 0; i < classes.length; i++ ) {
			classes[i] = dataset.classAttribute().value( i );
		}
		System.out.println( Arrays.toString( classes ) );
		
		for( int i = 0; i < splits.numInstances(); i++ ) {
			int repeat = (int) splits.instance( i ).value( ATT_SPLITS_REPEAT );
			int fold = (int) splits.instance( i ).value( ATT_SPLITS_FOLD );
			int type = (int) splits.instance( i ).value( ATT_SPLITS_TYPE );
			int rowid = (int) splits.instance( i ).value( ATT_SPLITS_ROWID );
			if( type == 1 ) {
				nrOfPredictions ++;
			}
			sets[type][repeat][fold].add( rowid );
		}
		predictions = new Instances( dataset.relationName() + "-predictions", generateAttributes( classes, regression ), nrOfPredictions );
		
		for( int j = 0; j < repeats; j++ ) {
			for( int k = 0; k < folds; k++ ) {
				if( regression ) {
					classifier = new Logistic();
				} else {
					classifier = new J48();
				}
				classifier.buildClassifier( integerToInstances( sets[0][j][k] ) );
				
				for( int i = 0; i < sets[1][j][k].size(); i++ ) {
					final int rowid = sets[1][j][k].get( i );
					final double prediction = classifier.classifyInstance( dataset.instance( rowid ) );
					final double[] values = { j, k, rowid, prediction };
					final double[] confidences = classifier.distributionForInstance( dataset.instance( rowid ) );
					final double[] total = regression ? values : ArrayUtils.addAll( values, confidences );
					predictions.add( new DenseInstance( 1.0, total ) );
				}
			}
		}
		writer.append( predictions.toString() );
		writer.close();
	}
	
	private Instances integerToInstances( ArrayList<Integer> integers ) {
		Instances instances = new Instances( empty );
		for( int i = 0; i < integers.size(); i++ ) {
			instances.add( dataset.instance( integers.get( i ) ) );
		}
		return instances;
	}
	
	private static InputStreamReader getURL( String sUrl ) throws IOException {
		URL url = new URL( sUrl );
		URLConnection urlConnection = url.openConnection();
		urlConnection.setConnectTimeout(1000);
		urlConnection.setReadTimeout(30000);
		return new InputStreamReader( urlConnection.getInputStream() );
	}
	
	private static ArrayList<Attribute> generateAttributes( String[] classes, boolean regression ) {
		ArrayList<String> targetClasses = new ArrayList<String>();
		for( int i = 0; i < classes.length; i++ )
			targetClasses.add( classes[i] ); 
		
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(new Attribute("repeat"));
		attributes.add(new Attribute("fold"));
		attributes.add(new Attribute("rowid"));
		if( regression ) {
			attributes.add(new Attribute("prediction"));
		} else {
			attributes.add(new Attribute("prediction",targetClasses));
			for( String c : classes ) {
				attributes.add(new Attribute("confidence."+c));
			}
		}
		return attributes;
	}
}
