package org.openml.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.openml.evaluate.Task;
import org.openml.models.ConfusionMatrix;

import weka.classifiers.Evaluation;
import weka.core.Instances;

public class Output {
	
	public static String printConfussionMatric( Evaluation evaluator, int classes ) {
		StringBuilder sb = new StringBuilder();
		
		return sb.toString();
	}
	
	
	public static String printMetrics( Evaluation evaluator, int classes, Task task ) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append( styleToJsonMetric( "number_of_instances","",				evaluator.numInstances(), true ) );
		sb.append( styleToJsonMetric( "mean_absolute_error","", 			evaluator.meanAbsoluteError() ) );
		sb.append( styleToJsonMetric( "mean_prior_absolute_error","", 		evaluator.meanPriorAbsoluteError() ) );
		sb.append( styleToJsonMetric( "root_mean_squared_error","",			evaluator.rootMeanSquaredError() ) );
		sb.append( styleToJsonMetric( "root_mean_prior_squared_error","",	evaluator.rootMeanPriorSquaredError() ) );
		sb.append( styleToJsonMetric( "relative_absolute_error","", 		evaluator.relativeAbsoluteError() / 100 ) );
		sb.append( styleToJsonMetric( "root_relative_squared_error","",		evaluator.rootRelativeSquaredError() / 100 ) );
		
		if( task == Task.REGRESSION )
			sb.append(regressionMetrics(evaluator, classes));
		else if( task == Task.CLASSIFICATION ) 
			sb.append( classificationMetrics(evaluator, classes));
		
		return sb.toString();
	}
	
	public static String regressionMetrics( Evaluation evaluator, int classes ) throws Exception {
		StringBuilder sb = new StringBuilder();
		//sb.append( styleToJsonMetric( "correlationCoefficient","",	evaluator.correlationCoefficient() ) );
		return sb.toString();
	}
	
	public static String classificationMetrics( Evaluation evaluator, int classes ) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append( styleToJsonMetric( "predictive_accuracy","",				evaluator.pctCorrect() / 100 ) );
		sb.append( styleToJsonMetric( "kappa","",							evaluator.kappa() ) );
		sb.append( styleToJsonMetric( "prior_entropy","",					evaluator.priorEntropy() ) );
		//sb.append( styleToJsonMetric( "KBInformation","",					evaluator.KBInformation() ) );
		//sb.append( styleToJsonMetric( "KBMeanInformation","",				evaluator.KBMeanInformation() ) );
		sb.append( styleToJsonMetric( "kb_relative_information_score","",	evaluator.KBRelativeInformation() / 100 ) );
		//sb.append( styleToJsonMetric( "pctUnclassified","",				evaluator.pctUnclassified() / 100 ) );
		//sb.append( styleToJsonMetric( "avgCost","",						evaluator.avgCost() ) );
		//sb.append( styleToJsonMetric( "totalCost","",						evaluator.totalCost() ) );
		//sb.append( styleToJsonMetric( "SFSchemeEntropy","",				evaluator.SFSchemeEntropy() ) );
		//sb.append( styleToJsonMetric( "SFMeanSchemeEntropy","",			evaluator.SFMeanSchemeEntropy() ) );
		//sb.append( styleToJsonMetric( "SFPriorEntropy","",				evaluator.SFPriorEntropy() ) );
		//sb.append( styleToJsonMetric( "SFMeanPriorEntropy","",			evaluator.SFMeanPriorEntropy() ) );
		//sb.append( styleToJsonMetric( "SFEntropyGain","",					evaluator.SFEntropyGain() ) );
		//sb.append( styleToJsonMetric( "SFMeanEntropyGain","",				evaluator.SFMeanEntropyGain() ) );
		
		return sb.toString();
	}
	
	public static String styleToJsonMetric( String name, String label, double value ) { 
		return styleToJsonMetric( name, label, value, false ); }
	public static String styleToJsonMetric( String name, String label, double value, boolean first ) {
		String delim = ( first == true ) ? "\n" : ",\n";
		return delim + "{\"name\":\"" + name + "\",\n \"label\":\"" + label + "\",\n \"value\":\"" + value + "\"}";}
	
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
