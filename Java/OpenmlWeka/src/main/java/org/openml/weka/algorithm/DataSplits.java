package org.openml.weka.algorithm;

import java.util.ArrayList;
import java.util.List;

import org.openml.apiconnector.algorithms.TaskInformation;
import org.openml.apiconnector.xml.Task;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class DataSplits {

	private final Instances[][][][] subsamples;
	private final ArrayList<Integer>[][][] rowids;

	public final int REPEATS;
	public final int FOLDS;
	public final int SAMPLES;
	public final int DATASET_ID;

	@SuppressWarnings("unchecked")
	public DataSplits(Task task, Instances dataset, Instances datasplits) throws Exception {
		int numRepeats = TaskInformation.getNumberOfRepeats(task);
		int numFolds = TaskInformation.getNumberOfFolds(task);
		int numSamples = 1;
		try {numSamples = TaskInformation.getNumberOfSamples(task);} catch (Exception e) {}

		DATASET_ID = TaskInformation.getSourceData(task).getData_set_id();
		REPEATS = numRepeats;
		FOLDS = numFolds;
		SAMPLES = numSamples;
		
		subsamples = new Instances[REPEATS][FOLDS][SAMPLES][2];
		rowids = new ArrayList[REPEATS][FOLDS][SAMPLES];
		for (int repeats = 0; repeats < REPEATS; ++repeats) {
			for (int folds = 0; folds < FOLDS; ++folds) {
				for (int samples = 0; samples < SAMPLES; ++samples) {
					for (int i = 0; i < 2; ++i) {
						subsamples[repeats][folds][samples][i] = new Instances(dataset, 0);
						rowids[repeats][folds][samples] = new ArrayList<Integer>();
					}
				}
			}
		}

		Attribute attRowid = datasplits.attribute("rowid");
		Attribute attRepeat = datasplits.attribute("repeat");
		Attribute attFold = datasplits.attribute("fold");
		Attribute attSample = datasplits.attribute("sample");
		Attribute attType = datasplits.attribute("type");
		for (int i = 0; i < datasplits.numInstances(); ++i) {
			Instance instanceMeta = datasplits.get(i);
			int rowid = (int) instanceMeta.value(attRowid);
			int repeat = attRepeat == null ? 0 : (int) instanceMeta.value(attRepeat);
			int fold = attFold == null ? 0 : (int) instanceMeta.value(attFold);
			int sample = attSample == null ? 0 : (int) instanceMeta.value(attSample);
			boolean train = attType.value((int) instanceMeta.value(attType)).equals("TRAIN");

			Instance instanceBase = dataset.get(rowid);
			subsamples[repeat][fold][sample][train == true ? 0 : 1].add(instanceBase);
			if (train == false) {
				rowids[repeat][fold][sample].add(rowid);
			}
		}
	}

	public Instances getTrainingSet(int repeat, int fold, int sample) {
		return subsamples[repeat][fold][sample][0];
	}

	public Instances getTestSet(int repeat, int fold, int sample) {
		return subsamples[repeat][fold][sample][1];
	}

	public List<Integer> getTestSetRowIds(int repeat, int fold, int sample) {
		return rowids[repeat][fold][sample];
	}
}
