package org.openml.moa;
import openml.io.ApiConnector;
import openml.xml.Job;
import weka.core.Utils;
import moa.DoTask;

public class RunJob {

	public static void main( String[] args ) throws Exception {
		
		int n;
		int ttid;
		
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
			Job j = ApiConnector.openmlRunGetjob( "Moa_2014.03", "" + ttid );
			
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
