package com.intuit.developer.adaptors;

import java.util.List;
import java.util.Map;

import com.intuit.developer.QBSession;
import com.picsauditing.jpa.entities.Currency;
import com.picsauditing.jpa.entities.Invoice;

public class GetInvoicesForUpdate extends InvoiceAdaptor {

	@Override
	public String getQbXml(QBSession currentSession) throws Exception {
		Currency currency = Currency.getFromISO(currentSession.getCountry());

		String where = "i.account.qbListID is not null AND i.qbListID is not null " +
				" AND i.qbListID not like 'NOLOAD%' AND i.qbSync = true AND i.currency = '"+ currency + "'";
		List<Invoice> invoices = getInvoiceDao().findWhere(where, 10);

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
