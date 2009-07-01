package com.intuit.developer.adaptors;

import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.quickbooks.qbxml.InvoiceQueryRqType;
import com.picsauditing.quickbooks.qbxml.ObjectFactory;
import com.picsauditing.quickbooks.qbxml.QBXML;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRq;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRs;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentQueryRqType;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentQueryRsType;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentRet;
import com.picsauditing.util.SpringUtils;

public class PaymentAdaptor extends QBXmlAdaptor {

	protected PaymentDAO paymentDao = null;

	
	public Map<String, ReceivePaymentRet> parsePaymentQueryResponse(String qbXml) throws Exception {

		Unmarshaller unmarshaller = jc.createUnmarshaller();

		StringReader stringReader = new StringReader(qbXml);

		QBXML xml = (QBXML) unmarshaller.unmarshal(stringReader);

		QBXMLMsgsRs msgsRs = xml.getQBXMLMsgsRs();

		List<Object> hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs = msgsRs
				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs();

		Map<String, ReceivePaymentRet> response = new HashMap<String, ReceivePaymentRet>();
		for (Object result : hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs) {

			ReceivePaymentQueryRsType thisQueryResponse = (ReceivePaymentQueryRsType) result;

			for (ReceivePaymentRet individualResponse : thisQueryResponse.getReceivePaymentRet()) {
				String key = individualResponse.getTxnID();
				response.put(key, individualResponse);
			}
		}

		return response;

	}

	public String getThesePayments(List<Payment> payments) throws Exception {
		Writer writer = makeWriter();

		ObjectFactory factory = new ObjectFactory();
		QBXML xml = factory.createQBXML();

		QBXMLMsgsRq request = factory.createQBXMLMsgsRq();
		request.setOnError("stopOnError");

		ReceivePaymentQueryRqType query = factory.createReceivePaymentQueryRqType();

		for (Payment payment : payments) {
			query.getTxnID().add(payment.getQbListID());
		}

		query.setRequestID(new Long(System.currentTimeMillis()).toString());

		request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(query);
		xml.setQBXMLMsgsRq(request);

		Marshaller m = makeMarshaller();

		m.marshal(xml, writer);
		return writer.toString();
	}

	protected PaymentDAO getPaymentDao() {
		if (paymentDao == null)
			paymentDao = (PaymentDAO) SpringUtils.getBean("PaymentDAO");

		return paymentDao;
	}
}
