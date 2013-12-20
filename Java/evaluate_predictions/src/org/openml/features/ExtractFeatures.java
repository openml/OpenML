package org.openml.features;

import java.io.IOException;
import java.util.ArrayList;

import org.openml.io.Input;
import org.openml.models.DataFeature;
import org.openml.models.DataQuality;

import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Instances;

public class ExtractFeatures {

	private final Instances dataset;
	
	public ExtractFeatures( String url, String default_class ) throws IOException {
		dataset = new Instances( Input.getURL( url ) );
		
		ArrayList<DataFeature> features = getFeatures();
		ArrayList<DataQuality> qualities= getQualities( default_class );
		
		output( features, qualities );
	}
	
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
	
	private ArrayList<DataQuality> getQualities( String default_class ) {
		final ArrayList<DataQuality> resultQualities = new ArrayList<DataQuality>();
		boolean nominalTarget = false;
		boolean classFound = false;
		
		int NumberOfInstances = dataset.numInstances();
		int NumberOfMissingValues = 0;
		int NumberOfInstancesWithMissingValues = 0;
		int NumberOfNumericFeatures = 0;
		int NumberOfClasses = -1;
		int MajorityClassSize = -1;
		int MinorytyClassSize = Integer.MAX_VALUE;
		
		
		for( int i = 0; i < dataset.numAttributes(); ++i ) {
			Attribute att = dataset.attribute( i );
			AttributeStats as = dataset.attributeStats( i );
			
			if(att.isNumeric()) {
				NumberOfNumericFeatures++;
			}
			if(att.name().equals( default_class )) {
				classFound = true;
				if(att.isNominal()) {
					nominalTarget = true;
					NumberOfClasses = att.numValues();
					int[] classDistribution = as.nominalCounts;
					for( int nominalSize : classDistribution ) {
						if(nominalSize > MajorityClassSize) MajorityClassSize = nominalSize;
						if(nominalSize < MinorytyClassSize) MinorytyClassSize = nominalSize;
					}
				}
			}
		}
		
		if( classFound == false && default_class != null ) throw new RuntimeException("Specified target class not found.");
		
		for( int i = 0; i < dataset.numInstances(); ++i ) {
			for( int j = 0; j < dataset.numAttributes(); ++j ) {
				if( dataset.instance(i).isMissing(j) ) ++NumberOfMissingValues;
			}
			if(dataset.instance(i).hasMissingValue()) NumberOfInstancesWithMissingValues++;
		}
		
		
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
		
		
		System.out.println("{\n\"data_features\":[" + features.toString().substring(1) + "\n],\n\"data_qualities\":[" + qualities.toString().substring(1) + "\n]}");
	}
}