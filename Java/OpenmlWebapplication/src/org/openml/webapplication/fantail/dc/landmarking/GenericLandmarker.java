package org.openml.webapplication.fantail.dc.landmarking;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.openml.webapplication.fantail.dc.Characterizer;

import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

public class GenericLandmarker extends Characterizer {
	private static final String[] measures = { "AUC", "ErrRate", "Kappa" };
	private final int numFolds;
	private final String classifierName;
	private final String className;
	
	// maps id name to option list
	private final TreeMap<String, String[]> options;
	
	public GenericLandmarker( String classifierName, String className, int numFolds, TreeMap<String, String[]> options ) {
		this.numFolds = numFolds;
		this.classifierName = classifierName;
		this.className = className;
		if( options != null ) {
			this.options = options;
		} else {
			this.options = new TreeMap<String, String[]>();
			this.options.put( "", new String[0] );
		}
	}

	@Override
	public String[] getIDs() {
		String[] keys = new String[options.size()*3];
		int currentIndex = 0;
		for( String option : options.keySet() ) {
			for( String measure : measures ) {
				keys[currentIndex++] = classifierName + option + measure;
			}
		}
		return keys;
	}

	@Override
	public Map<String, Double> characterize(Instances instances) {
		Map<String, Double> results = new HashMap<String, Double>();

		
		for( String id : options.keySet() ) {
			try {
				weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(instances);
				AbstractClassifier cls = (AbstractClassifier) Class.forName( className ).newInstance();
				cls.setOptions( options.get( id ) );
				
				eval.crossValidateModel(cls, instances, numFolds, new java.util.Random(1));

				results.put( classifierName + id + "AUC", eval.weightedAreaUnderROC() );
				results.put( classifierName + id + "ErrRate", eval.errorRate() );
				results.put( classifierName + id + "Kappa", eval.kappa() );
			} catch( Exception e ) {
				e.printStackTrace();
			}
		}
		return results;
	}

}
