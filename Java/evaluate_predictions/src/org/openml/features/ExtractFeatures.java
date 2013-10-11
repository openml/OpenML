package org.openml.features;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.openml.io.Input;
import org.openml.io.Output;

import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Instances;

public class ExtractFeatures {

	private final ArrayList<String> results;
	private final Instances dataset;
	
	public ExtractFeatures( String url ) throws IOException {
		results = new ArrayList<String>();
		dataset = new Instances( Input.getURL( url ) );
		
		outputFeatures( );
	}
	
	private void outputFeatures() {
		for( int i = 0; i < dataset.numAttributes(); i++ ) {
			Map<String, String> key_values = new HashMap<String, String>();
			Attribute att = dataset.attribute( i );
			AttributeStats as = dataset.attributeStats( i );
			
			key_values.put( "index", ""+att.index() );
			key_values.put( "name", att.name() );
			key_values.put( "NumberOfDistinctValues", ""+as.distinctCount );
			key_values.put( "NumberOfUniqueValues", ""+as.uniqueCount );
			key_values.put( "NumberOfMissingValues", ""+as.missingCount );
			key_values.put( "NumberOfIntegerValues", ""+as.intCount );
			key_values.put( "NumberOfRealValues", ""+as.realCount );
			
			if( att.isNominal() ) 
				key_values.put( "NumberOfNominalValues", ""+as.nominalCounts.length ); 
			key_values.put( "NumberOfValues", ""+as.totalCount );
			
			if( att.isNumeric() ) {
				key_values.put( "MaximumValue", ""+as.numericStats.max );
				key_values.put( "MinimumValue", ""+as.numericStats.min );
				key_values.put( "MeanValue", ""+as.numericStats.mean );
				key_values.put( "StandardDeviation", ""+as.numericStats.stdDev );
			}
			
			if( att.type() == 0 )
				key_values.put( "data_type", "numeric" );
			else if( att.type() == 1 )
				key_values.put( "data_type", "nominal" );
			else if( att.type() == 2 )
				key_values.put( "data_type", "string" );
			else
				key_values.put( "data_type", "unknown" );
			
			results.add(Output.dataFeatureToJson(key_values));
		}
		output();
	}
	
	private void output() {
		StringBuilder sb = new StringBuilder();
		for( String res : results ) {
			sb.append(",\n\t" + res);
		}
		
		System.out.println("{\n\"data_features\":[" + sb.toString().substring(1) + "\n]}");
	}
}