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
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import com.picsauditing.jpa.entities.PaymentMethod;
import com.picsauditing.quickbooks.qbxml.AppliedToTxnMod;
import com.picsauditing.quickbooks.qbxml.ObjectFactory;
import com.picsauditing.quickbooks.qbxml.QBXML;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRq;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRs;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentMod;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentModRqType;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentModRsType;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentRet;
import com.picsauditing.util.log.PicsLogger;

public class UpdatePayments extends PaymentAdaptor {

	@Override
	public String getQbXml(QBSession currentSession) throws Exception {

		if (currentSession.getToUpdatePayment().size() == 0) {
			PicsLogger.log("no payments to process...exiting");
			return super.getQbXml(currentSession);
		}

		currentSession.setCurrentBatch(new HashMap<String, String>());

		Writer writer = makeWriter();

		ObjectFactory factory = new ObjectFactory();
		QBXML xml = factory.createQBXML();

		QBXMLMsgsRq request = factory.createQBXMLMsgsRq();
		request.setOnError("continueOnError");

		for (ReceivePaymentRet receivePaymentRet : currentSession.getToUpdatePayment().values()) {

			Payment paymentJPA = getPaymentDao().findByListID(receivePaymentRet.getTxnID());
			PicsLogger.log("Found Payment " + paymentJPA.getId() + " where txnID=" + receivePaymentRet.getTxnID());
			
			ReceivePaymentModRqType modRequest = factory.createReceivePaymentModRqType();
			modRequest.setRequestID("update_payment_" + paymentJPA.getId());

			request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(modRequest);

			ReceivePaymentMod payment = factory.createReceivePaymentMod();
			modRequest.setReceivePaymentMod(payment);

			payment.setTxnID(receivePaymentRet.getTxnID());
			payment.setEditSequence(receivePaymentRet.getEditSequence());

			// Start Payment Insert/Update
			payment.setCustomerRef(factory.createCustomerRef());
			payment.getCustomerRef().setListID(paymentJPA.getAccount().getQbListID());

			payment.setARAccountRef(factory.createARAccountRef());
			payment.getARAccountRef().setFullName("Accounts Receivable");

			payment.setTxnDate(new SimpleDateFormat("yyyy-MM-dd").format(paymentJPA.getCreationDate()));

			payment.setTotalAmount(paymentJPA.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());

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
				//payment.setMemo("CC number: " + paymentJPA.getCcNumber());
			}

			for (PaymentAppliedToInvoice invoicePayment : paymentJPA.getInvoices()) {
				AppliedToTxnMod application = factory.createAppliedToTxnMod();
				payment.getAppliedToTxnMod().add(application);

				application.setTxnID(invoicePayment.getInvoice().getQbListID());

				application.setPaymentAmount(invoicePayment.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP)
						.toString());
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

			ReceivePaymentModRsType thisQueryResponse = (ReceivePaymentModRsType) result;

			ReceivePaymentRet receivePaymentRet = thisQueryResponse.getReceivePaymentRet();

			currentSession.getToUpdatePayment().remove(receivePaymentRet.getTxnID());

			try {
				if (receivePaymentRet == null)
					throw new Exception("no invoice object");

				Payment paymentJPA = getPaymentDao().findByListID(receivePaymentRet.getTxnID());
				if (paymentJPA != null) {
					paymentJPA.setQbSync(false);
					getPaymentDao().save(paymentJPA);
				}

			} catch (Exception e) {
				StringBuilder errorMessage = new StringBuilder("Problem updating payment\t");

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
		}

		return null;
	}

}
