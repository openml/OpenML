package org.openml.moa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import openml.algorithms.Conversion;
import openml.algorithms.TaskInformation;
import openml.xml.Task;
import openml.xml.Task.Output.Predictions.Feature;
import weka.core.Attribute;
import weka.core.Instances;
import moa.core.InstancesHeader;

public class ResultListener {
	
	private final File results;
	private final InstancesHeader header;
	private final BufferedWriter bw;
	
	private int att_index_row_id = -1;
	private int att_index_confidence = -1;
	private int att_index_prediction = -1;
	private int att_index_correct = -1;
	private ArrayList<String> classes = new ArrayList<String>();
	
	public ResultListener( Task t ) throws Exception {
		header = createInstanceHeader( t );
		results = Conversion.stringToTempFile( header.toString(), header.relationName(), "arff");
		bw = new BufferedWriter( new FileWriter( results ) );
		bw.write(header.toString());
	}
	
	public boolean sendToOpenML() throws IOException {
		bw.close();
		System.out.println( Conversion.fileToString(results) ); 
		return true;
	}
	
	public void addPrediction( int row_id, double[] predictions, int correct ) throws IOException {
		String line = "";
		int    predicted = -1;
		double predicted_best = 0;
		String[] instance = new String[header.numAttributes()];
		instance[att_index_row_id] = "" + row_id;
		instance[att_index_correct] = classes.get( correct );
		for( int i = 0; i < classes.size(); ++i ) {
			if( i < predictions.length ) {
				instance[att_index_confidence+i] = predictions[i] + "";
				if( predictions[i] > predicted_best ) {
					predicted_best = predictions[i];
					predicted = i;
				}
			} else {
				instance[att_index_confidence+i] = "0.0";
			}
		}
		
		instance[att_index_prediction] = predicted >= 0 ? classes.get( predicted ) : "?";
		
		for( int i = 0; i < instance.length; ++i ) {
			line += "," + ((instance[i] != null) ? instance[i] : "?");
		}
		bw.write( line.substring( 1 ) + "\n" );
	}
	
	private InstancesHeader createInstanceHeader( Task t ) throws Exception {
		ArrayList<Attribute> header = new ArrayList<Attribute>();
		Feature[] features = TaskInformation.getPredictions(t).getFeatures();
		// TODO: FIXME?!
		classes = new ArrayList<String>( Arrays.asList( TaskInformation.getClassNames(t) ) );
		for( int i = 0; i < features.length; i++ ) {
			Feature f = features[i];
			if( f.getName().equals("confidence.classname") ) {
				att_index_confidence = i;
				for (String s : TaskInformation.getClassNames(t)) {
					header.add(new Attribute("confidence." + s));
				}
			} else if (f.getName().equals("prediction")) {
				header.add(new Attribute("prediction",classes));
			} else {
				header.add(new Attribute(f.getName()));
			}
		}
		
		header.add( new Attribute( "correct", classes ) );
		Instances inst = new Instances("openml_task_"+t.getTask_id()+"_predictions", header, 0 );
		att_index_row_id = inst.attribute("row_id").index();
		att_index_correct = inst.attribute("correct").index();
		att_index_prediction = inst.attribute("prediction").index();
		
		return new InstancesHeader( inst );
	}
}
