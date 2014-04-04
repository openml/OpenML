/*
 *  OpenmlApiConnector - Java integration of the OpenML Web API
 *  Copyright (C) 2014 
 *  @author Jan N. van Rijn (j.n.van.rijn@liacs.leidenuniv.nl)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */
package org.openml.webapplication.io;

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
