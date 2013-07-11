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
public class ProcessQBResponseXMLCustomer extends ProcessQBResponseXMLStrategy  {

	public static final String DETAIL_NODE_NAME = "CustomerRet";
	public static final String REQUEST_TYPE = "Customer Add";


	@Override
	public void processDetailNode(Node detailNode,StringBuilder actionMessages, StringBuilder errorMessages) {
		NodeList customerRetChildNodes = detailNode.getChildNodes();
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
				updateContractor(qbListID, contractorID,actionMessages,errorMessages);
				return;
			}
		}
		errorMessages.append("Did not complete processing of contractor '"+contractorID+"' qbListID '"+qbListID+"'; ");
		return;
	}
}
