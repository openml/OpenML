/*
 *    kNN.java
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program. If not, see <http://www.gnu.org/licenses/>.
 *    
 */
package moa.classifiers.lazy;

import moa.classifiers.AbstractClassifier;
import moa.options.*;
import moa.core.*;
import weka.core.*;
import weka.core.neighboursearch.*;
import java.io.*;
import java.util.ArrayList;

/**
 * k Nearest Neighbor.<p>
 *
 * Valid options are:<p>
 *
 * -k number of neighbours <br> -m max instances <br> 
 *
 * @author Jesse Read (jesse@tsc.uc3m.es)
 * @version 03.2012
 */
public class kNN extends AbstractClassifier {

    private static final long serialVersionUID = 1L;

	public IntOption kOption = new IntOption( "k", 'k', "The number of neighbors", 10, 1, Integer.MAX_VALUE);

	public IntOption limitOption = new IntOption( "limit", 'w', "The maximum number of instances to store", 1000, 1, Integer.MAX_VALUE);

        public MultiChoiceOption nearestNeighbourSearchOption = new MultiChoiceOption(
            "nearestNeighbourSearch", 'n', "Nearest Neighbour Search to use", new String[]{
                "LinearNN", "KDTree"},
            new String[]{"Brute force search algorithm for nearest neighbour search. ",
                "KDTree search algorithm for nearest neighbour search"
            }, 0);


	int C = 0;

    @Override
    public String getPurposeString() {
        return "kNN: special.";
    }

    protected Instances window; 

	@Override
	public void setModelContext(InstancesHeader context) {
		try {
			this.window = new Instances(new StringReader(context.toString()),0);
			this.window.setClassIndex(context.classIndex());
		} catch(Exception e) {
			System.err.println("Error: no Model Context available.");
			e.printStackTrace();
			System.exit(1);
		}
	}

    @Override
    public void resetLearningImpl() {
		this.window = null;
    }

    @Override
    public void trainOnInstanceImpl(Instance inst) {
		if (inst.classValue() > C)
			C = (int)inst.classValue();
		if (this.window == null) {
			this.window = new Instances(inst.dataset());
		}
		if (this.limitOption.getValue() <= this.window.numInstances()) {
			this.window.delete(0);
		}
		this.window.add(inst);
    }

	@Override
    public double[] getVotesForInstance(Instance inst) {
		double v[] = new double[C+1];
		try {
			NearestNeighbourSearch search;
			if (this.nearestNeighbourSearchOption.getChosenIndex()== 0) {
				search = new LinearNNSearch(this.window);  
			} else {
				search = new KDTree();
				search.setInstances(this.window);
			}	
			if (this.window.numInstances()>0) {	
				Instances neighbours = search.kNearestNeighbours(inst,Math.min(kOption.getValue(),this.window.numInstances()));
				for(int i = 0; i < neighbours.numInstances(); i++) {
					v[(int)neighbours.instance(i).classValue()]++;
				}
			}
		} catch(Exception e) {
			//System.err.println("Error: kNN search failed.");
			//e.printStackTrace();
			//System.exit(1);
			return new double[inst.numClasses()];
		}
		return v;
    }

    @Override
    protected Measurement[] getModelMeasurementsImpl() {
        return null;
    }

    @Override
    public void getModelDescription(StringBuilder out, int indent) {
    }

    public boolean isRandomizable() {
        return false;
    }
}
