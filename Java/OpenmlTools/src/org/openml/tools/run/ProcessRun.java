package org.openml.tools.run;

import org.openml.apiconnector.algorithms.QueryUtils;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.settings.Config;

public class ProcessRun {

	private static Config config = new Config("username = janvanrijn@gmail.com; password = Feyenoord2002; server = http://openml.liacs.nl/");
	
	
	public static void main( String[] args ) {
		
		ApiConnector api = new ApiConnector( config.getServer() );
		
		for( int i = 0; i < 2; ++i ) {
			try {
				process(  );
			} catch( Exception e ) {
				e.printStackTrace();
			}
		}
	}
	
	private static void process(  ) throws Exception {
		
			//EvaluateRun er = new EvaluateRun(config);
	}
}
