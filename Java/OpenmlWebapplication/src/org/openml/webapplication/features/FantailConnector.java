/*
 *  Webapplication - Java library that runs on OpenML servers
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
package org.openml.webapplication.features;

import java.util.HashMap;
import java.util.Map;

import org.openml.webapplication.fantail.dc.Characterizer;
import org.openml.webapplication.fantail.dc.landmarking.J48BasedLandmarker;
import org.openml.webapplication.fantail.dc.landmarking.REPTreeBasedLandmarker;
import org.openml.webapplication.fantail.dc.landmarking.RandomTreeBasedLandmarker;
import org.openml.webapplication.fantail.dc.landmarking.SimpleLandmarkers;
import org.openml.webapplication.fantail.dc.statistical.AttributeCount;
import org.openml.webapplication.fantail.dc.statistical.AttributeEntropy;
import org.openml.webapplication.fantail.dc.statistical.AttributeType;
import org.openml.webapplication.fantail.dc.statistical.ClassAtt;
import org.openml.webapplication.fantail.dc.statistical.DefaultAccuracy;
import org.openml.webapplication.fantail.dc.statistical.IncompleteInstanceCount;
import org.openml.webapplication.fantail.dc.statistical.InstanceCount;
import org.openml.webapplication.fantail.dc.statistical.MissingValues;
import org.openml.webapplication.fantail.dc.statistical.NominalAttDistinctValues;
import org.openml.webapplication.fantail.dc.statistical.Statistical;

import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class FantailConnector {
	
	public static void extractFeatures(String datasetUrl, String datasetClass) throws Exception {
		Map<String, Double> qualities = datasetCharacteristics( datasetUrl, datasetClass );
		
		System.out.println( qualities );
	}
	
	public static Map<String, Double> datasetCharacteristics(String datasetUrl, String datasetClass) throws Exception {
		ArffLoader arffLoader = new ArffLoader();
		arffLoader.setURL(datasetUrl);
		
        Instances data = arffLoader.getDataSet();
        
        if( datasetClass != null ) {
        	data.setClass( data.attribute( datasetClass ) );
        } else {
        	data.setClassIndex( data.numAttributes() - 1 );
        }
    	
    	Map<String, Double> dcValues = new HashMap<String, Double>();
    	
        Characterizer dc = new Statistical();
        dcValues.putAll( dc.characterize(data) );

        dc = new AttributeCount();
        dcValues.putAll( dc.characterize(data) );

        dc = new AttributeType();
        dcValues.putAll( dc.characterize(data) );

        dc = new ClassAtt();
        dcValues.putAll( dc.characterize(data) );

        dc = new DefaultAccuracy();
        dcValues.putAll( dc.characterize(data) );

        dc = new IncompleteInstanceCount();
        dcValues.putAll( dc.characterize(data) );

        dc = new InstanceCount();
        dcValues.putAll( dc.characterize(data) );

        dc = new MissingValues();
        dcValues.putAll( dc.characterize(data) );

        dc = new NominalAttDistinctValues();
        dcValues.putAll( dc.characterize(data) );

        dc = new AttributeEntropy();
        dcValues.putAll( dc.characterize(data) );

        dc = new SimpleLandmarkers();
        dcValues.putAll( dc.characterize(data) );

        dc = new J48BasedLandmarker();
        dcValues.putAll( dc.characterize(data) );

        dc = new REPTreeBasedLandmarker();
        dcValues.putAll( dc.characterize(data) );

        dc = new RandomTreeBasedLandmarker();
        dcValues.putAll( dc.characterize(data) );
        
        return dcValues;
    }
}
