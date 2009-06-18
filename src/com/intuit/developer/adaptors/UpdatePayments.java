package com.intuit.developer.adaptors;

import java.io.StringReader;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.intuit.developer.QBSession;
import com.picsauditing.PICS.BrainTreeService;
import com.picsauditing.jpa.entities.InvoicePayment;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentMethod;
import com.picsauditing.quickbooks.qbxml.AppliedToTxnMod;
import com.picsauditing.quickbooks.qbxml.ObjectFactory;
import com.picsauditing.quickbooks.qbxml.PaymentMethodRef;
import com.picsauditing.quickbooks.qbxml.QBXML;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRq;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRs;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentMod;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentModRqType;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentModRsType;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentRet;

public class UpdatePayments extends PaymentAdaptor {

	@Override
	public String getQbXml(QBSession currentSession) throws Exception {

		if (currentSession.getToUpdate().size() == 0) {
			return super.getQbXml(currentSession);
		}

		Map<String, Map<String, Object>> data = currentSession.getToUpdate();
		currentSession.setCurrentBatch(new HashMap<String, String>());

		Writer writer = makeWriter();

		ObjectFactory factory = new ObjectFactory();
		QBXML xml = factory.createQBXML();

		QBXMLMsgsRq request = factory.createQBXMLMsgsRq();
		request.setOnError("continueOnError");

		Map<String, Object> thisPaymentParms = null;

		for (String thePk : data.keySet()) {

			thisPaymentParms = data.get(thePk);

			Payment paymentJPA = (Payment) thisPaymentParms.get("payment");
			paymentJPA = paymentDao.find(paymentJPA.getId());

			ReceivePaymentRet paymentRet = (ReceivePaymentRet) thisPaymentParms
					.get("paymentRet");

			ReceivePaymentModRqType modRequest = factory
					.createReceivePaymentModRqType();
			modRequest.setRequestID("update_payment_" + paymentJPA.getId());

			request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq()
					.add(modRequest);

			ReceivePaymentMod payment = factory.createReceivePaymentMod();
			modRequest.setReceivePaymentMod(payment);

			payment.setTxnID(paymentRet.getTxnID());
			payment.setEditSequence(paymentRet.getEditSequence());

			payment.setCustomerRef(factory.createCustomerRef());
			payment.getCustomerRef().setListID(
					paymentJPA.getAccount().getQbListID());

			payment.setARAccountRef(factory.createARAccountRef());
			payment.getARAccountRef().setFullName("Accounts Receivable");

			payment.setTxnDate(new SimpleDateFormat("yyyy-MM-dd")
					.format(paymentJPA.getCreationDate()));

			payment.setTotalAmount(paymentJPA.getTotalAmount().setScale(2,
					BigDecimal.ROUND_HALF_UP).toString());

			payment.setPaymentMethodRef(new PaymentMethodRef());

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

			for (InvoicePayment invoicePayment : paymentJPA.getInvoices()) {
				AppliedToTxnMod application = factory.createAppliedToTxnMod();
				payment.getAppliedToTxnMod().add(application);

				application.setTxnID(invoicePayment.getInvoice().getQbListID());

				application.setPaymentAmount(invoicePayment.getAmount()
						.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
			}
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

			ReceivePaymentModRsType thisQueryResponse = (ReceivePaymentModRsType) result;

			ReceivePaymentRet receivePaymentRet = thisQueryResponse
					.getReceivePaymentRet();

			String thePk = currentSession.getCurrentBatch().get(
					thisQueryResponse.getRequestID());

			Map<String, Object> thisCustomerParms = currentSession
					.getToUpdate().get(thePk);
			currentSession.getToUpdate().remove(thePk);

			Payment paymentJPA = (Payment) thisCustomerParms.get("payment");
			paymentJPA = getPaymentDao().find(paymentJPA.getId());

			try {
				if (receivePaymentRet == null)
					throw new Exception("no invoice object");

				String paymentIdString = receivePaymentRet.getRefNumber();

				int paymentId = Integer.parseInt(paymentIdString);

				if (paymentId != 0) {
					paymentJPA.setQbSync(false);
				}

			} catch (Exception e) {
				StringBuilder errorMessage = new StringBuilder(
						"Problem updating payment\t");

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
				errorMessage.append("\t");
				errorMessage.append(e.getMessage());

				currentSession.getErrors().add(errorMessage.toString());
			}

			getPaymentDao().save(paymentJPA);
		}

		return null;
	}

}
