package org.openml.weka.experiment;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Array;

import javax.swing.DefaultListModel;

import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.Task.Input.Data_set;
import org.openml.weka.algorithm.InstancesHelper;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.Utils;
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

	/** The task currently being used */
	protected Task m_CurrentTask;
	
	protected final OpenmlConnector apiconnector;

	public TaskBasedExperiment(Experiment exp, OpenmlConnector apiconnector) {
		this.m_ResultListener = exp.getResultListener();
		this.m_ResultProducer = exp.getResultProducer();
		this.m_RunLower = exp.getRunLower();
		this.m_RunUpper = exp.getRunUpper();
		this.m_Datasets = exp.getDatasets();
		this.m_UsePropertyIterator = true;
		this.m_PropertyArray = exp.getPropertyArray();
		this.m_Notes = exp.getNotes();
		// this.m_AdditionalMeasures =
		// this.m_ClassFirst = exp.classFirst(flag)
		this.m_AdvanceDataSetFirst = exp.getAdvanceDataSetFirst();
		
		this.apiconnector = apiconnector;
	}

	public DefaultListModel<Task> getTasks() {
		return m_Tasks;
	}

	public void setTasks(DefaultListModel<Task> tasks) {
		m_Tasks = tasks;
	}
	
	// TODO: dummy function for compatibility with Weka's RunPanel
	public DefaultListModel<File> getDatasets() {
		DefaultListModel<File> datasets = new DefaultListModel<File>();
		for (int i = 0; i < m_Tasks.size(); ++i) {
			datasets.add(i, new File("Task_" + m_Tasks.get(i).getTask_id() + ".arff"));
		}
		return datasets;
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
			throw new Exception("Lower run number is greater than upper run number");
		}
		if (getTasks().size() == 0) {
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
		String[] columnConstraints = m_ResultListener.determineColumnConstraints(m_ResultProducer);

		if (columnConstraints != null) {
			m_ResultProducer.setAdditionalMeasures(columnConstraints);
		}
	}

	@Override
	public void nextIteration() throws Exception {
		
		if (m_CurrentTask == null) {
			m_CurrentTask = (Task) getTasks().elementAt(m_DatasetNumber);

			Data_set ds = TaskInformation.getSourceData(m_CurrentTask);
			DataSetDescription dsd = TaskInformation.getSourceData(m_CurrentTask).getDataSetDescription(apiconnector);
			Instances instDataset = new Instances(new FileReader(dsd.getDataset(apiconnector.getApiKey())));

			InstancesHelper.setTargetAttribute(instDataset,ds.getTarget_feature());

			((TaskResultProducer) m_ResultProducer).setTask(m_CurrentTask);
			this.setRunUpper(TaskInformation.getNumberOfRepeats(m_CurrentTask));
			m_ResultProducer.setInstances(instDataset);
			
			// set classifier. Important, since by alternating between regression and 
			// classification tasks we possibly have resetted the splitevaluator
			
			System.err.println(((TaskResultProducer)m_ResultProducer).getSplitEvaluator().getClass().toString() );
			
			if (m_UsePropertyIterator) {
				setProperty(0, m_ResultProducer);
				m_CurrentProperty = m_PropertyNumber;
			}

		}

		m_ResultProducer.doRun(m_RunNumber);

		advanceCounters();
	}

	@Override
	public void advanceCounters() {

		if (m_AdvanceDataSetFirst) {
			m_RunNumber++;
			if (m_RunNumber > getRunUpper()) {
				m_RunNumber = getRunLower();
				m_DatasetNumber++;
				m_CurrentInstances = null;
				m_CurrentTask = null;
				if (m_DatasetNumber >= getTasks().size()) {
					m_DatasetNumber = 0;
					if (m_UsePropertyIterator) {
						m_PropertyNumber++;
						if (m_PropertyNumber >= Array.getLength(m_PropertyArray)) {
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
						if (m_DatasetNumber >= getTasks().size()) {
							m_Finished = true;
						}
					}
				} else {
					m_DatasetNumber++;
					m_CurrentInstances = null;
					m_CurrentTask = null;
					if (m_DatasetNumber >= getTasks().size()) {
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
		tasks.add(0, apiconnector.taskGet(task_id));
		setTasks(tasks);

		Classifier[] cArray = new Classifier[1];
		try{
			cArray[0] = (Classifier) Utils.forName(Classifier.class,
				classifierName, classifierOptions);
		} catch(Exception e){
			// Try again, this time loading packages first
			weka.core.WekaPackageManager.loadPackages(false);
			cArray[0] = (Classifier) Utils.forName(Classifier.class,
					classifierName, classifierOptions);
		}
		setPropertyArray(cArray);
	}

	public static void main(String[] args) {
		try {
			Config openmlconfig = new Config();
			
			if (openmlconfig.getApiKey() == null) {
				throw new Exception("No Api Key provided in config file. ");
			}
			
			OpenmlConnector apiconnector;
			if( openmlconfig.getServer() != null ) {
				apiconnector = new OpenmlConnector( openmlconfig.getServer(), openmlconfig.getApiKey() );
			} else { 
				apiconnector = new OpenmlConnector( openmlconfig.getApiKey() );
			}
			
			TaskBasedExperiment exp = new TaskBasedExperiment( new Experiment(), apiconnector );
			ResultProducer rp = new TaskResultProducer(apiconnector);
			TaskResultListener rl = new TaskResultListener(apiconnector, openmlconfig);
			SplitEvaluator se = new OpenmlClassificationSplitEvaluator();
			Classifier sec = null;

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
