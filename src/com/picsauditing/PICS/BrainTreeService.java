package com.picsauditing.PICS;

public class BrainTreeService {

	
	
	public static class CreditCard {
		protected String cardNumber = null;
		protected String expirationDate = null;
		public String getCardNumber() {
			return cardNumber;
		}
		public void setCardNumber(String cardNumber) {
			this.cardNumber = cardNumber;
		}
		public String getExpirationDate() {
			return expirationDate;
		}
		public void setExpirationDate(String expirationDate) {
			this.expirationDate = expirationDate;
		}
	}
	
}
