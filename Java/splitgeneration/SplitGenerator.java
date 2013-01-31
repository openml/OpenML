package test;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import weka.core.Attribute;
import weka.core.Instances;

public class SplitGenerator {
	
	public static void main(String[] args) {		
		SplitGenerator tg = new SplitGenerator();
		tg.generateSplits(args[0], 1, 
				Integer.valueOf(args[1]), 
				Integer.valueOf(args[2]), 
				args[3], 
				args[4]);
	}
	
	public static void testMain(){
		String[] args = new String[5];
		args[0] = "http://expdb.cs.kuleuven.be/expdb/data/uci/nominal/iris.arff";
		args[1] = "2";
		args[2] = "5";
		args[3] = "/Users/bo/Documents/workspace/FoldGeneratorTest";
		args[4] = "cv";
		
		SplitGenerator tg = new SplitGenerator();
		tg.generateSplits(args[0], 1, 
				Integer.valueOf(args[1]), 
				Integer.valueOf(args[2]), 
				args[3], 
				args[4]);
	}
	
	/**
	 * Dumps the given JSONobject to stdout
	 */
//	public void dumpJSON(JSONObject o){
//		   System.out.println(o.toJSONString());
//	}
	

//	public void exportToXML(JSONObject o, String indent){
//		System.out.println(indent + "<oml:folds xmlns:oml=\"http://open-ml.org/openml\">");
//		Iterator<Map.Entry<String,JSONObject>> repeats = o.entrySet().iterator();
//		while(repeats.hasNext()){
//			Map.Entry<String,JSONObject> repeat = (Map.Entry<String,JSONObject>) repeats.next();
//			System.out.println(indent+"<oml:repeat>");
//			System.out.println(indent+"\t<oml:repeat-id>"+repeat.getKey().toString().split("_")[1]+"</oml:repeat-id>");
//			Iterator<Map.Entry<String,JSONObject>> folds = ((JSONObject)repeat.getValue()).entrySet().iterator();
//			while(folds.hasNext()){
//				Map.Entry<String,JSONObject> fold = (Map.Entry<String,JSONObject>) folds.next();
//				System.out.println(indent+"\t<oml:fold>");
//				System.out.println(indent+"\t\t<oml:fold-id>"+fold.getKey().toString().split("_")[1]+"</oml:fold-id>");
//				Iterator<Map.Entry<String,JSONArray>> sets = ((JSONObject)fold.getValue()).entrySet().iterator();
//				while(sets.hasNext()){
//					Map.Entry<String,JSONArray> set = (Map.Entry) sets.next();
//					System.out.println(indent+"\t\t<oml:fold-"+set.getKey()+">");
//					System.out.print(indent+"\t\t\t<oml:observations>");
//					for(Object i: set.getValue()){
//						System.out.print(i+" ");
//					}
//					System.out.println("</oml:observations>");
//					System.out.println(indent+"\t\t</oml:fold-"+set.getKey()+">");
//				}
//				System.out.println(indent+"\t</oml:fold>");
//			}
//			System.out.println(indent+"</oml:repeat>");
//		}	
//		
//		System.out.println(indent+"</oml:folds>");
//	}
	
	/**
	 * Generates folds for the given dataset, and returns it as a JSON object.
	 * 
	 * @param url
	 * @param seed
	 * @param nrFolds
	 * @param nrRepetitions
	 */
//	public JSONObject generateFolds(String url, int seed, int nrFolds, int nrRepetitions){
//		JSONObject jrepetitions = new JSONObject();
//		
//		try{
//			URL m_url = new URL(url);
//			URLConnection connection = m_url.openConnection();
//			connection.connect();
//			InputStream in = new BufferedInputStream(connection.getInputStream());
//			InputStreamReader reader = new InputStreamReader(in);
//
//			Instances insts = new Instances(reader);
//			
//			insts.setClassIndex(insts.numAttributes()-1);
//			int numInstances = insts.numInstances();
//			insts.insertAttributeAt(new Attribute("rowid"), 0);
//			for (int instId = 0; instId < numInstances; instId++)
//				insts.instance(instId).setValue(0, instId);
//			
//			List<ArrayList<ArrayList<ArrayList<Integer>>>> rowids = new ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>>();
//
//			for(int i=0; i<nrRepetitions; i++){
//				ArrayList<ArrayList<ArrayList<Integer>>> repetitions = new ArrayList<ArrayList<ArrayList<Integer>>>();
//				JSONObject jfolds = new JSONObject();
//				seed = i+1;
//			   Random rand = new Random(seed);   // create seeded number generator
//			   Instances randData = new Instances(insts); // create copy of original data
//			   randData.setClassIndex(randData.numAttributes()-1);
//			   randData.randomize(rand);       		 	  // randomize data with number generator
//			   if (randData.classAttribute().isNominal())
//				   randData.stratify(nrFolds);				  // stratify
//			   for(int j=0; j<nrFolds; j++){
//				   ArrayList<ArrayList<Integer>> folds = new ArrayList<ArrayList<Integer>>();
//				   JSONObject jrowids = new JSONObject();
//				   ArrayList<Integer> trainRowIds = new ArrayList<Integer>();
//				   JSONArray jtrainRowIds = new JSONArray();
//				   ArrayList<Integer> testRowIds = new ArrayList<Integer>();
//				   JSONArray jtestRowIds = new JSONArray();
//				   Instances train = randData.trainCV(nrFolds, j);
//				   Instances test = randData.testCV(nrFolds, j);
//				   				   
//				   int numTrInstances = train.numInstances(); 
//				   for (int instId = 0; instId < numTrInstances; instId++){
//					   trainRowIds.add((int) train.instance(instId).value(0));
//					   jtrainRowIds.add((int)train.instance(instId).value(0));
//				   }
//				   folds.add(trainRowIds);	
//				   jrowids.put("train",jtrainRowIds);
//
//				   int numTeInstances = test.numInstances(); 
//				   for (int instId = 0; instId < numTeInstances; instId++){
//					   testRowIds.add((int) test.instance(instId).value(0));
//					   jtestRowIds.add((int) test.instance(instId).value(0));
//				   }
//				   folds.add(testRowIds);
//				   jrowids.put("test",jtestRowIds);
//				   repetitions.add(folds);
//				   jfolds.put("fold_"+j,jrowids);
//			   }
//			   rowids.add(repetitions);
//			   jrepetitions.put("repeat_"+i,jfolds);
//			}
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return jrepetitions;
//		
//	}

	
	/**
	 * Generates folds for the given dataset, and returns it as a JSON object.
	 * 
	 * @param url
	 * @param seed
	 * @param nrFolds
	 * @param nrRepetitions
	 * @param destination path
	 * @param option {cv, percentage}
	 */
	public void generateSplits(String url, int seed, int nrFolds, int nrRepetitions, String destinationPath, String option){
		if(option.equals("cv")){
			//prepare the splits arff 
			String splits_name = "";
			String nfolds = Integer.toString(nrFolds);
			String nrepeats = Integer.toString(nrRepetitions);
			String filename = destinationPath + "/";
	  	  
			BufferedWriter bfwriter;			
			try{
				//prepare the splits file
				URL m_url = new URL(url);
				String[] ns = m_url.getFile().split("/");
				int idx = ns[ns.length - 1].lastIndexOf("."); 
				if(idx > 0){
					splits_name = ns[ns.length - 1].substring(0, idx) + "_splits";
				}
				filename += splits_name + "_CV_" + nfolds + "_" + nrepeats + ".arff";
				bfwriter = new BufferedWriter(new OutputStreamWriter(
						  new FileOutputStream(filename),"UTF-8"));
				bfwriter.write("@RELATION " + splits_name + "\n");
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

				Instances insts = new Instances(reader);
				
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
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}//if option == cv
		
	}
}
