import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import weka.core.Attribute;
import weka.core.Instances;


public class FoldGenerator {
	
	public static void main(String[] args) {
		FoldGenerator tg = new FoldGenerator();
		JSONObject o = tg.generateFolds(args[0],1,Integer.valueOf(args[1]),Integer.valueOf(args[2]));
		if(args[3].toLowerCase().equals("json"))
			tg.dumpJSON(o);
		else if(args[3].toLowerCase().equals("xml"))
			tg.exportToXML(o, "");

		//tg.exportToXML(o, "\t\t\t\t\t");
	}
	
	/**
	 * Dumps the given JSONobject to stdout
	 */
	public void dumpJSON(JSONObject o){
		   System.out.println(o.toJSONString());
	}
	
	public void exportToXML(JSONObject o, String indent){
		Iterator<Map.Entry<String,JSONObject>> repeats = o.entrySet().iterator();
		while(repeats.hasNext()){
			Map.Entry<String,JSONObject> repeat = (Map.Entry<String,JSONObject>) repeats.next();
			System.out.println(indent+"<oml:repeat>");
			System.out.println(indent+"\t<oml:repeat-id>"+repeat.getKey().toString().split("_")[1]+"</oml:repeat-id>");
			Iterator<Map.Entry<String,JSONObject>> folds = ((JSONObject)repeat.getValue()).entrySet().iterator();
			while(folds.hasNext()){
				Map.Entry<String,JSONObject> fold = (Map.Entry<String,JSONObject>) folds.next();
				System.out.println(indent+"\t<oml:fold>");
				System.out.println(indent+"\t\t<oml:fold-id>"+fold.getKey().toString().split("_")[1]+"</oml:fold-id>");
				Iterator<Map.Entry<String,JSONArray>> sets = ((JSONObject)fold.getValue()).entrySet().iterator();
				while(sets.hasNext()){
					Map.Entry<String,JSONArray> set = (Map.Entry) sets.next();
					System.out.println(indent+"\t\t<oml:fold-"+set.getKey()+">");
					System.out.print(indent+"\t\t\t<oml:observations>");
					for(Object i: set.getValue()){
						System.out.print(i+" ");
					}
					System.out.println("</oml:observations>");
					System.out.println(indent+"\t\t</oml:fold-"+set.getKey()+">");
				}
				System.out.println(indent+"\t</oml:fold>");
			}
			System.out.println(indent+"</oml:repeat>");
		}		
	}
	
	/**
	 * Generates folds for the given dataset, and returns it as a JSON object.
	 * 
	 * @param url
	 * @param seed
	 * @param nrFolds
	 * @param nrRepetitions
	 */
	public JSONObject generateFolds(String url, int seed, int nrFolds, int nrRepetitions){
		JSONObject jrepetitions = new JSONObject();
		
		try{
			Instances insts = new Instances(new BufferedReader(new FileReader(url)));
			insts.setClassIndex(insts.numAttributes()-1);
			int numInstances = insts.numInstances();
			insts.insertAttributeAt(new Attribute("rowid"), 0);
			for (int instId = 0; instId < numInstances; instId++)
				insts.instance(instId).setValue(0, instId);
			
			List<ArrayList<ArrayList<ArrayList<Integer>>>> rowids = new ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>>();

			for(int i=0; i<nrRepetitions; i++){
				ArrayList<ArrayList<ArrayList<Integer>>> repetitions = new ArrayList<ArrayList<ArrayList<Integer>>>();
				JSONObject jfolds = new JSONObject();
				seed = i+1;
			   Random rand = new Random(seed);   // create seeded number generator
			   Instances randData = new Instances(insts); // create copy of original data
			   randData.setClassIndex(randData.numAttributes()-1);
			   randData.randomize(rand);       		 	  // randomize data with number generator
			   if (randData.classAttribute().isNominal())
				   randData.stratify(nrFolds);				  // stratify
			   for(int j=0; j<nrFolds; j++){
				   ArrayList<ArrayList<Integer>> folds = new ArrayList<ArrayList<Integer>>();
				   JSONObject jrowids = new JSONObject();
				   ArrayList<Integer> trainRowIds = new ArrayList<Integer>();
				   JSONArray jtrainRowIds = new JSONArray();
				   ArrayList<Integer> testRowIds = new ArrayList<Integer>();
				   JSONArray jtestRowIds = new JSONArray();
				   Instances train = randData.trainCV(nrFolds, j);
				   Instances test = randData.testCV(nrFolds, j);
				   				   
				   int numTrInstances = train.numInstances(); 
				   for (int instId = 0; instId < numTrInstances; instId++){
					   trainRowIds.add((int) train.instance(instId).value(0));
					   jtrainRowIds.add((int)train.instance(instId).value(0));
				   }
				   folds.add(trainRowIds);	
				   jrowids.put("train",jtrainRowIds);

				   int numTeInstances = test.numInstances(); 
				   for (int instId = 0; instId < numTeInstances; instId++){
					   testRowIds.add((int) test.instance(instId).value(0));
					   jtestRowIds.add((int) test.instance(instId).value(0));
				   }
				   folds.add(testRowIds);
				   jrowids.put("test",jtestRowIds);
				   repetitions.add(folds);
				   jfolds.put("fold_"+j,jrowids);
			   }
			   rowids.add(repetitions);
			   jrepetitions.put("repeat_"+i,jfolds);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		return jrepetitions;
		
	}

}
