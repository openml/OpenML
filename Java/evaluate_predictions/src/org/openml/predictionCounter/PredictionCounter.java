package org.openml.predictionCounter;

public interface PredictionCounter {
	
	public int getRepeats();
	public int getFolds();
	public int getSamples();
	public String getErrorMessage();
	public int getShadowTypeSize( int repeat, int fold, int sample );
	
	public void addPrediction( int repeat, int fold, int sample, int prediction );
	public boolean check();
}
