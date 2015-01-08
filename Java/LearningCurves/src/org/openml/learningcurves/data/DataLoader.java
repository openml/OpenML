package org.openml.learningcurves.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openml.apiconnector.algorithms.Conversion;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.CSVLoader;

public class DataLoader {
	
	// raw data hierarchy: task_id, setup_id, repeat, fold, sample
	private final Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<Integer, Map<String, Double>>>>>> memory = new HashMap<>();
	
	// task oriented data: task_id, setup_id, sample, Evaluation
	private Map<Integer, Map<Integer, Map<Integer, Evaluation>>> task_oriented = null;
	// setup oriented data: setup_id, task_id, sample, Evaluation
	private Map<Integer, Map<Integer, Map<Integer, Evaluation>>> setup_oriented = null;
	
	private Map<Integer, Map<Integer, List<Double>>> task_setup_fold_results = null;
	
	// task size
	private final Map<Integer, Integer> taskSamples;
	
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
		
		taskSamples = new HashMap<>();
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
			
			for( Integer tmp_task_id : memory.keySet() ) {
				taskSamples.put( tmp_task_id, memory.get(tmp_task_id).get(memory.get(tmp_task_id).keySet().iterator().next()).size() );
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
	
	public Map<Integer, Map<Integer, List<Double>>> getTaskSetupFoldResults() {
		if( task_setup_fold_results == null ) {
			task_setup_fold_results = createTaskSetupFoldResults();
		}
		return task_setup_fold_results;
	}
	
	public int taskSamples( int task_id ) {
		return taskSamples.get(task_id);
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
	
	public Map<Integer, Map<Integer, List<Double>>> createTaskSetupFoldResults() {
		Conversion.log("OK", "DataLoader", "Creating Task Setup Fold Results Map");
		Map<Integer, Map<Integer, List<Double>>> taskSetupFoldResults = new HashMap<>();
		for( Integer task_id : memory.keySet() ) {
			taskSetupFoldResults.put(task_id, new HashMap<Integer, List<Double>>());
			
			for( Integer setup_id : memory.get(task_id).keySet() ) {
				taskSetupFoldResults.get(task_id).put(setup_id, new ArrayList<Double>() );
				
				for( Integer repeat : memory.get(task_id).get(setup_id).keySet() ) {
					
					for( Integer fold : memory.get(task_id).get(setup_id).get(repeat).keySet() ) {
						
						double currentValue = memory.get(task_id).get(setup_id).get(repeat).get(fold).get(taskSamples(task_id)).get("predictive_accuracy");
						
						taskSetupFoldResults.get(task_id).get(setup_id).add(currentValue);
					}
				}
			}
		}
		Conversion.log("OK", "DataLoader", "Done creating Task Setup Fold Results Map");
		
		return taskSetupFoldResults;
	}
	
	public Instances getWinnerPerSample() {
		Map<Integer, Map<Integer, Map<Integer, Evaluation>>> task_oriented = getTaskOriented();
		Map<Integer, Map<Integer, Map<Integer, Evaluation>>> setup_oriented = getSetupOriented();

		Set<Integer> setup_idxs = setup_oriented.keySet();
		Set<Integer> samples = new HashSet<>();
		for( Integer task_id : task_oriented.keySet() ) {
			samples.addAll( task_oriented.get(task_id).get( setup_oriented.keySet().iterator().next() ).keySet() );
		}
		
		List<String> setups = new ArrayList<>();
		for( Integer setup_idx : setup_idxs ) { setups.add( setup_idx + "" ); }
		ArrayList<Attribute> attributes = new ArrayList<>();
		attributes.add( new Attribute("task_id") );
		for( Integer sample : samples ) {
			attributes.add( new Attribute("sample_" + sample, setups ) );
		}
		
		Instances dataset = new Instances("sample-info", attributes, task_oriented.size() );
		for( Integer task_id : task_oriented.keySet() ) {
			Integer[] bestSampleIdx = null;
			Double[] bestSampleScore = null;
			
			for( Integer setup_id : task_oriented.get(task_id).keySet() ) {
				if( bestSampleIdx == null ) { 
					bestSampleIdx = new Integer[task_oriented.get(task_id).get(setup_id).size()];
					bestSampleScore = new Double[task_oriented.get(task_id).get(setup_id).size()];
					
					for( Integer sample : task_oriented.get(task_id).get(setup_id).keySet() ) {
						bestSampleIdx[sample] = setup_id;
						bestSampleScore[sample] = task_oriented.get(task_id).get(setup_id).get(sample).getAccuracy();
					}
				} else {
					for( Integer sample : task_oriented.get(task_id).get(setup_id).keySet() ) {
						Double value = task_oriented.get(task_id).get(setup_id).get(sample).getAccuracy();
						if( value > bestSampleScore[sample] ) {
							bestSampleIdx[sample] = setup_id;
							bestSampleScore[sample] = value;
						}
					}
				}
			}
			
			Instance task = new DenseInstance(dataset.numAttributes());
			task.setValue( 0, task_id );
			for( int i = 1; i < dataset.numAttributes(); ++i ) {
				if( i <= bestSampleScore.length ) {
					task.setValue( i, dataset.attribute( i ).indexOfValue( bestSampleIdx[i-1] + "" ) );
				} else {
					task.setValue( i, Utils.missingValue() );
				}
			}
			dataset.add(task);
		}
		return dataset;
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
