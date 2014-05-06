package org.openml.tools.dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class MetaDataStreamInstance {
	public static final String ATT_TASK_ID_NAME = "openml_task_id";
	public static final String ATT_INTERVAL_START_NAME = "openml_interval_start";
	public static final String ATT_INTERVAL_END_NAME = "openml_interval_end";
	public static final String ATT_CLASSIFIER_PREFIX = "openml_classifier_";
	public static final String ATT_META_PREFIX = "meta_";
	public static final String ATT_CLASS_NAME = "class";
	
	private final int task_id;
	private final int did;
	private final Integer interval_start;
	private final Integer interval_end;
	
	private final Map<String,Double> classifier_scores;
	private final Map<String,Double> data_qualities;
	
	public MetaDataStreamInstance( int task_id, int did, Integer interval_start, Integer interval_end ) {
		this.task_id = task_id;
		this.did = did;
		this.interval_start = interval_start;
		this.interval_end = interval_end;
		
		this.classifier_scores = new HashMap<String, Double>();
		this.data_qualities = new HashMap<String, Double>();
	}
	
	public int getTask_id() {
		return task_id;
	}
	
	public int getDid() {
		return did;
	}

	public Integer getInterval_start() {
		return interval_start;
	}

	public Integer getInterval_end() {
		return interval_end;
	}

	public void addClassifierScore( String classifier, Double score ) {
		classifier_scores.put( classifier, score );
	}
	
	public void addDataQuality( String quality, Double value ) {
		data_qualities.put( quality, value );
	}
	
	public Instance toInstance( Instances instances ) {
		DenseInstance instance = new DenseInstance( instances.numAttributes() );
		instance.setDataset(instances);
		
		for( int i = 0; i < instances.numAttributes(); ++i ) {
			String attribute_name = instances.attribute( i ).name();
			if( attribute_name.equals( ATT_TASK_ID_NAME ) ) {
				instance.setValue( i,  task_id );
			} else if( attribute_name.equals( ATT_INTERVAL_START_NAME ) ) {
				instance.setValue( i,  interval_start );
			} else if( attribute_name.equals( ATT_INTERVAL_END_NAME ) ) {
				instance.setValue( i,  interval_end );
			} else if( attribute_name.startsWith( ATT_CLASSIFIER_PREFIX ) ) {
				String classifier_name = attribute_name.substring( ATT_CLASSIFIER_PREFIX.length() );
				if( classifier_scores.containsKey( classifier_name ) ) {
					instance.setValue( i,  classifier_scores.get( classifier_name ) );
				}
			} else if( attribute_name.equals( ATT_CLASS_NAME ) ) { 

				List<String> nomimalValues = nominalValues( instances.attribute( i ) );
				String bestClassifier = bestClassifier( nomimalValues );
				if( bestClassifier != null ) {
					instance.setValue( i, bestClassifier );
				} else {
					instance.setValue( i, Utils.missingValue() );
				}
				
			} else if( attribute_name.startsWith( ATT_META_PREFIX )) { // data quality
				String quality_name = attribute_name.substring( ATT_META_PREFIX.length() );
				if( data_qualities.containsKey( quality_name ) ) {
					instance.setValue( i,  data_qualities.get( quality_name ) );
				}
			}
		}
		
		return instance;
	}
	
	private static List<String> nominalValues( Attribute attribute ) {
		List<String> values = new ArrayList<String>();
		for( int i = 0; i < attribute.numValues(); ++i ) {
			values.add( attribute.value( i ) );
		}
		return values;
	}
	
	private String bestClassifier( List<String> legalValues ) {
		double bestScore = -1;
		String bestClassifier = null;
		for( String classifier : classifier_scores.keySet() ) {
			if( legalValues.contains( classifier ) ) {
				if( classifier_scores.get( classifier ) > bestScore ) {
					bestScore = classifier_scores.get( classifier );
					bestClassifier = classifier;
				}
			}
		}
		
		return bestClassifier;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( task_id + ", " + interval_start + ", " + interval_end + ", " + classifier_scores.size() + ", " + data_qualities.size() + ", " + bestClassifier( new ArrayList<String>(classifier_scores.keySet() ) ) );
		return sb.toString();
	}
	
	@Override 
	public boolean equals( Object other ) {
		if( other instanceof MetaDataStreamInstance ) {
			MetaDataStreamInstance o = (MetaDataStreamInstance) other;
			return o.getTask_id() == task_id && 
					o.getInterval_start() == interval_start && 
					o.getInterval_end() == interval_end;
		} else return false;
	}
	
	@Override
	public int hashCode() {
		return task_id * 7919 + interval_start;
	}
}
