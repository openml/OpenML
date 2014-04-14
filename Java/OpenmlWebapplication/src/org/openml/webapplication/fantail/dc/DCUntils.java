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
package org.openml.webapplication.fantail.dc;

import java.util.Enumeration;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class DCUntils {

	public static double computeClassEntropy(Instances data) {

		double[] classValueCounts = new double[data.numClasses()];
		for (int i = 0; i < data.numInstances(); i++) {
			Instance inst = data.instance(i);
			classValueCounts[(int) inst.classValue()]++;
		}
		double classEntropy = 0;
		for (int c = 0; c < data.numClasses(); c++) {
			double prob_c = classValueCounts[c] / data.numInstances();
			classEntropy += prob_c * (weka.core.Utils.log2(prob_c));
		}
		classEntropy = classEntropy * -1.0;

		if (Double.isNaN(classEntropy)) {
			classEntropy = -1;
		}

		return classEntropy;
	}

	public static double computeMeanAttributeEntropy(Instances data) {

		int numNomAtt = 0;
		double meanAttributeEntropy = 0;

		for (int attIndex = 0; attIndex < data.numAttributes(); attIndex++) {

			if (data.attribute(attIndex).isNominal()
					&& (data.classIndex() != attIndex)) {
				numNomAtt++;

				double[] attValueCounts = new double[data
						.numDistinctValues(attIndex)];

				for (int i = 0; i < data.numInstances(); i++) {
					Instance inst = data.instance(i);
					attValueCounts[(int) inst.value(attIndex)]++;
				}

				double attEntropy = 0;
				for (int c = 0; c < data.numDistinctValues(attIndex); c++) {
					double prob_c = attValueCounts[c] / data.numInstances();
					attEntropy += prob_c * (weka.core.Utils.log2(prob_c));
				}
				attEntropy = attEntropy * -1.0;
				meanAttributeEntropy += attEntropy;
			}
		}

		if (numNomAtt == 0) {
			return -1;
		}

		double value = meanAttributeEntropy / numNomAtt;

		if (Double.isNaN(value)) {
			value = -1;
		}

		return value;
	}

	public static double computeMutualInformation(Instances data) {

		double meanMI = 0;
		double numNomAtt = 0;

		for (int attIndex = 0; attIndex < data.numAttributes(); attIndex++) {
			if (data.attribute(attIndex).isNominal()
					&& (data.classIndex() != attIndex)) {
				numNomAtt++;
				try {
					meanMI += computeInfoGain(data, data.attribute(attIndex));
				} catch (Exception e) {
					meanMI += 0;
				}
			}
		}

		if (numNomAtt == 0) {
			return -1;
		}

		double MI = meanMI / numNomAtt;

		if (Double.isNaN(MI)) {
			MI = -1;
		}

		return MI;
	}

	private static Instances[] splitData(Instances data, Attribute att) {

		Instances[] splitData = new Instances[att.numValues()];
		for (int j = 0; j < att.numValues(); j++) {
			splitData[j] = new Instances(data, data.numInstances());
		}
		Enumeration<?> instEnum = data.enumerateInstances();
		while (instEnum.hasMoreElements()) {
			Instance inst = (Instance) instEnum.nextElement();
			splitData[(int) inst.value(att)].add(inst);
		}
		for (Instances splitData1 : splitData) {
			splitData1.compactify();
		}
		return splitData;
	}

	private static double computeInfoGain(Instances data, Attribute att)
			throws Exception {

		double infoGain = computeEntropy(data);
		Instances[] splitData = splitData(data, att);
		for (int j = 0; j < att.numValues(); j++) {
			if (splitData[j].numInstances() > 0) {
				infoGain -= ((double) splitData[j].numInstances() / (double) data
						.numInstances()) * computeEntropy(splitData[j]);
			}
		}
		return infoGain;
	}

	private static double computeEntropy(Instances data) throws Exception {

		double[] classCounts = new double[data.numClasses()];
		Enumeration<?> instEnum = data.enumerateInstances();
		while (instEnum.hasMoreElements()) {
			Instance inst = (Instance) instEnum.nextElement();
			classCounts[(int) inst.classValue()]++;
		}
		double entropy = 0;
		for (int j = 0; j < data.numClasses(); j++) {
			if (classCounts[j] > 0) {
				entropy -= classCounts[j] * Utils.log2(classCounts[j]);
			}
		}
		entropy /= (double) data.numInstances();
		return entropy + Utils.log2(data.numInstances());
	}
}
