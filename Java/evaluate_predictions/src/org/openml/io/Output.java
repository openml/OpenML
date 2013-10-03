package org.openml.io;

import weka.classifiers.Evaluation;

public class Output {

	
	public static String globalMetrics( Evaluation evaluator, int classes ) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append( styleToJsonMetric( "mean_absolute_error","", 			evaluator.meanAbsoluteError() ) );
		sb.append( styleToJsonMetric( "mean_prior_absolute_error","", 		evaluator.meanPriorAbsoluteError() ) );
		sb.append( styleToJsonMetric( "root_mean_squared_error","",			evaluator.rootMeanSquaredError() ) );
		sb.append( styleToJsonMetric( "root_mean_prior_squared_error","",	evaluator.rootMeanPriorSquaredError() ) );
		sb.append( styleToJsonMetric( "relative_absolute_error","", 		evaluator.relativeAbsoluteError() / 100 ) );
		sb.append( styleToJsonMetric( "root_relative_squared_error","",		evaluator.rootRelativeSquaredError() / 100 ) );
		return sb.toString();
	}
	
	public static String regressionMetrics( Evaluation evaluator, int classes ) throws Exception {
		StringBuilder sb = new StringBuilder();
		//sb.append( styleToJsonMetric( "correlationCoefficient","",	evaluator.correlationCoefficient(), true ) );
		return sb.toString();
	}
	
	public static String classificationMetrics( Evaluation evaluator, int classes ) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append( styleToJsonMetric( "predictive_accuracy","",				evaluator.pctCorrect() / 100 ) );
		sb.append( styleToJsonMetric( "kappa","",							evaluator.kappa() ) );
		sb.append( styleToJsonMetric( "prior_entropy","",					evaluator.priorEntropy() ) );
		//sb.append( styleToJsonMetric( "KBInformation","",					evaluator.KBInformation() ) );
		//sb.append( styleToJsonMetric( "KBMeanInformation","",				evaluator.KBMeanInformation() ) );
		sb.append( styleToJsonMetric( "kb_relative_information_score","",	evaluator.KBRelativeInformation() / 100, true ) );
		//sb.append( styleToJsonMetric( "pctUnclassified","",				evaluator.pctUnclassified() / 100 ) );
		//sb.append( styleToJsonMetric( "avgCost","",						evaluator.avgCost() ) );
		//sb.append( styleToJsonMetric( "totalCost","",						evaluator.totalCost() ) );
		//sb.append( styleToJsonMetric( "SFSchemeEntropy","",				evaluator.SFSchemeEntropy() ) );
		//sb.append( styleToJsonMetric( "SFMeanSchemeEntropy","",			evaluator.SFMeanSchemeEntropy() ) );
		//sb.append( styleToJsonMetric( "SFPriorEntropy","",				evaluator.SFPriorEntropy() ) );
		//sb.append( styleToJsonMetric( "SFMeanPriorEntropy","",			evaluator.SFMeanPriorEntropy() ) );
		//sb.append( styleToJsonMetric( "SFEntropyGain","",					evaluator.SFEntropyGain() ) );
		//sb.append( styleToJsonMetric( "SFMeanEntropyGain","",				evaluator.SFMeanEntropyGain(), true ) );
		
		return sb.toString();
	}
	
	public static String styleToJsonMetric( String name, String label, double value ) { 
		return styleToJsonMetric( name, label, value, false ); }
	public static String styleToJsonMetric( String name, String label, double value, boolean last ) {
		char komma = ( last == true ) ? ' ' : ',';
		return "{\"name\":\"" + name + "\",\n \"label\":\"" + label + "\",\n \"value\":\"" + value + "\"}" + komma + "\n";}
	
	public static String styleToJsonError( String value ) {
		return "{\"error\":\"" + value + "\"}" + "\n";
	}
}
