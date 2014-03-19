package org.openml.moa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import openml.algorithms.Conversion;
import openml.algorithms.MathFunctions;
import openml.algorithms.MoaAlgorithm;
import openml.algorithms.TaskInformation;
import openml.io.ApiConnector;
import openml.io.ApiSessionHash;
import openml.models.Metric;
import openml.models.MetricScore;
import openml.xml.Implementation;
import openml.xml.Run;
import openml.xml.Run.Parameter_setting;
import openml.xml.Task;
import openml.xml.Task.Output.Predictions.Feature;
import openml.xstream.XstreamXmlMapping;
import weka.core.Attribute;
import weka.core.Instances;
import moa.classifiers.Classifier;
import moa.core.InstancesHeader;

public class ResultListener {
	
	private final File results;
	private final Task task;
	private final InstancesHeader header;
	private final BufferedWriter bw;
	private final DecimalFormat df; 

	private final ApiSessionHash ash;
	
	private int att_index_row_id = -1;
	private int att_index_confidence = -1;
	private int att_index_prediction = -1;
	private int att_index_correct = -1;
	private ArrayList<String> classes = new ArrayList<String>();
	
	public ResultListener( Task task, ApiSessionHash ash ) throws Exception {
		this.ash = ash;
		this.task = task;
		
		df = new DecimalFormat(".######");
		header = createInstanceHeader( task );
		results = Conversion.stringToTempFile( header.toString(), header.relationName(), "arff");
		bw = new BufferedWriter( new FileWriter( results ) );
		bw.write(header.toString());
	}
	
	public boolean sendToOpenML( Classifier classifier, Map<Metric, MetricScore> userdefinedMeasures ) throws Exception {
		bw.close();
		
		Implementation implementation = MoaAlgorithm.create(classifier);
		int implementation_id = MoaAlgorithm.getImplementationId(implementation, classifier, ash.getSessionHash());
		implementation = ApiConnector.openmlImplementationGet( implementation_id ); // updated
		
		ArrayList<Parameter_setting> ps = MoaAlgorithm.getOptions( implementation, classifier.getOptions().getOptionArray() );
		
		Run run = new Run( task.getTask_id(), null, implementation_id, classifier.getCLICreationString(Classifier.class), ps.toArray(new Parameter_setting[ps.size()]) );
		for( Metric m : userdefinedMeasures.keySet() ) {
			MetricScore score = userdefinedMeasures.get(m);
			run.addOutputEvaluation( m.name, m.implementation, score.getScore(), score.getArrayAsString( df ) );
		}
		File descriptionXML = Conversion.stringToTempFile( XstreamXmlMapping.getInstance().toXML( run ), "moa_task_" + task.getTask_id(), "xml");
		
		
		
		Map<String, File> output_files = new HashMap<String, File>();
		output_files.put( "predictions", results );
		
		ApiConnector.openmlRunUpload(descriptionXML, output_files, ash.getSessionHash() );
		
		return true;
	}
	
	public void addPrediction( int row_id, double[] predictions, int correct ) throws IOException {
		String line = "";
		String[] instance = new String[header.numAttributes()];
		int predicted = MathFunctions.argmax(predictions, true);
		instance[att_index_prediction] = ( predicted < 0 ) ? "?" : classes.get( predicted ); 
		instance[att_index_row_id] = "" + row_id;
		instance[att_index_correct] = classes.get( correct );
		
		for( int i = 0; i < classes.size(); ++i ) {
			if( i < predictions.length ) {
				instance[att_index_confidence+i] = df.format(predictions[i]);
			} else {
				instance[att_index_confidence+i] = "0";
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
