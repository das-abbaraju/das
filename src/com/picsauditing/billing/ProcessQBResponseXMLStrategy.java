package com.picsauditing.billing;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.service.XmlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created with IntelliJ IDEA.
 * User: PSchlesinger
 * Date: 7/8/13
 * Time: 10:04 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ProcessQBResponseXMLStrategy {

	@Autowired
	private ContractorAccountDAO contractorAccountDAO;

	public void setInvoiceDAO(InvoiceDAO invoiceDAO) {
		this.invoiceDAO = invoiceDAO;
	}

	public void setContractorAccountDAO(ContractorAccountDAO contractorAccountDAO) {
		this.contractorAccountDAO = contractorAccountDAO;
	}

	@Qualifier("InvoiceDAO")
	@Autowired
	private InvoiceDAO invoiceDAO;

	public abstract void processDetailNode(Node detailNode, StringBuilder actionMessages, StringBuilder errorMessagses);

	protected void processParentNode(Node parentNode, String detailNodeName, String requestType, StringBuilder actionMessages, StringBuilder errorMessages) {
		if (isStatusMessageOk(parentNode)) {
			NodeList childNodes = parentNode.getChildNodes();
			int numChildNodes = childNodes.getLength();
			for (int i = 0; i < numChildNodes; ++i) {
				Node node = childNodes.item(i);
				if (node.getNodeName().equals(detailNodeName)) {
					processDetailNode(node, actionMessages, errorMessages);
				}
			}
		} else {
			errorMessages.append(requestType + " Request failed -- requestID " + getNodeRequestIDAttribute(parentNode));
			return;
		}
	}

	protected void updateInvoice(String qbListID, String invoiceID, StringBuilder actionMessages, StringBuilder errorMessages) {
		Invoice invoice = null;
		try {
			invoice = invoiceDAO.find(Integer.parseInt(invoiceID));
			if (invoice == null) {
				errorMessages.append("Invoice ID '" + invoiceID + "' not found; ");
				return;
			}
		} catch (Exception e) {
			errorMessages.append("Invoice ID '" + invoiceID + "' not found; ");
			return;
		}
		invoice.setQbSync(false);
		invoice.setQbListID(qbListID);
		actionMessages.append("Executing: UPDATE invoice SET qbListID = '" + qbListID + "',qbSync = 0 where id = " + invoiceID + ";");
		invoiceDAO.save(invoice);
	}

	protected void updateContractor(String qbListID, String contractorID, StringBuilder actionMessages, StringBuilder errorMessages) {
		String contractorIDnumsOnly = contractorID.replaceAll("[^0-9]","");
		ContractorAccount contractor = null;
		try {
			contractor = contractorAccountDAO.find(Integer.parseInt(contractorIDnumsOnly));
			if (contractor == null) {
				errorMessages.append("Contractor ID '" + contractorID + "' not found; ");
				return;
			}
		} catch (Exception e) {
			errorMessages.append("Contractor ID '" + contractorID + "' not found; ");
			return;
		}

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
		actionMessages.append("Executing: UPDATE accounts SET " + qbListIDColumnName + " = '" + qbListID + "', qbSync = 0 where id = " + contractorID + ";");
		contractorAccountDAO.save(contractor);
	}


	protected static String getNodeRequestIDAttribute(Node parentNode) {
		NamedNodeMap attributes = parentNode.getAttributes();
		Node statusMessage = attributes.getNamedItem("statusMessage");
		return attributes.getNamedItem("requestID").getNodeValue();
	}

	protected static boolean isStatusMessageOk(Node parentNode) {
		NamedNodeMap attributes = parentNode.getAttributes();
		Node statusMessage = attributes.getNamedItem("statusMessage");
		return statusMessage.getNodeValue().equals("Status OK");
	}

	public static NodeList findQBXMLMsgsRsChildNodes(InputStream inputStream, StringBuilder actionMessages, StringBuilder errorMessages) throws IOException, SAXException, ParserConfigurationException {

		Element rootElement = XmlService.getRootElementFromInputStream(inputStream);

		String seeking = "QBXML";
		if (!(rootElement.getNodeName()).equals(seeking)) {
			errorMessages.append("Didn't find '" + seeking + "' as root element; ");
			return null;
		}

		actionMessages.append("Root element of the doc is '" + seeking + "'; ");

		seeking = "QBXMLMsgsRs";
		NodeList childNodes = rootElement.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			Node childNode = childNodes.item(i);

			if (childNode.getNodeName().equals(seeking)) {
				actionMessages.append("Processing '" + seeking + "' child node; ");
				return childNode.getChildNodes();
			}

		}
		errorMessages.append("Didn't find '" + seeking + "' node; ");
		return null;
	}

	public static void processQBXMLMsgsRs(NodeList nodeList, StringBuilder actionMessages, StringBuilder errorMessages) {

		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);

			if (node.getNodeName().equals("#text")) {
				continue;
			}

			actionMessages.append("Processing node of type '" + node.getNodeName() + "'; ");

			ProcessQBResponseXMLStrategy processor = null;

			switch (node.getNodeName()) {
				case "InvoiceAddRs":
					processor = new ProcessQBResponseXMLInvoice();
					processor.processParentNode(node, ProcessQBResponseXMLInvoice.DETAIL_NODE_NAME, ProcessQBResponseXMLInvoice.REQUEST_TYPE, actionMessages, errorMessages);
					break;
				case "CustomerAddRs":
					processor = new ProcessQBResponseXMLCustomer();
					processor.processParentNode(node, ProcessQBResponseXMLCustomer.DETAIL_NODE_NAME, ProcessQBResponseXMLCustomer.REQUEST_TYPE, actionMessages, errorMessages);
					break;
				case "ReceivePaymentAddRs":
					processor = new ProcessQBResponseXMLPayment();
					processor.processParentNode(node, ProcessQBResponseXMLPayment.DETAIL_NODE_NAME, ProcessQBResponseXMLPayment.REQUEST_TYPE, actionMessages, errorMessages);
					break;
				default:
					errorMessages.append("Need code to process node of type '" + node.getNodeName() + "'; ");
					break;
			}
			continue;

		}
	}
}
