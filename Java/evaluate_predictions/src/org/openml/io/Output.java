package org.openml.io;

import weka.classifiers.Evaluation;

public class Output {

	
	public static String globalMetrics( Evaluation evaluator ) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append( styleToJson( "meanAbsoluteError", 		evaluator.meanAbsoluteError() ) );
		sb.append( styleToJson( "meanPriorAbsoluteError", 	evaluator.meanPriorAbsoluteError() ) );
		sb.append( styleToJson( "rootMeanSquaredError", 	evaluator.rootMeanSquaredError() ) );
		sb.append( styleToJson( "rootMeanPriorSquaredError",evaluator.rootMeanPriorSquaredError() ) );
		sb.append( styleToJson( "relativeAbsoluteError", 	evaluator.relativeAbsoluteError() / 100 ) );
		sb.append( styleToJson( "rootRelativeSquaredError",	evaluator.rootRelativeSquaredError() / 100 ) );
		return sb.toString();
	}
	
	public static String regressionMetrics( Evaluation evaluator ) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append( styleToJson( "correlationCoefficient",	evaluator.correlationCoefficient(), true ) );
		return sb.toString();
	}
	
	public static String classificationMetrics( Evaluation evaluator ) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append( styleToJson( "pctCorrect",			evaluator.pctCorrect() / 100 ) );
		sb.append( styleToJson( "kappa",				evaluator.kappa() ) );
		sb.append( styleToJson( "priorEntropy",			evaluator.priorEntropy() ) );
		sb.append( styleToJson( "KBInformation",		evaluator.KBInformation() ) );
		sb.append( styleToJson( "KBMeanInformation",	evaluator.KBMeanInformation() ) );
		sb.append( styleToJson( "KBRelativeInformation",evaluator.KBRelativeInformation() / 100 ) );
		sb.append( styleToJson( "pctUnclassified",		evaluator.pctUnclassified() / 100 ) );
		sb.append( styleToJson( "avgCost",				evaluator.avgCost() ) );
		sb.append( styleToJson( "totalCost",			evaluator.totalCost() ) );
		sb.append( styleToJson( "SFSchemeEntropy",		evaluator.SFSchemeEntropy() ) );
		sb.append( styleToJson( "SFMeanSchemeEntropy",	evaluator.SFMeanSchemeEntropy() ) );
		sb.append( styleToJson( "SFPriorEntropy",		evaluator.SFPriorEntropy() ) );
		sb.append( styleToJson( "SFMeanPriorEntropy",	evaluator.SFMeanPriorEntropy() ) );
		sb.append( styleToJson( "SFEntropyGain",		evaluator.SFEntropyGain() ) );
		sb.append( styleToJson( "SFMeanEntropyGain",	evaluator.SFMeanEntropyGain(), true ) );
		return sb.toString();
	}
	
	public static String styleToJson( String key, double value ) { 
		return styleToJson( key, value, false ); }
	public static String styleToJson( String key, double value, boolean last ) {
		char komma = ( last == true ) ? ' ' : ',';
		return "{\"" + key + "\":\"" + value + "\"}" + komma + "\n";}
	public String styleToJson( String key, String value ) { 
		return styleToJson( key, value, false ); }
	public static String styleToJson( String key, String value, boolean last ) {
		char komma = ( last == true ) ? ' ' : ',';
		return "{\"" + key + "\":\"" + value + "\"}" + komma + "\n";
	}
}
