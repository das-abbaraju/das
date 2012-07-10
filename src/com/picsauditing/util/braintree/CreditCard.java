package com.picsauditing.util.braintree;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.braintreegateway.exceptions.BraintreeException;
import com.picsauditing.util.Strings;

public class CreditCard {

	public CreditCard(BrainTreeResponse response) {
		cardNumber = response.get("cc_number");
		expirationDate = response.get("cc_exp");
		if (cardNumber == null || expirationDate == null)
			throw new BraintreeException();
	}

	public CreditCard(String ccNumber) {
		cardNumber = ccNumber;
	}

	private String cardNumber = null;
	private String expirationDate = null;

	public String getCardNumber() {
		return cardNumber;
	}

	public String getLastFour() {
		return cardNumber.substring(cardNumber.length() - 4, cardNumber.length());
	}

	public String getSimpleExpirationDate() {
		return expirationDate;
	}

	public Date getExpirationDate() {
		try {
			return new SimpleDateFormat("MMyy").parse(expirationDate);
		} catch (Exception e) {
		}
		return null;
	}

	public String getFormattedExpirationDateString() {

		if (expirationDate != null) {
			try {
				return new SimpleDateFormat("MM/yy").format(new SimpleDateFormat("MMyy").parse(expirationDate));
			} catch (Exception e) {
			}
		}
		return "";
	}

	public String getCardType() {
		// TODO make this more complete
		// http://en.wikipedia.org/wiki/Credit_card_number
		if (Strings.isEmpty(cardNumber))
			return "";
		String c = cardNumber.substring(0, 1);
		if (c.equals("3"))
			return "American Express";
		if (c.equals("4"))
			return "Visa";
		if (c.equals("5"))
			return "Mastercard";
		if (c.equals("6"))
			return "Discover";
		return "Unknown";
	}
}
