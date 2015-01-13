package org.openml.learningcurves.experiments;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openml.learningcurves.data.DataLoader;
import org.openml.learningcurves.tasks.BestAlgorithmGlobalRanking;
import org.openml.learningcurves.tasks.BestAlgorithmGlobalRankingPrime;
import org.openml.learningcurves.tasks.BestAlgorithmOriginal;
import org.openml.learningcurves.tasks.CurvesExperimentFull;

public class BestAlgorithmStrategies {

	public BestAlgorithmStrategies() throws IOException {
		DataLoader dl = new DataLoader("data/meta_curves.csv");
		
		BestAlgorithmGlobalRanking bag = new BestAlgorithmGlobalRanking(dl );
		bag.allTasks();
		System.out.println( bag.result() );
		System.out.println( bag.lossCurve() );
		FileUtils.writeStringToFile(new File("data/output/global_ranking.csv"), bag.globalRankingCsv() );
		
		BestAlgorithmGlobalRankingPrime bagprime = new BestAlgorithmGlobalRankingPrime(dl );
		bagprime.allTasks();
		System.out.println( bagprime.result() );
		System.out.println( bagprime.lossCurve() );
		FileUtils.writeStringToFile(new File("data/output/global_ranking_prime.csv"), bagprime.globalRankingCsv() );
		
		CurvesExperimentFull bao = new BestAlgorithmOriginal(dl, 6, 3);
		bao.allTasks();
		System.out.println( bao.result() );
		System.out.println( bao.lossCurve() );
	}
}
