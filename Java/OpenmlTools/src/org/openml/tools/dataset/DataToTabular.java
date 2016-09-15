package org.openml.tools.dataset;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.openml.tools.algorithms.InstancesHelper;

import weka.core.Instances;
import weka.filters.unsupervised.instance.Randomize;

public class DataToTabular {

	public static void main(String[] args) throws Exception {
		String fileLocation = "/home/rijnjnvan/data/dataset.arff";
		int numInstances = 105; // 51 for landscape, 35 for portrait
		
		// black=k,brown=n,buff=b,chocolate=h,gray=g,
		// green=r,orange=o,pink=p,purple=u,red=e,
		// white=w,yellow=y
		final Map<String, String> colors = new HashMap<String, String>();
		colors.put("k","black");
		colors.put("n","brown");
		colors.put("b","buff");
		colors.put("h","chocolate");
		colors.put("c","cinnamon");
		colors.put("g","gray");
		colors.put("r","green");
		colors.put("o","orange");
		colors.put("p","pink");
		colors.put("u","purple");
		colors.put("e","red");
		colors.put("w","white");
		colors.put("y","yellow");
		
		final Map<String, String> distances = new HashMap<String, String>();
		distances.put("c","close");
		distances.put("w","crowded");
		distances.put("d","distant");
		distances.put("b","broad");
		distances.put("n","narrow");
		

		final Map<String, String> surfaces = new HashMap<String, String>();
		surfaces.put("f","fibrous");
		surfaces.put("y","scaly");
		surfaces.put("k","silky");
		surfaces.put("s","smooth");
		
		final Map<String, String> odors = new HashMap<String, String>();
		odors.put("a","almond");
		odors.put("l","aise");
		odors.put("c","creosote");
		odors.put("y","fishy");
		odors.put("f","foul");
		odors.put("m","musty");
		odors.put("n","none");
		odors.put("p","pungent");
		odors.put("s","spicy");
		
		// abundant=a,clustered=c,numerous=n, scattered=s,several=v,solitary=y
		final Map<String,String> population = new HashMap<String, String>();
		population.put("a","abundant");
		population.put("c","clustered");
		population.put("n","numerous");
		population.put("s","scattered");
		population.put("v","several");
		population.put("y","solitary");
		
		// grasses=g,leaves=l,meadows=m,paths=p,urban=u,waste=w,woods=d
		final Map<String, String> habitats = new HashMap<String, String>();
		habitats.put("g","grasses");
		habitats.put("l","leaves");
		habitats.put("m","meadows");
		habitats.put("p","paths");
		habitats.put("u","urban");
		habitats.put("w","waste");
		habitats.put("d","woods");
		
		final Map<String, String> classValue = new HashMap<String, String>();
		classValue.put("e","edible");
		classValue.put("p","poisonous");
		
		final Map<String,Map<String,String>> valueMapper = new HashMap<String, Map<String,String>>();
		valueMapper.put("cap-color", colors);
		valueMapper.put("odor", odors);
		valueMapper.put("gill-spacing", distances);
		valueMapper.put("gill-size", distances);
		valueMapper.put("gill-color", colors);
		valueMapper.put("stalk-surface-above-ring", surfaces);
		valueMapper.put("stalk-surface-below-ring", surfaces);
		valueMapper.put("stalk-color-above-ring", colors);
		valueMapper.put("stalk-color-below-ring", colors);
		valueMapper.put("spore-print-color", colors);
		valueMapper.put("habitat", habitats);
		valueMapper.put("distances", distances);
		valueMapper.put("population", population);
		valueMapper.put("class", classValue);
		
		final Map<String, String> attributeMapper = new HashMap<String, String>();
		attributeMapper.put("cap-color","\\specialcell[t]{cap\\\\color}");
		attributeMapper.put("gill-spacing","\\specialcell[t]{gill\\\\spacing}");
		attributeMapper.put("stalk-surface-above-ring","\\specialcell[t]{stalk-surface\\\\above-ring}");
		attributeMapper.put("stalk-surface-below-ring","\\specialcell[t]{stalk-surface\\\\below-ring}");
		attributeMapper.put("stalk-color-above-ring","\\specialcell[t]{stalk-color\\\\above-ring}");
		attributeMapper.put("stalk-color-below-ring","\\specialcell[t]{stalk-color\\\\below-ring}");
		attributeMapper.put("spore-print-color","\\specialcell[t]{spore-print\\\\color}");
		
	//	Integer[] attributes = {2,4,12,13,19,20,21,22}; // portrait
	//	Integer[] attributes = {2,4,6,7,8,11,12,14,19,20,21,-1,22}; // landscape
		Integer[] attributes = {0,1,2,3};
		
		
		Instances dataset = new Instances(new FileReader(new File(fileLocation)));
		
	//	dataset = InstancesHelper.applyFilter(dataset, new Randomize(), "-S 42");
		
		boolean twoCols = true;
		
		printHeader(attributes, attributeMapper, dataset);
		
		for (int i = 0, inCol = 0; i < numInstances; ++i, ++inCol) {
			if (twoCols && i == Math.ceil(numInstances * 1.0 / 2)) {
				System.out.println("\\hline\n\\end{tabular}\\hspace{0.4cm}");

				printHeader(attributes, attributeMapper, dataset);
				inCol = 0;
			}
			if (inCol % 2 == 0) System.out.print("\\rowcolor{columnGray} "); 
			for (Integer idx : attributes) {
				String value; 
				String attribute; 
				
				if (idx == -1) {
					value = "";
					attribute = "";
				} else {
					if (dataset.attribute(idx).isNumeric()) {  
						value = dataset.get(i).value(idx) + "";
					} else {
						value = dataset.get(i).stringValue(idx);
					}
					attribute = dataset.attribute(idx).name();
				}
				
				if (valueMapper.containsKey(attribute)) {
					if (valueMapper.get(attribute).containsKey(value)) {
						value = valueMapper.get(attribute).get(value);
					}
				}
				if (idx > attributes[0] || idx == -1) {
					System.out.print(" & ");
				}
				System.out.print(value.replace("_", "\\_"));
			}
			System.out.println(" \\\\");
		}
		System.out.println("\\hline\n\\end{tabular}");
	}
	
	public static void printHeader(Integer[] attributes, Map<String, String> attributeMapper, Instances dataset) {

		String ls = "";
		for (int i = 0; i < attributes.length; ++i) {
			ls += "l ";
		}
		System.out.println("\\begin{tabular}{"+ls+"}");
		
		System.out.println("\\hline");
		
		for (Integer idx : attributes) {
			if (idx > attributes[0] || idx == -1) {
				System.out.print(" & ");
			}
			
			String name;
			if (idx == -1) {
				name = "\\ldots";
			} else {
				name = dataset.attribute(idx).name();
			}
			
			if (attributeMapper.containsKey(name)) {
				name = attributeMapper.get(name);
			}
			System.out.print(name.replace('-', ' '));
		}
		System.out.println(" \\\\\n\\hline");
	}
}
