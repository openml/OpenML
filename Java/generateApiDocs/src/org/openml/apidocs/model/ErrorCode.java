package org.openml.apidocs.model;

public class ErrorCode implements Comparable<ErrorCode> {

	private int code;
	private String error;
	private String description;
	
	public ErrorCode( int code, String error, String description ) {
		this.code = code;
		this.error = error;
		this.description = description;
	}

	public int getCode() {
		return code;
	}

	public String getError() {
		return error;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<dl><dt>"+code+": "+error+"</dt><dd>"+description+"</dd></dl>");
		
		return sb.toString();
	}

	@Override
	public int compareTo(ErrorCode o) {
		if( o.getCode() > code )
			return -1;
		if( o.getCode() < code )
			return 1;
		return 0;
	}
}
