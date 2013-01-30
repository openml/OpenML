package org.openml.apidocs.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.openml.apidocs.helpers.XmlFormatter;

public class Function {
	
	private String name;
	private String description;
	private String exampleRespons;
	private ArrayList<Argument> arguments = new ArrayList<Argument>();
	private ArrayList<ErrorCode> errorCodes = new ArrayList<ErrorCode>();
	
	public Function( String name, String description, String exampleUrl ) throws IOException {
		this.name = name;
		this.description = description;
		setExampleResponsFromUrl( exampleUrl );
	}

	public void addArgument( Argument a ) {
		arguments.add( a );
	}
	
	public void addErrorCode( ErrorCode e ) {
		errorCodes.add( e );
	}
	
	public String getName( char glue ) {
		return name.replace('.', glue);
	}
	
	public void setExampleResponsFromUrl( String exampleUrl ) throws IOException {
		//System.out.println( exampleUrl );
		StringBuilder sb = new StringBuilder();
		URL url = new URL( exampleUrl );
		BufferedReader in = new BufferedReader( new InputStreamReader(url.openStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) sb.append( inputLine + "\n" );
        in.close();
        
        
        exampleRespons = XmlFormatter.format( sb.toString() ).replace( ">", "&gt;" ).replace( "<", "&lt;" );
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( "<section id=\"" + name.replace('.', '_') + "\">" + "\n" );
		sb.append( "<h3>"+ name +" <small>"+description+"</small></h3>" + "\n" );
		
		sb.append( "<h5>Arguments</h5>" + "\n" );
		for( Argument a : arguments ) {
			sb.append( a + "\n" );
		}
		if( arguments.size() == 0 ) sb.append( "None\n" );
		
		sb.append("<h5>Example Response</h5><pre class=\"pre-scrollable\">" + "\n");
		sb.append( exampleRespons + "\n" );
		sb.append("</pre>" + "\n");
		
		sb.append("<h5>Error codes</h5>" + "\n");
		for( ErrorCode e : errorCodes ) {
			sb.append( e + "\n" );
		}
		if( errorCodes.size() == 0 ) sb.append( "None\n" );
		
		sb.append( "</section>" + "\n" );
		return sb.toString();
	}
	
}
