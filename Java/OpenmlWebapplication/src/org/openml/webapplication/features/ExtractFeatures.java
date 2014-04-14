/*
 *  Webapplication - Java library that runs on OpenML servers
 *  Copyright (C) 2014 
 *  @author Jan N. van Rijn (j.n.van.rijn@liacs.leidenuniv.nl)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
package org.openml.webapplication.features;

import java.io.IOException;
import java.util.ArrayList;

import org.openml.webapplication.models.AttributeStatistics;
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
	
	private static final int RAM_LIMIT = 10000;
	private boolean allInstancesInRAM = true;
	
	// We create out own attribute stats class, so that we do not need to rely on Weka's class.
	// This class has a much lower footprint, as it does not need to store the various values
	// itself, but rather maintains some aggregated variables. In addition, we (currently) also
	// use Weka's AttributeStats, but only uptil a certain number of instances. 
	private final AttributeStatistics[] attributeStats;
	
	public ExtractFeatures( String url, String default_class ) throws IOException {
		arffLoader = new ArffLoader();
		arffLoader.setURL(url);
		
		dataset = new Instances( arffLoader.getStructure() );
		
		if( default_class != null ) {
			classAttribute = dataset.attribute( default_class );
		} else {
			classAttribute = dataset.attribute( dataset.numAttributes() - 1 );
		}
		if( classAttribute == null ) throw new RuntimeException("Specified target class not found.");
		
		dataset.setClass( classAttribute );

		attributeStats = new AttributeStatistics[dataset.numAttributes()];
		for( int i = 0; i < dataset.numAttributes(); ++i ) {
			attributeStats[i] = new AttributeStatistics( );
		}
		
		ArrayList<DataQuality> qualities= getQualities();
		ArrayList<DataFeature> features = getFeatures();
		
		output( features, qualities );
	}
	
	// @pre: invoke getQualities, so that the attribute stats classes are ok
	private ArrayList<DataFeature> getFeatures() {
		final ArrayList<DataFeature> resultFeatures = new ArrayList<DataFeature>();
		for( int i = 0; i < dataset.numAttributes(); i++ ) {
			Attribute att = dataset.attribute( i );
			DataFeature key_values = new DataFeature(att.index(), att.name());
			
			if( allInstancesInRAM ) {
				AttributeStats as = dataset.attributeStats( i );
				key_values.put( "NumberOfDistinctValues", as.distinctCount );
				key_values.put( "NumberOfUniqueValues", as.uniqueCount );
				key_values.put( "NumberOfMissingValues", as.missingCount );
				key_values.put( "NumberOfIntegerValues", as.intCount );
				key_values.put( "NumberOfRealValues", as.realCount );
			}
			
			if( att.isNominal() ) {
				key_values.put( "NumberOfNominalValues", att.numValues() ); 
			}
			key_values.put( "NumberOfValues", attributeStats[i].getTotalObservations() );
			
			if( att.isNumeric() ) {
				key_values.put( "MaximumValue", attributeStats[i].getMaximum() );
				key_values.put( "MinimumValue", attributeStats[i].getMinimum() );
				key_values.put( "MeanValue", attributeStats[i].getMean() );
				key_values.put( "StandardDeviation", attributeStats[i].getStandardDeviation() );
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
		
		// update attribute based quality counters
		for( int i = 0; i < dataset.numAttributes(); ++i ) {
			Attribute att = dataset.attribute( i );
			if(att.isNumeric())	NumberOfNumericFeatures++;
		}
		
		// we go through all the instances in only one loop. 
		Instance currentInstance;
		while( ( currentInstance = arffLoader.getNextInstance( dataset ) ) != null ) {
			// increment instance counter
			NumberOfInstances ++;
			
			// increment total number of missing values counter
			for( int j = 0; j < dataset.numAttributes(); ++j ) {
				if( currentInstance.isMissing(j) ) ++NumberOfMissingValues;
				attributeStats[j].addValue( currentInstance.value( j ) );
			}
			
			// increment instances / missing value(s) counter
			if(currentInstance.hasMissingValue()) NumberOfInstancesWithMissingValues++;
			
			// increment class values counts
			if( currentInstance.classAttribute().isNominal() ) {
				classDistribution[(int) currentInstance.classValue()] ++;
			}
			
			// Important!! In order to make this program scalable, we only keep the first
			// RAM_LIMIT records in RAM. Datasets with more instances can of course be
			// evaluated, but we will no longer rely on weka's AttributeStats class. Instead
			// we'll use our own AttributeStatistics class, that does not require a lot of RAM.
			// At the costs of information like unique values and distinct values. 
			if( NumberOfInstances < RAM_LIMIT ) {
				dataset.add(currentInstance);
			} else {
				allInstancesInRAM = false;
			}
		}
		
		// update statistics on class attribute
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