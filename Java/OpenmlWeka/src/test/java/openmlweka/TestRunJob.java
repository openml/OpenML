package openmlweka;

import static org.junit.Assert.fail;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.openml.weka.experiment.RunOpenmlJob;

public class TestRunJob {
	
	
	@Test
	public void testApiRunUpload() {
		
		String[] algorithms = {"weka.classifiers.trees.REPTree", "weka.classifiers.meta.Bagging -- -P 50 -S 4385 -num-slots 4 -I 10 -W weka.classifiers.trees.J48 -- -R -N 3", "weka.classifiers.meta.FilteredClassifier -- -F \"weka.filters.supervised.attribute.Discretize -R first-last -precision 6\" -W weka.classifiers.trees.RandomForest -- -I 100 -K 0 -S 1 -num-slots 1"};
		
		String[] args = {"-task_id", "1", "-config", "server=http://capa.win.tue.nl/; avoid_duplicate_runs=false; skip_jvm_benchmark=true; api_key=8baa83ecddfe44b561fd3d92442e3319", "-C"};
		
		for (String algorithm : algorithms) {
			try {
				RunOpenmlJob.main(ArrayUtils.add(args, algorithm));
			} catch(Exception e) {
				e.printStackTrace();
				fail("Test failed: " + e.getMessage());
			}
		}
	}
}
