package org.openml.tools.run;

import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;

public class Reset {

	public static void main( String[] args ) throws Exception {
		Config c = new Config();
		OpenmlConnector connector = new OpenmlConnector( c.getServer(), c.getUsername(), c.getPassword() );
		
		connector.openmlRunReset( 53002 );
		
	}
}
