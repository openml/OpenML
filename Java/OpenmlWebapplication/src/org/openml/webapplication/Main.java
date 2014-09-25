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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.openml.apiconnector.io.ApiConnector;
import org.openml.apiconnector.io.ApiSessionHash;
import org.openml.apiconnector.settings.Config;
import org.openml.webapplication.generatefolds.GenerateFolds;
import org.openml.webapplication.io.Output;

public class Main {
	
	public static final int FOLD_GENERATION_SEED = 0;
	
	public static void main( String[] args ) {
		ApiConnector apiconnector;
		ApiSessionHash ash;
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
		options.addOption("m", false, "Flag determining whether the output of the splits file should be presented as a md5 hash");
		options.addOption("test", true, "A list of rowids for a holdout set (fold generation)" );
		
		
		try {
			CommandLine cli  = parser.parse( options, args );
			if( cli.hasOption("-config") == false ) {
				config = new Config();
			} else {
				config = new Config( cli.getOptionValue("config") );
			}
			
			apiconnector = new ApiConnector( config.getServer() );
			ash = new ApiSessionHash( apiconnector );
			ash.set( config.getUsername(), config.getPassword() );
			
			if( cli.hasOption("-id") ) {
				id = Integer.parseInt( cli.getOptionValue("id") );
			}
			
			
			if( cli.hasOption("f") ) {
				
				String function = cli.getOptionValue("f");
				if( function.equals("evaluate_run") ) {
					
					
					
					// bootstrap evaluate run
					new EvaluateRun(apiconnector, ash, id);
					
				} else if( function.equals("process_dataset") ) {
					
					
					// bootstrap process dataset
					new ProcessDataset(apiconnector, ash, id);
					
				} else if( function.equals("generate_folds") ) {
					
					// prepare ARFF output consisting of datasplits
					if( cli.hasOption("-d") && cli.hasOption("e") && cli.hasOption("c") && cli.hasOption("r") ) {
						int[] testset = null;
						if( cli.hasOption("-test") ) {
							String[] rowids = cli.getOptionValue("test").split(",");
							testset = new int[rowids.length];
							for( int i = 0; i < rowids.length; ++i ) {
								testset[i] = Integer.parseInt( rowids[i] );
							}
						}
						
						GenerateFolds gf = new GenerateFolds(
								apiconnector, ash, 
								cli.getOptionValue("d"), 
								cli.getOptionValue("e"), 
								cli.getOptionValue("c"), 
								cli.getOptionValue("r"), 
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
