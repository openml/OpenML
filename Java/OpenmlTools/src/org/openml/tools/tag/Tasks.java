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
		
		int[] curves2 = {7535, 1764, 10039, 10038, 10037, 10036, 10035, 10034, 10033, 10032, 10030, 10031, 10028, 10029, 10026, 10027, 10024, 10025, 10022, 10023, 10020, 10021, 10018, 10019, 10016, 10017, 10013, 1728, 10012, 10015, 1730, 10014, 10009, 10008, 10011, 10010, 10005, 10004, 1736, 10007, 10006, 10001, 10000, 10003, 1742, 10002, 9996, 9997, 9998, 9999, 1748, 9992, 9993, 9994, 9995, 2094, 1702, 1714, 1723, 2758};
		
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
