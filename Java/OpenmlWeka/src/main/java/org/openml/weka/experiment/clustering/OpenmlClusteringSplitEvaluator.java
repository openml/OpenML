package org.openml.weka.experiment.clustering;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import weka.classifiers.evaluation.Prediction;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.core.AdditionalMeasureProducer;
import weka.core.Instances;
import weka.experiment.DensityBasedClustererSplitEvaluator;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class OpenmlClusteringSplitEvaluator extends DensityBasedClustererSplitEvaluator {

	private static final long serialVersionUID = -73425852822213L;
	
	private double[] clusterAssignment;

	  /** The length of a result */
	  private static final int RESULT_SIZE = 9;
	
	  
	  
	/**
	   * Gets the results for the supplied train and test datasets.
	   * 
	   * @param train the training Instances.
	   * @param test the testing Instances.
	   * @return the results stored in an array. The objects stored in the array may
	   *         be Strings, Doubles, or null (for the missing value).
	   * @exception Exception if a problem occurs while getting the results
	   */
	  @Override
	  public Object[] getResult(Instances train, Instances test) throws Exception {

	    if (m_clusterer == null) {
	      throw new Exception("No clusterer has been specified");
	    }
	    int addm = (m_additionalMeasures != null) ? m_additionalMeasures.length : 0;
	    int overall_length = RESULT_SIZE + addm;

	    if (m_removeClassColumn && train.classIndex() != -1) {
	      // remove the class column from the training and testing data
	      Remove r = new Remove();
	      r.setAttributeIndicesArray(new int[] { train.classIndex() });
	      r.setInvertSelection(false);
	      r.setInputFormat(train);
	      train = Filter.useFilter(train, r);

	      test = Filter.useFilter(test, r);
	    }
	    train.setClassIndex(-1);
	    test.setClassIndex(-1);

	    ClusterEvaluation eval = new ClusterEvaluation();

	    Object[] result = new Object[overall_length];
	    long trainTimeStart = System.currentTimeMillis();
	    m_clusterer.buildClusterer(train);
	    double numClusters = m_clusterer.numberOfClusters();
	    eval.setClusterer(m_clusterer);
	    long trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;
	    long testTimeStart = System.currentTimeMillis();
	    eval.evaluateClusterer(test);
	    long testTimeElapsed = System.currentTimeMillis() - testTimeStart;
	    // m_result = eval.toSummaryString();

	    // TODO: our only addition to this function
	    clusterAssignment = eval.getClusterAssignments();
	    
	    // The results stored are all per instance -- can be multiplied by the
	    // number of instances to get absolute numbers
	    int current = 0;
	    result[current++] = new Double(train.numInstances());
	    result[current++] = new Double(test.numInstances());

	    result[current++] = new Double(eval.getLogLikelihood());
	    result[current++] = new Double(numClusters);

	    // Timing stats
	    result[current++] = new Double(trainTimeElapsed / 1000.0);
	    result[current++] = new Double(testTimeElapsed / 1000.0);

	    // sizes
	    if (m_NoSizeDetermination) {
	      result[current++] = -1.0;
	      result[current++] = -1.0;
	      result[current++] = -1.0;
	    } else {
	      ByteArrayOutputStream bastream = new ByteArrayOutputStream();
	      ObjectOutputStream oostream = new ObjectOutputStream(bastream);
	      oostream.writeObject(m_clusterer);
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

	    for (int i = 0; i < addm; i++) {
	      if (m_doesProduce[i]) {
	        try {
	          double dv = ((AdditionalMeasureProducer) m_clusterer)
	            .getMeasure(m_additionalMeasures[i]);
	          Double value = new Double(dv);

	          result[current++] = value;
	        } catch (Exception ex) {
	          System.err.println(ex);
	        }
	      } else {
	        result[current++] = null;
	      }
	    }

	    if (current != overall_length) {
	      throw new Error("Results didn't fit RESULT_SIZE");
	    }
	    return result;
	  }
	  
	  public double[] getClusterAssignment() {
		  return clusterAssignment;
	  }
}
