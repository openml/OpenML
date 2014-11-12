package org.openml.tools.tag;

import java.util.Arrays;

import org.openml.apiconnector.algorithms.QueryUtils;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;

public class Setups {

	public static void main( String[] args ) throws Exception {
		Config c = new Config();
		OpenmlConnector openmlConnector = new OpenmlConnector( c.getServer(), c.getUsername(), c.getPassword() );
		
		String sql = "SELECT s.sid FROM algorithm_setup s, implementation i WHERE i.id = s.implementation_id AND i.dependencies = 'Weka_3.7.12-SNAPSHOT' AND s.isDefault = 'true' AND setup_string NOT LIKE 'weka.classifiers.meta.AttributeSelectedClassifier%'";
		int[] res = QueryUtils.getIdsFromDatabase( openmlConnector, sql );
		
		System.out.println( Arrays.toString( res ) );
		for( Integer setup_id : res ) {
			try {
				openmlConnector.openmlSetupTag(setup_id, "curves");
				
			} catch( Exception e ) {
				System.err.println( "error at: " + setup_id + ". " + e.getMessage() );
			}
		}
	}
}
