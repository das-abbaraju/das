package com.picsauditing.actions;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@SuppressWarnings("serial")
public class ProcessQBResponseXML extends PicsActionSupport {

	private static final String ACCOUNT = "Account";
	private static final String INVOICE = "Invoice";

	@Autowired
	private ContractorAccountDAO contractorAccountDAO;
	@Qualifier("InvoiceDAO")
	@Autowired
	private InvoiceDAO invoiceDAO;



	private String qbresponsexml = null;

	public String execute() throws NoRightsException, IOException, SAXException, ParserConfigurationException {
		permissions.tryPermission(OpPerms.Billing);

		if (button != null) {
			if (button.equals("save")) {
				save();
			}
		}

		return SUCCESS;
	}

	public String save() throws NoRightsException, ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(new ByteArrayInputStream(this.getQbresponsexml().getBytes()));
		// normalize text representation
		doc.getDocumentElement ().normalize ();
		Element rootElement = doc.getDocumentElement();

		if (!("QBXML".equals(rootElement.getNodeName())))   {
			    addActionError("Didn't find 'QBXML' as root element");
			return ERROR;
		}

		addActionMessage("Root element of the doc is QBXML");

		NodeList childNodes = rootElement.getChildNodes();
		Boolean qbxmlmsgsrsFound = false;
		for (int i = 0; i < childNodes.getLength(); ++i) {
			Node childNode = childNodes.item(i);

			if (childNode.getNodeName().equals("QBXMLMsgsRs")) {
				qbxmlmsgsrsFound = true;
				addActionMessage("Processing QBXMLMsgsRs child node");
				processQBXMLMsgsRs(childNode.getChildNodes());
				break;
			}

		}
		if (!qbxmlmsgsrsFound)   {
			addActionError("Didn't find 'QBXMLMsgsRs' node");
			return ERROR;
		}

		return SUCCESS;
	}

	private void processQBXMLMsgsRs(NodeList nodeList) {

		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);

			if (node.getNodeName().equals("#text")) {
				continue;
			}

			addActionMessage("Processing node of type '"+node.getNodeName()+"'");

			switch (node.getNodeName()) {
				case "InvoiceAddRs":
					processInvoiceAdd(node);
					break;
				case "CustomerAddRs":
					processCustomerAdd(node);
					break;
				case "ReceivePaymentAddRs":
					processPaymentAdd(node);
				default:
					addActionError("Need code to process node of type '"+node.getNodeName()+"'");
					break;
			}
			continue;

		}


	}

	private void processPaymentAdd(Node paymentRsNode) {
		NamedNodeMap attributes = paymentRsNode.getAttributes();
		Node statusMessage= attributes.getNamedItem("statusMessage");
		if (statusMessage.getNodeValue().equals("Status OK")) {
			NodeList childNodes = paymentRsNode.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				Node node = childNodes.item(i);
				if (node.getNodeName().equals("ReceivePaymentRet")) {
					processPaymentRetNode(node);
				}
			}
		} else {
			addActionError("Payment Add Request failed -- requestID "+attributes.getNamedItem("requestID").getNodeValue());
			return;
		}

	}

	private void processPaymentRetNode(Node paymentRet) {
		NodeList paymentRetChildNodes = paymentRet.getChildNodes();
		String qbListID = "";
		String paymentID = "";
		for (int j = 0; j < paymentRetChildNodes.getLength(); ++j) {
			Node nodeInQuestion = paymentRetChildNodes.item(j);
			switch (nodeInQuestion.getNodeName()) {
				case "TxnID":
					qbListID = nodeInQuestion.getTextContent();
					break;
				case "Memo":
					paymentID = nodeInQuestion.getTextContent().replaceAll("[^0-9]","");
					break;
				default:
					break;
			}
			if (!qbListID.isEmpty() && !paymentID.isEmpty()) {
				updateInvoice(qbListID, paymentID);
				return;
			}
		}
		addActionError("Did not complete processing of payment '"+paymentID+"' qbListID '"+qbListID+"'");
		return;
	}

	private void processCustomerAdd(Node customerRsNode) {
		NamedNodeMap attributes = customerRsNode.getAttributes();
		Node statusMessage= attributes.getNamedItem("statusMessage");
		if (statusMessage.getNodeValue().equals("Status OK")) {
			NodeList childNodes = customerRsNode.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				Node node = childNodes.item(i);
				if (node.getNodeName().equals("CustomerRet")) {
					processCustomerRetNode(node);
				}
			}
		} else {
			addActionError("Customer Add Request failed -- requestID "+attributes.getNamedItem("requestID").getNodeValue());
			return;
		}

	}

	private void processCustomerRetNode(Node customerRet) {
		NodeList customerRetChildNodes = customerRet.getChildNodes();
		String qbListID = "";
		String contractorID = "";
		for (int j = 0; j < customerRetChildNodes.getLength(); ++j) {
			Node nodeInQuestion = customerRetChildNodes.item(j);
			switch (nodeInQuestion.getNodeName()) {
				case "ListID":
					qbListID = nodeInQuestion.getTextContent();
					break;
				case "Name":
					contractorID = nodeInQuestion.getTextContent();
					break;
				default:
					break;
			}
			if (!qbListID.isEmpty() && !contractorID.isEmpty()) {
				updateContractor(qbListID, contractorID);
				return;
			}
		}
		addActionError("Did not complete processing of contractor '"+contractorID+"' qbListID '"+qbListID+"'");
		return;
	}

	private void updateContractor(String qbListID, String contractorID) {
		ContractorAccount contractor = contractorAccountDAO.find(Integer.parseInt(contractorID));
		String qbListIDColumnName = "";
		switch (contractor.getCurrency()) {
			 case USD:
				 contractor.setQbListID(qbListID);
				 qbListIDColumnName = "qbListID";
				 break;
			 case CAD:
				 contractor.setQbListCAID(qbListID);
				 qbListIDColumnName = "qbListCAID";
				 break;
			 case EUR:
				 contractor.setQbListEUID(qbListID);
				 qbListIDColumnName = "qbListEUID";
				 break;
			case GBP:
				contractor.setQbListUKID(qbListID);
				qbListIDColumnName = "qbListUKID";
				break;
		}
		contractor.setQbSync(false);
		addActionMessage("Executing: UPDATE accounts SET " + qbListIDColumnName + " = '" + qbListID + "', qbSync = 0 where id = " + contractorID + ";");
		contractorAccountDAO.save(contractor);

	}

	private void processInvoiceAdd(Node invoiceRsNode) {
		NamedNodeMap attributes = invoiceRsNode.getAttributes();
		Node statusMessage= attributes.getNamedItem("statusMessage");
		if (statusMessage.getNodeValue().equals("Status OK")) {
			NodeList childNodes = invoiceRsNode.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				Node node = childNodes.item(i);
			  if (node.getNodeName().equals("InvoiceRet")) {
				  processInvoiceRetNode(node);
			  }
			}
		} else {
			addActionError("Invoice Add Request failed -- requestID "+attributes.getNamedItem("requestID").getNodeValue());
			return;
		}


	}

	private void processInvoiceRetNode(Node invoiceRet) {
		NodeList invoiceRetChildNodes = invoiceRet.getChildNodes();
		String qbListID = "";
		String invoiceID = "";
		for (int j = 0; j < invoiceRetChildNodes.getLength(); ++j) {
			Node nodeInQuestion = invoiceRetChildNodes.item(j);
			switch (nodeInQuestion.getNodeName()) {
				case "TxnID":
					qbListID = nodeInQuestion.getTextContent();
					break;
				case "RefNumber":
					invoiceID = nodeInQuestion.getTextContent();
					break;
				default:
					break;
			}
			if (!qbListID.isEmpty() && !invoiceID.isEmpty()) {
				updateInvoice(qbListID, invoiceID);
				return;
			}
		}
		addActionError("Did not complete processing of invoice '"+invoiceID+"' qbListID '"+qbListID+"'");
		return;
	}

	private void updateInvoice(String qbListID, String invoiceID) {
		addActionMessage("Executing: UPDATE invoice SET qbListID = '"+qbListID+"',qbSync = 0 where id = "+invoiceID+";");
		Invoice invoice = invoiceDAO.find(Integer.parseInt(invoiceID));
		invoice.setQbSync(false);
		invoice.setQbListID(qbListID);
		invoiceDAO.save(invoice);
	}


	public String getQbresponsexml() {
		return qbresponsexml;
	}

	public void setQbresponsexml(String qbresponsexml) {
		this.qbresponsexml = qbresponsexml;
	}
}
