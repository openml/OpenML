package org.openml.weka.experiment;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.models.Metric;
import org.openml.apiconnector.models.MetricScore;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.Task.Input.Data_set;
import org.openml.apiconnector.xml.Task.Input.Estimation_procedure;
import org.openml.weka.algorithm.InstancesHelper;

import weka.core.Instances;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;
import weka.experiment.CrossValidationResultProducer;
import weka.experiment.OutputZipper;

public class TaskResultProducer extends CrossValidationResultProducer {

	private static final long serialVersionUID = 1L;

	private static final String FOLDS_FILE_TRAIN = "TRAIN";
	private static final String FOLDS_FILE_TEST = "TEST";

	public static final String TASK_FIELD_NAME = "OpenML_Task_id";
	public static final String SAMPLE_FIELD_NAME = "Sample";

	/** The task to be run */
	protected Task m_Task;

	/** Instances file with splits in it **/
	protected Instances m_Splits;

	/** Number of samples, if applicable **/
	protected int m_NumSamples = 1; // default to 1
	
	/** Current task information string **/
	protected String currentTaskRepresentation = "";

	public TaskResultProducer() {
		super();
		m_SplitEvaluator = new TaskSplitEvaluator();
	}

	public void setTask(Task t) throws Exception {
		m_Task = t;

		Data_set ds = TaskInformation.getSourceData(m_Task);
		Estimation_procedure ep = TaskInformation.getEstimationProcedure(m_Task);

		DataSetDescription dsd = ds.getDataSetDescription();
		m_Instances = new Instances( new FileReader( dsd.getDataset() ) );
		
		InstancesHelper.setTargetAttribute(m_Instances, ds.getTarget_feature());
		
		m_Splits = new Instances( new FileReader( ep.getDataSplits() ) );
		
		currentTaskRepresentation = "Task " + m_Task.getTask_id() + " (" + TaskInformation.getSourceData(m_Task).getDataSetDescription().getName() + ")";

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

		String[] keyNames = m_SplitEvaluator.getKeyNames();
		// Add in the names of our extra key fields
		String[] newKeyNames = new String[keyNames.length + 5];
		newKeyNames[0] = DATASET_FIELD_NAME;
		newKeyNames[1] = RUN_FIELD_NAME;
		newKeyNames[2] = FOLD_FIELD_NAME;
		newKeyNames[3] = SAMPLE_FIELD_NAME;
		newKeyNames[4] = TASK_FIELD_NAME;
		System.arraycopy(keyNames, 0, newKeyNames, 5, keyNames.length);
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

		Object[] keyTypes = m_SplitEvaluator.getKeyTypes();
		// Add in the types of our extra fields
		Object[] newKeyTypes = new String[keyTypes.length + 5];
		newKeyTypes[0] = new String();
		newKeyTypes[1] = new String();
		newKeyTypes[2] = new String();
		newKeyTypes[3] = new String();
		newKeyTypes[4] = new String();
		System.arraycopy(keyTypes, 0, newKeyTypes, 5, keyTypes.length);
		return newKeyTypes;
	}

	@Override
	public void doRun(int run) throws Exception {
		boolean useSamples = false;
		int attTypeIndex = m_Splits.attribute("type").index();
		int attRowidIndex = m_Splits.attribute("rowid").index();
		int attFoldIndex = m_Splits.attribute("fold").index();
		int attRepeatIndex = m_Splits.attribute("repeat").index();
		int attSampleIndex = -1;
		TaskSplitEvaluator tse = ((TaskSplitEvaluator) m_SplitEvaluator);
		String currentRunRepresentation = currentTaskRepresentation + " with " + (String) tse.getKey()[0] + " - Repeat " + (run-1);
		
		if (m_Splits.attribute("sample") != null) {
			attSampleIndex = m_Splits.attribute("sample").index();
			useSamples = true;
		}

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
		Conversion.log( "INFO", "Splits", "Folds obtained " );

		for (int fold = 0; fold < m_NumFolds; fold++) {
			for (int sample = 0; sample < m_NumSamples; ++sample) {
				// Add in some fields to the key like run and fold number, data set, name
				String currentFoldRepresentation = "fold " + fold + (useSamples ? ", sample " + sample : "");
				Conversion.log( "INFO", "Perform Run", "Started on performing " + currentRunRepresentation + ", " + currentFoldRepresentation );
				Object[] seKey = m_SplitEvaluator.getKey();
				Object[] key = new Object[seKey.length + 5];
				key[0] = Utils.backQuoteChars(m_Instances.relationName());
				key[1] = "" + run;
				key[2] = "" + (fold + 1);
				key[3] = "" + sample;
				key[4] = "" + m_Task.getTask_id();
				System.arraycopy(seKey, 0, key, 5, seKey.length);
				if (m_ResultListener.isResultRequired(this, key)) {
					try {
						Map<String, Object> splitEvaluatorResults = new HashMap<String, Object>();
						Map<Metric, MetricScore> userMeasures = new HashMap<Metric, MetricScore>();
						String[] seResultNames = m_SplitEvaluator.getResultNames();
						Object[] seResults = m_SplitEvaluator.getResult(trainingSets[fold][sample], testSets[fold][sample]);
						Object[] results = new Object[seResults.length + 1];
						results[0] = getTimestamp();
						System.arraycopy(seResults, 0, results, 1, seResults.length);
						
						for( int i = 0; i < seResultNames.length; ++i ) {
							splitEvaluatorResults.put( seResultNames[i], seResults[i] );
						}
						
						m_ResultListener.acceptResult(this, key, results);
						
						userMeasures.put( 
							new Metric("build_cpu_time", "openml.evaluation.build_cpu_time(1.0)", null), 
							new MetricScore( (Double) splitEvaluatorResults.get("Elapsed_Time_training") ) );
						userMeasures.put(
							new Metric("predictive_accuracy", "openml.evaluation.predictive_accuracy(1.0)", null), 
							new MetricScore( ((Double) splitEvaluatorResults.get("Percent_correct")) / 100 ) );
						
						if (m_ResultListener instanceof TaskResultListener) {
							// TODO: key[7] can be unstable. Make this more stable. 
							// run - 1 is for 1-based/0-based correction
							((TaskResultListener) m_ResultListener).acceptResultsForSending(  
								m_Task, run - 1, fold, (useSamples ? sample : null), tse.getClassifier(),
								(String) key[7], rowids.get(foldSampleIdx(fold,sample)),tse.recentPredictions(), userMeasures);
						}
					} catch (UnsupportedAttributeTypeException ex) {
						// Save the train and test data sets for debugging purposes?
						Conversion.log( "ERROR", "Perform Run", "Unable to finish " + currentRunRepresentation + ", " + 
								currentFoldRepresentation + " with " + tse.getClassifier().getClass().getName() + ": " + 
								ex.getMessage() );
						if (m_ResultListener instanceof TaskResultListener) {
							((TaskResultListener) m_ResultListener).acceptErrorResult(
								m_Task, tse.getClassifier(), ex.getMessage(), (String) key[7]);
						}
					}
				}
			}
		}
	}

	private int foldSampleIdx(int fold, int sample) {
		return fold * m_NumSamples + sample;
	}
}
