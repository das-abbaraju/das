package com.intuit.developer.adaptors;

import com.intuit.developer.QBSession;
import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.quickbooks.qbxml.*;
import com.picsauditing.util.EmailAddressUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;

public class InsertContractors extends CustomerAdaptor {

	private static final Logger logger = LoggerFactory.getLogger(InsertContractors.class);
	
	// FIXME This is practically identical to the same method in UpdateContractors.java
	@Override
	public String getQbXml(QBSession currentSession) throws Exception {

		List<ContractorAccount> contractors = getContractorDao().findWhere(
				"a.qbSync = true and a." + currentSession.getQbID() + " is null and a.status != 'Demo' and a.country.currency = '"
						+ currentSession.getCurrencyCode() + "'");

		// no work to do
		if (CollectionUtils.isEmpty(contractors)) {
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
				String requestID = "insert_customer_" + contractor.getId();
				/**
				 * Euros and GBP are stored in the same quickbooks server which doesn't allow for multiple currencies
				 * per account. Have to name each account differently.
				 **/
				if (currentSession.isEUR())
					requestID += "EU";
				customerAddRequest.setRequestID(requestID);

				request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(customerAddRequest);

				CustomerAdd customer = factory.createCustomerAdd();
				customerAddRequest.setCustomerAdd(customer);

				String customerName = contractor.getIdString();
				if (currentSession.isEUR())
					customerName += "EU";
				
				customer.setName(customerName);
				customer.setIsActive(new Boolean((contractor.getStatus().isActive() || contractor.isRenew()))
						.toString());

				customer.setCompanyName(nullSafeSubString(contractor.getName(), 0, 41));

				List<User> billingUsersInContractorAccount = contractor.getUsersByRole(OpPerms.ContractorBilling);
				
				User primary = null;
				if (contractor.getPrimaryContact() != null)
					primary = contractor.getPrimaryContact();
				else {
					if (CollectionUtils.isEmpty(billingUsersInContractorAccount)) {
						logger.warn("Invalid data for contractor ID = {}", contractor.getId());
						continue;
					}
					
					primary = billingUsersInContractorAccount.get(0);
				}

				customer.setContact(nullSafeSubString(primary.getName(), 0, 41));
				customer.setFirstName(nullSafeSubString(getFirstName(primary.getName()), 0, 25));
				customer.setLastName(nullSafeSubString(getLastName(primary.getName()), 0, 25));

				customer.setBillAddress(factory.createBillAddress());
				customer.setBillAddress(updateBillAddress(contractor, customer.getBillAddress()));

				if (currentSession.isEUR()) {
					customer.setCurrencyRef(factory.createCurrencyRef());
					customer.setCurrencyRef(updateCurrencyRef(contractor, customer.getCurrencyRef()));
				}

				customer.setPhone(nullSafePhoneFormat(contractor.getPhone()));
				customer.setFax(nullSafeSubString(contractor.getFax(), 0, 19));
				customer.setEmail(EmailAddressUtils.validate(primary.getEmail()));
				
				if (CollectionUtils.isEmpty(billingUsersInContractorAccount)) {       // PICS-8332
					customer.setAltContact(customer.getContact());
					customer.setAltPhone(customer.getPhone());
				} else {
					customer.setAltContact(nullSafeSubString(billingUsersInContractorAccount.get(0).getName(), 0, 41));
					customer.setAltPhone(nullSafePhoneFormat(billingUsersInContractorAccount.get(0).getPhone()));
				}

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
						if (currentSession.isUS())
							connected.setQbListID(customer.getListID());
						else if (currentSession.isCanada())
							connected.setQbListCAID(customer.getListID());
						else if (currentSession.isGBP())
							connected.setQbListUKID(customer.getListID());
						else if (currentSession.isEUR())
							connected.setQbListEUID(customer.getListID());
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

				if (currentSession.isUS())
					connected.setQbListID(null);
				else
					connected.setQbListCAID(null);
				connected.setQbSync(true);
			}

			getContractorDao().save(connected);

		}

		return null;
	}

}
