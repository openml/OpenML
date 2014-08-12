package org.openml.weka.experiment;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.SciMark;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.io.ApiException;
import org.openml.apiconnector.io.ApiSessionHash;
import org.openml.apiconnector.models.Metric;
import org.openml.apiconnector.models.MetricScore;
import org.openml.apiconnector.settings.Constants;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Implementation;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.Run.Parameter_setting;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.Task.Output.Predictions.Feature;
import org.openml.apiconnector.xml.UploadRun;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.openml.weka.algorithm.InstancesHelper;
import org.openml.weka.algorithm.WekaAlgorithm;

import com.thoughtworks.xstream.XStream;

import weka.classifiers.Classifier;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.RevisionHandler;
import weka.core.Utils;
import weka.experiment.InstancesResultListener;

public class TaskResultListener extends InstancesResultListener {

	private static final long serialVersionUID = 7230120341L;

	/** List of OpenML tasks currently being solved. Folds/repeats are gathered */
	private final Map<String, OpenmlExecutedTask> currentlyCollecting;

	/**
	 * List of OpenML tasks that reported back with errors. Will be send to
	 * server with error message
	 */
	private final ArrayList<String> tasksWithErrors;
	
	private final SciMark benchmarker;

	private final ApiConnector apiconnector;
	
	/** Credentials for sending results to server */
	private ApiSessionHash ash;

	/** boolean checking whether correct credentials have been stored */
	private boolean credentials = false;

	public TaskResultListener( ApiConnector apiconnector, SciMark benchmarker ) {
		super();
		
		this.benchmarker = benchmarker;
		this.apiconnector = apiconnector;
		currentlyCollecting = new HashMap<String, OpenmlExecutedTask>();
		tasksWithErrors = new ArrayList<String>();
		ash = null;
	}

	public boolean acceptCredentials(String username, String password) {
		ash = new ApiSessionHash( apiconnector );
		credentials = ash.set(username, password);
		return credentials;
	}

	public boolean gotCredentials() {
		return credentials;
	}

	public void acceptResultsForSending(Task t, Integer repeat, Integer fold, Integer sample,
			Classifier classifier, String options,
			Integer[] rowids, ArrayList<Prediction> predictions, Map<Metric, MetricScore> userMeasures ) throws Exception {
		// TODO: do something better than undefined
		String revision = (classifier instanceof RevisionHandler) ? ((RevisionHandler)classifier).getRevision() : "undefined"; 
		String implementationId = classifier.getClass().getName() + "(" + revision + ")";
		String key = t.getTask_id() + "_" + implementationId + "_" + options;
		if (currentlyCollecting.containsKey(key) == false) {
			currentlyCollecting.put(key, new OpenmlExecutedTask(t,
					classifier, null, options, apiconnector));
		}
		OpenmlExecutedTask oet = currentlyCollecting.get(key);
		oet.addBatchOfPredictions(fold, repeat, sample, rowids, predictions);
		oet.addUserDefinedMeasures(fold, repeat, sample, userMeasures);

		if (oet.complete()) {
			oet.prepareForSending();
			sendTask(oet);
			currentlyCollecting.remove(key);
		}
	}

	public void acceptErrorResult(Task t, Classifier classifier, String error_message, String options)
			throws Exception {
		// TODO: do something better than undefined
		String revision = (classifier instanceof RevisionHandler) ? ((RevisionHandler)classifier).getRevision() : "undefined"; 		
		String implementationId = classifier.getClass().getName() + "(" + revision + ")";
		String key = t.getTask_id() + "_" + implementationId + "_" + options;

		if (tasksWithErrors.contains(key) == false) {
			tasksWithErrors.add(key);
			sendTaskWithError(new OpenmlExecutedTask(t, classifier,
					error_message, options, apiconnector));
		}
	}

	private void sendTask(OpenmlExecutedTask oet) throws Exception {
		Conversion.log( "INFO", "Upload Run", "Starting send run process... " );
		XStream xstream = XstreamXmlMapping.getInstance();
		File tmpPredictionsFile;
		File tmpDescriptionFile;
		
		ashUpdate();
		// also add information about CPU performance and OS to run:
		oet.getRun().addOutputEvaluation("os_information", "openml.userdefined.os_information(1.0)", null, "[" + StringUtils.join( benchmarker.getOsInfo(), ", " ) + "]"  );
		oet.getRun().addOutputEvaluation("scimark_benchmark", "openml.userdefined.scimark_benchmark(1.0)", benchmarker.getResult(), "[" + StringUtils.join( benchmarker.getStringArray() ) + "]" );
		
		tmpPredictionsFile = InstancesHelper.instancesToTempFile(
				oet.getPredictions(), "weka_generated_predictions", Constants.DATASET_FORMAT);
		tmpDescriptionFile = Conversion.stringToTempFile(
				xstream.toXML(oet.getRun()), "weka_generated_run", "xml");
		Map<String, File> output_files = new HashMap<String, File>();
		
		output_files.put("predictions", tmpPredictionsFile);
		if(oet.serializedClassifier != null ) { output_files.put("model_serialized", oet.serializedClassifier); }
		if(oet.humanReadableClassifier != null ) { output_files.put("model_readable", oet.humanReadableClassifier); }
		
		try { 
			UploadRun ur = apiconnector.openmlRunUpload(tmpDescriptionFile, output_files, ash.getSessionHash());
			Conversion.log( "INFO", "Upload Run", "Run was uploaded with rid " + ur.getRun_id() + 
					". Obtainable at " + apiconnector.getApiUrl() + "?f=openml.run.get&run_id=" + 
					ur.getRun_id() );
		} catch( ApiException ae ) {
			ae.printStackTrace(); 
			Conversion.log( "ERROR", "Upload Run", "Failed to upload run: " + ae.getMessage() );
		}

	}

	private void sendTaskWithError(OpenmlExecutedTask oet) throws Exception {
		Conversion.log( "WARNING", "Upload Run", "Starting to upload run... (including error results) " );
		XStream xstream = XstreamXmlMapping.getInstance();
		File tmpDescriptionFile;
		
		ashUpdate();
		
		tmpDescriptionFile = Conversion.stringToTempFile(
				xstream.toXML(oet.getRun()), "weka_generated_run", Constants.DATASET_FORMAT);
		try { 
			UploadRun ur = apiconnector.openmlRunUpload(tmpDescriptionFile, new HashMap<String, File>(), ash.getSessionHash());
			Conversion.log( "WARNING", "Upload Run", "Run was uploaded with rid " + ur.getRun_id() + 
					". It includes an error message. Obtainable at " + 
					apiconnector.getApiUrl() +  "?f=openml.run.get&run_id=" + ur.getRun_id() );
		} catch( ApiException ae ) { 
			ae.printStackTrace(); 
			Conversion.log( "ERROR", "Upload Run", "Failed to upload run: " + ae.getMessage() );
		}
	}

	private void ashUpdate() throws Exception {
		if (ash == null)
			throw new Exception("No credentials provided yet. ");
		if (ash.isValid() == false)
			ash.update();
		if (ash.isValid() == false)
			throw new Exception("Credentials not valid. ");
	}

	private class OpenmlExecutedTask {
		private int task_id;
		private Classifier classifier;
		private Instances predictions;
		private Instances inputData;
		private boolean inputDataSet;
		private int nrOfResultBatches;
		private final int nrOfExpectedResultBatches;
		private String[] classnames;
		private Run run;
		private int implementation_id;
		
		private File serializedClassifier = null;
		private File humanReadableClassifier = null;
		
		private Map<Metric,Double> userDefinedMeasuresTotals;

		public OpenmlExecutedTask(Task t, Classifier classifier,
				String error_message, String options, ApiConnector apiconnector ) throws Exception {
			this.classifier = classifier;
			classnames = TaskInformation.getClassNames(apiconnector, t);
			task_id = t.getTask_id();
			
			int repeats = 1;
			int folds = 1;
			int samples = 1;
			try {repeats = TaskInformation.getNumberOfRepeats(t);} catch( Exception e ){};
			try {folds = TaskInformation.getNumberOfFolds(t);} catch( Exception e ){};
			try {samples = TaskInformation.getNumberOfSamples(t);} catch( Exception e ){};
			try {
				DataSetDescription dsd = TaskInformation.getSourceData(t).getDataSetDescription( apiconnector );
				inputData = new Instances( new FileReader( dsd.getDataset() ) );
				inputData.setClass( inputData.attribute(TaskInformation.getSourceData(t).getTarget_feature()) );
				inputDataSet = true;
			} catch( Exception e ) {
				inputDataSet = false;
			};
			
			nrOfExpectedResultBatches = repeats * folds * samples;
			nrOfResultBatches = 0;
			ArrayList<Attribute> attInfo = new ArrayList<Attribute>();
			for (Feature f : TaskInformation.getPredictions(t).getFeatures()) {
				if (f.getName().equals("confidence.classname")) {
					for (String s : TaskInformation.getClassNames(apiconnector, t)) {
						attInfo.add(new Attribute("confidence." + s));
					}
				} else if (f.getName().equals("prediction")) {
					List<String> values = new ArrayList<String>(classnames.length);
					for (String classname : classnames)
						values.add(classname);
					attInfo.add(new Attribute(f.getName(), values));
				} else {
					attInfo.add(new Attribute(f.getName()));
				}
			}
			
			// this is an additional field, which is ignored on the server
			if( inputDataSet ) {
				attInfo.add( inputData.classAttribute().copy("correct") );
			}
			
			predictions = new Instances("openml_task_" + t.getTask_id() + "_predictions", attInfo, 0);
			
			
			Implementation find = WekaAlgorithm.create( classifier.getClass().getName(), options );
			
			implementation_id = WekaAlgorithm.getImplementationId( find, classifier, apiconnector, ash.getSessionHash() );
			Implementation implementation = apiconnector.openmlImplementationGet( implementation_id );
			
			String setup_string = classifier.getClass().getName();
			if(options.equals("") == false) setup_string += (" -- " + options);
			
			String[] params = Utils.splitOptions( options );
			List<Parameter_setting> list = WekaAlgorithm.getParameterSetting( params, implementation );
			
			run = new Run(t.getTask_id(), error_message, implementation.getId(), setup_string, list.toArray(new Parameter_setting[list.size()]) );
			userDefinedMeasuresTotals = new HashMap<Metric, Double>();
		}
		
		public void addBatchOfPredictions(Integer fold, Integer repeat, Integer sample, Integer[] rowids, ArrayList<Prediction> batchPredictions) {
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
				if( inputDataSet ) {
					values[predictions.attribute("correct").index()] = inputData.instance(rowids[i]).classValue();
				}
				
				if (current instanceof NominalPrediction) {
					double[] confidences = ((NominalPrediction) current)
							.distribution();
					for (int j = 0; j < confidences.length; ++j) {
						values[predictions.attribute(
								"confidence." + classnames[j]).index()] = confidences[j];
					}
				}

				predictions.add(new DenseInstance(1.0D, values));
			}
		}
		
		public void addUserDefinedMeasures(Integer fold, Integer repeat, Integer sample, Map<Metric, MetricScore> userMeasures) {
			// attach fold/sample specific user measures to run
			for( Metric m : userMeasures.keySet() ) {
				Double score = userMeasures.get(m).getScore();
				getRun().addOutputEvaluation(m.name, repeat, fold, sample, m.implementation, score );
				
				if(userDefinedMeasuresTotals.containsKey(m) == false) {
					userDefinedMeasuresTotals.put(m, score);
				} else {
					userDefinedMeasuresTotals.put(m, userDefinedMeasuresTotals.get(m) + score);
				}
			}
		}
		
		public void prepareForSending() {
			// calculate "global" (opposed to fold specific) user defined evaluation measures
			for( Metric m : userDefinedMeasuresTotals.keySet() ) {
				run.addOutputEvaluation(m.name, m.implementation, userDefinedMeasuresTotals.get(m) / nrOfResultBatches, null);
			}
			if( inputDataSet ) {
				// build model for entire data set. This can take some time
				try {
					Conversion.log( "OK", "Total Model", "Started building a model over the full dataset. " );
					classifier.buildClassifier(inputData);
					
					humanReadableClassifier = Conversion.stringToTempFile(classifier.toString(), "WekaModel_" + classifier.getClass().getName(), "model");
					serializedClassifier = WekaAlgorithm.classifierSerializedToFile(classifier, task_id);
				} catch (Exception e) { 
					e.printStackTrace(); 
					Conversion.log( "WARNING", "Total Model", "There was an error building a model over the full dataset. " );
				}
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
	}
}
