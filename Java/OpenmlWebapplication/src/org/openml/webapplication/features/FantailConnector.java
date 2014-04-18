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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.io.ApiSessionHash;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
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
import org.openml.webapplication.io.Output;

import com.thoughtworks.xstream.XStream;

import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class FantailConnector {
	
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	private static final Characterizer[] allCharacterizers = {
			new Statistical(), new AttributeCount(), new AttributeType(),
			new ClassAtt(), new DefaultAccuracy(),
			new IncompleteInstanceCount(), new InstanceCount(),
			new MissingValues(), new NominalAttDistinctValues(),
			new AttributeEntropy(), new SimpleLandmarkers(),
			new J48BasedLandmarker(), new REPTreeBasedLandmarker(),
			new RandomTreeBasedLandmarker() 
	};

	public static void extractFeatures(Integer did, String datasetClass,
			Config config) throws Exception {
		List<String> prevCalcQualities;
		ApiConnector apiconnector;
		
		if( config.getServer() != null ) {
			apiconnector = new ApiConnector( config.getServer() );
		} else { 
			apiconnector = new ApiConnector();
		} 
		
		DataSetDescription dsd = apiconnector.openmlDataDescription(did);
		
		try {
			DataQuality apiQualities = apiconnector.openmlDataQuality(did);
			prevCalcQualities = Arrays.asList( apiQualities.getQualityNames() );
		} catch( Exception e ) {
			// no qualities calculated yet. We might want to avoid catching this error
			prevCalcQualities = new ArrayList<String>();
		}
		
		List<String> uncalculatedQualities = characteristicsAvailable();
		uncalculatedQualities.removeAll( prevCalcQualities );
		
		if( uncalculatedQualities.size() == 0 ) {
			System.out.println(Output.statusMessage("OK", "No new Fantail Features from data #" + did));
			return;
		}
		Map<String, Double> qualities = datasetCharacteristics(dsd.getUrl(), datasetClass);

		DataQuality dq = new DataQuality(did, qualities);
		String strQualities = xstream.toXML(dq);
		ApiSessionHash ash = new ApiSessionHash( apiconnector );
		ash.set(config.getUsername(), config.getPassword());

		apiconnector.openmlDataQualityUpload(
				Conversion.stringToTempFile(strQualities, "qualities_did_"
						+ did, "xml"), ash.getSessionHash());

		System.out.println(Output.statusMessage("OK",
				"Successfully extracted Fantail Features from data #" + did));
	}

	public static List<String> characteristicsAvailable() {
		List<String> allCharacteristics = new ArrayList<String>();
		
		for( Characterizer dc : allCharacterizers ) {
			allCharacteristics.addAll( Arrays.asList( dc.getIDs() ) );
		}
		
		return allCharacteristics;
	}

	public static Map<String, Double> datasetCharacteristics(String datasetUrl,
			String datasetClass) throws Exception {
		ArffLoader arffLoader = new ArffLoader();
		arffLoader.setURL(datasetUrl);

		Instances data = arffLoader.getDataSet();

		if (datasetClass != null) {
			data.setClass(data.attribute(datasetClass));
		} else {
			data.setClassIndex(data.numAttributes() - 1);
		}

		Map<String, Double> dcValues = new HashMap<String, Double>();
		
		for( Characterizer dc : allCharacterizers ) {
			dcValues.putAll(dc.characterize(data));
		}

		return dcValues;
	}
}
