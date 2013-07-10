package com.picsauditing.billing;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created with IntelliJ IDEA.
 * User: PSchlesinger
 * Date: 7/8/13
 * Time: 10:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessQBResponseXMLInvoice extends ProcessQBResponseXMLStrategy  {

	public static final String DETAIL_NODE_NAME = "InvoiceRet";
	public static final String REQUEST_TYPE = "Invoice Add";

	@Override
	public void processDetailNode(Node detailNode) {
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
				updateInvoice(qbListID, invoiceID);
				return;
			}
		}
		addActionError("Did not complete processing of invoice '"+invoiceID+"' qbListID '"+qbListID+"'");
		return;
	}

}
