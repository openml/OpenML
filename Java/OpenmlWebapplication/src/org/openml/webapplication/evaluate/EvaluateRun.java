package org.openml.webapplication.evaluate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.settings.Config;

public class EvaluateRun {
	
	private final ApiConnector apiconnector;
	private final Map<String,Integer> file_ids;
	
	public static void main( String[] args ) {
		Config c = new Config("username = janvanrijn@gmail.com; password = Feyenoord2002; server = http://openml.liacs.nl/");
		try {
			new EvaluateRun(1, c);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public EvaluateRun( int run_id, Config config ) throws JSONException, IOException {
		apiconnector = new ApiConnector( config.getServer() );
		file_ids = new HashMap<String, Integer>();
		
		
		JSONObject json =  apiconnector.openmlFreeQuery( "SELECT `field`,`file_id` FROM `runfile` WHERE `source` = " + run_id );
		JSONArray files = (JSONArray) json.get("data");
		
		for( int i = 0; i < files.length(); ++i ) {
			String field = ((JSONArray) files.get( i )).getString( 0 );
			int file_index = ((JSONArray) files.get( i )).getInt( 1 );
			
			file_ids.put(field, file_index);
		}
		
		
		System.out.println( file_ids );
	}
}
