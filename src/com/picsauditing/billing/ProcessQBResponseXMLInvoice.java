package com.picsauditing.billing;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ProcessQBResponseXMLInvoice extends ProcessQBResponseXMLStrategy  {

	public static final String DETAIL_NODE_NAME = "InvoiceRet";
	public static final String REQUEST_TYPE = "Invoice Add";

	@Override
	public void processDetailNode(Node detailNode, StringBuilder actionMessages, StringBuilder errorMessages) {
		NodeList invoiceRetChildNodes = detailNode.getChildNodes();
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
				updateInvoice(qbListID, invoiceID,actionMessages, errorMessages);
				return;
			}
		}
		errorMessages.append("Did not complete processing of invoice '"+invoiceID+"' qbListID '"+qbListID+"'; ");
		return;
	}

}
