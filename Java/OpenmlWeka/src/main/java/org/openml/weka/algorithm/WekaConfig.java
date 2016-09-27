package org.openml.weka.algorithm;

import org.openml.apiconnector.settings.Config;

public class WekaConfig extends Config {
	
	private static final long serialVersionUID = 5388614163709096253L;

	public WekaConfig() {
		super();
	}
	
	public WekaConfig(String config) {
		super(config);
	}
	
	/**
	 * @return Whether to build a model over full dataset in runs (takes time)
	 */
	public boolean getModelFullDataset() {
		if (get("model_full_dataset") == null) {
			return true; // default value
		}
		if (get("model_full_dataset").equals("false")) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return Whether to benchmark Jvm before uploading results
	 */
	public boolean getSkipJvmBenchmark() {
		if (get("skip_jvm_benchmark") == null) {
			return false; // default value
		}
		if (get("skip_jvm_benchmark").equals("true")) {
			return true;
		}
		return false;
	}
	

	/**
	 * @return Whether to avoid duplicate runs
	 */
	public boolean getAvoidDuplicateRuns() {
		if (get("avoid_duplicate_runs") == null) {
			return true; // default value
		}
		if (get("avoid_duplicate_runs").equals("false")) {
			return false;
		}
		return true;
	}
	
	public String getJobRequestTaskTag() {
		return get("job_request_task_tag");
	}
	
	public String getJobRequestSetupTag() {
		return get("job_request_setup_tag");
	}
	
	public Integer getJobRequestSetupId() {
		if (get("job_request_setup_id") != null) {
			return Integer.parseInt(get("job_request_setup_id"));
		}
		return null;
	}
}
