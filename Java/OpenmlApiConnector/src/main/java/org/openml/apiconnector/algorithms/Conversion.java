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
package org.openml.apiconnector.algorithms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

public class Conversion {

	/**
	 * Stores a string into a temporarily existing file. 
	 * 
	 * @param string - The string to store in the file. 
	 * @param filename - The name of the file.
	 * @param format - The extension of the file. 
	 * @return A pointer to the temp file that was created. 
	 * @throws IOException
	 */
	public static File stringToTempFile( String string, String filename, String format ) throws IOException {
		File file = File.createTempFile(filename, '.' + format );
		BufferedWriter br = new BufferedWriter(new FileWriter(file));
		br.write(string);
		br.close();
		file.deleteOnExit();
		return file;
	}
	
	/**
	 * Parses a comma separated string to a integer array
	 * 
	 * @param commaSeparated - the comma separated string to parse
	 * @return the resulting integer array
	 * @throws NumberFormatException
	 */
	public static int[] commaSeparatedStringToIntArray( String commaSeparated ) throws NumberFormatException {
		String[] splitted = commaSeparated.replaceAll("\\s","").split(","); // remove spaces, split on comma
		int[] result = new int[splitted.length];
		for(int i = 0; i < result.length; ++i) {
			result[i] = Integer.parseInt(splitted[i]);
		}
		return result;
	}
	
	/**
	 * Reads a file and stores the content in a string
	 * 
	 * @param f - File pointer to the file that needs to be read. Should be non-binary. 
	 * @return A string containing the content of the file. 
	 * @throws IOException
	 */
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
	
	public static void log( String status, String action, String message ) {
		log( status, action, message, System.err );
	}
	
	public static void log( String status, String action, String message, PrintStream writer ) {
		writer.println( "["+DateParser.humanReadable.format( System.currentTimeMillis() )+"] ["+status+"] ["+action+"] " + message );
	}
}
