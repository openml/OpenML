package org.openml.apiconnector.algorithms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.settings.Settings;
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
	
	public static boolean nominalColumnEqualsValue( Instances instances, int index, String columnName, String value ) {
		int columnIdx = instances.attribute(columnName).index();
		if( instances.instance(index).value( columnIdx ) == instances.attribute( columnIdx ).indexOfValue( value ) ) {
			return true;
		} else {
			return false;
		}
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
	
	public static double[] toProbDist( double[]  d ) {
		double total = 0;
		double[] result = new double[d.length];
		for( int i = 0; i < d.length; ++i ) { // scan for infinity
			if( Double.isInfinite( d[i] ) ) {
				result[i] = 1.0;
				return result;
			}
		}
		
		for( int i = 0; i < d.length; ++i ) { 
			if( Double.isNaN( d[i] ) == false ) // only if it is a legal nr. 
				total += d[i];
		}
		
		for( int i = 0; i < d.length; ++i ) {
			if( Double.isNaN( d[i] ) )
				result[i] = 0.0D;
			else if( total > 0.0 )
				result[i] = d[i] / total;
			else 
				result[i] = d[i];
		}
		return result;
	}
	
	public static void instanes2writer( Instances instances, Writer out ) throws IOException {
		BufferedWriter bw = new BufferedWriter( out );
		// Important: We can not use a std Instances.toString() approach, as instance files can grow
		bw.write("@relation " + instances.relationName() + "\n\n");
		for( int i = 0; i < instances.numAttributes(); ++i ) {
			bw.write( instances.attribute(i) + "\n" );
		}
		bw.write("\n@data\n");
		for( int i = 0; i < instances.numInstances(); ++i ) {
			if( i + 1 == instances.numInstances() ) {
				bw.write( instances.instance(i) + "" ); // fix for last instance
			} else {
				bw.write( instances.instance(i) + "\n" );
			}
		}
		bw.close();
	}
	
	public static File downloadAndCache( String type, String identifier, String url, String serverMd5 ) throws IOException {
		File directory = new File( Settings.CACHE_DIRECTORY + type + "/" );
		File file = new File( directory.getAbsolutePath() + "/" + identifier );
		File dataset;
		
		if( file.exists() ) {
			String clientMd5 = Hashing.md5(file);
			if( clientMd5.equals( serverMd5.trim() ) ) {
				System.out.println("[Cache] Loaded " + type + " " + identifier + " from cache. " );
				return file;
			} else {
				System.out.println("[Cache ERROR] " + type + " " + identifier + " hash and cache not identical: \n- Client: " + clientMd5 + "\n- Server: " + serverMd5 );
			}
		}
		
		if( Settings.CACHE_ALLOWED ) {
			directory.mkdirs();
			dataset = ApiConnector.getFileFromUrl( url, file.getAbsolutePath() );
		} else {
			dataset = Conversion.stringToTempFile( ApiConnector.getStringFromUrl( url ), identifier, "arff" );
		}
		return dataset;
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