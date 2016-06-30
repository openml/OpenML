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
			arrayDataQuality.add(Double.parseDouble(csvRecord.get(3)));
			arrayDataProbability.add(Double.parseDouble(csvRecord.get(4)));
			arrayDataPositives.add((int) Double.parseDouble(csvRecord.get(5)));
		}
		
		scores.add(new EvaluationScore("openml.evaluation.coverage(1.0)", "coverage", arrayDataCoverage.get(0) + "", null, arrayDataCoverage.toString()));
		scores.add(new EvaluationScore("openml.evaluation.quality(1.0)", "quality", arrayDataQuality.get(0) + "", null, arrayDataQuality.toString()));
		scores.add(new EvaluationScore("openml.evaluation.probability(1.0)", "probability", arrayDataProbability.get(0) + "", null, arrayDataProbability.toString()));
		scores.add(new EvaluationScore("openml.evaluation.positives(1.0)", "positives", arrayDataPositives.get(0) + "", null, arrayDataPositives.toString()));
		scores.add(new EvaluationScore("openml.evaluation."+nameMapping(measure)+"(1.0)", nameMapping(measure), arrayDataQuality.get(0) + "", null, arrayDataQuality.toString()));
		
		return scores;
	}
	
	private static final String nameMapping(String measure) {
		return measure.replace(' ', '_').toLowerCase();
	}
}
