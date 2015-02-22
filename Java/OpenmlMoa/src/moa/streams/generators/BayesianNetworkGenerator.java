package moa.streams.generators;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.openml.apiconnector.algorithms.MathHelper;

import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.classifiers.bayes.net.search.SearchAlgorithm;
import weka.core.Attribute;
import weka.core.AttributeStats;
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
	private static final int DISCRETIZE_BINS_NOMINAL = 3;
	private static final int DISCRETIZE_BINS_NUMERIC = 5;
	
	private boolean numericAttributes;
	private InstancesHeader streamHeader;
    private Instances sourceDataDiscretized;
    private Instances sourceDataOriginal;
    private Random instanceRandom;
    private Random driftRandom;
	private BayesNet bayesianNetwork;
	private SearchAlgorithm searchAlgorithm;
	private Map<Integer, BinDetails[]> bindetails;
	private Discretize discretizationFilter;
	private int instancesDone;
	
	private int conceptDriftFrequency;
	private int conceptDriftImpact;
	// first index is attribute. second index maps to ICPs, 
	// which has the same number of values as the cardinality of parents. 
	private Integer[][] distributionsDriftMapping;
	
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

	public IntOption driftRandomSeedOption = new IntOption(
            "friftRandomSeed", 'd',
            "Seed for random generation of concept drift.", 1);
	
	public IntOption conceptDriftFrequencyOption = new IntOption(
            "conceptDriftFrequency", 'c',
            "Determines at how after how many instances a concept drift generating operation will be performed.", 0);
	
	public IntOption conceptDriftImpactOption = new IntOption(
            "conceptDriftImpact", 'I',
            "Determines the number of swaps for every concept drift operation", 0);
	
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
		double[] nominalValue = new double[sourceDataDiscretized.numAttributes()];
		double[] usedValue = new double[sourceDataDiscretized.numAttributes()];
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
					
					iCPT = iCPT * sourceDataDiscretized.attribute(nParent).numValues() + (int) nominalValue[nParent]; 
					allParentsSet &= attributesSet[att_idx]; // bit shift: If attributesSet[att_idx] = false, so will be allParentsSet
				}
				if( allParentsSet && attributesSet[iAttribute] == false ) {
					attributesSet[iAttribute] = true;
					if( numericAttributes && sourceDataOriginal.attribute( iAttribute ).isNumeric() ) {
						double current = getNumericValueByProdBist(
							m_Distributions[iAttribute][distributionsDriftMapping[iAttribute][iCPT]], 
							sourceDataDiscretized.attribute(iAttribute) );
						usedValue[iAttribute] = current;
						try {
							nominalValue[iAttribute] = getBin(current, discretizationFilter.getCutPoints( iAttribute ));
						} catch( Exception e ) {
							throw new RuntimeException("Could not locate correct bin for numeric value. ");
						}
					} else {
						double current = getNominalValueByProbDist( 
							m_Distributions[iAttribute][distributionsDriftMapping[iAttribute][iCPT]], 
							sourceDataDiscretized.attribute(iAttribute) );
						nominalValue[iAttribute] = current;
						usedValue[iAttribute] = current;
					}
				}
			}
		}
		for( int i = 0; i < getHeader().numAttributes(); ++i ) {
			inst.setValue( i, usedValue[i] );
		}
		instancesDone += 1;
		if( conceptDriftFrequency > 0 && conceptDriftImpact > 0 ) {
			if( instancesDone % conceptDriftFrequency == 0 ) {
				
				for( int iNumberOfSwaps = 0; iNumberOfSwaps < conceptDriftImpact; ++iNumberOfSwaps ) {
					// pick attribute to perform swap on
					int attribute = Math.abs( driftRandom.nextInt() ) % sourceDataDiscretized.numAttributes();
					int cardinality = m_ParentSets[attribute].getCardinalityOfParents();
					// pick line in distribution table
					int swap1 = Math.abs( driftRandom.nextInt() ) % cardinality;
					
					if( cardinality > 1) { // we can only swap if there are two or more
						// pick second line
						int swap2 = Math.abs( driftRandom.nextInt() ) % (cardinality - 1);
						// correction for same line
						if( swap1 == swap2 ) { swap2 += 1; }
	
						int tmp = distributionsDriftMapping[attribute][swap1];
						distributionsDriftMapping[attribute][swap1] = distributionsDriftMapping[attribute][swap2];
						distributionsDriftMapping[attribute][swap2] = tmp;
					}
				}
			}
			
		}
		return inst;
	}

	@Override
	public void restart() {
        instanceRandom = new Random(this.instanceRandomSeedOption.getValue());
        driftRandom = new Random(this.driftRandomSeedOption.getValue());
        instancesDone = 0;
	}

	@Override
	protected void prepareForUseImpl(TaskMonitor arg0, ObjectRepository arg1) {
		instancesDone = 0;
		numericAttributes = this.numericAttributesOption.isSet();
		
        instanceRandom = new Random(this.instanceRandomSeedOption.getValue());
        driftRandom = new Random(this.driftRandomSeedOption.getValue());
        
        conceptDriftFrequency = this.conceptDriftFrequencyOption.getValue();
        conceptDriftImpact = this.conceptDriftImpactOption.getValue();
        
		try {
	        sourceDataOriginal = new Instances( new FileReader( this.arffFileOption.getFile() ) );
	        sourceDataDiscretized = new Instances( new FileReader( this.arffFileOption.getFile() ) );
			String relationNameOriginal = sourceDataDiscretized.relationName();
			
			discretizationFilter = new Discretize();
			String discretizeOptions = getDiscretizationOptions( 
				sourceDataDiscretized, 
				numericAttributes ? DISCRETIZE_BINS_NUMERIC : DISCRETIZE_BINS_NOMINAL,
				numericAttributes ? true : false );
			if( discretizeOptions != null ) { sourceDataDiscretized = applyFilter(sourceDataDiscretized, discretizationFilter, discretizeOptions ); }
			
			bindetails = createBinDetails( sourceDataOriginal, sourceDataDiscretized, discretizationFilter );
			
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
			
			// init distribution mapping to its normal behavour
			Estimator[][] m_Distributions = bayesianNetwork.getDistributions();
			distributionsDriftMapping = new Integer[m_Distributions.length][m_Distributions[0].length];
			for( int i = 0; i < m_Distributions.length; ++i ) {
				for( int j = 0; j < m_Distributions[i].length; ++j ) {
					distributionsDriftMapping[i][j] = j;
				}
			}
			
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
	
	public static String getDiscretizationOptions( Instances sourceData, int numBins, boolean equalFrequency ) {
		ArrayList<Integer> variablesToDiscretize = new ArrayList<Integer>();
		for( int i = 0; i < sourceData.numAttributes(); ++i ) {
			if( sourceData.attribute( i ).isNumeric() ) {
				variablesToDiscretize.add( i + 1 ); // 0-based/1-based
			}
		}
		
		// only discretize variables if needed
		if( variablesToDiscretize.size() > 0 ) {
			String eqFr = equalFrequency ? "-F " : "";
			return eqFr + "-Y -B " + numBins + " -R " + StringUtils.join( variablesToDiscretize, ',' );
		} else {
			return null;
		}
	}
	
	private static Instances applyFilter( Instances dataset, Filter filter, String options ) throws Exception {
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
	
	// Interesting observation:
	// - When taking the weighted mean, the class observation becomes very predictable
	private double getNumericValueByProdBist( Estimator e, Attribute att ) {
		Double current = instanceRandom.nextDouble();
		Double totalFound = 0.0;
		for( int i = 0; i < att.numValues(); ++i ) {
			totalFound += e.getProbability( i );
			if( current < totalFound ) return getGaussianFromBin( bindetails.get( att.index() )[i] );
		}
		return getGaussianFromBin( bindetails.get( att.index() )[att.numValues() - 1] ); // prevent an "off by epsilon" error.
	}
	private double getGaussianFromBin( BinDetails binStatistics ) {
		return instanceRandom.nextGaussian() * binStatistics.getStdev() + binStatistics.getMean(); 
	}

	private void createWekaSearchAlgorithm(String[] options) throws Exception {
        String searchAlgorithmName = options[0];
        String[] newoptions = options.clone();
        newoptions[0] = "";
        this.searchAlgorithm = (SearchAlgorithm) Utils.forName( SearchAlgorithm.class, searchAlgorithmName, newoptions );
    }

	@SuppressWarnings("unchecked")
	private static Map<Integer, BinDetails[]> createBinDetails( Instances datasetOriginal, Instances datasetDiscretized, Discretize discretizationFilter ) {
		Map<Integer, BinDetails[]> result = new HashMap<Integer, BinDetails[]>();
		for( int iNumAttributes = 0; iNumAttributes < datasetOriginal.numAttributes(); ++iNumAttributes ) {
			if( datasetOriginal.attribute( iNumAttributes ).isNumeric() ) { 
				AttributeStats attributeStatistics = datasetOriginal.attributeStats( iNumAttributes );
				double[] cutPoints = discretizationFilter.getCutPoints( iNumAttributes );
				int numBins = cutPoints.length + 1;
				BinDetails[] current = new BinDetails[numBins];
				List<Double>[] values = new ArrayList[numBins];
				for( int i = 0; i < numBins; ++i ) { values[i] = new ArrayList<Double>(); }
				
				for( int iNumInstances = 0; iNumInstances < datasetOriginal.numInstances(); ++iNumInstances ) {
					double currentValue = datasetOriginal.instance( iNumInstances ).value( iNumAttributes );
					if( Utils.isMissingValue( currentValue ) ) continue;
					try {
						int bin = getBin( currentValue, cutPoints );
						values[bin].add( currentValue );
					} catch (Exception e) {	e.printStackTrace(); } // cannot happen. 
				}
				
				for( int i = 0; i < numBins; ++i ) {
					Double[] population = values[i].toArray(new Double[values[i].size()]);
					// TODO: check on population size.
					if( population.length > 0 ) {
						current[i] = new BinDetails( 
								population.length, 
								MathHelper.mean( population ), 
								MathHelper.standard_deviation( population, true ) );
					} else {
						// empty bin, pick the minimum
						double totalWidth = attributeStatistics.numericStats.max - attributeStatistics.numericStats.min;
						double binWidth = totalWidth / numBins;
						
						current[i] = new BinDetails( 
								population.length, 
								attributeStatistics.numericStats.min + (i * binWidth) + (0.5 * binWidth), 
								0 );
					}
				}
				result.put( iNumAttributes, current );
			}
		}
		
		return result;
	}
	
	private static int getBin( double number, double[] cutPoints ) throws Exception {
		double currentValue = number;
		if( Utils.isMissingValue( currentValue) == false ) {
			if( currentValue <= cutPoints[0] ) {
				return 0;
			} else if( currentValue > cutPoints[cutPoints.length-1] ) {
				return cutPoints.length; // there is one more bin than cutPoints.length;
			} else {
				for( int iNumCutPoints = 1; iNumCutPoints < cutPoints.length; ++iNumCutPoints ) {
					if( currentValue > cutPoints[iNumCutPoints-1] && currentValue <= cutPoints[iNumCutPoints] ) {
						return iNumCutPoints;
					} 
				}
				// guaranteed that we will not come here.
				throw new Exception("Impossible exception. Number could not be fitted in any bin: " + number );
			}
		} else {
			throw new Exception("Cannot bin a missing value. ");
		}
	}
	
	private static class BinDetails {
		private final int size;
		private final double mean;
		private final double stdev;
		
		public BinDetails( int size, double mean, double stdev ) {
			this.size = size;
			this.mean = mean;
			this.stdev = stdev;
		}

		public int getSize() {
			return size;
		}

		public double getMean() {
			return mean;
		}

		public double getStdev() {
			return stdev;
		}
		
		@Override
		public String toString() {
			return "[size = "+getSize()+"; mean = "+getMean()+"; stdev = "+getStdev()+"]";
		}
	}
}
