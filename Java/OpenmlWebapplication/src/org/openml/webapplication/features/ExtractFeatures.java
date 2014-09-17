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
import java.util.Enumeration;
import java.util.HashMap;

import org.openml.apiconnector.xml.DataFeature.Feature;
import org.openml.apiconnector.xml.DataQuality.Quality;
import org.openml.webapplication.models.AttributeStatistics;

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
	// use Weka's AttributeStats, but only up till a certain number of instances. 
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
	}
	
	// @pre: invoke getQualities, so that the attribute stats classes are ok
	public ArrayList<Feature> getFeatures() {
		final ArrayList<Feature> resultFeatures = new ArrayList<Feature>();
		ArrayList<String> fullClassDistribution = getClassDistribution();
		
		for( int i = 0; i < dataset.numAttributes(); i++ ) {
			Attribute att = dataset.attribute( i );
			
			String data_type = null;
			
			Integer numberOfDistinctValues = null;
			Integer numberOfUniqueValues = null;
			Integer numberOfMissingValues = null;
			Integer numberOfIntegerValues = null;
			Integer numberOfRealValues = null;
			Integer numberOfNominalValues = null;
			Integer numberOfValues = null;
			
			Double maximumValue = null;
			Double minimumValue = null;
			Double meanValue = null;
			Double standardDeviation = null;
			
			String classDistribution = null;
			if( classAttribute.isNominal())
				classDistribution = fullClassDistribution.get(i);
			
			if( allInstancesInRAM ) {
				AttributeStats as = dataset.attributeStats( i );
				
				numberOfDistinctValues = as.distinctCount;
				numberOfUniqueValues = as.uniqueCount;
				numberOfMissingValues =  as.missingCount;
				numberOfIntegerValues = as.intCount;
				numberOfRealValues = as.realCount;
			}
			
			if( att.isNominal() ) {
				numberOfNominalValues = att.numValues(); 
			}
			numberOfValues = attributeStats[i].getTotalObservations();
			
			if( att.isNumeric() ) {
				maximumValue = attributeStats[i].getMaximum();
				minimumValue = attributeStats[i].getMinimum();
				meanValue = attributeStats[i].getMean();
				standardDeviation = attributeStats[i].getStandardDeviation();
			}
			
			if( att.type() == 0 )
				data_type = "numeric";
			else if( att.type() == 1 )
				data_type = "nominal";
			else if( att.type() == 2 )
				data_type = "string";
			else
				data_type = "unknown";
			
			resultFeatures.add( new Feature( att.index(), att.name(), data_type,
					att.name().equals( classAttribute.name() ), numberOfDistinctValues,
					numberOfUniqueValues, numberOfMissingValues,
					numberOfIntegerValues, numberOfRealValues,
					numberOfNominalValues, numberOfValues,
					maximumValue, minimumValue, meanValue,
					standardDeviation, classDistribution) );
		}
		return resultFeatures;
	}
	
	public ArrayList<String> getClassDistribution() {
        ArrayList<String> result = new ArrayList<String>();
		if (dataset.classAttribute().isNominal()){
			// feature : feature value : class : count
			HashMap<Integer,int[][]> features = new HashMap<Integer,int[][]>();
			int class_index = dataset.classAttribute().index();

	        //init
			for (int l = 0; l < dataset.numAttributes(); l++){
				if(dataset.attribute(l).isNominal())
					features.put(l,new int[dataset.attribute(l).numValues()][dataset.classAttribute().numValues()]);
				result.add("[]");
			}

			//cycle
	        for (int k = 0; k < dataset.numInstances(); k++) {
	            if (!dataset.instance(k).isMissing(class_index)) {
	              int classValue = (int) dataset.instance(k).value(class_index);
	  	          for (int l = 0; l < dataset.numAttributes(); l++){
	  	        	if(dataset.attribute(l).isNominal() && !dataset.instance(k).isMissing(l))
	  	        		features.get(l)[(int) dataset.instance(k).value(l)][classValue]++;
	  	          }
	            }
	        }
	  	    
	        for (int l = 0; l < dataset.numAttributes(); l++){
		        //Stringyfy
				if(dataset.attribute(l).isNominal()){
					String s = "[[\"";
					boolean newName = true;
					Enumeration e = dataset.attribute(l).enumerateValues();
					while(e.hasMoreElements()){
						if(newName){
							s += e.nextElement();
							newName = false;
						}
						else
							s += "\",\""+e.nextElement();
					}
					s += "\"],[";
			        boolean newC = true;
			        boolean newF = true;
			        for(int[] c : features.get(l)){
				          if(newC){
				        	  s += "[";
				        	  newC = false;
				          } else
				        	  s += "],[";
			        	  newF = true;
			        	  for(int f : c){
			        		  if(newF){
			        			  s += f;
					        	  newF = false;
			        		  } else
			        			  s += ","+f;
			        	  }
			        }
			        s += "]]]";
			        result.set(l,s);
				}
		}
		}
	    return result;
	}
	
	public ArrayList<Quality> getQualities( ) throws IOException {
		final ArrayList<Quality> resultQualities = new ArrayList<Quality>();
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
		
//		resultQualities.add( new Quality("DefaultTargetNominal", nominalTarget ? "true" : "false" ) );
//		resultQualities.add( new Quality("DefaultTargetNumerical", nominalTarget ? "false" : "true" ) );
		resultQualities.add( new Quality("NumberOfInstances", ""+NumberOfInstances ) );
		resultQualities.add( new Quality("NumberOfFeatures", ""+dataset.numAttributes() ) );
		resultQualities.add( new Quality("NumberOfInstancesWithMissingValues", ""+NumberOfInstancesWithMissingValues ) );
		resultQualities.add( new Quality("NumberOfMissingValues", ""+NumberOfMissingValues ) );
		resultQualities.add( new Quality("NumberOfNumericFeatures", ""+NumberOfNumericFeatures ) );
		if( nominalTarget ) {
			resultQualities.add( new Quality("NumberOfClasses", ""+NumberOfClasses) );
			resultQualities.add( new Quality("DefaultAccuracy", ""+((MajorityClassSize*1.0) / (NumberOfInstances*1.0))) );
			resultQualities.add( new Quality("MajorityClassSize", ""+MajorityClassSize ) );
			resultQualities.add( new Quality("MinorityClassSize", ""+MinorytyClassSize ) );
		}
		
		return resultQualities;
	}
}