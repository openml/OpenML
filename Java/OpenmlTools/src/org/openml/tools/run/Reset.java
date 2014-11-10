package org.openml.tools.run;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.QueryUtils;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.RunReset;

public class Reset {

	public static void main( String[] args ) throws Exception {
		
		String sql = 
			"SELECT r.rid " +
			"FROM `run` `r`, `algorithm_setup` `s`, `setup_tag` `st`, task_tag `tt`, `implementation` `i` " +
			"WHERE `i`.`id` = `s`.`implementation_id` AND `r`.`task_id` = `tt`.`id` " +
			"AND `r`.`setup` = `s`.`sid` AND `s`.`sid` = `st`.`id` " +
			"AND `r`.`error`  IS NOT NULL " +
			"AND st.tag = 'curves' AND tt.tag = 'curves' " +
			"AND r.error NOT LIKE 'Inconsistent Eva%'";
		
		reset(sql);
	}
	
	private static void reset( String sql ) throws Exception {
		Config c = new Config();
		OpenmlConnector connector = new OpenmlConnector( c.getServer(), c.getUsername(), c.getPassword() );
		
		int[] ids = QueryUtils.getIdsFromDatabase(connector, sql);
		
		Conversion.log( "OK", "Init", "Total runs to be resetted: " + ids.length );
		for( int id : ids ) {
			RunReset r = connector.openmlRunReset( id );
			Conversion.log( "OK", "Reset", "Reset run: " + r.getRun_id() );
		}
	}
}
