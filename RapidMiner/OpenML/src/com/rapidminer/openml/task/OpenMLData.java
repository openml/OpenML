package com.rapidminer.openml.task;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.ResultObjectAdapter;


public class OpenMLData extends ResultObjectAdapter  {

	private static final long serialVersionUID = -4215319878656535337L;
	
	private int dataId;
	private String dataDescriptionXML;
	private ExampleSet data;

	public OpenMLData(int dataId, String dataDescriptionXML,ExampleSet data) {
		this.dataId = dataId;
		this.dataDescriptionXML = dataDescriptionXML;
		this.data = data;

	}
	
	public int getDataId() {
		return dataId;
	}

	
	public String getDataDescriptionXML() {
		return dataDescriptionXML;
	}

	
	public ExampleSet getData() {
		return data;
	}
	
	@Override
	public String toResultString() {
		return "data id: "+dataId + "\n" + dataDescriptionXML;
	}
	
	

}
