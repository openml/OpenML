package org.openml.tools.bayesnetgenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.classifiers.bayes.net.search.local.K2;
import weka.classifiers.bayes.net.search.local.SimulatedAnnealing;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.estimators.Estimator;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

public class BayesianNetworkGenerator {
	
	private static final Random RANDOMGENERATOR = new Random( );
	private static final String INPUTDATA = "/Users/jan/Documents/datasets/anneal.arff";
	private static final String OUTPUTDATA = "/Users/jan/Desktop/BNG_anneal.arff";
	private static final int NUMINSTANCES = 1000000;
	
	public static void main( String[] args ) throws Exception {
		Instances m_Instances = new Instances( new FileReader( new File( INPUTDATA ) ) );
		BufferedWriter bw = new BufferedWriter( new FileWriter( new File( OUTPUTDATA ) ) );
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
		bw.write( m_Result.toString() );
		
		BayesNet bn = new BayesNet();
		bn.setSearchAlgorithm( new K2() );
		bn.buildClassifier( m_Instances );
		
		for( int i = 0; i < NUMINSTANCES; ++i ) {
			Instance inst = new DenseInstance( m_Result.numAttributes() );
			inst.setDataset( m_Result );
			double[] newValues = generateInstance( bn, m_Instances );
			for( int j = 0; j < newValues.length; ++j ) {
				inst.setValue( j, newValues[j] );
			}
			bw.write( inst.toString() + "\n" );
		}
		bw.close();
		
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
		Double current = RANDOMGENERATOR.nextDouble();
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
