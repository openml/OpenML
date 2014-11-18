package org.openml.weka.experiment;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

import org.openml.apiconnector.algorithms.Conversion;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.AbstractEvaluationMetric;
import weka.classifiers.evaluation.Prediction;
import weka.core.AdditionalMeasureProducer;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Summarizable;
import weka.core.Utils;
import weka.experiment.RegressionSplitEvaluator;

public class OpenmlRegressionSplitEvaluator extends RegressionSplitEvaluator implements OpenmlSplitEvaluator {
	
	private static final long serialVersionUID = 207500686037430871L;
	
	private transient ArrayList<Prediction> recentPredictions = null;
	
	/** 
	 * JvR: Direct copies from weka.experiment.RegressionSplitEvaluator. 
	 * Change values if these also change there. */
	  private static final int RESULT_SIZE = 25;
	
	private boolean m_NoSizeDetermination = true;
	
	/**
	 * Gets the results for the supplied train and test datasets. Now performs a
	 * deep copy of the classifier before it is built and evaluated (just in
	 * case the classifier is not initialized properly in buildClassifier()).
	 *
	 * @param train
	 *            the training Instances.
	 * @param test
	 *            the testing Instances.
	 * @return the results stored in an array. The objects stored in the array
	 *         may be Strings, Doubles, or null (for the missing value).
	 * @throws Exception
	 *             if a problem occurs while getting the results
	 */
	public Object[] getResult(Instances train, Instances test) throws Exception {

		if (train.classAttribute().type() != Attribute.NUMERIC) {
			throw new Exception("Class attribute is not numeric!");
		}
		if (m_Template == null) {
			throw new Exception("No classifier has been specified");
		}
		ThreadMXBean thMonitor = ManagementFactory.getThreadMXBean();
		boolean canMeasureCPUTime = thMonitor.isThreadCpuTimeSupported();
		if (canMeasureCPUTime && !thMonitor.isThreadCpuTimeEnabled()) {
			thMonitor.setThreadCpuTimeEnabled(true);
		}

		int addm = (m_AdditionalMeasures != null) ? m_AdditionalMeasures.length
				: 0;
		Object[] result = new Object[RESULT_SIZE + addm + m_numPluginStatistics];
		long thID = Thread.currentThread().getId();
		long CPUStartTime = -1, trainCPUTimeElapsed = -1, testCPUTimeElapsed = -1, trainTimeStart, trainTimeElapsed, testTimeStart, testTimeElapsed;
		Evaluation eval = new Evaluation(train);
		m_Classifier = AbstractClassifier.makeCopy(m_Template);
		
		// training classifier
		Conversion.log( "INFO",	"Build Model", "Start building model on training data (" + train.numInstances() + " instances)");
		trainTimeStart = System.currentTimeMillis();
		if (canMeasureCPUTime) {
			CPUStartTime = thMonitor.getThreadUserTime(thID);
		}
		m_Classifier.buildClassifier(train);
		if (canMeasureCPUTime) {
			trainCPUTimeElapsed = thMonitor.getThreadUserTime(thID)
					- CPUStartTime;
		}
		trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;
		Conversion.log("INFO", "Build Model", "Done");

		// testing classifier
		Conversion.log("INFO", "Test Model", "Start making predictions on test data (" + test.numInstances() + " instances)");
		testTimeStart = System.currentTimeMillis();
		if (canMeasureCPUTime) {
			CPUStartTime = thMonitor.getThreadUserTime(thID);
		}
		eval.evaluateModel(m_Classifier, test);
		if (canMeasureCPUTime) {
			testCPUTimeElapsed = thMonitor.getThreadUserTime(thID)
					- CPUStartTime;
		}
		testTimeElapsed = System.currentTimeMillis() - testTimeStart;
		thMonitor = null;
		
		Conversion.log("INFO", "Test Model", "Done");

		recentPredictions = eval.predictions();
		/** END ADDITIONS BY JvR **/
		
		m_result = eval.toSummaryString();
		// The results stored are all per instance -- can be multiplied by the
		// number of instances to get absolute numbers
		int current = 0;
		result[current++] = new Double(train.numInstances());
		result[current++] = new Double(eval.numInstances());

		result[current++] = new Double(eval.meanAbsoluteError());
		result[current++] = new Double(eval.rootMeanSquaredError());
		result[current++] = new Double(eval.relativeAbsoluteError());
		result[current++] = new Double(eval.rootRelativeSquaredError());
		result[current++] = new Double(eval.correlationCoefficient());
		result[current++] = new Double(eval.unclassified());
		result[current++] = new Double(eval.pctUnclassified());

		result[current++] = new Double(eval.SFPriorEntropy());
		result[current++] = new Double(eval.SFSchemeEntropy());
		result[current++] = new Double(eval.SFEntropyGain());
		result[current++] = new Double(eval.SFMeanPriorEntropy());
		result[current++] = new Double(eval.SFMeanSchemeEntropy());
		result[current++] = new Double(eval.SFMeanEntropyGain());

		// Timing stats
		result[current++] = new Double(trainTimeElapsed / 1000.0);
		result[current++] = new Double(testTimeElapsed / 1000.0);
		if (canMeasureCPUTime) {
			result[current++] = new Double(
					(trainCPUTimeElapsed / 1000000.0) / 1000.0);
			result[current++] = new Double(
					(testCPUTimeElapsed / 1000000.0) / 1000.0);
		} else {
			result[current++] = new Double(Utils.missingValue());
			result[current++] = new Double(Utils.missingValue());
		}

		// sizes
		if (m_NoSizeDetermination) {
			result[current++] = -1.0;
			result[current++] = -1.0;
			result[current++] = -1.0;
		} else {
			ByteArrayOutputStream bastream = new ByteArrayOutputStream();
			ObjectOutputStream oostream = new ObjectOutputStream(bastream);
			oostream.writeObject(m_Classifier);
			result[current++] = new Double(bastream.size());
			bastream = new ByteArrayOutputStream();
			oostream = new ObjectOutputStream(bastream);
			oostream.writeObject(train);
			result[current++] = new Double(bastream.size());
			bastream = new ByteArrayOutputStream();
			oostream = new ObjectOutputStream(bastream);
			oostream.writeObject(test);
			result[current++] = new Double(bastream.size());
		}

		// Prediction interval statistics
		result[current++] = new Double(
				eval.coverageOfTestCasesByPredictedRegions());
		result[current++] = new Double(eval.sizeOfPredictedRegions());

		if (m_Classifier instanceof Summarizable) {
			result[current++] = ((Summarizable) m_Classifier).toSummaryString();
		} else {
			result[current++] = null;
		}

		for (int i = 0; i < addm; i++) {
			if (m_doesProduce[i]) {
				try {
					double dv = ((AdditionalMeasureProducer) m_Classifier)
							.getMeasure(m_AdditionalMeasures[i]);
					if (!Utils.isMissingValue(dv)) {
						Double value = new Double(dv);
						result[current++] = value;
					} else {
						result[current++] = null;
					}
				} catch (Exception ex) {
					System.err.println(ex);
				}
			} else {
				result[current++] = null;
			}
		}

		// get the actual metrics from the evaluation object
		List<AbstractEvaluationMetric> metrics = eval.getPluginMetrics();
		if (metrics != null) {
			for (AbstractEvaluationMetric m : metrics) {
				if (m.appliesToNumericClass()) {
					List<String> statNames = m.getStatisticNames();
					for (String s : statNames) {
						result[current++] = new Double(m.getStatistic(s));
					}
				}
			}
		}

		if (current != RESULT_SIZE + addm + m_numPluginStatistics) {
			throw new Error("Results didn't fit RESULT_SIZE");
		}
		return result;
	}
	
	public ArrayList<Prediction> recentPredictions() throws Exception {
		if (recentPredictions != null)
			return recentPredictions;
		throw new Exception("No predictions set by SplitEvaluator. ");
	}
	
	public Classifier getClassifier() {
		return m_Classifier;
	}
}
