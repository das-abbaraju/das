package com.picsauditing.billing;

import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.PicsDAO;
import com.picsauditing.util.SpringUtils;

public class ProcessQBResponseXMLInvoiceAddOrUpdate extends ProcessQBResponseXMLStrategy  {

	public static final String PARENT_NODE_NAME_ADD_RESULT = "InvoiceAddRs";
	public static final String PARENT_NODE_NAME_QUERY_RESULT = "InvoiceQueryRs";
	public static final String DETAIL_NODE_NAME = "InvoiceRet";
	public static final String REQUEST_TYPE = "Invoice Add";

	public static final String DAO_TYPE = "InvoiceDAO";
	public static final String TABLE_NAME = "invoice";
	public static final String QBXML_LISTID_NODE_NAME = "TxnID";
	public static final String QBXML_TABLEPK_NODE_NAME = "RefNumber";

	public ProcessQBResponseXMLInvoiceAddOrUpdate(StringBuilder actionMessages, StringBuilder errorMessages) {
		super(actionMessages,errorMessages);
	}

	public static ProcessQBResponseXMLInvoiceAddOrUpdate factory (StringBuilder actionMessages, StringBuilder errorMessages,PicsDAO dao) {
		ProcessQBResponseXMLInvoiceAddOrUpdate object = new ProcessQBResponseXMLInvoiceAddOrUpdate(actionMessages, errorMessages);
		object.setParentNodeNameAddResult(PARENT_NODE_NAME_ADD_RESULT);
		object.setParentNodeNameQueryResult(PARENT_NODE_NAME_QUERY_RESULT);
		object.setDetailNodeName(DETAIL_NODE_NAME);
		object.setRequestType(REQUEST_TYPE);
		object.setDao(dao != null ? dao :(InvoiceDAO)SpringUtils.getBean(DAO_TYPE));
		object.setTableName(TABLE_NAME);
		object.setQbXMLListIDNodeName(QBXML_LISTID_NODE_NAME);
		object.setQbXMLTablePKNodeName(QBXML_TABLEPK_NODE_NAME);

		return object;

	}
}
