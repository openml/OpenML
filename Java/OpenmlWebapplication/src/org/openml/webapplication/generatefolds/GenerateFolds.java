/*
 *  OpenmlApiConnector - Java integration of the OpenML Web API
 *  Copyright (C) 2014 
 *  @author Jan N. van Rijn (j.n.van.rijn@liacs.leidenuniv.nl)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
package org.openml.webapplication.generatefolds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.openml.webapplication.algorithm.InstancesHelper;
import org.openml.webapplication.generatefolds.EvaluationMethod.EvaluationMethods;
import org.openml.webapplication.io.Input;
import org.openml.webapplication.io.Md5Writer;
import org.openml.webapplication.io.Output;

import weka.core.Attribute;
import weka.core.Instances;

public class GenerateFolds {
	public static final int MAX_SPLITS_SIZE = 1000000;
	
	private final Instances dataset;
	private final Instances splits;
	private final String splits_name;
	private final Integer splits_size;
	
	private final EvaluationMethod evaluationMethod;
	
	private final ArffMapping am;
	private final Random rand;
	
	public GenerateFolds( String datasetPath, String evaluation, String targetFeature, String rowid, int seed ) throws Exception {
		rand = new Random(seed);
		
		dataset = new Instances( new BufferedReader( Input.getURL( datasetPath ) ) );
		evaluationMethod = new EvaluationMethod(evaluation,dataset);
		
		InstancesHelper.setTargetAttribute( dataset, targetFeature );
		
		am = new ArffMapping( evaluationMethod.getEvaluationMethod() == EvaluationMethods.LEARNINGCURVE);
		
		splits_name = Input.filename( datasetPath ) + "_splits";
		splits_size = evaluationMethod.getSplitsSize(dataset);
		
		if(rowid.equals("")) {
			rowid = "rowid";
			addRowId(dataset,rowid);
		}
		
		splits = generateInstances(splits_name);
	}
	
	public void toFile( String splitsPath ) throws IOException {
		FileWriter f = new FileWriter( new File( splitsPath ) );
		Output.instanes2file(splits, f );
	}
	
	public void toStdout() throws IOException {
		Output.instanes2file(splits, new OutputStreamWriter( System.out ) );
	}
	
	public void toStdOutMd5() throws NoSuchAlgorithmException, IOException {
		Output.instanes2file(splits, new Md5Writer() );
	}
	
	private Instances generateInstances(String name) {
		Instances splits = new Instances(name,am.getArffHeader(),splits_size);
		switch(evaluationMethod.getEvaluationMethod()) {
			case HOLDOUT:
				for( int r = 0; r < evaluationMethod.getRepeats(); ++r) {
					dataset.randomize(rand);
					int testSetSize = Math.round(dataset.numInstances()*evaluationMethod.getPercentage()/100);
					
					for( int i = 0; i < dataset.numInstances(); ++i ) {
						int rowid = (int) dataset.instance(i).value(0);
						splits.add(am.createInstance(i >= testSetSize,rowid,r,0));
					}
				}
				break;
			case CROSSVALIDATION:
				for( int r = 0; r < evaluationMethod.getRepeats(); ++r) {
					dataset.randomize(rand);
					if (dataset.classAttribute().isNominal())
						dataset.stratify(evaluationMethod.getFolds());
					
					for( int f = 0; f < evaluationMethod.getFolds(); ++f ) {
						Instances train = dataset.trainCV(evaluationMethod.getFolds(), f);
						Instances test = dataset.testCV(evaluationMethod.getFolds(), f);
						
						for( int i = 0; i < train.numInstances(); ++i ) {
							int rowid = (int) train.instance(i).value(0);
							splits.add(am.createInstance(true,rowid,r,f));
						}
						for( int i = 0; i < test.numInstances(); ++i ) {
							int rowid = (int) test.instance(i).value(0);
							splits.add(am.createInstance(false,rowid,r,f));
						}
					}
				}
				break;
			case LEAVEONEOUT:
				for( int f = 0; f < dataset.numInstances(); ++f ) {
					for( int i = 0; i < dataset.numInstances(); ++i ) {
						int rowid = (int) dataset.instance(i).value(0);
						splits.add(am.createInstance(f!=i,rowid,0,f));
					}
				}
				break;
			case LEARNINGCURVE:
				for( int r = 0; r < evaluationMethod.getRepeats(); ++r ) {
					dataset.randomize(rand);
					if (dataset.classAttribute().isNominal())
						InstancesHelper.stratify(dataset); // do our own stratification
					
					for( int f = 0; f < evaluationMethod.getFolds(); ++f ) {
						Instances train = dataset.trainCV(evaluationMethod.getFolds(), f);
						Instances test = dataset.testCV(evaluationMethod.getFolds(), f);
						
						for( int s = 0; s < evaluationMethod.getNumberOfSamples( train.numInstances() ); ++s ) {
							for( int i = 0; i < evaluationMethod.sampleSize( s, train.numInstances() ); ++i ) {
								int rowid = (int) train.instance(i).value(0);
								splits.add(am.createInstance(true,rowid,r,f,s));
							}
							for( int i = 0; i < test.numInstances(); ++i ) {
								int rowid = (int) test.instance(i).value(0);
								splits.add(am.createInstance(false,rowid,r,f,s));
							}
						}
					}
				}
				break;
		}
		return splits;
	}
	
	private static Instances addRowId( Instances instances, String name ) {
		instances.insertAttributeAt(new Attribute(name), 0);
		for( int i = 0; i < instances.numInstances(); ++i )
			instances.instance(i).setValue(0, i);
		return instances;
	}
}
