package org.openml.weka.algorithm;

import org.openml.apiconnector.settings.Config;

public class WekaConfig extends Config {
	
	private static final long serialVersionUID = 5388614163709096253L;

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
	 * @return Whether to build a model over full dataset in runs (takes time)
	 */
	public boolean getSkipRunPerformedTest() {
		if (get("skip_run_performed_test") == null) {
			return false; // default value
		}
		if (get("skip_run_performed_test").equals("true")) {
			return true;
		}
		return false;
	}
}
