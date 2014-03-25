package org.openml.apiconnector.io;

public class ApiException extends Exception {
	
	private static final long serialVersionUID = 1155887744L;
	private int code;
	
	public ApiException( int code, String message ) {
		super( message );
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
