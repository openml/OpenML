import openml.io.ApiConnector;
import openml.xml.Job;
import moa.DoTask;


public class RunJob {

	public static void main( String[] args ) throws Exception {
		
		for( int i = 0; i < 100; ++i ) {
			doTask();
		}
	}
	
	public static void doTask() throws Exception {
		Job j = ApiConnector.openmlRunGetjob( "moa", "4" );
		
		System.err.println( "task: " + j.getTask_id() + "; learner: " + j.getLearner() );
		
		String[] taskArgs = new String[5];
		taskArgs[0] = "openml.OpenmlDataStreamClassification";
		taskArgs[1] = "-l";
		taskArgs[2] = j.getLearner();
		taskArgs[3] = "-t";
		taskArgs[4] = ""+j.getTask_id();
		
		DoTask.main( taskArgs );
	}
}
