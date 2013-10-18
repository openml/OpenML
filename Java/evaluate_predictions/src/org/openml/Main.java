package org.openml;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.openml.evaluate.EvaluatePredictions;
import org.openml.features.ExtractFeatures;
import org.openml.generatefolds.GenerateFolds;
import org.openml.io.Output;

public class Main {
	
	public static void main( String[] args ) {
		CommandLineParser parser = new GnuParser();
		Options options = new Options();

		options.addOption("f", true, "The function to invole");
		options.addOption("d", true, "The dataset used");
		options.addOption("c", true, "The target class");
		options.addOption("s", true, "The splitfile used");
		options.addOption("p", true, "The prediction file used");
		options.addOption("e", true, "The evaluation method");
		options.addOption("o", true, "The output file");
		options.addOption("r", true, "The rowid");
		
		CommandLine cli;
		try {
			cli = parser.parse( options, args );
			if( cli.hasOption("f") ) {
				String function = cli.getOptionValue("f");
				if( function.equals("evaluate_predictions") ) {
					if( cli.hasOption("-d") == true && cli.hasOption("-c") == true && cli.hasOption("-s") && cli.hasOption("-p") == true ) {

						new EvaluatePredictions( cli.getOptionValue("d"), cli.getOptionValue("s"), cli.getOptionValue("p"), cli.getOptionValue("c") );

					} else {
						System.out.println( Output.styleToJsonError("Missing arguments for function 'evaluate_predictions'. Need d (url to dataset), c (string target feature), s (url to splits file), and p (url to predictions file). ") );
					}
				} else if( function.equals("data_features") ) {
					if( cli.hasOption("-d") == true ) {
						new ExtractFeatures( cli.getOptionValue("d") );
					} else {
						System.out.println( Output.styleToJsonError("Missing arguments for function 'data_features'. Need d (url to dataset). ") );
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
						} else {
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
}
