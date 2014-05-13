package org.openml.tools.stream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.openml.apiconnector.algorithms.QueryUtils;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.webapplication.evaluate.EvaluateRun;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Utils;

public class ShowMissingValues {

	Map<String, Map<Integer, Integer>> missingValues = new HashMap<String, Map<Integer,Integer>>();
	Instances instances;
	
	public static void main( String[] args ) throws Exception {
		
		ArrayList<String> attributes = new ArrayList<String>();
		attributes.add("openml_classifier_moa.LeveragingBag_HoeffdingTree(1)");
		attributes.add("openml_classifier_moa.LeveragingBag_kNN(1)");
		attributes.add("openml_classifier_moa.OzaBoost_HoeffdingTree(1)");
		attributes.add("openml_classifier_moa.OzaBagAdwin_HoeffdingTree(1)");

		attributes.add("openml_classifier_moa.WEKAClassifier_J48(1)");
		attributes.add("openml_classifier_moa.WEKAClassifier_REPTree(1)");
		attributes.add("openml_classifier_moa.WEKAClassifier_OneR(1)");
		attributes.add("openml_classifier_moa.WEKAClassifier_SMO_PolyKernel(1)");

		attributes.add("openml_classifier_moa.SGD(1)");
		attributes.add("openml_classifier_moa.NaiveBayes(1)");
		attributes.add("openml_classifier_moa.SPegasos(1)");
		attributes.add("openml_classifier_moa.HoeffdingTree(1)");
		attributes.add("openml_classifier_moa.kNN(1)");
		
		new ShowMissingValues( "/Users/jan/Desktop/msa.arff", attributes );
	}
	
	public ShowMissingValues( String arfffile, ArrayList<String> attributenames ) throws Exception {
		instances = new Instances( new BufferedReader( new FileReader( new File( arfffile ) ) ) );
		ApiConnector api = new ApiConnector();
		Config c = new Config("username = janvanrijn@gmail.com; password = Feyenoord2002; server = http://openml.liacs.nl/");

		Integer[] speedup = {1443,
				1486,
				1456,
				1384,
				1383,
				1260,
				1253,
				1433,
				1386,
				1460};
		ArrayList<Integer> allRunIds = new ArrayList<Integer>( Arrays.asList( speedup ) );
		
		/*for( String attributename : attributenames ) {
			ArrayList<Integer> task_ids = doAttribute(attributename);
			ArrayList<Integer> classifierRunIds = new ArrayList<Integer>();
			
			String classifier = attributename.substring( "openml_classifier_".length() );
			
			for( Integer task_id : task_ids ) {
				String sql = "select r.rid,r.task_id,a.sid,i.fullName from run r,  algorithm_setup a, implementation i WHERE r.setup = a.sid AND a.implementation_id = i.id AND r.task_id = " + task_id + " AND i.fullName = '" + classifier + "';";
				Integer run_id = (int) QueryUtils.getIntFromDatabase( api , sql );
				//System.out.println( task_id + ", " + classifier + ", " + run_id );
				allRunIds.add( run_id );
				classifierRunIds.add( run_id );
			}
			System.out.println( classifier + " - " + classifierRunIds );
		}*/
		
		int counter = 0;
		for( Integer run_id : allRunIds ) {
			System.out.println( "Re evaluate run: " + run_id + " ("+(++counter)+"/"+allRunIds.size()+")" );
			new EvaluateRun( c, run_id);
		}
		
	}
	
	public ArrayList<Integer> doAttribute( String attributename ) {
		Attribute att = instances.attribute( attributename );
		missingValues.put( attributename, new HashMap<Integer, Integer>());
		
		for( int i = 0; i < instances.numInstances(); ++i ) {
			if( Utils.isMissingValue( instances.instance( i ).value(att) ) ) {
				int task_id = (int) instances.instance( i ).value( 0 );
				if( missingValues.get( attributename ).containsKey( task_id ) ) {
					missingValues.get( attributename ).put( task_id, missingValues.get( attributename ).get( task_id) + 1 );
				} else {
					missingValues.get( attributename ).put( task_id, 1 );
				}
			}
		}
		return new ArrayList<Integer>( missingValues.get( attributename ).keySet() );
	}
}
