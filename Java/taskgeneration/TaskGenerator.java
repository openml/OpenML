import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import weka.core.Attribute;
import weka.core.Instances;


public class TaskGenerator {
	
	String url = "/Users/joa/Documents/Data/classlast/iris.arff";
	
	public static void main(String[] args) {
		TaskGenerator tg = new TaskGenerator();
		tg.createTask(1,10,10);
	}
	
	public void createTask(int seed, int nrFolds, int nrRepetitions){
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
			   seed = i+1;
			   Random rand = new Random(seed);   // create seeded number generator
			   Instances randData = new Instances(insts); // create copy of original data
			   randData.setClassIndex(randData.numAttributes()-1);
			   randData.randomize(rand);       		 	  // randomize data with number generator
			   if (randData.classAttribute().isNominal())
				   randData.stratify(nrFolds);				  // stratify
			   for(int j=0; j<nrFolds; j++){
				   ArrayList<ArrayList<Integer>> folds = new ArrayList<ArrayList<Integer>>();
				   ArrayList<Integer> trainRowIds = new ArrayList<Integer>();
				   ArrayList<Integer> testRowIds = new ArrayList<Integer>();
				   Instances train = randData.trainCV(nrFolds, j);
				   Instances test = randData.testCV(nrFolds, j);
				   				   
				   int numTrInstances = train.numInstances(); 
				   for (int instId = 0; instId < numTrInstances; instId++)
					   trainRowIds.add((int) train.instance(instId).value(0));
				   folds.add(trainRowIds);	

				   int numTeInstances = test.numInstances(); 
				   for (int instId = 0; instId < numTeInstances; instId++)
					   testRowIds.add((int) test.instance(instId).value(0));
				   folds.add(testRowIds);
				   repetitions.add(folds);
			   }
			   rowids.add(repetitions);
			}
			
			for(ArrayList<ArrayList<ArrayList<Integer>>> rep : rowids){
				System.out.println("\t\t\t\t\t<oml:repeat>");
				System.out.println("\t\t\t\t\t\t<oml:repeat-id>"+rowids.indexOf(rep)+"</oml:repeat-id>");
				for(ArrayList<ArrayList<Integer>> fold : rep){
					System.out.println("\t\t\t\t\t\t<oml:fold>");
					System.out.println("\t\t\t\t\t\t\t<oml:fold-id>"+rep.indexOf(fold)+"</oml:fold-id>");
					for(ArrayList<Integer> set: fold){
						if(fold.indexOf(set)==0)
							System.out.println("\t\t\t\t\t\t\t<oml:fold-train>");
						else
							System.out.println("\t\t\t\t\t\t\t<oml:fold-test>");
						System.out.print("\t\t\t\t\t\t\t\t<oml:observations>");
						for(Integer i: set){
							System.out.print(i+" ");
						}
						System.out.println("</oml:observations>");
						if(fold.indexOf(set)==0)
							System.out.println("\t\t\t\t\t\t\t</oml:fold-train>");
						else
							System.out.println("\t\t\t\t\t\t\t</oml:fold-test>");
					}
					System.out.println("\t\t\t\t\t\t</oml:fold>");
				}
				System.out.println("\t\t\t\t\t</oml:repeat>");
			}	
		}
		catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		
	}

}
