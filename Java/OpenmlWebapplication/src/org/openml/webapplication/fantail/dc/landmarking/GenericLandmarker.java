package org.openml.webapplication.fantail.dc.landmarking;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.openml.webapplication.fantail.dc.Characterizer;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;

public class GenericLandmarker extends Characterizer {
	private static final String[] measures = { "AUC", "ErrRate", "Kappa" };
	private final int numFolds;
	private final String landmarkerName;
	private final String className;
	
	// maps id name to option list
	private final String[] options;
	
	public GenericLandmarker(String classifierName, String className, int numFolds, String[] options) {
		this.numFolds = numFolds;
		this.landmarkerName = classifierName;
		this.className = className;
		this.options = options;
	}

	@Override
	public String[] getIDs() {
		String[] keys = new String[measures.length];
		int currentIndex = 0;
		for( String measure : measures ) {
			keys[currentIndex++] = landmarkerName + measure;
		}
		return keys;
	}

	@Override
	public Map<String, Double> characterize(Instances instances) {
		
		Map<String, Double> results = new HashMap<String, Double>();
		
		try {
			
			Evaluation eval = new Evaluation(instances);
			AbstractClassifier cls = (AbstractClassifier) Class.forName( className ).newInstance();
			cls.setOptions( options );
			
			eval.crossValidateModel(cls, instances, numFolds, new Random(1));

			results.put( landmarkerName + "AUC", eval.weightedAreaUnderROC() );
			results.put( landmarkerName + "ErrRate", eval.errorRate() );
			results.put( landmarkerName + "Kappa", eval.kappa() );
			
		} catch( Exception e ) {
			results.put( landmarkerName + "AUC", null );
			results.put( landmarkerName + "ErrRate", null );
			results.put( landmarkerName + "Kappa", null );
		}
		
		return results;
	}

}
