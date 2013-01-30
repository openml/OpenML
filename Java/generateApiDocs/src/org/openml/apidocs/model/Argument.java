package org.openml.apidocs.model;

public class Argument {

	private String name;
	private String protocol;
	private boolean required;
	private String description;
	
	public Argument( String name, String protocol, boolean required, String description ) {
		this.name = name;
		this.protocol = protocol;
		this.required = required;
		this.description = description;
	}

	@Override 
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( "<dl><dt><code>"+protocol+" "+name+"</code>" );
		if( required )sb.append(" (Required)");
		sb.append("</dt><dd>"+description+"</dd></dl>");
		return sb.toString();
	}
}
