package org.openml.io;

import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Writer extends Writer {
	MessageDigest md;
	
	public Md5Writer() throws NoSuchAlgorithmException {
		md = MessageDigest.getInstance("MD5");
	}
	
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		md.update( String.valueOf( cbuf ).substring( off, off + len ).getBytes() );
	}

	@Override
	public void flush() throws IOException {
		BigInteger hash = new BigInteger(1, md.digest());
		String result = hash.toString(16);
		while (result.length() < 32) { // 40 for SHA-1
			result = "0" + result;
		}
		System.out.println( result );
	}

	@Override
	public void close() throws IOException {
		flush();
	}
}
