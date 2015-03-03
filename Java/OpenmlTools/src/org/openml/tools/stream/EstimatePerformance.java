package org.openml.tools.stream;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.openml.apiconnector.io.OpenmlConnector;

public class EstimatePerformance {

	
	public static void main( String args[] ) throws Exception {
		OpenmlConnector api = new OpenmlConnector();
		Map<Integer, ScoreObject> scores = new HashMap<Integer, ScoreObject>();
		
		String classifier = "moa.HoeffdingTree(1)";
		String sql = "SELECT r.rid, r.task_id, v.value AS did, q.value AS numInstances, e.value "
				+ "FROM `run` `r`,`evaluation` `e`, `task_values` `v`, data_quality q, implementation i, algorithm_setup s "
				+ "WHERE v.value = q.data and q.quality = 'NumberOfInstances' AND v.task_id = r.task_id "
				+ "AND v.input = 1 AND r.setup = s.sid and s.implementation_id = i.id "
				+ "AND e.function = 'predictive_accuracy' AND e.source = r.rid and "
				+ "r.task_id IN (2056, 171, 170, 175, 174, 163, 160, 185, 190, "
				+ "191, 188, 189, 178, 177, 182, 183, 2133, 2132, 2134, 2129, 200, "
				+ "2128, 2131, 2130, 197, 196, 199, 198, 193, 192, 195, 194, 2126, "
				+ "2127, 2167, 2166, 2165, 2164, 2163, 2162, 2160, 2150, 2151, "
				+ "127, 2159, 2156, 2157, 2154, 122) AND i.fullName IN ('"+classifier+"' ) ";
		
		JSONArray ja = (JSONArray) api.freeQuery(sql).get("data");
		
		for( int i = 0; i < ja.length(); ++i ) {
			JSONArray row = (JSONArray) ja.get( i );
			/*System.out.println( "rid = " + row.get( 0 ) );
			System.out.println( "task = " + row.get( 1 ) );
			System.out.println( "did = " + row.get( 2 ) );
			System.out.println( "numinstances = " + row.get( 3 ) );
			System.out.println( "score = " + row.get( 4 ) );
			System.out.println( "===================" );*/
			
			int task = row.getInt( 1 );
			int instances = row.getInt( 3 );
			double score = row.getDouble( 4 );
			
			
			System.out.println( "adding: " + task + " with score " + score + " and " + instances + "instances: ");
			scores.put( row.getInt( 1 ), new ScoreObject(row.getInt( 3 ), row.getDouble( 4 ) ) );
			
		}
		

		System.out.println( scores );
		System.out.println( scores.size() );
		
		double total = 0.0;
		int total_w = 0;
		for( Integer i : scores.keySet() ) {
			total += scores.get( i ).score * scores.get( i ).instances;
			total_w += scores.get( i ).instances;
		}
		
		System.out.println( total / total_w );
	}
	
	public static class ScoreObject {
		int instances;
		double score;
		
		public ScoreObject( int instances, double score ) {
			this.instances = instances;
			this.score = score;
		}
		
		@Override
		public String toString() {
			return "" + score;
		}
	}
}
