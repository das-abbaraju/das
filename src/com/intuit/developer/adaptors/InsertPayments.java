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
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.quickbooks.qbxml.AppliedToTxnAdd;
import com.picsauditing.quickbooks.qbxml.ObjectFactory;
import com.picsauditing.quickbooks.qbxml.PaymentMethodRef;
import com.picsauditing.quickbooks.qbxml.QBXML;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRq;
import com.picsauditing.quickbooks.qbxml.QBXMLMsgsRs;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentAdd;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentAddRqType;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentAddRsType;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentRet;
import com.picsauditing.quickbooks.qbxml.TxnLineDetail;

public class InsertPayments extends CustomerAdaptor {

	@Override
	public String getQbXml(QBSession currentSession) throws Exception {

		// no work to do
		if (currentSession.getPaymentsToInsert().size() == 0) {
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
		for (Invoice invoiceJPA : currentSession.getPaymentsToInsert()) {

			if (invoiceJPA != null) {

				if (++x == threshold) {
					break;
				}

				ReceivePaymentAddRqType addRequest = factory.createReceivePaymentAddRqType();
				addRequest.setRequestID("insert_payment_" + invoiceJPA.getId());

				request.getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq().add(addRequest);

				ReceivePaymentAdd payment = factory.createReceivePaymentAdd();

				addRequest.setReceivePaymentAdd(payment);

				payment.setCustomerRef(factory.createCustomerRef());
				payment.getCustomerRef().setListID(invoiceJPA.getAccount().getQbListID());

				payment.setARAccountRef(factory.createARAccountRef());
				payment.getARAccountRef().setFullName("Accounts Receivable");

				payment.setTxnDate(new SimpleDateFormat("yyyy-MM-dd").format(invoiceJPA.getPaidDate()));

				// payment.setRefNumber(invoiceJPA.getCheckNumber()); they're not using this field in quickbooks anymore
				payment.setTotalAmount(invoiceJPA.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());

				payment.setPaymentMethodRef(new PaymentMethodRef());

//				if (invoiceJPA.getTransactionID() != null) {
//					payment.getPaymentMethodRef().setFullName("Check");
//					payment.setMemo(invoiceJPA.getTransactionID());
//				} else {
//					payment.getPaymentMethodRef().setFullName("Braintree Credit");
//				}

				// payment.setIsAutoApply("false"); MUST not set this if we apply the payment ourselves

				AppliedToTxnAdd application = factory.createAppliedToTxnAdd();
				payment.getAppliedToTxnAdd().add(application);
				application.setTxnID(factory.createAppliedToTxnAddTxnID());
				application.getTxnID().setValue(invoiceJPA.getQbListID());

				TxnLineDetail createTxnLineDetail = factory.createTxnLineDetail();
				application.getTxnLineDetail().add(createTxnLineDetail);

				createTxnLineDetail.setTxnLineID(invoiceJPA.getQbListID());
				createTxnLineDetail.setAmount(invoiceJPA.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP)
						.toString());

				application.setPaymentAmount(invoiceJPA.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP)
						.toString());

				currentSession.getCurrentBatch().put(addRequest.getRequestID(),
						new Integer(invoiceJPA.getId()).toString());
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

			ReceivePaymentAddRsType thisQueryResponse = (ReceivePaymentAddRsType) result;

			ReceivePaymentRet receivePaymentRet = thisQueryResponse.getReceivePaymentRet();

			int invoiceId = new Integer(currentSession.getCurrentBatch().get(thisQueryResponse.getRequestID()))
					.intValue();
			Invoice connected = getInvoiceDao().find(invoiceId);

			if (receivePaymentRet != null) {

				try {
					String qbPaymentId = receivePaymentRet.getTxnID();

					if (qbPaymentId != null && qbPaymentId.length() > 0) {
						// TODO Update the Payment object
						//connected.setQbPaymentListID(qbPaymentId);
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

				// TODO Update the Payment object
				//connected.setQbPaymentListID(null);
			}

			getInvoiceDao().save(connected);

			currentSession.getPaymentsToInsert().remove(connected);
		}

		if (currentSession.getPaymentsToInsert().size() > 0) {
			setRepeat(true);
		}

		return null;
	}

}
