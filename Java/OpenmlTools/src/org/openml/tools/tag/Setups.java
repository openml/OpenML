package org.openml.tools.tag;

import java.util.Arrays;

import org.openml.apiconnector.algorithms.QueryUtils;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.settings.Config;

public class Setups {

	private static final Integer[] setups = { 1838, 1839, 1840, 1841, 1842, 1843, 1844, 1845, 1846, 1847, 1848, 1849, 1850, 1851, 1852, 1853, 1854, 1855, 1856, 1857, 1858, 1859, 1860, 1861, 1862, 1863, 1864, 1865, 1866, 1867, 1868, 1869, 1870, 1871, 1872, 1873, 1874, 1875, 1876, 1877, 1878, 1879, 1880, 1881, 1882, 1883, 1884, 1885, 1886, 1887, 1888, 1913, 1914, 1915, 1916, 1917, 1918, 1919, 1920, 1921, 1922, 1923, 1924, 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023, 2024, 2025, 2026, 2027, 2028, 2029, 2030, 2031, 2032, 2033, 2034, 2035, 2036, 2037, 2038, 2039, 2040, 2041, 2042, 2043, 2044, 2045, 2046, 2047, 2048, 2049, 2050, 2051, 2052, 2053, 2054, 2055, 2056, 2057, 2058, 2059, 2060, 2061, 2062, 2063, 2064, 2065, 2066, 2067, 2068, 2069, 2070 };
	
	private static final String TAG_NAME = "joaquin";
	
	public static void main( String[] args ) throws Exception {
		Config c = new Config();
		OpenmlConnector openmlConnector = new OpenmlConnector( c.getServer(), c.getApiKey() );
		
		
		String arraystring = Arrays.toString( setups );
		String sql = "SELECT s.sid FROM algorithm_setup s WHERE sid IN ( " + arraystring.substring( 1, arraystring.length() - 1 ) + " )";
		System.out.println( sql );
		int[] res = QueryUtils.getIdsFromDatabase( openmlConnector, sql );
		
		System.out.println( Arrays.toString( res ) );
		for( Integer setup_id : res ) {
			try {
				openmlConnector.setupTag(setup_id, TAG_NAME );
				
			} catch( Exception e ) {
				System.err.println( "error at: " + setup_id + ". " + e.getMessage() );
			}
		}
	}
}
