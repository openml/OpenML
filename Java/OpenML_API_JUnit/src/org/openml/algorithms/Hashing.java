package org.openml.algorithms;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
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

	public static String md5(File input) throws NoSuchAlgorithmException,
			IOException {
		String result;
		MessageDigest md = MessageDigest.getInstance("MD5");
		InputStream is = new FileInputStream(input);
		try {
			is = new DigestInputStream(is, md);
		} finally {
			is.close();
		}
		BigInteger hash = new BigInteger(1, md.digest());
		result = hash.toString(16);
		while (result.length() < 32) { // 40 for SHA-1
			result = "0" + result;
		}
		return result;
	}
}
