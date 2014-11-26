package org.openml.learningcurves.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openml.apiconnector.algorithms.Conversion;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class DataLoader {
	
	// raw data hierarchy: task_id, setup_id, repeat, fold, sample
	Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<String, Double>>>>>> memory = new HashMap<>();
	
	// task oriented data: task_id, setup_id, sample, Evaluation
	Map<Integer, Map<Integer, Map<Integer, Evaluation>>> task_oriented = null;
	// setup oriented data: setup_id, task_id, sample, Evaluation
	Map<Integer, Map<Integer, Map<Integer, Evaluation>>> setup_oriented = null;
	
	private final int ATTIDX_TASKID;
	private final int ATTIDX_SETUPID;
	private final int ATTIDX_REPEAT;
	private final int ATTIDX_FOLD;
	private final int ATTIDX_SAMPLE;
	private final int ATTIDX_FUNCTION;
	private final int ATTIDX_VALUE;

	public DataLoader(String filename) throws IOException {
		Conversion.log("OK", "DataLoader", "Start Dataset Check");
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(filename));
		Instances data = loader.getDataSet();

		ATTIDX_TASKID = data.attribute("task_id").index();
		ATTIDX_SETUPID = data.attribute("setup_id").index();
		ATTIDX_REPEAT = data.attribute("repeat").index();
		ATTIDX_FOLD = data.attribute("fold").index();
		ATTIDX_SAMPLE = data.attribute("sample").index();
		ATTIDX_FUNCTION = data.attribute("function").index();
		ATTIDX_VALUE = data.attribute("value").index();
		Conversion.log("OK", "DataLoader", "Loaded dataset");

		for (int i = 0; i < data.numInstances(); ++i) {
			Instance current = data.get(i);
			int task_id = (int) current.value(ATTIDX_TASKID);
			int setup_id = (int) current.value(ATTIDX_SETUPID);
			int repeat_nr = (int) current.value(ATTIDX_REPEAT);
			int fold_nr = (int) current.value(ATTIDX_FOLD);
			int sample_nr = (int) current.value(ATTIDX_SAMPLE);
			String function = data.attribute(ATTIDX_FUNCTION).value(
					(int) current.value(ATTIDX_FUNCTION));
			Double value = current.value(ATTIDX_VALUE);
			
			if (memory.containsKey(task_id) == false) {
				memory.put(
						task_id,
						new HashMap<Integer,Map<Integer,Map<Integer,Map<Integer,Map<String,Double>>>>>());
			}

			if (memory.get(task_id).containsKey(setup_id) == false) {
				memory.get(task_id).put(
						setup_id,
						new HashMap<Integer,Map<Integer,Map<Integer,Map<String,Double>>>>());
			}

			if (memory.get(task_id).get(setup_id).containsKey(repeat_nr) == false) {
				memory.get(task_id).get(setup_id).put(
						repeat_nr, 
						new HashMap<Integer,Map<Integer,Map<String,Double>>>());
			}

			if (memory.get(task_id).get(setup_id).get(repeat_nr).containsKey(fold_nr) == false) {
				memory.get(task_id).get(setup_id).get(repeat_nr).put(
						fold_nr, 
						new HashMap<Integer,Map<String,Double>>());
			}
			if (memory.get(task_id).get(setup_id).get(repeat_nr).get(fold_nr).containsKey(sample_nr) == false) {
				memory.get(task_id).get(setup_id).get(repeat_nr).get(fold_nr).put(
						sample_nr, 
						new HashMap<String,Double>());
			}
			if (memory.get(task_id).get(setup_id).get(repeat_nr).get(fold_nr).get(sample_nr).containsKey(function) == false) {
				memory.get(task_id).get(setup_id).get(repeat_nr).get(fold_nr).get(sample_nr).put(
						function, 
						value);
			}
		}
	}
	
	public Map<Integer, Map<Integer, Map<Integer, Evaluation>>> getTaskOriented() {
		if( task_oriented == null ) {
			task_oriented = createTaskOriented( memory );
		}
		return task_oriented;
	}
	
	public Map<Integer, Map<Integer, Map<Integer, Evaluation>>> getSetupOriented() {
		if( setup_oriented == null ) {
			setup_oriented = createSetupOriented( memory );
		}
		return setup_oriented;
	}
	
	private static Map<Integer, Map<Integer, Map<Integer, Evaluation>>> createTaskOriented( Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<String, Double>>>>>> memory ) {
		Conversion.log("OK", "DataLoader", "Converting into task oriented set");
		Map<Integer, Map<Integer, Map<Integer, Evaluation>>> tmpTaskOriented = new HashMap<>();
		
		for( Integer task_id : memory.keySet() ) {
			Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<String, Double>>>>> currentTask = memory.get( task_id );
			if( tmpTaskOriented.containsKey(task_id) == false ) {
				tmpTaskOriented.put(task_id, new HashMap<Integer, Map<Integer, Evaluation>>());
			}
			
			for( Integer setup_id : currentTask.keySet() ) {
				Map<Integer, Map<Integer, Map<Integer, Map<String, Double>>>> currentSetup = currentTask.get( setup_id );
				if( tmpTaskOriented.get(task_id).containsKey(setup_id) == false ) {
					tmpTaskOriented.get(task_id).put( setup_id, new HashMap<Integer, Evaluation>() );
				}
				
				for( Integer repeat_nr : currentSetup.keySet() ) {
					Map<Integer, Map<Integer, Map<String, Double>>> currentRepeat = currentSetup.get( repeat_nr );
					
					for( Integer fold_nr : currentRepeat.keySet() ) {
						Map<Integer, Map<String, Double>> currentFold = currentRepeat.get( fold_nr );
						
						for( Integer sample_nr : currentFold.keySet() ) {
							Map<String, Double> currentSample = currentFold.get( sample_nr );
							if( tmpTaskOriented.get(task_id).get(setup_id).containsKey(sample_nr) == false ) {
								tmpTaskOriented.get(task_id).get(setup_id).put( sample_nr, new Evaluation() );
							}
							
							Evaluation evaluation = new Evaluation(
								currentSample.get("predictive_accuracy"), 
								currentSample.get("area_under_roc_curve"), 
								currentSample.get("build_cpu_time"));
							tmpTaskOriented.get(task_id).get(setup_id).get(sample_nr).add( evaluation );
						}
					}
				}
			}
		}
		Conversion.log("OK", "DataLoader", "Done converting into task oriented set");
		return tmpTaskOriented;
	}
	
	private Instances getWinnerPerSample() {
		Map<Integer, Map<Integer, Map<Integer, Evaluation>>> task_oriented = getTas
	}
	
	private static Map<Integer, Map<Integer, Map<Integer, Evaluation>>> createSetupOriented( Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<String, Double>>>>>> memory ) {
		Conversion.log("OK", "DataLoader", "Converting into setup oriented set");
		Map<Integer, Map<Integer, Map<Integer, Evaluation>>> tmpSetupOriented = new HashMap<>();
		
		for( Integer task_id : memory.keySet() ) {
			Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<String, Double>>>>> currentTask = memory.get( task_id );
			
			for( Integer setup_id : currentTask.keySet() ) {
				Map<Integer, Map<Integer, Map<Integer, Map<String, Double>>>> currentSetup = currentTask.get( setup_id );
				if( tmpSetupOriented.containsKey(setup_id) == false ) {
					tmpSetupOriented.put(setup_id, new HashMap<Integer, Map<Integer, Evaluation>>());
				}
				
				if( tmpSetupOriented.get(setup_id).containsKey(task_id) == false ) {
					tmpSetupOriented.get(setup_id).put( task_id, new HashMap<Integer, Evaluation>() );
				}
				
				for( Integer repeat_nr : currentSetup.keySet() ) {
					Map<Integer, Map<Integer, Map<String, Double>>> currentRepeat = currentSetup.get( repeat_nr );
					
					for( Integer fold_nr : currentRepeat.keySet() ) {
						Map<Integer, Map<String, Double>> currentFold = currentRepeat.get( fold_nr );
						
						for( Integer sample_nr : currentFold.keySet() ) {
							Map<String, Double> currentSample = currentFold.get( sample_nr );
							if( tmpSetupOriented.get(setup_id).get(task_id).containsKey(sample_nr) == false ) {
								tmpSetupOriented.get(setup_id).get(task_id).put( sample_nr, new Evaluation() );
							}
							
							Evaluation evaluation = new Evaluation(
								currentSample.get("predictive_accuracy"), 
								currentSample.get("area_under_roc_curve"), 
								currentSample.get("build_cpu_time"));
							tmpSetupOriented.get(setup_id).get(task_id).get(sample_nr).add( evaluation );
						}
					}
				}
			}
		}
		Conversion.log("OK", "DataLoader", "Done converting into setup oriented set");
		return tmpSetupOriented;
	}
}
