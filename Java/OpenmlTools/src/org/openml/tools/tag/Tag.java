package org.openml.tools.tag;

import java.util.Arrays;

import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;

public class Tag {

	// CONFIG THIS:
	private final static String TAG = "study_16";
	private final static TagEntities type = TagEntities.run;
	private final static boolean untag = false;
	private static Integer[] ids = null; //{3542,3551,3593,3567,3550,3608};
	
	private static OpenmlConnector openmlConnector;
	
	public static void main(String[] args) throws Exception {
		Config c = new Config();
		openmlConnector = new OpenmlConnector(c.getServer(), c.getApiKey());
		openmlConnector.setVerboseLevel(1);
		
		System.out.println(Arrays.toString(ids));
		for(int id : ids) {
			try {
				switch(type) {
					case data:
						if (untag) {
							openmlConnector.dataUntag(id, TAG );
						} else {
							openmlConnector.dataTag(id, TAG );
						}
						break;
					case task:
						if (untag) {
							openmlConnector.taskUntag(id, TAG );
						} else {
							openmlConnector.taskTag(id, TAG );
						}
						break;
					case flow:
						if (untag) {
							openmlConnector.flowUntag(id, TAG );
						} else {
							openmlConnector.flowTag(id, TAG );
						}
						break;
					case setup:
						if (untag) {
							openmlConnector.setupUntag(id, TAG );
						} else {
							openmlConnector.setupTag(id, TAG );
						}
						break;
					case run:
						if (untag) {
							openmlConnector.runUntag(id, TAG );
						} else {
							openmlConnector.runTag(id, TAG );
						}
						break;
					default:
						throw new Exception("Programming error. ");
				}
			} catch(Exception e) {
				System.err.println("Error at: " + id + ". " + e.getMessage());
			}
		}
		
	}
	
	public static String tagDatasetsFromTask() {
		String sql = "SELECT value FROM task_inputs i WHERE input = 'source_data' AND task_id IN (SELECT id FROM task_tag WHERE tag = '"+TAG+"')";
		return sql;
	}
	
	public static String tagRunFromTaskSetup() {
		String sql = "SELECT rid FROM run WHERE task_id IN (SELECT id FROM task_tag WHERE tag = '"+TAG+"') AND setup IN (SELECT id FROM setup_tag WHERE tag = '"+TAG+"')";
		return sql;
	}
	
}
