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
package org.openml.apiconnector.algorithms;

import java.io.BufferedReader;
import java.io.FileReader;

import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xml.Task;
import org.openml.apiconnector.xml.Task.Input.Data_set;
import org.openml.apiconnector.xml.Task.Input.Estimation_procedure;
import org.openml.apiconnector.xml.Task.Output.Predictions;

public class TaskInformation {

	/**
	 * @param t - Input Task. 
	 * @return The number of repeats
	 * @throws Exception
	 */
	public static int getNumberOfRepeats( Task t ) throws Exception {
		Estimation_procedure ep = getEstimationProcedure(t);
		for(int i = 0; i < ep.getParameters().length; ++i) {
			if(ep.getParameters()[i].getName().equals("number_repeats") ) {
				return Integer.parseInt(ep.getParameters()[i].getValue());
			}
		}
		throw new Exception("Tasks estimation procedure does not contain \"number_repeats\"");
	}

	/**
	 * @param t - Input Task. 
	 * @return The number of samples
	 * @throws Exception
	 */
	public static int getNumberOfSamples( Task t ) throws Exception {
		Estimation_procedure ep = getEstimationProcedure(t);
		for(int i = 0; i < ep.getParameters().length; ++i) {
			if(ep.getParameters()[i].getName().equals("number_samples") ) {
				return Integer.parseInt(ep.getParameters()[i].getValue());
			}
		}
		throw new Exception("Tasks estimation procedure does not contain \"number_samples\"");
	}

	/**
	 * @param t - Input Task. 
	 * @return The number of folds
	 * @throws Exception
	 */
	public static int getNumberOfFolds( Task t ) throws Exception {
		Estimation_procedure ep = getEstimationProcedure(t);
		for(int i = 0; i < ep.getParameters().length; ++i) {
			if(ep.getParameters()[i].getName().equals("number_folds") ) {
				return Integer.parseInt(ep.getParameters()[i].getValue());
			}
		}
		throw new Exception("Tasks estimation procedure does not contain \"number_folds\"");
	}
	
	/**
	 * @param t - Input Task. 
	 * @return The estimation procedure
	 * @throws Exception
	 */
	public static Estimation_procedure getEstimationProcedure( Task t ) throws Exception {
		for( int i = 0; i < t.getInputs().length; ++i ) {
			if(t.getInputs()[i].getName().equals("estimation_procedure") ) {
				return t.getInputs()[i].getEstimation_procedure();
			}
		}
		throw new Exception("Task does not define an estimation procedure. ");
	}
	
	/**
	 * @param t - Input Task. 
	 * @return The source data
	 * @throws Exception
	 */
	public static Data_set getSourceData( Task t ) throws Exception {
		for( int i = 0; i < t.getInputs().length; ++i ) {
			if(t.getInputs()[i].getName().equals("source_data") ) {
				return t.getInputs()[i].getData_set();
			}
		}
		throw new Exception("Task does not define an estimation procedure. ");
	}
	
	/**
	 * @param t - Input Task. 
	 * @return The prediction format
	 * @throws Exception
	 */
	public static Predictions getPredictions( Task t ) throws Exception {
		for( int i = 0; i < t.getOutputs().length; ++i ) {
			if(t.getOutputs()[i].getName().equals("predictions") ) {
				return t.getOutputs()[i].getPredictions();
			}
		}
		throw new Exception("Task does not define an predictions. ");
	}
	
	/**
	 * @param t - Input Task. 
	 * @return The classnames of the input data
	 * @throws Exception
	 */
	public static String[] getClassNames( Task t ) throws Exception {
		DataSetDescription dsd = getSourceData(t).getDataSetDescription();
		String targetFeature = getSourceData(t).getTarget_feature();
		BufferedReader br = new BufferedReader( new FileReader( dsd.getDataset() ) );
		
		String line;
		
		while( (line = br.readLine()) != null) {
			if( ArffHelper.isDataDeclaration(line) ) throw new Exception("Attribute not found.");
			if( ArffHelper.isAttributeDeclaration(line) ) {
				try {
					if( ArffHelper.getAttributeName( line ).equals( targetFeature ) ) {
						br.close();
						return ArffHelper.getNominalValues( line );
					}
				} catch( Exception e ) {/*Not going to happen*/}
			}
		}
		br.close();
		throw new Exception("Attribute not found.");
	}
}
