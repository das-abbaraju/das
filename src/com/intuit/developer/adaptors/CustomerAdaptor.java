package com.intuit.developer.adaptors;

import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.intuit.developer.QBSession;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.quickbooks.qbxml.CustomerQueryRqType;
import com.picsauditing.quickbooks.qbxml.CustomerQueryRsType;
import com.picsauditing.quickbooks.qbxml.CustomerRet;
import com.picsauditing.quickbooks.qbxml.ListDelRqType;
import com.picsauditing.quickbooks.qbxml.ObjectFactory;
import com.picsauditing.quickbooks.qbxml.QBXML;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRq;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRs;

public class CustomerAdaptor extends QBXmlAdaptor {

	protected String getTheseContractors(QBSession currentSession, List<ContractorAccount> contractors) throws Exception {
		Writer writer = makeWriter();

		ObjectFactory factory = new ObjectFactory();
		QBXML xml = factory.createQBXML();

		QBXMLMsgsRq request = factory.createQBXMLMsgsRq();
		request.setOnError("stopOnError");

		CustomerQueryRqType customerQuery = factory.createCustomerQueryRqType();

		for (ContractorAccount contractor : contractors) {
			customerQuery.getListID().add(contractor.getQbListID(currentSession.getCountryCode()));
		}

		customerQuery.setRequestID(new Long(System.currentTimeMillis()).toString());

		customerQuery.getIncludeRetElement().add("ListID");
		customerQuery.getIncludeRetElement().add("EditSequence");
		customerQuery.getIncludeRetElement().add("AccountNumber");

		request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(customerQuery);
		xml.setQBXMLMsgsRq(request);

		Marshaller m = makeMarshaller();

		m.marshal(xml, writer);
		return writer.toString();
	}

	public Map<String, Map<String, Object>> parseCustomerQueryResponse(String qbXml) throws Exception {

		Map<String, Map<String, Object>> response = new HashMap<String, Map<String, Object>>();

		Unmarshaller unmarshaller = jc.createUnmarshaller();

		StringReader stringReader = new StringReader(qbXml);

		QBXML xml = (QBXML) unmarshaller.unmarshal(stringReader);

		QBXMLMsgsRs msgsRs = xml.getQBXMLMsgsRs();

		List<Object> hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs = msgsRs
				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs();

		for (Object result : hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs) {

			CustomerQueryRsType thisQueryResponse = (CustomerQueryRsType) result;

			for (CustomerRet customer : thisQueryResponse.getCustomerRet()) {

				ContractorAccount contractor = new ContractorAccount();

				try {
					String accountNumber = customer.getAccountNumber();

					int accountId = Integer.parseInt(accountNumber);

					contractor.setId(accountId);
					contractor.setName(customer.getCompanyName());
				} catch (Exception e) {
					contractor.setId(0);
				}
				response.put(customer.getListID(), new HashMap<String, Object>());
				response.get(customer.getListID()).put("contractor", contractor);
				response.get(customer.getListID()).put("EditSequence", customer.getEditSequence());

			}
		}

		return response;
	}

	public String deleteTheseContractors(Map<String, Map<String, Object>> qbIds) throws Exception {
		Writer writer = makeWriter();

		ObjectFactory factory = new ObjectFactory();
		QBXML xml = factory.createQBXML();

		QBXMLMsgsRq request = factory.createQBXMLMsgsRq();
		request.setOnError("stopOnError");

		for (String thePk : qbIds.keySet()) {

			ListDelRqType deleteRequest = factory.createListDelRqType();
			deleteRequest.setRequestID("delete_" + thePk);
			deleteRequest.setListDelType("Customer");
			deleteRequest.setListID(thePk);

			request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(deleteRequest);
		}

		xml.setQBXMLMsgsRq(request);

		Marshaller m = makeMarshaller();

		m.marshal(xml, writer);
		return writer.toString();

	}

	protected String getFirstName(String fullName) {
		if (fullName != null && fullName.length() > 0) {

			String[] names = fullName.split(" ");

			if (names.length == 0) {
				return "";
			} else {
				return names[0];
			}
		} else {
			return "";
		}
	}

	protected String getLastName(String fullName) {
		if (fullName != null && fullName.length() > 0) {

			String[] names = fullName.split(" ");

			if (names.length == 0) {
				return "";
			} else {
				return names[names.length - 1];
			}
		} else {
			return "";
		}
	}

}
