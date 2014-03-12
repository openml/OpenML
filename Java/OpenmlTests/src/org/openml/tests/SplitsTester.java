package org.openml.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;

import openml.algorithms.TaskInformation;
import openml.io.ApiConnector;
import openml.io.ApiException;
import openml.xml.Task;

import org.junit.Test;

import weka.core.Instance;
import weka.core.Instances;

public class SplitsTester {

	@Test
	@SuppressWarnings("unchecked")
	public void testLearningCurves() {
		
		for( int i = 1; i <= 10; ++i ) {
			try {
				Task t = ApiConnector.openmlTasksSearch( i );
				if( t.getTask_type().equals("Learning Curve") ) {
					System.out.println( "Task " + t.getTask_id() );
					Instances splits = TaskInformation.getEstimationProcedure(t).getData_splits();
					Instances data = TaskInformation.getSourceData(t).getDataSetDescription().getDataset();
					String[] classValues = TaskInformation.getClassNames(t);
					data.setClass( data.attribute( TaskInformation.getSourceData(t).getTarget_feature() ) );
					
					int repeats = TaskInformation.getNumberOfRepeats(t);
					int folds = TaskInformation.getNumberOfFolds(t);
					int samples = TaskInformation.getNumberOfSamples(t);

					ArrayList<Integer>[][][] train = new ArrayList[repeats][folds][samples];
					ArrayList<Integer>[][][] test  = new ArrayList[repeats][folds][samples];
					
					int[][][][] classesTrain = new int[repeats][folds][samples][classValues.length];
					int[][][][] classesTest  = new int[repeats][folds][samples][classValues.length];
					int[] classesTotal = new int[classValues.length];
					
					for( int c = 0; c < data.numInstances(); ++c ) {
						classesTotal[(int) data.instance(c).classValue()]++;
					}
					
					for( int r = 0; r < repeats; ++r ) {for( int f  = 0; f < folds; ++f ) {for( int s = 0; s < samples; ++s ){
						train[r][f][s] = new ArrayList<Integer>();
						test[r][f][s]  = new ArrayList<Integer>();
					}}}
						
					for( int j = 0; j < splits.numInstances(); ++j ) {
						Instance row = splits.instance( j );
						
						int type   = (int) row.value( splits.attribute("type") );
						int rowid  = (int) row.value( splits.attribute("rowid") );
						int repeat = (int) row.value( splits.attribute("repeat") );
						int fold   = (int) row.value( splits.attribute("fold") );
						int sample = (int) row.value( splits.attribute("sample") );
						int target = (int) data.instance( rowid ).classValue();
						
						if( type == 0 ) {
							train[repeat][fold][sample].add(rowid);
							classesTrain[repeat][fold][sample][target]++;
						} else {
							test[repeat][fold][sample].add(rowid);
							classesTest[repeat][fold][sample][target]++;
						}
					}
					
					for( int r = 0; r < repeats; ++r ) {
						for( int f  = 0; f < folds; ++f ) {
							for( int s = 0; s < samples; ++s ) {

								if( s+1 < samples ) { // all until last round
									// check for correct size of samples. 
									assertTrue( train[r][f][s].size() == Math.round( Math.pow(2, 6.0 + (0.5 * s) ) ) );
								}
								
								if( s > 0 ) { // all after round 0
									// check whether we contain all previous instances.
									assertTrue( train[r][f][s].containsAll( train[r][f][s-1] ) );
									// check whether test sets are the same
									assertTrue( test[r][f][s].equals( test[r][f][s-1] ) );
								}
								
								if( s+1 == samples ) {
									int totalsize = test[r][f][s].size() + train[r][f][s].size();
									assertTrue( Collections.disjoint(test[r][f][s], train[r][f][s]));
									assertTrue( totalsize == data.numInstances() );
								}
								// TODO: stratification. 
								for( int c = 0; c < classValues.length; ++c ) {
									double totalRatio = classesTotal[c] * 1.0 / data.numInstances();
									
									assertTrue( withinBounds( (int) classesTrain[r][f][s][c], (int) train[r][f][s].size(), totalRatio ) );
									assertTrue( withinBounds( (int) classesTest[r][f][s][c], (int) test[r][f][s].size(), totalRatio ) );
								}
							}
						}
					}
				}
			} catch( Exception e ) {
				if( e instanceof ApiException && ( (ApiException) e ).getCode() == 151 ) {
					// OK
				} else {
					e.printStackTrace();
					fail();
				}
			}
		}
	}
	
	private boolean withinBounds( double setSize, double totalSize, double targetSize ) {
		if( (setSize - 1) / totalSize > targetSize || (setSize + 1) / totalSize < targetSize )
			return false;
		return true;
	}
}
