package org.openml.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openml.evaluate.Task;
import org.openml.helpers.MathHelper;
import org.openml.models.Metric;
import org.openml.models.MetricCollector;
import org.openml.models.MetricScore;

import weka.classifiers.Evaluation;
import weka.core.Instances;

public class Output {
	
	public static Map<Metric, MetricScore> evaluatorToMap( Evaluation evaluator, int classes, Task task ) throws Exception {
		Map<Metric, MetricScore> m = new HashMap<Metric, MetricScore>();
		
		m.put(new Metric("number_of_instances", "openml.evaluation.number_of_instances(1.0)", null), new MetricScore( evaluator.numInstances() ) );
		m.put(new Metric("mean_absolute_error", "openml.evaluation.mean_absolute_error(1.0)", null), new MetricScore( evaluator.meanAbsoluteError() ) );
		m.put(new Metric("mean_prior_absolute_error", "openml.evaluation.mean_prior_absolute_error(1.0)", null), new MetricScore( evaluator.meanPriorAbsoluteError() ) );
		m.put(new Metric("root_mean_squared_error", "openml.evaluation.root_mean_squared_error(1.0)", null), new MetricScore( evaluator.rootMeanSquaredError() ) );
		m.put(new Metric("root_mean_prior_squared_error", "openml.evaluation.root_mean_prior_squared_error(1.0)", null), new MetricScore( evaluator.rootMeanPriorSquaredError() ) );
		m.put(new Metric("relative_absolute_error", "openml.evaluation.relative_absolute_error(1.0)", null), new MetricScore( evaluator.relativeAbsoluteError() / 100 ) );
		m.put(new Metric("root_relative_squared_error", "openml.evaluation.root_relative_squared_error(1.0)", null), new MetricScore( evaluator.rootRelativeSquaredError() / 100 ) );
		
		if( task == Task.REGRESSION ) {
			// here all measures for regression tasks
		}else if( task == Task.CLASSIFICATION ) {
			m.put(new Metric("predictive_accuracy", "openml.evaluation.predictive_accuracy(1.0)", null), new MetricScore( evaluator.pctCorrect() / 100 ) );
			m.put(new Metric("kappa", "openml.evaluation.kappa(1.0)", null), new MetricScore( evaluator.kappa() ) );
			m.put(new Metric("prior_entropy", "openml.evaluation.prior_entropy(1.0)", null), new MetricScore( evaluator.priorEntropy() ) );
			m.put(new Metric("kb_relative_information_score", "openml.evaluation.kb_relative_information_score(1.0)", null), new MetricScore( evaluator.KBRelativeInformation() / 100 ) );
			
			Double[] precision = new Double[classes];
			Double[] recall = new Double[classes];
			Double[] auroc = new Double[classes];
			Double[] fMeasure = new Double[classes];
			double[][] confussion_matrix = evaluator.confusionMatrix();
			for( int i = 0; i < classes; ++i ) {
				precision[i] = evaluator.precision(i);
				recall[i] = evaluator.recall(i);
				auroc[i] = evaluator.areaUnderROC(i);
				fMeasure[i] = evaluator.fMeasure(i);
			}
			// TODO: Fix AUROC!
			m.put(new Metric("precision", "openml.evaluation.precision(1.0)",null),new MetricScore( precision ));
			m.put(new Metric("recall", "openml.evaluation.recall(1.0)",null),new MetricScore( recall ));
			m.put(new Metric("f_measure", "openml.evaluation.f_measure(1.0)",null),new MetricScore( fMeasure ));
			//m.put(new Metric("area_under_the_roc_curve", "openml.evaluation.area_under_the_roc_curve(1.0)",null),new MetricScore( auroc ));
			m.put(new Metric("mean_weighted_precision", "openml.evaluation.mean_weighted_precision(1.0)",null),new MetricScore( evaluator.weightedPrecision(), precision ));
			m.put(new Metric("mean_weighted_recall", "openml.evaluation.mean_weighted_recall(1.0)",null),new MetricScore( evaluator.weightedRecall(), recall ));
			m.put(new Metric("mean_weighted_f_measure", "openml.evaluation.mean_weighted_f_measure(1.0)",null),new MetricScore( evaluator.weightedFMeasure(), fMeasure ));
			//m.put(new Metric("mean_weighted_area_under_the_roc_curve", "openml.evaluation.mean_weighted_area_under_the_roc_curve(1.0)",null),new MetricScore( evaluator.weightedAreaUnderROC(), auroc ));
			
			m.put(new Metric("confusion_matrix","openml.evaluation.confusion_matrix(1.0)",null), new MetricScore(confussion_matrix));
		}
		return m;
	}
	
	public static String printMetrics( Map<Metric, MetricScore> metrics ) throws Exception {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for( Metric m : metrics.keySet() ) {
			MetricScore value = metrics.get(m);
			sb.append( styleToJsonMetric( m.name, m.implementation, m.label, value, null, first ) );
			first = false;
		}
		
		return sb.toString();
	}
	
	public static String printMetrics( Map<Metric, MetricScore> metrics, MetricCollector population ) throws Exception {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for( Metric m : metrics.keySet() ) {
			MetricScore value = metrics.get(m);
			Double[] p = population.getScores( m ).toArray( new Double[population.getScores( m ).size()] ); 
			Double stdev = (p.length == 0) ? null : MathHelper.standard_deviation( p, false );
			sb.append( styleToJsonMetric( m.name, m.implementation, m.label, value, stdev, first ) );
			first = false;
		}
		
		return sb.toString();
	}
	
	public static String styleToJsonMetric( String name, String implementation, String label, MetricScore value, Double stdev ) { 
		return styleToJsonMetric( name, implementation, label, value, stdev, false ); }
	public static String styleToJsonMetric( String name, String implementation, String label, MetricScore value, Double stdev, boolean first ) {
		String delim = ( first == true ) ? "\n" : ",\n";
		String labelString = (label != null) ? ",\n \"label\":\"" + label : "";
		String stdevString = (stdev != null) ? ",\n \"stdev\":" + stdev : "";
		String valueString = value.getScore() != null ? ",\n \"value\":" + value.getScore() : "";
		String arrayString = value.hasArray() ? ",\n \"array_data\":" + value.getArrayAsString() : "";
		return delim + "{\"name\":\"" + name + "\",\n \"implementation\":\"" + implementation + "\"" +labelString+stdevString+valueString+arrayString + "}";}
	
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
}
