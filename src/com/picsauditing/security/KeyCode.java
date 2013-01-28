package com.picsauditing.security;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General-purpose object that represents an encoded key, such as a password, or
 * an API key. This class can be instantiated for repeated use, or there are
 * convenient static methods for one-time use.
 * 
 * There are two different constructors for this class: Use EncodedKey(int
 * maxCharLength) if you intend to generate a key(s). Use EncodedKey(String key)
 * if you intend to work with an existing key (and/or generate more like it).
 * 
 * TODO Rename this class (back) to EncodedKey (after EncodedKey is renamed to
 * something like EncodedKeyUtils)
 * 
 * TODO this class is either completely redundant with javax.crypto.KeyGenerator
 * and javax.crypto.SecretKey, or it should at least be taking advantage of
 * them.
 * 
 */
public class KeyCode {
	final static Logger logger = LoggerFactory.getLogger(KeyCode.class);

	private static final int DEFAULT_RADIX = 36;
	private static final int DEFAULT_MIN_LENGTH = 32;
	private static final int MIN_COMPLEXITY = 12;
	private int maxCharLength = DEFAULT_MIN_LENGTH;
	private SecureRandom random = new SecureRandom();
	private String key;
	private int maxTries = 20;
	private int radix = DEFAULT_RADIX;

	/**
	 * Use this constructor if you intend to generate a key
	 */
	public KeyCode(int maxCharLength) {
		this.maxCharLength = maxCharLength;
	}

	/**
	 * Use this constructor if you intend to work with an existing key (and/or
	 * generate more like it).
	 */
	public KeyCode(String key) {
		this.key = key;
		if (key != null && key.length() > maxCharLength) {
			this.maxCharLength = key.length();
		}
	}

	/**
	 * Generates a random key according to the maxCharLength and radix
	 * properties. (Use getKey() to find out what it is.)
	 */
	public void generateRandom() {
		int bitsPerChar = (int) Math.ceil(Math.sqrt(radix));
		key = new BigInteger(bitsPerChar * maxCharLength, random).toString(radix).substring(0, maxCharLength);
	}

	/**
	 * Same as generateRandom() except that the generated key is guaranteed to
	 * be sufficiently complex (as long as the maxTries property is high
	 * enough).
	 */
	public void generateRandomComplex() {
		for (int i = 0; i < maxTries; i++) {
			try {
				setRadix(DEFAULT_RADIX);
				generateRandom();
				verifySufficientlyComplex();
				return;
			} catch (SecurityException e) {
				// do nothing (just loop)
			}
		}
		throw new SecurityException("Unable to generate a sufficiently complex key after " + maxTries + " attempts.");
	}

	/**
	 * The radix is the number of possible unique characters that will be
	 * generated. Default is 36.
	 * 
	 * 10 = digits only
	 * 
	 * 36 = digits and lower case letters
	 * 
	 * 62 = digits, lower case letters and upper case letters
	 */
	public void setRadix(int radix) {
		this.radix = radix;
	}

	/**
	 * The maximum number of attempts that generateRandomComplex() will make at
	 * generating a key that is sufficiently complex. Default is 20.
	 */
	public void setMaxTries(int maxTries) {
		this.maxTries = maxTries;
	}

	public void verifySufficientlyComplex() throws SecurityException {
		if (complexity() < MIN_COMPLEXITY) {
			logger.warn("Invalid attempt to use an insufficiently complex key ('{}').", key);
			throw new SecurityException("Invalid attempt to use an insufficiently complex key.");
		}
	}

	/**
	 * For now, complexity is merely the number of unique characters.
	 * 
	 * TODO separately evaluate the number of unique upper-case, lower-case,
	 * digits, etc. and give an even distribution more weight.
	 */
	/* testable */int complexity() {
		if (key == null) {
			return 0;
		}
		Set uniqueChars = new HashSet();
		for (int pos = 0; pos < key.length(); pos++) {
			uniqueChars.add(key.charAt(pos));
		}
		return uniqueChars.size();
	}

	/**
	 * The generated key.
	 */
	public String getKey() {
		return key;
	}

}
