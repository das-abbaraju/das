package com.intuit.developer.adaptors;

import com.intuit.developer.QBSession;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.featuretoggle.Features;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.quickbooks.qbxml.*;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.SpringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateContractors extends CustomerAdaptor {

	private static final Logger logger = LoggerFactory.getLogger(UpdateContractors.class);
	
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

                setBillAddress(factory, contractor, customer);

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

				currentSession.getCurrentBatch().put(customerModRequest.getRequestID(), thePk);
			}
		}

		xml.setQBXMLMsgsRq(request);

		Marshaller m = makeMarshaller();

		m.marshal(xml, writer);
//		logger.error("XML after marshalling: " + writer.toString());
		return writer.toString();

	}

    private void setBillAddress(ObjectFactory factory, ContractorAccount contractor, CustomerMod customer) {
        if (Features.QUICKBOOKS_INCLUDE_CONTRACTOR_ADDRESS.isActive()) {
            customer.setBillAddress(factory.createBillAddress());
            customer.setBillAddress(updateBillAddress(contractor, customer.getBillAddress()));
        }
    }

    @Override
	public Object parseQbXml(QBSession currentSession, String qbXml) throws Exception {

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
