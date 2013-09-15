package com.intuit.developer.adaptors;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.intuit.developer.QBSession;
import com.picsauditing.jpa.entities.ContractorAccount;

public class GetContractorsForUpdate extends CustomerAdaptor {

	public static String getWhereClause(String qbID, String currency) {
		return "a." + qbID + " IS NOT NULL and a." + qbID
				+ " NOT LIKE 'NOLOAD%' AND a.qbSync = true AND a.status != 'Demo' AND a.country.currency = '"
				+ currency + "'";
	}

	@Override
	public String getQbXml(QBSession currentSession) throws Exception {

		List<ContractorAccount> contractors = getContractorDao().findWhere(
				getWhereClause(currentSession.getQbID(), currentSession.getCurrencyCode()));

		if (contractors.size() > 0) {
			currentSession.getPossibleUpdates().addAll(contractors);
			return getTheseContractors(currentSession, contractors);
		}

		return super.getQbXml(currentSession);
	}

	@Override
	public Object parseQbXml(QBSession currentSession, String qbXml) throws Exception {
		Map<String, Map<String, Object>> parsedResponses = parseCustomerQueryResponse(qbXml);

		Set<String> removeKeys = new HashSet<String>();

		currentSession.getToUpdate().clear();

		for (String listId : parsedResponses.keySet()) {
			Map<String, Object> dataForThisListId = parsedResponses.get(listId);

			ContractorAccount contractor = (ContractorAccount) dataForThisListId.get("contractor");

			if (contractor != null && contractor.getId() != 0
					&& currentSession.getPossibleUpdates().contains(contractor)) {
				currentSession.getToUpdate().put(listId, dataForThisListId);
				removeKeys.add(listId);
			} else {
				// the qbid is mapped in mysql, but the accountnumber is not
				// mapped in quickbooks. what should we do?
			}
		}

		for (String key : removeKeys) {
			parsedResponses.remove(key);
		}

		return null;
	}
}