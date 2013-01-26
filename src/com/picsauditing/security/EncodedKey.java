package com.picsauditing.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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
	final static Logger logger = LoggerFactory.getLogger(EncodedKey.class);

	private static final int DEFAULT_RADIX = 36;
	private static final int MIN_SUFFICIENT_KEY_LENGTH = 20;
	private static final int MIN_UNIQUE_PERCENT = 60;
	private int maxCharLength = 32;
	private SecureRandom random = new SecureRandom();
	private String key;

	public EncodedKey(int maxCharLength) {
		this.maxCharLength = maxCharLength;

	}

	public String generateRandom() {
		return generateRandom(DEFAULT_RADIX);
	}

	public String generateRandom(int radix) {
		int bitsPerChar = (int) Math.ceil(Math.sqrt(radix));
		key = new BigInteger(bitsPerChar * maxCharLength, random).toString(radix);
		return key.substring(0, maxCharLength);
	}

	/**
	 * Generate a random API key (for use with the REST API). For now, we're
	 * using a straight random number returned using a radix of 36 (10 digits
	 * and 26 letters) with a max character length of 32. Later, we might want
	 * to switch to a GUID, or something else along those lines.
	 */
	public static String randomApiKey() {
		return randomApiKey(20);
	}

	/* testable*/ static String randomApiKey(int maxTries) {
		EncodedKey encodedKey = new EncodedKey(32);
		String result = null;
		for (int i = 0; i < maxTries; i++) {
			try {
				result = encodedKey.generateRandom(36);
				EncodedKey.verifySufficientlyComplex(result);
				return result;
			} catch (SecurityException e) {
				// do nothing (just loop)
			}
		}
		throw new SecurityException("Unable to generate a sufficiently complex key after "+maxTries+" attempts.");
	}

	/**
	 * Generate a random user password.
	 */
	public static String randomPassword() {
		// TODO Switch this to use BigInteger & SecureRandom to be consistent
		// with randomApiKey()
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

	public static void verifySufficientlyComplex(String key) throws SecurityException {
		boolean soFarSoGood = (key != null);

		if (soFarSoGood) {
			soFarSoGood = (key.length() >= MIN_SUFFICIENT_KEY_LENGTH);
		}
		if (soFarSoGood) {
			Set uniqueChars = new HashSet();
			for (int pos = 0; pos < key.length(); pos++) {
				uniqueChars.add(key.charAt(pos));
			}
			soFarSoGood = ((uniqueChars.size() * 100 / key.length()) >= MIN_UNIQUE_PERCENT);
		}
		if (!soFarSoGood) {
			logger.warn("Invalid attempt to use an insufficiently complex key ('{}').", key);
			throw new SecurityException("Invalid attempt to use an insufficiently complex key.");
		}
	}
}
