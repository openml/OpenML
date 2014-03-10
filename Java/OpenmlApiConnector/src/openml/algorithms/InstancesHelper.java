package openml.algorithms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;

import openml.io.ApiConnector;
import openml.settings.Settings;
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
	
	public static double[] toProbDist( double[]  d ) {
		double total = 0;
		double[] result = new double[d.length];
		for( int i = 0; i < d.length; ++i ) {
			total += d[i];
		}
		
		for( int i = 0; i < d.length; ++i ) {
			if( total > 0.0 )
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
}
