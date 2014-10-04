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
import java.util.Random;

import org.openml.webapplication.fantail.dc.Characterizer;
import org.openml.webapplication.fantail.dc.Randomizable;

import weka.core.Instances;

public class RandomTreeBasedLandmarker2 extends Characterizer implements
		Randomizable {

	private int m_Seed = 1;
	private int m_NumFolds = 2;
	private int m_K = 1;

	protected final String[] ids = new String[] {
			"RandomTreeDepth1ErrRate_K_" + m_K,
			"RandomTreeDepth1Kappa_K_" + m_K,
			"RandomTreeDepth2ErrRate_K_" + m_K,
			"RandomTreeDepth2Kappa_K_" + m_K,
			"RandomTreeDepth3ErrRate_K_" + m_K,
			"RandomTreeDepth3Kappa_K_" + m_K };

	public String[] getIDs() {
		return ids;
	}

	@Override
	public void setSeed(int seed) {
		m_Seed = seed;
	}

	public void setK(int k) {
		m_K = k;
	}

	public void setNumFolds(int n) {
		m_NumFolds = n;
	}

	public Map<String, Double> characterize(Instances data) {

		int seed = m_Seed;
		Random r = new Random(seed);

		int numFolds = m_NumFolds;

		double score1 = 0.5;
		double score2 = 0.5;
		// double score3 = 0.5;

		double score3 = 0.5;
		double score4 = 0.5;
		// double score3 = 0.5;

		double score5 = 0.5;
		double score6 = 0.5;

		weka.classifiers.trees.RandomTree cls = new weka.classifiers.trees.RandomTree();
		cls.setSeed(r.nextInt());
		cls.setKValue(m_K);
		// cls.setMaxDepth(1);

		try {
			// ds.buildClassifier(data);
			weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(
					data);
			eval.crossValidateModel(cls, data, numFolds,
					new java.util.Random(1));

			score1 = eval.pctIncorrect();
			score2 = eval.kappa();

		} catch (Exception e) {
			e.printStackTrace();
		}

		//
		cls = new weka.classifiers.trees.RandomTree();
		cls.setSeed(r.nextInt());
		cls.setKValue(m_K);
		// cls.setMaxDepth(2);

		try {

			weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(
					data);
			eval.crossValidateModel(cls, data, numFolds,
					new java.util.Random(1));

			score3 = eval.pctIncorrect();
			score4 = eval.kappa();

		} catch (Exception e) {
			e.printStackTrace();
		}

		//
		cls = new weka.classifiers.trees.RandomTree();
		cls.setSeed(r.nextInt());
		cls.setKValue(m_K);
		// cls.setMaxDepth(3);

		try {

			weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(
					data);
			eval.crossValidateModel(cls, data, numFolds,
					new java.util.Random(1));

			score5 = eval.pctIncorrect();
			score6 = eval.kappa();

		} catch (Exception e) {
			e.printStackTrace();
		}

		//
		cls = new weka.classifiers.trees.RandomTree();
		cls.setSeed(r.nextInt());
		cls.setKValue(m_K);
		// cls.setMaxDepth(4);

		try {

			weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(
					data);
			eval.crossValidateModel(cls, data, numFolds,
					new java.util.Random(1));

		} catch (Exception e) {
			e.printStackTrace();
		}

		//
		cls = new weka.classifiers.trees.RandomTree();
		cls.setSeed(r.nextInt());
		cls.setKValue(m_K);
		// cls.setMaxDepth(5);

		try {
			weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(
					data);
			eval.crossValidateModel(cls, data, numFolds,
					new java.util.Random(1));

		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, Double> qualities = new HashMap<String, Double>();
		qualities.put(ids[0], score1);
		qualities.put(ids[1], score2);
		qualities.put(ids[2], score3);
		qualities.put(ids[3], score4);
		qualities.put(ids[4], score5);
		qualities.put(ids[5], score6);
		return qualities;
	}
}
