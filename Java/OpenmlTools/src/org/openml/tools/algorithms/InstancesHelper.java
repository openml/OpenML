package org.openml.tools.algorithms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;

public class InstancesHelper {

	public static void toFile( Instances dataset, String filename ) throws IOException {
		BufferedWriter bw = new BufferedWriter( new FileWriter( new File( filename + ".arff" ) ) );
		
		bw.write( "@relation " + dataset.relationName() + "\n\n" );
		
		for( int i = 0; i < dataset.numAttributes(); ++i ) {
			bw.write( dataset.attribute(i) + "\n" );
		}
		
		bw.write("\n@data");
		for( int i = 0; i < dataset.numInstances(); ++i ) {
			bw.write( "\n" + dataset.instance(i) );
		}
		
		bw.close();
	}
	
	public static Instances applyFilter( Instances dataset, Filter filter, String options ) throws Exception {
		((OptionHandler) filter).setOptions( Utils.splitOptions( options ) );
		filter.setInputFormat(dataset);
		return Filter.useFilter(dataset, filter);
	}
	
	public static Attribute copyAttribute( Attribute attribute ) {
		if( attribute.isNumeric() ) {
			return new Attribute( attribute.name() );
		} else {
			List<String> values = new ArrayList<String>();
			for( int i = 0; i < attribute.numValues(); ++i ) {
				values.add( attribute.value( i ) );
			}
			return new Attribute( attribute.name(), values );
			
			
		}
	}
}
