package org.openml.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openml.evaluate.Task;
import org.openml.helpers.MathHelper;
import org.openml.models.ConfusionMatrix;
import org.openml.models.Metric;
import org.openml.models.MetricCollector;

import weka.classifiers.Evaluation;
import weka.core.Instances;

public class Output {
	
	public static Map<Metric, Double> evaluatorToMap( Evaluation evaluator, int classes, Task task ) throws Exception {
		Map<Metric, Double> m = new HashMap<Metric, Double>();
		
		m.put(new Metric("number_of_instances", null), evaluator.numInstances());
		m.put(new Metric("mean_absolute_error", null), evaluator.meanAbsoluteError() );
		m.put(new Metric("mean_prior_absolute_error", null), evaluator.meanPriorAbsoluteError() );
		m.put(new Metric("root_mean_squared_error", null), evaluator.rootMeanSquaredError() );
		m.put(new Metric("root_mean_prior_squared_error", null), evaluator.rootMeanPriorSquaredError() );
		m.put(new Metric("relative_absolute_error", null), evaluator.relativeAbsoluteError() / 100 );
		m.put(new Metric("root_relative_squared_error", null), evaluator.rootRelativeSquaredError() / 100 );
		
		if( task == Task.REGRESSION ) {
			// here all measures for regression tasks
		}else if( task == Task.CLASSIFICATION ) {
			m.put(new Metric("predictive_accuracy", null), evaluator.pctCorrect() / 100 );
			m.put(new Metric("kappa", null), evaluator.kappa() );
			m.put(new Metric("prior_entropy", null), evaluator.priorEntropy() );
			m.put(new Metric("kb_relative_information_score", null), evaluator.KBRelativeInformation() / 100 );
		}
		return m;
	}
	
	public static String printMetrics( Map<Metric, Double> metrics ) throws Exception {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for( Metric m : metrics.keySet() ) {
			Double value = metrics.get(m);
			sb.append( styleToJsonMetric( m.name, m.label, value, -1.0, first ) );
			first = false;
		}
		
		return sb.toString();
	}
	
	public static String printMetrics( Map<Metric, Double> metrics, MetricCollector population ) throws Exception {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for( Metric m : metrics.keySet() ) {
			Double value = metrics.get(m);
			Double[] p = population.get( m ).toArray( new Double[population.get( m ).size()] );
			Double stdev = MathHelper.standard_deviation( p, false );
			sb.append( styleToJsonMetric( m.name, m.label, value, stdev, first ) );
			first = false;
		}
		
		return sb.toString();
	}
	
	public static String styleToJsonMetric( String name, String label, double value, double stdev ) { 
		return styleToJsonMetric( name, label, value, stdev, false ); }
	public static String styleToJsonMetric( String name, String label, double value, double stdev, boolean first ) {
		String delim = ( first == true ) ? "\n" : ",\n";
		String labelString = (label != null) ? "\"label\":\"" + label + "\",\n" : "";
		String stdevString = (stdev >= 0) ? "\"stdev\":" + stdev + ",\n" : "";
		return delim + "{\"name\":\"" + name + "\",\n "+labelString+stdevString+" \"value\":" + value + "}";}
	
	public static String styleToJsonError( String value ) {
		return "{\"error\":\"" + value + "\"}" + "\n";
	}
	
	public static String dataFeatureToJson( Map<String,String> key_values ) {
		StringBuilder sb = new StringBuilder();
		for( String key : key_values.keySet() ) {
			sb.append( ",\"" + key + "\":\"" + key_values.get(key) + "\"" );
		}
		return "{" + sb.toString().substring( 1 ) + "}";
	}
	
	public static void instanes2file( Instances instances, String filepath ) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
		// Important: We can not use a std Instances.toString() approach, as instance files can grow
		bw.write("@relation " + instances.relationName() + "\n\n");
		for( int i = 0; i < instances.numAttributes(); ++i ) {
			bw.write( instances.attribute(i) + "\n" );
		}
		bw.write("\n@data\n");
		for( int i = 0; i < instances.numInstances(); ++i ) {
			bw.write( instances.instance(i) + "\n" );
		}
		bw.close();
	}
	
	public static String confusionMatrixToJson( ConfusionMatrix cm ) {
		StringBuilder sb = new StringBuilder();
		
		for( int i = 0; i < cm.size(); ++i ) {
			for( int j = 0; j < cm.size(); ++j ) {
				sb.append( ",{\"actual\":"+i+",\"predicted\":"+j+",\"count\":"+cm.get(i,j)+"}\n" );
			}
		}
		
		return sb.toString().substring(1);
	}
}
