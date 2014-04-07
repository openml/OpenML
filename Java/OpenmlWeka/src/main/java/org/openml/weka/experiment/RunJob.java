package org.openml.weka.experiment;

import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;
import org.openml.apiconnector.algorithms.DateParser;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.io.ApiSessionHash;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.Job;

import weka.core.Utils;
import weka.core.Version;

public class RunJob {
	
	public static void main( String[] args ) throws Exception {
		
		int n;
		int ttid;
		
		String strN = Utils.getOption('N', args);
		String strTtid = Utils.getOption('T', args);
		
		n = ( strN.equals("") ) ? 1 : Integer.parseInt(strN);
		ttid = ( strTtid.equals("") ) ? 1 : Integer.parseInt(strTtid);
		
		for( int i = 0; i < n; ++i ) {
			doTask(ttid);
		}
	}
	
	public static void doTask( int ttid ) {
		try{ 
			Config c = new Config();
			if( ApiSessionHash.checkCredentials( c.getUsername(), c.getPassword() ) == false ) {
				throw new Exception("Authentication failed. ");
			}
			
			Job j = ApiConnector.openmlRunGetjob( "Weka_" + Version.VERSION, "" + ttid );
			
			System.err.println( "[" + DateParser.humanReadable.format( new Date() ) + "] Task: " + j.getTask_id() + "; learner: " + j.getLearner() );
			
			String[] classArgs= Utils.splitOptions( j.getLearner() );
			String[] taskArgs = new String[3];
			taskArgs[0] = "-T";
			taskArgs[1] = ""+j.getTask_id();
			taskArgs[2] = "-C";
			
			TaskBasedExperiment.main( ArrayUtils.addAll(taskArgs, classArgs) );
		} catch( Exception e) {
			e.printStackTrace();
		}
	}
}
