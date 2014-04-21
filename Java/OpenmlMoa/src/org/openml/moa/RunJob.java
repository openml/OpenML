package org.openml.moa;

import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.io.ApiSessionHash;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.Job;

import weka.core.Utils;
import moa.DoTask;

public class RunJob {
	
	private static ApiConnector apiconnector;
	
	public static void main( String[] args ) throws Exception {
		
		int n;
		int ttid;
		Config c = new Config();
		
		if( c.getServer() != null ) {
			apiconnector = new ApiConnector( c.getServer() );
		} else { 
			apiconnector = new ApiConnector();
		}
		
		ApiSessionHash ash = new ApiSessionHash(apiconnector);
		
		
		if( ash.checkCredentials(c.getUsername(), c.getPassword() ) == false ) {
			throw new Exception("Username/password incorrect");
		}
		
		String strN = Utils.getOption('N', args);
		String strTtid = Utils.getOption('T', args);
		
		n = ( strN.equals("") ) ? 1 : Integer.parseInt(strN);
		ttid = ( strTtid.equals("") ) ? 4 : Integer.parseInt(strTtid);
		
		for( int i = 0; i < n; ++i ) {
			doTask(ttid);
		}
	}
	
	public static void doTask(int ttid) {
		try {
			Job j = apiconnector.openmlJobGet( "Moa_2014.03", "" + ttid );
			
			System.err.println( "task: " + j.getTask_id() + "; learner: " + j.getLearner() );
			
			String[] taskArgs = new String[5];
			taskArgs[0] = "openml.OpenmlDataStreamClassification";
			taskArgs[1] = "-l";
			taskArgs[2] = "(" + j.getLearner() + ")";
			taskArgs[3] = "-t";
			taskArgs[4] = ""+j.getTask_id();
			
			DoTask.main( taskArgs );
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
}
