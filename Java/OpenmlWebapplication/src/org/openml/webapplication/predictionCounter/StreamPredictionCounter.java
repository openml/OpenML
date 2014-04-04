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
package org.openml.webapplication.predictionCounter;

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
