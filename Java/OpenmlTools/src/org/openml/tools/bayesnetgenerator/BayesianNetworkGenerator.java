package org.openml.tools.bayesnetgenerator;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.classifiers.bayes.net.search.local.SimulatedAnnealing;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.estimators.Estimator;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

public class BayesianNetworkGenerator {
	
	private static final Random randomGenerator = new Random( );

	public static void main( String[] args ) throws Exception {
		Instances m_Instances = new Instances( new FileReader( new File("/Users/jan/Documents/openmltest/iris2.arff") ) );
		String relationNameOriginal = m_Instances.relationName();
		
		ArrayList<Integer> variablesToDiscretize = new ArrayList<Integer>();
		for( int i = 0; i < m_Instances.numAttributes(); ++i ) {
			if( m_Instances.attribute( i ).isNominal() == false ) {
				variablesToDiscretize.add( i + 1 ); // 0-based/1-based
			}
		}
		String options = "-Y -B 3 -R " + StringUtils.join( variablesToDiscretize, ',' );
		m_Instances = applyFilter(m_Instances, new Discretize(), options );
		
		Instances m_Result = new Instances( m_Instances, 0, 0 );
		m_Result.setRelationName( "BayesianNetworkGenerator(" + relationNameOriginal + ")" );
		m_Instances.setClass( m_Instances.attribute( m_Instances.numAttributes() - 1 ) );
		
		BayesNet bn = new BayesNet();
		bn.setSearchAlgorithm( new SimulatedAnnealing() );
		bn.buildClassifier( m_Instances );
		
		for( int i = 0; i < 150; ++i ) {
			m_Result.add( new DenseInstance(1.0, generateInstance( bn, m_Instances ) ) );
		}
		
		System.out.println(m_Result);
		
	}
	
	private static double[] generateInstance( BayesNet bayesianNetwork, Instances instances ) {
		ParentSet[] m_ParentSets = bayesianNetwork.getParentSets();
		Estimator[][] m_Distributions = bayesianNetwork.getDistributions();
		boolean[] attributesSet = new boolean[instances.numAttributes()];
		boolean allAttribuesSet = false;
		double[] result = new double[instances.numAttributes()];
		
		while( allAttribuesSet == false ) {
			allAttribuesSet = true; // until proven otherwise
			for (int iAttribute = 0; iAttribute < instances.numAttributes(); iAttribute++) {
				allAttribuesSet &= attributesSet[iAttribute]; // bit shift: If attributesSet[att_idx] = false, so will be allAttributesSet
				
				boolean allParentsSet = true;
				int iCPT = 0;
				
				for (int iParent = 0; iParent < m_ParentSets[iAttribute].getNrOfParents(); iParent++) {
					int att_idx = m_ParentSets[iAttribute].getParent( iParent );
					int nParent = bayesianNetwork.getParentSet(iAttribute).getParent(iParent);
					
					iCPT = iCPT * instances.attribute(nParent).numValues() + (int) result[nParent];
					allParentsSet &= attributesSet[att_idx]; // bit shift: If attributesSet[att_idx] = false, so will be allParentsSet
				}
				if( allParentsSet && attributesSet[iAttribute] == false ) {
					attributesSet[iAttribute] = true;
					result[iAttribute] = getNominalValueByProbDist( m_Distributions[iAttribute][iCPT], instances.attribute(iAttribute).numValues() );
				}
			}
		}
		return result;
	}
	
	private static int getNominalValueByProbDist( Estimator e, int numValues ) {
		Double current = randomGenerator.nextDouble();
		Double totalFound = 0.0;
		for( int i = 0; i < numValues; ++i ) {
			totalFound += e.getProbability( i );
			if( current < totalFound ) return i;
		}
		return numValues - 1; // prevent an "off by epsilon" error.
	}
	

	
	private static Instances applyFilter( Instances dataset, Filter filter, String options ) throws Exception {
		((OptionHandler) filter).setOptions( Utils.splitOptions( options ) );
		filter.setInputFormat(dataset);
		return Filter.useFilter(dataset, filter);
	}
}
