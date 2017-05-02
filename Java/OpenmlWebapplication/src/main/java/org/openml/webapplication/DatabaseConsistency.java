package org.openml.webapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.openml.apiconnector.io.OpenmlConnector;

public class DatabaseConsistency {

	private final OpenmlConnector connector;
	
	public DatabaseConsistency(OpenmlConnector connector) {
		this.connector = connector;
		connector.setVerboseLevel(1);
	}
	
	public String all_checks() throws JSONException, Exception {
		curves_tasks();
		return "{status: \"OK\"}";
	}
	
	public int curves_tasks() throws JSONException, Exception {
		String sql = "SELECT t.task_id FROM task t LEFT JOIN task_inputs i ON t.task_id = i.task_id AND i.input = \"number_samples\" WHERE ttid = 3 AND value IS NULL";
		
		JSONArray res = (JSONArray) connector.freeQuery(sql).get("data");
		
		if( res.length() > 0 ) {
			
			for (int i = 0; i < res.length(); ++i ) {
				int task_id = ((JSONArray) res.get( i )).getInt( 0 );
				connector.taskDelete(task_id);
			}
			
			return res.length();
		} else {
			return 0;
		}
	}
}
