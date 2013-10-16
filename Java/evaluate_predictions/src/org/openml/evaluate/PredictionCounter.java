package org.openml.evaluate;

import java.util.ArrayList;
import java.util.Collections;

import weka.core.AttributeStats;
import weka.core.Instance;
import weka.core.Instances;

public class PredictionCounter {
	//private final int ATT_PREDICTION_ROWID;
	//private final int ATT_PREDICTION_REPEAT;
	//private final int ATT_PREDICTION_FOLD;
	
	private final int ATT_SPLITS_TYPE;
	private final int ATT_SPLITS_ROWID;
	private final int ATT_SPLITS_REPEAT;
	private final int ATT_SPLITS_FOLD;
	
	private final int NR_OF_REPEATS;
	private final int NR_OF_FOLDS;
	
	private final ArrayList<Integer>[][] expected;
	private final ArrayList<Integer>[][] actual;
	private int expectedTotal;
	
	private String error_message;
	
	public PredictionCounter( Instances splits ) {
		this(splits,"TEST");
	}
	
	@SuppressWarnings("unchecked")
	public PredictionCounter( Instances splits, String type ) {
		ATT_SPLITS_TYPE = splits.attribute("type").index();
		ATT_SPLITS_ROWID = splits.attribute("rowid").index();
		ATT_SPLITS_REPEAT = splits.attribute("repeat").index();
		ATT_SPLITS_FOLD = splits.attribute("fold").index();

		AttributeStats repeatStats = splits.attributeStats( ATT_SPLITS_REPEAT );
		AttributeStats foldStats = splits.attributeStats( ATT_SPLITS_FOLD );

		NR_OF_REPEATS = (int) repeatStats.numericStats.max + 1;
		NR_OF_FOLDS = (int) foldStats.numericStats.max + 1;

		expectedTotal = 0;
		expected = new ArrayList[NR_OF_REPEATS][NR_OF_FOLDS];
		actual   = new ArrayList[NR_OF_REPEATS][NR_OF_FOLDS];
		for( int i = 0; i < NR_OF_REPEATS; i++ ) for( int j = 0; j < NR_OF_FOLDS; j++ ) {
			expected[i][j] = new ArrayList<Integer>();
			actual[i][j]   = new ArrayList<Integer>();
		}
		
		for( int i = 0; i < splits.numInstances(); i++ ) {
			Instance instance = splits.instance( i );
			if( instance.value( ATT_SPLITS_TYPE ) == splits.attribute( ATT_SPLITS_TYPE ).indexOfValue(type) ) {
				int repeat = (int) instance.value( ATT_SPLITS_REPEAT );
				int fold   = (int) instance.value( ATT_SPLITS_FOLD );
				int rowid  = (int) instance.value( ATT_SPLITS_ROWID );
				expected[repeat][fold].add( rowid );
				expectedTotal++;
			}
		}
		
		for( int i = 0; i < NR_OF_REPEATS; i++ ) {
			for( int j = 0; j < NR_OF_FOLDS; j++ ){
				Collections.sort( expected[i][j] );
			}
		}
		
		error_message = "";
	}
	
	public void addPrediction( int repeat, int fold, int rowid ) {
		if( actual.length <= repeat )
			throw new RuntimeException("Repeat #"+repeat+" not defined by task. ");
		if( actual[repeat].length <= fold )
			throw new RuntimeException("Fold #"+fold+" not defined by task. ");
		actual[repeat][fold].add( rowid );
	}
	
	public boolean check() {
		for( int i = 0; i < NR_OF_REPEATS; i++ ) {
			for( int j = 0; j < NR_OF_FOLDS; j++ ){
				Collections.sort( actual[i][j] );
				if( actual[i][j].equals( expected[i][j] ) == false ) {
					error_message = "Repeat " + i + " fold " + j + " expected predictions with row id's " + 
									expected[i][j] + " , but got predictions with row id's " + actual[i][j];
					return false;
				}
			}
		}
		return true;
	}
	
	public ArrayList<Integer> getExpectedRowids(int i, int j) {
		return expected[i][j];
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
	
	public int getExpectedTotal() {
		return expectedTotal;
	}
}
