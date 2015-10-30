package weka.experiment;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.models.Metric;
import org.openml.apiconnector.models.MetricScore;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.Task.Input.Data_set;
import org.openml.apiconnector.xml.Task.Input.Estimation_procedure;
import org.openml.weka.algorithm.InstancesHelper;

import weka.core.Instances;
import weka.core.UnsupportedAttributeTypeException;
import weka.experiment.CrossValidationResultProducer;
import weka.experiment.OutputZipper;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class ClusteringResultProducer extends CrossValidationResultProducer {
	
	
	private static final long serialVersionUID = 1L;

	private static final String FOLDS_FILE_TRAIN = "TRAIN";
	private static final String FOLDS_FILE_TEST = "TEST";

	public static final String TASK_FIELD_NAME = "OpenML_Task_id";
	public static final String SAMPLE_FIELD_NAME = "Sample";

	/** The task to be run */
	protected Task m_Task;
	protected boolean regressionTask;

	/** Number of samples, if applicable **/
	protected int m_NumSamples = 1; // default to 1

	protected String currentTaskRepresentation;
	
	protected OpenmlConnector apiconnector;
	
	public ClusteringResultProducer() {
		
	}
	
	public ClusteringResultProducer(OpenmlConnector apiconnector ) {
		super();
		this.m_SplitEvaluator = new OpenmlClusteringSplitEvaluator();
		this.apiconnector = apiconnector;
	}

	public void setTask(Task t) throws Exception {
		m_Task = t;

		Data_set ds = TaskInformation.getSourceData(m_Task);
		Estimation_procedure ep = TaskInformation.getEstimationProcedure(m_Task);

		DataSetDescription dsd = ds.getDataSetDescription(apiconnector);
		m_Instances = new Instances( new FileReader( dsd.getDataset( apiconnector.getApiKey() ) ) );
		
		// remove attributes that may not be used.
		if( dsd.getIgnore_attribute() != null ) {
			for( String ignoreAttr : dsd.getIgnore_attribute() ) {
				Remove remove = new Remove();
				remove.setAttributeIndices(""+(m_Instances.attribute(ignoreAttr).index()+1)); // 0-based / 1-based
				remove.setInputFormat(m_Instances);
				m_Instances = Filter.useFilter(m_Instances, remove);
				
			}
		}
		
		currentTaskRepresentation = "Task " + m_Task.getTask_id() + " (" + TaskInformation.getSourceData(m_Task).getDataSetDescription(apiconnector).getName() + ")";

		m_NumFolds = 1;
		m_NumSamples = 1;
		try { m_NumFolds = TaskInformation.getNumberOfFolds(t); } catch (Exception e) { }
		try { m_NumSamples = TaskInformation.getNumberOfSamples(t); } catch (Exception e) { }
	}

	/**
	 * Gets the names of each of the columns produced for a single run. This
	 * method should really be static.
	 * 
	 * @return an array containing the name of each column
	 */
	@Override
	public String[] getKeyNames() {
		// TODO: also add keys of splitevaluator again, for analyse panel
		//String[] keyNames = m_SplitEvaluator.getKeyNames();
		// Add in the names of our extra key fields
		String[] newKeyNames = new String[/*keyNames.length + */5];
		newKeyNames[0] = DATASET_FIELD_NAME;
		newKeyNames[1] = RUN_FIELD_NAME;
		newKeyNames[2] = FOLD_FIELD_NAME;
		newKeyNames[3] = SAMPLE_FIELD_NAME;
		newKeyNames[4] = TASK_FIELD_NAME;
		//System.arraycopy(keyNames, 0, newKeyNames, 5, keyNames.length);
		return newKeyNames;
	}

	/**
	 * Gets the data types of each of the columns produced for a single run.
	 * This method should really be static.
	 * 
	 * @return an array containing objects of the type of each column. The
	 *         objects should be Strings, or Doubles.
	 */
	@Override
	public Object[] getKeyTypes() {

		// TODO: also add keys of splitevaluator again, for analyse panel
		//Object[] keyTypes = m_SplitEvaluator.getKeyTypes();
		// Add in the types of our extra fields
		Object[] newKeyTypes = new String[/*keyTypes.length + */5];
		newKeyTypes[0] = new String();
		newKeyTypes[1] = new String();
		newKeyTypes[2] = new String();
		newKeyTypes[3] = new String();
		newKeyTypes[4] = new String();
		//System.arraycopy(keyTypes, 0, newKeyTypes, 5, keyTypes.length);
		return newKeyTypes;
	}

	  /**
	   * Gets the names of each of the columns produced for a single run. This
	   * method should really be static.
	   * 
	   * @return an array containing the name of each column
	   */
	  @Override
	  public String[] getResultNames() {

			// TODO: also add keys of splitevaluator again, for analyse panel
	    //String[] resultNames = m_SplitEvaluator.getResultNames();
	    // Add in the names of our extra Result fields
	    String[] newResultNames = new String[/*resultNames.length + */1];
	    newResultNames[0] = TIMESTAMP_FIELD_NAME;
	    //System.arraycopy(resultNames, 0, newResultNames, 1, resultNames.length);
	    return newResultNames;
	  }

	  /**
	   * Gets the data types of each of the columns produced for a single run. This
	   * method should really be static.
	   * 
	   * @return an array containing objects of the type of each column. The objects
	   *         should be Strings, or Doubles.
	   */
	  @Override
	  public Object[] getResultTypes() {

			// TODO: also add keys of splitevaluator again, for analyse panel
	    //Object[] resultTypes = m_SplitEvaluator.getResultTypes();
	    // Add in the types of our extra Result fields
	    Object[] newResultTypes = new Object[/*resultTypes.length + */1];
	    newResultTypes[0] = new Double(0);
	    //System.arraycopy(resultTypes, 0, newResultTypes, 1, resultTypes.length);
	    return newResultTypes;
	  }

	@Override
	public void doRun(int run) throws Exception {
		
		
		String currentRunRepresentation = currentTaskRepresentation + " with " + (String) m_SplitEvaluator.getKey()[0] + " - Repeat " + (run-1);
		

		if (getRawOutput()) {
			if (m_ZipDest == null) {
				m_ZipDest = new OutputZipper(m_OutputFile);
			}
		}

		if (m_Instances == null) {
			throw new Exception("No Instances set");
		}

		if (m_Task == null) {
			throw new Exception("No task set");
		}

		// creating all empty copies for each fold
		Instances[][] trainingSets = new Instances[m_NumFolds][m_NumSamples];
		Instances[][] testSets = new Instances[m_NumFolds][m_NumSamples];
		Map<Integer, Integer[]> rowids = new HashMap<Integer, Integer[]>();

		for (int i = 0; i < m_NumFolds; ++i) {
			for (int j = 0; j < m_NumSamples; ++j) {
				trainingSets[i][j] = new Instances(m_Instances, 0, 0);
				testSets[i][j] = new Instances(m_Instances, 0, 0);
			}
		}
		/*
		// This is the part where we sample all train and test sets.
		// We should not measure time for doing so, since this is OpenML overhead.
		Conversion.log( "INFO", "Splits", "Obtaining folds for " + currentRunRepresentation );
		for (int i = 0; i < m_Splits.numInstances(); ++i) {
			int repeat = (int) m_Splits.instance(i).value(attRepeatIndex);
			if (repeat == run - 1) { // 1-based/0-based correction
				int fold = (int) m_Splits.instance(i).value(attFoldIndex);
				int sample = (attSampleIndex < 0) ? 0 : (int) m_Splits
						.instance(i).value(attSampleIndex);
				String type = m_Splits.attribute(attTypeIndex).value(
						(int) m_Splits.instance(i).value(attTypeIndex));
				int rowid = (int) m_Splits.instance(i).value(attRowidIndex);

				if (type.equals(FOLDS_FILE_TRAIN)) {
					trainingSets[fold][sample].add(m_Instances.instance(rowid));
				} else if (type.equals(FOLDS_FILE_TEST)) {
					testSets[fold][sample].add(m_Instances.instance(rowid));
					if (rowids.containsKey(foldSampleIdx(fold, sample))) {
						rowids.put( 
							foldSampleIdx(fold, sample),
							ArrayUtils.addAll(rowids.get(foldSampleIdx(fold, sample)),rowid));
					} else {
						Integer[] n = { rowid };
						rowids.put(foldSampleIdx(fold, sample), n);
					}
				}
			}
		} // END OF OPENML SAMPLING
		Conversion.log( "INFO", "Splits", "Folds obtained " );*/

		// TODO: this is a dirty hack, clean up later?
		Integer[] rowidsHack = new Integer[m_Instances.numInstances()];
		for (int i = 0; i < m_Instances.numInstances(); ++i) {
			rowidsHack[i] = i;
		}
		
		
		//for (int fold = 0; fold < m_NumFolds; fold++) {
		//	for (int sample = 0; sample < m_NumSamples; ++sample) {
				// Add in some fields to the key like run and fold number, data set, name
				String currentFoldRepresentation = 1+"";
				Conversion.log( "INFO", "Perform Run", "Started on performing " + currentRunRepresentation + ", " + currentFoldRepresentation );

				OpenmlClusteringSplitEvaluator ocs = (OpenmlClusteringSplitEvaluator) m_SplitEvaluator;
				try {
					Map<String, Object> splitEvaluatorResults = new HashMap<String, Object>();
					Map<Metric, MetricScore> userMeasures = new HashMap<Metric, MetricScore>();
					String[] seResultNames = m_SplitEvaluator.getResultNames();
					// TODO: randomize data??????
					Object[] seResults = m_SplitEvaluator.getResult(m_Instances, m_Instances);
					Object[] results = new Object[seResults.length + 1];
					results[0] = getTimestamp();
					System.arraycopy(seResults, 0, results, 1, seResults.length);
					
					for( int i = 0; i < seResultNames.length; ++i ) {
						splitEvaluatorResults.put( seResultNames[i], seResults[i] );
					}
					/*
					for( UserMeasures um : USER_MEASURES ) {
						if( splitEvaluatorResults.containsKey(um.wekaFunctionName)) {
							userMeasures.put( 
								new Metric(um.openmlFunctionName, um.openmlImplementationName), 
								new MetricScore( 
									((Double) splitEvaluatorResults.get(um.wekaFunctionName)) * um.factor, 
									testSets[fold][sample].size() ) );
						}
					}
					*/
					if (m_ResultListener instanceof ClusteringResultListener) {
						// run - 1 is for 1-based/0-based correction
						((ClusteringResultListener) m_ResultListener).acceptResultsForSending(  
							m_Task, run - 1, ocs.getClusterer(),
							(String) m_SplitEvaluator.getKey()[1], rowidsHack,
							ocs.getClusterAssignment(), null);
					}
				} catch (UnsupportedAttributeTypeException ex) {
					// Save the train and test data sets for debugging purposes?
					Conversion.log( "ERROR", "Perform Run", "Unable to finish " + currentRunRepresentation + ", " + 
							currentFoldRepresentation + " with " + ocs.getClusterer().getClass().getName() + ": " + 
							ex.getMessage() );
					if (m_ResultListener instanceof ClusteringResultListener) {
						((ClusteringResultListener) m_ResultListener).acceptErrorResult(
							m_Task, ocs.getClusterer(), ex.getMessage(), (String) ocs.getKey()[1]);
					}
				}
			//}
		//}
	}

	private int foldSampleIdx(int fold, int sample) {
		return fold * m_NumSamples + sample;
	}
	
	private static class UserMeasures {
		private final String openmlFunctionName;
		private final String openmlImplementationName;
		private final String wekaFunctionName;
		private final double factor;
		
		private UserMeasures( String openmlFunctionName, String openmlImplementationName, String wekaFunctionName, double factor ) {
			this.openmlFunctionName = openmlFunctionName;
			this.openmlImplementationName = openmlImplementationName;
			this.wekaFunctionName = wekaFunctionName;
			this.factor = factor;
		}
		
		private UserMeasures( String openmlFunctionName, String openmlImplementationName, String wekaFunctionName ) {
			this(openmlFunctionName, openmlImplementationName, wekaFunctionName, 1.0D );
		}
	}
}
