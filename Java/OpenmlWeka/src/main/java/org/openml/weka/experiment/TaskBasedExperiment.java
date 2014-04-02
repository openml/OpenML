package org.openml.weka.experiment;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Array;

import javax.swing.DefaultListModel;

import org.openml.apiconnector.algorithms.SciMark;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.Task.Input.Data_set;
import org.openml.weka.algorithm.InstancesHelper;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ConverterUtils;
import weka.experiment.ClassifierSplitEvaluator;
import weka.experiment.CrossValidationResultProducer;
import weka.experiment.Experiment;
import weka.experiment.PropertyNode;
import weka.experiment.ResultProducer;
import weka.experiment.SplitEvaluator;

public class TaskBasedExperiment extends Experiment {

	private static final long serialVersionUID = 1L;

	/** An array of the Tasks to be executed */
	protected DefaultListModel<Task> m_Tasks = new DefaultListModel<Task>();

	/**
	 * boolean to specify whether this is a plain dataset based experiment, or
	 * an OpenML specific task based experiment
	 */
	protected boolean datasetBasedExperiment = true;

	/** The task currently being used */
	protected Task m_CurrentTask;

	public TaskBasedExperiment() {

	}

	public TaskBasedExperiment(Experiment exp) {
		this.m_ResultListener = exp.getResultListener();
		this.m_ResultProducer = exp.getResultProducer();
		this.m_RunLower = exp.getRunLower();
		this.m_RunUpper = exp.getRunUpper();
		this.m_Datasets = exp.getDatasets();
		this.m_UsePropertyIterator = exp.getUsePropertyIterator();
		this.m_PropertyArray = exp.getPropertyArray();
		this.m_Notes = exp.getNotes();
		// this.m_AdditionalMeasures =
		// this.m_ClassFirst = exp.classFirst(flag)
		this.m_AdvanceDataSetFirst = exp.getAdvanceDataSetFirst();
	}

	public void setMode(boolean datasetBasedExperiment) {
		this.datasetBasedExperiment = datasetBasedExperiment;
	}

	public boolean getMode() {
		return datasetBasedExperiment;
	}

	public DefaultListModel<Task> getTasks() {
		return m_Tasks;
	}

	public void setTasks(DefaultListModel<Task> tasks) {
		m_Tasks = tasks;
	}

	@Override
	public void initialize() throws Exception {
		m_RunNumber = getRunLower();
		m_DatasetNumber = 0;
		m_PropertyNumber = 0;
		m_CurrentProperty = -1;
		m_CurrentInstances = null;
		m_CurrentTask = null;
		m_Finished = false;
		if (m_UsePropertyIterator && (m_PropertyArray == null)) {
			throw new Exception("Null array for property iterator");
		}
		if (getRunLower() > getRunUpper()) {
			throw new Exception(
					"Lower run number is greater than upper run number");
		}
		if (getDatasets().size() == 0 && datasetBasedExperiment) {
			throw new Exception("No datasets have been specified");
		}
		if (getTasks().size() == 0 && datasetBasedExperiment == false) {
			throw new Exception("No tasks have been specified");
		}
		if (m_ResultProducer == null) {
			throw new Exception("No ResultProducer set");
		}
		if (m_ResultListener == null) {
			throw new Exception("No ResultListener set");
		}

		m_ResultProducer.setResultListener(m_ResultListener);
		m_ResultProducer.setAdditionalMeasures(m_AdditionalMeasures);
		m_ResultProducer.preProcess();

		// constrain the additional measures to be only those allowable
		// by the ResultListener
		String[] columnConstraints = m_ResultListener
				.determineColumnConstraints(m_ResultProducer);

		if (columnConstraints != null) {
			m_ResultProducer.setAdditionalMeasures(columnConstraints);
		}
	}

	@Override
	public void nextIteration() throws Exception {
		if (m_UsePropertyIterator) {
			if (m_CurrentProperty != m_PropertyNumber) {
				setProperty(0, m_ResultProducer);
				m_CurrentProperty = m_PropertyNumber;
			}
		}

		if (datasetBasedExperiment) {

			if (m_CurrentInstances == null) {
				File currentFile = (File) getDatasets().elementAt(
						m_DatasetNumber);
				AbstractFileLoader loader = ConverterUtils
						.getLoaderForFile(currentFile);
				loader.setFile(currentFile);
				Instances data = new Instances(loader.getDataSet());
				// only set class attribute if not already done by loader
				if (data.classIndex() == -1) {
					if (m_ClassFirst) {
						data.setClassIndex(0);
					} else {
						data.setClassIndex(data.numAttributes() - 1);
					}
				}
				m_CurrentInstances = data;
				m_ResultProducer.setInstances(m_CurrentInstances);
			}
		} else {
			if (m_CurrentTask == null) {
				m_CurrentTask = getTasks().elementAt(m_DatasetNumber);

				Data_set ds = TaskInformation.getSourceData(m_CurrentTask);
				DataSetDescription dsd = TaskInformation.getSourceData(
						m_CurrentTask).getDataSetDescription();
				Instances instDataset = new Instances(new FileReader(
						dsd.getDataset()));

				InstancesHelper.setTargetAttribute(instDataset,
						ds.getTarget_feature());

				((TaskResultProducer) m_ResultProducer).setTask(m_CurrentTask);
				this.setRunUpper(TaskInformation
						.getNumberOfRepeats(m_CurrentTask));
				m_ResultProducer.setInstances(instDataset);
			}
		}

		m_ResultProducer.doRun(m_RunNumber);

		advanceCounters();
	}

	@Override
	public void advanceCounters() {
		Integer subjectsInExperiment = (datasetBasedExperiment) ? getDatasets()
				.size() : getTasks().size();

		if (m_AdvanceDataSetFirst) {
			m_RunNumber++;
			if (m_RunNumber > getRunUpper()) {
				m_RunNumber = getRunLower();
				m_DatasetNumber++;
				m_CurrentInstances = null;
				m_CurrentTask = null;
				if (m_DatasetNumber >= subjectsInExperiment) {
					m_DatasetNumber = 0;
					if (m_UsePropertyIterator) {
						m_PropertyNumber++;
						if (m_PropertyNumber >= Array
								.getLength(m_PropertyArray)) {
							m_Finished = true;
						}
					} else {
						m_Finished = true;
					}
				}
			}
		} else { // advance by custom iterator before data set
			m_RunNumber++;
			if (m_RunNumber > getRunUpper()) {
				m_RunNumber = getRunLower();
				if (m_UsePropertyIterator) {
					m_PropertyNumber++;
					if (m_PropertyNumber >= Array.getLength(m_PropertyArray)) {
						m_PropertyNumber = 0;
						m_DatasetNumber++;
						m_CurrentInstances = null;
						m_CurrentTask = null;
						if (m_DatasetNumber >= subjectsInExperiment) {
							m_Finished = true;
						}
					}
				} else {
					m_DatasetNumber++;
					m_CurrentInstances = null;
					m_CurrentTask = null;
					if (m_DatasetNumber >= subjectsInExperiment) {
						m_Finished = true;
					}
				}
			}
		}
	}

	/**
	 * Parses a given list of options.
	 * 
	 * <pre>
	 * -T &lt;task_id&gt;
	 *  The OpenML task to run the experiment on. (required)
	 * </pre>
	 * 
	 * 
	 * <pre>
	 * -C &lt;class name&gt;
	 *  The full class name of the classifier.
	 *  eg: weka.classifiers.bayes.NaiveBayes
	 * </pre>
	 * 
	 * <!-- options-end -->
	 * 
	 * All options after -- will be passed to the classifier.
	 * <p>
	 * 
	 * @param options
	 *            the list of options as an array of strings
	 * @throws Exception
	 *             if an option is not supported
	 */
	public void setOptions(String[] options) throws Exception {

		Integer task_id = Integer.parseInt(Utils.getOption('T', options));
		String classifierName = Utils.getOption('C', options);
		String[] classifierOptions = Utils.partitionOptions(options);

		DefaultListModel<Task> tasks = new DefaultListModel<Task>();
		tasks.add(0, ApiConnector.openmlTasksSearch(task_id));
		setTasks(tasks);

		Classifier[] cArray = new Classifier[1];
		cArray[0] = (Classifier) Utils.forName(Classifier.class,
				classifierName, classifierOptions);
		setPropertyArray(cArray);
	}

	public static void main(String[] args) {
		try {
			TaskBasedExperiment exp = new TaskBasedExperiment();
			ResultProducer rp = new TaskResultProducer();
			TaskResultListener rl = new TaskResultListener(new SciMark());
			SplitEvaluator se = new TaskSplitEvaluator();
			Classifier sec = null;

			Config openmlconfig = new Config();

			if (rl.acceptCredentials(openmlconfig.getUsername(),
					openmlconfig.getPassword()) == false)
				throw new Exception(
						"Please provide correct credentials in a config file (openml.conf)");

			exp.setMode(false);

			exp.setResultProducer(rp);
			exp.setResultListener(rl);
			exp.setUsePropertyIterator(true);

			sec = ((ClassifierSplitEvaluator) se).getClassifier();
			PropertyNode[] propertyPath = new PropertyNode[2];
			try {
				propertyPath[0] = new PropertyNode(se, new PropertyDescriptor(
						"splitEvaluator", CrossValidationResultProducer.class),
						CrossValidationResultProducer.class);
				propertyPath[1] = new PropertyNode(sec, new PropertyDescriptor(
						"classifier", se.getClass()), se.getClass());
			} catch (IntrospectionException err) {
				err.printStackTrace();
			}
			exp.setPropertyPath(propertyPath);

			exp.setOptions(args);

			System.err.println("Initializing...");
			exp.initialize();
			System.err.println("Iterating...");
			exp.runExperiment();
			System.err.println("Postprocessing...");
			exp.postProcess();
			System.err.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
