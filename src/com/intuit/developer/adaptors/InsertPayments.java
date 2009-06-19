package com.intuit.developer.adaptors;

import java.io.StringReader;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.intuit.developer.QBSession;
import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.jpa.entities.PaymentApplied;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentMethod;
import com.picsauditing.quickbooks.qbxml.AppliedToTxnAdd;
import com.picsauditing.quickbooks.qbxml.ObjectFactory;
import com.picsauditing.quickbooks.qbxml.QBXML;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRq;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRs;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentAdd;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentAddRqType;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentAddRsType;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentRet;
import com.picsauditing.quickbooks.qbxml.TxnLineDetail;

public class InsertPayments extends PaymentAdaptor {

	@Override
	public String getQbXml(QBSession currentSession) throws Exception {

		List<Payment> payments = getPaymentDao()
				.findWhere(
						"p.account.qbListID is not null AND p.status != 'Void' AND p.qbSync = true AND p.qbListID is null "
								+ "AND p.account.qbListID not like 'NOLOAD%'",
						10);

		// no work to do
		if (payments.size() == 0) {
			return super.getQbXml(currentSession);
		}

		currentSession.setCurrentBatch(new HashMap<String, String>());

		Writer writer = makeWriter();

		ObjectFactory factory = new ObjectFactory();
		QBXML xml = factory.createQBXML();

		QBXMLMsgsRq request = factory.createQBXMLMsgsRq();
		request.setOnError("continueOnError");

		for (Payment paymentJPA : payments) {

			ReceivePaymentAddRqType addRequest = factory
					.createReceivePaymentAddRqType();
			addRequest.setRequestID("insert_payment_" + paymentJPA.getId());

			request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq()
					.add(addRequest);

			ReceivePaymentAdd payment = factory.createReceivePaymentAdd();

			addRequest.setReceivePaymentAdd(payment);

			payment.setCustomerRef(factory.createCustomerRef());
			payment.getCustomerRef().setListID(
					paymentJPA.getAccount().getQbListID());

			payment.setARAccountRef(factory.createARAccountRef());
			payment.getARAccountRef().setFullName("Accounts Receivable");

			payment.setTxnDate(new SimpleDateFormat("yyyy-MM-dd")
					.format(paymentJPA.getCreationDate()));

			// payment.setRefNumber(invoiceJPA.getCheckNumber()); they're not
			// using this field in quickbooks anymore
			payment.setTotalAmount(paymentJPA.getTotalAmount().setScale(2,
					BigDecimal.ROUND_HALF_UP).toString());

			payment.setPaymentMethodRef(factory.createPaymentMethodRef());
			payment.setDepositToAccountRef(factory.createDepositToAccountRef());

			if (paymentJPA.getPaymentMethod().equals(PaymentMethod.Check)) {
				payment.getPaymentMethodRef().setFullName("Check");
				payment.getDepositToAccountRef().setFullName(
						"Undeposited Funds");
				payment.setMemo("Check number: " + paymentJPA.getCheckNumber());

			} else {

				String cardType = new BrainTreeService.CreditCard(paymentJPA
						.getCcNumber()).getCardType();

				if (cardType.equals("") || cardType.equals("Unknown")) {
					payment.getPaymentMethodRef().setFullName("Check");
					payment.getDepositToAccountRef().setFullName(
							"Undeposited Funds");
				} else if (cardType.equals("Visa")
						|| cardType.equals("Mastercard")) {
					payment.getPaymentMethodRef().setFullName(
							"Braintree VISA/MC");
					payment.getDepositToAccountRef().setFullName(
							"VISA/MC Merchant Account");
				} else if (cardType.equals("American Express")) {
					payment.getPaymentMethodRef().setFullName("Braintree AMEX");
					payment.getDepositToAccountRef().setFullName(
							"Amex Merchant Account");
				} else if (cardType.equals("Discover")) {
					payment.getPaymentMethodRef().setFullName(
							"Braintree DISCOVER");
					payment.getDepositToAccountRef().setFullName(
							"Discover Merchant Account");
				}

				payment.getPaymentMethodRef().setFullName("Braintree Credit");
				payment.setMemo(paymentJPA.getCcNumber());
			}

			for (PaymentApplied invoicePayment : paymentJPA.getInvoices()) {
				AppliedToTxnAdd application = factory.createAppliedToTxnAdd();
				payment.getAppliedToTxnAdd().add(application);
				application.setTxnID(factory.createAppliedToTxnAddTxnID());
				application.getTxnID().setValue(paymentJPA.getQbListID());

				TxnLineDetail createTxnLineDetail = factory
						.createTxnLineDetail();
				application.getTxnLineDetail().add(createTxnLineDetail);

				createTxnLineDetail.setTxnLineID(paymentJPA.getQbListID());
				createTxnLineDetail.setAmount(invoicePayment.getAmount()
						.setScale(2, BigDecimal.ROUND_HALF_UP).toString());

				application.setPaymentAmount(invoicePayment.getAmount()
						.setScale(2, BigDecimal.ROUND_HALF_UP).toString());

			}

			currentSession.getCurrentBatch().put(addRequest.getRequestID(),
					new Integer(paymentJPA.getId()).toString());
		}

		xml.setQBXMLMsgsRq(request);

		Marshaller m = makeMarshaller();

		m.marshal(xml, writer);
		return writer.toString();

	}

	@Override
	public Object parseQbXml(QBSession currentSession, String qbXml)
			throws Exception {

		Unmarshaller unmarshaller = jc.createUnmarshaller();

		StringReader stringReader = new StringReader(qbXml);

		QBXML xml = (QBXML) unmarshaller.unmarshal(stringReader);

		QBXMLMsgsRs msgsRs = xml.getQBXMLMsgsRs();

		List<Object> hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs = msgsRs
				.getHostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs();

		for (Object result : hostQueryRsOrCompanyQueryRsOrCompanyActivityQueryRs) {

			ReceivePaymentAddRsType thisQueryResponse = (ReceivePaymentAddRsType) result;

			ReceivePaymentRet receivePaymentRet = thisQueryResponse
					.getReceivePaymentRet();

			int paymentId = new Integer(currentSession.getCurrentBatch().get(
					thisQueryResponse.getRequestID())).intValue();
			Payment connected = getPaymentDao().find(paymentId);

			if (receivePaymentRet != null) {

				try {
					String qbPaymentId = receivePaymentRet.getTxnID();

					if (qbPaymentId != null && qbPaymentId.length() > 0) {
						// TODO Update the Payment object
						// connected.setQbPaymentListID(qbPaymentId);
					}
				} catch (Exception e) {
				}
			} else {
				StringBuilder errorMessage = new StringBuilder(
						"Problem inserting payment\t");

				errorMessage.append(thisQueryResponse.getRequestID());
				errorMessage.append("\t");
				errorMessage.append(currentSession.getCurrentBatch().get(
						thisQueryResponse.getRequestID()));
				errorMessage.append("\t");
				errorMessage.append(thisQueryResponse.getStatusMessage());
				errorMessage.append("\t");
				errorMessage.append(thisQueryResponse.getStatusSeverity());
				errorMessage.append("\t");
				errorMessage.append(thisQueryResponse.getStatusCode());

				currentSession.getErrors().add(errorMessage.toString());

				// TODO Update the Payment object
				// connected.setQbPaymentListID(null);
			}

			getPaymentDao().save(connected);
		}

		return null;
	}

}
