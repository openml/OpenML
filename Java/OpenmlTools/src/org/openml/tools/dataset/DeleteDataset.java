package org.openml.tools.dataset;

import org.openml.apiconnector.io.OpenmlConnector;

public class DeleteDataset {

	public static void main( String[] args ) throws Exception {
		
		OpenmlConnector oc = new OpenmlConnector("janvanrijn@gmail.com","Feyenoord2008");
		
		for( int i = 1246; i <= 1350; ++i ) {
			try  {
				oc.dataDelete( i );
				System.out.println( "Deleted " + i );
			} catch( Exception e ) {
				System.err.println( e.getMessage() + "(" + i + ")" );
			}
		}
		
		
	}
}
