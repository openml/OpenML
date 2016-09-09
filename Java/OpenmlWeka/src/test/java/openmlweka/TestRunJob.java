package openmlweka;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.openml.weka.experiment.RunOpenmlJob;

public class TestRunJob {
	
	
	@Test
	public void testApiRunUpload() {
		
		String[] args = {"-task_id", "1", "-C", "weka.classifiers.trees.REPTree"};
		try {
			RunOpenmlJob.main(args);
		} catch(Exception e) {
			e.printStackTrace();
			fail("Test failed: " + e.getMessage());
		}
	}
}
