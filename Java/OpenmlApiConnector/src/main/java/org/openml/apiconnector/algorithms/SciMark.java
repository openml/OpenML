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

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import jnt.scimark2.Constants;
import jnt.scimark2.Random;
import jnt.scimark2.kernel;

public class SciMark implements Serializable {
	
	private static final long serialVersionUID = -5563065042084199486L;
	private boolean benchmarkDone;
	private double results[] = new double[5];
	private double average;
	
	String[] os = new String[5];
	
	public static void main( String[] args ) {
		SciMark scimark = new SciMark();
		String[] d = scimark.getOsInfo();
		System.out.println("Operating System: " + d[3] + " v" + d[4] + "\nJava version: " + d[1] + " by " + d[0] + "\nSystem architecture: " + d[2]  );
		System.out.println("Composite score: " + scimark.doBenchmark() );
		System.out.println("[ " + StringUtils.join( scimark.getStringArray(), ", " ) + " ]" );
		
	}
	
	/**
	 * Initiates a new SciMark instance. 
	 */
	public SciMark() {
		benchmarkDone = false;
		
		os[0] = System.getProperty("java.vendor");
		os[1] = System.getProperty("java.version");
		os[2] = System.getProperty("os.arch");
		os[3] = System.getProperty("os.name");
		os[4] = System.getProperty("os.version");
	}
	
	/**
	 * @return scores of the SciMark benchmark
	 */
	public double doBenchmark() {
		double min_time = Constants.RESOLUTION_DEFAULT;

		int FFT_size = Constants.FFT_SIZE;
		int SOR_size =  Constants.SOR_SIZE;
		int Sparse_size_M = Constants.SPARSE_SIZE_M;
		int Sparse_size_nz = Constants.SPARSE_SIZE_nz;
		int LU_size = Constants.LU_SIZE;
		
		Random R = new Random(Constants.RANDOM_SEED);

		results[0] = kernel.measureFFT( FFT_size, min_time, R);
		results[1] = kernel.measureSOR( SOR_size, min_time, R);
		results[2] = kernel.measureMonteCarlo(min_time, R);
		results[3] = kernel.measureSparseMatmult( Sparse_size_M, 
					Sparse_size_nz, min_time, R);
		results[4] = kernel.measureLU( LU_size, min_time, R);


		average = ( results[0] + results[1] + results[2] + results[3] + results[4]) / 5;
		benchmarkDone = true;
		return average;
	}
	
	/**
	 * @return Array containing basic information about the OS. 
	 */
	public String[] getOsInfo() {
		return os;
	}
	
	/**
	 * @return Scores from the benchmark converted to Strings. 
	 */
	public String[] getStringArray() {
		String[] res = new String[results.length];
		for(int i = 0; i < results.length; ++i) {
			res[i] = "" + results[i];
		}
		return res;
	}
	
	/**
	 * @return The average of all SciMark benchmarks
	 */
	public double getResult() {
		if( benchmarkDone == false )
			doBenchmark();
		return average;
	}
}
