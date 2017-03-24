package openmlweka;

import java.util.UUID;

import org.junit.Test;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.Flow;
import org.openml.apiconnector.xml.UploadFlow;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.openml.weka.algorithm.WekaAlgorithm;

import com.thoughtworks.xstream.XStream;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.HoeffdingTree;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.LMT;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;

public class TestFlowSerialization {
	
	public final String[] TAGS = {"OpenmlWeka", "weka"};
	public final boolean USE_SENTINEL = false;
	public final XStream xstream = XstreamXmlMapping.getInstance();
	public final OpenmlConnector connector = new OpenmlConnector(
			"https://test.openml.org/", "8baa83ecddfe44b561fd3d92442e3319");
	
	@Test
	public void testSimpleFlow() throws Exception {
		String uuid = UUID.randomUUID().toString();
		Classifier[] classifiers = {new ZeroR(), new OneR(), new JRip(), 
									new J48(), new REPTree(), new HoeffdingTree(), new LMT(),
		                            new NaiveBayes(), new IBk(),
		                            new SMO(), new Logistic(), new MultilayerPerceptron(),
		                            new RandomForest(), new Bagging(), new AdaBoostM1()};
		                            
		for (Classifier classif : classifiers){
			String classname = classif.getClass().getName();
			Flow result = WekaAlgorithm.serializeClassifier(classif.getClass().getName(), TAGS);
			if (USE_SENTINEL) {
				result.setName(uuid + "_" + result.getName());
			}
			String resultAsString = xstream.toXML(result);
			UploadFlow uf = connector.flowUpload(Conversion.stringToTempFile(resultAsString, classname, "xml"), null, null);
			System.out.println(uf.getId());
			// TODO: now download and check if equal
		}
	}
}
