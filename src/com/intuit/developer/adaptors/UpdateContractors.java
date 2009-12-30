package com.intuit.developer.adaptors;

import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.intuit.developer.QBSession;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.quickbooks.qbxml.CustomerMod;
import com.picsauditing.quickbooks.qbxml.CustomerModRqType;
import com.picsauditing.quickbooks.qbxml.CustomerModRsType;
import com.picsauditing.quickbooks.qbxml.CustomerRet;
import com.picsauditing.quickbooks.qbxml.ObjectFactory;
import com.picsauditing.quickbooks.qbxml.QBXML;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRq;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRs;
import com.picsauditing.util.SpringUtils;

public class UpdateContractors extends CustomerAdaptor {

	@Override
	public String getQbXml(QBSession currentSession) throws Exception {

		if (currentSession.getToUpdate().size() == 0) {
			return super.getQbXml(currentSession);
		}

		int threshold = 10;

		Map<String, Map<String, Object>> data = currentSession.getToUpdate();
		currentSession.setCurrentBatch(new HashMap<String, String>());

		Writer writer = makeWriter();

		ObjectFactory factory = new ObjectFactory();
		QBXML xml = factory.createQBXML();

		QBXMLMsgsRq request = factory.createQBXMLMsgsRq();
		request.setOnError("continueOnError");

		Map<String, Object> thisCustomerParms = null;

		ContractorAccountDAO contractorDao = (ContractorAccountDAO) SpringUtils.getBean("ContractorAccountDAO");
		int x = 0;
		for (String thePk : data.keySet()) {

			thisCustomerParms = data.get(thePk);

			ContractorAccount contractor = (ContractorAccount) thisCustomerParms.get("contractor");

			contractor = contractorDao.find(contractor.getId());

			if (contractor != null) {

				if (++x == threshold) {
					break;
				}

				CustomerModRqType customerModRequest = factory.createCustomerModRqType();
				customerModRequest.setRequestID("update_customer_" + thePk);

				request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(customerModRequest);

				CustomerMod customer = factory.createCustomerMod();
				customerModRequest.setCustomerMod(customer);

				customer.setListID(thePk);
				customer.setEditSequence((String) thisCustomerParms.get("EditSequence"));

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
				customer.getBillAddress().setState(contractor.getState().getIsoCode());
				customer.getBillAddress().setPostalCode(contractor.getZip());
				customer.getBillAddress().setCountry(contractor.getCountry().getName());

				customer.setPhone(nullSafePhoneFormat(contractor.getPhone()));
				customer.setFax(nullSafeSubString(contractor.getFax(), 0, 19));
				customer.setEmail(contractor.getEmail());

				customer.setAltContact(nullSafeSubString(contractor.getBillingContact(), 0, 41));
				customer.setAltPhone(nullSafePhoneFormat(contractor.getBillingPhone()));

				customer.setTermsRef(factory.createTermsRef());
				customer.getTermsRef().setFullName("Net 30");

				customer.setAccountNumber(contractor.getIdString());

				currentSession.getCurrentBatch().put(customerModRequest.getRequestID(), thePk);
			}
		}

		xml.setQBXMLMsgsRq(request);

		Marshaller m = makeMarshaller();

		m.marshal(xml, writer);
		return writer.toString();

	}

	@Override
	public Object parseQbXml(QBSession currentSession, String qbXml) throws Exception {

		List<ContractorAccount> successes = new Vector<ContractorAccount>();
		Map<String, Map<String, Object>> updatePool = currentSession.getToUpdate();

		Unmarshaller unmarshaller = jc.createUnmarshaller();

		StringReader stringReader = new StringReader(qbXml);

		QBXML xml = (QBXML) unmarshaller.unmarshal(stringReader);

		QBXMLMsgsRs msgsRs = xml.getQBXMLMsgsRs();

		List<Object> hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs = msgsRs
				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs();

		for (Object result : hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs) {

			CustomerModRsType thisQueryResponse = (CustomerModRsType) result;

			CustomerRet customer = thisQueryResponse.getCustomerRet();

			String thePk = currentSession.getCurrentBatch().get(thisQueryResponse.getRequestID());

			Map<String, Object> thisCustomerParms = updatePool.get(thePk);
			updatePool.remove(thePk);

			ContractorAccount contractor = (ContractorAccount) thisCustomerParms.get("contractor");
			contractor = getContractorDao().find(contractor.getId());

			try {
				if (customer == null)
					throw new Exception("no customer object");

				String accountNumber = customer.getAccountNumber();

				int accountId = Integer.parseInt(accountNumber);

				if (accountId != 0) {
					contractor.setQbSync(false);
				}

			} catch (Exception e) {
				StringBuilder errorMessage = new StringBuilder("Problem updating contractor\t");

				errorMessage.append(thisQueryResponse.getRequestID());
				errorMessage.append("\t");
				errorMessage.append(currentSession.getCurrentBatch().get(thisQueryResponse.getRequestID()));
				errorMessage.append("\t");
				errorMessage.append(thisQueryResponse.getStatusMessage());
				errorMessage.append("\t");
				errorMessage.append(thisQueryResponse.getStatusSeverity());
				errorMessage.append("\t");
				errorMessage.append(thisQueryResponse.getStatusCode());
				errorMessage.append("\t");
				errorMessage.append(e.getMessage());

				currentSession.getErrors().add(errorMessage.toString());
			}

			getContractorDao().save(contractor);
		}

		if (currentSession.getToUpdate().size() > 0) {
			setRepeat(true);
		}

		return null;
	}

}
