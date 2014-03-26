package org.openml.tools.bayesnetgenerator;

import java.io.File;
import java.io.FileReader;

import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ADNode;
import weka.core.Instances;

public class BayesnetGenerator {

	public static void main( String[] args ) throws Exception {
		Instances instances = new Instances( new FileReader( new File("/Users/jan/Documents/iris2.arff") ) );
		instances.setClass( instances.attribute( instances.numAttributes() - 1 ) );
		BayesNet bn = new BayesNet();
		
		bn.buildClassifier( instances );
		
		System.out.println( "Number of Nodes: " + bn.getNrOfNodes() );
		ADNode adtree = bn.getADTree();
		
		System.out.println( adtree );
	}
}
