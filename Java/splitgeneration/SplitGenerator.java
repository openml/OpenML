package test;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import weka.core.Attribute;
import weka.core.Instances;

public class SplitGenerator {
	
	/*
	 * args[0] url of the dataset
	 * args[1] nr of folds if the option is "cv"
	 * args[1] percentage of the dataset for training if the option is "holdout"
	 * args[2] nr of repeats
	 * args[3] destination folder where the generated splits file is stored
	 * args[4] option {cv, holdout}
	 */
	public static void main(String[] args) {		
		SplitGenerator tg = new SplitGenerator();
		if(args != null && args.length == 5){
			if(args[4].equals("cv")){
				tg.generateCVSplits(args[0], 1, 
						Integer.valueOf(args[1]), 
						Integer.valueOf(args[2]), 
						args[3]);
			}
			else if(args[4].equals("holdout")){
				//the percent should be between 0 and 1
				if(Float.valueOf(args[1]) > 0 && Float.valueOf(args[1]) < 1){
					tg.generateHoldoutSplits(args[0], 1, 
							Float.valueOf(args[1]), 
							Integer.valueOf(args[2]), 
							args[3]);
				}
				else{
					System.out.println("The percentage for holdout should be between 0 and 1.");
				}
			}
		}

	}
	
	public static void testCVSplits(){
		String[] args = new String[5];
		args[0] = "http://expdb.cs.kuleuven.be/expdb/data/uci/nominal/iris.arff";
		args[1] = "2";
		args[2] = "5";
		args[3] = "/Users/bo/Documents/workspace/SplitGenerator";
		args[4] = "cv";
		
		SplitGenerator tg = new SplitGenerator();
		tg.generateCVSplits(args[0], 1, 
				Integer.valueOf(args[1]), 
				Integer.valueOf(args[2]), 
				args[3]);
	}

	public static void testHoldoutSplits(){
		String[] args = new String[5];
		args[0] = "http://expdb.cs.kuleuven.be/expdb/data/uci/nominal/iris.arff";
		args[1] = "0.3";
		args[2] = "5";
		args[3] = "/Users/bo/Documents/workspace/SplitGenerator";
		args[4] = "holdout";
		
		SplitGenerator tg = new SplitGenerator();
		tg.generateHoldoutSplits(args[0], 1, 
				Float.valueOf(args[1]), 
				Integer.valueOf(args[2]), 
				args[3]);
	}
	
	/**
	 * @param url
	 * @param seed
	 * @param nrFolds
	 * @param nrRepetitions
	 * @param destination path
	 */
	public void generateCVSplits(String url, int seed, int nrFolds, int nrRepetitions, String destinationPath){
		//prepare the splits arff 
		String splits_name = "";
		String nfolds = Integer.toString(nrFolds);
		String nrepeats = Integer.toString(nrRepetitions);
		String filename = destinationPath + "/";
		BufferedWriter bfwriter;	
		Instances insts;
		try {
			//prepare the splits file
			URL m_url = new URL(url);
			String[] ns = m_url.getFile().split("/");
			int idx = ns[ns.length - 1].lastIndexOf("."); 
			if(idx > 0){
				splits_name = ns[ns.length - 1].substring(0, idx) + "_splits";
			}
			filename += splits_name + "_CV_f" + nfolds + "_r" + nrepeats + ".arff";
			
			bfwriter = new BufferedWriter(new OutputStreamWriter(
					  new FileOutputStream(filename),"UTF-8"));
			bfwriter.write("@RELATION " + splits_name + "_CV_f" + nfolds + "_r" + nrepeats + "\n");
			bfwriter.write("\n");
			bfwriter.write("@ATTRIBUTE type {TRAIN,TEST} \n");
			bfwriter.write("@ATTRIBUTE rowid STRING \n");
			bfwriter.write("@ATTRIBUTE fold INTEGER \n");
			bfwriter.write("@ATTRIBUTE repeat INTEGER \n");	
			bfwriter.write("\n");
			bfwriter.write("@DATA \n");
			
			//fetch the dataset
			URLConnection connection = m_url.openConnection();
			connection.connect();
			InputStream in = new BufferedInputStream(connection.getInputStream());
			InputStreamReader reader = new InputStreamReader(in);
			insts = new Instances(reader);
			insts.setClassIndex(insts.numAttributes()-1);
			int numInstances = insts.numInstances();
			insts.insertAttributeAt(new Attribute("rowid"), 0);
			
			for (int instId = 0; instId < numInstances; instId++)
				insts.instance(instId).setValue(0, instId);
			for(int i=0; i<nrRepetitions; i++){
				   seed = i+1;
				   Random rand = new Random(seed);   // create seeded number generator
				   Instances randData = new Instances(insts); // create copy of original data
				   randData.setClassIndex(randData.numAttributes()-1);
				   randData.randomize(rand);       		 	  // randomize data with number generator
				   if (randData.classAttribute().isNominal())
					   randData.stratify(nrFolds);				  // stratify
				   for(int j=0; j<nrFolds; j++){
					   Instances train = randData.trainCV(nrFolds, j);
					   Instances test = randData.testCV(nrFolds, j);
					   				   
					   int numTrInstances = train.numInstances(); 
					   for (int instId = 0; instId < numTrInstances; instId++){
						   String rowid = Integer.toString((int) train.instance(instId).value(0));
						   String foldid = Integer.toString(j);
						   String repeatid = Integer.toString(i);
						   bfwriter.write("TRAIN, " + rowid + ", " + foldid + ", " + repeatid + "\n");
					   }

					   int numTeInstances = test.numInstances(); 
					   for (int instId = 0; instId < numTeInstances; instId++){
						   String rowid = Integer.toString((int) test.instance(instId).value(0));
						   String foldid = Integer.toString(j);
						   String repeatid = Integer.toString(i);
						   bfwriter.write("TEST, " + rowid + ", " + foldid + ", " + repeatid + "\n");
					   }
				   }
			}
			bfwriter.close();
		
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}//generate cv splits
	
	
	/**
	 * @param url
	 * @param seed
	 * @param percentage
	 * @param nrRepetitions
	 * @param destination path
	 */
	public void generateHoldoutSplits(String url, int seed, float percent, int nrRepetitions, String destinationPath){
		//prepare the splits arff 
		String splits_name = "";
		String percentage = Float.toString(percent);
		String nrepeats = Integer.toString(nrRepetitions);
		String filename = destinationPath + "/";
		BufferedWriter bfwriter;	
		Instances insts;
		try {
			//prepare the splits file
			URL m_url = new URL(url);
			String[] ns = m_url.getFile().split("/");
			int idx = ns[ns.length - 1].lastIndexOf("."); 
			if(idx > 0){
				splits_name = ns[ns.length - 1].substring(0, idx) + "_splits";
			}
			filename += splits_name + "_Holdout_per" + percentage + "_r" + nrepeats + ".arff";
			
			bfwriter = new BufferedWriter(new OutputStreamWriter(
					  new FileOutputStream(filename),"UTF-8"));
			bfwriter.write("@RELATION " + splits_name + "_Holdout_per" + percentage + "_r" + nrepeats + "\n");
			bfwriter.write("\n");
			bfwriter.write("@ATTRIBUTE type {TRAIN,TEST} \n");
			bfwriter.write("@ATTRIBUTE rowid STRING \n");
			bfwriter.write("@ATTRIBUTE fold INTEGER \n");
			bfwriter.write("@ATTRIBUTE repeat INTEGER \n");	
			bfwriter.write("\n");
			bfwriter.write("@DATA \n");
			
			//fetch the dataset
			URLConnection connection = m_url.openConnection();
			connection.connect();
			InputStream in = new BufferedInputStream(connection.getInputStream());
			InputStreamReader reader = new InputStreamReader(in);
			insts = new Instances(reader);
			insts.setClassIndex(insts.numAttributes()-1);
			int numInstances = insts.numInstances();
			insts.insertAttributeAt(new Attribute("rowid"), 0);
			
			//number of instances for training 
			int nrTraining = Math.round(insts.numInstances()*percent);
			
			for (int instId = 0; instId < numInstances; instId++)
				insts.instance(instId).setValue(0, instId);
			for(int i=0; i<nrRepetitions; i++){
				   seed = i+1;
				   Random rand = new Random(seed);   // create seeded number generator
				   Instances randData = new Instances(insts); // create copy of original data
				   randData.setClassIndex(randData.numAttributes()-1);
				   randData.randomize(rand);       		 	  // randomize data with number generator
				   
				   String foldid = "0";
				   String repeatid = Integer.toString(i);
				   
				   for(int j=0; j<nrTraining; j++){
					   String rowid = Integer.toString((int)randData.instance(j).value(0));
					   bfwriter.write("TRAIN, " + rowid + ", " + foldid + ", " + repeatid + "\n");
				   }
				   
				   for(int j=nrTraining; j<insts.numInstances(); j++){
					   String rowid = Integer.toString((int)randData.instance(j).value(0));
					   bfwriter.write("TEST, " + rowid + ", " + foldid + ", " + repeatid + "\n");
				   }
				   
			}
			bfwriter.close();
		
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}//generate holdout splits
	
	
}
