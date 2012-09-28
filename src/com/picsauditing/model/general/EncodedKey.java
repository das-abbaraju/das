package com.picsauditing.model.general;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * General-purpose object tha represents an encoded key, such as a password, or an API key.
 * This class can be instatiated for repeated use, or there are convenient static methods for one-time use. 
 * TODO find all other PICS Organizer business logic that depends on Random or SecureRandom and move it to here.
 *
 */
public class EncodedKey {
	private int maxCharLength = 32;
	private SecureRandom random = new SecureRandom();
	private String key;
	
	public EncodedKey(int maxCharLength) {
		super();
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
}
