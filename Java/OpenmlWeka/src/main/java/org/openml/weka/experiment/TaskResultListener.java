package org.openml.weka.experiment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.SciMark;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.io.ApiException;
import org.openml.apiconnector.models.Metric;
import org.openml.apiconnector.models.MetricScore;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.settings.Constants;
import org.openml.apiconnector.xml.Flow;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.Run.Parameter_setting;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.Task.Output.Predictions.Feature;
import org.openml.apiconnector.xml.UploadRun;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.openml.weka.algorithm.OptimizationTrace;
import org.openml.weka.algorithm.WekaAlgorithm;
import org.openml.weka.algorithm.OptimizationTrace.Quadlet;

import com.thoughtworks.xstream.XStream;

import weka.classifiers.Classifier;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.RevisionHandler;
import weka.core.Utils;
import weka.core.Version;
import weka.experiment.InstancesResultListener;

public class TaskResultListener extends InstancesResultListener {

	private static final long serialVersionUID = 7230120341L;
	
	private static final String[] DEFAULT_TAGS = {"weka", "weka_" + Version.VERSION };
	
	/** List of OpenML tasks currently being solved. Folds/repeats are gathered */
	private final Map<String, OpenmlExecutedTask> currentlyCollecting;

	/**
	 * List of OpenML tasks that reported back with errors. Will be send to
	 * server with error message
	 */
	private final ArrayList<String> tasksWithErrors;

	private final OpenmlConnector apiconnector;
	
	private final String[] all_tags;
	
	public TaskResultListener(OpenmlConnector apiconnector, Config config) {
		super();
		
		this.apiconnector = apiconnector;
		currentlyCollecting = new HashMap<String, OpenmlExecutedTask>();
		tasksWithErrors = new ArrayList<String>();
		all_tags = ArrayUtils.addAll(DEFAULT_TAGS, config.getTags());
	}

	public void acceptResultsForSending(Task t, Instances sourceData, Integer repeat, Integer fold, Integer sample,
			Classifier classifier, String options,
			Integer[] rowids, ArrayList<Prediction> predictions, Map<Metric, MetricScore> userMeasures, List<Quadlet<String,Double,List<Entry<String,String>>,Boolean>> optimizationTrace) throws Exception {
		// TODO: do something better than undefined
		String revision = (classifier instanceof RevisionHandler) ? ((RevisionHandler)classifier).getRevision() : "undefined"; 
		String implementationId = classifier.getClass().getName() + "(" + revision + ")";
		String key = t.getTask_id() + "_" + implementationId + "_" + options;
		if (currentlyCollecting.containsKey(key) == false) {
			currentlyCollecting.put(key, new OpenmlExecutedTask(t,
					classifier, sourceData, null, options, apiconnector, all_tags ));
		}
		OpenmlExecutedTask oet = currentlyCollecting.get(key);
		oet.addBatchOfPredictions(fold, repeat, sample, rowids, predictions, optimizationTrace);
		oet.addUserDefinedMeasures(fold, repeat, sample, userMeasures);

		if (oet.complete()) {
			oet.prepareForSending();
			sendTask(oet);
			currentlyCollecting.remove(key);
		}
	}

	public void acceptErrorResult(Task t, Instances sourceData, Classifier classifier, String error_message, String options)
			throws Exception {
		// TODO: do something better than undefined
		String revision = (classifier instanceof RevisionHandler) ? ((RevisionHandler)classifier).getRevision() : "undefined"; 		
		String implementationId = classifier.getClass().getName() + "(" + revision + ")";
		String key = t.getTask_id() + "_" + implementationId + "_" + options;

		if (tasksWithErrors.contains(key) == false) {
			tasksWithErrors.add(key);
			sendTaskWithError(new OpenmlExecutedTask(t, classifier, sourceData,
				error_message, options, apiconnector, all_tags ));
		}
	}

	private void sendTask(OpenmlExecutedTask oet) throws Exception {
		Conversion.log( "INFO", "Upload Run", "Starting send run process... " );
		XStream xstream = XstreamXmlMapping.getInstance();
		File tmpPredictionsFile;
		File tmpDescriptionFile;
		
		// also add information about CPU performance and OS to run:
		SciMark benchmarker = SciMark.getInstance();
		oet.getRun().addOutputEvaluation("os_information", "openml.userdefined.os_information(1.0)", null, "[" + StringUtils.join( benchmarker.getOsInfo(), ", " ) + "]"  );
		oet.getRun().addOutputEvaluation("scimark_benchmark", "openml.userdefined.scimark_benchmark(1.0)", benchmarker.getResult(), "[" + StringUtils.join( benchmarker.getStringArray(), ", " ) + "]" );
		
		tmpPredictionsFile = Conversion.stringToTempFile(
				oet.getPredictions().toString(), "weka_generated_predictions", Constants.DATASET_FORMAT);
		tmpDescriptionFile = Conversion.stringToTempFile(
				xstream.toXML(oet.getRun()), "weka_generated_run", "xml");
		Map<String, File> output_files = new HashMap<String, File>();
		
		output_files.put("predictions", tmpPredictionsFile);
		if(oet.serializedClassifier != null ) { output_files.put("model_serialized", oet.serializedClassifier); }
		if(oet.humanReadableClassifier != null ) { output_files.put("model_readable", oet.humanReadableClassifier); }
		if(oet.optimizationTrace != null) { 
			output_files.put("trace", Conversion.stringToTempFile(oet.optimizationTrace.toString(), "optimization_trace", "arff"));
		}
		
		try { 
			UploadRun ur = apiconnector.runUpload(tmpDescriptionFile, output_files );
			Conversion.log( "INFO", "Upload Run", "Run was uploaded with rid " + ur.getRun_id() + 
					". Obtainable at " + apiconnector.getApiUrl() + "run/" + ur.getRun_id() );
		} catch( ApiException ae ) {
			ae.printStackTrace(); 
			Conversion.log( "ERROR", "Upload Run", "Failed to upload run: " + ae.getMessage() );
		}
	}

	private void sendTaskWithError(OpenmlExecutedTask oet) throws Exception {
		Conversion.log( "WARNING", "Upload Run", "Starting to upload run... (including error results) " );
		XStream xstream = XstreamXmlMapping.getInstance();
		File tmpDescriptionFile;
		
		tmpDescriptionFile = Conversion.stringToTempFile(
				xstream.toXML(oet.getRun()), "weka_generated_run", Constants.DATASET_FORMAT);
		try { 
			UploadRun ur = apiconnector.runUpload(tmpDescriptionFile, new HashMap<String, File>() );
			Conversion.log( "WARNING", "Upload Run", "Run was uploaded with rid " + ur.getRun_id() + 
					". It includes an error message. Obtainable at " + 
					apiconnector.getApiUrl() +  "?f=openml.run.get&run_id=" + ur.getRun_id() );
		} catch( ApiException ae ) { 
			ae.printStackTrace(); 
			Conversion.log( "ERROR", "Upload Run", "Failed to upload run: " + ae.getMessage() );
		}
	}

	private class OpenmlExecutedTask {
		private final boolean isRegression;
		private int task_id;
		private Task task;
		private Classifier classifier;
		private Instances predictions;
		private Instances inputData;
		private Instances optimizationTrace;
		private int nrOfResultBatches;
		private final int nrOfExpectedResultBatches;
		private String[] classnames;
		private Run run;
		private int implementation_id;
		
		private int repeats;
		private int samples;
		
		private File serializedClassifier = null;
		private File humanReadableClassifier = null;

		public OpenmlExecutedTask(Task t, Classifier classifier, Instances sourceData,
				String error_message, String options, OpenmlConnector apiconnector,
				String[] tags ) throws Exception {
			this.classifier = classifier;
			this.task = t;
			
			// TODO: instable. Do better
			isRegression = t.getTask_type().equals("Supervised Regression");
			inputData = sourceData;
			optimizationTrace = null;
			
			if( !isRegression ) {
				classnames = TaskInformation.getClassNames(apiconnector, this.task);
			}
			task_id = this.task.getTask_id();
			
			repeats = 1;
			int folds = 1;
			samples = 1;
			try {repeats = TaskInformation.getNumberOfRepeats(t);} catch( Exception e ){};
			try {folds = TaskInformation.getNumberOfFolds(t);} catch( Exception e ){};
			try {samples = TaskInformation.getNumberOfSamples(t);} catch( Exception e ){};
			
			nrOfExpectedResultBatches = repeats * folds * samples;
			nrOfResultBatches = 0;
			ArrayList<Attribute> attInfo = new ArrayList<Attribute>();
			for (Feature f : TaskInformation.getPredictions(t).getFeatures()) {
				if (f.getName().equals("confidence.classname")) {
					for (String s : TaskInformation.getClassNames(apiconnector, t)) {
						attInfo.add(new Attribute("confidence." + s));
					}
				} else if (f.getName().equals("prediction")) {
					if(isRegression) {
						attInfo.add(new Attribute("prediction"));
					} else {
						List<String> values = new ArrayList<String>(classnames.length);
						for (String classname : classnames)
							values.add(classname);
						attInfo.add(new Attribute(f.getName(), values));
					}
				} else {
					attInfo.add(new Attribute(f.getName()));
				}
			}
			
			attInfo.add( inputData.classAttribute().copy("correct") );
			
			
			predictions = new Instances("openml_task_" + t.getTask_id() + "_predictions", attInfo, 0);
			
			
			Flow find = WekaAlgorithm.create(classifier.getClass().getName(), options, tags);
			
			implementation_id = WekaAlgorithm.getImplementationId(find, classifier, apiconnector);
			Flow implementation = apiconnector.flowGet(implementation_id);
			
			String setup_string = classifier.getClass().getName();
			if(options.equals("") == false) setup_string += (" -- " + options);
			
			String[] params = Utils.splitOptions(options);
			List<Parameter_setting> list = WekaAlgorithm.getParameterSetting(params, implementation);
			
			run = new Run(t.getTask_id(), error_message, implementation.getId(), setup_string, list.toArray(new Parameter_setting[list.size()]), tags);
		}
		
		public void addBatchOfPredictions(Integer fold, Integer repeat, Integer sample, Integer[] rowids, ArrayList<Prediction> batchPredictions, List<Quadlet<String,Double,List<Entry<String,String>>,Boolean>> optimizationTraceFold) {
			nrOfResultBatches += 1;
			for (int i = 0; i < rowids.length; ++i) {
				Prediction current = batchPredictions.get(i);
				double[] values = new double[predictions.numAttributes()];
				values[predictions.attribute("row_id").index()] = rowids[i];
				values[predictions.attribute("fold").index()] = fold;
				values[predictions.attribute("repeat").index()] = repeat; 
				values[predictions.attribute("prediction").index()] = current.predicted();
				if(predictions.attribute("sample") != null) {
					values[predictions.attribute("sample").index()] = sample;
				}
				values[predictions.attribute("correct").index()] = inputData.instance(rowids[i]).classValue();
				
				
				if (current instanceof NominalPrediction) {
					double[] confidences = ((NominalPrediction) current).distribution();
					for (int j = 0; j < confidences.length; ++j) {
						values[predictions.attribute("confidence." + classnames[j]).index()] = confidences[j];
					}
				}

				predictions.add(new DenseInstance(1.0D, values));
			}
			
			// add trace
			if (optimizationTraceFold != null) {
				this.optimizationTrace = OptimizationTrace.addTraceToDataset(this.optimizationTrace, optimizationTraceFold, task_id, repeat, fold);
			}
		}
		
		public void addUserDefinedMeasures(Integer fold, Integer repeat, Integer sample, Map<Metric, MetricScore> userMeasures) throws Exception {
			// attach fold/sample specific user measures to run
			for( Metric m : userMeasures.keySet() ) {
				MetricScore score = userMeasures.get(m);
				
				getRun().addOutputEvaluation(m.name, repeat, fold, sample, m.implementation, score.getScore() );
			}
		}
		
		public void prepareForSending() {
			// build model for entire data set. This can take some time
			try {
				Conversion.log( "OK", "Total Model", "Started building a model over the full dataset. " );
				
				OpenmlSplitEvaluator ose = new OpenmlClassificationSplitEvaluator(); // TODO: regression
				ose.setClassifier(classifier);
				
				Map<String, Object> splitEvaluatorResults = WekaAlgorithm.splitEvaluatorToMap(ose, ose.getResult(inputData, inputData) );
				Classifier classifierModel = ose.getClassifier();
				String keyTraining = "UserCPU_Time_millis_training";
				String keyTesting  = "UserCPU_Time_millis_testing";
				
				if(splitEvaluatorResults.containsKey(keyTraining) && splitEvaluatorResults.containsKey(keyTesting)) {
					Double totalTimeTraining = (Double) splitEvaluatorResults.get(keyTraining);
					Double totalTimeTesting  = (Double) splitEvaluatorResults.get(keyTesting);
					
					getRun().addOutputEvaluation(keyTesting.toLowerCase(),  "openml.evaluation."+keyTesting.toLowerCase()+"(1.0)", totalTimeTesting, null);
					getRun().addOutputEvaluation(keyTraining.toLowerCase(), "openml.evaluation."+keyTraining.toLowerCase()+"(1.0)", totalTimeTraining, null);
					getRun().addOutputEvaluation("usercpu_time_millis", "openml.evaluation.usercpu_time_millis(1.0)", totalTimeTraining + totalTimeTesting, null);
				}
				humanReadableClassifier = Conversion.stringToTempFile(classifierModel.toString(), "WekaModel_" + classifierModel.getClass().getName(), "model");
				serializedClassifier = WekaAlgorithm.classifierSerializedToFile(classifierModel, task_id);
				Conversion.log( "OK", "Total Model", "Done building full dataset model. " );
			} catch (Exception e) { 
				e.printStackTrace(); 
				Conversion.log( "WARNING", "Total Model", "There was an error building a model over the full dataset. " );
			}
		}

		public Run getRun() {
			return run;
		}

		public Instances getPredictions() {
			return predictions;
		}

		public boolean complete() {
			return nrOfResultBatches == nrOfExpectedResultBatches;
		}
		
/*		private Double getNormalizedScore( MetricScore m ) throws Exception {
			Double score = m.getScore();
			String type = TaskInformation.getEstimationProcedure(task).getType();
			if( type.equals("crossvalidation") ) {
				// whenever we work with folds, always calculate global measures 
				// normalized to subsample size
				score *= ((double) m.getNrOfInstances()) / inputData.size();
				score /= (repeats * samples);
			} else {
				score /= nrOfExpectedResultBatches;
			}
			return score;
		}
*/
	}
}
