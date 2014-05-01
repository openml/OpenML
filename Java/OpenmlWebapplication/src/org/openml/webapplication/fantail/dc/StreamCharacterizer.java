package org.openml.webapplication.fantail.dc;

import java.util.Map;

public abstract class StreamCharacterizer extends Characterizer {

	public abstract Map<String, Double> global( );
	public abstract Map<String, Double> interval( int interval_start );
}
