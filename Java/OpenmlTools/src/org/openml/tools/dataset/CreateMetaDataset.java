package org.openml.tools.dataset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.DataQuality.Quality;
import org.openml.apiconnector.xml.TaskEvaluations;
import org.openml.apiconnector.xml.TaskEvaluations.Evaluation;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.RemoveUseless;

public class CreateMetaDataset {

	private static final int MISSING_RUNS_TRESHOLD = 4;
	private static final String FILENAME = "meta_lb_vs_all.arff";
	private static final String ONE_VS_ALL_VALUE = "Others";
	
	private final ApiConnector ac;
	private final Map<Integer, Map<Setup, Double>> tasks;
	private final Map<Setup, Map<Integer, Double>> runs;
	private final Map<Integer,Integer> taskidToDatasetid;
	private final List<Setup> setups;
	private final BufferedWriter outputfile;
	
	private final Integer[] illegal_datasets = { 116, 117, 121, 128, 148, 253, 256, 272 };
	
	public static void main(String[] args) throws Exception {
		Integer[] task_ids = { 120, 121, 122, 123, 124, 125, 126, 127, 128,
				129, 130, 131, 158, /*159, 160, 161, 162, 163, 164, 165, 166,
				167, 168, 169, 170, 171, 172,  174, 175, 176, 177, 178,
				179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190,
				191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 2056, 2126,
				2127, 2128, 2129, 2130, 2131, 2132, 2133, 2134, 2149, 2150,
				2151, 2152, 2153, 2154, 2155, 2156, 2157, 2158, 2159, 2160,
				 2162, 2163, 2164, 2165, 2166, 2167, 2168, 2244*/ };
		
		Integer[] base_classifiers = { 78, 79, 22, 80, 91, 101, 108, 103, 96, 97, 105, 99, 98, 95 };
		Integer[] meta_classifiers = { 83, 82, 92, 87, 94, 85, 86, 84, };
		Integer[] weka_classifiers = { 99, 101, 103, 105, 106, 108 };
		Integer[] lb_hoeffding = { 24 };
		Integer[]  all_classifiers = { };
		
		new CreateMetaDataset( task_ids, all_classifiers, false, FILENAME );
	}

	public CreateMetaDataset(Integer[] task_ids, Integer[] setup_ids, boolean oneVsAll, String filename ) throws Exception {
		outputfile = new BufferedWriter( new FileWriter( new File( filename ) ) );
		runs = new HashMap<Setup, Map<Integer,Double>>();
		tasks = new HashMap<Integer, Map<Setup,Double>>();
		taskidToDatasetid = new HashMap<Integer, Integer>();
		
		// default config initialization
		Config config = new Config();
		if( config.getServer() != null ) {
			ac = new ApiConnector( config.getServer() );
		} else {
			ac = new ApiConnector();
		}
		
		// Download all task evaluations and store the setups that were involved in all of those
		List<Integer> legal_task_ids = new ArrayList<Integer>();
		for( Integer task_id : task_ids ) {
			Conversion.log( "Ok", "Create MetaDataset", "Downloading Task: " + task_id );
			TaskEvaluations te = ac.openmlTaskEvaluations( task_id );
			
			int did = te.getInput_data();
			if( Arrays.asList( illegal_datasets ).contains( did ) ) {
				Conversion.log( "WARNING", "Create MetaDataset", "Skipping Task, illegal dataset: " + task_id + "(did "+did+")" );
				continue;
			} else {
				legal_task_ids.add( task_id );
			}
			
			tasks.put( task_id, new HashMap<Setup, Double>() );
			taskidToDatasetid.put( task_id, did );
			
			if( te.getEvaluation() != null ) {
				for( Evaluation evaluation : te.getEvaluation() ) {
					if( setup_ids.length == 0 || Arrays.asList( setup_ids ).contains( evaluation.getImplementation_id() ) || oneVsAll ) {
						Setup current = new Setup( evaluation.getSetup_id(), evaluation.getImplementation() );
						if( runs.containsKey( current ) == false ) {
							runs.put( current, new HashMap<Integer, Double>() ); 
						}
						try {
							String evaluation_score = evaluation.getMeasure("predictive_accuracy");
							Double score = Double.parseDouble( evaluation_score );
							runs.get( current ).put( task_id, score );
							tasks.get( task_id ).put( current, score );
						} catch( Exception e ) {
							Conversion.log( "Warning", "Create MetaDataset", e.getMessage() );
						}
					}
				}
				Conversion.log( "OK", "Create MetaDataset", "Found scores: " + tasks.get( task_id ).size() );
			} else {
				System.err.println( "No evaluation for task " + task_id );
			}
		}
		
		
		
		// download all sensible meta data
		String[] metaFeatures = ac.openmlDataQualityList().getQualities();
		
		// now get all implementations that have run on all tasks
		setups = allRunsDone( runs, legal_task_ids );
		ArrayList<String> classValues = new ArrayList<String>();
		for( Setup s : setups ) {
			if( setup_ids.length == 0 || Arrays.asList( setup_ids ).contains( s.getSetup_id() ) ) {
				classValues.add( s.getImplementation() );
			} // else ignore this value. 
		}
		if( oneVsAll ) { classValues.add( ONE_VS_ALL_VALUE ); }
		
		Conversion.log( "OK", "Create MetaDataset", "Found the following implementations: " + classValues );
		Attribute classAttribute = new Attribute( "class", classValues );
		
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for( String metaFeature : metaFeatures ) {
			attributes.add( new Attribute( metaFeature ) );
		}
		attributes.add( classAttribute ) ;
		
		Instances instances = new Instances( "meta_dataset", attributes, legal_task_ids.size() );
		instances.setClassIndex( instances.numAttributes() - 1 );
		
		for( Integer task_id : legal_task_ids ) {
			if( tasks.get( task_id ).size() > 0 ) {
				Instance current = new DenseInstance( instances.numAttributes() );
				current.setDataset(instances);
				String biggest = biggest( tasks.get( task_id) ).getImplementation();
				if( classValues.contains( biggest ) ) {
					current.setClassValue( biggest );
				} else if( oneVsAll ) {
					current.setClassValue( ONE_VS_ALL_VALUE );
				} else {
					throw new Exception("Trying to set an illegal Classvalue: Task " + task_id + ", value " + biggest );
				}
				Integer dataset_id = taskidToDatasetid.get( task_id );
	
				Conversion.log( "Ok", "Create MetaDataset", "Downloading Qualities for did: " + dataset_id );
				DataQuality dataqualities = ac.openmlDataQuality( dataset_id, 0, 1000 );
				for( Quality q : dataqualities.getQualities() ) {
					// TODO: refactor. we might want to work with nominal values??? 
					Attribute attribute = instances.attribute( q.getName() );
					if( attribute != null )
						current.setValue( attribute, Double.parseDouble( q.getValue() ) );
					else
						System.err.println("Could not find attribute: " + q.getName() );
				}
				
				instances.add( current );
			} else {
				Conversion.log( "Warning", "Create MetaDataset", "Did not find any evalutations for task: " + task_id );
			}
		}
		
		// remove attributes that are unused
		instances = applyFilter( instances, new RemoveUseless(), "-M 100.0 " ); 
		
		outputfile.write( instances.toString() );
		outputfile.close();
	}
	
	private Setup biggest( Map<Setup,Double> results ) {
		Entry<Setup, Double> max = null;
		for (Entry<Setup, Double> entry : results.entrySet()) {
		    if (max == null || max.getValue() < entry.getValue()) {
		        max = entry;
		    }
		}
		return max.getKey();
	}
	
	private List<Setup> allRunsDone( Map<Setup, Map<Integer,Double>> runs, List<Integer> task_ids ) {
		List<Setup> setups = new ArrayList<Setup>();
		for( Setup setup : runs.keySet() ) {
			Map<Integer, Double> runs_participated = runs.get( setup );
			ArrayList<Integer> missingRuns = new ArrayList<Integer>();
			for( Integer task_id : task_ids ) {
				if( runs_participated.containsKey( task_id ) == false ) {
					missingRuns.add( task_id );
				}
			}
			
			if( missingRuns.size() < MISSING_RUNS_TRESHOLD ) {
				Conversion.log( "OK", "Create MetaDataset", "Missing task for setup :" + setup + " - " + missingRuns );
				setups.add( setup );
			} else {
				Conversion.log( "Warning", "Create MetaDataset", "DROPPING setup because of too many missing runs:" + setup + " (Missing runs: "+missingRuns+")" );
			}
		}
		return setups;
	}
	
	// TODO: should go into a real helper class. 
	public static Instances applyFilter( Instances dataset, Filter filter, String options ) throws Exception {
		((OptionHandler) filter).setOptions( Utils.splitOptions( options ) );
		filter.setInputFormat(dataset);
		return Filter.useFilter(dataset, filter);
	}
	
	private class Setup {
		private int setup_id;
		private String implementation;
		
		// creates a specific setup
		public Setup( int setup_id, String implementation ) {
			this.setup_id = setup_id;
			this.implementation = implementation;
		}

		public int getSetup_id() {
			return setup_id;
		}
		
		public String getImplementation() {
			return implementation;
		}
		
		@Override 
		public boolean equals( Object other ) {
			if( other instanceof Setup ) {
				return ((Setup) other).getSetup_id() == setup_id;
			} else return false;
		}
		
		@Override
		public int hashCode() {
			return setup_id;
		}
		
		@Override
		public String toString() {
			return setup_id + " (" + implementation + ")";
		}
	}
}
