package org.openml.tools.dataset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import weka.filters.unsupervised.attribute.RemoveUnusedClassValues;
import weka.filters.unsupervised.attribute.RemoveUseless;

public class CreateMetaDataset {

	private static final int MISSING_RUNS_TRESHOLD = 4;
	private static final String ONE_VS_ALL_VALUE = "Others";
	
	private final ApiConnector ac;
	private final Map<Integer, Map<Setup, Double>> tasks;
	private final Map<Setup, Map<Integer, Double>> runs;
	private final Map<Integer,Integer> taskidToDatasetid;
	private final List<Setup> setups;
	private final BufferedWriter outputfile;
	private final OfflineFeatures offlinefeatures = new OfflineFeatures();
	
	private final Integer[] illegal_datasets = { /*116, 117, 121, 128, 148, 253, 256, 272*/ };
	
	public static void main(String[] args) throws Exception {
		Integer[] task_ids = { 120, 121, 122, 123, 124, 125, 126, 127, 128,
				129, 158, 159, 160, 163, 164, 165, 166, 167, 169, 170, 171,
				172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183,
				184, 185, 186, 188, 189, 190, 191, 192, 193, 194, 195, 196,
				197, 198, 199, 200, 2056, 2126, 2127, 2128, 2129, 2130, 2131,
				2132, 2133, 2134, 2150, 2151, 2154, 2155, 2156, 2157, 2159,
				2160, 2161, 2162, 2163, 2164, 2165, 2166, 2167, 2244,  };

		Integer[] base_classifiers = { 78, 79, 22, 80, 91, 101, 108, 103, 96, 97, 105, 99, 98, 95 };
		Integer[] meta_classifiers = { 83, 82, 92, 87, 94, 85, 86, 84, };
//		Integer[] weka_classifiers = { 99, 101, 103, 105, 106, 108 };
		Integer[] lb_hoeffding = { 83 };
		Integer[]  all_classifiers = { };
		
		RunConfiguration[] configs = new RunConfiguration[1];
		//configs[0] = new RunConfiguration( base_classifiers, false, "meta_bc.arff" );
		//configs[1] = new RunConfiguration( meta_classifiers, false, "meta_mc.arff" );
		configs[0] = new RunConfiguration( lb_hoeffding, true, "meta_lbhoeffding.arff" );
		//configs[3] = new RunConfiguration( all_classifiers, false, "meta_all.arff" );
		
		for( RunConfiguration c : configs ) {
			new CreateMetaDataset( task_ids, c.ids, c.oneVsAll, c.filename );
		}
	}

	public CreateMetaDataset(Integer[] task_ids, Integer[] implementation_ids, boolean oneVsAll, String filename ) throws Exception {
		outputfile = new BufferedWriter( new FileWriter( new File( filename ) ) );
		runs = new HashMap<Setup, Map<Integer,Double>>(); // TODO: remove this hashmap
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
					if( implementation_ids.length == 0 || Arrays.asList( implementation_ids ).contains( evaluation.getImplementation_id() ) || oneVsAll ) {
						Setup current = new Setup( evaluation.getSetup_id(), evaluation.getImplementation_id(), evaluation.getImplementation() );
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
		List<String> classValues = new ArrayList<String>();
		
		// create the nominal values for the class attribute
		for( Setup s : setups ) {
			if( implementation_ids.length == 0 || Arrays.asList( implementation_ids ).contains( s.getImplementation_id() ) ) {
				classValues.add( s.getImplementation() );
			} else {
				//Conversion.log( "OK", "Create MetaDataset", "Implementation will not be added to class values: " +  s.getSetup_id() + " - " + s.getImplementation() );
			}
		}
		if( oneVsAll ) { classValues.add( ONE_VS_ALL_VALUE ); }
		Conversion.log( "OK", "Create MetaDataset", "Found the following implementations: " + classValues );
		
		// now remove all implementation scores of those not included in the comparisson from the task map
		if( oneVsAll == false ) {
			filterTaskResult(classValues, tasks); // alters the shallow copy
		}
		
		// add all the meta features attribute
		Attribute classAttribute = new Attribute( "class", classValues );
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for( String metaFeature : metaFeatures ) {
			attributes.add( new Attribute( metaFeature ) );
		}
		// add a special type attribute
		attributes.add( offlinefeatures.datasetType() );
		
		// lastly, add the class attribute (so that it is positioned last)
		attributes.add( classAttribute ) ;
		
		Instances instances = new Instances( "meta_dataset", attributes, legal_task_ids.size() );
		instances.setClassIndex( instances.numAttributes() - 1 );
		
		for( Integer task_id : legal_task_ids ) {
			if( tasks.get( task_id ).size() > 0 ) {
				Instance current = new DenseInstance( instances.numAttributes() );
				Integer dataset_id = taskidToDatasetid.get( task_id );
				current.setDataset(instances);
				String biggest = biggest( tasks.get( task_id) ).getImplementation();
				if( classValues.contains( biggest ) ) {
					current.setClassValue( biggest );
				} else if( oneVsAll ) {
					current.setClassValue( ONE_VS_ALL_VALUE );
				} else {
					throw new Exception("Trying to set an illegal Classvalue: Task " + task_id + ", value " + biggest );
				}
				
				// add temp off line feature
				current.setValue( offlinefeatures.datasetType(), offlinefeatures.getDatasetType( dataset_id ) );
	
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
		instances = applyFilter( instances, new RemoveUnusedClassValues(), "-T 1" );
		
		instances.setRelationName( filename.substring( 0, filename.indexOf( '.' ) ) );
		
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
	
	private static Map<Integer, Map<Setup,Double>> filterTaskResult( List<String> classValues, Map<Integer, Map<Setup,Double>> tasks ) {
		for( Integer task_id : tasks.keySet() ) {
			Set<Setup> all_setups = tasks.get( task_id ).keySet();
			List<Setup> toRemove = new ArrayList<Setup>();
			for( Setup s : all_setups ) {
				if( classValues.contains( s.implementation ) == false ) { toRemove.add( s ); }
			}
			for( Setup s : toRemove ) {
				tasks.get( task_id ).remove( s );
			}
		}
		return tasks;
	}
	
	// TODO: should go into a real helper class. 
	public static Instances applyFilter( Instances dataset, Filter filter, String options ) throws Exception {
		((OptionHandler) filter).setOptions( Utils.splitOptions( options ) );
		filter.setInputFormat(dataset);
		return Filter.useFilter(dataset, filter);
	}
	
	private class Setup {
		private final Integer setup_id;
		private final Integer implementation_id;
		private final String implementation;
		
		// creates a specific setup
		public Setup( Integer setup_id, Integer implementation_id, String implementation ) {
			this.setup_id = setup_id;
			this.implementation_id = implementation_id;
			this.implementation = implementation;
		}

		public int getSetup_id() {
			return setup_id;
		}
		
		public String getImplementation() {
			return implementation;
		}
		
		public Integer getImplementation_id() {
			return implementation_id;
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
	
	private static class RunConfiguration {
		private final Integer[] ids;
		private final boolean oneVsAll;
		private final String filename;
		
		public RunConfiguration( Integer[] ids, boolean oneVsAll, String filename ) {
			this.ids = ids;
			this.oneVsAll = oneVsAll;
			this.filename = filename;
		}
	}
}
