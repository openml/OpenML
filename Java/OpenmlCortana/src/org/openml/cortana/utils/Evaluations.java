package org.openml.cortana.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.openml.apiconnector.xml.EvaluationScore;

public class Evaluations {

	public static List<EvaluationScore> extract(File evaluationsFile, String measure) throws IOException {
		List<EvaluationScore> scores = new ArrayList<EvaluationScore>();
		
		List<Integer> arrayDataCoverage = new ArrayList<Integer>();
		List<Double> arrayDataQuality = new ArrayList<Double>();
		List<Double> arrayDataProbability = new ArrayList<Double>();
		List<Integer> arrayDataPositives = new ArrayList<Integer>();
		
		CSVParser parser = CSVParser.parse(evaluationsFile, StandardCharsets.UTF_8, CSVFormat.TDF);
		int i = -1;
		for (CSVRecord csvRecord : parser) {
			i+=1;
			if (i == 0) continue; // skip csv header
			arrayDataCoverage.add(Integer.parseInt(csvRecord.get(2)));
			arrayDataQuality.add(roundDigits(Double.parseDouble(csvRecord.get(3)), 7));
			arrayDataProbability.add(roundDigits(Double.parseDouble(csvRecord.get(4)), 7));
			arrayDataPositives.add((int) Double.parseDouble(csvRecord.get(5)));
		}
		
		scores.add(new EvaluationScore("coverage", arrayDataCoverage.get(0) + "", null, arrayDataCoverage.toString()));
		scores.add(new EvaluationScore("quality", arrayDataQuality.get(0) + "", null, arrayDataQuality.toString()));
		scores.add(new EvaluationScore("probability", arrayDataProbability.get(0) + "", null, arrayDataProbability.toString()));
		scores.add(new EvaluationScore("positives", arrayDataPositives.get(0) + "", null, arrayDataPositives.toString()));
		scores.add(new EvaluationScore(nameMapping(measure), arrayDataQuality.get(0) + "", null, arrayDataQuality.toString()));
		
		return scores;
	}
	
	private static final String nameMapping(String measure) {
		return measure.replace(' ', '_').toLowerCase();
	}
	
	private static double roundDigits(double val, int numDigits) {
		double factor = Math.pow(10, numDigits);
		val = Math.round(val * factor);
		return val / factor;
	}
}
