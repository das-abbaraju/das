package com.picsauditing.billing;

import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.util.SpringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ProcessQBResponseXMLInvoiceAddOrUpdate extends ProcessQBResponseXMLStrategy  {

	public static final String PARENT_NODE_NAME_ADD_RESULT = "InvoiceAddRs";
	public static final String PARENT_NODE_NAME_QUERY_RESULT = "InvoiceQueryRs";
	public static final String DETAIL_NODE_NAME = "InvoiceRet";
	public static final String REQUEST_TYPE = "Invoice Add";

	private InvoiceDAO invoiceDAO = null;

	public ProcessQBResponseXMLInvoiceAddOrUpdate() {
		setPARENT_NODE_NAME_ADD_RESULT(PARENT_NODE_NAME_ADD_RESULT);
		setPARENT_NODE_NAME_QUERY_RESULT(PARENT_NODE_NAME_QUERY_RESULT);
		setDETAIL_NODE_NAME(DETAIL_NODE_NAME);
		setREQUEST_TYPE(REQUEST_TYPE);
	}

	public void setInvoiceDAO(InvoiceDAO invoiceDAO) {
		this.invoiceDAO = invoiceDAO;
	}

	protected void updateInvoice(String qbListID, String invoiceID, StringBuilder actionMessages, StringBuilder errorMessages, Boolean setQBSyncToFalse) {
		if (invoiceDAO == null) {
			invoiceDAO = SpringUtils.getBean("InvoiceDAO");
		}
		Invoice invoice = null;
		try {
			invoice = invoiceDAO.find(Integer.parseInt(invoiceID));
			if (invoice == null) {
				errorMessages.append("Invoice ID '" + invoiceID + "' not found<br/>");
				return;
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stacktrace = sw.toString();

			errorMessages.append("Invoice ID '" + invoiceID + "' not found; exception: "+e.getMessage()+" "+stacktrace+"<br/>");
			return;
		}
		invoice.setQbListID(qbListID);
		actionMessages.append("Executing: UPDATE invoice SET qbListID = '" + qbListID + "'");

		if (setQBSyncToFalse) {
			invoice.setQbSync(false);
			actionMessages.append(", qbSync = 0");
		}
		actionMessages.append(" where id = " + invoiceID + ";<br/>");
		invoiceDAO.save(invoice);
	}



	@Override
	public void processDetailNode(Node detailNode, StringBuilder actionMessages, StringBuilder errorMessages, String parentNodeName) {
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
				Boolean isAddNotJustQuerySoSetQbSyncToFalse = isAddNotJustQuerySoSetQbSyncToFalse(parentNodeName);
				updateInvoice(qbListID, invoiceID,actionMessages, errorMessages, isAddNotJustQuerySoSetQbSyncToFalse);
				return;
			}
		}
		errorMessages.append("Did not complete processing of invoice '"+invoiceID+"' qbListID '"+qbListID+"'<br/>");
		return;
	}

}
