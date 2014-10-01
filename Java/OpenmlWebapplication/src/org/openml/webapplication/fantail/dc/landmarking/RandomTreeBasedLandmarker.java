/*
 *  Webapplication - Java library that runs on OpenML servers
 *  Copyright (C) 2014 
 *  @author Jan N. van Rijn (j.n.van.rijn@liacs.leidenuniv.nl)
 *  @author Quan Sun (quan.sun.nz@gmail.com)
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
package org.openml.webapplication.fantail.dc.landmarking;

import java.util.HashMap;
import java.util.Map;

import org.openml.webapplication.fantail.dc.Characterizer;
import org.openml.webapplication.fantail.dc.Randomizable;

import weka.core.Instances;

public class RandomTreeBasedLandmarker extends Characterizer implements
		Randomizable {

	private int m_NumFolds = 2;
	private int m_K = 0;
	private int m_Seed = 1;

	@Override
	public void setSeed(int seed) {
		m_Seed = seed;
	}

	public void setK(int k) {
		m_K = k;
	}

	protected final String[] ids = new String[] {
			"RandomTreeDepth1AUC_K_" + m_K, "RandomTreeDepth2AUC_K_" + m_K,
			"RandomTreeDepth3AUC_K_" + m_K };

	public String[] getIDs() {
		return ids;
	}

	public Map<String, Double> characterize(Instances data) {

		int numFolds = m_NumFolds;

		double score1 = 0.5;
		double score2 = 0.5;
		double score3 = 0.5;

		weka.classifiers.trees.RandomTree cls = new weka.classifiers.trees.RandomTree();
		cls.setSeed(m_Seed);
		cls.setMaxDepth(1);

		try {
			// ds.buildClassifier(data);
			weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(
					data);
			eval.crossValidateModel(cls, data, numFolds,
					new java.util.Random(1));

			score1 = eval.weightedAreaUnderROC();

		} catch (Exception e) {
			e.printStackTrace();
		}

		//
		cls = new weka.classifiers.trees.RandomTree();
		cls.setSeed(m_Seed);
		cls.setMaxDepth(2);

		try {

			weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(
					data);
			eval.crossValidateModel(cls, data, numFolds,
					new java.util.Random(1));

			score2 = eval.weightedAreaUnderROC();

		} catch (Exception e) {
			e.printStackTrace();
		}

		//
		cls = new weka.classifiers.trees.RandomTree();
		cls.setSeed(m_Seed);
		cls.setMaxDepth(3);

		try {

			weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(
					data);
			eval.crossValidateModel(cls, data, numFolds,
					new java.util.Random(1));

			score3 = eval.weightedAreaUnderROC();

		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, Double> qualities = new HashMap<String, Double>();
		qualities.put(ids[0], score1);
		qualities.put(ids[1], score2);
		qualities.put(ids[2], score3);
		return qualities;
	}
}
