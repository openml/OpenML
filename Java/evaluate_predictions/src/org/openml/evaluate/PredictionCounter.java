package org.openml.evaluate;

import java.util.ArrayList;
import java.util.Collections;

import weka.core.AttributeStats;
import weka.core.Instance;
import weka.core.Instances;

public class PredictionCounter {
	
	private final int ATT_SPLITS_TYPE;
	private final int ATT_SPLITS_ROWID;
	private final int ATT_SPLITS_REPEAT;
	private final int ATT_SPLITS_FOLD;
	private final int ATT_SPLITS_SAMPLE;
	
	private final int NR_OF_REPEATS;
	private final int NR_OF_FOLDS;
	private final int NR_OF_SAMPLES;
	
	private final ArrayList<Integer>[][][] expected;
	private final ArrayList<Integer>[][][] actual;
	private int expectedTotal;
	
	private final int[][][] shadowTypeSize;
	
	private String error_message;
	
	public PredictionCounter( Instances splits ) {
		this(splits,"TEST","TRAIN");
	}
	
	@SuppressWarnings("unchecked")
	public PredictionCounter( Instances splits, String type, String shadowType ) {
		ATT_SPLITS_TYPE = splits.attribute("type").index();
		ATT_SPLITS_ROWID = splits.attribute("rowid").index();
		ATT_SPLITS_REPEAT = splits.attribute("repeat").index();
		ATT_SPLITS_FOLD = splits.attribute("fold").index();
		ATT_SPLITS_SAMPLE = splits.attribute("sample") == null ? -1 : splits.attribute("sample").index();

		AttributeStats repeatStats = splits.attributeStats( ATT_SPLITS_REPEAT );
		NR_OF_REPEATS = (int) repeatStats.numericStats.max + 1;
		
		AttributeStats foldStats = splits.attributeStats( ATT_SPLITS_FOLD );
		NR_OF_FOLDS = (int) foldStats.numericStats.max + 1;

		NR_OF_SAMPLES = splits.attribute("sample") == null ? 1 : (int) splits.attributeStats( ATT_SPLITS_SAMPLE ).numericStats.max + 1;
		
		expectedTotal = 0;
		expected = new ArrayList[NR_OF_REPEATS][NR_OF_FOLDS][NR_OF_SAMPLES];
		actual   = new ArrayList[NR_OF_REPEATS][NR_OF_FOLDS][NR_OF_SAMPLES];
		shadowTypeSize = new int[NR_OF_REPEATS][NR_OF_FOLDS][NR_OF_SAMPLES];
		for( int i = 0; i < NR_OF_REPEATS; i++ ) for( int j = 0; j < NR_OF_FOLDS; j++ ) for( int k = 0; k < NR_OF_SAMPLES; k++ ) {
			expected[i][j][k] = new ArrayList<Integer>();
			actual[i][j][k]   = new ArrayList<Integer>();
		}
		
		for( int i = 0; i < splits.numInstances(); i++ ) {
			Instance instance = splits.instance( i );
			if( instance.value( ATT_SPLITS_TYPE ) == splits.attribute( ATT_SPLITS_TYPE ).indexOfValue(type) ) {
				int repeat = (int) instance.value( ATT_SPLITS_REPEAT );
				int fold   = (int) instance.value( ATT_SPLITS_FOLD );
				int sample = ATT_SPLITS_SAMPLE < 0 ? 0 : (int) instance.value( ATT_SPLITS_SAMPLE );
				int rowid  = (int) instance.value( ATT_SPLITS_ROWID ); //TODO: maybe we need instance.stringValue() ...
				expected[repeat][fold][sample].add( rowid );
				expectedTotal++;
			} else if( instance.value( ATT_SPLITS_TYPE ) == splits.attribute( ATT_SPLITS_TYPE ).indexOfValue( shadowType ) ) {
				int repeat = (int) instance.value( ATT_SPLITS_REPEAT );
				int fold   = (int) instance.value( ATT_SPLITS_FOLD );
				int sample = ATT_SPLITS_SAMPLE < 0 ? 0 : (int) instance.value( ATT_SPLITS_SAMPLE );
				
				shadowTypeSize[repeat][fold][sample]++;
			}
		}
		
		for( int i = 0; i < NR_OF_REPEATS; i++ ) {
			for( int j = 0; j < NR_OF_FOLDS; j++ ){
				for( int k = 0; k < NR_OF_SAMPLES; k++ ){
					Collections.sort( expected[i][j][k] );
				}
			}
		}
		
		error_message = "";
	}
	
	public void addPrediction( int repeat, int fold, int sample, int rowid ) {
		if( actual.length <= repeat )
			throw new RuntimeException("Repeat #"+repeat+" not defined by task. ");
		if( actual[repeat].length <= fold )
			throw new RuntimeException("Fold #"+fold+" not defined by task. ");
		actual[repeat][fold][sample].add( rowid );
	}
	
	public boolean check() {
		for( int i = 0; i < NR_OF_REPEATS; i++ ) {
			for( int j = 0; j < NR_OF_FOLDS; j++ ){
				for( int k = 0; k < NR_OF_SAMPLES; k++ ){
					Collections.sort( actual[i][j][k] );
					if( actual[i][j][k].equals( expected[i][j][k] ) == false ) {
						error_message = "Repeat " + i + " fold " + j + " sample " + k + " expected predictions with row id's " + 
										expected[i][j][k] + " , but got predictions with row id's " + actual[i][j][k];
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public ArrayList<Integer> getExpectedRowids(int i, int j, int k) {
		return expected[i][j][k];
	}
	
	public int getShadowTypeSize(int i, int j, int k) {
		return shadowTypeSize[i][j][k];
	}
	
	public String getErrorMessage() {
		return error_message;
	}
	
	public int getRepeats() {
		return NR_OF_REPEATS;
	}
	
	public int getFolds() {
		return NR_OF_FOLDS;
	}
	
	public int getSamples() {
		return NR_OF_SAMPLES;
	}
	
	public int getExpectedTotal() {
		return expectedTotal;
	}
}
