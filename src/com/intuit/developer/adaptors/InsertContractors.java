package com.intuit.developer.adaptors;

import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.intuit.developer.QBSession;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.quickbooks.qbxml.CustomerAdd;
import com.picsauditing.quickbooks.qbxml.CustomerAddRqType;
import com.picsauditing.quickbooks.qbxml.CustomerAddRsType;
import com.picsauditing.quickbooks.qbxml.CustomerRet;
import com.picsauditing.quickbooks.qbxml.ObjectFactory;
import com.picsauditing.quickbooks.qbxml.QBXML;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRq;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRs;

public class InsertContractors extends CustomerAdaptor {

	@Override
	public String getQbXml(QBSession currentSession) throws Exception {

		List<ContractorAccount> contractors = getContractorDao().findWhere("a.qbSync = true and a.qbListID is null");

		// no work to do
		if (contractors.size() == 0) {
			return super.getQbXml(currentSession);
		}

		int threshold = 10;

		currentSession.setCurrentBatch(new HashMap<String, String>());

		Writer writer = makeWriter();

		ObjectFactory factory = new ObjectFactory();
		QBXML xml = factory.createQBXML();

		QBXMLMsgsRq request = factory.createQBXMLMsgsRq();
		request.setOnError("continueOnError");

		int x = 0;
		for (ContractorAccount contractor : contractors) {

			if (contractor != null) {

				if (++x == threshold) {
					break;
				}

				CustomerAddRqType customerAddRequest = factory.createCustomerAddRqType();
				customerAddRequest.setRequestID("insert_customer_" + contractor.getId());

				request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(customerAddRequest);

				CustomerAdd customer = factory.createCustomerAdd();
				customerAddRequest.setCustomerAdd(customer);

				customer.setName(contractor.getIdString());
				customer.setIsActive(new Boolean((contractor.isActiveB() || contractor.isRenew())).toString());

				customer.setCompanyName(nullSafeSubString(contractor.getName(), 0, 41));

				customer.setContact(nullSafeSubString(contractor.getContact(), 0, 41));

				customer.setFirstName(nullSafeSubString(getFirstName(contractor.getContact()), 0, 25));
				customer.setLastName(nullSafeSubString(getLastName(contractor.getContact()), 0, 25));

				customer.setBillAddress(factory.createBillAddress());
				customer.getBillAddress().setAddr1(nullSafeSubString(contractor.getName(), 0, 41));
				customer.getBillAddress().setAddr2(nullSafeSubString(contractor.getContact(), 0, 41));
				customer.getBillAddress().setAddr3(nullSafeSubString(contractor.getAddress(), 0, 41));
				customer.getBillAddress().setCity(contractor.getCity());
				customer.getBillAddress().setState(contractor.getState());
				customer.getBillAddress().setPostalCode(contractor.getZip());
				customer.getBillAddress().setCountry(contractor.getCountry());

				customer.setPhone(nullSafePhoneFormat(contractor.getPhone()));
				customer.setFax(nullSafeSubString(contractor.getFax(), 0, 19));
				customer.setEmail(contractor.getEmail());

				customer.setAltContact(nullSafeSubString(contractor.getBillingContact(), 0, 41));
				customer.setAltPhone(nullSafePhoneFormat(contractor.getBillingPhone()));

				customer.setTermsRef(factory.createTermsRef());
				customer.getTermsRef().setFullName("Net 30");

				customer.setAccountNumber(contractor.getIdString());

				currentSession.getCurrentBatch().put(customerAddRequest.getRequestID(), contractor.getIdString());
			}
		}

		xml.setQBXMLMsgsRq(request);

		Marshaller m = makeMarshaller();

		m.marshal(xml, writer);
		return writer.toString();

	}

	@Override
	public Object parseQbXml(QBSession currentSession, String qbXml) throws Exception {

		Unmarshaller unmarshaller = jc.createUnmarshaller();

		StringReader stringReader = new StringReader(qbXml);

		QBXML xml = (QBXML) unmarshaller.unmarshal(stringReader);

		QBXMLMsgsRs msgsRs = xml.getQBXMLMsgsRs();

		List<Object> hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs = msgsRs
				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs();

		for (Object result : hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs) {

			CustomerAddRsType thisQueryResponse = (CustomerAddRsType) result;

			CustomerRet customer = thisQueryResponse.getCustomerRet();

			int conId = new Integer(currentSession.getCurrentBatch().get(thisQueryResponse.getRequestID())).intValue();
			ContractorAccount connected = getContractorDao().find(conId);

			if (customer != null) {

				try {
					String accountNumber = customer.getAccountNumber();
					int accountId = Integer.parseInt(accountNumber);

					if (accountId != 0) {
						connected.setQbListID(customer.getListID());
						connected.setQbSync(false);
					}
				} catch (Exception e) {
				}
			} else {
				StringBuilder errorMessage = new StringBuilder("Problem inserting contractor\t");

				errorMessage.append(thisQueryResponse.getRequestID());
				errorMessage.append("\t");
				errorMessage.append(currentSession.getCurrentBatch().get(thisQueryResponse.getRequestID()));
				errorMessage.append("\t");
				errorMessage.append(thisQueryResponse.getStatusMessage());
				errorMessage.append("\t");
				errorMessage.append(thisQueryResponse.getStatusSeverity());
				errorMessage.append("\t");
				errorMessage.append(thisQueryResponse.getStatusCode());

				currentSession.getErrors().add(errorMessage.toString());

				connected.setQbListID(null);
				connected.setQbSync(true);
			}

			getContractorDao().save(connected);

		}

		return null;
	}

}
