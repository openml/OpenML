/*
 *  OpenmlApiConnector - Java integration of the OpenML Web API
 *  Copyright (C) 2014 
 *  @author Jan N. van Rijn (j.n.van.rijn@liacs.leidenuniv.nl)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
package org.openml.apiconnector.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.Hashing;
import org.openml.apiconnector.settings.Settings;
import org.openml.apiconnector.xml.ApiError;
import org.openml.apiconnector.xml.Authenticate;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Implementation;
import org.openml.apiconnector.xml.ImplementationExists;
import org.openml.apiconnector.xml.Job;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.UploadDataSet;
import org.openml.apiconnector.xml.UploadImplementation;
import org.openml.apiconnector.xml.UploadRun;
import org.openml.apiconnector.xstream.XstreamXmlMapping;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import org.openml.apiconnector.xml.ImplementationDelete;
import org.openml.apiconnector.xml.ImplementationOwned;

import com.thoughtworks.xstream.XStream;

public class ApiConnector {
	
	public static String API_URL = Settings.BASE_URL; // can be altered outside the class
	private static final String API_PART = "rest_api/";
	private static XStream xstream = XstreamXmlMapping.getInstance();
	
	private static HttpClient httpclient;
	
	/**
	 * @param username - The username that is used for authentication
	 * @param password - The password used for authentication
	 * @return Authenticate - An object containing the Api Session Hash 
	 * (which can be used to authenticate without username / password)
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public static Authenticate openmlAuthenticate( String username, String password ) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", Hashing.md5( password )));
		
		Object apiResult = doApiRequest("openml.authenticate", "", new UrlEncodedFormEntity(params, "UTF-8"));
        if( apiResult instanceof Authenticate){
        	return (Authenticate) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to Authenticate");
        }
	}
	
	/**
	 * @param did - The data_id of the data description to download. 
	 * @return DataSetDescription - An object containing the description of the data
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public static DataSetDescription openmlDataDescription( int did ) throws Exception {
		Object apiResult = doApiRequest("openml.data.description", "&data_id=" + did );
        if( apiResult instanceof DataSetDescription){
        	return (DataSetDescription) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to DataSetDescription");
        }
	}
	
	/**
	 * @param implementation_id - Numeric ID of the implementation to be obtained. 
	 * @return Implementation - An object containing the description of the implementation
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public static Implementation openmlImplementationGet(int implementation_id) throws Exception {
		Object apiResult = doApiRequest("openml.implementation.get", "&implementation_id=" + implementation_id );
        if( apiResult instanceof Implementation){
        	return (Implementation) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to Implementation");
        }
	}
	
	/**
	 * @param session_hash - A session hash (obtainable by openmlAuthenticate)
	 * @return ImplementationOwned - An object containing all implementation_ids that are owned by the current user.
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public static ImplementationOwned openmlImplementationOwned( String session_hash ) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("session_hash",new StringBody(session_hash));
		
		Object apiResult = doApiRequest("openml.implementation.owned", "", params);
		if( apiResult instanceof ImplementationOwned){
        	return (ImplementationOwned) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to ImplementationOwned");
        }
	}
	
	/**
	 * @param id - The numeric id of the implementation to be deleted. 
	 * @param session_hash - A session hash (obtainable by openmlAuthenticate)
	 * @return
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public static ImplementationDelete openmlImplementationDelete( int id, String session_hash ) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("implementation_id",new StringBody(""+id));
		params.addPart("session_hash",new StringBody(session_hash));
		
		Object apiResult = doApiRequest("openml.implementation.delete", "", params);
		if( apiResult instanceof ImplementationDelete){
        	return (ImplementationDelete) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to ImplementationDelete");
        }
	}
	
	/**
	 * @param name - The name of the implementation to be checked
	 * @param external_version - The external version (workbench version). If not a proper revision number is available, 
	 * it is recommended to use a MD5 hash of the source code.
	 * @return ImplementationExists - An object describing whether this implementation is already known on the server.
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public static ImplementationExists openmlImplementationExists( String name, String external_version ) throws Exception {
		Object apiResult = doApiRequest("openml.implementation.exists", "&name=" + name + "&external_version=" + external_version );
        if( apiResult instanceof ImplementationExists){
        	return (ImplementationExists) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to ImplementationExists");
        }
	}
	
	/**
	 * @param task_id - The numeric id of the task to be obtained.
	 * @return Task - An object describing the task
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public static Task openmlTasksSearch( int task_id ) throws Exception {
		Object apiResult = doApiRequest("openml.tasks.search", "&task_id=" + task_id );
        if( apiResult instanceof Task){
        	return (Task) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to Task");
        }
	}
	
	/**
	 * @param description - An XML file describing the data. See documentation at openml.org
	 * @param dataset - The actual dataset. Preferably in ARFF format, but almost everything is OK. 
	 * @param session_hash - A session hash (obtainable by openmlAuthenticate)
	 * @return UploadDataSet - An object containing information on the data upload. 
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public static UploadDataSet openmlDataUpload( File description, File dataset, String session_hash ) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));
		if( dataset != null) params.addPart("dataset", new FileBody(dataset));
		params.addPart("session_hash",new StringBody(session_hash));
        
        Object apiResult = doApiRequest("openml.data.upload", "", params);
        if( apiResult instanceof UploadDataSet){
        	return (UploadDataSet) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to UploadDataSet");
        }
	}
	
	/**
	 * @param description - An XML file describing the data. See documentation at openml.org. Should contain the url field.
	 * @param session_hash - A session hash (obtainable by openmlAuthenticate)
	 * @return UploadDataSet - An object containing information on the data upload. 
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public static UploadDataSet openmlDataUpload( File description, String session_hash ) throws Exception {
		return openmlDataUpload(description, null, session_hash);
	}
	
	/**
	 * @param descriptionUploadDataSet - An XML file describing the implementation. See documentation at openml.org.
	 * @param binary - A file containing the implementation binary. 
	 * @param source - A file containing the implementation source.
	 * @param session_hash - A session hash (obtainable by openmlAuthenticate)
	 * @return UploadImplementation - An object containing information on the implementation upload. 
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public static UploadImplementation openmlImplementationUpload( File description, File binary, File source, String session_hash ) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));
		if(source != null)
			params.addPart("source", new FileBody(source));
		if(binary != null)
			params.addPart("binary", new FileBody(binary));
		params.addPart("session_hash",new StringBody(session_hash));
		
        Object apiResult = doApiRequest("openml.implementation.upload", "", params);
        if( apiResult instanceof UploadImplementation){
        	return (UploadImplementation) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to UploadImplementation");
        }
	}
	
	/**
	 * @param description - An XML file describing the run. See documentation at openml.org.
	 * @param output_files - A Map<String,File> containing all relevant output files. Key "predictions" 
	 * usually contains the predictions that were generated by this run. 
	 * @param session_hash - A session hash (obtainable by openmlAuthenticate)
	 * @return UploadRun - An object containing information on the implementation upload. 
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, etc.
	 */
	public static UploadRun openmlRunUpload( File description, Map<String,File> output_files, String session_hash ) throws Exception {
		MultipartEntity params = new MultipartEntity();
		if(Settings.API_VERBOSE) {
			System.out.println( Conversion.fileToString(output_files.get("predictions")) );
			System.out.println("\n==========\n"+Conversion.fileToString(description)+"\n==========");
		}
		params.addPart("description", new FileBody(description));
		for( String s : output_files.keySet() ) {
			params.addPart(s,new FileBody(output_files.get(s)));
		}
		params.addPart("session_hash",new StringBody(session_hash));
		Object apiResult = doApiRequest("openml.run.upload", "", params);
        if( apiResult instanceof UploadRun){
        	return (UploadRun) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to UploadRun");
        }
	}
	
	/**
	 * @param workbench - The workbench that will execute the task.
	 * @param task_type_id - The task type id that the workbench should execute. 
	 * Weka generally performs Supervised Classification tasks, whereas MOA performs 
	 * Data Stream tasks. For task id's, please see openml.org.
	 * @return Job - An object describing the task to be executed
	 * @throws Exception - Can be: API Error (see documentation at openml.org), 
	 * server down, no tasks available for this workbench.
	 */
	public static Job openmlRunGetjob( String workbench, String task_type_id ) throws Exception {
		Object apiResult = doApiRequest("openml.run.getjob", "&workbench=" + workbench + "&task_type_id=" + task_type_id );
        if( apiResult instanceof Job ){
        	return (Job) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to Job");
        }
	}
	
	/**
	 * @param url - The URL to obtain
	 * @return String - The content of the URL
	 * @throws IOException - Can be: server down, etc.
	 */
	public static String getStringFromUrl( String url ) throws IOException {
		return IOUtils.toString(  new URL( url ) );
	}
	
	/**
	 * @param url - The URL to obtain
	 * @param filepath - Where to safe the file.
	 * @return File - a pointer to the file that was saved. 
	 * @throws IOException - Can be: server down, etc.
	 */
	public static File getFileFromUrl( String url, String filepath ) throws IOException {
		File file = new File( filepath );
		FileUtils.copyURLToFile( new URL(url), file );
		return file;
	}
	
	private static Object doApiRequest(String function, String queryString) throws Exception {
		return doApiRequest(function, queryString, null);
	}
	
	private static Object doApiRequest(String function, String queryString, HttpEntity entity) throws Exception {
		String result = "";
		httpclient = new DefaultHttpClient();
		String requestUri = API_URL + API_PART + "?f=" + function + queryString;
		long contentLength = 0;
		try {
            HttpPost httppost = new HttpPost( requestUri );
            
            if(entity != null) {
            	httppost.setEntity(entity);
            }
            
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
            	result = httpEntitiToString(resEntity);
                contentLength = resEntity.getContentLength();
            } else {
            	throw new Exception("An exception has occured while reading data input stream. ");
            }
		} finally {
            try { httpclient.getConnectionManager().shutdown(); } catch (Exception ignore) {}
        }
		if(Settings.API_VERBOSE)
			System.out.println("===== REQUEST URI: " + requestUri + " (Content Length: "+contentLength+") =====\n" + result + "\n=====\n");
		
		Object apiResult = xstream.fromXML(result);
		if(apiResult instanceof ApiError) {
			ApiError apiError = (ApiError) apiResult;
			throw new ApiException( Integer.parseInt( apiError.getCode() ), apiError.getMessage() );
		}
		return apiResult;
	}
	
	private static String httpEntitiToString(HttpEntity resEntity) throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(new InputStreamReader( resEntity.getContent() ), writer );
		return writer.toString();
	}
}
