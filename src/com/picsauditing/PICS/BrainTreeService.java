package com.picsauditing.PICS;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.util.Strings;

public class BrainTreeService {
	private static String urlBase = "https://secure.braintreepaymentgateway.com/api/";

	protected String userName = null;
	protected String password = null;

	public CreditCard getCreditCard(int contractorId) throws Exception {
		StringBuilder request = new StringBuilder(urlBase).append("query.php?report_type=customer_vault");
		appendUsernamePassword(request);
		request.append("&customer_vault_id=").append(contractorId);

		Document document = getDocument(request.toString());
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

	public String getTransactionCondition(String transactionID) throws Exception {
		StringBuilder request = new StringBuilder(urlBase).append("query.php?transaction_id=").append(transactionID);
		appendUsernamePassword(request);

		Document document = getDocument(request.toString());
		return getValueFromDocument(document, "condition");
	}

	public boolean processPayment(Payment payment, Invoice invoice) throws IOException,
			BrainTreeServiceErrorResponseException, NoBrainTreeServiceResponseException, BrainTreeLoginException {
		StringBuilder request = new StringBuilder(urlBase).append("transact.php?type=sale");
		appendUsernamePassword(request);
		request.append("&customer_vault_id=").append(payment.getAccount().getId());
		request.append("&amount=").append(payment.getTotalAmount());
		if (invoice != null)
			request.append("&order_id=").append(invoice.getId());

		Map<String, String> map = getUrl(request.toString());

		// No way to automatically validate a transaction if no response
		// Amount not query-able via API
		// Admin will handle manually

		if (map == null || map.isEmpty())
			throw new NoBrainTreeServiceResponseException((map == null) ? " Response is null " : " No response ");
		String response = map.get("response");
		if (response.equals("1")) {
			payment.setTransactionID(map.get("transactionid"));
			return true;
		} else if (response.equals("2")) {
			String responseCode = map.get("response_code");
			throw new BrainTreeCardDeclinedException(map.get("responsetext") + " "
					+ BrainTreeCodes.getTransactionResponse(responseCode));
		} else {
			String responseCode = map.get("response_code");
			throw new BrainTreeServiceErrorResponseException(map.get("responsetext") + " "
					+ BrainTreeCodes.getTransactionResponse(responseCode));
		}
	}

	public boolean processRefund(String transactionID, BigDecimal amount) throws Exception {
		StringBuilder request = new StringBuilder(urlBase).append("transact.php?type=refund");
		appendUsernamePassword(request);
		request.append("&transactionid=").append(transactionID);
		request.append("&amount=").append(amount);

		Map<String, String> map = getUrl(request.toString());
		String response = map.get("response");
		if (response.equals("1")) {
			return true;
		} else {
			throw new Exception(map.get("responsetext"));
		}
	}

	public boolean processCancellation(String transactionID) throws Exception {
		StringBuilder request = new StringBuilder(urlBase).append("transact.php?type=refund");
		appendUsernamePassword(request);
		request.append("&transactionid=").append(transactionID);

		Map<String, String> map = getUrl(request.toString());
		if (map.get("response").equals("1")) {
			return true;
		} else {
			throw new Exception(map.get("responsetext"));
		}
	}

	/**
	 * Voiding a transaction will cancel an existing sale or captured
	 * authorization from actually charging the card. In addition, non-captured
	 * authorizations can be voided to prevent any future capture. Note however,
	 * that the amount is still reserved on the card and will take a few days to
	 * expire. You will have to call the issuing bank to request that the
	 * authorization be removed if the customer does not want to wait for it to
	 * expire on its own. Voids can only occur if the transaction has not been
	 * settled; settled transactions should be refunded.
	 * 
	 * @param transactionid
	 * @throws BrainTreeLoginException
	 *             , BrainTreeServiceErrorResponseException, IOException
	 */

	public boolean voidTransaction(String transactionID) throws BrainTreeServiceErrorResponseException, IOException,
			BrainTreeLoginException {
		StringBuilder request = new StringBuilder(urlBase).append("transact.php?type=void");
		appendUsernamePassword(request);
		request.append("&transactionid=").append(transactionID);

		Map<String, String> map = getUrl(request.toString());
		String response = map.get("response");
		if (response.equals("1")) {
			return true;
		} else {
			throw new BrainTreeServiceErrorResponseException(map.get("responsetext"));
		}
	}

	private Document getDocument(String address) throws SAXException, IOException, ParserConfigurationException {
		URL url = new URL(address);
		InputStream inputStream = url.openStream();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(inputStream);
		inputStream.close();
		return document;
	}

	private Map<String, String> getUrl(String address) throws IOException {
		URL url = new URL(address);
		InputStream inputStream = url.openStream();

		StringBuffer buffer = new StringBuffer();
		int nextByte;
		while ((nextByte = inputStream.read()) > -1) {
			buffer.append((char) nextByte);
		}
		inputStream.close();
		return Strings.mapParams(buffer.toString());
	}

	private String getValueFromDocument(Document document, String tagName) {
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

		public CreditCard() {
		}

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

		public Date getExpirationDate2() {
			try {
				return new SimpleDateFormat("MMyy").parse(expirationDate);
			} catch (Exception e) {
			}
			return null;
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

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private void appendUsernamePassword(StringBuilder request) throws BrainTreeLoginException {
		if (userName == null || password == null)
			throw new BrainTreeLoginException("Missing BrainTree username and password");

		request.append("&username=").append(userName);
		request.append("&password=").append(password);
	}

}
