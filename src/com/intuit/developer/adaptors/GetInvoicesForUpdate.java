package com.intuit.developer.adaptors;

import java.util.List;
import java.util.Map;

import com.intuit.developer.QBSession;
import com.picsauditing.jpa.entities.Invoice;


public class GetInvoicesForUpdate extends InvoiceAdaptor {
	
	
	@Override
	public String getQbXml(QBSession currentSession) throws Exception {
		
		//List<ContractorAccount> contractors = getContractorDao().findWhere("i.qbListID is not null and a.qbSync = true");
		List<Invoice> invoices = getInvoiceDao().findWhere("i.qbListID is not null and i.qbSync = true");
	
		if( invoices.size() > 0 ) {
			currentSession.getPossibleInvoiceUpdates().addAll(invoices);
			return getTheseInvoices(invoices);
		}
		
		return super.getQbXml(currentSession);
	}
	
	
	@Override
	public Object parseQbXml(QBSession currentSession, String qbXml)
			throws Exception {
		
		Map<String, Map<String, Object>> parsedResponses = parseInvoiceQueryResponse(qbXml);

		currentSession.getToUpdate().clear();
		
		for( String listId : parsedResponses.keySet() ) {
			Map<String, Object> dataForThisListId = parsedResponses.get(listId);
			
			Invoice invoice = ( Invoice ) dataForThisListId.get("invoice");
			
			if( invoice != null && invoice.getId() != 0 && currentSession.getPossibleInvoiceUpdates().contains(invoice)) {
				currentSession.getToUpdate().put(listId, dataForThisListId);
			}
		}
		
		
		return null;
	}
}
