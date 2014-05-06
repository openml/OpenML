package org.openml.tools.dataset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import weka.core.Attribute;

public class OfflineFeatures {
	
	private static final String REALWORLD = "realworld";
	private static final String BAYESNETGENERATOR = "bayesnetgenerator";
	private static final String CONVENTIONAL = "conventionalgenerator";
	
	private static int[] real_world_data = { 149, 150, 151, 155, 273, 274 };
	private static int[] conventionalgenerator = { 152, 153, 154, 156, 157, 158, 159, 160, 161, 162 };
	private static int[] bayesnet_generator= { 70,71,75,77,116,117,119,122,121,124,126,128,129,133,136,138,140,142,144,146,72,73,74,76,78,115,118,120,123,125,127,130,131,132,134,135,137,139,141,143,145,147,148,244,245,248,251,253,255,257,256,258,260,262,263,265,266,267,269,246,247,249,250,252,254,259,261,264,268,270,271,272 };
	
	private Attribute datatype;
	
	public OfflineFeatures() {
		List<String> typeValues = new ArrayList<String>();
		typeValues.add( REALWORLD );
		typeValues.add( BAYESNETGENERATOR );
		typeValues.add( CONVENTIONAL );
		datatype = new Attribute( "datatype", typeValues );
	}
	
	public Attribute datasetType() {
		return datatype;
	}
	
	public double getDatasetType( int did ) {
		if( Arrays.asList( real_world_data ).contains( did ) ) {
			return datatype.indexOfValue( REALWORLD );
		} else if( Arrays.asList( conventionalgenerator ).contains( did ) ) {
			return datatype.indexOfValue( CONVENTIONAL );
		} else { // BNG
			return datatype.indexOfValue( BAYESNETGENERATOR );
		}
	}
}
