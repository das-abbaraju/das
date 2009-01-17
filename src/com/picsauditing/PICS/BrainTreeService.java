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
	
	
	public CreditCard getCreditCard( int contractorId ) throws Exception {

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

	public void addCreditCard( int contractorId, CreditCard cc ) throws Exception {
		
		String urlBase = "https://secure.braintreepaymentgateway.com/api/transact.php?customer_vault=add_customer";
		
		StringBuilder request = new StringBuilder( urlBase )
		.append("&username=")
		.append(userName)
		.append("&password=")
		.append(password)
		.append("&customer_vault_id=")
		.append(new Integer(contractorId).toString())
		.append("&ccnumber=")
		.append(cc.getCardNumber())
		.append("&ccexp=")
		.append(cc.getExpirationDate());
		
		URL url = new URL(request.toString());
		
		InputStream inputStream = url.openStream();
		inputStream.close();
	}
	
	public void deleteCreditCard( int contractorId ) throws Exception {
		
		String urlBase = "https://secure.braintreepaymentgateway.com/api/transact.php?customer_vault=delete_customer";
		
		StringBuilder request = new StringBuilder( urlBase )
		.append("&username=")
		.append(userName)
		.append("&password=")
		.append(password)
		.append("&customer_vault_id=")
		.append(new Integer(contractorId).toString());
		
		URL url = new URL(request.toString());
		InputStream inputStream = url.openStream();
		inputStream.close();
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

		
		CreditCard cc = new CreditCard();
		cc.setCardNumber("4111111111111111");
		cc.setExpirationDate("1010");
		
		service.addCreditCard(654321, cc);
		
		cc = service.getCreditCard(654321);
		System.out.println( cc.getCardNumber() );

		
		service.deleteCreditCard(654321);

		try {
			cc = service.getCreditCard(654321);
			System.out.println( cc.getCardNumber() );
			
		}
		catch( Exception problem ) {
			
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
	
	
	
}
