package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.EmailHash;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class EmailHashCodeGenerator {

	public static String generateHashCode(final EmailHash emailHash) throws NoSuchAlgorithmException {
		String hashSalt = Long.toString(DateBean.today().getTime()) + emailHash.getEmailAddress()
				+ emailHash.getEmployee().getAccountId();
		MessageDigest msgDigest = MessageDigest.getInstance("MD5");
		msgDigest.update(hashSalt.getBytes());
		byte[] hashed = msgDigest.digest();

		BigInteger number = new BigInteger(1, hashed);
		return number.toString(16).replace("+", "_");
	}

}