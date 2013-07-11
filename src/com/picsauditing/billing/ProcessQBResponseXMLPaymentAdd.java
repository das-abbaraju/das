package com.picsauditing.billing;

import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.Payment;
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
public class ProcessQBResponseXMLPaymentAdd extends ProcessQBResponseXMLStrategy  {

	public static final String DETAIL_NODE_NAME = "ReceivePaymentRet";
	public static final String REQUEST_TYPE = "Payment Add";
	private PaymentDAO paymentDAO = null;

	public void setPaymentDAO(PaymentDAO paymentDAO) {
		this.paymentDAO = paymentDAO;
	}

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
				updatePayment(qbListID, paymentID, actionMessages, errorMessages);
				return;
			}
		}
		errorMessages.append("Did not complete processing of payment '"+paymentID+"' qbListID '"+qbListID+"'<br/>");
		return;
	}

	protected void updatePayment(String qbListID, String paymentID, StringBuilder actionMessages, StringBuilder errorMessages) {
		if (paymentDAO == null) {
			paymentDAO = SpringUtils.getBean("PaymentDAO");
		}
		Payment payment = null;
		try {
			payment = paymentDAO.find(Integer.parseInt(paymentID));
			if (payment == null) {
				errorMessages.append("Payment ID '" + paymentID + "' not found; ");
				return;
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stacktrace = sw.toString();

			errorMessages.append("Payment ID '" + paymentID + "' not found; exception: "+e.getMessage()+" "+stacktrace+"<br/>");
			return;
		}
		payment.setQbSync(false);
		payment.setQbListID(qbListID);
		actionMessages.append("Executing: UPDATE invoice SET qbListID = '" + qbListID + "',qbSync = 0 where id = " + paymentID + ";<br/>");
		paymentDAO.save(payment);
	}
}
