package org.openml.webapplication.fantail.dc.stream;

import java.util.Map;

import org.openml.webapplication.fantail.dc.StreamCharacterizer;

import weka.core.Instances;

public class ChangeDetectors extends StreamCharacterizer {

	@Override
	public String[] getIDs() {
		return new String[] { "AdwinHoeffding" };
	}

	@Override
	public Map<String, Double> characterize(Instances instances) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Double> interval(int interval_start) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void doTask() {
		
	}

}
