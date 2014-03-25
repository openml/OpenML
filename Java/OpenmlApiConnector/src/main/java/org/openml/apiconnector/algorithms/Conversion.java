package org.openml.apiconnector.algorithms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import weka.core.Instances;

public class Conversion {

	public static File instancesToTempFile( Instances dataset, String filename, String format ) throws IOException {
		File file = File.createTempFile(filename, '.' + format );
		BufferedWriter br = new BufferedWriter(new FileWriter(file));
		br.write(dataset.toString());
		br.close();
		file.deleteOnExit();
		return file;
	}
	
	public static File stringToTempFile( String string, String filename, String format ) throws IOException {
		File file = File.createTempFile(filename, '.' + format );
		BufferedWriter br = new BufferedWriter(new FileWriter(file));
		br.write(string);
		br.close();
		file.deleteOnExit();
		return file;
	}
	
	/*public static String arrayToString( double[] array ) {
		StringBuilder sb = new StringBuilder();
		for( double d : array ) {
			sb.append( ", " + d );
		}
		return "[" + sb.toString().substring( 1 ) + "]";
	}*/
	
	public static int[] commaSeperatedStringToIntArray( String commaSeperated ) throws NumberFormatException {
		String[] splitted = commaSeperated.replaceAll("\\s","").split(","); // remove spaces, split on comma
		int[] result = new int[splitted.length];
		for(int i = 0; i < result.length; ++i) {
			result[i] = Integer.parseInt(splitted[i]);
		}
		return result;
	}
	
	public static String fileToString( File f ) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(f));
		StringBuilder sb = new StringBuilder();
		String line = br.readLine();
		while( line != null ) {
			sb.append( line + "\n" );
			line = br.readLine();
		}
		br.close();
		return sb.toString();
	}
}
