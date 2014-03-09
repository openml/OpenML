package org.openml.predictionCounter;

public class StreamPredictionCounter implements PredictionCounter {

	@Override
	public int getRepeats() {
		return 1;
	}

	@Override
	public int getFolds() {
		return 1;
	}

	@Override
	public int getSamples() {
		return 1;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public int getShadowTypeSize(int repeat, int fold, int sample) {
		return 0;
	}

	@Override
	public void addPrediction(int repeat, int fold, int sample, int prediction) {
		// TODO: implement
	}

	@Override
	public boolean check() {
		// TODO: implement
		return true;
	}
	
}
