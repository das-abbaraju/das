package com.intuit.developer.adaptors;

import java.io.Writer;
import java.util.Map;

import javax.xml.bind.Marshaller;

import com.intuit.developer.QBSession;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.quickbooks.qbxml.CustomerQueryRqType;
import com.picsauditing.quickbooks.qbxml.ObjectFactory;
import com.picsauditing.quickbooks.qbxml.QBXML;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRq;

public class MapUnMappedContractors extends CustomerAdaptor {

	@Override
	public String getQbXml(QBSession currentSession) throws Exception {
		Writer writer = makeWriter();

		ObjectFactory factory = new ObjectFactory();
		QBXML xml = factory.createQBXML();

		QBXMLMsgsRq request = factory.createQBXMLMsgsRq();
		request.setOnError("stopOnError");

		CustomerQueryRqType customerQuery = factory.createCustomerQueryRqType();

		customerQuery.setRequestID(new Long(System.currentTimeMillis()).toString());

		customerQuery.getIncludeRetElement().add("ListID");
		customerQuery.getIncludeRetElement().add("AccountNumber");
		customerQuery.getIncludeRetElement().add("CompanyName");

		request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(customerQuery);
		xml.setQBXMLMsgsRq(request);

		Marshaller m = makeMarshaller();

		m.marshal(xml, writer);
		return writer.toString();
	}

	@Override
	public Object parseQbXml(QBSession currentSession, String qbXml) throws Exception {

		Map<String, Map<String, Object>> parsedResponses = parseCustomerQueryResponse(qbXml);

		for (String listId : parsedResponses.keySet()) {
			Map<String, Object> dataForThisListId = parsedResponses.get(listId);

			ContractorAccount contractor = (ContractorAccount) dataForThisListId.get("contractor");

			if (contractor != null && contractor.getId() != 0) {

				try {
					ContractorAccount contractor2 = getContractorDao().find(contractor.getId());
					if (contractor2 != null) {

						if (contractor2.getQbListID() == null || contractor2.getQbListID().length() == 0) {
							contractor2.setQbListID(listId);
							getContractorDao().save(contractor2);
						} else {
							currentSession.getErrors().add(
									"update accounts set qbListID = '" + listId + "' where id = " + contractor.getId()
											+ " AND qbListID is null;");
						}
					}
				} catch (Exception e) {
					currentSession.getErrors().add(
							"update accounts set qbListID = '" + listId + "' where id = " + contractor.getId()
									+ " AND qbListID is null;");
				}

			}
		}

		return null;
	}

}
