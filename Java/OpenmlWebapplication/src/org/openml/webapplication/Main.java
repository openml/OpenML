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
package org.openml.webapplication;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.json.JSONArray;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;
import org.openml.webapplication.features.FantailConnector;
import org.openml.webapplication.generatefolds.GenerateFolds;
import org.openml.webapplication.io.Output;

public class Main {
	
	public static final int FOLD_GENERATION_SEED = 0;
	
	public static void main( String[] args ) {
		OpenmlConnector apiconnector;
		CommandLineParser parser = new GnuParser();
		Options options = new Options();
		Integer id = null;
		Config config;
		
		options.addOption("id", true, "The id of the dataset/run used");
		options.addOption("config", true, "The config string describing the settings for API interaction");
		options.addOption("f", true, "The function to invole");
		options.addOption("d", true, "The dataset used");
		options.addOption("c", true, "The target class");
		options.addOption("s", true, "The splitfile used");
		options.addOption("p", true, "The prediction file used");
		options.addOption("e", true, "The evaluation method");
		options.addOption("o", true, "The output file");
		options.addOption("r", true, "The rowid");
		options.addOption("t", true, "The task id");
		options.addOption("x", false, "Flag determining whether we should pick a random id");
		options.addOption("m", false, "Flag determining whether the output of the splits file should be presented as a md5 hash");
		options.addOption("test", true, "A list of rowids for a holdout set (fold generation)" );
		
		
		try {
			CommandLine cli  = parser.parse( options, args );
			if( cli.hasOption("-config") == false ) {
				config = new Config();
			} else {
				config = new Config( cli.getOptionValue("config") );
			}
			config.updateStaticSettings();
			apiconnector = new OpenmlConnector( config.getServer(), config.getApiKey() );
			
			if( cli.hasOption("-id") ) {
				id = Integer.parseInt( cli.getOptionValue("id") );
			}
			
			
			if( cli.hasOption("f") ) {
				
				String function = cli.getOptionValue("f");
				if( function.equals("evaluate_run") ) {
					
					
					// bootstrap evaluate run
					new EvaluateRun(apiconnector, id, cli.hasOption("x"));
					
				} else if( function.equals("process_dataset") ) {
					
					
					// bootstrap process dataset
					new ProcessDataset(apiconnector, id);
				} else if( function.equals("extract_features_all") ) {
					
					FantailConnector fc = new FantailConnector( apiconnector, id );
					fc.toString();
					
				} else if( function.equals("generate_folds") ) {
					
					// prepare ARFF output consisting of datasplits
					if( cli.hasOption("-d") && cli.hasOption("e") && cli.hasOption("c") ) {
						
						List<List<List<Integer>>> testset = new ArrayList<List<List<Integer>>>();
						if( cli.hasOption("-test") ) {
							JSONArray rowidsJson = new JSONArray(cli.getOptionValue("test"));
							
							
							for( int i = 0; i < rowidsJson.length(); ++i ) {
								while (testset.size() <= i) {
									testset.add(new ArrayList<List<Integer>>());
								}
								
								for( int j = 0; j < rowidsJson.getJSONArray(i).length(); ++j ) {
									while (testset.get(i).size() <= j) {
										testset.get(i).add(new ArrayList<Integer>());
									}
									
									for( int k = 0; k < rowidsJson.getJSONArray(i).getJSONArray(j).length(); ++k ) {
										
										testset.get(i).get(j).add(rowidsJson.getJSONArray(i).getJSONArray(j).getInt(k));
									}
									
								}
							}
						}
						
						apiconnector.setVerboseLevel(0);
						GenerateFolds gf = new GenerateFolds(
								apiconnector, 
								config.getApiKey(),
								cli.getOptionValue("d"), 
								cli.getOptionValue("e"), 
								cli.getOptionValue("c"), 
								testset, 
								FOLD_GENERATION_SEED );
						if(cli.hasOption("o") == true) {
							gf.toFile(cli.getOptionValue("o"));
						} else if(cli.hasOption("m") == true) {
							gf.toStdOutMd5();
						}else {
							gf.toStdout();
						}
					} else {
						System.out.println( Output.styleToJsonError("Missing arguments for function 'generate_folds'. Need d (url to dataset), c (target feature), e (evaluation_method, {cv_{repeats}_{folds},holdout_{repeats}_{percentage},leave_one_out}), and r (row_id, textual). ") );
					}
				} else if(function.equals("all_wrong")) {
					
					if( cli.hasOption("-r") && cli.hasOption("-t") ) {
						
						String[] run_ids_splitted = cli.getOptionValue("r").split(",");
						Integer task_id = Integer.parseInt(cli.getOptionValue("t"));
						List<Integer> run_ids = new ArrayList<Integer>();
						
						for (String s : run_ids_splitted) {
							run_ids.add(Integer.parseInt(s));
						}
						InstanceBased aw = new InstanceBased(apiconnector, run_ids, task_id);
						
						aw.calculateAllWrong();
						
						aw.toStdout();
					} else {
						System.out.println( Output.styleToJsonError("Missing arguments for function 'all_wrong'. Need r (run ids, comma separated) and t (task_id)") );
					}
					
				} else if(function.equals("different_predictions")) {
					
					if( cli.hasOption("-r") && cli.hasOption("-t") ) {
						
						String[] run_ids_splitted = cli.getOptionValue("r").split(",");
						Integer task_id = Integer.parseInt(cli.getOptionValue("t"));
						List<Integer> run_ids = new ArrayList<Integer>();
						
						for (String s : run_ids_splitted) {
							run_ids.add(Integer.parseInt(s));
						}
						InstanceBased aw = new InstanceBased(apiconnector, run_ids, task_id);
						
						aw.calculateDifference();
						
						aw.toStdout();
					} else {
						System.out.println( Output.styleToJsonError("Missing arguments for function 'all_wrong'. Need r (run ids, comma separated) and t (task_id)") );
					}
				} else if (function.equals("db_consistency")) {
					DatabaseConsistency db_consistency = new DatabaseConsistency(apiconnector);
					
					System.out.println( db_consistency.all_checks() );
				} else {
					System.out.println( Output.styleToJsonError("call to unknown function: " + function) );
				}
			} else {
				System.out.println( Output.styleToJsonError("No function specified. ") );
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println( Output.styleToJsonError(e.getMessage() ));
		}
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
