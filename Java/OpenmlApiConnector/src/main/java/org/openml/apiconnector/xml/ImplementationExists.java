package org.openml.apiconnector.xml;

import org.openml.apiconnector.settings.Constants;

public class ImplementationExists {
	private final String oml = Constants.OPENML_XMLNS;
	
	private boolean exists;
	private int id;
	
	public String getOml() {
		return oml;
	}
	public boolean exists() {
		return exists;
	}
	public int getId() {
		return id;
	}
}
