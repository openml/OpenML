package openmlweka;

import static org.junit.Assert.*;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.Flow;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.SetupParameters;
import org.openml.apiconnector.xml.SetupParameters.Parameter;
import org.openml.weka.algorithm.WekaConfig;
import org.openml.weka.experiment.RunOpenmlJob;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;

public class TestRunJob {
	
	private static final String configString = "server=https://test.openml.org/; avoid_duplicate_runs=false; skip_jvm_benchmark=true; api_key=8baa83ecddfe44b561fd3d92442e3319";
	private static final WekaConfig config = new WekaConfig(configString);
	private static final OpenmlConnector openml = new OpenmlConnector(config.getServer(), config.getApiKey());
	
	@Test
	public void testApiRunUploadFromCliString() throws Exception {
		String[] algorithms = {"weka.classifiers.trees.REPTree", "weka.classifiers.meta.Bagging -P 50 -S 4385 -num-slots 4 -I 10 -W weka.classifiers.trees.J48 -- -R -N 3", "weka.classifiers.meta.FilteredClassifier -F \"weka.filters.supervised.attribute.Discretize -R first-last -precision 6\" -W weka.classifiers.trees.RandomForest -- -I 100 -K 0 -S 1 -num-slots 1"};
		String[] args = {"-task_id", "1", "-config", configString, "-C"};
		
		for (String algorithm : algorithms) {
			RunOpenmlJob.main(ArrayUtils.add(args, algorithm));
		}
	}
	
	@Test
	public void testApiRunUploadNB() throws Exception {
		int runIdA = RunOpenmlJob.executeTask(openml, config, 115, new NaiveBayes());
		assertTrue(openml.runGet(runIdA).getFlow_name().contains("NaiveBayes"));
	}
	
	@Test
	public void testApiRunUploadJ48() throws Exception {
		J48 tree = new J48();
		tree.setConfidenceFactor(0.001F);
		tree.setMinNumObj(5);
		tree.setBinarySplits(true);
		int runId = RunOpenmlJob.executeTask(openml, config, 115, tree);
		Run run = openml.runGet(runId);
		int setupId = run.getSetup_id();
		Flow flow = openml.flowGet(run.getFlow_id());
		SetupParameters sp = openml.setupParameters(setupId);
		Map<String, Parameter> parameters = sp.getParametersAsMap();
		String fullName = flow.getName() + "(" + flow.getVersion() + ")";
		assertEquals(parameters.get(fullName + "_M").getValue(), "5");
		assertEquals(parameters.get(fullName + "_C").getValue(), "0.001");
		assertEquals(parameters.get(fullName + "_B").getValue(), "true");
	}
}
