package com.picsauditing.billing;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.util.SpringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: PSchlesinger
 * Date: 7/8/13
 * Time: 10:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessQBResponseXMLCustomerAddOrUpdate extends ProcessQBResponseXMLStrategy  {
	public static final String PARENT_NODE_NAME_ADD_RESULT = "CustomerAddRs";
	public static final String PARENT_NODE_NAME_UPDATE_RESULT = "CustomerModRs";
	public static final String PARENT_NODE_NAME_QUERY_RESULT = "CustomerQueryRs";
	public static final String DETAIL_NODE_NAME = "CustomerRet";
	public static final String REQUEST_TYPE = "Customer Add";
	public static final String DAO_TYPE = "ContractorAccountDAO";
	public static final String TABLE_NAME = "accounts";
	public static final String QBXML_TABLEPK_NODE_NAME = "Name";
	public static final String QBXML_LISTID_NODE_NAME = "ListID";

	public ProcessQBResponseXMLCustomerAddOrUpdate(StringBuilder actionMessages, StringBuilder errorMessages) {
		super(actionMessages,errorMessages);
	}

	public static ProcessQBResponseXMLCustomerAddOrUpdate factory (StringBuilder actionMessages, StringBuilder errorMessages, ContractorAccountDAO dao) {
		ProcessQBResponseXMLCustomerAddOrUpdate object = new ProcessQBResponseXMLCustomerAddOrUpdate(actionMessages,errorMessages);
		object.setParentNodeNameAddResult(PARENT_NODE_NAME_ADD_RESULT);
		object.setParentNodeNameQueryResult(PARENT_NODE_NAME_QUERY_RESULT);
		object.setParentNodeNameUpdateResult(PARENT_NODE_NAME_UPDATE_RESULT);
		object.setDetailNodeName(DETAIL_NODE_NAME);
		object.setRequestType(REQUEST_TYPE);
		object.setContractorAccountDAO(dao != null ? dao :(ContractorAccountDAO)SpringUtils.getBean(DAO_TYPE));
		object.setTableName(TABLE_NAME);
		object.setQbXMLListIDNodeName(QBXML_LISTID_NODE_NAME);
		object.setQbXMLTablePKNodeName(QBXML_TABLEPK_NODE_NAME);

		return object;

	}

}