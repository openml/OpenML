package org.openml.webapplication.fantail.dc.stream;

import java.util.Map;

import org.openml.webapplication.fantail.dc.StreamCharacterizer;

import weka.core.Instances;

public class ChangeDetectors extends StreamCharacterizer {
	
	private final RunChangeDetectorTask rcdt;
	
	public ChangeDetectors( Integer interval_size ) {
		rcdt = new RunChangeDetectorTask( interval_size );
	}
	
	@Override
	public String[] getIDs() {
		return rcdt.getIDs();
	}

	@Override
	public Map<String, Double> characterize(Instances instances) {
		return rcdt.characterize(instances);
	}

	@Override
	public Map<String, Double> interval(int interval_start) {
		return rcdt.interval(interval_start);
	}

	@Override
	public Map<String, Double> global() {
		return rcdt.global();
	}

}
