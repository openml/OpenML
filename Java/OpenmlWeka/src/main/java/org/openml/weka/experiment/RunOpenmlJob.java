package org.openml.weka.experiment;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import javax.swing.DefaultListModel;

import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.Task;
import org.openml.weka.algorithm.WekaConfig;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.CommandlineRunnable;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.experiment.ClassifierSplitEvaluator;
import weka.experiment.CrossValidationResultProducer;
import weka.experiment.Experiment;
import weka.experiment.PropertyNode;
import weka.experiment.ResultProducer;
import weka.experiment.SplitEvaluator;

public class RunOpenmlJob implements CommandlineRunnable {

	public static void main(String[] args) throws Exception {
		RunOpenmlJob rj = new RunOpenmlJob();
		rj.run(rj, args);
	}
	
	public static int executeTask(OpenmlConnector openml, WekaConfig config, Integer task_id, Classifier classifier) throws Exception {
		TaskBasedExperiment exp = new TaskBasedExperiment(new Experiment(), openml, config);
		ResultProducer rp = new TaskResultProducer(openml, config);
		TaskResultListener rl = new TaskResultListener(openml, config);
		SplitEvaluator se = new OpenmlClassificationSplitEvaluator();
		Classifier sec = null;

		exp.setResultProducer(rp);
		exp.setResultListener(rl);
		exp.setUsePropertyIterator(true);

		sec = ((ClassifierSplitEvaluator) se).getClassifier();
		PropertyNode[] propertyPath = new PropertyNode[2];
		try {
			propertyPath[0] = new PropertyNode(se, new PropertyDescriptor("splitEvaluator", CrossValidationResultProducer.class),
					CrossValidationResultProducer.class);
			propertyPath[1] = new PropertyNode(sec, new PropertyDescriptor("classifier", se.getClass()), se.getClass());
		} catch (IntrospectionException err) {
			err.printStackTrace();
		}
		exp.setPropertyPath(propertyPath);
		
		// set classifier
		Classifier[] cArray = {classifier};
		exp.setPropertyArray(cArray);
		
		// set task
		DefaultListModel<Task> tasks = new DefaultListModel<Task>();
		tasks.add(0, openml.taskGet(task_id));
		exp.setTasks(tasks);
		
		// run the stuff
		System.err.println("Initializing...");
		exp.initialize();
		System.err.println("Iterating...");
		exp.runExperiment();
		System.err.println("Postprocessing...");
		exp.postProcess();
		System.err.println("Done");
		
		int runId = ((TaskResultListener) exp.getResultListener()).getRunIds().get(0);
		return runId;
	}
	
	@Override
	public void run(Object arg0, String[] args) throws Exception {
		String strTaskid;
		
		String strConfig;
		WekaConfig config;
		
		try { strConfig = Utils.getOption("config", args); } catch (Exception e) { strConfig = null; }
		if (strConfig != null & strConfig.equals("") == false) {
			config = new WekaConfig(strConfig);
		} else {
			config = new WekaConfig();
		}
		
		OpenmlConnector apiconnector;

		String username = config.getApiKey();
		String server = config.getServer();

		if (server != null) {
			apiconnector = new OpenmlConnector(server, username);
		} else {
			apiconnector = new OpenmlConnector(username);
		}

		try {
			strTaskid = Utils.getOption("task_id", args);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		
		String classifierCliString = Utils.getOption('C', args);
	    if (classifierCliString.length() == 0) {
	      throw new Exception("A classifier must be specified with the -C option.");
		}
		String[] classifierOptions = Utils.splitOptions(classifierCliString);
		// Do it first without options, so if an exception is thrown during
		// the option setting, listOptions will contain options for the actual
		// Classifier.

		Classifier classifier;
		try {
			classifier = (AbstractClassifier.forName(classifierOptions[0], null));
			classifierOptions[0] = "";
		} catch (Exception e) {
			weka.core.WekaPackageManager.loadPackages(false);
			classifier = (AbstractClassifier.forName(classifierOptions[0], null));
			classifierOptions[0] = "";
		}
		
		if (classifier instanceof OptionHandler) {
			((OptionHandler) classifier).setOptions(classifierOptions);
		}

		executeTask(apiconnector, config, Integer.parseInt(strTaskid), classifier);
	}

	@Override
	public void postExecution() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preExecution() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
