package moa.streams.generators;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.classifiers.bayes.net.search.SearchAlgorithm;
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
import moa.options.FlagOption;
import moa.options.IntOption;
import moa.options.WEKAClassOption;
import moa.streams.InstanceStream;
import moa.tasks.TaskMonitor;

public class BayesianNetworkGenerator extends AbstractOptionHandler implements
		InstanceStream {

	private static final long serialVersionUID = 6466101955540024300L;
	
    private InstancesHeader streamHeader;
    private Instances sourceData;
    private Random instanceRandom;
	private BayesNet bayesianNetwork;
	private SearchAlgorithm searchAlgorithm;
	
	public IntOption instanceRandomSeedOption = new IntOption(
            "instanceRandomSeed", 'i',
            "Seed for random generation of instances.", 1);
	
	public FileOption arffFileOption = new FileOption("arffFile", 'f',
            "ARFF file to load.", null, "arff", false);
	
	public WEKAClassOption searchAlgorithmOption = new WEKAClassOption(
			"searchAlgorithm", 'a', 
			"The search algorithm for generating the Bayesian Network", 
			SearchAlgorithm.class, "weka.classifiers.bayes.net.search.local.K2");
	
	//public FlagOption printNetworkOption = new FlagOption(
	//		"printNetwork", 'p', "Indicates whether the network should also be printed to a file. ");
	
	public FileOption printNetworkOption = new FileOption(
			"printNetwork", 'p', "If set, the generated Network will also be printed to a file.", 
			null, "arff", true );
	
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
		Instance inst = new DenseInstance(getHeader().numAttributes());
		inst.setDataset(getHeader());
		
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
		for( int i = 0; i < getHeader().numAttributes(); ++i ) {
			inst.setValue( i, result[i] );
		}
		
		return inst;
	}

	@Override
	public void restart() {
        instanceRandom = new Random(this.instanceRandomSeedOption.getValue());
	}

	@Override
	protected void prepareForUseImpl(TaskMonitor arg0, ObjectRepository arg1) {
		try {
	        instanceRandom = new Random(this.instanceRandomSeedOption.getValue());
			sourceData = new Instances( new FileReader( this.arffFileOption.getFile() ) );
			String relationNameOriginal = sourceData.relationName();
			
			ArrayList<Integer> variablesToDiscretize = new ArrayList<Integer>();
			for( int i = 0; i < sourceData.numAttributes(); ++i ) {
				if( sourceData.attribute( i ).isNominal() == false ) {
					variablesToDiscretize.add( i + 1 ); // 0-based/1-based
				}
			}
			
			// only discretize variables if needed
			if( variablesToDiscretize.size() > 0 ) {
				String options = "-Y -B 3 -R " + StringUtils.join( variablesToDiscretize, ',' );
				sourceData = applyFilter(sourceData, new Discretize(), options );
			}
			
			sourceData = applyFilter(sourceData, new ReplaceMissingValues(), "" );
			sourceData.setClass( sourceData.attribute( sourceData.numAttributes() - 1 ) );
			
			streamHeader = new InstancesHeader( sourceData );
			streamHeader.setRelationName( relationNameOriginal );
			
			String searchAlgorithmString = searchAlgorithmOption.getValueAsCLIString();
			
			createWekaSearchAlgorithm( Utils.splitOptions( searchAlgorithmString ) );
			// sets variable search algorithm
			
			bayesianNetwork = new BayesNet();
			bayesianNetwork.setSearchAlgorithm( searchAlgorithm );
			bayesianNetwork.buildClassifier( sourceData );
			
			if( printNetworkOption.getFile() != null ) {
				BufferedWriter bw = new BufferedWriter( new FileWriter( printNetworkOption.getFile() ) );
				bw.write( bayesianNetwork.toXMLBIF03() );
				bw.close();
			} 
			
		} catch ( Exception e) {
			e.printStackTrace();
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

	private void createWekaSearchAlgorithm(String[] options) throws Exception {
        String searchAlgorithmName = options[0];
        String[] newoptions = options.clone();
        newoptions[0] = "";
        this.searchAlgorithm = (SearchAlgorithm) Utils.forName( SearchAlgorithm.class, searchAlgorithmName, newoptions );
    }
}
