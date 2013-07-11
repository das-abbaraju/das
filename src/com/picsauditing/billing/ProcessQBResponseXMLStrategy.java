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

	private String PARENT_NODE_NAME_ADD_RESULT;
	private String PARENT_NODE_NAME_QUERY_RESULT;
	private String DETAIL_NODE_NAME;
	private String REQUEST_TYPE;

	public abstract void processDetailNode(Node detailNode, StringBuilder actionMessages, StringBuilder errorMessagses, String nodeName);

	protected void processParentNode(Node parentNode, String detailNodeName, String requestType, StringBuilder actionMessages, StringBuilder errorMessages) {
		if (isStatusMessageOk(parentNode)) {
			NodeList childNodes = parentNode.getChildNodes();
			int numChildNodes = childNodes.getLength();
			for (int i = 0; i < numChildNodes; ++i) {
				Node node = childNodes.item(i);
				if (node.getNodeName().equals(detailNodeName)) {
					processDetailNode(node, actionMessages, errorMessages,parentNode.getNodeName());
				}
			}
		} else {
			errorMessages.append(requestType + " Request failed -- requestID " + getNodeRequestIDAttribute(parentNode)+"<br/>");
			return;
		}
	}


	protected static String getNodeRequestIDAttribute(Node parentNode) {
		NamedNodeMap attributes = parentNode.getAttributes();
		return attributes.getNamedItem("requestID").getNodeValue();
	}

	protected static boolean isStatusMessageOk(Node parentNode) {
		NamedNodeMap attributes = parentNode.getAttributes();
		return attributes.getNamedItem("statusMessage").getNodeValue().equals("Status OK");
	}

	protected Boolean isAddNotJustQuerySoSetQbSyncToFalse(String parentNodeName) {
		return (parentNodeName.equals(getPARENT_NODE_NAME_ADD_RESULT()) ? true : false);
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
				case ProcessQBResponseXMLInvoiceAddOrUpdate.PARENT_NODE_NAME_ADD_RESULT:
				case ProcessQBResponseXMLInvoiceAddOrUpdate.PARENT_NODE_NAME_QUERY_RESULT:
					processor = new ProcessQBResponseXMLInvoiceAddOrUpdate();
					processor.processParentNode(node, ProcessQBResponseXMLInvoiceAddOrUpdate.DETAIL_NODE_NAME, ProcessQBResponseXMLInvoiceAddOrUpdate.REQUEST_TYPE, actionMessages, errorMessages);
					break;
				case ProcessQBResponseXMLCustomerAddOrUpdate.PARENT_NODE_NAME_ADD_RESULT:
				case ProcessQBResponseXMLCustomerAddOrUpdate.PARENT_NODE_NAME_QUERY_RESULT:
					processor = new ProcessQBResponseXMLCustomerAddOrUpdate();
					processor.processParentNode(node, ProcessQBResponseXMLCustomerAddOrUpdate.DETAIL_NODE_NAME, ProcessQBResponseXMLCustomerAddOrUpdate.REQUEST_TYPE, actionMessages, errorMessages);
					break;
				case ProcessQBResponseXMLPaymentAddOrUpdate.PARENT_NODE_NAME_ADD_RESULT:
					processor = new ProcessQBResponseXMLPaymentAddOrUpdate();
					processor.processParentNode(node, ProcessQBResponseXMLPaymentAddOrUpdate.DETAIL_NODE_NAME, ProcessQBResponseXMLPaymentAddOrUpdate.REQUEST_TYPE, actionMessages, errorMessages);
					break;
				default:
					errorMessages.append("Need code to process node of type '" + node.getNodeName() + "'<br/>");
					break;
			}
			continue;

		}
	}



	public String getPARENT_NODE_NAME_ADD_RESULT() {
		return PARENT_NODE_NAME_ADD_RESULT;
	}

	public void setPARENT_NODE_NAME_ADD_RESULT(String PARENT_NODE_NAME_ADD_RESULT) {
		this.PARENT_NODE_NAME_ADD_RESULT = PARENT_NODE_NAME_ADD_RESULT;
	}

	public String getPARENT_NODE_NAME_QUERY_RESULT() {
		return PARENT_NODE_NAME_QUERY_RESULT;
	}

	public void setPARENT_NODE_NAME_QUERY_RESULT(String PARENT_NODE_NAME_QUERY_RESULT) {
		this.PARENT_NODE_NAME_QUERY_RESULT = PARENT_NODE_NAME_QUERY_RESULT;
	}

	public String getDETAIL_NODE_NAME() {
		return DETAIL_NODE_NAME;
	}

	public void setDETAIL_NODE_NAME(String DETAIL_NODE_NAME) {
		this.DETAIL_NODE_NAME = DETAIL_NODE_NAME;
	}

	public String getREQUEST_TYPE() {
		return REQUEST_TYPE;
	}

	public void setREQUEST_TYPE(String REQUEST_TYPE) {
		this.REQUEST_TYPE = REQUEST_TYPE;
	}


}
