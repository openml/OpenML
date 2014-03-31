package org.openml.webapplication.features;

import java.io.IOException;
import java.util.ArrayList;

import org.openml.webapplication.models.DataFeature;
import org.openml.webapplication.models.DataQuality;

import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class ExtractFeatures {

	private final Instances dataset;
	private final ArffLoader arffLoader;
	private final Attribute classAttribute;
	
	public ExtractFeatures( String url, String default_class ) throws IOException {
		arffLoader = new ArffLoader();
		arffLoader.setURL(url);
		
		dataset = new Instances( arffLoader.getStructure() );
		
		classAttribute = dataset.attribute( default_class );
		if( classAttribute == null ) throw new RuntimeException("Specified target class not found.");
		
		dataset.setClass( classAttribute );

		ArrayList<DataQuality> qualities= getQualities();
		ArrayList<DataFeature> features = getFeatures();
		
		output( features, qualities );
	}
	
	// @pre: invoke getQualities, so that the attribute stats classes are ok
	private ArrayList<DataFeature> getFeatures() {
		final ArrayList<DataFeature> resultFeatures = new ArrayList<DataFeature>();
		for( int i = 0; i < dataset.numAttributes(); i++ ) {
			Attribute att = dataset.attribute( i );
			AttributeStats as = dataset.attributeStats( i );
			DataFeature key_values = new DataFeature(att.index(), att.name());
			
			key_values.put( "NumberOfDistinctValues", as.distinctCount );
			key_values.put( "NumberOfUniqueValues", as.uniqueCount );
			key_values.put( "NumberOfMissingValues", as.missingCount );
			key_values.put( "NumberOfIntegerValues", as.intCount );
			key_values.put( "NumberOfRealValues", as.realCount );
			
			if( att.isNominal() ) {
				key_values.put( "NumberOfNominalValues", as.nominalCounts.length ); 
			}
			key_values.put( "NumberOfValues", as.totalCount );
			
			if( att.isNumeric() ) {
				key_values.put( "MaximumValue", as.numericStats.max );
				key_values.put( "MinimumValue", as.numericStats.min );
				key_values.put( "MeanValue", as.numericStats.mean );
				key_values.put( "StandardDeviation", as.numericStats.stdDev );
			}
			
			if( att.type() == 0 )
				key_values.put( "data_type", "numeric" );
			else if( att.type() == 1 )
				key_values.put( "data_type", "nominal" );
			else if( att.type() == 2 )
				key_values.put( "data_type", "string" );
			else
				key_values.put( "data_type", "unknown" );
			
			resultFeatures.add(key_values);
		}
		return resultFeatures;
	}
	
	private ArrayList<DataQuality> getQualities( ) throws IOException {
		final ArrayList<DataQuality> resultQualities = new ArrayList<DataQuality>();
		boolean nominalTarget = dataset.classAttribute().isNominal();
		int[] classDistribution = new int[dataset.classAttribute().numValues()];
		
		int NumberOfInstances = 0;
		int NumberOfMissingValues = 0;
		int NumberOfInstancesWithMissingValues = 0;
		int NumberOfNumericFeatures = 0;
		int NumberOfClasses = dataset.classAttribute().numValues();
		int MajorityClassSize = -1;
		int MinorytyClassSize = Integer.MAX_VALUE;
		
		Instance currentInstance;
		while( ( currentInstance = arffLoader.getNextInstance( dataset ) ) != null ) {
			// increment instance counter
			NumberOfInstances ++;
			
			// increment total number of missing values counter
			for( int j = 0; j < dataset.numAttributes(); ++j ) {
				if( currentInstance.isMissing(j) ) ++NumberOfMissingValues;
			}
			
			// increment instances / missing value(s) counter
			if(currentInstance.hasMissingValue()) NumberOfInstancesWithMissingValues++;
			
			// increment class values counts
			if( currentInstance.classAttribute().isNominal() ) {
				classDistribution[(int) currentInstance.classValue()] ++;
			}
			
			// TODO! In order to make this class scalable, 
			// we need to remove the following line of code and
			// think of something that replaces the AttributeStats counters. 
			dataset.add(currentInstance);
		}
		
		for( int i = 0; i < dataset.numAttributes(); ++i ) {
			Attribute att = dataset.attribute( i );
			
			if(att.isNumeric()) {
				NumberOfNumericFeatures++;
			}
		}
		
		for( int nominalSize : classDistribution ) { // check will only be performed with nominal target
			if(nominalSize > MajorityClassSize) MajorityClassSize = nominalSize;
			if(nominalSize < MinorytyClassSize) MinorytyClassSize = nominalSize;
		}
		
		
		resultQualities.add( new DataQuality("DefaultTargetNominal", nominalTarget ? "true" : "false" ) );
		resultQualities.add( new DataQuality("DefaultTargetNumerical", nominalTarget ? "false" : "true" ) );
		resultQualities.add( new DataQuality("NumberOfInstances", ""+NumberOfInstances ) );
		resultQualities.add( new DataQuality("NumberOfFeatures", ""+dataset.numAttributes() ) );
		resultQualities.add( new DataQuality("NumberOfInstancesWithMissingValues", ""+NumberOfInstancesWithMissingValues ) );
		resultQualities.add( new DataQuality("NumberOfMissingValues", ""+NumberOfMissingValues ) );
		resultQualities.add( new DataQuality("NumberOfNumericFeatures", ""+NumberOfNumericFeatures ) );
		if( nominalTarget ) {
			resultQualities.add( new DataQuality("NumberOfClasses", ""+NumberOfClasses) );
			resultQualities.add( new DataQuality("DefaultAccuracy", ""+((MajorityClassSize*1.0) / (NumberOfInstances*1.0))) );
			resultQualities.add( new DataQuality("MajorityClassSize", ""+MajorityClassSize ) );
			resultQualities.add( new DataQuality("MinorityClassSize", ""+MinorytyClassSize ) );
		}
		
		return resultQualities;
	}
	
	private void output( ArrayList<DataFeature> featuresArray, ArrayList<DataQuality> qualitiesArray ) {
		StringBuilder features = new StringBuilder();
		StringBuilder qualities = new StringBuilder();
		for( DataFeature res : featuresArray ) features.append(",\n\t" + res);
		for( DataQuality res : qualitiesArray ) qualities.append(",\n\t" + res);
		
		
		System.out.println("{\n\"data_features\":[" + 
				(features.length() > 0 ? features.toString().substring(1) : "") + "\n],\n\"data_qualities\":[" + 
				(qualities.length() > 0 ? qualities.toString().substring(1) : "") + "\n]}");
	}
}