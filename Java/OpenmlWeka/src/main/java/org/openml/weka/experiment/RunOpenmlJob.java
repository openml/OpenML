package org.openml.weka.experiment;

import org.apache.commons.lang3.ArrayUtils;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.Job;

import weka.core.CommandlineRunnable;
import weka.core.Utils;
import weka.core.Version;

public class RunOpenmlJob implements CommandlineRunnable {

	public static void main(String[] args) throws Exception {
		RunOpenmlJob rj = new RunOpenmlJob();
		rj.run(rj, args);
	}

	public static void obtainTask(int ttid, Config config, OpenmlConnector apiconnector) {
		try {
			Job j = apiconnector.jobRequest("Weka_" + Version.VERSION, "" + ttid);
			
			Conversion.log("OK", "Obtain Task", "Task: " + j.getTask_id() + "; learner: " + j.getLearner());
			
			String[] classArgs = Utils.splitOptions(j.getLearner());
			String[] taskArgs = new String[3];
			taskArgs[0] = "-T";
			taskArgs[1] = "" + j.getTask_id();
			taskArgs[2] = "-C";

			TaskBasedExperiment.main(ArrayUtils.addAll(taskArgs, classArgs));
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
	}
	
	public static void executeTask(Integer task_id, String setup_string) throws Exception {
		String[] classArgs = Utils.splitOptions(setup_string);
		String[] taskArgs = new String[3];
		taskArgs[0] = "-T";
		taskArgs[1] = "" + task_id;
		taskArgs[2] = "-C";

		TaskBasedExperiment.main(ArrayUtils.addAll(taskArgs, classArgs));
	}
	
	@Override
	public void run(Object arg0, String[] args) throws IllegalArgumentException {
		int n;
		Integer ttid;
		
		String strN;
		String strTtid;
		String strTaskid;
		String setup_string;
		
		Config config = new Config();
		OpenmlConnector apiconnector;

		String username = config.getApiKey();
		String server = config.getServer();

		if (server != null) {
			apiconnector = new OpenmlConnector(server, username);
		} else {
			apiconnector = new OpenmlConnector(username);
		}

		try {
			strN = Utils.getOption('N', args);
			strTtid = Utils.getOption('T', args);
			strTaskid = Utils.getOption("task_id", args);
			setup_string = Utils.getOption("C", args);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		
		if (strTaskid.equals("")) {
			// obtain tasks from server
			n = (strN.equals("")) ? 1 : Integer.parseInt(strN);
			ttid = (strTtid.equals("")) ? 1 : Integer.parseInt(strTtid);
			
			for (int i = 0; i < n; ++i) {
				obtainTask(ttid, config, apiconnector);
			}
		} else {
			try {
				executeTask(Integer.parseInt(strTaskid), setup_string);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
