package com.picsauditing.util.braintree;

/**
 * Various Codes and their meanings from BrainTree See
 * http://dev.braintreepaymentsolutions.com/vault/direct-post#response for more information.
 */
public class BrainTreeCodes {
	static public String getTransactionResponse(String code) {
		try {
			int code2 = Integer.parseInt(code);
			return getTransactionResponse(code2);
		} catch (Exception e) {
			return getTransactionResponse(0);
		}
	}

	/**
	 * Convert an Transaction Response code from Braintree to a meaningful message
	 * 
	 * @param code
	 * @return
	 */
	static public String getTransactionResponse(int code) {
		switch (code) {
		case 100:
			return "Transaction was approved";
		case 200:
			return "Transaction was declined by Processor";
		case 201:
			return "Do Not Honor";
		case 202:
			return "Insufficient Funds";
		case 203:
			return "Over Limit";
		case 204:
			return "Transaction not allowed";
		case 220:
			return "Incorrect Payment Data";
		case 221:
			return "No such card issuer";
		case 222:
			return "No card number on file with Issuer";
		case 223:
			return "Expired card";
		case 224:
			return "Invalid expiration date";
		case 225:
			return "Invalid card security code";
		case 240:
			return "Call Issuer for further information";
		case 250:
			return "Pick up card";
		case 251:
			return "Lost card";
		case 252:
			return "Stolen card";
		case 253:
			return "Fraudulent card";
		case 260:
			return "Declined with further instructions available (see response text)";
		case 261:
			return "Declined ñ Stop all recurring payments";
		case 262:
			return "Declined ñ Stop this recurring program";
		case 263:
			return "Declined ñ Updated cardholder data available";
		case 264:
			return "Declined ñ Retry in a few days";
		case 300:
			return "Transaction was rejected by gateway";
		case 400:
			return "Transaction error returned by processor";
		case 410:
			return "Invalid merchant configuration";
		case 411:
			return "Merchant account is inactive";
		case 420:
			return "Communication error";
		case 421:
			return "Communication error with issuer";
		case 430:
			return "Duplicate transaction at processor";
		case 440:
			return "Processor format error";
		case 441:
			return "Invalid transaction information";
		case 460:
			return "Processor feature not available";
		case 461:
			return "Unsupported card type";
		}
		return "Unknown code " + code;
	}

	/**
	 * Convert an AVS code from Braintree to a meaningful message
	 * 
	 * @param code
	 * @return
	 */
	static public String getAvsResponse(String code) {
		return "Unknown";
	}

	/**
	 * Convert an CVV code from Braintree to a meaningful message
	 * 
	 * @param code
	 * @return
	 */
	static public String getCvvResponse(String code) {
		return "Unknown";
	}
}
