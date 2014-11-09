package org.openml.tools.tag;

import java.util.Arrays;

import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Tasks;
import org.openml.apiconnector.xml.Tasks.Task;
import org.openml.apiconnector.xml.Tasks.Task.Quality;

public class TagTasksCurves {
	
	public static void main( String[] args ) throws Exception {
		Config config = new Config();
		OpenmlConnector oc = new OpenmlConnector( config.getUsername(), config.getPassword() );
		
		Tasks tasks = oc.openmlTasks( 3 );
		int count = 0;
		
		for( Task task : tasks.getTask() ) {
			
			if( task.getTask_id() < 1700 ) continue;
			
			org.openml.apiconnector.xml.Task current = oc.openmlTaskGet( task.getTask_id() );
			DataSetDescription dsd = oc.openmlDataDescription( task.getDid() );
			
			int repeats = TaskInformation.getNumberOfRepeats( current );
			String[] tags = dsd.getTag();
			Quality[] qualities = task.getQualities();
			
			int instances = 0;
			for( Quality q : qualities ) {
				if( q.getName().equals("NumberOfInstances") ) {
					instances = Integer.parseInt( q.getValue() );
				}
			}
			
			if( repeats == 1 && instances >= 512 ) {
				if( tags != null ) {
					if( Arrays.asList( tags ).contains( "BNG" ) ) {
						continue;
						
					}
				}
				count++;
				try {
					oc.openmlTaskTag( current.getTask_id(), "curves");
				} catch(Exception e ) {
					System.out.println(e.getMessage());
				}
				System.out.println( "Task " + current.getTask_id() + " - " + task.getName() + ", count: " + count );
			}
		}
	}
}
