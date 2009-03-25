package com.picsauditing.PICS;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.util.Strings;

public class BrainTreeService {
	private static String urlBase = "https://secure.braintreepaymentgateway.com/api/";

	protected String userName = null;
	protected String password = null;

	public CreditCard getCreditCard(int contractorId) throws Exception {

		StringBuilder request = new StringBuilder(urlBase).append("query.php?report_type=customer_vault");
		appendUsernamePassword(request);
		request.append("&customer_vault_id=").append(contractorId);

		URL url = new URL(request.toString());
		InputStream inputStream = url.openStream();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(inputStream);
		inputStream.close();

		String cc = getValueFromDocument(document, "cc_number");
		String expDate = getValueFromDocument(document, "cc_exp");

		if (cc != null) {
			CreditCard response = new CreditCard();
			response.setCardNumber(cc);
			response.setExpirationDate(expDate);
			return response;
		}

		return null;
	}

	public void deleteCreditCard(int contractorId) throws Exception {

		StringBuilder request = new StringBuilder(urlBase).append("transact.php?customer_vault=delete_customer");
		appendUsernamePassword(request);
		request.append("&customer_vault_id=").append(contractorId);

		URL url = new URL(request.toString());
		InputStream inputStream = url.openStream();
		inputStream.close();
	}
	
	public boolean processPayment(Invoice invoice) throws Exception {

		StringBuilder request = new StringBuilder(urlBase).append("transact.php?type=sale");
		appendUsernamePassword(request);
		request.append("&customer_vault_id=").append(invoice.getAccount().getId());
		request.append("&orderid=").append(invoice.getId());
		request.append("&amount=").append(invoice.getTotalAmount());
		
		StringBuffer buffer = new StringBuffer();
		
		URL url = new URL(request.toString());
		InputStream inputStream = url.openStream();
		int nextByte;
		while ((nextByte = inputStream.read()) > -1) {
			buffer.append((char)nextByte);
		}
		inputStream.close();
		Map<String, String> map = Strings.mapParams(buffer.toString());
		String response = map.get("response");
		if (response.equals("1")) {
			invoice.setTransactionID(map.get("transactionid"));
			return true;
		} else {
			throw new Exception(map.get("responsetext"));
		}
	}

	protected String getValueFromDocument(Document document, String tagName) {
		NodeList list = document.getElementsByTagName(tagName);

		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);

			Node value = node.getFirstChild();

			if (value.getNodeType() == Node.TEXT_NODE) {
				return value.getNodeValue();
			}
		}
		return null;
	}

	public static class CreditCard {
		protected String cardNumber = null;
		protected String expirationDate = null;
		
		public CreditCard() {}
		
		public CreditCard(String cardNumber) {
			this.cardNumber = cardNumber;
		}
		
		public CreditCard(String cardNumber, String expirationDate) {
			this.cardNumber = cardNumber;
			this.expirationDate = expirationDate;
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

		public String getExpirationDateFormatted() {

			if (expirationDate != null) {
				try {
					return new SimpleDateFormat("MM/yy").format(new SimpleDateFormat("MMyy").parse(expirationDate));
				} catch (Exception e) {
				}
			}
			return "";
		}
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	private void appendUsernamePassword(StringBuilder request) {
		request.append("&username=").append(userName);
		request.append("&password=").append(password);
	}

}
