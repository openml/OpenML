package webapplication;

import java.io.BufferedReader;
import java.net.URL;

import org.junit.Test;
import org.openml.apiconnector.algorithms.Input;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.webapplication.fantail.dc.Characterizer;
import org.openml.webapplication.fantail.dc.statistical.Cardinality;

import weka.core.Instances;

public class TestCardinalityCharacterizer {
	
	private static final String url = "https://www.openml.org/";
	private static final OpenmlConnector client_read = new OpenmlConnector(url, "c1994bdb7ecb3c6f3c8f3b35f4b47f1f"); // R-TEAM
	private static final Characterizer Cardinality = new Cardinality();
	
	private static Instances getById(int id) throws Exception {
		DataSetDescription dsd = client_read.dataGet(id);
		URL dataUrl = client_read.getOpenmlFileUrl(dsd.getFile_id(), "dataset");
		return new Instances(new BufferedReader(Input.getURL(dataUrl)));
	}
	
	@Test
	public void testAnneal() throws Exception {
		int id = 2;
		Instances dataset = getById(id);
		
		System.out.println(Cardinality.characterize(dataset));
	}
}
