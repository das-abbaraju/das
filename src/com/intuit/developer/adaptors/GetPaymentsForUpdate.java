package com.intuit.developer.adaptors;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;

import com.intuit.developer.QBSession;
import com.picsauditing.jpa.entities.Currency;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.quickbooks.qbxml.QBXML;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRs;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentQueryRsType;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentRet;
import com.picsauditing.util.log.PicsLogger;

public class GetPaymentsForUpdate extends PaymentAdaptor {

	@Override
	public String getQbXml(QBSession currentSession) throws Exception {
		Currency currency = Currency.getFromISO(currentSession.getCountry());

		String where = "p.account.qbListID is not null AND p.qbListID is not null " +
				" AND p.qbListID not like 'NOLOAD%' AND p.qbSync = true AND p.currency = '"+ currency + "'";
		List<Payment> payments = getPaymentDao().findWhere(where, 10);

		if (payments.size() > 0) {
			currentSession.getPossiblePaymentUpdates().addAll(payments);

			return getThesePayments(payments);
		}

		return super.getQbXml(currentSession);
	}

	@Override
	public Object parseQbXml(QBSession currentSession, String qbXml) throws Exception {

		PicsLogger.log("Starting parseQbXml");
		Unmarshaller unmarshaller = jc.createUnmarshaller();

		StringReader stringReader = new StringReader(qbXml);

		QBXML xml = (QBXML) unmarshaller.unmarshal(stringReader);

		QBXMLMsgsRs msgsRs = xml.getQBXMLMsgsRs();

		List<Object> hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs = msgsRs
				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs();

		Map<String, ReceivePaymentRet> parsedResponses = new HashMap<String, ReceivePaymentRet>();
		for (Object result : hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs) {

			ReceivePaymentQueryRsType thisQueryResponse = (ReceivePaymentQueryRsType) result;

			for (ReceivePaymentRet individualResponse : thisQueryResponse.getReceivePaymentRet()) {
				String key = individualResponse.getTxnID();
				parsedResponses.put(key, individualResponse);
			}
		}
		PicsLogger.log("found " + parsedResponses.size() + " ReceivePaymentRet");

		currentSession.getToUpdatePayment().clear();
		for (String listId : parsedResponses.keySet()) {
			ReceivePaymentRet qbPayment = parsedResponses.get(listId);
			currentSession.getToUpdatePayment().put(listId, qbPayment);
		}
		
		// It's possible that we may have a Payment that was deleted in QB but not in PICS
		// We should notify someone so we can remove it from PICS or insert it in QB again
		if(currentSession.getPossiblePaymentUpdates().size() != parsedResponses.size()) {
			StringBuffer paymentIDs = new StringBuffer();
			for(Payment payment : currentSession.getPossiblePaymentUpdates())
				paymentIDs.append(payment.getId()).append(",");
			
			currentSession.getErrors().add("A PICS Payment was deleted from QuickBooks. Check these Payment IDS: " + paymentIDs.toString());
		}
		
		currentSession.getPossiblePaymentUpdates().clear();
		PicsLogger.log("populated " + currentSession.getToUpdatePayment().size() + " getToUpdatePayment()");

		return null;
	}

}
