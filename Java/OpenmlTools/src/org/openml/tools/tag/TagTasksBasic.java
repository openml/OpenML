package org.openml.tools.tag;

import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.Tasks;
import org.openml.apiconnector.xml.Tasks.Task;

public class TagTasksBasic {
	
	public static void main( String[] args ) throws Exception {
		Config config = new Config();
		OpenmlConnector oc = new OpenmlConnector( config.getServer(), config.getUsername(), config.getPassword() );
		
		Tasks tasks = oc.tasks( 1 );
		
		for( Task task : tasks.getTask() ) {
			
			if( task.getTask_id() > 62 ) continue;
			oc.taskTag( task.getTask_id(), "basic" );
		}
	}
	
}
