package org.openml.apidocs.model;

public class Schema {
	private String name;
	private String url;
	
	public Schema( String name, String url ) {
		this.name = name;
		this.url = url;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<a type=\"button\" class=\"btn btn-primary\" href=\""+url+"\">"+name+"</a>");
		
		return sb.toString();
	}
}
