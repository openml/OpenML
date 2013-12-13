package org.openml.xml;

import org.openml.constants.Constants;

public class ApiError {
	private final String oml = Constants.OPENML_XMLNS;
	
	private String code;
	private String message;
	private String additional_information;
	
	public String getOml() {
		return oml;
	}
	public String getCode() {
		return code;
	}
	public String getMessage() {
		return message;
	}
	public String getAdditional_information() {
		return additional_information;
	}
}
