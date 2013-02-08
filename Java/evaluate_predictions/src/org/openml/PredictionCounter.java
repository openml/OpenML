package org.openml;

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
	
	@SuppressWarnings("unchecked")
	public PredictionCounter( Instances splits ) {
		
		ATT_SPLITS_TYPE = splits.attribute("type").index();
		ATT_SPLITS_ROWID = splits.attribute("rowid").index();
		ATT_SPLITS_REPEAT = splits.attribute("repeat").index();
		ATT_SPLITS_FOLD = splits.attribute("fold").index();

		AttributeStats repeatStats = splits.attributeStats( ATT_SPLITS_REPEAT );
		AttributeStats foldStats = splits.attributeStats( ATT_SPLITS_FOLD );

		NR_OF_REPEATS = (int) repeatStats.numericStats.max + 1;
		NR_OF_FOLDS = (int) foldStats.numericStats.max + 1;
		
		expected = new ArrayList[NR_OF_REPEATS][NR_OF_FOLDS];
		for( int i = 0; i < NR_OF_REPEATS; i++ ) for( int j = 0; j < NR_OF_FOLDS; j++ ) expected[i][j] = new ArrayList<Integer>();
		
		for( int i = 0; i < splits.numInstances(); i++ ) {
			Instance instance = splits.instance( i );
			if( instance.value( ATT_SPLITS_TYPE ) == splits.attribute( ATT_SPLITS_TYPE ).indexOfValue("TEST") ) {
				int repeat = (int) instance.value( ATT_SPLITS_REPEAT );
				int fold   = (int) instance.value( ATT_SPLITS_FOLD );
				int rowid  = (int) instance.value( ATT_SPLITS_ROWID );
				expected[repeat][fold].add( rowid );
			}
		}
		
		for( int i = 0; i < NR_OF_REPEATS; i++ ) {
			for( int j = 0; j < NR_OF_FOLDS; j++ ){
				Collections.sort( expected[i][j] );
			}
		}
	}
}
