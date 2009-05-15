package com.intuit.developer.adaptors;

import java.util.HashMap;
import java.util.Map;

import com.intuit.developer.QBSession;
import com.picsauditing.jpa.entities.Invoice;

public class GetPaymentsForUpdate extends PaymentAdaptor {

	@Override
	public String getQbXml(QBSession currentSession) throws Exception {

		if (currentSession.getPossiblePaymentUpdates().size() > 0) {

			currentSession.setCurrentBatch(new HashMap<String, String>());

			String thesePayments = getThesePayments(currentSession.getPossiblePaymentUpdates());

			for (Invoice invoice : currentSession.getPossiblePaymentUpdates()) {
				currentSession.getCurrentBatch().put(invoice.getQbPaymentListID(),
						new Integer(invoice.getId()).toString());
			}

			return thesePayments;
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
