package com.picsauditing.billing;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.util.SpringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created with IntelliJ IDEA.
 * User: PSchlesinger
 * Date: 7/8/13
 * Time: 10:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessQBResponseXMLCustomerAddOrUpdate extends ProcessQBResponseXMLStrategy  {
	public static final String PARENT_NODE_NAME_ADD_RESULT = "CustomerAddRs";
	public static final String PARENT_NODE_NAME_QUERY_RESULT = "CustomerQueryRs";
	public static final String DETAIL_NODE_NAME = "CustomerRet";
	public static final String REQUEST_TYPE = "Customer Add";

	private ContractorAccountDAO contractorAccountDAO = null;

	public ProcessQBResponseXMLCustomerAddOrUpdate() {
		setPARENT_NODE_NAME_ADD_RESULT(PARENT_NODE_NAME_ADD_RESULT);
		setPARENT_NODE_NAME_QUERY_RESULT(PARENT_NODE_NAME_QUERY_RESULT);
		setDETAIL_NODE_NAME(DETAIL_NODE_NAME);
		setREQUEST_TYPE(REQUEST_TYPE);
	}

	public void setContractorAccountDAO(ContractorAccountDAO contractorAccountDAO) {
		this.contractorAccountDAO = contractorAccountDAO;
	}

	protected void updateContractor(String qbListID, String contractorID, StringBuilder actionMessages, StringBuilder errorMessages, Boolean setQbSyncToFalse) {
		if (contractorAccountDAO == null) {
			contractorAccountDAO = SpringUtils.getBean("ContractorAccountDAO");
		}

		String contractorIDNumsOnly = contractorID.replaceAll("[^0-9]","");
		ContractorAccount contractor = null;
		try {
			contractor = contractorAccountDAO.find(Integer.parseInt(contractorIDNumsOnly));
			if (contractor == null) {
				errorMessages.append("Contractor ID '" + contractorID + "' not found<br/>");
				return;
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stacktrace = sw.toString();

			errorMessages.append("Contractor ID '" + contractorID + "' not found; exception: "+e.getMessage()+" "+stacktrace+"<br/>");
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
		actionMessages.append("Executing: UPDATE accounts SET " + qbListIDColumnName + " = '" + qbListID + "'");
		if (setQbSyncToFalse) {
			contractor.setQbSync(false);
			actionMessages.append(", qbSync = 0 ");
		}
		actionMessages.append("where id = " + contractorID + ";<br/>");
		contractorAccountDAO.save(contractor);
	}


	@Override
	public void processDetailNode(Node detailNode, StringBuilder actionMessages, StringBuilder errorMessages, String parentNodeName) {
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
				Boolean isAddNotJustQuerySoSetQbSyncToFalse = isAddNotJustQuerySoSetQbSyncToFalse(parentNodeName);
				updateContractor(qbListID, contractorID,actionMessages,errorMessages,isAddNotJustQuerySoSetQbSyncToFalse);
				return;
			}
		}
		errorMessages.append("Did not complete processing of contractor '"+contractorID+"' qbListID '"+qbListID+"'<br/>");
		return;
	}
}
