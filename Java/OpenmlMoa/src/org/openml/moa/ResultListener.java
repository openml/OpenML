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

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.MathHelper;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.models.Metric;
import org.openml.apiconnector.models.MetricScore;
import org.openml.apiconnector.xml.Flow;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.UploadRun;
import org.openml.apiconnector.xml.Run.Parameter_setting;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.Task.Output.Predictions.Feature;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.openml.moa.algorithm.MoaAlgorithm;

import weka.core.Attribute;
import weka.core.Instances;
import moa.classifiers.Classifier;
import moa.core.InstancesHeader;

public class ResultListener {
	
	private static final String[] MOA_TAGS = { "Moa" };
	
	private final File results;
	private final Task task;
	private final InstancesHeader header;
	private final BufferedWriter bw;
	private final DecimalFormat df; 

	private final OpenmlConnector apiconnector;
	
	private int att_index_row_id = -1;
	private int att_index_confidence = -1;
	private int att_index_prediction = -1;
	private int att_index_correct = -1;
	private ArrayList<String> classes = new ArrayList<String>();
	
	public ResultListener( Task task, OpenmlConnector apiconnector ) throws Exception {
		this.task = task;
		this.apiconnector = apiconnector;
		
		df = new DecimalFormat(".######");
		header = createInstanceHeader( task );
		results = Conversion.stringToTempFile( header.toString(), header.relationName(), "arff");
		bw = new BufferedWriter( new FileWriter( results ) );
		bw.write(header.toString());
	}
	
	public boolean sendToOpenML( Classifier classifier, Map<Metric, MetricScore> userdefinedMeasures ) throws Exception {
		bw.close();
		
		Flow implementation = MoaAlgorithm.create(classifier);
		int implementation_id = MoaAlgorithm.getFlowId(implementation, classifier, apiconnector );
		implementation = apiconnector.flowGet( implementation_id ); // updated
		
		ArrayList<Parameter_setting> ps = MoaAlgorithm.getOptions( implementation, classifier.getOptions().getOptionArray() );
		
		Run run = new Run( task.getTask_id(), null, implementation_id, classifier.getCLICreationString(Classifier.class), ps.toArray(new Parameter_setting[ps.size()]), MOA_TAGS );
		for( Metric m : userdefinedMeasures.keySet() ) {
			MetricScore score = userdefinedMeasures.get(m);
			run.addOutputEvaluation( m.name, m.implementation, score.getScore(), score.getArrayAsString( df ) );
		}
		String runxml = XstreamXmlMapping.getInstance().toXML( run );
		File descriptionXML = Conversion.stringToTempFile( runxml, "moa_task_" + task.getTask_id(), "xml");
		
		Map<String, File> output_files = new HashMap<String, File>();
		output_files.put( "predictions", results );
		
		UploadRun ur = apiconnector.runUpload(descriptionXML, output_files );
		Conversion.log( "OK", "Upload Result", "Result successfully uploaded, with rid " + ur.getRun_id() );
		return true;
	}
	
	public void addPrediction( int row_id, double[] predictions, int correct ) throws IOException {
		String line = "";
		String[] instance = new String[header.numAttributes()];
		int predicted = MathHelper.argmax(predictions, true);
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
		classes = new ArrayList<String>( Arrays.asList( TaskInformation.getClassNames(apiconnector,t) ) );
		for( int i = 0; i < features.length; i++ ) {
			Feature f = features[i];
			if( f.getName().equals("confidence.classname") ) {
				att_index_confidence = i;
				for (String s : TaskInformation.getClassNames(apiconnector,t)) {
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
