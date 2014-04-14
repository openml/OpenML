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
import org.openml.webapplication.fantail.dc.NFoldCrossValidationBased;

import weka.core.Instances;

public class SimpleLandmarkers extends Characterizer implements
		NFoldCrossValidationBased {

	private int m_NumFolds = 2;

	@Override
	public void setNumFolds(int n) {
		m_NumFolds = n;
	}

	protected final String[] ids = new String[] { "DecisionStumpErrRate",
			"DecisionStumpAUC", "NBErrRate", "NBAUC", "DecisionStumpKappa",
			"NBKappa" };

	public String[] getIDs() {
		return ids;
	}

	public Map<String, Double> characterize(Instances data) {

		int numFolds = m_NumFolds;

		double score1 = 0.5;
		double score2 = 0.5;

		double score5 = 0.5;
		double score6 = 0.5;

		double score3 = 0.5;
		double score4 = 0.5;

		weka.classifiers.trees.DecisionStump ds = new weka.classifiers.trees.DecisionStump();
		try {

			weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(
					data);
			eval.crossValidateModel(ds, data, numFolds, new java.util.Random(1));

			score1 = eval.pctIncorrect();
			score2 = eval.weightedAreaUnderROC();
			score3 = eval.kappa();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			weka.classifiers.bayes.NaiveBayes nb = new weka.classifiers.bayes.NaiveBayes();

			weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(
					data);
			eval.crossValidateModel(nb, data, numFolds, new java.util.Random(1));

			score5 = eval.pctIncorrect();
			score6 = eval.weightedAreaUnderROC();
			score4 = eval.kappa();

		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, Double> qualities = new HashMap<String, Double>();
		qualities.put(ids[0], score1);
		qualities.put(ids[1], score2);
		qualities.put(ids[2], score5);
		qualities.put(ids[3], score6);
		qualities.put(ids[4], score3);
		qualities.put(ids[5], score4);
		return qualities;
	}
}
