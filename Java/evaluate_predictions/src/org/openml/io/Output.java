package org.openml.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import openml.algorithms.MathHelper;
import openml.models.Metric;
import openml.models.MetricCollector;
import openml.models.MetricScore;

import org.apache.commons.lang3.StringUtils;
import org.openml.evaluate.Task;
import org.openml.models.JsonItem;

import weka.classifiers.Evaluation;
import weka.core.Instances;

public class Output {
	
	private static DecimalFormat decimalFormat = new DecimalFormat("#.######", DecimalFormatSymbols.getInstance( Locale.ENGLISH ));
	
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
		}else if( task == Task.CLASSIFICATION || task == Task.LEARNINGCURVE || task == Task.TESTTHENTRAIN ) {
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
		return printMetrics( metrics, null, null );
	}
	
	public static String printMetrics( Map<Metric, MetricScore> metrics, ArrayList<JsonItem> additionalItems ) throws Exception {
		return printMetrics( metrics, null, additionalItems );
	}
	
	public static String printMetrics( Map<Metric, MetricScore> metrics, MetricCollector population, ArrayList<JsonItem> additionalItems ) throws Exception {
		String[] strMetrics = new String[metrics.size()];
		
		int counter = 0; 
		for( Metric m : metrics.keySet() ) {
			MetricScore value = metrics.get(m);
			Double[] p = null;
			Double stdev = null;
			
			if( population != null ) {
				p = population.getScores( m ).toArray( new Double[population.getScores( m ).size()] ); 
			}
			
			if(p != null) {
				if(p.length != 0) stdev = MathHelper.standard_deviation( p, false );
			}
			
			ArrayList<JsonItem> jsonItems = new ArrayList<JsonItem>();
			boolean mayPrint = false; // will be set to true if we find either a value or legal array. 
			
			jsonItems.add( new JsonItem( "name", m.name ) );
			jsonItems.add( new JsonItem( "implementation", m.implementation ) );
			jsonItems.add( new JsonItem( "label", m.label ) );
			if(value.getScore() != null && value.getScore().isNaN() == false ) {
				jsonItems.add( new JsonItem( "value", value.getScore() ) );
				mayPrint = true;
			}
			if( stdev != null && stdev.isNaN() == false )
				jsonItems.add( new JsonItem( "stdev", stdev ) );
			if(value.hasArray()) {
				jsonItems.add( new JsonItem( "array_data", value.getArrayAsString( decimalFormat ), false ) );
				mayPrint = true;
			}
			if( additionalItems != null ) {
				jsonItems.addAll( additionalItems );
			}
			
			if( mayPrint ) {
				strMetrics[counter] = dataFeatureToJson( jsonItems );
				counter ++;
			}
		}
		
		return StringUtils.join( strMetrics, ", \n", 0, counter );
	}
	
	public static String styleToJsonError( String value ) {
		return "{\"error\":\"" + value + "\"}" + "\n";
	}
	
	public static DecimalFormat getDecimalFormat() {
		return decimalFormat;
	}
	
	public static String dataFeatureToJson( List<JsonItem> jsonItems ) {
		StringBuilder sb = new StringBuilder();
		for( JsonItem item : jsonItems ) {
			if( item.useQuotes() ) {
				sb.append( ", \"" + item.getKey() + "\":\"" + item.getValue() + "\"" );
			} else {
				sb.append( ", \"" + item.getKey() + "\":" + item.getValue() );
			}
		}
		return "{" + sb.toString().substring( 2 ) + "}";
	}
	
	public static void instanes2file( Instances instances, Writer out ) throws IOException {
		BufferedWriter bw = new BufferedWriter( out );
		// Important: We can not use a std Instances.toString() approach, as instance files can grow
		bw.write("@relation " + instances.relationName() + "\n\n");
		for( int i = 0; i < instances.numAttributes(); ++i ) {
			bw.write( instances.attribute(i) + "\n" );
		}
		bw.write("\n@data\n");
		for( int i = 0; i < instances.numInstances(); ++i ) {
			if( i + 1 == instances.numInstances() ) {
				bw.write( instances.instance(i) + "" ); // fix for last instance
			} else {
				bw.write( instances.instance(i) + "\n" );
			}
		}
		bw.close();
	}
}
