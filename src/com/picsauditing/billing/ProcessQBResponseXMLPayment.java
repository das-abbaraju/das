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
public class ProcessQBResponseXMLPayment extends ProcessQBResponseXMLStrategy  {

	public static final String DETAIL_NODE_NAME = "ReceivePaymentRet";
	public static final String REQUEST_TYPE = "Payment Add";


	@Override
	public void processDetailNode(Node detailNode,StringBuilder actionMessages, StringBuilder errorMessages) {
		NodeList paymentRetChildNodes = detailNode.getChildNodes();
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
				updateInvoice(qbListID, paymentID,actionMessages,errorMessages);
				return;
			}
		}
		errorMessages.append("Did not complete processing of payment '"+paymentID+"' qbListID '"+qbListID+"'; ");
		return;
	}
}
