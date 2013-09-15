package com.intuit.developer.adaptors;

import com.intuit.developer.QBSession;
import com.picsauditing.jpa.entities.Invoice;

import java.util.List;
import java.util.Map;

public class GetInvoicesForUpdate extends InvoiceAdaptor {

	public static String getWhereClause(String qbID, String currency) {
		return "i.account."
				+ qbID
				+ " IS NOT NULL AND i.qbListID IS NOT NULL"
				+ " AND i.account."
				+ qbID
				+ " NOT LIKE 'NOLOAD%' AND i.qbListID NOT LIKE 'NOLOAD%' AND i.account.status != 'Demo' AND i.qbSync = true AND i.currency LIKE '"
				+ currency + "'";
	}

	@Override
	public String getQbXml(QBSession currentSession) throws Exception {

		List<Invoice> invoices = getInvoiceDao().findWhere(
				getWhereClause(currentSession.getQbID(), currentSession.getCurrencyCode()), 10);

		if (invoices.size() > 0) {
			currentSession.getPossibleInvoiceUpdates().addAll(invoices);
			return getTheseInvoices(invoices);
		}

		return super.getQbXml(currentSession);
	}

	@Override
	public Object parseQbXml(QBSession currentSession, String qbXml) throws Exception {

		Map<String, Map<String, Object>> parsedResponses = parseInvoiceQueryResponse(qbXml);

		currentSession.getToUpdate().clear();

		for (String listId : parsedResponses.keySet()) {
			Map<String, Object> dataForThisListId = parsedResponses.get(listId);

			Invoice invoice = (Invoice) dataForThisListId.get("invoice");

			if (invoice != null && invoice.getId() != 0 && currentSession.getPossibleInvoiceUpdates().contains(invoice)) {
				currentSession.getToUpdate().put(listId, dataForThisListId);
			}
		}

		return null;
	}
}