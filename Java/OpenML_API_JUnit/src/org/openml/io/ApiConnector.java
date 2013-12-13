package org.openml.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

import org.openml.algorithms.Hashing;
import org.openml.constants.Settings;
import org.openml.xml.ApiError;
import org.openml.xml.Authenticate;
import org.openml.xml.DataSetDescription;
import org.openml.xml.Implementation;
import org.openml.xml.ImplementationDelete;
import org.openml.xml.ImplementationOwned;
import org.openml.xml.UploadDataSet;
import org.openml.xml.UploadImplementation;
import org.openml.xml.UploadRun;
import org.openml.xml.XstreamXmlMapping;
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

import com.thoughtworks.xstream.XStream;

public class ApiConnector {
	private static final String API_URL = Settings.BASE_URL + "api/";
	private static XStream xstream = XstreamXmlMapping.getInstance();
	
	private static HttpClient httpclient;
	
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
	
	public static DataSetDescription openmlDataDescription( int did ) throws Exception {
		Object apiResult = doApiRequest("openml.data.description", "&data_id=" + did );
        if( apiResult instanceof DataSetDescription){
        	return (DataSetDescription) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to DataSetDescription");
        }
	}
	
	public static Implementation openmlImplementationGet(String implementation_id) throws Exception {
		Object apiResult = doApiRequest("openml.implementation.get", "&implementation_id=" + implementation_id );
        if( apiResult instanceof Implementation){
        	return (Implementation) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to Implementation");
        }
	}
	
	public static UploadDataSet openmlDataUpload( File description, File dataset, String session_hash ) throws Exception {
		MultipartEntity params = new MultipartEntity();
		params.addPart("description", new FileBody(description));
		params.addPart("dataset", new FileBody(dataset));
		params.addPart("session_hash",new StringBody(session_hash));
        
        Object apiResult = doApiRequest("openml.data.upload", "", params);
        if( apiResult instanceof UploadDataSet){
        	return (UploadDataSet) apiResult;
        } else {
        	throw new DataFormatException("Casting Api Object to UploadDataSet");
        }
	}
	
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
	
	public static UploadRun openmlRunUpload( File description, Map<String,File> output_files, String session_hash ) throws Exception {
		MultipartEntity params = new MultipartEntity();
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
	
	private static Object doApiRequest(String function, String queryString) throws Exception {
		return doApiRequest(function, queryString, null);
	}
	
	private static Object doApiRequest(String function, String queryString, HttpEntity entity) throws Exception {
		String result = "";
		httpclient = new DefaultHttpClient();
		String requestUri = API_URL + "?f=" + function + queryString;
		try {
            HttpPost httppost = new HttpPost( requestUri );
            
            if(entity != null) {
            	httppost.setEntity(entity);
            }
            
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
            	result = httpEntitiToString(resEntity);
            } else {
            	throw new Exception("An exception has occured while reading data input stream. ");
            }
		} finally {
            try { httpclient.getConnectionManager().shutdown(); } catch (Exception ignore) {}
        }
		System.err.println("api result: " + result);
		Object apiResult = xstream.fromXML(result);
		if(apiResult instanceof ApiError) {
			ApiError apiError = (ApiError) apiResult;
			throw new Exception("ApiError " + apiError.getCode() + ": " + apiError.getMessage() );
		}
		return apiResult;
	}
	
	private static String httpEntitiToString(HttpEntity resEntity) throws IOException {
		String result = "";
		BufferedReader br = new BufferedReader( new InputStreamReader( resEntity.getContent() ) );
        while( br.ready() ) result += br.readLine();
        return result;
	}
}
