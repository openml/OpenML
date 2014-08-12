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
	private String response;
	private String exampleResponse;
	private ArrayList<Argument> arguments = new ArrayList<Argument>();
	private ArrayList<Schema> schemas = new ArrayList<Schema>();
	private ArrayList<ErrorCode> errorCodes = new ArrayList<ErrorCode>();
	
	public Function( String name, String description ) throws IOException {
		this.name = name;
		this.description = description;
		this.exampleResponse = null;
		this.response = "";
	}
	
	public Function( String name, String description, String response, String exampleUrl ) throws IOException {
		this.name = name;
		this.description = description;
		this.response = response;
		if( exampleUrl.equals("-") == false ) {
			setExampleResponsFromUrl( exampleUrl );
		}
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

	public void addSchema( Schema s ) {
		schemas.add( s );
	}
	
	public void setExampleResponsFromUrl( String exampleUrl ) throws IOException {
		//System.out.println( exampleUrl );
		StringBuilder sb = new StringBuilder();
		URL url = new URL( exampleUrl );
		BufferedReader in = new BufferedReader( new InputStreamReader(url.openStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) sb.append( inputLine + "\n" );
        in.close();
        
        exampleResponse = XmlFormatter.format( sb.toString() ).replace( ">", "&gt;" ).replace( "<", "&lt;" );
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append( "<!-- [START] Api function description: " + name + " --> \n\n\n" );
		sb.append( "<h3 id=" + name.replace('.', '_') + ">" + name + "</h3>" + "\n" );
		sb.append( "<p><i>" + description + "</i></p>" + "\n\n" );
		
		sb.append( "<h5>Arguments</h5>" + "\n" );
		sb.append( "<div class=\"bs-callout\">" + "\n" );
		for( Argument a : arguments ) {
			sb.append( a + "\n" );
		}
		if( arguments.size() == 0 ) sb.append( "None\n" );
		sb.append( "</div>" + "\n" );
		
		if( response.equals("-") == false || schemas.size() > 0 ) {
			sb.append( "<h5>Schema's</h5>\n<div class=\"bs-callout bs-callout-info\">\n<h5>"+name+"</h5>\n" + "\n" );
			sb.append( response + "<br/>" + "\n" );
			for( Schema s : schemas ) {
				sb.append( s + "\n" );
			}
			sb.append( "</div>" + "\n" );
		}
		
		if( exampleResponse != null ) {
			sb.append("<h5>Example Response</h5>\n<div class='highlight'>\n<pre class='pre-scrollable'>\n<code class='html'>" + "\n");
			sb.append( exampleResponse + "\n" );
			sb.append("</code>\n</pre>\n</div>" + "\n");
		}
		
		sb.append("<h5>Error codes</h5>" + "\n");
		sb.append("<div class='bs-callout bs-callout-danger'>\n");
		for( ErrorCode e : errorCodes ) {
			sb.append( e + "\n" );
		}
		if( errorCodes.size() == 0 ) sb.append( "None\n" );
		sb.append("</div>" + "\n\n");
		sb.append( "<!-- [END] Api function description: " + name + " -->  \n\n\n" );
		
		return sb.toString();
	}
	
}
