package org.openml.learningcurves;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openml.apiconnector.algorithms.Conversion;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class CheckDataset {
	// task_id, setup_id, (omitted: repeat), fold, sample, function
	Map<Integer, Map<Integer, Map<Integer, Map<Integer, Set<String>>>>> memory = new HashMap<>();

	private final int ATTIDX_TASKID;
	private final int ATTIDX_SETUPID;
	private final int ATTIDX_FOLD;
	private final int ATTIDX_SAMPLE;
	private final int ATTIDX_FUNCTION;
	
	// global. same for each task
	private final Set<Integer> REQUIRED_SETUPS;
	private final Set<Integer> REQUIRED_FOLDNRS;
	private final Set<String> REQUIRED_FUNCTIONS;
	// changes per task
	private Set<Integer> REQUIRED_SAMPLENRS;

	public static void main(String[] args) throws IOException {
		new CheckDataset("data/meta_curves.csv");
	}

	public CheckDataset(String filename) throws IOException {
		int exampleTaskId = -1;
		int exampleSetupId = -1;
		
		Conversion.log("OK","Init","Start Dataset Check");
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(filename));
		Instances data = loader.getDataSet();

		ATTIDX_TASKID = data.attribute("task_id").index();
		ATTIDX_SETUPID = data.attribute("setup_id").index();
		ATTIDX_FOLD = data.attribute("fold").index();
		ATTIDX_SAMPLE = data.attribute("sample").index();
		ATTIDX_FUNCTION = data.attribute("function").index();
		Conversion.log("OK","Load","Loaded dataset");

		for (int i = 0; i < data.numInstances(); ++i) {
			Instance current = data.get(i);
			int task_id = (int) current.value(ATTIDX_TASKID);
			int setup_id = (int) current.value(ATTIDX_SETUPID);
			int fold_nr = (int) current.value(ATTIDX_FOLD);
			int sample_nr = (int) current.value(ATTIDX_SAMPLE);
			String function = data.attribute(ATTIDX_FUNCTION).value(
					(int) current.value(ATTIDX_FUNCTION));
			exampleTaskId = task_id;
			exampleSetupId = setup_id;

			if (memory.containsKey(task_id) == false) {
				memory.put(
						task_id,
						new HashMap<Integer, Map<Integer, Map<Integer, Set<String>>>>());
			}

			if (memory.get(task_id).containsKey(setup_id) == false) {
				memory.get(task_id).put(
						setup_id,
						new HashMap<Integer, Map<Integer, Set<String>>>());
			}

			if (memory.get(task_id).get(setup_id).containsKey(fold_nr) == false) {
				memory.get(task_id).get(setup_id).put(
						fold_nr, 
						new HashMap<Integer, Set<String>>());
			}

			if (memory.get(task_id).get(setup_id).get(fold_nr).containsKey(sample_nr) == false) {
				memory.get(task_id).get(setup_id).get(fold_nr).put(
						sample_nr, 
						new HashSet<String>());
			}
			memory.get(task_id).get(setup_id).get(fold_nr).get(sample_nr).add( function );
			
			
		}
		REQUIRED_SETUPS = memory.get(exampleTaskId).keySet();
		REQUIRED_FOLDNRS = memory.get(exampleTaskId).get(exampleSetupId).keySet();
		REQUIRED_FUNCTIONS = memory.get(exampleTaskId).get(exampleSetupId).get(0).get(0);
		Conversion.log("OK","Loaded","Loaded all instances");
		
		int setup_errors = 0;
		int fold_errors = 0;
		int sample_errors = 0;
		int function_errors = 0;
		
		for(Integer task_id : memory.keySet()) {
			REQUIRED_SAMPLENRS = memory.get(task_id).get(exampleSetupId).get(0).keySet();
			Set<Integer> current_setups = memory.get(task_id).keySet();
			
			// check if setup set is consistent
			if(compareSets(REQUIRED_SETUPS,current_setups,task_id,"Task","Setup") == false) {
				setup_errors += 1;
				continue;
			}
			
			for(Integer setup_id : memory.get(task_id).keySet()) {
				Set<Integer> current_folds = memory.get(task_id).get(setup_id).keySet();
				
				if(compareSets(REQUIRED_FOLDNRS,current_folds,setup_id,"Setup","Fold") == false) {
					fold_errors += 1;
					continue;
				}
				
				for(Integer fold_nr : memory.get(task_id).get(setup_id).keySet()) {
					Set<Integer> current_samples = memory.get(task_id).get(setup_id).get(fold_nr).keySet();
					
					if(compareSets(REQUIRED_SAMPLENRS,current_samples,fold_nr,"Fold","Sample") == false) {
						sample_errors += 1;
						continue;
					}
					
					for(Integer sample_nr : memory.get(task_id).get(setup_id).get(fold_nr).keySet()) {
						Set<String> current_functions = memory.get(task_id).get(setup_id).get(fold_nr).get(sample_nr);
						
						if(compareSets(REQUIRED_FUNCTIONS,current_functions,sample_nr,"Sample","Function") == false) {
							function_errors += 1;
							continue;
						}
					}
				}
			}
		}
		
		System.out.println("Setup errors: " + setup_errors);
		System.out.println("Fold errors: " + fold_errors);
		System.out.println("Sample errors: " + sample_errors);
		System.out.println("Function errors: " + function_errors);
	}
	
	private static <T> boolean compareSets( Set<T> required, Set<T> obtained, Integer entityId, String entity, String collecting ) {
		if( required.equals(obtained) ) {
			return true;
		} else {
			System.err.print("Missing "+collecting+" in "+entity+" " + entityId + ": ");
			
			Set<T> missing = new HashSet<T>(required);
			missing.removeAll(obtained);
			if( missing.size() > 0 ) {
				System.err.println(missing);
			} else {
				System.err.println("Required "+collecting+" Set not complete. ");
			}
			return false;
		}
	}
}
