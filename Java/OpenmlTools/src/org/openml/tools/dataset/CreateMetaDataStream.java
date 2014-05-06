package org.openml.tools.dataset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.QueryUtils;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.DataQuality.Quality;
import org.openml.apiconnector.xml.TaskEvaluations;
import org.openml.apiconnector.xml.TaskEvaluations.Evaluation;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.RemoveUnusedClassValues;
import weka.filters.unsupervised.attribute.RemoveUseless;

public class CreateMetaDataStream {
	private static Config config = new Config();
	private static ApiConnector apiconnector;
	private static Boolean TEST_MODE = false;
	private static int INTERVALS_PER_DOWNLOAD = 100;
	
	private Map<String, Integer> allQualities = new HashMap<String, Integer>();
	private Map<String, Integer> allClassifiers = new HashMap<String, Integer>();
	
	
	public static void main(String[] args) throws Exception {
		Integer[] task_ids = 
			  { 120, 121, 122, 123, 124, 125, 126, 127, 128,
				129, 158, 159, 160, 163, 164, 165, 166, 167, 169, 170, 171,
				172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183,
				184, 185, 186, 188, 189, 190, 191, 192, 193, 194, 195, 196,
				197, 198, 199, 200, 2056, 2126, 2127, 2128, 2129, 2130, 2131,
				2132, 2133, 2134, 2150, 2151, 2154, 2155, 2156, 2157, 2159,
				2160, 2161, 2162, 2163, 2164, 2165, 2166, 2167 };
		
		if( config.getServer() != null ) {
			apiconnector = new ApiConnector( config.getServer() );
		} else {
			apiconnector = new ApiConnector();
		}
		
		new CreateMetaDataStream( task_ids, "meta_stream", 1000 );
	}
	
	public CreateMetaDataStream( Integer[] task_ids, String name, int interval_size ) throws Exception {
		Map<String, MetaDataStreamInstance> instances = getAllStreamInstances( task_ids, interval_size );
		
		// now add the data qualities: (slow process)
		int counter = 0;
		List<String> toDelete = new ArrayList<String>();
		for( String key : instances.keySet() ) {
			Conversion.log("OK", "Create MetaDatastream", "Downloading data qualities for key: " + key + "("+(++counter)+"/"+instances.size()+")" );
			MetaDataStreamInstance instance = instances.get( key );
			try {
				DataQuality dq = apiconnector.openmlDataQuality(instance.getDid(), instance.getInterval_start() - interval_size, instance.getInterval_end() - interval_size, interval_size);
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
		
		
		Instances dataset = createInstanceHeader( name, instances.size() );
		
		for( String key : instances.keySet() ) {
			MetaDataStreamInstance instance = instances.get( key );
			dataset.add( instance.toInstance( dataset ) );
		}
		
		// remove attributes that are unused
		Conversion.log("OK", "Create MetaDatastream", "Start applying filters... " );
		dataset = applyFilter( dataset, new RemoveUseless(), "-M 100.0 " ); 
		dataset = applyFilter( dataset, new RemoveUnusedClassValues(), "-T 0" );
		dataset.setRelationName( name );

		Conversion.log("OK", "Create MetaDatastream", "Start writing to file: " + name );
		toFile( dataset, name );
		Conversion.log("OK", "Create MetaDatastream", "Done." );
	}
	
	// TODO: should go into a real helper class. 
	public static Instances applyFilter( Instances dataset, Filter filter, String options ) throws Exception {
		((OptionHandler) filter).setOptions( Utils.splitOptions( options ) );
		filter.setInputFormat(dataset);
		return Filter.useFilter(dataset, filter);
	}
	
	private Map<String, MetaDataStreamInstance> getAllStreamInstances( Integer[] task_ids, int interval_size ) throws Exception {
		Map<String, MetaDataStreamInstance> instances = new HashMap<String, MetaDataStreamInstance>(); // indexing with String is soooo wrong. 
		for( Integer task_id : task_ids ) {
			Conversion.log("OK", "Create MetaDatastream", "Downloading Task: " + task_id );
			
			String sql = "SELECT `q`.`value` FROM `data_quality` `q`, `task_values` `t` WHERE `t`.`input` = 1 AND `q`.`quality` = 'InstanceCount' AND `t`.`value` = `q`.`data` AND `t`.`task_id` = " + task_id; 
			double task_size = QueryUtils.getIntFromDatabase( apiconnector, sql );
			
			for( int i = interval_size; i < task_size; i += INTERVALS_PER_DOWNLOAD * interval_size ) {
				Conversion.log("OK", "Create MetaDatastream", "Downloading Task Evaluations: " + task_id + " interval " + i + " - " + (i + INTERVALS_PER_DOWNLOAD * interval_size) + " OF " + task_size );
				
				TaskEvaluations te = apiconnector.openmlTaskEvaluations( task_id, i, i + INTERVALS_PER_DOWNLOAD * interval_size, interval_size );
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
							instances.get( key ).addClassifierScore( evaluation.getImplementation(), predictive_accuracy );
						} catch( Exception e ) { 
							Conversion.log( "WARNING","Generate Meta DataStream", e.getMessage() );
						}
					}
				}
			}
		}
		return instances;
	}
	
	private Instances createInstanceHeader( String relationName, int numInstances ) {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		
		attributes.add( new Attribute( MetaDataStreamInstance.ATT_TASK_ID_NAME) );
		attributes.add( new Attribute( MetaDataStreamInstance.ATT_INTERVAL_START_NAME ) );
		attributes.add( new Attribute( MetaDataStreamInstance.ATT_INTERVAL_END_NAME ) );
		
		ArrayList<String> classValues = new ArrayList<String>();
		for( String classifier : allClassifiers.keySet() ) {
			if( allClassifiers.get( classifier ) < (numInstances * 0.9) ) {
				Conversion.log("WARNING","Generate Meta DataStream","Dropping classifier since to few runs: " + classifier );
				continue;
			}
			attributes.add( new Attribute( MetaDataStreamInstance.ATT_CLASSIFIER_PREFIX + classifier ) );
			classValues.add( classifier );
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
	
	private static void toFile( Instances dataset, String filename ) throws IOException {
		BufferedWriter bw = new BufferedWriter( new FileWriter( new File( filename + ".arff" ) ) );
		
		bw.write( "@relation " + dataset.relationName() + "\n\n" );
		
		for( int i = 0; i < dataset.numAttributes(); ++i ) {
			bw.write( dataset.attribute(i) + "\n" );
		}
		
		bw.write("\n@data");
		for( int i = 0; i < dataset.numInstances(); ++i ) {
			bw.write( "\n" + dataset.instance(i) );
		}
		
		bw.close();
	}
}
