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
import org.openml.apiconnector.settings.Config;
import org.openml.webapplication.evaluate.EvaluateBatchPredictions;
import org.openml.webapplication.evaluate.EvaluateStreamPredictions;
import org.openml.webapplication.features.ExtractFeatures;
import org.openml.webapplication.features.FantailConnector;
import org.openml.webapplication.generatefolds.GenerateFolds;
import org.openml.webapplication.io.Output;

public class Main {
	
	public static void main( String[] args ) {
		CommandLineParser parser = new GnuParser();
		Options options = new Options();
		Config c;
		
		options.addOption("did", true, "The (d)id of the dataset used");
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
		
		CommandLine cli;
		try {
			cli = parser.parse( options, args );
			if( cli.hasOption("f") ) {
				String function = cli.getOptionValue("f");
				if( function.equals("evaluate_predictions") ) {
					if( cli.hasOption("-d") == true && cli.hasOption("-c") == true && cli.hasOption("-s") && cli.hasOption("-p") == true ) {
						new EvaluateBatchPredictions( cli.getOptionValue("d"), cli.getOptionValue("s"), cli.getOptionValue("p"), cli.getOptionValue("c") );
					} else {
						System.out.println( Output.styleToJsonError("Missing arguments for function 'evaluate_predictions'. Need d (url to dataset), c (string target feature), s (url to splits file), and p (url to predictions file). ") );
					}
				} else if( function.equals("evaluate_stream_predictions") ) {
					if( cli.hasOption("-d") == true && cli.hasOption("-c") == true && cli.hasOption("-p") == true ) {
						new EvaluateStreamPredictions( cli.getOptionValue("d"), cli.getOptionValue("p"), cli.getOptionValue("c"), 1000 );
					} else {
						System.out.println( Output.styleToJsonError("Missing arguments for function 'evaluate_stream_predictions'. Need d (url to dataset), c (string target feature), s (url to splits file), and p (url to predictions file). ") );
					}
				} else if( function.equals("data_features") ) {
					if( cli.hasOption("-d") == true ) {
						String default_class = null;
						if( cli.hasOption("-c") == true ) { default_class = cli.getOptionValue("c"); }
						new ExtractFeatures( cli.getOptionValue("d"), default_class );
					} else {
						System.out.println( Output.styleToJsonError("Missing arguments for function 'data_features'. Need d (url to dataset). ") );
					}
				} else if( function.equals("data_qualities") ) {
					if( cli.hasOption("-did") == false ) {
						System.out.println( Output.styleToJsonError("Missing arguments for function 'data_qualities'. Need did (dataset id). ") );
					} else if( isInteger( cli.getOptionValue("did") ) == false ) {
						System.out.println( Output.styleToJsonError("Option did must be an integer. ") );					
					} else {
						if( cli.hasOption("-config") == false ) {
							c = new Config();
						} else {
							c = new Config( cli.getOptionValue("config") );
						}
						
						
						String default_class = null;
						if( cli.hasOption("-c") == true ) { default_class = cli.getOptionValue("c"); }
						FantailConnector.extractFeatures( Integer.parseInt( cli.getOptionValue("did") ), default_class, c );
					} 
				} else if( function.equals("generate_folds") ) {
					if( cli.hasOption("-d") && cli.hasOption("e") && cli.hasOption("c") && cli.hasOption("r") ) {
						GenerateFolds gf = new GenerateFolds(
								cli.getOptionValue("d"), 
								cli.getOptionValue("e"), 
								cli.getOptionValue("c"), 
								cli.getOptionValue("r"), 
								0);
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
