package org.openml.models;

import org.openml.io.Output;

public class JsonItem {
	private String key;
	private String value;
	private boolean quotes;
	
	public JsonItem( String key, String value ) {
		this.key = key;
		this.value = value;
		this.quotes = true;
	}
	
	public JsonItem( String key, String value, boolean quotes ) {
		this.key = key;
		this.value = value;
		this.quotes = quotes;
	}
	
	public JsonItem( String key, Double value ) {
		this.key = key;
		this.value = Output.getDecimalFormat().format(value);
		this.quotes = false;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public boolean useQuotes() {
		return quotes;
	}
}
