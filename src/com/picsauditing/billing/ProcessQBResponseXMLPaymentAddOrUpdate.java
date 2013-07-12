package com.picsauditing.billing;

import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.dao.PicsDAO;
import com.picsauditing.util.SpringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: PSchlesinger
 * Date: 7/8/13
 * Time: 10:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessQBResponseXMLPaymentAddOrUpdate extends ProcessQBResponseXMLStrategy  {

	public static final String PARENT_NODE_NAME_ADD_RESULT = "ReceivePaymentAddRs";
	public static final String DETAIL_NODE_NAME = "ReceivePaymentRet";
	public static final String REQUEST_TYPE = "Payment List ID Update";

	public static final String DAO_TYPE = "PaymentDAO";
	public static final String TABLE_NAME = "invoice";
	public static final String QBXML_LISTID_NODE_NAME = "TxnID";
	public static final String QBXML_TABLEPK_NODE_NAME = "Memo";

	public ProcessQBResponseXMLPaymentAddOrUpdate(StringBuilder actionMessages, StringBuilder errorMessages) {
		super(actionMessages,errorMessages);
	}

	public static ProcessQBResponseXMLPaymentAddOrUpdate factory (StringBuilder actionMessages, StringBuilder errorMessages,PicsDAO dao) {
		ProcessQBResponseXMLPaymentAddOrUpdate object = new ProcessQBResponseXMLPaymentAddOrUpdate(actionMessages, errorMessages);
		object.setParentNodeNameAddResult(PARENT_NODE_NAME_ADD_RESULT);
		object.setDetailNodeName(DETAIL_NODE_NAME);
		object.setRequestType(REQUEST_TYPE);
		object.setDao(dao != null ? dao :(PaymentDAO)SpringUtils.getBean(DAO_TYPE));
		object.setTableName(TABLE_NAME);
		object.setQbXMLListIDNodeName(QBXML_LISTID_NODE_NAME);
		object.setQbXMLTablePKNodeName(QBXML_TABLEPK_NODE_NAME);

		return object;

	}

}
