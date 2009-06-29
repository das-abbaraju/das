package com.intuit.developer.adaptors;

import java.util.List;
import java.util.Map;

import com.intuit.developer.QBSession;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.Payment;

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

		Map<String, Map<String, Object>> parsedResponses = parsePaymentQueryResponse(qbXml, currentSession
				.getCurrentBatch());

		currentSession.getToUpdate().clear();

		for (String listId : parsedResponses.keySet()) {
			Map<String, Object> dataForThisListId = parsedResponses.get(listId);

			Invoice invoice = (Invoice) dataForThisListId.get("invoice");

			if (invoice != null && invoice.getId() != 0 && currentSession.getPossiblePaymentUpdates().contains(invoice)) {
				currentSession.getToUpdate().put(listId, dataForThisListId);
			}
		}

		currentSession.getPossiblePaymentUpdates().clear();

		return null;
	}

}
