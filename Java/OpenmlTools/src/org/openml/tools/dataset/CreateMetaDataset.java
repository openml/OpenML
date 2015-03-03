package org.openml.tools.dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.DataQuality.Quality;
import org.openml.apiconnector.xml.TaskEvaluations;
import org.openml.apiconnector.xml.TaskEvaluations.Evaluation;
import org.openml.tools.algorithms.InstancesHelper;

import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.RemoveUnusedClassValues;
import weka.filters.unsupervised.attribute.RemoveUseless;

public class CreateMetaDataset {
	private static Config config = new Config();
	private static OpenmlConnector apiconnector;
	
	private Map<String, MetaDataStreamInstance> instances;
	private Map<String, Integer> allQualities = new HashMap<String, Integer>();
	private Map<String, Integer> allClassifiers = new HashMap<String, Integer>();
	private Map<Integer, String> classifierIdToName = new HashMap<Integer, String>();
	
	Integer[] all_classifiers = { 78, 79, 80, 81, 98, 82, 83, 84, 86, 99, 101, 103, 108 };
	Integer[] base_classifiers = { 78, 79, 80, 81, 98,  };
	Integer[] meta_classifiers = { 82, 83, 84, 86 };
	Integer[] weka_classifiers = { 99, 101, 103, 108 };
	
	public static void main(String[] args) throws Exception {
		Integer[] task_ids = { 120, 121, 122, 123, 124, 125, 126, 127, 128,
				129, 158, 159, 160, 163, 164, 165, 166, 167, 169, 170, 171,
				172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183,
				184, 185, 186, 188, 189, 190, 191, 192, 193, 194, 195, 196,
				197, 198, 199, 200, 2056, 2126, 2127, 2128, 2129, 2130, 2131,
				2132, 2133, 2134, 2150, 2151, 2154, 2155, 2156, 2157, 2159,
				2160, 2161, 2162, 2163, 2164, 2165, 2166, 2167, 2244, 2268,
				2269 };
		
		if( config.getServer() != null ) {
			apiconnector = new OpenmlConnector( config.getServer() );
		} else {
			apiconnector = new OpenmlConnector();
		}
		
		new CreateMetaDataset( task_ids, "meta_all2", 1000 );
	}
	
	public CreateMetaDataset( Integer[] task_ids, String name, int interval_size ) throws Exception {
		// download all task evaluations...
		instances = getAllStreamInstances( task_ids, interval_size );
		
		// now add the data qualities: (slow process)
		int counter = 0;
		List<String> toDelete = new ArrayList<String>();
		for( String key : instances.keySet() ) {
			Conversion.log("OK", "Create MetaDatastream", "Downloading data qualities for key: " + key + "("+(++counter)+"/"+instances.size()+")" );
			MetaDataStreamInstance instance = instances.get( key );
			try {
				DataQuality dq = apiconnector.dataQualities(instance.getDid(), 0, interval_size, interval_size );
				for( Quality quality : dq.getQualities() ) {
					if( allQualities.containsKey( quality.getName() ) == true ) {
						allQualities.put( quality.getName(), allQualities.get( quality.getName() ) + 1 );
					} else {
						allQualities.put( quality.getName(), 1 );
					}
					
					instance.addDataQuality( quality.getName(), Double.parseDouble( quality.getValue() ) );
				}
			} catch( Exception e ) { // corrupt data
				toDelete.add( key );
			}
		}
		
		for( String key : toDelete ) {
			Conversion.log( "WARNING","Generate Meta DataStream", "Removing corrupt instance: " + instances.get(key) );
			instances.remove( key );
		}
		Conversion.log( "WARNING","Generate Meta DataStream", "Removed a number of corrupt instances: " + toDelete.size() );
		
		createDataset( "meta_all", implementationIdsToImplementationList( all_classifiers ) );
		createDataset( "meta_base", implementationIdsToImplementationList( base_classifiers ) );
		createDataset( "meta_meta", implementationIdsToImplementationList( meta_classifiers ) );
		createDataset( "meta_weka", implementationIdsToImplementationList( weka_classifiers ) );
	}
	
	private void createDataset( String name, List<String> classifierValues ) throws Exception {
		Instances dataset = createInstanceHeader( name, classifierValues, instances.size() );
		
		for( String key : instances.keySet() ) {
			MetaDataStreamInstance instance = instances.get( key );
			dataset.add( instance.toInstance( dataset ) );
		}
		
		// remove attributes that are unused
		Conversion.log("OK", "Create MetaDatastream", "Start applying filters... " );
		dataset = InstancesHelper.applyFilter( dataset, new RemoveUseless(), "-M 100.0 " ); 
		dataset = InstancesHelper.applyFilter( dataset, new RemoveUnusedClassValues(), "-T 1" );
		dataset.setRelationName( name );

		Conversion.log("OK", "Create MetaDatastream", "Start writing to file: " + name );
		InstancesHelper.toFile( dataset, name );
		Conversion.log("OK", "Create MetaDatastream", "Done." );
	}
	
	private Map<String, MetaDataStreamInstance> getAllStreamInstances( Integer[] task_ids, int interval_size ) throws Exception {
		Map<String, MetaDataStreamInstance> instances = new HashMap<String, MetaDataStreamInstance>(); // indexing with String is soooo wrong. 
		for( Integer task_id : task_ids ) {
			Conversion.log("OK", "Create MetaDatastream", "Downloading Task: " + task_id );
			
			Conversion.log("OK", "Create MetaDatastream", "Downloading Task Evaluations: " + task_id );
			
			TaskEvaluations te = apiconnector.taskEvaluations( task_id );
			if( te.getEvaluation() != null ) {
				for( Evaluation evaluation : te.getEvaluation() ) {
					
					try {
						String key = task_id + "_" + evaluation.getInterval_start();
						Double predictive_accuracy = Double.parseDouble( evaluation.getMeasure("predictive_accuracy" ) );
						if( instances.containsKey( key ) == false ) {
							instances.put(key, new MetaDataStreamInstance(task_id, te.getInput_data(), evaluation.getInterval_start(), evaluation.getInterval_end() ) );
						}
						if( allClassifiers.containsKey( evaluation.getImplementation() ) == true ) {
							allClassifiers.put( evaluation.getImplementation(), allClassifiers.get( evaluation.getImplementation() ) + 1 );
						} else {
							allClassifiers.put( evaluation.getImplementation(), 1 );
						}
						
						classifierIdToName.put( evaluation.getImplementation_id(), evaluation.getImplementation() );
						instances.get( key ).addClassifierScore( evaluation.getImplementation(), predictive_accuracy );
					} catch( Exception e ) { 
						Conversion.log( "WARNING","Generate Meta DataStream", e.getMessage() );
					}
				}
			}
		}
		return instances;
	}
	
	private Instances createInstanceHeader( String relationName, List<String> classifiersInClass, int numInstances ) {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		
		attributes.add( new Attribute( MetaDataStreamInstance.ATT_TASK_ID_NAME) );
		
		List<String> classValues = new ArrayList<String>();
		if( classifiersInClass != null ) {
			classValues = classifiersInClass;
		} else {
			for( String classifier : allClassifiers.keySet() ) {
				if( allClassifiers.get( classifier ) < (numInstances * 0.9) ) {
					Conversion.log("WARNING","Generate Meta DataStream","Dropping classifier since to few runs: " + classifier );
					continue;
				}
				attributes.add( new Attribute( MetaDataStreamInstance.ATT_CLASSIFIER_PREFIX + classifier ) );
				classValues.add( classifier );
			}
		}
		
		for( String quality : allQualities.keySet() ) {
			if( allQualities.get( quality ) < (numInstances * 0.75) ) {
				Conversion.log("WARNING","Generate Meta DataStream","Dropping quality since to few runs: " + quality );
				continue;
			}
			attributes.add( new Attribute( MetaDataStreamInstance.ATT_META_PREFIX + quality ) );
		}
		
		Attribute classAtt = new Attribute( MetaDataStreamInstance.ATT_CLASS_NAME, classValues );
		attributes.add( classAtt );
		Instances instances = new Instances( relationName, attributes, numInstances );
		instances.setClass( classAtt );
		
		return instances;
	}
	
	private List<String> implementationIdsToImplementationList( Integer[] ids ) {
		List<String> result = new ArrayList<String>();
		
		for( Integer id : ids ) {
			if( classifierIdToName.containsKey( id ) ) {
				result.add( classifierIdToName.get( id ) );
			}
		}
		
		return result;
	}
}
