package com.picsauditing.billing;

import com.picsauditing.service.XmlService;
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
			errorMessages.append(requestType + " Request failed -- requestID " + getNodeRequestIDAttribute(parentNode)+"<br/>");
			return;
		}
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
			errorMessages.append("Didn't find '" + seeking + "' as root element<br/>");
			return null;
		}

		actionMessages.append("Root element of the doc is '" + seeking + "'<br/>");

		seeking = "QBXMLMsgsRs";
		NodeList childNodes = rootElement.getChildNodes();
		StringBuilder nodesFound = new StringBuilder();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			Node childNode = childNodes.item(i);

			if (childNode.getNodeName().equals(seeking)) {
				actionMessages.append("Processing '" + seeking + "' child node<br/>");
				return childNode.getChildNodes();
			} else if (!childNode.getNodeName().equals("#text")){
				nodesFound.append(childNode.getNodeName()+"; ");
			}

		}
		errorMessages.append("Didn't find '" + seeking + "' node -- instead found '"+nodesFound+"'<br/>");

		return null;
	}

	public static void processQBXMLMsgsRs(NodeList nodeList, StringBuilder actionMessages, StringBuilder errorMessages) {

		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);

			if (node.getNodeName().equals("#text")) {
				continue;
			}

			actionMessages.append("Processing node of type '" + node.getNodeName() + "'<br/>");

			ProcessQBResponseXMLStrategy processor = null;

			switch (node.getNodeName()) {
				case "InvoiceAddRs":
					processor = new ProcessQBResponseXMLInvoiceAdd();
					processor.processParentNode(node, ProcessQBResponseXMLInvoiceAdd.DETAIL_NODE_NAME, ProcessQBResponseXMLInvoiceAdd.REQUEST_TYPE, actionMessages, errorMessages);
					break;
				case "CustomerAddRs":
					processor = new ProcessQBResponseXMLCustomerAdd();
					processor.processParentNode(node, ProcessQBResponseXMLCustomerAdd.DETAIL_NODE_NAME, ProcessQBResponseXMLCustomerAdd.REQUEST_TYPE, actionMessages, errorMessages);
					break;
				case "ReceivePaymentAddRs":
					processor = new ProcessQBResponseXMLPaymentAdd();
					processor.processParentNode(node, ProcessQBResponseXMLPaymentAdd.DETAIL_NODE_NAME, ProcessQBResponseXMLPaymentAdd.REQUEST_TYPE, actionMessages, errorMessages);
					break;
				default:
					errorMessages.append("Need code to process node of type '" + node.getNodeName() + "'<br/>");
					break;
			}
			continue;

		}
	}


}
