package moa.streams.generators;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.classifiers.bayes.net.search.SearchAlgorithm;
import weka.core.Attribute;
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
	
	private boolean numericAttributes;
	private InstancesHeader streamHeader;
    private Instances sourceDataDiscretized;
    private Instances sourceDataOriginal;
    private Random instanceRandom;
	private BayesNet bayesianNetwork;
	private SearchAlgorithm searchAlgorithm;
	
	public IntOption instanceRandomSeedOption = new IntOption(
            "instanceRandomSeed", 'i',
            "Seed for random generation of instances.", 1);
	
	public FileOption arffFileOption = new FileOption("arffFile", 'f',
            "ARFF file to load.", null, "arff", false);
	
	public FlagOption numericAttributesOption = new FlagOption(
			"numericAttributes", 'n', 
			"If set, it will generate a Dataset that maintains the numeric features, " + 
			"reconstructed from the binning phase");
	
	public WEKAClassOption searchAlgorithmOption = new WEKAClassOption(
			"searchAlgorithm", 'a', 
			"The search algorithm for generating the Bayesian Network", 
			SearchAlgorithm.class, "weka.classifiers.bayes.net.search.local.K2");
	
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
		boolean[] attributesSet = new boolean[sourceDataDiscretized.numAttributes()];
		boolean allAttribuesSet = false;
		double[] result = new double[sourceDataDiscretized.numAttributes()];
		Instance inst = new DenseInstance(getHeader().numAttributes());
		inst.setDataset(getHeader());
		
		while( allAttribuesSet == false ) {
			allAttribuesSet = true; // until proven otherwise
			for (int iAttribute = 0; iAttribute < sourceDataDiscretized.numAttributes(); iAttribute++) {
				allAttribuesSet &= attributesSet[iAttribute]; // bit shift: If attributesSet[att_idx] = false, so will be allAttributesSet
				
				boolean allParentsSet = true;
				int iCPT = 0;
				
				for (int iParent = 0; iParent < m_ParentSets[iAttribute].getNrOfParents(); iParent++) {
					int att_idx = m_ParentSets[iAttribute].getParent( iParent );
					int nParent = bayesianNetwork.getParentSet(iAttribute).getParent(iParent);
					
					iCPT = iCPT * sourceDataDiscretized.attribute(nParent).numValues() + (int) result[nParent];
					allParentsSet &= attributesSet[att_idx]; // bit shift: If attributesSet[att_idx] = false, so will be allParentsSet
				}
				if( allParentsSet && attributesSet[iAttribute] == false ) {
					attributesSet[iAttribute] = true;
					if( numericAttributes && sourceDataOriginal.attribute( iAttribute ).isNumeric() ) {
						result[iAttribute] = getNumericValueByProdBist(
							m_Distributions[iAttribute][iCPT], 
							sourceDataDiscretized.attribute(iAttribute) );
					} else {
						result[iAttribute] = getNominalValueByProbDist( 
							m_Distributions[iAttribute][iCPT], 
							sourceDataDiscretized.attribute(iAttribute) );		
					}
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
			numericAttributes = this.numericAttributesOption.isSet();
			
	        instanceRandom = new Random(this.instanceRandomSeedOption.getValue());
	        sourceDataOriginal = new Instances( new FileReader( this.arffFileOption.getFile() ) );
	        sourceDataDiscretized = new Instances( new FileReader( this.arffFileOption.getFile() ) );
			String relationNameOriginal = sourceDataDiscretized.relationName();
			
			String discretizeOptions = getDiscretizationOptions( sourceDataDiscretized );
			if( discretizeOptions != null ) { sourceDataDiscretized = applyFilter(sourceDataDiscretized, new Discretize(), discretizeOptions ); }
			
			sourceDataDiscretized = applyFilter(sourceDataDiscretized, new ReplaceMissingValues(), "" );
			sourceDataDiscretized.setClass( sourceDataDiscretized.attribute( sourceDataDiscretized.numAttributes() - 1 ) );
			
			streamHeader = new InstancesHeader( numericAttributes ? sourceDataOriginal : sourceDataDiscretized );
			streamHeader.setRelationName( relationNameOriginal );
			
			String searchAlgorithmString = searchAlgorithmOption.getValueAsCLIString();
			
			createWekaSearchAlgorithm( Utils.splitOptions( searchAlgorithmString ) );
			// sets variable search algorithm
			
			bayesianNetwork = new BayesNet();
			bayesianNetwork.setSearchAlgorithm( searchAlgorithm );
			bayesianNetwork.buildClassifier( sourceDataDiscretized );
			
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
	
	public static String getDiscretizationOptions( Instances sourceData ) {
		ArrayList<Integer> variablesToDiscretize = new ArrayList<Integer>();
		for( int i = 0; i < sourceData.numAttributes(); ++i ) {
			if( sourceData.attribute( i ).isNominal() == false ) {
				variablesToDiscretize.add( i + 1 ); // 0-based/1-based
			}
		}
		
		// only discretize variables if needed
		if( variablesToDiscretize.size() > 0 ) {
			return "-Y -B 3 -R " + StringUtils.join( variablesToDiscretize, ',' );
		} else {
			return null;
		}
	}
	
	public static Instances applyFilter( Instances dataset, Filter filter, String options ) throws Exception {
		((OptionHandler) filter).setOptions( Utils.splitOptions( options ) );
		filter.setInputFormat(dataset);
		return Filter.useFilter(dataset, filter);
	}
	
	private int getNominalValueByProbDist( Estimator e, Attribute att ) {
		Double current = instanceRandom.nextDouble();
		Double totalFound = 0.0;
		for( int i = 0; i < att.numValues(); ++i ) {
			totalFound += e.getProbability( i );
			if( current < totalFound ) return i;
		}
		return att.numValues() - 1; // prevent an "off by epsilon" error.
	}
	
	private double getNumericValueByProdBist( Estimator e, Attribute att ) {
		double total_weighted_sum = 0.0;
		for( int i = 0; i < att.numValues(); ++i ) {
			// TODO!!! shift towards mean/stdev of this bin
			double current = instanceRandom.nextDouble(); 
			total_weighted_sum += e.getProbability( i ) * current;
		}
		return total_weighted_sum;
	}

	private void createWekaSearchAlgorithm(String[] options) throws Exception {
        String searchAlgorithmName = options[0];
        String[] newoptions = options.clone();
        newoptions[0] = "";
        this.searchAlgorithm = (SearchAlgorithm) Utils.forName( SearchAlgorithm.class, searchAlgorithmName, newoptions );
    }
}
