package com.intuit.developer.adaptors;

import java.io.Writer;
import java.util.List;

import javax.xml.bind.Marshaller;

import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.quickbooks.qbxml.ObjectFactory;
import com.picsauditing.quickbooks.qbxml.QBXML;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRq;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentQueryRqType;
import com.picsauditing.util.SpringUtils;

public class PaymentAdaptor extends QBXmlAdaptor {

	protected PaymentDAO paymentDao = null;

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
//		logger.error("XML after marshalling: " + writer.toString());
		return writer.toString();
	}

	protected PaymentDAO getPaymentDao() {
		if (paymentDao == null)
			paymentDao = (PaymentDAO) SpringUtils.getBean("PaymentDAO");

		return paymentDao;
	}
}
