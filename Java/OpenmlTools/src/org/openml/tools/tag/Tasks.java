package org.openml.tools.tag;

import java.util.Arrays;

import org.openml.apiconnector.algorithms.QueryUtils;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;

public class Tasks {

	private static OpenmlConnector openmlConnector;
	
	public static void main( String[] args ) throws Exception {

		Config c = new Config();
		openmlConnector = new OpenmlConnector( c.getServer(), c.getApiKey() );
		
		int[] curves2 = {10047, 10046, 10045, 7535, 10044, 10043, 10042, 10041, 1764, 1728, 1730, 1736, 1742, 1748, 2094, 1702, 10088, 1714, 10082, 10083, 10080, 10081, 10086, 10087, 1723, 10084, 10085, 10073, 10072, 10075, 10074, 10077, 10076, 10079, 10078, 10065, 10064, 10067, 10066, 10069, 10068, 10071, 10070, 10056, 10057, 10058, 10059, 10060, 10061, 10062, 10063, 10048, 10049, 10050, 10051, 10052, 10053, 10054, 10055, 2758 };		
		tag(curves2, "curves2");
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
