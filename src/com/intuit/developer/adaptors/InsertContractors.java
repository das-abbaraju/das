package com.intuit.developer.adaptors;

import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.intuit.developer.QBSession;
import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccess;
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
		String country = currentSession.getCountry();
		String where = " AND";
		if(country.equals("CA")) 
			where += " a.country.isoCode = 'CA'";
		else 
			where += " a.country.isoCode != 'CA'";
		
		List<ContractorAccount> contractors = getContractorDao().findWhere("a.qbSync = true and a.qbListID is null" + where);

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

				customer.setContact(nullSafeSubString(contractor.getPrimaryContact().getName(), 0, 41));

				customer.setFirstName(nullSafeSubString(getFirstName(contractor.getPrimaryContact().getName()), 0, 25));
				customer.setLastName(nullSafeSubString(getLastName(contractor.getPrimaryContact().getName()), 0, 25));

				customer.setBillAddress(factory.createBillAddress());
				
				customer.setBillAddress(updateBillAddress(contractor, customer.getBillAddress()));

				customer.setPhone(nullSafePhoneFormat(contractor.getPhone()));
				customer.setFax(nullSafeSubString(contractor.getFax(), 0, 19));
				customer.setEmail(contractor.getPrimaryContact().getEmail());
				
				customer.setAltContact(nullSafeSubString(contractor.getUsersByRole(OpPerms.ContractorBilling).get(0).getName(), 0, 41));
				customer.setAltPhone(nullSafePhoneFormat(contractor.getUsersByRole(OpPerms.ContractorBilling).get(0).getPhone()));

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
