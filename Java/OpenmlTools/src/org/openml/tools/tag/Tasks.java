package org.openml.tools.tag;

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
			"SELECT t.task_id, d.did, d.name, f.name AS target, f.data_type FROM task t, task_inputs i, dataset d, data_feature f, dataset_tag tag " +
			"WHERE t.task_id = i.task_id AND i.input = 'source_data' AND i.value = d.did AND t.ttid =4 AND d.did = f.did AND f.name = d.default_target_attribute AND tag.id = d.did AND tag.tag = 'BNG'";
		
		int[] ids = QueryUtils.getIdsFromDatabase(openmlConnector, sql);
		tag( ids, "streams" );
	}
	
	
	private static void tag( int[] ids, String tag ) {
		for( int id : ids ) {
			try {
				openmlConnector.openmlTaskTag(id, tag );
				
			} catch( Exception e ) {
				System.err.println( "error at: " + id + ". " + e.getMessage() );
			}
		}
	}
}
