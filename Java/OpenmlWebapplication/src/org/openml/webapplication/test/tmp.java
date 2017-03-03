package org.openml.webapplication.test;

import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.webapplication.features.FantailConnector;

public class tmp {

	public static void main(String[] args) throws Exception {
		OpenmlConnector connector = new OpenmlConnector("http://capa.win.tue.nl/","ad6244a6f01a5c9fc4985a0875b30b97");
		FantailConnector fc = new FantailConnector(connector, 1, false, null, null);
		fc.toString();

	}

}
