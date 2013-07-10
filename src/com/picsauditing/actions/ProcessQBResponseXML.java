package com.picsauditing.actions;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.billing.ProcessQBResponseXMLCustomer;
import com.picsauditing.billing.ProcessQBResponseXMLInvoice;
import com.picsauditing.billing.ProcessQBResponseXMLPayment;
import com.picsauditing.billing.ProcessQBResponseXMLStrategy;
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

			ProcessQBResponseXMLStrategy processor = null;

			switch (node.getNodeName()) {
				case "InvoiceAddRs":
					processor = new ProcessQBResponseXMLInvoice();
					processor.processParentNode(node,ProcessQBResponseXMLInvoice.DETAIL_NODE_NAME,ProcessQBResponseXMLInvoice.REQUEST_TYPE);
					break;
				case "CustomerAddRs":
					processor = new ProcessQBResponseXMLCustomer();
					processor.processParentNode(node,ProcessQBResponseXMLCustomer.DETAIL_NODE_NAME,ProcessQBResponseXMLCustomer.REQUEST_TYPE);
					break;
				case "ReceivePaymentAddRs":
					processor = new ProcessQBResponseXMLPayment();
					processor.processParentNode(node,ProcessQBResponseXMLPayment.DETAIL_NODE_NAME,ProcessQBResponseXMLPayment.REQUEST_TYPE);
				default:
					addActionError("Need code to process node of type '"+node.getNodeName()+"'");
					break;
			}
			continue;

		}
	}

	public String getQbresponsexml() {
		return qbresponsexml;
	}

	public void setQbresponsexml(String qbresponsexml) {
		this.qbresponsexml = qbresponsexml;
	}
}
