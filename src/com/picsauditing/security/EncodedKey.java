package com.picsauditing.security;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.picsauditing.util.Base64;

/**
 * General-purpose object that represents an encoded key, such as a password, or
 * an API key. This class can be instantiated for repeated use, or there are
 * convenient static methods for one-time use. TODO find all other PICS
 * Organizer business logic that depends on Random or SecureRandom and move it
 * to here.
 * 
 */
public class EncodedKey {
	private int maxCharLength = 32;
	private SecureRandom random = new SecureRandom();
	private String key;
	
	public EncodedKey(int maxCharLength) {
		this.maxCharLength = maxCharLength;

	}
	public String generateRandom() {
		return generateRandom(36);
	}

	public String generateRandom(int radix) {
		int bitsPerChar = (int) Math.ceil(Math.sqrt(radix));
		key = new BigInteger(bitsPerChar * maxCharLength, random).toString(radix);
		return key.substring(0,maxCharLength);
	}
	/**
	 * Generate a random API key (for use with the REST API).
	 * For now, we're using a straight random number returned using a radix of 36 (10 digits and 26 letters) with a max character length of 32.
	 * Later, we might want to switch to a GUID, or something else along those lines.
	 */
	public static String randomApiKey() {
		EncodedKey encodedKey = new EncodedKey(32);
		return encodedKey.generateRandom(36);
	}
	
	/**
	 * Generate a random user password.
	 */
	public static String randomPassword() {
		// TODO Switch this to use BigInteger & SecureRandom to be consistent with randomApiKey()
		return Long.toString(new Random().nextLong());
	}

	public static String newServerSecretKey() {
		KeyGenerator kg;
		try {
			kg = KeyGenerator.getInstance("HmacSHA1");
			SecretKey sk = kg.generateKey();
			return Base64.encodeBytes(sk.getEncoded());
		} catch (NoSuchAlgorithmException e) {
			// This shouldn't really happen given that HmacSHA1 is built into
			// java
			e.printStackTrace();
		}
		return null;
	}
}