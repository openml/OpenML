package org.openml.cortana;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.Tasks;
import org.openml.apiconnector.xml.Tasks.Task;

public class ScheduleMeta {
	
	private static final Config config = new Config();
	private static OpenmlConnector connector = new OpenmlConnector(config.getApiKey());
	private static final String experiment_directory = System.getProperty("user.home") + "/data/SD";
	private static final String cortanaJarLocation = "lib/cortana.3103.jar";
	private static Tasks tasks;
	private static List<List<String>> parameters;
	private static String[] param_names = {"search_depth", "search_strategy_width", "numeric_operators", "numeric_strategy", "nr_bins", "time_limit"};
	private static final String prefix = "\"overall_ranking_loss\":\"0.0\",\"post_processing_count\":\"20\",\"beta\":\"1.0\",\"minimum_coverage\":\"2\",\"nr_threads\":\"1\",\"search_strategy\":\"beam\",\"alpha\":\"0.5\",\"beam_seed\":\"\",\"use_nominal_sets\":\"false\",\"maximum_coverage_fraction\":\"1.0\",\"post_processing_do_autorun\":\"true\",\"maximum_subgroups\":\"100\"";
	
	public static void run(List<String> values) throws Exception {
		String setup_json = "";
		String directory_suffix = "";
		for (int i = 0; i < values.size(); ++i) {
			setup_json += ",\"" + param_names[i] + "\":\"" + values.get(i) + "\"";
			directory_suffix += "___" + param_names[i] + "__" + values.get(i);
		}
		
		setup_json = "{" + prefix + setup_json + "}";
		directory_suffix = directory_suffix.substring(3).replaceAll("[^A-Za-z0-9_-]", ""); // remove leading underscores
		
		for (Task t : tasks.task) {
			String current_directory = experiment_directory + "/" + t.getTask_id() + "/" + directory_suffix;
			File directory = new File(current_directory);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			File runFile = new File(current_directory + "/run.xml");
			
			if (runFile.exists()) {
				continue;
			}
			
			File lockFile = new File(experiment_directory + ".lock.txt");
			lockFile.createNewFile();
			
			RandomAccessFile raf = new RandomAccessFile(lockFile, "rw");
		    FileChannel fileChannel = raf.getChannel();
		    FileLock lock = null;
			try {
				lock = fileChannel.lock();
				
				CLI.process(connector, t.getTask_id(), cortanaJarLocation, directory, setup_json, false, false);
				
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("couldn't get lock");
				// Do nothing, next experiment
			} finally {
				if (lock != null) {
					try {
						lock.release();
					} catch (IOException e) {
						// Oops, this seems bad ..
					}
				}
				raf.close();
			}
		}
		
		System.out.println(setup_json);
	}
	
	public static void add(int param_idx, List<String> values) throws Exception {
		if (param_idx >= parameters.size()) {
			run(values);
		} else {
			boolean deepened = false;
			for (int i = 0; i < parameters.get(param_idx).size(); ++i) {
				if (!param_names[param_idx].equals("nr_bins") || values.get(3).contains("bins")) {
					values.add(parameters.get(param_idx).get(i));
					add(param_idx + 1, values);
					values.remove(values.size()-1);
					deepened = true;
				}
			}
			// for non-bin based strategies
			if (deepened == false) {
				add(param_idx + 1, values);
			}
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		tasks = connector.taskList("study_17");
		
		String[] ref_depth = {"4"};
		String[] search_width = {"1024"};
		String[] numeric_operators = {"<html>&#8804;, &#8805;, =<\\/html>", "<html>&#8804;, &#8805;<\\/html>", "="};
		String[] num_bins = {"1", "2", "4", "8", "16", "32", "64", "128"};
		String[] numeric_strat = {"best-bins", "bins", "all", "best"};
		String[] time_limit = {"60"};
		
		parameters = new ArrayList<List<String>>();
		parameters.add(Arrays.asList(ref_depth));
		parameters.add(Arrays.asList(search_width));
		parameters.add(Arrays.asList(numeric_operators));
		parameters.add(Arrays.asList(numeric_strat));
		parameters.add(Arrays.asList(num_bins));
		parameters.add(Arrays.asList(time_limit));
		
		add(0, new ArrayList<>());
		
	}
}
