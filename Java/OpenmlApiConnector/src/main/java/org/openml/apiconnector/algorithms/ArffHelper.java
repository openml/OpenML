package org.openml.apiconnector.algorithms;

import java.io.File;
import java.io.IOException;

import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.settings.Settings;

public class ArffHelper {
	
	public static File downloadAndCache( String type, String identifier, String url, String serverMd5 ) throws IOException {
		File directory = new File( Settings.CACHE_DIRECTORY + type + "/" );
		File file = new File( directory.getAbsolutePath() + "/" + identifier );
		File dataset;
		
		if( file.exists() ) {
			String clientMd5 = Hashing.md5(file);
			if( clientMd5.equals( serverMd5.trim() ) ) {
				System.err.println("[Cache] Loaded " + type + " " + identifier + " from cache. " );
				return file;
			} else {
				System.err.println("[Cache ERROR] " + type + " " + identifier + " hash and cache not identical: \n- Client: " + clientMd5 + "\n- Server: " + serverMd5 );
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
	
	public static boolean isDataDeclaration( String line ) {
		if( line.length() == 0 ) return false;
		if( line.charAt( 0 ) == '%' ) return false; // comment; 
		if( line.toUpperCase().contains( "@DATA" ) )
			return true;
		return false;
	}
	
	public static boolean isAttributeDeclaration( String line ) {
		if( line.length() == 0 ) return false;
		if( line.charAt( 0 ) == '%' ) return false; // comment; 
		if( line.toUpperCase().contains( "@ATTRIBUTE" ) )
			return true;
		return false;
	}
	
	public static String getAttributeName( String attributeLine ) throws Exception {
		if( isAttributeDeclaration( attributeLine ) == false )
			throw new Exception("Not a valid attribute. ");
		
		String[] words = attributeLine.split("\\s+");
		if( words.length < 2 ) throw new Exception("Not a valid attribute.");
		
		return words[1];
	}
	
	public static String[] getNominalValues( String attributeLine ) throws Exception {
		if( isAttributeDeclaration( attributeLine ) == false )
			throw new Exception("Not a valid attribute. ");
		
		int idxStartBracket = attributeLine.indexOf('{');
		int idxEndBracket   = attributeLine.indexOf('}');
		
		if( idxStartBracket == -1 || idxEndBracket == -1 ) {
			throw new Exception("Not a nominal attribute. ");
		}
		if( idxStartBracket > idxEndBracket ) {
			throw new Exception("Not a legal nominal attribute. ");
		}
		return attributeLine.substring( idxStartBracket, idxEndBracket ).split(",");
	}
	
}
