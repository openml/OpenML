
package com.rapidminer.openml.task;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.openml.util.OpenMLUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.rapidminer.operator.ResultObjectAdapter;

public abstract class OpenMLTask extends ResultObjectAdapter {

	private static final long serialVersionUID = -2791609055769347704L;
	
	private int taskId;
	private String taskXML;
	private OpenMLData data;
	private Document taskDocument;

	public OpenMLTask(int taskId, String taskXML, OpenMLData data) throws SAXException, IOException, ParserConfigurationException {
		this.taskId = taskId;
		this.taskXML = taskXML;
		this.data = data;
		this.taskDocument = OpenMLUtil.getDocumentfromXml(getTaskXML());
	}

	public int getTaskId() {
		return taskId;
	}
	
	public Document getTaskDocument() {
		return taskDocument;
	}

	public String getTaskXML() {
		return taskXML;
	}

	public OpenMLData getData() {
		return data;
	}

	@Override
	public String toResultString() {
		return "Task id: "+taskId + "\n" + taskXML;
	}
	
	

}