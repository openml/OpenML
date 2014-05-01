package org.openml.webapplication.fantail.dc.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.core.Instance;
import weka.core.Instances;
import moa.classifiers.Classifier;
import moa.core.InstancesHeader;
import moa.core.Measurement;
import moa.core.ObjectRepository;
import moa.options.AbstractOptionHandler;
import moa.options.ClassOption;
import moa.tasks.NullMonitor;
import moa.tasks.TaskMonitor;

public class RunChangeDetectorTask extends AbstractOptionHandler {

	private static final long serialVersionUID = -6617634692316208778L;

	private ClassOption[] allDriftDetectors;
	
	private final List<String> relevantMeasurements;
	
	public Map<String, Double> globalCharacteristics;
	public Map<Integer, Map<String, Double>> intervalCharacteristics;
	
	public ClassOption driftDetector1Option = new ClassOption( 
			"NaiveBayesDdm", 'a', 
			"Calculates meta feature based on NaiveBayes/DDM", 
			Classifier.class, 
			"drift.DriftDetectionMethodClassifier" );
	
	public ClassOption driftDetector2Option = new ClassOption( 
			"NaiveBayesAdwin", 'b', 
			"Calculates meta feature based on NaiveBayes/Adwin", 
			Classifier.class, 
			"drift.DriftDetectionMethodClassifier -d ADWINChangeDetector" );
	
	public ClassOption driftDetector3Option = new ClassOption( 
			"HoeffdingDDM", 'c', 
			"Calculates meta feature based on Hoeffding/DDM", 
			Classifier.class, 
			"drift.DriftDetectionMethodClassifier -l trees.HoeffdingTree" );
	
	public ClassOption driftDetector4Option = new ClassOption( 
			"HoeffdingAdwin", 'd', 
			"Calculates meta feature based on Hoeffding/Adwin", 
			Classifier.class, 
			"drift.DriftDetectionMethodClassifier -l trees.HoeffdingTree -d ADWINChangeDetector" );
	
	private final int interval_size;
	
	public String[] getIDs() {
		String[] ids = new String[allDriftDetectors.length * 4];
		
		for( int i = 0; i < allDriftDetectors.length; ++i ) {
			ids[i*4+0] = allDriftDetectors[i].getName() + ".changes";
			ids[i*4+1] = allDriftDetectors[i].getName() + ".warnings";
			ids[i*4+2] = allDriftDetectors[i].getName() + ".changes.total";
			ids[i*4+3] = allDriftDetectors[i].getName() + ".changes.total";
		}
		return ids;
	}

	public RunChangeDetectorTask( Integer interval_size ) {
		this.interval_size = interval_size != null ? interval_size : Integer.MAX_VALUE; // we don't need to "interval"-ize
		allDriftDetectors = new ClassOption[4];
		allDriftDetectors[0] = driftDetector1Option;
		allDriftDetectors[1] = driftDetector2Option;
		allDriftDetectors[2] = driftDetector3Option;
		allDriftDetectors[3] = driftDetector4Option;

		relevantMeasurements = new ArrayList<String>();
		relevantMeasurements.add("Change detected");
		relevantMeasurements.add("Warning detected");
	}
	

	public Map<String, Double> characterize(Instances instances) {
		prepareForUseImpl( new NullMonitor(), null );
		intervalCharacteristics = new HashMap<Integer, Map<String,Double>>();
		globalCharacteristics = new HashMap<String, Double>();
		
		InstancesHeader streamHeader = new InstancesHeader( instances );
		
		for( int iClassifiers = 0; iClassifiers < allDriftDetectors.length; ++iClassifiers ) {
			Classifier learner = (Classifier) getPreparedClassOption( allDriftDetectors[iClassifiers] );
			learner.setModelContext( streamHeader );
			boolean allProcessed = true;
			
			int previousIntervalStart = 0;
			for( int iInstances = 0; iInstances < instances.size(); ++iInstances ) {
				allProcessed = false;
				Instance trainInst = instances.instance( iInstances );
				
				learner.trainOnInstance( trainInst );
				
				if( (iInstances + 1) % interval_size == 0 ) {
					processIntervalMeasures( learner, iClassifiers, previousIntervalStart );
					previousIntervalStart = iInstances + 1;
					allProcessed = true;
				}
			}
			if( !allProcessed ) {
				processIntervalMeasures( learner, iClassifiers, previousIntervalStart );
			}
		}
		
		// now fill global characteristics, just by summing all interval measures
		for( Integer interval : intervalCharacteristics.keySet() ) {
			Map<String, Double> probe = intervalCharacteristics.get( interval );
			for( String measure : probe.keySet() ) {
				if( globalCharacteristics.containsKey( measure ) == false ) {
					globalCharacteristics.put( measure, probe.get( measure ) );
				} else {
					globalCharacteristics.put( measure, globalCharacteristics.get(measure) + probe.get( measure ) );
				}
			}
		}
		
		return globalCharacteristics;
	}
	
	public Map<String, Double> interval(int interval_start) {
		return intervalCharacteristics.get( interval_start );
	}
	
	public Map<String, Double> global() {
		return globalCharacteristics;
	}
	
	@Override
	public void getDescription(StringBuilder sb, int indent) {
		sb.append( "Runs various Change Detector methods over a datastream for the purpose of landmarking. " );
	}

	@Override
	protected void prepareForUseImpl(TaskMonitor monitor,
			ObjectRepository repository) {
        prepareClassOptions(monitor, repository);
	}
	
	public void processIntervalMeasures( Classifier learner, int classifierIndex, int previousIntervalStart ) {
		// get relevant characteristics
		Map<String, Double> currentMeasurements = getMeasurements( learner, relevantMeasurements );

		// create characteristics for interval
		if( intervalCharacteristics.containsKey( previousIntervalStart ) == false ) {
			intervalCharacteristics.put( previousIntervalStart, new HashMap<String, Double>() );
		}
		// obtain characteristics for interval
		Map<String, Double> target = intervalCharacteristics.get( previousIntervalStart );
		
		// and fill that interval with values for current window
		String baseKey = allDriftDetectors[classifierIndex].getName();
		target.put( baseKey + ".changes", currentMeasurements.get("Change detected") );
		target.put( baseKey + ".warnings", currentMeasurements.get("Warning detected") );
	}
	
	private static Map<String, Double> getMeasurements( Classifier learner, List<String> included ) {
		Measurement[] measurements = learner.getModelMeasurements();
		
		Map<String, Double> measurementMap = new HashMap<String, Double>();
		for( Measurement m : measurements ) {
			if( included.contains( m.getName() ) ) {
				measurementMap.put( m.getName(), m.getValue());
			}
		}
		
		return measurementMap;
	}
}
