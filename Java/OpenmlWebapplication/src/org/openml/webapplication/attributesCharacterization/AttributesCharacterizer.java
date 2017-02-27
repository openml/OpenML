package org.openml.webapplication.attributesCharacterization;

import java.util.Map;

import org.openml.webapplication.fantail.dc.Characterizer;

import weka.core.Instances;

public class AttributesCharacterizer extends Characterizer {

	public static final String[] ids = new String[] { 
		
	};
	
	@Override
	public String[] getIDs() {
		return ids;
	}
	
	public Map<String, Double> characterize(Instances dataset) {
		return null;
	}
}
