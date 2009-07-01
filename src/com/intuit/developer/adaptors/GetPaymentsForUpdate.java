package com.intuit.developer.adaptors;

import java.util.List;
import java.util.Map;

import com.intuit.developer.QBSession;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentRet;

public class GetPaymentsForUpdate extends PaymentAdaptor {

	@Override
	public String getQbXml(QBSession currentSession) throws Exception {

		String where = "p.account.qbListID is not null AND p.qbListID is not null AND p.qbListID not like 'NOLOAD%' AND p.qbSync = true";
		List<Payment> payments = getPaymentDao().findWhere(where, 10);

		if (payments.size() > 0) {
			currentSession.getPossiblePaymentUpdates().addAll(payments);

			return getThesePayments(payments);
		}

		return super.getQbXml(currentSession);
	}

	@Override
	public Object parseQbXml(QBSession currentSession, String qbXml) throws Exception {

		Map<String, ReceivePaymentRet> parsedResponses = parsePaymentQueryResponse(qbXml);

		currentSession.getToUpdatePayment().clear();

		for (String listId : parsedResponses.keySet()) {
			ReceivePaymentRet qbPayment = parsedResponses.get(listId);
			currentSession.getToUpdatePayment().put(listId, qbPayment);
		}
		
		// It's possible that we may have a Payment that was deleted in QB but not in PICS
		// We should notify someone so we can remove it from PICS or insert it in QB again
		if(currentSession.getPossiblePaymentUpdates().size() != parsedResponses.size()) {
			StringBuffer paymentIDs = new StringBuffer();
			for(Payment payment : currentSession.getPossiblePaymentUpdates())
				paymentIDs.append(payment.getId()).append(",");
			
			currentSession.getErrors().add("A PICS Payment was deleted from QuickBooks. Check these Payment IDS: " + paymentIDs.toString());
		}
		
		currentSession.getPossiblePaymentUpdates().clear();

		return null;
	}

}
