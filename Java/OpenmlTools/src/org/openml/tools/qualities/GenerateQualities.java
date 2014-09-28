package org.openml.tools.qualities;

import java.io.IOException;

import org.json.JSONException;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.QueryUtils;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.webapplication.features.FantailConnector;

public class GenerateQualities {

	// program for auto filling of data qualities
	public static void main( String[] args ) throws JSONException, IOException {
		Config config = new Config("username = janvanrijn@gmail.com; password = Feyenoord2002; server = http://localhost/openexpdb_v2/");
		int numQualitiesThreshold = 66;
		
		OpenmlConnector api = new OpenmlConnector( config.getServer() );
		String sql = "SELECT `did`, `name`, `error`, `i`.`value` AS `num_instances`, COUNT(*) AS `qualities` FROM `dataset` `d` LEFT JOIN `data_quality` `i` ON `d`.`did` = `i`.`data` LEFT JOIN `data_quality` `q` ON `d`.`did` = `q`.`data` WHERE `i`.`quality` = 'NumberOfInstances' GROUP BY `d`.`did` HAVING COUNT(*) < " + numQualitiesThreshold;
		
		int[] dataset_ids = QueryUtils.getIdsFromDatabase(api, sql);

		if( dataset_ids.length == 0 ) {
			Conversion.log("OK", "Extract Qualities", "No datasets to process. ");
		}
		
		for( int did : dataset_ids ) {
			try {
				DataSetDescription dsd = api.openmlDataDescription(did);
				FantailConnector.extractFeatures(did, dsd.getDefault_target_attribute(), null, config);
				
			} catch( Exception e ) { System.err.println("Error at " + did + ": " + e.getMessage() ); }
		}
	}
}
