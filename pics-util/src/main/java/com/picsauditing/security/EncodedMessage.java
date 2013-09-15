package com.picsauditing.security;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import com.picsauditing.util.Base64;

public class EncodedMessage {

	public static String hmacBase64(String message, SecretKey sk) {
		if (sk == null) {
			return null;
		}
		return Base64.encodeBytes(hmac(message, sk));
	}

	public static byte[] hmac(String message, SecretKey sk) {
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(sk);
			return mac.doFinal(message.getBytes());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String hash(String seed) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			return e.getMessage();
		}
		digest.update(seed.getBytes());
		byte[] hashed = digest.digest();
		BigInteger number = new BigInteger(1, hashed);
		// String value = Base64.encodeBytes(hashed);
		return number.toString(16);
	}

	public static String md5(String seed) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(seed.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			return number.toString(16);
		} catch (NoSuchAlgorithmException e) {
			return e.getMessage();
		}
	}
}
