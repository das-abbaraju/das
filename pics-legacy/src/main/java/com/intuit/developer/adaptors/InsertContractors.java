package com.intuit.developer.adaptors;

import com.intuit.developer.QBSession;
import com.picsauditing.access.OpPerms;
import com.picsauditing.featuretoggle.Features;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Currency;
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

	public static String getWhereClause(Currency currency) {
        String qbID = getQBListID(currency);
		return "a.qbSync = true AND a." + qbID + " IS NULL AND a.status = 'Active' AND a.country.currency = '"
				+ currency.name() + "'";
	}

	// FIXME This is practically identical to the same method in
	// UpdateContractors.java
	@Override
	public String getQbXml(QBSession currentSession) throws Exception {

		List<ContractorAccount> contractors = getContractorDao().findWhere(
				getWhereClause(currentSession.getCurrency()));

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
				 * Euros and GBP are stored in the same quickbooks server which
				 * doesn't allow for multiple currencies per account. Have to
				 * name each account differently.
				 **/

                requestID = requestID + getCurrencyCodeSuffixForQB(currentSession) ;

				customerAddRequest.setRequestID(requestID);

				request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(customerAddRequest);

				CustomerAdd customer = factory.createCustomerAdd();
				customerAddRequest.setCustomerAdd(customer);

				String customerName = contractor.getIdString() + getCurrencyCodeSuffixForQB(currentSession);

				customer.setName(customerName);
				customer.setIsActive(new Boolean((contractor.getStatus().isActive() || contractor.isRenew()))
						.toString());

				customer.setCompanyName(nullSafeSubString(contractor.getName(), 0, 41));

				List<User> billingUsersInContractorAccount = contractor.getUsersByRole(OpPerms.ContractorBilling);

				User primary = null;
				if (contractor.getPrimaryContact() != null) {
					primary = contractor.getPrimaryContact();
				} else {
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

                customer.setCurrencyRef(factory.createCurrencyRef());

                customer.getCurrencyRef().setFullName(getCurrencyRefFullName(contractor));

				customer.setPhone(nullSafePhoneFormat(contractor.getPhone()));
				customer.setFax(nullSafeSubString(contractor.getFax(), 0, 19));
				customer.setEmail(EmailAddressUtils.validate(primary.getEmail()));

				if (CollectionUtils.isEmpty(billingUsersInContractorAccount)) { // PICS-8332
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
		// logger.error("XML after marshalling: " + writer.toString());
		return writer.toString();

	}

    private void setBillAddress(ObjectFactory factory, ContractorAccount contractor, CustomerAdd customer) {
        if (!Features.QUICKBOOKS_EXCLUDE_CONTRACTOR_ADDRESS.isActive()) {
            customer.setBillAddress(factory.createBillAddress());
            customer.setBillAddress(updateBillAddress(contractor, customer.getBillAddress()));
        }
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
			ContractorAccount contractorAccount = getContractorDao().find(conId);

			if (customer != null) {

				try {
					String accountNumber = customer.getAccountNumber();
					int accountId = Integer.parseInt(accountNumber);

					if (accountId != 0) {
                        setQBListID(currentSession, customer, contractorAccount);
						contractorAccount.setQbSync(false);
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

				if (currentSession.isUS()) {
					contractorAccount.setQbListID(null);
				} else if (currentSession.isGBP()) {
					contractorAccount.setQbListUKID(null);
				} else if (currentSession.isEUR()) {
					contractorAccount.setQbListEUID(null);
				}else if (currentSession.isCHF()) {
                    contractorAccount.setQbListCHFID(null);
                }else {
					contractorAccount.setQbListCAID(null);
				}

				contractorAccount.setQbSync(true);
			}

			getContractorDao().save(contractorAccount);

		}

		return null;
	}

    private void setQBListID(QBSession currentSession, CustomerRet customer, ContractorAccount contractorAccount) {
        Currency currentCurrency = currentSession.getCurrency();

        switch (currentCurrency) {
            case USD:
                contractorAccount.setQbListID(customer.getListID());
                break;
            case CAD:
                contractorAccount.setQbListCAID(customer.getListID());
                break;
            case GBP:
                contractorAccount.setQbListUKID(customer.getListID());
                break;
            case CHF:
                contractorAccount.setQbListCHFID(customer.getListID());
                break;
            case EUR:
                contractorAccount.setQbListEUID(customer.getListID());
                break;
        }
    }

}
