package org.openml.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Input {

	public static InputStreamReader getURL( String sUrl ) throws IOException {
		URL url = new URL( sUrl );
		URLConnection urlConnection = url.openConnection();
		urlConnection.setConnectTimeout(1000);
		urlConnection.setReadTimeout(30000);
		return new InputStreamReader( urlConnection.getInputStream() );
	}
	
	public static InputStreamReader getFile( String filename ) throws IOException {
		return new InputStreamReader( new FileInputStream( new File( filename ) ) );
	}
	
	public static String filename( String sUrl ) {
		if(sUrl.substring(sUrl.lastIndexOf('/') + 1).contains(".") == false ) {
			return sUrl.substring( sUrl.lastIndexOf('/') + 1 );
		}
		return sUrl.substring( sUrl.lastIndexOf('/') + 1, sUrl.lastIndexOf('.') );
	}
}
