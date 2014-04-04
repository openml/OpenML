/*
 *  OpenmlApiConnector - Java integration of the OpenML Web API
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
package org.openml.webapplication.algorithm;

import java.util.LinkedList;
import java.util.List;

import weka.core.Instance;
import weka.core.Instances;

public class InstancesHelper {

	public static void setTargetAttribute( Instances instances, String classAttribute ) throws Exception {
		for(int i = 0; i < instances.numAttributes(); ++i ) {
			if(instances.attribute(i).name().equals(classAttribute)) {
				instances.setClassIndex(i);
				return;
			}
		}
		throw new Exception("classAttribute " + classAttribute + " non-existant on dataset. ");
	}
	
	public static int getRowIndex( String[] names, Instances instances ) {
		for( String name : names ) {
			int probe = getRowIndex(name, instances);
			if( probe >= 0 ) return probe;
		}
		return -1;
	}
	
	public static int getRowIndex( String name, Instances instances ) {
		return (instances.attribute( name ) != null) ? instances.attribute( name ).index() : -1;
	}
	
	public static int[] classCounts( Instances dataset ) {
		int[] count = new int[dataset.classAttribute().numValues()];
		for( int i = 0; i < dataset.numInstances(); ++i ) {
			count[(int)dataset.instance(i).classValue()]++;
		}
		return count;
	}
	
	public static double[] classRatios( Instances dataset ) {
		double[] result = new double[dataset.classAttribute().numValues()];
		int[] count = classCounts( dataset );
		
		for( int i = 0; i < result.length; ++i ) {
			result[i] = count[i] * 1.0 / dataset.numInstances();
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static void stratify( Instances dataset ) {
		int numClasses = dataset.classAttribute().numValues();
		int numInstances = dataset.numInstances();
		double[] classRatios = classRatios( dataset );
		double[] currentRatios = new double[numClasses];
		int[] currentCounts = new int[numClasses];
		List<Instance>[] instancesSorted = new LinkedList[numClasses];
		
		for( int i = 0; i < numClasses; ++i ) {
			instancesSorted[i] = new LinkedList<Instance>();
		}
		
		// first, sort all instances based on class in different lists
		for( int i = 0; i < numInstances; ++i ) {
			Instance current = dataset.instance(i);
			instancesSorted[(int) current.classValue()].add( current );
		}
		
		// now empty the original dataset, all instances are stored in the L.L.
		for( int i = 0; i < numInstances; i++ ) {
			dataset.delete( dataset.numInstances() - 1 );
		}
		
		for( int i = 0; i < numInstances; ++i ) {
			int idx = biggestDifference( classRatios, currentRatios );
			dataset.add( instancesSorted[idx].remove( 0 ) );
			currentCounts[idx]++;
			
			for( int j = 0; j < currentRatios.length; ++j ) {
				currentRatios[j] = (currentCounts[j] * 1.0) / (i+1);
			}
		}
	}
	
	private static int biggestDifference( double[] target, double[] current ) {
		int biggestIdx = -1;
		double biggestValue = Integer.MIN_VALUE;
		for( int i = 0; i < target.length; ++i ) {
			double currentValue = target[i] - current[i];
			if( currentValue > biggestValue ) {
				biggestIdx = i;
				biggestValue = currentValue;
			}
		}
		return biggestIdx;
	}
}