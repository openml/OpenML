package org.openml.weka.experiment;

import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.models.MetricScore;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.Task.Input.Data_set;
import org.openml.apiconnector.xml.Task.Input.Estimation_procedure;
import org.openml.weka.algorithm.DataSplits;
import org.openml.weka.algorithm.InstancesHelper;
import org.openml.weka.algorithm.OptimizationTrace;
import org.openml.weka.algorithm.WekaAlgorithm;
import org.openml.weka.algorithm.OptimizationTrace.Quadlet;
import org.openml.weka.algorithm.WekaConfig;

import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.UnsupportedAttributeTypeException;
import weka.experiment.CrossValidationResultProducer;
import weka.experiment.OutputZipper;

public class TaskResultProducer extends CrossValidationResultProducer {

	private static UserMeasures[] USER_MEASURES = {
			new UserMeasures("predictive_accuracy", "Percent_correct", .01),
			new UserMeasures("kappa", "Kappa_statistic"),
			new UserMeasures("root_mean_squared_error", "Root_mean_squared_error"),
			new UserMeasures("root_relative_squared_error", "Root_relative_squared_error", .01),
			new UserMeasures("usercpu_time_millis_training", "UserCPU_Time_millis_training"),
			new UserMeasures("usercpu_time_millis_testing", "UserCPU_Time_millis_testing"),
			new UserMeasures("usercpu_time_millis", "UserCPU_Time_millis"), };

	private static final long serialVersionUID = 1L;

	public static final String TASK_FIELD_NAME = "OpenML_Task_id";
	public static final String SAMPLE_FIELD_NAME = "Sample";
	
	/** The task to be run */
	protected Task m_Task;
	protected boolean regressionTask;
	protected boolean missingLabels;
	
	/** Object representing the datasplits **/
	protected DataSplits m_DataSplits;

	/** Number of samples, if applicable **/
	protected int m_NumSamples = 1; // default to 1

	/** Current task information string **/
	protected String currentTaskRepresentation = "";

	protected OpenmlConnector apiconnector;
	protected WekaConfig openmlconfig;

	public TaskResultProducer(OpenmlConnector apiconnector, WekaConfig openmlconfig) {
		super();
		this.m_SplitEvaluator = new OpenmlClassificationSplitEvaluator();
		this.apiconnector = apiconnector;
		this.openmlconfig = openmlconfig;
	}

	public void setTask(Task t) throws Exception {
		m_Task = t;

		regressionTask = t.getTask_type_id() == 2;

		if (regressionTask) {
			throw new Exception("OpenML Plugin Exception: Regression tasks currently not supported. Aborting.");
		}

		/*
		 * if( regressionTask && !( m_SplitEvaluator instanceof
		 * OpenmlRegressionSplitEvaluator ) ) { m_SplitEvaluator = new
		 * OpenmlRegressionSplitEvaluator(); } else if( !( m_SplitEvaluator
		 * instanceof OpenmlClassificationSplitEvaluator ) ) { m_SplitEvaluator
		 * = new OpenmlClassificationSplitEvaluator(); }
		 */
		
		m_Instances = InstancesHelper.getDatasetFromTask(apiconnector, m_Task);
		Data_set ds = TaskInformation.getSourceData(m_Task);
		int targetAttributeIndex = InstancesHelper.getAttributeIndex(m_Instances, ds.getTarget_feature());
		AttributeStats targetStats = m_Instances.attributeStats(targetAttributeIndex);
		
		Estimation_procedure ep = TaskInformation.getEstimationProcedure(m_Task);
		Instances splits = new Instances(new FileReader(ep.getDataSplits(m_Task.getTask_id())));

		missingLabels = targetStats.missingCount > 0;
		m_DataSplits = new DataSplits(m_Task, m_Instances, splits);
		m_NumFolds = m_DataSplits.FOLDS;
		m_NumSamples = m_DataSplits.SAMPLES;

		currentTaskRepresentation = "Task " + m_Task.getTask_id() + " (" + TaskInformation.getSourceData(m_Task).getDataSetDescription(apiconnector).getName() + ")";
	}
	
	public Object getSplitEvaluatorKey(int index) {
		return m_SplitEvaluator.getKey()[index];
	}

	@Override
	public void setInstances(Instances m_Instances) {
		throw new RuntimeException("TaskResultProducer Exception: function setInstances may not be invoked. Use setTask instead. ");
	}

	public void doFullRun() throws Exception {
		Conversion.log("OK", "Total Model", "Started building a model over the full dataset. ");
		OpenmlSplitEvaluator tse = ((OpenmlSplitEvaluator) m_SplitEvaluator);

		Map<String, Object> splitEvaluatorResults = WekaAlgorithm.splitEvaluatorToMap(tse, tse.getResult(m_Instances, m_Instances));
		
		if (m_ResultListener instanceof TaskResultListener) {
			((TaskResultListener) m_ResultListener).acceptFullModel(m_Task, m_Instances, tse.getClassifier(), (String) tse.getKey()[1], splitEvaluatorResults, tse);
		}
		
		Conversion.log("OK", "Total Model", "Done building full dataset model. ");
	}

	@Override
	public void doRun(int run) throws Exception {
		OpenmlSplitEvaluator tse = ((OpenmlSplitEvaluator) m_SplitEvaluator);
		String currentRunRepresentation = currentTaskRepresentation + " with " + (String) tse.getKey()[0] + " - Repeat " + (run - 1);
		Conversion.log("OK", "Attribtes", "Attributes available: " + InstancesHelper.getAttributes(m_Instances));
		Conversion.log("OK", "Class", "Class attribute: " + m_Instances.classAttribute());
		
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

		int repeat = run - 1; // 0/1 based
		for (int fold = 0; fold < m_NumFolds; fold++) {
			for (int sample = 0; sample < m_NumSamples; ++sample) {
				// Add in some fields to the key like run and fold number, data
				// set, name
				String currentFoldRepresentation = "fold " + fold + ", sample " + sample;
				Conversion.log("INFO", "Perform Run", "Started on performing " + currentRunRepresentation + ", " + currentFoldRepresentation);

				Map<String, MetricScore> userMeasures = new HashMap<String, MetricScore>();
				
				Instances train = m_DataSplits.getTrainingSet(repeat, fold, sample);
				Instances test = m_DataSplits.getTestSet(repeat, fold, sample);

				try {
					Object[] seResults = tse.getResult(train, test);
					Object[] results = new Object[seResults.length + 1];
					results[0] = getTimestamp();
					System.arraycopy(seResults, 0, results, 1, seResults.length);

					Map<String, Object> splitEvaluatorResults = WekaAlgorithm.splitEvaluatorToMap(tse, seResults);
					List<Quadlet<String, Double, List<Entry<String, Object>>, Boolean>> trace = null;
					try {
						trace = OptimizationTrace.extractTrace(tse.getClassifier());

						Conversion.log("OK", "Trace", "Found MultiSearch or FilteredClassifier(MultiSearch). Extracting trace. ");
					} catch (Exception e) {
						// This is totally OK, no need to catch this
						
					}

					// just adding an additional measure: UserCPU_Time_millis
					// (total training time + test time)
					if (splitEvaluatorResults.containsKey("UserCPU_Time_millis_training") && splitEvaluatorResults.containsKey("UserCPU_Time_millis_testing")) {
						double traintime = (Double) splitEvaluatorResults.get("UserCPU_Time_millis_training");
						double testtime = (Double) splitEvaluatorResults.get("UserCPU_Time_millis_testing");
						splitEvaluatorResults.put("UserCPU_Time_millis", traintime + testtime);
					}

					if (missingLabels == false) {
						for (UserMeasures um : USER_MEASURES) {
							if (splitEvaluatorResults.containsKey(um.wekaFunctionName)) {
								userMeasures.put(um.openmlFunctionName, new MetricScore(((Double) splitEvaluatorResults.get(um.wekaFunctionName)) * um.factor, test.size()));
							}
						}
					}
					
					boolean modelFullDataset = openmlconfig.getModelFullDataset();
					if (m_ResultListener instanceof TaskResultListener) {
						((TaskResultListener) m_ResultListener).acceptResultsForSending(m_Task, m_Instances, repeat, fold, sample,
								tse.getClassifier(), (String) tse.getKey()[1], m_DataSplits.getTestSetRowIds(repeat, fold, sample), tse.recentPredictions(), userMeasures,
								trace, modelFullDataset);
					}
				} catch (UnsupportedAttributeTypeException ex) {
					// Save the train and test data sets for debugging purposes?
					Conversion.log("ERROR", "Perform Run", "Unable to finish " + currentRunRepresentation + ", " + currentFoldRepresentation + " with "
							+ tse.getClassifier().getClass().getName() + ": " + ex.getMessage());
					if (m_ResultListener instanceof TaskResultListener) {
						((TaskResultListener) m_ResultListener).acceptErrorResult(m_Task, m_Instances, tse.getClassifier(), ex.getMessage(), (String) tse.getKey()[1]);
					}
				}
			}
		}
	}

	private static class UserMeasures {
		private final String openmlFunctionName;
		private final String wekaFunctionName;
		private final double factor;

		private UserMeasures(String openmlFunctionName, String wekaFunctionName, double factor) {
			this.openmlFunctionName = openmlFunctionName;
			this.wekaFunctionName = wekaFunctionName;
			this.factor = factor;
		}

		private UserMeasures(String openmlFunctionName, String wekaFunctionName) {
			this(openmlFunctionName, wekaFunctionName, 1.0D);
		}
	}
}
