package com.intuit.developer.adaptors;

import com.intuit.developer.QBSession;
import com.picsauditing.braintree.CreditCard;
import com.picsauditing.jpa.entities.Currency;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import com.picsauditing.jpa.entities.PaymentMethod;
import com.picsauditing.quickbooks.model.CreditCardAccount;
import com.picsauditing.quickbooks.qbxml.*;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

public class InsertPayments extends PaymentAdaptor {

    public static final String VISA_MC_DISC_MERCHANT_ACCT_EURO = "VISA/MC/DISC Merchant Acct EURO";
    public static final String VISA_MC_DISC_MERCHANT_ACCOUNT = "VISA/MC/DISC Merchant Account";
    public static final String VISA_CHF = "Visa CHF";
    public static final String AMEX_MERCHANT_ACCOUNT_EURO = "AMEX Merchant Account EURO";
    public static final String AMEX_MERCHANT_ACCOUNT = "Amex Merchant Account";
    public static final String UNDEPOSITED_FUNDS_EURO = "Undeposited Funds EURO";
    public static final String UNDEPOSITED_FUNDS_CHF = "Undeposited Funds CHF";
    public static final String UNDEPOSITED_FUNDS = "Undeposited Funds";
    public static final String UNDEPOSITED_FUNDS_PLN = "Undeposited Funds PLN";

    public static String getWhereClause(Currency currency) {
        String qbID = getQBListID(currency);
        return "p.account." + qbID + " is not null AND p.status != 'Void' AND p.qbSync = true AND p.qbListID is null "
				+ "AND p.account." + qbID + " not like 'NOLOAD%' and p.account.status != 'Demo' AND p.currency like '"
				+ currency.name() + "'";
	}

	@Override
	public String getQbXml(QBSession currentSession) throws Exception {

		List<Payment> payments = getPaymentDao().findWhere(
				getWhereClause(currentSession.getCurrency()), 10);

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
            processPayment(currentSession, factory, request, paymentJPA);
		}

		xml.setQBXMLMsgsRq(request);

		Marshaller m = makeMarshaller();

		m.marshal(xml, writer);
		// logger.error("XML after marshalling: " + writer.toString());
		PicsLogger.stop();

		return writer.toString();

	}

    public void processPayment(QBSession currentSession, ObjectFactory factory, QBXMLMsgsRq request, Payment paymentJPA) throws Exception {
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
        payment.getCustomerRef().setListID(paymentJPA.getAccount().getQbListID(currentSession.getCurrencyCode()));

        PicsLogger.log("   setARAccountRef");
        payment.setARAccountRef(factory.createARAccountRef());

        String accountsReceivableAccountRef = getAccountsReceivableAccountRef(
                currentSession);

        payment.getARAccountRef().setFullName(accountsReceivableAccountRef);

        PicsLogger.log("   setTxnDate");
        payment.setTxnDate(new SimpleDateFormat("yyyy-MM-dd").format(paymentJPA.getCreationDate()));

        payment.setTotalAmount(paymentJPA.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());

        PicsLogger.log("   setPaymentMethodRef");
        payment.setPaymentMethodRef(factory.createPaymentMethodRef());
        payment.setDepositToAccountRef(factory.createDepositToAccountRef());

        boolean isCheck = paymentJPA.getPaymentMethod().equals(PaymentMethod.Check);
        String cardType = null;
        if (!isCheck) {
            cardType = new CreditCard(paymentJPA.getCcNumber()).getCardType();
            if (cardType == null || cardType.equals("") || cardType.equals("Unknown")) {
                isCheck = true;
            }
        }

        PicsLogger.log("   setMemo");
        payment.setMemo("PICS Payment# " + paymentJPA.getId());
        /**
         * Special handling is needed for Euros because we stored Euros and
         * GBP in the same QuickBooks server.
         */
        if (isCheck) {
            payment.getPaymentMethodRef().setFullName("Check");
            payment.setRefNumber(paymentJPA.getCheckNumber());

            String unDepositedFundsAccountName = getUnDepositedFundsAccountName(currentSession.getCurrency());

            payment.getDepositToAccountRef().setFullName(unDepositedFundsAccountName);

        } else {
            com.picsauditing.quickbooks.model.CreditCard creditCard = com.picsauditing.quickbooks.model.CreditCard.fromName(cardType);
            processCreditCardPayment(currentSession.getCurrency(), paymentJPA, payment, creditCard);
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

    public void processCreditCardPayment(Currency currency, Payment paymentJPA, ReceivePaymentAdd payment, com.picsauditing.quickbooks.model.CreditCard creditCard) throws Exception {
        // todo: more magic strings to clean up...
        payment.getPaymentMethodRef().setFullName("Braintree Credit");

        if (creditCard.isVisaMCDiscover()) {
            payment.getPaymentMethodRef().setFullName("Braintree VISA/MC/DISC");
            String creditCardAccountName = getVisaMCDiscCreditCardAccountName(currency);
            payment.getDepositToAccountRef().setFullName(creditCardAccountName);

        } else if (creditCard == com.picsauditing.quickbooks.model.CreditCard.AMEX) {
            payment.getPaymentMethodRef().setFullName("Braintree AMEX");

            String creditCardAccountName = getAmexCreditCardAccountName(currency);

            payment.getDepositToAccountRef().setFullName(creditCardAccountName);

        }
        payment.setRefNumber(paymentJPA.getTransactionID());
        // payment.setMemo("CC number: " + paymentJPA.getCcNumber());
    }

    private String getAmexCreditCardAccountName(Currency currency) throws Exception {
        CreditCardAccount creditCardAccount = currency.getQbAmexAccount();
        if (creditCardAccount == CreditCardAccount.NONE) {
            throw new InvalidQBCreditCardException("This currency does not allow Amex Credit Card: " + creditCardAccount);
        }
        return creditCardAccount.getAccountName();
    }

    private String getUnDepositedFundsAccountName(Currency currency) {
        return currency.getQbUnDepositedFundsAccount().getAccountName();
        }

    private String getVisaMCDiscCreditCardAccountName(Currency currency) {
        return currency.getQbVisaMCDiscAccount().getAccountName();
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
