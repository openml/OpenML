package moa.streams.generators;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.classifiers.bayes.net.search.local.SimulatedAnnealing;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.estimators.Estimator;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import moa.core.InstancesHeader;
import moa.core.ObjectRepository;
import moa.options.AbstractOptionHandler;
import moa.options.FileOption;
import moa.options.IntOption;
import moa.streams.InstanceStream;
import moa.tasks.TaskMonitor;

public class BayesianNetworkGenerator extends AbstractOptionHandler implements
		InstanceStream {

	private static final long serialVersionUID = 6466101955540024300L;
	
    private InstancesHeader streamHeader;
    private Instances sourceData;
    private Random instanceRandom;
	private BayesNet bayesianNetwork;
	
	public IntOption instanceRandomSeedOption = new IntOption(
            "instanceRandomSeed", 'i',
            "Seed for random generation of instances.", 1);
	
	public FileOption arffFileOption = new FileOption("arffFile", 'f',
            "ARFF file to load.", null, "arff", false);

	@Override
	public void getDescription(StringBuilder arg0, int arg1) {
		String description = 
			"Takes a deault dataset, build a Bayesian Network over it, " + 
			"and uses this network to generate instances. Can be used to " + 
			"turn a relatively small dataset with real world data into a " + 
			"big dataset with pseudo real world data. ";
		arg0.append( description );
	}

	@Override
	public long estimatedRemainingInstances() {
		return -1;
	}

	@Override
	public InstancesHeader getHeader() {
		return streamHeader;
	}

	@Override
	public boolean hasMoreInstances() {
		return true;
	}

	@Override
	public boolean isRestartable() {
		return true;
	}

	@Override
	public Instance nextInstance() {
		ParentSet[] m_ParentSets = bayesianNetwork.getParentSets();
		Estimator[][] m_Distributions = bayesianNetwork.getDistributions();
		boolean[] attributesSet = new boolean[sourceData.numAttributes()];
		boolean allAttribuesSet = false;
		double[] result = new double[sourceData.numAttributes()];
		
		while( allAttribuesSet == false ) {
			allAttribuesSet = true; // until proven otherwise
			for (int iAttribute = 0; iAttribute < sourceData.numAttributes(); iAttribute++) {
				allAttribuesSet &= attributesSet[iAttribute]; // bit shift: If attributesSet[att_idx] = false, so will be allAttributesSet
				
				boolean allParentsSet = true;
				int iCPT = 0;
				
				for (int iParent = 0; iParent < m_ParentSets[iAttribute].getNrOfParents(); iParent++) {
					int att_idx = m_ParentSets[iAttribute].getParent( iParent );
					int nParent = bayesianNetwork.getParentSet(iAttribute).getParent(iParent);
					
					iCPT = iCPT * sourceData.attribute(nParent).numValues() + (int) result[nParent];
					allParentsSet &= attributesSet[att_idx]; // bit shift: If attributesSet[att_idx] = false, so will be allParentsSet
				}
				if( allParentsSet && attributesSet[iAttribute] == false ) {
					attributesSet[iAttribute] = true;
					result[iAttribute] = getNominalValueByProbDist( m_Distributions[iAttribute][iCPT], sourceData.attribute(iAttribute).numValues() );
				}
			}
		}
		return new DenseInstance( 1.0, result ) ;
	}

	@Override
	public void restart() {
        this.instanceRandom = new Random(this.instanceRandomSeedOption.getValue());

	}

	@Override
	protected void prepareForUseImpl(TaskMonitor arg0, ObjectRepository arg1) {
		try {
			sourceData = new Instances( new FileReader( this.arffFileOption.getFile() ) );
			String relationNameOriginal = sourceData.relationName();
			
			ArrayList<Integer> variablesToDiscretize = new ArrayList<Integer>();
			for( int i = 0; i < sourceData.numAttributes(); ++i ) {
				if( sourceData.attribute( i ).isNominal() == false ) {
					variablesToDiscretize.add( i + 1 ); // 0-based/1-based
				}
			}
			String options = "-Y -B 3 -R " + StringUtils.join( variablesToDiscretize, ',' );
			sourceData = applyFilter(sourceData, new Discretize(), options );
			sourceData = applyFilter(sourceData, new ReplaceMissingValues(), "" );
			sourceData.setClass( sourceData.attribute( sourceData.numAttributes() - 1 ) );
			
			streamHeader = new InstancesHeader( sourceData );
			streamHeader.setRelationName( relationNameOriginal );
			
			bayesianNetwork = new BayesNet();
			bayesianNetwork.setSearchAlgorithm( new SimulatedAnnealing() );
			bayesianNetwork.buildClassifier( sourceData );
		} catch ( Exception e) {
			throw new RuntimeException("Failed to initiate stream from indicated ARFF file. ");
		}

	}
	
	private static Instances applyFilter( Instances dataset, Filter filter, String options ) throws Exception {
		((OptionHandler) filter).setOptions( Utils.splitOptions( options ) );
		filter.setInputFormat(dataset);
		return Filter.useFilter(dataset, filter);
	}
	
	private int getNominalValueByProbDist( Estimator e, int numValues ) {
		Double current = instanceRandom.nextDouble();
		Double totalFound = 0.0;
		for( int i = 0; i < numValues; ++i ) {
			totalFound += e.getProbability( i );
			if( current < totalFound ) return i;
		}
		return numValues - 1; // prevent an "off by epsilon" error.
	}

}
