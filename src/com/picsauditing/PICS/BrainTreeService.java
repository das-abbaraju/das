package com.picsauditing.PICS;

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BrainTreeService {

	protected String userName = null;
	protected String password = null;
	
	
	public CreditCard getCustomerData( int contractorId ) throws Exception {

		CreditCard response = new CreditCard();

		String urlBase = "https://secure.braintreepaymentgateway.com/api/query.php?report_type=customer_vault";
		
		StringBuilder request = new StringBuilder( urlBase )
			.append("&username=")
			.append(userName)
			.append("&password=")
			.append(password)
			.append("&customer_vault_id=")
			.append(new Integer(contractorId).toString());
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		URL url = new URL(request.toString());
		InputStream inputStream = url.openStream();
		Document document = db.parse(inputStream);
		inputStream.close();
		
		String cc = getValueFromDocument(document, "cc_number");
		String expDate = getValueFromDocument(document, "cc_exp");

		if( cc != null && expDate != null ) {
			response.setCardNumber(cc);
			response.setExpirationDate(expDate);
		}
		
		return response;
	}

	protected String getValueFromDocument(Document document, String tagName) {
		NodeList list = document.getElementsByTagName(tagName);	

		for( int i = 0; i < list.getLength(); i++ ) {
			Node node = list.item(i);
			
			Node value = node.getFirstChild();
			
			if( value.getNodeType() == Node.TEXT_NODE ) {
				return value.getNodeValue();
			}
		}
		return null;
	}
	
	
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
	
	public static void main(String[] args) throws Exception {
		
		BrainTreeService service = new BrainTreeService();
		service.setUserName("testapi");
		service.setPassword("password1");
		
		CreditCard cc = service.getCustomerData(81);
		
		System.out.println( cc.getCardNumber() );
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
	
	
	
}
