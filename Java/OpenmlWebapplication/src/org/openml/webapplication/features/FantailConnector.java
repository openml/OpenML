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

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Constants;
import org.openml.apiconnector.xml.DataQuality;
import org.openml.apiconnector.xml.DataQuality.Quality;
import org.openml.apiconnector.xml.DataQualityUpload;
import org.openml.apiconnector.xml.DataSetDescription;
import org.openml.apiconnector.xstream.XstreamXmlMapping;
import org.openml.webapplication.fantail.dc.Characterizer;
import org.openml.webapplication.fantail.dc.StreamCharacterizer;
import org.openml.webapplication.fantail.dc.landmarking.GenericLandmarker;
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
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToNominal;

public class FantailConnector {
	private final Integer window_size;
	
	private static final XStream xstream = XstreamXmlMapping.getInstance();
	private Characterizer[] batchCharacterizers = {
		new Statistical(), new AttributeCount(), new AttributeType(),
		new ClassAtt(), new DefaultAccuracy(),
		new IncompleteInstanceCount(), new InstanceCount(),
		new MissingValues(), new NominalAttDistinctValues(),
		new AttributeEntropy(), 

		new GenericLandmarker( "NaiveBayes", "weka.classifiers.bayes.NaiveBayes", 2, null ),
		new GenericLandmarker( "NBTree", "weka.classifiers.trees.NBTree", 2, null ),
		new GenericLandmarker( "DecisionStump", "weka.classifiers.trees.DecisionStump", 2, null ),
		new GenericLandmarker( "SimpleLogistic", "weka.classifiers.functions.SimpleLogistic", 2, null ),
		new GenericLandmarker( "JRip", "weka.classifiers.rules.JRip", 2, null )
	};
	
	private static StreamCharacterizer[] streamCharacterizers;
	private static OpenmlConnector apiconnector;
	
	public FantailConnector( OpenmlConnector ac, Integer dataset_id ) throws Exception {
		int expectedQualities = 8; // start of with 8 basic qualities, apparently
		apiconnector = ac;
		window_size = null;
		
		// additional batch landmarkers
		TreeMap<String, String[]> REPOptions = new TreeMap<String, String[]>();
		TreeMap<String, String[]> J48Options = new TreeMap<String, String[]>();
		TreeMap<String, String[]> RandomTreeOptions = new TreeMap<String, String[]>();
		TreeMap<String, String[]> kNNOptions = new TreeMap<String, String[]>();
		TreeMap<String, String[]> smoPolyOptions = new TreeMap<String, String[]>();
		String zeros = "0";
		for( int i = 1; i <= 3; ++i ) {
			zeros += "0";
			String[] repOption = { "-L", "" + i };
			REPOptions.put( "Depth" + i, repOption );

			String[] j48Option = { "-C", "." + zeros + "1" };
			J48Options.put( "." + zeros + "1.", j48Option );
			
			String[] randomtreeOption = { "-depth", "" + i };
			RandomTreeOptions.put( "Depth" + i, randomtreeOption );
					
			String[] kNNOption = { "-K", "" + i };
			kNNOptions.put( "_" + i + "N", kNNOption );
			
			String[] smoPolyOption = { "-M", "-K", "weka.classifiers.functions.supportVector.PolyKernel -E "+i+".0" };
			smoPolyOptions.put( "e" + i, smoPolyOption );
		}
		
		batchCharacterizers = ArrayUtils.add( batchCharacterizers, new GenericLandmarker( "REPTree", "weka.classifiers.trees.REPTree", 2, REPOptions ) );
		batchCharacterizers = ArrayUtils.add( batchCharacterizers, new GenericLandmarker( "J48", "weka.classifiers.trees.J48", 2, J48Options ) );
		batchCharacterizers = ArrayUtils.add( batchCharacterizers, new GenericLandmarker( "RandomTree", "weka.classifiers.trees.RandomTree", 2, RandomTreeOptions ) );
		batchCharacterizers = ArrayUtils.add( batchCharacterizers, new GenericLandmarker( "kNN", "weka.classifiers.lazy.IBk", 2, kNNOptions ) );
		batchCharacterizers = ArrayUtils.add( batchCharacterizers, new GenericLandmarker( "SVM", "weka.classifiers.functions.SMO", 2, smoPolyOptions ) );
		
		for( Characterizer characterizer : batchCharacterizers ) {
			expectedQualities += characterizer.getNumMetaFeatures();
		}
		
		if( dataset_id != null ) {
			Conversion.log( "OK", "Process Dataset", "Processing dataset " + dataset_id + " on special request. " );
			extractFeatures( dataset_id, window_size );
		} else {
			dataset_id = getDatasetId( expectedQualities, window_size );
			while( dataset_id != null ) {
				Conversion.log( "OK", "Process Dataset", "Processing dataset " + dataset_id + " as obtained from database. " );
				extractFeatures( dataset_id, window_size );
				dataset_id = getDatasetId( expectedQualities, window_size );
			}
			Conversion.log( "OK", "Process Dataset", "No more datasets to process. " );
		}
	}
	
	public Integer getDatasetId( int expectedQualities, Integer window_size ) throws JSONException, Exception {
		String sql = 
			"SELECT `d`.`did`, `q`.`value` AS `numInstances`, `interval_end` - `interval_start` AS `interval_size`, " +
			"CEIL(`q`.`value` / " + window_size + ") AS `numIntervals`, " +
			"(COUNT(*) / CEIL(`q`.`value` / " + window_size + ")) AS `qualitiesPerInterval`, " +
			"COUNT(*) AS `qualities` " +
			"FROM `data_quality` `q`, `dataset` `d`" +
			"LEFT JOIN `data_quality_interval` `i` ON `d`.`did` = `i`.`data` AND `i`.`interval_end` - `i`.`interval_start` =  " + window_size + " " +
			"WHERE `q`.`quality` IS NOT NULL " +
			"AND `d`.`did` = `q`.`data` " +
			"AND `q`.`quality` = 'NumberOfInstances'  " +
			"AND `d`.`error` = 'false' AND `d`.`processed` IS NOT NULL " +
			"AND `d`.`did` IN " + 
			"(SELECT i.value AS did FROM task_inputs i, task_tag t WHERE t.id = i.task_id AND i.input = 'source_data' AND t.tag = 'streams') " +
			"GROUP BY `d`.`did` " +
			"HAVING (COUNT(*) / CEIL(`q`.`value` / " + window_size + ")) < " + expectedQualities + " " +
			"ORDER BY `qualitiesPerInterval` ASC; ";
		
		if( window_size == null ) {
			sql = "SELECT data, COUNT(*) AS `numQualities` FROM data_quality GROUP BY data HAVING numQualities < " + expectedQualities;
		}
		
		Conversion.log( "OK", "FantailQuery", sql );
		JSONArray runJson = (JSONArray) apiconnector.freeQuery( sql ).get("data");
		
		Random random = new Random( System.currentTimeMillis() );
		
		if( runJson.length() > 0 ) {
			int dataset_id = ((JSONArray) runJson.get( Math.abs( random.nextInt() ) % runJson.length() ) ).getInt( 0 );
			return dataset_id;
		} else {
			return null;
		}
	}
	
	private boolean extractFeatures(Integer did, Integer interval_size) throws Exception {
		Conversion.log( "OK", "Extract Features", "Start extracting features for dataset: " + did );
		// TODO: initialize this properly!!!!!!
		streamCharacterizers = new StreamCharacterizer[1]; 
		streamCharacterizers[0] = new ChangeDetectors( interval_size );
		
		DataSetDescription dsd = apiconnector.dataGet(did);
		
		Conversion.log( "OK", "Extract Features", "Start downloading dataset: " + did );
		
		Instances dataset = new Instances( new FileReader(dsd.getDataset(apiconnector.getApiKey())) );
		
		dataset.setClass( dataset.attribute( dsd.getDefault_target_attribute() ) );
		

		// first run stream characterizers
		Conversion.log( "OK", "Extract Features", "Running Stream Characterizers (full data)" );
		
		for( StreamCharacterizer sc : streamCharacterizers ) {
			sc.characterize( dataset );
		}
		
		List<Quality> qualities = new ArrayList<DataQuality.Quality>();
		if( interval_size != null ) {
			Conversion.log( "OK", "Extract Features", "Running Batch Characterizers (partial data)" );
			
			for( int i = 0; i < dataset.numInstances(); i += interval_size ) {
				if( apiconnector.getVerboselevel() >= Constants.VERBOSE_LEVEL_ARFF ) {
					Conversion.log( "OK", "FantailConnector", "Starting window [" + i + "," + (i + interval_size) + "> (did = " + did + ",total size = " + dataset.numInstances() + ")" );
				}
				qualities.addAll( datasetCharacteristics( dataset, i, interval_size ) );
				
				for( StreamCharacterizer sc : streamCharacterizers ) {
					qualities.addAll( hashMaptoList( sc.interval( i ), i, interval_size ) );
				}
			}
			
		} else {
			Conversion.log( "OK", "Extract Features", "Running Batch Characterizers (full data, might take a while)" );
			qualities.addAll( datasetCharacteristics( dataset, null, null ) );
			for( StreamCharacterizer sc : streamCharacterizers ) {
				Map<String, Double> streamqualities = sc.global();
				qualities.addAll( hashMaptoList( streamqualities, null, null ) );
			}
		}
		Conversion.log( "OK", "Extract Features", "Done generating features, start wrapping up" );
		
		DataQuality dq = new DataQuality(did, qualities.toArray( new Quality[qualities.size()] ) );
		String strQualities = xstream.toXML(dq);
		
		DataQualityUpload dqu = apiconnector.dataQualitiesUpload(
				Conversion.stringToTempFile(strQualities, "qualities_did_"
						+ did, "xml"));
		Conversion.log( "OK", "Extract Features", "DONE: " + dqu.getDid() );
		
		return true;
	}

	private List<Quality> datasetCharacteristics( Instances fulldata, Integer start, Integer interval_size ) throws Exception {
		List<Quality> result = new ArrayList<DataQuality.Quality>();
		Instances intervalData;
		
		// Be careful changing this!
		if( interval_size != null ) {
			intervalData = new Instances( fulldata, start, Math.min( interval_size, fulldata.numInstances() - start ) );
			intervalData = applyFilter( intervalData, new StringToNominal(), "-R first-last" );
			intervalData.setClassIndex( fulldata.classIndex() );
		} else {
			intervalData = fulldata;
			// todo: use StringToNominal filter? might be to expensive
		}
		
		for( Characterizer dc : batchCharacterizers ) {
			Map<String,Double> qualities = dc.characterize(intervalData);
			result.addAll( hashMaptoList( qualities, start, interval_size ) );
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
	
	private static Instances applyFilter( Instances dataset, Filter filter, String options ) throws Exception {
		((OptionHandler) filter).setOptions( Utils.splitOptions( options ) );
		filter.setInputFormat(dataset);
		return Filter.useFilter(dataset, filter);
	}
}
