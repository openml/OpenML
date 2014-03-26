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
package org.openml.apiconnector.algorithms;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashing {

	public static String md5(String input) throws NoSuchAlgorithmException {
		String result;
		MessageDigest md = MessageDigest.getInstance("MD5"); // or "SHA-1"
		md.update(input.getBytes());
		BigInteger hash = new BigInteger(1, md.digest());
		result = hash.toString(16);
		while (result.length() < 32) { // 40 for SHA-1
			result = "0" + result;
		}
		return result;
	}

	public static String md5(File input) throws IOException {
		FileInputStream fis = new FileInputStream(input);
		return org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
	}
}
