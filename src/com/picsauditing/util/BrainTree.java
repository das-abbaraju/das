package com.picsauditing.util;


public class BrainTree {
	public static String buildHash(String orderID, String amount, String vaultID, String time, String key) {
		// TODO add some warnings if the parameters look suspicious
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
	
	public static String buildHash(String orderID, String amount, String response, String transactionid, 
			String avsresponse, String cvvresponse, String vaultID, String time, String key) {
		// orderid|amount|response|transactionid|avsresponse|cvvresponse|customer_vault_id|time|key
		// TODO add some warnings if the parameters look suspicious
		StringBuilder salt = new StringBuilder();
		salt.append(orderID).append("|");
		salt.append(amount).append("|");
		salt.append(response).append("|");
		salt.append(transactionid).append("|");
		salt.append(avsresponse).append("|");
		salt.append(cvvresponse).append("|");
		if (!Strings.isEmpty(vaultID))
			salt.append(vaultID).append("|");
		salt.append(time).append("|");
		salt.append(key);
		String hash = Strings.md5(salt.toString());
		return hash;
	}
}
