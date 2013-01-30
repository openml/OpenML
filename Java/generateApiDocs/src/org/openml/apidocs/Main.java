package org.openml.apidocs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.openml.apidocs.model.Argument;
import org.openml.apidocs.model.Category;
import org.openml.apidocs.model.ErrorCode;
import org.openml.apidocs.model.Function;


public class Main {
	
	/* generates PHP api docs, based on a file called input/api.in 
	 * also puts all errors in a file called api-errors.php, on which
	 * the PHP api relies.
	 * 
	 * Format of api.in:
	 * 
	 * One line with the number of categories (integer)
	 * FOREACH category:
	 * 		one line with the name (string)
	 * 		one line with the number of functions (integer)
	 * 		FOREACH function:
	 * 			one line with the function name (typically openml.category.name) (string)
	 * 			one line with description of function (string)
	 * 			one line with URL for example output (string, valid URL)
	 * 			one line with number of parameters (numeric)
	 * 			FOREACH parameter
	 * 				one line with parameter name (string)
	 * 				one line with protocol (GET,POST,PUT) 
	 * 				one line with required (boolean)
	 * 				one line with description (string)
	 * 			one line with number of errorCodes (integer)
	 * 			FOREACH errorCode
	 * 				one line with number, or code (integer)
	 * 				one line with error (short description, shown by api respons.) (string)
	 * 				one line with description (shown in apidocs) (string)
	 * 			one empty line, for readability. omitting this line will result in an error. 
	 * */
	
	public ArrayList<Category> categories = new ArrayList<Category>();
	public ArrayList<ErrorCode> errorCodes = new ArrayList<ErrorCode>();
	
	public static void main( String args[] ) throws IOException {
		new Main();
	}
	
	public Main() throws IOException {
		init( "input/api.in" );
		writeApiDocs( "output/apidocs.php", "input/template.tpl.php" );
		writeApiErrors( "output/api-errors.php" );
	}
	
	private void writeApiDocs( String filename, String templatePath ) throws IOException {
		BufferedWriter bw = new BufferedWriter( new FileWriter( new File( filename ) ) );
		String template = FileUtils.readFileToString( new File( templatePath ) );
		StringBuilder menuString = new StringBuilder();
		StringBuilder functionsString = new StringBuilder();
		for( Category c : categories ) {
			menuString.append( c.generateMenu() + "\n" );
			functionsString.append( c + "\n" );
		}
		template = template.replace( "[[[MENU]]]", menuString.toString() );
		template = template.replace( "[[[FUNCTIONS]]]", functionsString.toString() );

		bw.append( template );
		bw.close();
	}
	
	private void writeApiErrors( String filename ) throws IOException {
		BufferedWriter bw = new BufferedWriter( new FileWriter( new File( filename ) ) );
		bw.append("<?php\n\n");
		for( ErrorCode e : errorCodes ) {
			bw.append( "$apiErrors["+e.getCode()+"][0] = '"+e.getError()+"';\n" );
			bw.append( "$apiErrors["+e.getCode()+"][1] = '"+e.getDescription()+"';\n\n" );
		}
		bw.append("\n\n?>");
		bw.close();
	}
	
	private void init( String filename ) throws IOException  {
		BufferedReader br = new BufferedReader( new FileReader( new File( filename ) ) );
		
		Integer nrOfCategories = Integer.parseInt( br.readLine() );
		//System.out.println( "nrOfCategories: " + nrOfCategories );
		for( int i = 0; i < nrOfCategories; i++ ) {
			Category c;
			String categoryName = br.readLine();
			Integer categoryNrOfFunctions = Integer.parseInt( br.readLine() );
			//System.out.println( "nrOfFunctions: " + categoryNrOfFunctions);
			
			c = new Category( categoryName );
			for( int j = 0; j < categoryNrOfFunctions; j++ ) {
				Function f;
				String functionName = br.readLine();
				String functionDescription = br.readLine();
				String functionExampleUrl = br.readLine();
				f = new Function( functionName, functionDescription, functionExampleUrl );
				Integer functionNrOfParams = Integer.parseInt( br.readLine() );
				for( int k = 0; k < functionNrOfParams; k++ ) {
					Argument a;
					String parameterName = br.readLine();
					String parameterProtocol = br.readLine();
					Boolean parameterRequired = br.readLine().equals( "true" ) ? true : false;
					String parameterDescription = br.readLine();
					a = new Argument(parameterName, parameterProtocol, parameterRequired, parameterDescription);
					f.addArgument(a);
				}
				Integer functionNrOfErrorCodes = Integer.parseInt( br.readLine() );
				for( int k = 0; k < functionNrOfErrorCodes; k++ ) {
					ErrorCode e;
					Integer errorCode = Integer.parseInt( br.readLine() );
					String errorError = br.readLine();
					String errorDescription = br.readLine();
					e = new ErrorCode(errorCode, errorError, errorDescription);
					f.addErrorCode(e);
					errorCodes.add(e);
				}
				c.addFunction(f);
				br.readLine();
			}
			categories.add( c );
		}
		br.close();
		Collections.sort( errorCodes );
	}
}
