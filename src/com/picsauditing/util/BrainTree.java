package com.picsauditing.util;


public class BrainTree {
	public static String buildHash(String orderID, String amount, String vaultID, String time, String key) {
		StringBuilder salt = new StringBuilder();
		salt.append(orderID).append("|");
		salt.append(amount).append("|");
		if (!Strings.isEmpty(vaultID))
			salt.append(vaultID).append("|");
		salt.append(time).append("|");
		salt.append(key);
		String hash = Strings.md5(salt.toString());
		return hash;
	}
}
