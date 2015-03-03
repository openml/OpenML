package org.openml.tools.tag;

import java.util.Arrays;

import org.openml.apiconnector.algorithms.QueryUtils;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;

public class Tasks {

	private static OpenmlConnector openmlConnector;
	
	public static void main( String[] args ) throws Exception {

		Config c = new Config();
		openmlConnector = new OpenmlConnector( c.getServer(), c.getUsername(), c.getPassword() );
		bng_tasks();
	}
	
	
	private static void bng_tasks() throws Exception {
		String sql = 
			"SELECT t.task_id FROM dataset d, task tt, task_inputs t,data_quality q WHERE d.did = t.value AND t.input = \"source_data\" AND q.quality = \"NumberOfInstances\" AND q.data = d.did AND tt.task_id = t.task_id AND tt.ttid = 1 AND ABS(q.value) <= 100000";
		
		int[] ids = QueryUtils.getIdsFromDatabase(openmlConnector, sql);
		System.out.println( "Tagging " + ids.length + ": " + Arrays.toString( ids ) );
		tag( ids, "under100k" );
	}
	
	
	private static void tag( int[] ids, String tag ) {
		for( int id : ids ) {
			try {
				openmlConnector.taskTag(id, tag );
				
			} catch( Exception e ) {
				System.err.println( "error at: " + id + ". " + e.getMessage() );
			}
		}
	}
}
