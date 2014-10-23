package org.openml.weka.experiment;

import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;
import org.openml.apiconnector.algorithms.DateParser;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.Job;

import weka.core.Utils;
import weka.core.Version;

public class RunJob {
	
	public static void main( String[] args ) throws Exception {
		
		int n;
		int ttid;
		
		Config config = new Config();
		OpenmlConnector apiconnector;
		if( config.getServer() != null ) {
			apiconnector = new OpenmlConnector( config.getServer() );
		} else { 
			apiconnector = new OpenmlConnector();
		}
		
		String strN = Utils.getOption('N', args);
		String strTtid = Utils.getOption('T', args);
		
		n = ( strN.equals("") ) ? 1 : Integer.parseInt(strN);
		ttid = ( strTtid.equals("") ) ? 1 : Integer.parseInt(strTtid);
		
		for( int i = 0; i < n; ++i ) {
			doTask(ttid, config, apiconnector);
		}
	}
	
	public static void doTask( int ttid, Config config, OpenmlConnector apiconnector ) {
		try{ 
			if( apiconnector.checkCredentials( config.getUsername(), config.getPassword() ) == false ) {
				throw new Exception("Authentication failed. ");
			}
			
			Job j = apiconnector.openmlJobGet( "Weka_" + Version.VERSION, "" + ttid );
			
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
