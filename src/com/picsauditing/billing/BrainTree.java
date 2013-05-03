package com.picsauditing.billing;

import com.picsauditing.braintree.BrainTreeAuthentication;
import com.picsauditing.braintree.BrainTreeService;
import com.picsauditing.braintree.CreditCard;
import com.picsauditing.braintree.PaymentData;
import com.picsauditing.currency.Currency;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.Payment;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class BrainTree {

    @Autowired
    AppPropertyDAO propertyDAO;

    BrainTreeService service;

    public CreditCard getCreditCard(ContractorAccount account) throws Exception {
        return getCreditCardService().getCreditCard(account.getId());
    }

    public void deleteCreditCard(ContractorAccount account) throws Exception {
        getCreditCardService().deleteCreditCard(account.getId());
    }

    public String getTransactionCondition(String transactionID) throws Exception {
        return getCreditCardService().getTransactionCondition(transactionID);
    }

    public boolean processPayment(Payment payment, Invoice invoice) throws Exception {
        payment.setTransactionID(getCreditCardService().processPayment(new PaymentToBe(payment, invoice)));
        return true;
    }

    public boolean processRefund(String transactionID, BigDecimal amount) throws Exception {
        return getCreditCardService().processRefund(transactionID, amount);
    }

    public boolean processCancelation(String transactionID) throws Exception {
        return getCreditCardService().processCancellation(transactionID);
    }

    public boolean voidTransaction(String transactionID) throws Exception {
        return getCreditCardService().voidTransaction(transactionID);
    }

    private BrainTreeService getCreditCardService() {
        if (service == null) {
            synchronized (this) {
                if (service == null) {
                    BrainTreeAuthentication auth =
                            new BrainTreeAuthentication.BrainTreeAuthenticationBuilder()
                            .setUsername(property(USERNAME))
                            .setPassword(property(PASSWORD))
                            .setKey(property(KEY_ID), property(KEY))
                            .setProcessorID(Currency.GBP, property(GBP_PROCESSOR))
                            .setProcessorID(Currency.CAD, property(CAD_PROCESSOR))
                            .setProcessorID(Currency.DKK, property(DKK_PROCESSOR))
                            .setProcessorID(Currency.EUR, property(EUR_PROCESSOR))
                            .setProcessorID(Currency.NOK, property(NOK_PROCESSOR))
                            .setProcessorID(Currency.SEK, property(SEK_PROCESSOR))
                            .setProcessorID(Currency.USD, property(USD_PROCESSOR))
                            .setProcessorID(Currency.ZAR, property(ZAR_PROCESSOR))
                            .build();
                    service = new BrainTreeService(auth);
                }
            }
        }
        return service;
    }

    private String property(String key) {
        return propertyDAO.getProperty(key);
    }

    private static final String USERNAME = "brainTree.username";
    private static final String PASSWORD = "brainTree.password";
    private static final String KEY = "brainTree.key";
    private static final String KEY_ID = "brainTree.key_id";
    private static final String GBP_PROCESSOR = "brainTree.processor_id.gbp";
    private static final String CAD_PROCESSOR = "brainTree.processor_id.canada";
    private static final String DKK_PROCESSOR = "brainTree.processor_id.dkk";
    private static final String EUR_PROCESSOR = "brainTree.processor_id.eur";
    private static final String NOK_PROCESSOR = "brainTree.processor_id.nok";
    private static final String SEK_PROCESSOR = "brainTree.processor_id.sek";
    private static final String USD_PROCESSOR = "brainTree.processor_id.us";
    private static final String ZAR_PROCESSOR = "brainTree.processor_id.zar";


    private class PaymentToBe implements PaymentData {
        private Payment payment;
        private Invoice invoice;

        private PaymentToBe(Payment payment, Invoice invoice) {
            this.payment = payment;
            this.invoice = invoice;
        }

        @Override
        public String getAccountID() {
            if (invoice == null) return null;
            return String.valueOf(invoice.getAccount().getId());
        }

        @Override
        public String getInvoiceID() {
            if (invoice == null) return null;
            return String.valueOf(invoice.getId());
        }

        @Override
        public BigDecimal getAmount() {
            return payment.getTotalAmount();
        }

        @Override
        public Currency getCurrency() {
            return payment.getCurrency().toNewCurrency();
        }
    }


}
