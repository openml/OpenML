package org.openml.junit.objects;

import java.io.IOException;

import org.openml.io.Input;

import weka.core.Instances;

public class Dataset {
	private final String url;
	private final String targetFeature;
	private final String rowid;
	private Instances inst;
	
	public Dataset( final String url, final String targetFeature ) {
		this.url = url;
		this.targetFeature = targetFeature;
		this.rowid = "";
		this.inst = null;
	}
	public String getUrl() {
		return url;
	}
	public String getTargetfeature() {
		return targetFeature;
	}
	public String getRowid() {
		return rowid;
	}
	public String getName() {
		return Input.filename(url);
	}
	public Instances getInstances() throws IOException {
		if( inst == null ) 
			inst = new Instances(Input.getURL(url));
		return inst;
	}
}
