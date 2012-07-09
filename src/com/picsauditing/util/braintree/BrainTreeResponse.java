package com.picsauditing.util.braintree;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.picsauditing.PICS.NoBrainTreeServiceResponseException;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.util.StreamContentProvider;
import com.picsauditing.util.Strings;

public abstract class BrainTreeResponse {

	StreamContentProvider contentProvider;

	public abstract String get(String param);



	private static class XMLResponse extends BrainTreeResponse {

		private Document document;

		private XMLResponse(BrainTreeRequest request) throws Exception {
			contentProvider = request.getContentProvider();
			InputStream responseContent = contentProvider.openResponseFrom(request.getRequest());
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			document = db.parse(responseContent);
			responseContent.close();

			if (document == null)
				throw new NoBrainTreeServiceResponseException(" No response ");
		}

		public String get(String parameter) {
			NodeList nodes = document.getElementsByTagName(parameter);
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);

				Node value = node.getFirstChild();

				if (value.getNodeType() == Node.TEXT_NODE) {
					return value.getNodeValue();
				}
			}
			return null;
		}
	}




	private static class ParameterizedResponse extends BrainTreeResponse {

		private Map<String, String> params;

		private ParameterizedResponse(BrainTreeRequest request) throws Exception {
			contentProvider = request.getContentProvider();
			String responseContent = contentProvider.getResponseFrom(request.getRequest());
			params = Strings.mapParams(responseContent);

			if (params == null || params.isEmpty())
				throw new NoBrainTreeServiceResponseException((params == null) ? "(Response is null.)" : "(No response.)");
		}

		public String get(String param) {
			return params.get(param);
		}

	}



	private static class IgnoredResponse extends BrainTreeResponse {
		private IgnoredResponse(BrainTreeRequest request) throws Exception {
			contentProvider = request.getContentProvider();
			contentProvider.openResponseFrom(request.getRequest()).close();
		}

		public String get(String param) {
			return null;
		}
	}




	public static class BrainTreeRequest {

		@Autowired
		AppPropertyDAO dao;

		@Autowired
		StreamContentProvider contentProvider;

		private StringBuilder requestString;
		private static final String urlBase = "https://secure.braintreepaymentgateway.com/api/";

		private String getRequest() {
			appendUsernameAndPasswordTo(requestString);
			return requestString.toString();
		}

		public BrainTreeResponse creditCardFor(int contractorID) throws Exception {
			requestString = new StringBuilder(urlBase).append("query.php?report_type=customer_vault");
			requestString.append("&customer_vault_id=").append(contractorID);
			return new XMLResponse(this);
		}

		public BrainTreeResponse deleteCreditCardFor(int contractorID) throws Exception {
			requestString = new StringBuilder(urlBase).append("transact.php?customer_vault=delete_customer");
			requestString.append("&customer_vault_id=").append(contractorID);
			return new IgnoredResponse(this);		}

		public BrainTreeResponse transactionConditionFor(String transactionID) throws Exception {
			requestString = new StringBuilder(urlBase).append("query.php?transaction_id=").append(transactionID);
			return new XMLResponse(this);
		}

		public BrainTreeResponse processRefund(String transactionID, BigDecimal amount) throws Exception {
			requestString = new StringBuilder(urlBase).append("transact.php?type=refund");
			requestString.append("&transactionid=").append(transactionID);
			requestString.append("&amount=").append(amount);
			return new ParameterizedResponse(this);
		}

		public BrainTreeResponse processCancellation(String transactionID) throws Exception {
			requestString = new StringBuilder(urlBase).append("transact.php?type=refund");
			requestString.append("&transactionid=").append(transactionID);
			return new ParameterizedResponse(this);
		}

		public BrainTreeResponse voidTransaction(String transactionID) throws Exception {
			requestString = new StringBuilder(urlBase).append("transact.php?type=void");
			requestString.append("&transactionid=").append(transactionID);
			return new ParameterizedResponse(this);
		}

		public BrainTreeResponse processPayment(Payment payment, Invoice invoice) throws Exception {
			requestString = new StringBuilder(urlBase).append("transact.php?type=sale");
			requestString.append("&customer_vault_id=").append(payment.getAccount().getId());
			requestString.append("&amount=").append(payment.getTotalAmount());
			requestString.append("&currency=").append(payment.getCurrency());

			String processorID = "";
			switch (payment.getCurrency()) {
			case CAD:
				processorID = dao.find("brainTree.processor_id.canada").getValue();
				break;
			case GBP:
				processorID = dao.find("brainTree.processor_id.gbp").getValue();
				break;
			case EUR:
				processorID = dao.find("brainTree.processor_id.eur").getValue();
				break;
			default:
				processorID = dao.find("brainTree.processor_id.us").getValue();
			}
			// if the processorID was somehow empty, BrainTree would default to our primary processor which is USD
			requestString.append("&processor_id=").append(processorID);

			if (invoice != null)
				requestString.append("&order_id=").append(invoice.getId());

			return new ParameterizedResponse(this);
		}

		private void appendUsernameAndPasswordTo(StringBuilder request) {
			String userName = dao.find("brainTree.username").getValue();
			String password = dao.find("brainTree.password").getValue();

			if (userName == null || password == null)
				throw new RuntimeException(new BrainTreeLoginException("Missing BrainTree username and password"));

			request.append("&username=").append(userName);
			request.append("&password=").append(password);
		}

		private StreamContentProvider getContentProvider() {
			return contentProvider;
		}
	}

}
