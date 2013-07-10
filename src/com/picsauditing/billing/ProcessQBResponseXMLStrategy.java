package com.picsauditing.billing;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.w3c.dom.*;


/**
 * Created with IntelliJ IDEA.
 * User: PSchlesinger
 * Date: 7/8/13
 * Time: 10:04 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ProcessQBResponseXMLStrategy extends PicsActionSupport {

	@Autowired
	private ContractorAccountDAO contractorAccountDAO;
	@Qualifier("InvoiceDAO")
	@Autowired
	private InvoiceDAO invoiceDAO;

	public abstract void processDetailNode(Node detailNode);

	public void processParentNode(Node parentNode, String detailNodeName, String requestType) {
		if (isStatusMessageOk(parentNode)) {
			NodeList childNodes = parentNode.getChildNodes();
			int numChildNodes = childNodes.getLength();
			for (int i = 0; i < numChildNodes; ++i) {
				Node node = childNodes.item(i);
				if (node.getNodeName().equals(detailNodeName)) {
					processDetailNode(node);
				}
			}
		} else {
			addActionError(requestType+" Request failed -- requestID "+ getNodeRequestIDAttribute(parentNode));
			return;
		}
	}

	protected void updateInvoice(String qbListID, String invoiceID) {
		addActionMessage("Executing: UPDATE invoice SET qbListID = '"+qbListID+"',qbSync = 0 where id = "+invoiceID+";");
		Invoice invoice = invoiceDAO.find(Integer.parseInt(invoiceID));
		invoice.setQbSync(false);
		invoice.setQbListID(qbListID);
		invoiceDAO.save(invoice);
	}

	protected void updateContractor(String qbListID, String contractorID) {
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


	protected String getNodeRequestIDAttribute(Node parentNode) {
		NamedNodeMap attributes = parentNode.getAttributes();
		Node statusMessage= attributes.getNamedItem("statusMessage");
		return attributes.getNamedItem("requestID").getNodeValue();
	}

	protected boolean isStatusMessageOk(Node parentNode) {
		NamedNodeMap attributes = parentNode.getAttributes();
		Node statusMessage= attributes.getNamedItem("statusMessage");
		return statusMessage.getNodeValue().equals("Status OK");
	}
}
