
package org.openml.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class OpenMLUtil {

	private static final int TIMEOUT_URL_CONNECTION = 20000;

	private static void errorOnMissingValue(Object obj) throws MissingValueException {
		if (obj instanceof String) {
			if (((String) obj).length() == 0) {
				throw new MissingValueException();
			}
		} else if (obj instanceof Integer) {
			if (((Integer) obj) == 0){
				throw new MissingValueException();
			}

		}
	}
	
	public static String readStringfromURL(URL url) throws IOException{
		
		if (url == null) {
			throw new IllegalArgumentException("url must not be null!");
		}

		URLConnection connection = url.openConnection();
		connection.setConnectTimeout(TIMEOUT_URL_CONNECTION);
		connection.setReadTimeout(TIMEOUT_URL_CONNECTION);
		InputStream inputStream = connection.getInputStream();
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		int i;
		while((i = inputStream.read()) != -1) {
		bout.write(i);
		}

		byte[] data = bout.toByteArray();
		String urlToString = new String(data);
		
		
		return urlToString;
		
	}
	
	public static Document getDocumentfromXml(String XML) throws SAXException, IOException, ParserConfigurationException{
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		builderFactory.setNamespaceAware(true);
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(XML));

		return builder.parse(is);
		
	}

	public static Document readDocumentfromURL(URL url) throws IOException, ParserConfigurationException, SAXException {

		if (url == null) {
			throw new IllegalArgumentException("url must not be null!");
		}

		URLConnection connection = url.openConnection();
		connection.setConnectTimeout(TIMEOUT_URL_CONNECTION);
		connection.setReadTimeout(TIMEOUT_URL_CONNECTION);
		InputStream inputStream = connection.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		builderFactory.setNamespaceAware(true);
		DocumentBuilder builder = builderFactory.newDocumentBuilder();

		InputSource inputSource = new InputSource(inputStreamReader);
		return builder.parse(inputSource);
		

	}

	public static String readFile(File file) throws IOException {
		StringBuffer content = new StringBuffer();

		FileInputStream fileInputStream = new FileInputStream(file);
		InputStreamReader streamReader = new InputStreamReader(fileInputStream,
				"UTF-8");
		int readChars = 0;
		do {
			char[] contentBuffer = new char[1024];
			readChars = streamReader.read(contentBuffer);
			content.append(contentBuffer, 0, readChars);
		} while (readChars == 1024);
		streamReader.close();
		return content.toString();
	}

	public static Integer getTaskId(Document taskDocument) throws MissingValueException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(new OpenMLNameSpaceContext());
		try {
			Integer taskId = ((Double) xPath.evaluate("/oml:task/oml:task_id",
					taskDocument, XPathConstants.NUMBER)).intValue();
			
			errorOnMissingValue(taskId);
			
			return taskId;
		} catch (XPathExpressionException e) {
			return -1;
		}

	}

	public static String getTaskType(Document taskDocument) throws MissingValueException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(new OpenMLNameSpaceContext());
		try {
			String taskType = xPath.evaluate("/oml:task/oml:task_type", taskDocument);
			errorOnMissingValue(taskType);
			return taskType;
		} catch (XPathExpressionException e) {
			return null;
		}

	}
	
	public static String getEvaluationMethod(Document taskDocument) throws MissingValueException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(new OpenMLNameSpaceContext());
		try {
			String evaluationMethod = xPath.evaluate("/oml:task/oml:estimation_procedure/oml:type", taskDocument);
			errorOnMissingValue(evaluationMethod);
			return evaluationMethod;
		} catch (XPathExpressionException e) {
			return null;
		}

	}
	
	public static String getTargetFeature(Document taskDocument) throws MissingValueException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(new OpenMLNameSpaceContext());
		try {
			String targetFeature = xPath.evaluate("//oml:data_set/oml:target_feature", taskDocument);
			errorOnMissingValue(targetFeature);
			return targetFeature;
		} catch (XPathExpressionException e) {
			return null;
		}

	}
	
	public static Integer getNumberOfFolds(Document taskDocument) throws MissingValueException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(new OpenMLNameSpaceContext());
		try {
			Integer numberOfFolds = ((Double) xPath.evaluate("//oml:parameter[@name=\"number_folds\"]",
					taskDocument, XPathConstants.NUMBER)).intValue();
			errorOnMissingValue(numberOfFolds);
			return numberOfFolds;
		} catch (XPathExpressionException e) {
			return -1;
		}

	}
	
	public static Integer getNumberOfRepeats(Document taskDocument) throws MissingValueException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(new OpenMLNameSpaceContext());
		try {
			Integer numberOfRepeats = ((Double) xPath.evaluate("//oml:parameter[@name=\"number_repeats\"]",
					taskDocument, XPathConstants.NUMBER)).intValue();
			errorOnMissingValue(numberOfRepeats);
			return numberOfRepeats;
		} catch (XPathExpressionException e) {
			return -1;
		}

	}
	
	

	public static Integer getDataSetId(Document taskDocument) throws MissingValueException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(new OpenMLNameSpaceContext());
		try {
			Integer dataSetId = ((Double) xPath.evaluate("//oml:data_set/oml:data_set_id",
					taskDocument, XPathConstants.NUMBER)).intValue();
			errorOnMissingValue(dataSetId);
			return dataSetId;
		} catch (XPathExpressionException e) {
			return -1;
		}

	}
	
	public static String getDataSplitURL(Document taskDocument) throws MissingValueException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(new OpenMLNameSpaceContext());
		try {
			String dataSetURL = (String) xPath.evaluate("//oml:data_splits_url",taskDocument, XPathConstants.STRING);
			errorOnMissingValue(dataSetURL);
			return dataSetURL;
		} catch (XPathExpressionException e) {
			errorOnMissingValue("");
		}
		return null;
	}

	public static String getRowIdAttribute(Document dataSetDocument) throws MissingValueException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(new OpenMLNameSpaceContext());
		try {
			String rowIdAttribute = xPath.evaluate("/oml:data_set_description/oml:row_id_attribute",
					dataSetDocument);
			errorOnMissingValue(rowIdAttribute);
			return rowIdAttribute;
		} catch (XPathExpressionException e) {
			return null;
		}

	}

	public static URL getDataSetURL(Document dataSetDocument) throws MissingValueException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(new OpenMLNameSpaceContext());
		try {
			String datasetURL = xPath.evaluate("/oml:data_set_description/oml:url",
					dataSetDocument);
			errorOnMissingValue(datasetURL);
			return new URL(datasetURL);
		} catch (XPathExpressionException e) {
			return null;
		} catch (MalformedURLException e) {
			return null;
		}

	}


}
