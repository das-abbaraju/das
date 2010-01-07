package com.intuit.developer.adaptors;

import java.io.StringReader;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jboss.util.Strings;

import com.intuit.developer.QBSession;
import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.jpa.entities.Currency;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
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
import com.picsauditing.util.log.PicsLogger;

public class InsertPayments extends PaymentAdaptor {

	@Override
	public String getQbXml(QBSession currentSession) throws Exception {
		Currency currency = Currency.getFromISO(currentSession.getCountry());

		List<Payment> payments = getPaymentDao().findWhere(
				"p.account.qbListID is not null AND p.status != 'Void' AND p.qbSync = true AND p.qbListID is null "
						+ "AND p.account.qbListID not like 'NOLOAD%' AND p.currency = '"+ currency + "'", 10);

		// no work to do
		if (payments.size() == 0) {
			PicsLogger.log("no payments to process...exiting");
			return super.getQbXml(currentSession);
		}

		PicsLogger.start("InsertPayments", "Found " + payments.size() + " payments to insert");
		currentSession.setCurrentBatch(new HashMap<String, String>());

		Writer writer = makeWriter();

		ObjectFactory factory = new ObjectFactory();
		QBXML xml = factory.createQBXML();

		QBXMLMsgsRq request = factory.createQBXMLMsgsRq();
		request.setOnError("continueOnError");

		PicsLogger.log(" created Writer, ObjectFactory, and QBXMLMsgsRq");
		for (Payment paymentJPA : payments) {
			PicsLogger.log("paymentID = " + paymentJPA.getId());

			ReceivePaymentAddRqType addRequest = factory.createReceivePaymentAddRqType();
			addRequest.setRequestID("insert_payment_" + paymentJPA.getId());

			request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(addRequest);

			ReceivePaymentAdd payment = factory.createReceivePaymentAdd();

			addRequest.setReceivePaymentAdd(payment);
			PicsLogger.log(" added payment to request");

			// Start Payment Insert/Update
			PicsLogger.log("   setCustomerRef");
			payment.setCustomerRef(factory.createCustomerRef());
			payment.getCustomerRef().setListID(paymentJPA.getAccount().getQbListID());

			PicsLogger.log("   setARAccountRef");
			payment.setARAccountRef(factory.createARAccountRef());
			payment.getARAccountRef().setFullName("Accounts Receivable");

			PicsLogger.log("   setTxnDate");
			payment.setTxnDate(new SimpleDateFormat("yyyy-MM-dd").format(paymentJPA.getCreationDate()));

			payment.setTotalAmount(paymentJPA.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());

			PicsLogger.log("   setPaymentMethodRef");
			payment.setPaymentMethodRef(factory.createPaymentMethodRef());
			payment.setDepositToAccountRef(factory.createDepositToAccountRef());

			boolean isCheck = paymentJPA.getPaymentMethod().equals(PaymentMethod.Check);
			String cardType = null;
			if (!isCheck) {
				cardType = new BrainTreeService.CreditCard(paymentJPA.getCcNumber()).getCardType();
				if (cardType == null || cardType.equals("") || cardType.equals("Unknown")) {
					isCheck = true;
				}
			}

			PicsLogger.log("   setMemo");
			payment.setMemo("PICS Payment# " + paymentJPA.getId());
			if (isCheck) {
				payment.getPaymentMethodRef().setFullName("Check");
				payment.getDepositToAccountRef().setFullName("Undeposited Funds");
				payment.setRefNumber(paymentJPA.getCheckNumber());

			} else {
				payment.getPaymentMethodRef().setFullName("Braintree Credit");

				if (cardType.equals("Visa") || cardType.equals("Mastercard")) {
					payment.getPaymentMethodRef().setFullName("Braintree VISA/MC");
					payment.getDepositToAccountRef().setFullName("VISA/MC Merchant Account");
				} else if (cardType.equals("American Express")) {
					payment.getPaymentMethodRef().setFullName("Braintree AMEX");
					payment.getDepositToAccountRef().setFullName("Amex Merchant Account");
				} else if (cardType.equals("Discover")) {
					payment.getPaymentMethodRef().setFullName("Braintree DISCOVER");
					payment.getDepositToAccountRef().setFullName("Discover Merchant Account");
				}
				payment.setRefNumber(paymentJPA.getTransactionID());
				// payment.setMemo("CC number: " + paymentJPA.getCcNumber());
			}

			for (PaymentAppliedToInvoice invoicePayment : paymentJPA.getInvoices()) {
				PicsLogger.log("   add AppliedToTxnAdd for invoiceID = " + invoicePayment.getInvoice().getId());
				AppliedToTxnAdd application = factory.createAppliedToTxnAdd();
				payment.getAppliedToTxnAdd().add(application);
				application.setTxnID(factory.createAppliedToTxnAddTxnID());
				application.getTxnID().setValue(invoicePayment.getInvoice().getQbListID());

				application.setPaymentAmount(invoicePayment.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP)
						.toString());
			}

			if (paymentJPA.getInvoices().size() == 0)
				payment.setIsAutoApply("false");

			currentSession.getCurrentBatch().put(addRequest.getRequestID(), new Integer(paymentJPA.getId()).toString());
			PicsLogger.log(" done with paymentID " + paymentJPA.getId());
		}

		xml.setQBXMLMsgsRq(request);

		Marshaller m = makeMarshaller();

		m.marshal(xml, writer);
		PicsLogger.stop();
		
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

			ReceivePaymentAddRsType thisQueryResponse = (ReceivePaymentAddRsType) result;

			ReceivePaymentRet receivePaymentRet = thisQueryResponse.getReceivePaymentRet();

			int paymentId = new Integer(currentSession.getCurrentBatch().get(thisQueryResponse.getRequestID()))
					.intValue();
			Payment connected = getPaymentDao().find(paymentId);

			if (receivePaymentRet != null) {

				try {
					String qbPaymentId = receivePaymentRet.getTxnID();

					if (!Strings.isEmpty(qbPaymentId)) {
						connected.setQbListID(qbPaymentId);
						connected.setQbSync(false);
					}
				} catch (Exception e) {
				}
			} else {
				StringBuilder errorMessage = new StringBuilder("Problem inserting payment\t");

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
			}

			getPaymentDao().save(connected);
		}

		return null;
	}

}
