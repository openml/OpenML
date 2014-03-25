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
