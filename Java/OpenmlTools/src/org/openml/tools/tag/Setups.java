package org.openml.tools.tag;

import java.util.Arrays;

import org.openml.apiconnector.algorithms.QueryUtils;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;

public class Setups {

	public static void main( String[] args ) throws Exception {
		Config c = new Config();
		OpenmlConnector openmlConnector = new OpenmlConnector( c.getServer(), c.getUsername(), c.getPassword() );
		
		String sql = "SELECT s.sid FROM algorithm_setup s WHERE sid IN (1838,1839,1840,1841,1842,1843,1844,1845,1846,1847,1848,1849,1850,1851,1852,1853,1854,1855,1856,1857,1858,1859,1860,1861,1862,1863,1864,1865,1866,1867,1868,1869,1870,1871,1872,1873,1874,1875,1876,1877,1878,1879,1880,1881,1882,1883,1884,1885,1886,1887,1888)";
		int[] res = QueryUtils.getIdsFromDatabase( openmlConnector, sql );
		
		System.out.println( Arrays.toString( res ) );
		for( Integer setup_id : res ) {
			try {
				openmlConnector.openmlSetupTag(setup_id, "streams");
				
			} catch( Exception e ) {
				System.err.println( "error at: " + setup_id + ". " + e.getMessage() );
			}
		}
	}
}
