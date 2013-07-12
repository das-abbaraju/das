package com.picsauditing.billing;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.PicsDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Transaction;
import com.picsauditing.service.XmlService;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Created with IntelliJ IDEA.
 * User: PSchlesinger
 * Date: 7/8/13
 * Time: 10:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessQBResponseXMLStrategy {

	private String parentNodeNameAddResult;
	private String parentNodeNameQueryResult;
	private String detailNodeName;
	private String requestType;
	private PicsDAO dao;
	private String tableName;
	private String qbXMLListIDNodeName;
	private String qbXMLTablePKNodeName;
	private StringBuilder actionMessages;
	private StringBuilder errorMessages;

	private ContractorAccountDAO contractorAccountDAO = null;

	public ProcessQBResponseXMLStrategy(StringBuilder actionMessages, StringBuilder errorMessages) {
		this.actionMessages = actionMessages;
		this.errorMessages = errorMessages;
	}


	protected void processParentNode(Node parentNode) {

		if (isStatusMessageOk(parentNode)) {
			NodeList childNodes = parentNode.getChildNodes();
			int numChildNodes = childNodes.getLength();
			for (int i = 0; i < numChildNodes; ++i) {
				Node node = childNodes.item(i);
				if (node.getNodeName().equals(getDetailNodeName())) {
					processDetailNode(node, parentNode.getNodeName());
				}
			}
		} else {
			errorMessages.append(getRequestType() + " Request failed");
			if (isAddRequest(parentNode.getNodeName())) {
				errorMessages.append("requestID " + getNodeRequestIDAttribute(parentNode));
			}
			errorMessages.append("<br/>");
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

	protected Boolean isAddRequest(String parentNodeName) {
		return (parentNodeName.equals(getParentNodeNameAddResult()) ? true : false);
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
			PicsDAO nullDao = null;
			ContractorAccountDAO caDao = null;

			switch (node.getNodeName()) {
				case ProcessQBResponseXMLInvoiceAddOrUpdate.PARENT_NODE_NAME_ADD_RESULT:
				case ProcessQBResponseXMLInvoiceAddOrUpdate.PARENT_NODE_NAME_QUERY_RESULT:
					processor = ProcessQBResponseXMLInvoiceAddOrUpdate.factory(actionMessages,errorMessages,nullDao);
					break;
				case ProcessQBResponseXMLCustomerAddOrUpdate.PARENT_NODE_NAME_ADD_RESULT:
				case ProcessQBResponseXMLCustomerAddOrUpdate.PARENT_NODE_NAME_QUERY_RESULT:
					processor = ProcessQBResponseXMLCustomerAddOrUpdate.factory(actionMessages,errorMessages,caDao);
					break;
				case ProcessQBResponseXMLPaymentAddOrUpdate.PARENT_NODE_NAME_ADD_RESULT:
				case ProcessQBResponseXMLPaymentAddOrUpdate.PARENT_NODE_NAME_QUERY_RESULT:
					processor = ProcessQBResponseXMLPaymentAddOrUpdate.factory(actionMessages,errorMessages,nullDao);
					break;
				default:
					break;
			}
			if (processor != null) {
				processor.processParentNode(node);
			}  else {
				errorMessages.append("Need code to process node of type '" + node.getNodeName() + "'<br/>");
			}
			continue;

		}
	}

	public void processDetailNode(Node detailNode, String parentNodeName) {
		NodeList childNodes = detailNode.getChildNodes();
		String qbListID = "";
		String tablePK = "";
		for (int j = 0; j < childNodes.getLength(); ++j) {
			Node nodeInQuestion = childNodes.item(j);

			if (nodeInQuestion.getNodeName().equals(getQbXMLListIDNodeName())) {
				qbListID = nodeInQuestion.getTextContent();
			} else if (nodeInQuestion.getNodeName().equals(getQbXMLTablePKNodeName())) {
				tablePK = nodeInQuestion.getTextContent().replaceAll("[^0-9]", "");
			}
			if (!qbListID.isEmpty() && !tablePK.isEmpty()) {
				boolean isAddNotJustQuerySoSetQbSyncToFalse = isAddRequest(parentNodeName);
				updateDatabaseTable(qbListID, tablePK, isAddNotJustQuerySoSetQbSyncToFalse);
				return;
			}
		}
		errorMessages.append("Did not complete processing of "+getTableName()+" '"+tablePK+"' qbListID '"+qbListID+"'<br/>");
		return;
	}

	protected void updateDatabaseTable(String qbListID, String tablePK, boolean setQBSyncToFalse) {
		switch (getTableName()) {
			case ProcessQBResponseXMLCustomerAddOrUpdate.TABLE_NAME:
				updateDatabaseTableContractor(qbListID, tablePK, setQBSyncToFalse);
				break;
			case ProcessQBResponseXMLInvoiceAddOrUpdate.TABLE_NAME:
			// case ProcessQBResponseXMLPaymentAddOrUpdate.TABLE_NAME: // same as invoice right now
				updateDatabaseTableTransaction(qbListID, tablePK, setQBSyncToFalse);
				break;
			default:
				break;
		}

	}

	private void updateDatabaseTableContractor(String qbListID, String tablePK, boolean setQbSyncToFalse) {

		int contractorIDNumsOnly = Integer.parseInt(tablePK.replaceAll("[^0-9]",""));
		ContractorAccount contractor = null;
		try {
			contractor = contractorAccountDAO.find(contractorIDNumsOnly);
			if (contractor == null) {
				errorMessages.append(StringUtils.capitalize(getTableName())+" ID '" + tablePK + "' not found<br/>");
				return;
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stacktrace = sw.toString();

			errorMessages.append(StringUtils.capitalize(getTableName())+" ID '" + tablePK + "' not found; exception: "+e.getMessage()+" "+stacktrace+"<br/>");
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
			actionMessages.append(", qbSync = 0");
		}
		actionMessages.append(" where id = " + tablePK + ";<br/>");
		contractorAccountDAO.save(contractor);
	}

	private void updateDatabaseTableTransaction(String qbListID, String tablePK, boolean setQBSyncToFalse) {
		Transaction transactionObject = null;
		try {
			transactionObject = getDao().find(Transaction.class,Integer.parseInt(tablePK));
			if (transactionObject == null) {
				errorMessages.append(StringUtils.capitalize(getTableName())+" ID '" + tablePK + "' not found<br/>");
				return;
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stacktrace = sw.toString();

			errorMessages.append(StringUtils.capitalize(getTableName())+" ID '" + tablePK + "' not found; exception: "+e.getMessage()+" "+stacktrace+"<br/>");
			return;
		}
		transactionObject.setQbListID(qbListID);
		actionMessages.append("Executing: UPDATE "+getTableName()+" SET qbListID = '" + qbListID + "'");

		if (setQBSyncToFalse) {
			transactionObject.setQbSync(false);
			actionMessages.append(", qbSync = 0");
		}
		actionMessages.append(" where id = " + tablePK + ";<br/>");
		getDao().save(transactionObject);
	}

	public String getParentNodeNameAddResult() {
		return parentNodeNameAddResult;
	}

	public void setParentNodeNameAddResult(String parentNodeNameAddResult) {
		this.parentNodeNameAddResult = parentNodeNameAddResult;
	}

	public String getParentNodeNameQueryResult() {
		return parentNodeNameQueryResult;
	}

	public void setParentNodeNameQueryResult(String parentNodeNameQueryResult) {
		this.parentNodeNameQueryResult = parentNodeNameQueryResult;
	}

	public String getDetailNodeName() {
		return detailNodeName;
	}

	public void setDetailNodeName(String detailNodeName) {
		this.detailNodeName = detailNodeName;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public StringBuilder getActionMessages() {
		return actionMessages;
	}

	public void setActionMessages(StringBuilder actionMessages) {
		this.actionMessages = actionMessages;
	}

	public StringBuilder getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(StringBuilder errorMessages) {
		this.errorMessages = errorMessages;
	}

	public PicsDAO getDao() {
		return dao;
	}

	public void setDao(PicsDAO dao) {
		this.dao = dao;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getQbXMLListIDNodeName() {
		return qbXMLListIDNodeName;
	}

	public void setQbXMLListIDNodeName(String qbXMLListIDNodeName) {
		this.qbXMLListIDNodeName = qbXMLListIDNodeName;
	}

	public String getQbXMLTablePKNodeName() {
		return qbXMLTablePKNodeName;
	}

	public void setQbXMLTablePKNodeName(String qbXMLTablePKNodeName) {
		this.qbXMLTablePKNodeName = qbXMLTablePKNodeName;
	}

	public void setContractorAccountDAO(ContractorAccountDAO contractorAccountDAO) {
		this.contractorAccountDAO = contractorAccountDAO;
	}

	public ContractorAccountDAO getContractorAccountDAO() {
		return contractorAccountDAO;
	}
}
