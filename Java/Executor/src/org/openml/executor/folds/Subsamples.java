package org.openml.executor.folds;

import java.io.BufferedReader;
import java.util.ArrayList;

import org.openml.apiconnector.algorithms.Input;
import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Task;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class Subsamples {

	private final Instances[][][][] subsamples;
	private final ArrayList<Integer>[][][] rowids;
	
	public final int REPEATS;
	public final int FOLDS;
	public final int SAMPLES;
	public final int DATASET_ID;
	public final Task TASK;
	
	@SuppressWarnings("unchecked")
	public Subsamples( OpenmlConnector connector, int task_id ) throws Exception {
		TASK = connector.openmlTaskGet( task_id );
		
		int numRepeats = 1;
		int numFolds = 1;
		int numSamples = 1;
		try { numFolds = TaskInformation.getNumberOfRepeats(TASK); } catch (Exception e) { }
		try { numFolds = TaskInformation.getNumberOfFolds(TASK); } catch (Exception e) { }
		try { numSamples = TaskInformation.getNumberOfSamples(TASK); } catch (Exception e) { }
		
		DATASET_ID = TaskInformation.getSourceData(TASK).getData_set_id();
		REPEATS = numRepeats;
		FOLDS = numFolds;
		SAMPLES = numSamples;
		
		DataSetDescription dsd = connector.openmlDataDescription( DATASET_ID );
		String splitsUrl = TaskInformation.getEstimationProcedure( TASK ).getData_splits_url();
		Instances dataset = new Instances( new BufferedReader( Input.getURL( dsd.getUrl() + "?session_hash=" + connector.getSessionHash() ) ) );
		Instances datasplits = new Instances( new BufferedReader( Input.getURL( splitsUrl ) ) );
		
		subsamples = new Instances[REPEATS][FOLDS][SAMPLES][2];
		rowids = new ArrayList[REPEATS][FOLDS][SAMPLES];
		for( int repeats = 0; repeats < REPEATS; ++repeats ) {
			for( int folds = 0; folds < FOLDS; ++folds ) {
				for( int samples = 0; samples < SAMPLES; ++samples ) {
					for( int i = 0; i < 2; ++i ) {
						subsamples[repeats][folds][samples][i] = new Instances(dataset, 0);
						rowids[repeats][folds][samples] = new ArrayList<Integer>();
					}
				}
			}
		}
		
		Attribute attRowid = datasplits.attribute("rowid");
		Attribute attRepeat = datasplits.attribute("repeat");
		Attribute attFold   = datasplits.attribute("fold");
		Attribute attSample = datasplits.attribute("sample");
		Attribute attType = datasplits.attribute("type");
		for( int i = 0; i < datasplits.numInstances(); ++i ) {
			Instance instanceMeta = datasplits.get( i );
			int rowid  = (int) instanceMeta.value( attRowid );
			int repeat = attRepeat == null ? 0 : (int) instanceMeta.value( attRepeat );
			int fold   = attFold   == null ? 0 : (int) instanceMeta.value( attFold );
			int sample = attSample == null ? 0 : (int) instanceMeta.value( attSample );
			boolean train = attType.value( (int) instanceMeta.value( attType ) ).equals("TRAIN");
			
			Instance instanceBase = dataset.get( rowid );
			subsamples[repeat][fold][sample][train == true ? 0 : 1].add(instanceBase);
			if( train == false ) {
				rowids[repeat][fold][sample].add( rowid );
			}
		}
	}
	
	public Instances getTrainingSet( int repeat, int fold ) {
		return subsamples[repeat][fold][0][0];
	}
	
	public Instances getTrainingSet( int repeat, int fold, int sample ) {
		return subsamples[repeat][fold][sample][0];
	}
	
	public Instances getTestSet( int repeat, int fold ) {
		return subsamples[repeat][fold][0][1];
	}
	
	public Instances getTestSet( int repeat, int fold, int sample ) {
		return subsamples[repeat][fold][sample][1];
	}
	
	public ArrayList<Integer> getTestSetRowIds( int repeat, int fold ) {
		return rowids[repeat][fold][0];
	}
	
	public ArrayList<Integer> getTestSetRowIds( int repeat, int fold, int sample ) {
		return rowids[repeat][fold][sample];
	}
}
