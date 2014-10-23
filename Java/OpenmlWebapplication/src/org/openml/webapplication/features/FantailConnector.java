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
import java.util.List;
import java.util.Map;

import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.DataQuality.Quality;
import org.openml.apiconnector.xml.DataQualityUpload;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.openml.webapplication.fantail.dc.Characterizer;
import org.openml.webapplication.fantail.dc.StreamCharacterizer;
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
import org.openml.webapplication.fantail.dc.stream.ChangeDetectors;

import com.thoughtworks.xstream.XStream;

import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class FantailConnector {
	
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	private static final Characterizer[] batchCharacterizers = {
			new Statistical(), new AttributeCount(), new AttributeType(),
			new ClassAtt(), new DefaultAccuracy(),
			new IncompleteInstanceCount(), new InstanceCount(),
			new MissingValues(), new NominalAttDistinctValues(),
			new AttributeEntropy(), new SimpleLandmarkers(),
			new J48BasedLandmarker(), new REPTreeBasedLandmarker(),
			new RandomTreeBasedLandmarker() 
	};
	
	private static StreamCharacterizer[] streamCharacterizers;
	
	public static boolean extractFeatures(Integer did, String datasetClass, Integer interval_size,
			Config config) throws Exception {
		Conversion.log( "OK", "Extract Features", "Start extracting features for dataset: " + did );
		// TODO: initialize this properly!!!!!!
		streamCharacterizers = new StreamCharacterizer[1]; 
		streamCharacterizers[0] = new ChangeDetectors( interval_size );
		
		//List<String> prevCalcQualities;
		OpenmlConnector apiconnector;
		
		if( config.getServer() != null ) {
			apiconnector = new OpenmlConnector( config.getServer(), config.getUsername(), config.getPassword() );
		} else { 
			apiconnector = new OpenmlConnector( config.getUsername(), config.getPassword() );
		} 
		
		DataSetDescription dsd = apiconnector.openmlDataDescription(did);
		
		Conversion.log( "OK", "Extract Features", "Start downloading dataset: " + did );
		
		ArffLoader datasetLoader = new ArffLoader();
		datasetLoader.setURL(dsd.getUrl());
		Instances dataset = new Instances( datasetLoader.getDataSet() );
		if( datasetClass == null ) {
			datasetClass = dataset.attribute( dataset.numAttributes() - 1 ).name();
		}
		dataset.setClass( dataset.attribute( datasetClass ) );
		

		// first run stream characterizers
		Conversion.log( "OK", "Extract Features", "Running Stream Characterizers (full data)" );
		
		for( StreamCharacterizer sc : streamCharacterizers ) {
			sc.characterize( dataset );
		}
		
		List<Quality> qualities = new ArrayList<DataQuality.Quality>();
		if( interval_size != null ) {
			Conversion.log( "OK", "Extract Features", "Running Batch Characterizers (partial data)" );
			
			for( int i = 0; i < dataset.numInstances(); i += interval_size ) {
				qualities.addAll( datasetCharacteristics( dataset, i, interval_size ) );
				
				for( StreamCharacterizer sc : streamCharacterizers ) {
					qualities.addAll( hashMaptoList( sc.interval( i ), i, interval_size ) );
				}
			}
			
		} else {
			Conversion.log( "OK", "Extract Features", "Running Batch Characterizers (full data, might take a while)" );
			qualities.addAll( datasetCharacteristics( dataset, null, null ) );
			for( StreamCharacterizer sc : streamCharacterizers ) {
				qualities.addAll( hashMaptoList( sc.global( ), null, null ) );
			}
		}
		Conversion.log( "OK", "Extract Features", "Done generating features, start wrapping up" );
		
		DataQuality dq = new DataQuality(did, qualities.toArray( new Quality[qualities.size()] ) );
		String strQualities = xstream.toXML(dq);
		
		DataQualityUpload dqu = apiconnector.openmlDataQualityUpload(
				Conversion.stringToTempFile(strQualities, "qualities_did_"
						+ did, "xml"));
		Conversion.log( "OK", "Extract Features", "DONE: " + dqu.getDid() );
		
		return true;
	}

	private static List<Quality> datasetCharacteristics( Instances fulldata, Integer start, Integer interval_size ) throws Exception {
		List<Quality> result = new ArrayList<DataQuality.Quality>();
		Instances intervalData;
		
		// Be careful changing this!
		if( interval_size != null ) {
			intervalData = new Instances( fulldata, start, Math.min( interval_size, fulldata.numInstances() - start ) );
			intervalData.setClassIndex( fulldata.classIndex() );
		} else {
			intervalData = fulldata;
		}
		
		for( Characterizer dc : batchCharacterizers ) {
			result.addAll( hashMaptoList( dc.characterize(intervalData), start, interval_size ) );
			
		}
		
		return result;
	}
	
	public static List<Quality> hashMaptoList( Map<String, Double> map, Integer start, Integer size ) {
		List<Quality> result = new ArrayList<DataQuality.Quality>();
		for( String quality : map.keySet() ) {
			Integer end = start != null ? start + size : null;
			result.add( new Quality( quality, map.get( quality ) + "", start, end ) );
		}
		return result;
	}
}
